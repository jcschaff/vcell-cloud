package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.jlibsedml.SEDMLDocument;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sedml.*;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class SEDMLExporterTest {

	private String filename;

	public SEDMLExporterTest(String filename){
		this.filename = filename;
	}

	public enum FAULT {
		EXPRESSIONS_DIFFERENT,
		EXPRESSION_BINDING,
		MATH_GENERATION_FAILURE,
		MATHOVERRIDES_INVALID,
		MATHOVERRIDES_SurfToVol,
		MATHOVERRIDES_A_FUNCTION,
		UNITS_EXCEPTION,
		TOO_SLOW,
		GEOMETRYSPEC_DIFFERENT,
		NULL_POINTER_EXCEPTION,
		UNKNOWN_IDENTIFIER,
		SBML_IMPORT_FAILURE,
		DIVIDE_BY_ZERO,
	};

	@BeforeClass
	public static void printSkippedModels() {
		for (String filename : outOfMemorySet()){
			System.err.println("skipping - out of memory: "+filename);
		}
		for (String filename : largeFileSet()){
			System.err.println("skipping - too large (model not in repo): "+filename);
		}
		for (String filename : slowTestSet()){
			System.err.println("skipping - SEDML processing too slow - will parse and generate math only: "+filename);
		}
	}

	/**
	 * each file in largeFileSet is > 500K on disk and is not included in the test suite.
	 * @return
	 */
	public static Set<String> largeFileSet() {
		Set<String> largeFiles = new HashSet<>();
		largeFiles.add("biomodel_101963252.vcml");
		largeFiles.add("biomodel_189321805.vcml");
		largeFiles.add("biomodel_200301029.vcml");
		largeFiles.add("biomodel_26455186.vcml");
		largeFiles.add("biomodel_27192717.vcml");
		largeFiles.add("biomodel_28625786.vcml");
		largeFiles.add("biomodel_34826524.vcml");
		largeFiles.add("biomodel_38086434.vcml");
		largeFiles.add("biomodel_47429473.vcml");
		largeFiles.add("biomodel_55178308.vcml");
		largeFiles.add("biomodel_59361239.vcml");
		largeFiles.add("biomodel_60799209.vcml");
		largeFiles.add("biomodel_61699798.vcml");
		largeFiles.add("biomodel_81992349.vcml");
		largeFiles.add("biomodel_83091496.vcml");
		largeFiles.add("biomodel_84275910.vcml");
		largeFiles.add("biomodel_93313420.vcml");
		largeFiles.add("biomodel_98150237.vcml");
		return largeFiles;
	}

	/**
	 * 	each file in the slowTestSet takes > 10s on disk and is not included in the unit test (move to integration testing)
	 */
	public static Set<String> slowTestSet() {
		Set<String> slowModels = new HashSet<>();
		slowModels.add("biomodel_101981216.vcml"); // 10s
		slowModels.add("biomodel_147699816.vcml"); // 14s
		slowModels.add("biomodel_17326658.vcml"); // 25s
		slowModels.add("biomodel_200301029.vcml"); // > 30 seconds
		slowModels.add("biomodel_200301683.vcml"); // 23s
		slowModels.add("biomodel_26581203.vcml"); // 12s
		slowModels.add("biomodel_26928347.vcml"); // 40s
		slowModels.add("biomodel_28625786.vcml"); // 41s
		slowModels.add("biomodel_59280306.vcml"); // 11s
		slowModels.add("biomodel_60647264.vcml"); // 14s
		slowModels.add("biomodel_61699798.vcml"); // 58s
		slowModels.add("biomodel_62467093.vcml"); // 19s
		slowModels.add("biomodel_62477836.vcml"); // 24s
		slowModels.add("biomodel_62585003.vcml"); // 20s
		slowModels.add("biomodel_66264206.vcml"); // 10s
		slowModels.add("biomodel_91986407.vcml"); // 11s
		slowModels.add("biomodel_95094548.vcml"); // 10s
		slowModels.add("biomodel_9590643.vcml"); // 19s
		return slowModels;
	}

	public static Set<String> outOfMemorySet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
		return outOfMemoryModels;
	}

	/**
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	public static Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named I conflicts with membrane parameter I
		return faults;
	}

	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	@Parameterized.Parameters
	public static Collection<String> testCases() {
		Predicate<String> surfToVolume_Overrides_Filter = (t) -> knownFaults().containsKey(t) && knownFaults().get(t) == FAULT.MATHOVERRIDES_SurfToVol;
//		Predicate<String> slowFilter = (t) -> !slowTestSet().contains(t);
//		Predicate<String> outOfMemoryFilter = (t) -> !outOfMemorySet().contains(t);
//		Predicate<String> allTestsFilter = (t) -> true;
//		Predicate<String> allFailures = (t) -> knownFaults().containsKey(t) && skipFilter.test(t);
		Predicate<String> oneModelFilter = (t) -> t.equals("lumped_reaction_no_size_in_rate.vcml");
		Predicate<String> skipFilter = (t) -> !outOfMemorySet().contains(t) && !largeFileSet().contains(t);
		return Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter).collect(Collectors.toList());
	}

	@Test
	public void test_parse_vcml() throws XmlParseException, PropertyVetoException, MappingException {
		String savedInstalldirProperty = System.getProperty("vcell.installDir");
		try {
			System.setProperty("vcell.installDir", "..");

			InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);
			String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));

			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));

			bioModel.updateAll(false);

			if (slowTestSet().contains(filename)) {
				// skip SEDML export processing if this is a slow model or uses too much memory
				return;
			}

			int sedmlLevel = 1;
			int sedmlVersion = 2;
			Predicate<Simulation> simulationExportFilter = sim -> true;
			List<Simulation> simsToExport = Arrays.stream(bioModel.getSimulations()).filter(simulationExportFilter).collect(Collectors.toList());

			// we replace the obsolete solver with the fully supported equivalent
			for (Simulation simulation : simsToExport) {
				if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
					simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
				}
			}
			File outputDir = Files.createTempDir();
			String jsonFullyQualifiedName = new File(outputDir, filename + ".json").getAbsolutePath();
			System.out.println(jsonFullyQualifiedName);
			SEDMLExporter sedmlExporter = new SEDMLExporter(filename, bioModel, sedmlLevel, sedmlVersion, simsToExport, jsonFullyQualifiedName);
			boolean bValidate = true;
			SEDMLDocument sedmlDocument = sedmlExporter.getSEDMLDocument(outputDir.getAbsolutePath(), filename.replace(".vcml", ""),
					ModelFormat.SBML, true, bValidate);

			SEDMLRecorder sedmlLogger = sedmlExporter.getSedmlLogger();
			boolean bAnyFailures = false;
			for (SEDMLTaskRecord sedmlTaskRecord : sedmlLogger.getLogs()) {
				boolean bFailed = false;
				switch (sedmlTaskRecord.getTaskType()) {
					case BIOMODEL: {
						break;
					}
					case SIMCONTEXT: {
						bFailed = sedmlTaskRecord.getTaskResult() == TaskResult.FAILED &&
								(sedmlTaskRecord.getException() == null || !sedmlTaskRecord.getException().getClass().equals(UnsupportedSbmlExportException.class));
						break;
					}
					case UNITS:
					case SIMULATION: {
						bFailed = sedmlTaskRecord.getTaskResult() == TaskResult.FAILED;
						break;
					}
				}
				bAnyFailures |= bFailed;
				if (!knownFaults().containsKey(filename)) {
					// skip assert if the model is known to have a problem
					Assert.assertFalse(sedmlTaskRecord.getCSV(), bFailed);
				}
			}
			FAULT knownFault = knownFaults().get(filename);
			if (knownFault != null){
				// if in the knownFaults list, the model should fail
				// this is how we can keep track of our list of known problems
				// if we fix a model without removing it from the knownFaults map, then this test will fail
				Assert.assertTrue(
						"expecting fault "+knownFault.name()+" in "+filename+" but it passed, Please remove from SEDMLExporterTest.knownFaults()",
						bAnyFailures);
			}
		}finally{
			if (savedInstalldirProperty != null){
				System.setProperty("vcell.installDir",savedInstalldirProperty);
			}
		}
	}

}
