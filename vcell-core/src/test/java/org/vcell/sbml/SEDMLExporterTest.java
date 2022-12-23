package org.vcell.sbml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.resource.NativeLib;
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
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sedml.*;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class SEDMLExporterTest {

	private static class TestCase {
		public final String filename;
		public final ModelFormat modelFormat;
		public TestCase(String filename, ModelFormat modelFormat){
			this.filename = filename;
			this.modelFormat = modelFormat;
		}
	}


	private TestCase testCase;

	private static boolean bDebug = false;

	//
	// save state for zero side-effect
	//
	private static String previousInstalldirPropertyValue;
	private static boolean previousWriteDebugFiles;

	public SEDMLExporterTest(TestCase testCase){
		this.testCase = testCase;
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
		UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM
	};

	public enum SEDML_FAULT {
		DIFF_NUMBER_OF_BIOMODELS,
		NO_MODELS_IN_OMEX,
		ERROR_CONSTRUCTING_SIMCONTEXT,
		NONSPATIAL_STOCH_HISTOGRAM
	};

	@BeforeClass
	public static void setup(){
		previousInstalldirPropertyValue = System.getProperty("vcell.installDir");
		System.setProperty("vcell.installDir", "..");
		NativeLib.combinej.load();
		previousWriteDebugFiles = SBMLExporter.bWriteDebugFiles;
		SBMLExporter.bWriteDebugFiles = bDebug;
	}

	@AfterClass
	public static void teardown() {
		if (previousInstalldirPropertyValue!=null) {
			System.setProperty("vcell.installDir", previousInstalldirPropertyValue);
		}
		SBMLExporter.bWriteDebugFiles = previousWriteDebugFiles;
	}

	@BeforeClass
	public static void printSkippedModels() {
		for (String filename : outOfMemorySet(ModelFormat.SBML)){
			System.err.println("skipping - SBML test case: out of memory: "+filename);
		}
		for (String filename : outOfMemorySet(ModelFormat.SBML)){
			System.err.println("skipping - VCML test case: out of memory: "+filename);
		}
		for (String filename : largeFileSet()){
			System.err.println("skipping - too large (model not in repo): "+filename);
		}
		for (String filename : slowTestSet(ModelFormat.SBML)){
			System.err.println("skipping - SEDML processing with SBML format: too slow - will parse and generate math only: "+filename);
		}
		for (String filename : slowTestSet(ModelFormat.VCML)){
			System.err.println("skipping - SEDML processing with VCML format: too slow - will parse and generate math only: "+filename);
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

	public static Set<String> slowTestSet(ModelFormat modelFormat){
		switch (modelFormat) {
			case SBML: {
				return slowTestSet_SBML();
			}
			case VCML: {
				return slowTestSet_VCML();
			}
			default: {
				throw new RuntimeException("unexpected model format " + modelFormat);
			}
		}
	}

	/**
	 * 	each file in the slowTestSet takes > 10s on disk and is not included in the unit test (move to integration testing)
	 */
	public static Set<String> slowTestSet_SBML() {
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
	public static Set<String> slowTestSet_VCML() {
		Set<String> slowModels = new HashSet<>();
		return slowModels;
	}

	public static Set<String> outOfMemorySet(ModelFormat modelFormat) {
		switch (modelFormat) {
			case SBML: {
				return outOfMemorySet_SBML();
			}
			case VCML: {
				return outOfMemorySet_VCML();
			}
			default: {
				throw new RuntimeException("unexpected model format " + modelFormat);
			}
		}
	}

	public static Set<String> outOfMemorySet_SBML() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
		return outOfMemoryModels;
	}

	public static Set<String> outOfMemorySet_VCML() {
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
	public static Map<String, FAULT> knownFaults(ModelFormat modelFormat) {
		switch (modelFormat) {
			case SBML: {
				return knownFaults_SBML();
			}
			case VCML: {
				return knownFaults_VCML();
			}
			default: {
				throw new RuntimeException("unexpected model format " + modelFormat);
			}
		}
	}

	public static Map<String, FAULT> knownFaults_SBML() {
		HashMap<String, FAULT> faults = new HashMap();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named I conflicts with membrane parameter I
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override.
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram
		return faults;
	}

	public static Map<String, FAULT> knownFaults_VCML() {
		HashMap<String, FAULT> faults = new HashMap();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named I conflicts with membrane parameter I
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override.
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram
		return faults;
	}

	public static Map<String, SEDML_FAULT> knownSEDMLFaults(ModelFormat modelFormat) {
		switch (modelFormat) {
			case SBML: {
				return knownSEDMLFaultsForSBML();
			}
			case VCML: {
				return knownSEDMLFaultsForVCML();
			}
			default: {
				throw new RuntimeException("unexpected model format " + modelFormat);
			}
		}
	}

	public static Map<String, SEDML_FAULT> knownSEDMLFaultsForVCML() {
		HashMap<String, SEDML_FAULT> faults = new HashMap();
		return faults;
	}

	public static Map<String, SEDML_FAULT> knownSEDMLFaultsForSBML() {
		HashMap<String, SEDML_FAULT> faults = new HashMap();
		faults.put("biomodel_100596964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_100961371.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_113655498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929912.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929971.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116930032.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123465498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123465505.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_124562627.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_13736736.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_154961582.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_155016832.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_145545992.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_156134818.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16763273.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16804037.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_168717401.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_169993006.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_17098642.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_188880263.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_189512756.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_189513183.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_20253928.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_211839191.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403233.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403238.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403244.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403250.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403576.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_225440511.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_232498815.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2912851.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2913730.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2915537.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917738.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917788.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917999.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2930915.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2962862.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568171.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568356.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_55396830.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_60203358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_60227051.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_63307133.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_66265579.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_77305266.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803961.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803976.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_81284732.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_82250339.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_84982474.vcml", SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS); // not supported nonspatial histogram
		faults.put("biomodel_9254662.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98147638.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98174143.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98296160.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_165181964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		return faults;
	}

	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	@Parameterized.Parameters
	public static Collection<TestCase> testCases() {
//		Predicate<String> surfToVolume_Overrides_Filter = (t) -> knownFaults().containsKey(t) && knownFaults().get(t) == FAULT.MATHOVERRIDES_SurfToVol;
//		Predicate<String> slowFilter = (t) -> !slowTestSet().contains(t);
//		Predicate<String> outOfMemoryFilter = (t) -> !outOfMemorySet().contains(t);
//		Predicate<String> allTestsFilter = (t) -> true;
//		Predicate<String> allFailures = (t) -> knownFaults().containsKey(t) && skipFilter.test(t);
		Predicate<String> oneModelFilter = (t) -> t.equals("lumped_reaction_no_size_in_rate.vcml");
		Predicate<String> skipFilter_SBML = (t) -> !outOfMemorySet(ModelFormat.SBML).contains(t) && !largeFileSet().contains(t);
		Predicate<String> skipFilter_VCML = (t) -> !outOfMemorySet(ModelFormat.VCML).contains(t) && !largeFileSet().contains(t);
		List<TestCase> testCases = Stream.concat(
				Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_SBML).map(fname -> new TestCase(fname, ModelFormat.SBML)),
				Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_VCML).map(fname -> new TestCase(fname, ModelFormat.VCML)))
				.collect(Collectors.toList());
		return testCases;
	}

	@Test
	public void test_parse_vcml() throws Exception {
		String test_case_name = testCase.modelFormat+"."+testCase.filename;
		InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(testCase.filename);
		String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
				.lines().collect(Collectors.joining("\n"));

		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));

		bioModel.updateAll(false);

		if (slowTestSet(testCase.modelFormat).contains(testCase.filename)) {
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
		String jsonFullyQualifiedName = new File(outputDir, test_case_name + ".json").getAbsolutePath();
		System.out.println(jsonFullyQualifiedName);

		boolean bFromCLI = true;
		boolean bRoundTripSBMLValidation = true;
		boolean bWriteOmexArchive = true;
		File omexFile = new File(outputDir, test_case_name + ".omex");
		List<SEDMLTaskRecord> sedmlTaskRecords = SEDMLExporter.writeBioModel(bioModel, omexFile, testCase.modelFormat, bFromCLI, bRoundTripSBMLValidation, bWriteOmexArchive);

		boolean bAnyFailures = false;
		for (SEDMLTaskRecord sedmlTaskRecord : sedmlTaskRecords) {
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
			if (!knownFaults(testCase.modelFormat).containsKey(testCase.filename)) {
				// skip assert if the model is known to have a problem
				Assert.assertFalse(sedmlTaskRecord.getCSV(), bFailed);
			}
		}
		FAULT knownFault = knownFaults(testCase.modelFormat).get(testCase.filename);
		if (knownFault != null){
			// if in the knownFaults list, the model should fail
			// this is how we can keep track of our list of known problems
			// if we fix a model without removing it from the knownFaults map, then this test will fail
			Assert.assertTrue(
					"expecting fault "+knownFault.name()+" in "+test_case_name+" but it passed, Please remove from SEDMLExporterTest.knownFaults()",
					bAnyFailures);
		}
		SBMLExporter.MemoryVCLogger memoryVCLogger = new SBMLExporter.MemoryVCLogger();

		try {
			List<BioModel> bioModels = XmlHelper.readOmex(omexFile, memoryVCLogger);
			Assert.assertTrue(memoryVCLogger.highPriority.size() == 0);

			File tempDir = Files.createTempDir();
			String origVcmlPath = new File(tempDir, "orig.vcml").getAbsolutePath();
			XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModel), origVcmlPath, true);

			if (bDebug) {
				SBMLExporter.bWriteDebugFiles=true;
				for (int i = 0; i < bioModels.size(); i++) {
					String rereadVcmlPath = new File(tempDir, "reread_" + i + ".vcml").getAbsolutePath();
					XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModels.get(i)), rereadVcmlPath, true);
				}
				System.err.println("wrote original and final BioModel VCML files to " + tempDir.getAbsolutePath());
			}

			Assert.assertEquals("expecting 1 biomodel in round trip", 1, bioModels.size());

			Assert.assertNull("file "+test_case_name+" passed SEDML Round trip, but knownSEDMLFault was set", knownSEDMLFaults(testCase.modelFormat).get(testCase.filename));
		}catch (Exception | AssertionError e){
			if (e.getMessage().contains("There are no models in ")){
				if (knownSEDMLFaults(testCase.modelFormat).get(testCase.filename) == SEDML_FAULT.NO_MODELS_IN_OMEX) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				}else{
					System.err.println("add SEDML_FAULT.NO_MODELS_IN_OMEX to "+test_case_name);
				}
			}
			if (e.getMessage().contains("expecting 1 biomodel in round trip")){
				if (knownSEDMLFaults(testCase.modelFormat).get(testCase.filename) == SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				}else{
					System.err.println("add SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS to "+test_case_name);
				}
			}
			if (e.getMessage().contains("Error constructing a new simulation context")) {
				if (knownSEDMLFaults(testCase.modelFormat).get(testCase.filename) == SEDML_FAULT.ERROR_CONSTRUCTING_SIMCONTEXT) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.ERROR_CONSTRUCTING_SIMCONTEXT to " + test_case_name);
				}
			}
			if (e.getMessage().contains("non-spatial stochastic simulation with histogram option to SEDML not supported")) {
				if (knownSEDMLFaults(testCase.modelFormat).get(testCase.filename) == SEDML_FAULT.NONSPATIAL_STOCH_HISTOGRAM) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.NONSPATIAL_STOCH_HISTOGRAM to " + test_case_name);
				}
			}
			throw e;
		}
	}

}
