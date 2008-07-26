package cbit.vcell.client;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.controls.DataManager;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.math.AnnotatedFunction;
import cbit.gui.DialogUtils;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.server.User;
import cbit.vcell.simdata.MergedDataInfo;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import cbit.vcell.client.desktop.simulation.SimulationCompareWindow;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.biomodel.BioModel;
import java.math.*;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.clientdb.DocumentManager;
import javax.swing.JDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import cbit.vcell.client.server.DynamicDataManager;
import cbit.vcell.server.VCDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.DataErrorSummary;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.sql.KeyValue;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cbit.vcell.solver.SimulationInfo;
import java.util.Vector;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.client.desktop.TestingFrameworkWindowPanel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.messaging.admin.ColumnComparator;
import cbit.vcell.messaging.admin.ManageTableModel;
import cbit.vcell.messaging.admin.sorttable.JSortTable;
import cbit.util.BeanUtils;
import cbit.vcell.client.desktop.testingframework.EditTestCriteriaPanel;
import cbit.vcell.client.desktop.testingframework.AddTestSuitePanel;
import cbit.vcell.client.desktop.testingframework.TestCaseAddPanel;
import cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.clientdb.ClientDocumentManager;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.TFGenerateReport;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.numericstest.*;
import cbit.vcell.numericstest.TestCriteriaCrossRefOPResults.CrossRefData;
import cbit.util.AsynchProgressPopup;
/**
 * Insert the type's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrameworkWindowManager extends TopLevelWindowManager implements DataViewerManager {
	
	
	public static final int COPY_REGRREF = 0;
	public static final int ASSIGNORIGINAL_REGRREF = 1;
	public static final int ASSIGNNEW_REGRREF = 2;

	public static class NewTestSuiteUserInformation{
		public TestSuiteInfoNew testSuiteInfoNew;
		public int regrRefFlag;
		public NewTestSuiteUserInformation(TestSuiteInfoNew argTestSuiteInfoNew,int argRegrRefFlag){
			testSuiteInfoNew = argTestSuiteInfoNew;
			regrRefFlag = argRegrRefFlag;
		}
	};
	private TestingFrameworkWindowPanel testingFrameworkWindowPanel;
	private EditTestCriteriaPanel editTestCriteriaPanel =
		new EditTestCriteriaPanel();
	private TestCaseAddPanel testCaseAddPanel = new TestCaseAddPanel();
	private Vector dataViewerPlotsFramesVector = new Vector();
	private AddTestSuitePanel addTestSuitePanel = new AddTestSuitePanel();
	private JOptionPane addTestSuiteDialog =
		new JOptionPane(
			null,
			JOptionPane.PLAIN_MESSAGE,
			0,
			null,
			new Object[] { "OK", "Cancel" });
	private JOptionPane addTestCaseDialog =
		new JOptionPane(
			null,
			JOptionPane.PLAIN_MESSAGE,
			0,
			null,
			new Object[] { "OK", "Cancel" });
	private JOptionPane editTestCriteriaDialog =
		new JOptionPane(
			null,
			JOptionPane.PLAIN_MESSAGE,
			0,
			null,
			new Object[] { "OK", "Cancel" });

/**
 * TestingFrameworkWindowManager constructor comment.
 * @param requestManager cbit.vcell.client.RequestManager
 */
public TestingFrameworkWindowManager(TestingFrameworkWindowPanel testingFrameworkWindowPanel, RequestManager requestManager) {
	super(requestManager);
	setTestingFrameworkWindowPanel(testingFrameworkWindowPanel);
}


/**
 * Add a cbit.vcell.desktop.controls.DataListener.
 */
public void addDataListener(cbit.vcell.desktop.controls.DataListener newListener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void addNewTestSuiteToTF() throws Exception {

	NewTestSuiteUserInformation newTestSuiteUserInformation = getNewTestSuiteInfoFromUser(null,null);
	if (newTestSuiteUserInformation != null && newTestSuiteUserInformation.testSuiteInfoNew != null) {
		saveNewTestSuiteInfo(newTestSuiteUserInformation.testSuiteInfoNew);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
public String addTestCases(final TestSuiteInfoNew tsInfo, final TestCaseNew[] testCases,int regrRefFlag,AsynchProgressPopup pp){
		
	if (testCases == null || testCases.length == 0 || tsInfo == null) {
		throw new IllegalArgumentException("TestCases and TestSuiteInfo cannot be null");
	}

	StringBuffer errors = new StringBuffer();

	// When a testCase (mathmodel/biomodel) is added to a testSuite, a new version of the mathModel/biomodel should be created.
	// Also, the simulations in the original mathmodel/biomodel should be rid of their parent simulation reference.

	pp.setMessage("Getting testSuite");
	pp.setProgress(1);
	TestSuiteNew testSuite = null;
	try{
		testSuite = getRequestManager().getDocumentManager().getTestSuite(tsInfo.getTSKey());
	}catch(Throwable e){
		throw new RuntimeException("couldn't get test suite "+tsInfo.getTSID()+"\n"+e.getClass().getName()+" mesg="+e.getMessage()+"\n");
	}
	
	if(testSuite != null){
		//Saving BioModels
		TestCaseNew existingTestCases[] = testSuite.getTestCases();
		java.util.HashMap bioModelHashMap = new java.util.HashMap();
		//if(existingTestCases != null){
			// Find BioModels, Using the same BM reference for sibling Applications
			for (int i = 0; i < testCases.length; i++){
				pp.setProgress(Math.max(1,((int)(((double)i/(double)(testCases.length*3))*100))));
				pp.setMessage("Checking "+testCases[i].getVersion().getName());
				try{
					if (testCases[i] instanceof TestCaseNewBioModel) {
						TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCases[i];
						//
						// re-save only once for each BioModel/TestSuite combination
						//
						if (bioModelHashMap.get(bioTestCase.getBioModelInfo().getVersion().getVersionKey())==null){
							pp.setMessage("Getting BM "+testCases[i].getVersion().getName());
							BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
							if (!bioModel.getVersion().getOwner().equals(getRequestManager().getDocumentManager().getUser())) {
								throw new Exception("BioModel does not belong to VCELLTESTACCOUNT, cannot proceed with test!");
							}
							//
							// if biomodel already exists in same testsuite, then use this BioModel edition
							//
							BioModel newBioModel = null;
							if(existingTestCases != null){
								for (int j = 0; newBioModel==null && j < existingTestCases.length; j++){
									if (existingTestCases[j] instanceof TestCaseNewBioModel){
										TestCaseNewBioModel existingTestCaseBioModel = (TestCaseNewBioModel)existingTestCases[j];
										//
										// check if BioModel has same BranchID (an edition of same BioModel)
										//
										if (existingTestCaseBioModel.getBioModelInfo().getVersion().getBranchID().equals(bioTestCase.getBioModelInfo().getVersion().getBranchID())){
											//
											// check if BioModel has same Key (same edition)
											//
											if (existingTestCaseBioModel.getBioModelInfo().getVersion().getVersionKey().equals(bioTestCase.getBioModelInfo().getVersion().getVersionKey())){
												//
												// same, store this "unchanged" in bioModelHashMap
												//
												newBioModel = bioModel;
											}else{
												//
												// different edition of same BioModel ... not allowed
												//
												throw new Exception("can't add new test case using ("+bioTestCase.getBioModelInfo().getVersion().getName()+" "+bioTestCase.getBioModelInfo().getVersion().getDate()+")\n"+
													                           "a test case already exists with different edition of same BioModel dated "+existingTestCaseBioModel.getBioModelInfo().getVersion().getDate());
											}
										}
									}
								}
							}

							if (newBioModel==null){
								pp.setMessage("Saving BM "+testCases[i].getVersion().getName());
								cbit.vcell.mapping.SimulationContext[] simContexts = bioModel.getSimulationContexts();
								for (int j = 0; j < simContexts.length; j++){
									simContexts[j].clearVersion();
									cbit.vcell.geometry.surface.GeometrySurfaceDescription gsd =
										simContexts[j].getGeometry().getGeometrySurfaceDescription();
									if(gsd != null){
										cbit.vcell.geometry.surface.GeometricRegion[] grArr = gsd.getGeometricRegions();
										if(grArr == null){
											gsd.updateAll();
										}
									}
									cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContexts[j]);
									simContexts[j].setMathDescription(mathMapping.getMathDescription());
								}
								Simulation[] sims = bioModel.getSimulations();
								String[] simNames = new String[sims.length];
								for (int j = 0; j < sims.length; j++){
									// prevents parent simulation (from the original mathmodel) reference connection
									// Otherwise it will refer to data from previous (parent) simulation.
									sims[j].clearVersion();
									simNames[j] = sims[j].getName();
								}

								newBioModel = getRequestManager().getDocumentManager().save(bioModel, simNames);
							}
							
							bioModelHashMap.put(bioTestCase.getBioModelInfo().getVersion().getVersionKey(),newBioModel);
						}
					}
				}catch(Throwable e){
					errors.append("Error collecting BioModel for TestCase "+
						(testCases[i].getVersion() != null?"Name="+testCases[i].getVersion().getName():"TCKey="+testCases[i].getTCKey())+"\n"+
							e.getClass().getName()+" "+e.getMessage()+"\n");
				}
			}
		//}
		// then process each BioModelTestCase individually
		//if(bioModelHashMap != null){
		for (int i = 0; i < testCases.length; i++){
			pp.setProgress(Math.max(1,((int)(((double)(i+testCases.length)/(double)(testCases.length*3))*100))));
			pp.setMessage("Checking "+testCases[i].getVersion().getName());
			try{
				AddTestCasesOP testCaseOP = null;
				if (testCases[i] instanceof TestCaseNewBioModel) {
					pp.setMessage("Processing BM "+testCases[i].getVersion().getName());
					TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCases[i];
					
					BioModel newBioModel = (BioModel)bioModelHashMap.get(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
					if(newBioModel == null){
						throw new Exception("BioModel not found");
					}
					cbit.vcell.mapping.SimulationContext simContext = null;
					for (int j = 0; j < newBioModel.getSimulationContexts().length; j++){
						if (newBioModel.getSimulationContexts(j).getName().equals(bioTestCase.getSimContextName())){
							simContext = newBioModel.getSimulationContexts(j);
						}
					}
					
					Simulation[] newSimulations = simContext.getSimulations();
					AddTestCriteriaOPBioModel[] testCriteriaOPs = new AddTestCriteriaOPBioModel[newSimulations.length];
					for (int j = 0; j < newSimulations.length; j++){
						TestCriteriaNewBioModel tcritOrigForSimName = null;
						for(int k=0;bioTestCase.getTestCriterias() != null && k < bioTestCase.getTestCriterias().length;k+= 1){
							if(bioTestCase.getTestCriterias()[k].getSimInfo().getName().equals(newSimulations[j].getName())){
								tcritOrigForSimName = (TestCriteriaNewBioModel)bioTestCase.getTestCriterias()[k];
								break;
							}
						}
						
						KeyValue regressionBioModelKey = null;
						KeyValue regressionBioModelSimKey = null;
						if(bioTestCase.getType().equals(TestCaseNew.REGRESSION)){
							if(regrRefFlag == TestingFrameworkWindowManager.COPY_REGRREF){
								regressionBioModelKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionBioModelInfo() != null?tcritOrigForSimName.getRegressionBioModelInfo().getVersion().getVersionKey():null);
								regressionBioModelSimKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionSimInfo() != null?tcritOrigForSimName.getRegressionSimInfo().getVersion().getVersionKey():null);							
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNORIGINAL_REGRREF){
								regressionBioModelKey = (tcritOrigForSimName != null?bioTestCase.getBioModelInfo().getVersion().getVersionKey():null);
								regressionBioModelSimKey = (tcritOrigForSimName != null?tcritOrigForSimName.getSimInfo().getVersion().getVersionKey():null);								
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNNEW_REGRREF){
								regressionBioModelKey = newBioModel.getVersion().getVersionKey();
								regressionBioModelSimKey = newSimulations[j].getVersion().getVersionKey();						
							}else{
								throw new IllegalArgumentException(this.getClass().getName()+".addTestCases(...) BIOMODEL Unknown Regression Operation Flag");
							}
						}
						testCriteriaOPs[j] =
							new AddTestCriteriaOPBioModel(testCases[i].getTCKey(),
								newSimulations[j].getVersion().getVersionKey(),
								regressionBioModelKey,regressionBioModelSimKey,
								(tcritOrigForSimName != null?tcritOrigForSimName.getMaxAbsError():new Double(1e-16)),
								(tcritOrigForSimName != null?tcritOrigForSimName.getMaxRelError():new Double(1e-9)),
								null);
						
					}

					testCaseOP =
						new AddTestCasesOPBioModel(
							new BigDecimal(tsInfo.getTSKey().toString()),
							newBioModel.getVersion().getVersionKey(),
							simContext.getKey(),
							bioTestCase.getType(), bioTestCase.getAnnotation(), testCriteriaOPs);

					getRequestManager().getDocumentManager().doTestSuiteOP(testCaseOP);
				}
			}catch(Throwable e){
				errors.append("Error processing Biomodel for TestCase "+
							(testCases[i].getVersion() != null?"Name="+testCases[i].getVersion().getName():"TCKey="+testCases[i].getTCKey())+"\n"+
							e.getClass().getName()+" "+e.getMessage()+"\n");
			}
		}
		//}
		
		// Process MathModels
		for (int i = 0; i < testCases.length; i++){
			pp.setProgress(Math.max(1,((int)(((double)(i+testCases.length+testCases.length)/(double)(testCases.length*3))*100))));
			pp.setMessage("Checking "+testCases[i].getVersion().getName());
			try{
				AddTestCasesOP testCaseOP = null;
				if (testCases[i] instanceof TestCaseNewMathModel) {
					TestCaseNewMathModel mathTestCase = (TestCaseNewMathModel)testCases[i];
					pp.setMessage("Getting MathModel "+testCases[i].getVersion().getName());
					MathModel mathModel = getRequestManager().getDocumentManager().getMathModel(mathTestCase.getMathModelInfo().getVersion().getVersionKey());
					if (!mathModel.getVersion().getOwner().equals(getRequestManager().getDocumentManager().getUser())) {
						throw new Exception("MathModel does not belong to VCELLTESTACCOUNT, cannot proceed with test!");
					}
					Simulation[] sims = mathModel.getSimulations();
					String[] simNames = new String[sims.length];
					for (int j = 0; j < sims.length; j++){
						// prevents parent simulation (from the original mathmodel) reference connection
						// Otherwise it will refer to data from previous (parent) simulation.
						sims[j].clearVersion();
						simNames[j] = sims[j].getName();
					}

					pp.setMessage("Saving MathModel "+testCases[i].getVersion().getName());
					MathModel newMathModel = getRequestManager().getDocumentManager().save(mathModel, simNames);
					Simulation[] newSimulations = newMathModel.getSimulations();
					AddTestCriteriaOPMathModel[] testCriteriaOPs = new AddTestCriteriaOPMathModel[newSimulations.length];
					for (int j = 0; j < newSimulations.length; j++){
						TestCriteriaNewMathModel tcritOrigForSimName = null;
						for(int k=0;mathTestCase.getTestCriterias() != null && k < mathTestCase.getTestCriterias().length;k+= 1){
							if(mathTestCase.getTestCriterias()[k].getSimInfo().getName().equals(newSimulations[j].getName())){
								tcritOrigForSimName = (TestCriteriaNewMathModel)mathTestCase.getTestCriterias()[k];
								break;
							}
						}
						
						KeyValue regressionMathModelKey = null;
						KeyValue regressionMathModelSimKey = null;
						if(mathTestCase.getType().equals(TestCaseNew.REGRESSION)){
							if(regrRefFlag == TestingFrameworkWindowManager.COPY_REGRREF){
								regressionMathModelKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionMathModelInfo() != null?tcritOrigForSimName.getRegressionMathModelInfo().getVersion().getVersionKey():null);
								regressionMathModelSimKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionSimInfo() != null?tcritOrigForSimName.getRegressionSimInfo().getVersion().getVersionKey():null);							
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNORIGINAL_REGRREF){
								regressionMathModelKey = (tcritOrigForSimName != null?mathTestCase.getMathModelInfo().getVersion().getVersionKey():null);
								regressionMathModelSimKey = (tcritOrigForSimName != null?tcritOrigForSimName.getSimInfo().getVersion().getVersionKey():null);								
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNNEW_REGRREF){
								regressionMathModelKey = newMathModel.getVersion().getVersionKey();
								regressionMathModelSimKey = newSimulations[j].getVersion().getVersionKey();							
							}else{
								throw new IllegalArgumentException(this.getClass().getName()+".addTestCases(...) MATHMODEL Unknown Regression Operation Flag");
							}
						}
						testCriteriaOPs[j] =
						new AddTestCriteriaOPMathModel(
							testCases[i].getTCKey(),
							newSimulations[j].getVersion().getVersionKey(),
							regressionMathModelKey,regressionMathModelSimKey,
							(tcritOrigForSimName != null?tcritOrigForSimName.getMaxAbsError():new Double(1e-16)),
							(tcritOrigForSimName != null?tcritOrigForSimName.getMaxRelError():new Double(1e-9)),
							null);
					}

					testCaseOP =
						new AddTestCasesOPMathModel(
							new BigDecimal(tsInfo.getTSKey().toString()),
							newMathModel.getVersion().getVersionKey(),
							mathTestCase.getType(), mathTestCase.getAnnotation(),
							testCriteriaOPs);
						
					getRequestManager().getDocumentManager().doTestSuiteOP(testCaseOP);
				}
			}catch(Throwable e){
					errors.append("Error processing MathModel for TestCase "+
								(testCases[i].getVersion() != null?"Name="+testCases[i].getVersion().getName():"TCKey="+testCases[i].getTCKey())+"\n"+
								e.getClass().getName()+" "+e.getMessage()+"\n");
			}
		}
	}
	
	if(errors.length() > 0){
		return errors.toString();
	}
	return null;

}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void checkNewTestSuiteInfo(TestSuiteInfoNew newTestSuiteInfo) throws DataAccessException{

	if(newTestSuiteInfo == null){
		throw new IllegalArgumentException("TestSuiteInfo is null");
	}

	// check if the newly defined testSuite already exists.
	TestSuiteInfoNew[] testSuiteInfos = null;
	
	testSuiteInfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
	if(testSuiteInfos != null){
		for (int i = 0; i < testSuiteInfos.length; i++) {
			if (newTestSuiteInfo.getTSID().equals(testSuiteInfos[i].getTSID())) {
				throw new RuntimeException("TestSuite Version "+newTestSuiteInfo.getTSID()+" already exists");
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:52:18 AM)
 * @return boolean
 * @param mathDesc cbit.vcell.math.MathDescription
 * 
 */
public void compare(TestCriteriaNew testCriteria){
	
	// create the merged data for the simulationInfo in testCriteria and the regression simInfo
	SimulationInfo simInfo = testCriteria.getSimInfo();
	SimulationInfo regrSimInfo = testCriteria.getRegressionSimInfo();

	if (regrSimInfo == null) {
		return;
	}
	
	VCDataIdentifier vcSimId1 = new VCSimulationDataIdentifier(simInfo.getAuthoritativeVCSimulationIdentifier(), 0); 
	VCDataIdentifier vcSimId2 = new VCSimulationDataIdentifier(regrSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
	User user = simInfo.getOwner();
	MergedDataInfo mergedDataInfo = new MergedDataInfo(user, new VCDataIdentifier[] {vcSimId1, vcSimId2});

	// get the data manager and wire it up
	try {
		DataManager mergedDataManager = getRequestManager().getDataManager(mergedDataInfo, false);

		//
		// get all "Data1.XXX" data identifiers ... and remove those which are functions
		// add functions of the form DIFF_XXX = (Data1.XXX - Data2.XXX) for convenience in comparing results.
		//
		Simulation sim1 = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(simInfo);
		Simulation sim2 = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(regrSimInfo);
		DataManager data1Manager = getRequestManager().getDataManager(vcSimId1, sim1.getIsSpatial());
		DataManager data2Manager = getRequestManager().getDataManager(vcSimId2, sim2.getIsSpatial());
		
		Vector functionList = new Vector();
		cbit.vcell.math.AnnotatedFunction data1Functions[] = data1Manager.getFunctions();
		cbit.vcell.math.AnnotatedFunction existingFunctions[] = mergedDataManager.getFunctions();
		cbit.vcell.simdata.DataIdentifier data1Identifiers[] = data1Manager.getDataIdentifiers();
		cbit.vcell.simdata.DataIdentifier data2Identifiers[] = data2Manager.getDataIdentifiers();
		for (int i = 0; i < data1Identifiers.length; i++){
			//
			// make sure dataIdentifier is not already a function
			//
			boolean bIsFunction = false;
			for (int j = 0; j < data1Functions.length; j++){
				if (data1Identifiers[i].getName().equals(data1Functions[j].getName())){
					bIsFunction = true;
				}
			}
			if (bIsFunction){
				continue;
			}
			//
			// make sure corresponding identifier exists in "Data2"
			//
			boolean bIsInData2 = false;
			for (int j = 0; j < data2Identifiers.length; j++){
				if (data2Identifiers[j].getName().equals(data1Identifiers[i].getName())){
					bIsInData2 = true;
				}
			}
			if (!bIsInData2){
				continue;
			}
			
			//
			// create "Diff" function
			//
			String data1Name = "Data1."+data1Identifiers[i].getName();
			String data2Name = "Data2."+data1Identifiers[i].getName();
			String functionName = "DIFF_"+data1Identifiers[i].getName();
			cbit.vcell.simdata.VariableType varType = data1Identifiers[i].getVariableType();
			cbit.vcell.parser.Expression exp = new cbit.vcell.parser.Expression(data1Name+"-"+data2Name);
			cbit.vcell.math.AnnotatedFunction newFunction = new cbit.vcell.math.AnnotatedFunction(functionName,exp,"",varType,true);
			
			//
			// make sure new "Diff" function isn't already in existing function list.
			//
			boolean bDiffFunctionAlreadyHere = false;
			for (int j = 0; j < existingFunctions.length; j++){
				if (newFunction.getName().equals(existingFunctions[j].getName())){
					bDiffFunctionAlreadyHere = true;
				}
			}
			if (bDiffFunctionAlreadyHere){
				continue;
			}
			
			functionList.add(newFunction);
		}
		if (functionList.size()>0){
			AnnotatedFunction[] newDiffFunctions = (AnnotatedFunction[])BeanUtils.getArray(functionList,AnnotatedFunction.class);
			boolean[] bReplaceArr = new boolean[newDiffFunctions.length];
			Arrays.fill(bReplaceArr, false);
			mergedDataManager.addFunctions(newDiffFunctions,bReplaceArr);
		}

		
		// make the viewer
		DynamicDataManager dynamicMergedDataMgr = getRequestManager().getDynamicDataManager(mergedDataInfo);
		addDataListener(dynamicMergedDataMgr);
		DataViewer viewer = dynamicMergedDataMgr.createViewer(mergedDataManager.getIsODEData());
		viewer.setDataViewerManager(this);
		addExportListener(viewer);
		
		// create the simCompareWindow - this is just a lightweight window to display the simResults. 
		// It was created originally to compare 2 sims, it can also be used here instead of creating the more heavy-weight SimWindow.
		SimulationCompareWindow simCompareWindow = new SimulationCompareWindow(mergedDataInfo, viewer);
		if (simCompareWindow != null) {
			// just show it right now...
			final JInternalFrame existingFrame = simCompareWindow.getFrame();
			DocumentWindowManager.showFrame(existingFrame, getTestingFrameworkWindowPanel().getJDesktopPane1());
			
			//SwingUtilities.invokeLater(new Runnable() {
				//public void run() {
					//DocumentWindowManager.showFrame(existingFrame, desktopPane);
				//}
			//});
		}

	} catch (Throwable e) {
		PopupGenerator.showErrorDialog(e.getMessage());
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 4:13:02 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
	
	// just pass them along...
	fireDataJobMessage(event);
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public String duplicateTestSuite(
		final TestSuiteInfoNew testSuiteInfo_Original,
		final TestSuiteInfoNew newTestSuiteInfo,
		int regrRefFlag,
		AsynchProgressPopup pp) throws DataAccessException{
	
	if (testSuiteInfo_Original == null || newTestSuiteInfo == null) {
		throw new IllegalArgumentException(this.getClass().getName()+"duplicateTestSuite_Private: TestSuite cannot be null");
	}
	
	checkNewTestSuiteInfo(newTestSuiteInfo);

	TestSuiteNew testSuite_Original = getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo_Original.getTSKey());
	if(testSuite_Original == null){
		throw new DataAccessException("Couldn't get TestSuite for tsInfo "+testSuiteInfo_Original.getTSID());
	}
	AddTestSuiteOP testSuiteOP =
		new AddTestSuiteOP(
			newTestSuiteInfo.getTSID(),
			newTestSuiteInfo.getTSVCellBuild(),
			newTestSuiteInfo.getTSNumericsBuild(),null,newTestSuiteInfo.getTSAnnotation());
		
	getRequestManager().getDocumentManager().doTestSuiteOP(testSuiteOP);

	TestSuiteInfoNew[] tsinArr = getRequestManager().getDocumentManager().getTestSuiteInfos();
	TestSuiteInfoNew tsin = null;
	for(int i=0;i<tsinArr.length;i+= 1){
		if(tsinArr[i].getTSID().equals(newTestSuiteInfo.getTSID())){
			tsin = tsinArr[i];
			break;
		}
	}
	if(tsin == null){
		throw new DataAccessException("couldn't find new TestSuiteInfo "+newTestSuiteInfo.getTSID()+" in DB");
	}

	TestCaseNew[] originalTestCases = testSuite_Original.getTestCases();
	TestCaseNew[] newTestCases = null;
	if(originalTestCases != null && originalTestCases.length > 0){
		newTestCases = new TestCaseNew[originalTestCases.length];
		for(int i=0;i<originalTestCases.length;i+= 1){
			if(originalTestCases[i] instanceof TestCaseNewMathModel){
				TestCaseNewMathModel tcnmm = (TestCaseNewMathModel)originalTestCases[i];
				TestCriteriaNew[] tcritnmm = (TestCriteriaNew[])tcnmm.getTestCriterias();
				TestCriteriaNewMathModel[] newTCrits = null;
				if(tcritnmm != null && tcritnmm.length > 0){
					//Copy regression and errors
					newTCrits = new TestCriteriaNewMathModel[tcritnmm.length];
					for(int j=0;j<tcritnmm.length;j+= 1){
						newTCrits[j] =
							new TestCriteriaNewMathModel(
								null,
								tcritnmm[j].getSimInfo(),
								((TestCriteriaNewMathModel)tcritnmm[j]).getRegressionMathModelInfo(),
								tcritnmm[j].getRegressionSimInfo(),
								tcritnmm[j].getMaxRelError(),
								tcritnmm[j].getMaxAbsError(),
								null,
								TestCriteriaNew.TCRIT_STATUS_NODATA,null//new will have no data
							);
					}
				}
				//copy mathmodel,type and annotation and copied tcrits
				newTestCases[i] =
					new TestCaseNewMathModel(
						null,
						tcnmm.getMathModelInfo(),
						tcnmm.getType(),
						tcnmm.getAnnotation(),
						newTCrits
					);
			}else if(originalTestCases[i] instanceof TestCaseNewBioModel){
				TestCaseNewBioModel tcnbm = (TestCaseNewBioModel)originalTestCases[i];
				TestCriteriaNew[] tcritnbm = (TestCriteriaNew[])tcnbm.getTestCriterias();
				TestCriteriaNewBioModel[] newTCrits = null;
				if(tcritnbm != null && tcritnbm.length > 0){
					//Copy regression and errors
					newTCrits = new TestCriteriaNewBioModel[tcritnbm.length];
					for(int j=0;j<tcritnbm.length;j+= 1){
						newTCrits[j] =
							new TestCriteriaNewBioModel(
								null,
								tcritnbm[j].getSimInfo(),
								((TestCriteriaNewBioModel)tcritnbm[j]).getRegressionBioModelInfo(),
								((TestCriteriaNewBioModel)tcritnbm[j]).getRegressionApplicationName(),
								tcritnbm[j].getRegressionSimInfo(),
								tcritnbm[j].getMaxRelError(),
								tcritnbm[j].getMaxAbsError(),
								null,
								TestCriteriaNew.TCRIT_STATUS_NODATA,null//new will have no data
							);
					}
				}
				//copy mathmodel,type and annotation and copied tcrits
				newTestCases[i] =
					new TestCaseNewBioModel(
						null,
						tcnbm.getBioModelInfo(),
						tcnbm.getSimContextName(),
						tcnbm.getSimContextKey(),
						tcnbm.getType(),
						tcnbm.getAnnotation(),
						newTCrits
					);
			}else{
				throw new RuntimeException("Unsupported TestCase type "+originalTestCases[i].getClass().getName());
			}
		}
	}
	
	//Add the new TestCases
	if(newTestCases != null && newTestCases.length > 0){
		 return addTestCases(tsin,newTestCases,regrRefFlag,pp);
	}else{
		return null;
	}
}


public void updateTestCaseAnnotation(TestCaseNew testCase,String newAnnotation) throws DataAccessException{
	EditTestCasesOP etcop =
		new EditTestCasesOP(new BigDecimal[] {testCase.getTCKey()},new String[] {newAnnotation});
	getRequestManager().getDocumentManager().doTestSuiteOP(etcop);

}

public void updateTestSuiteAnnotation(TestSuiteInfoNew tsInfoNew,String newAnnotation) throws DataAccessException{
	EditTestSuiteOP etsop =
		new EditTestSuiteOP(new BigDecimal[] {tsInfoNew.getTSKey()},new String[] {newAnnotation});
	getRequestManager().getDocumentManager().doTestSuiteOP(etsop);

}

private void updateReports(final Hashtable<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>> genReportHash){
	new Thread(
	new Runnable() {
		public void run() {
			Set<java.util.Map.Entry<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>>> tsInfoEntry = genReportHash
					.entrySet();
			Iterator<java.util.Map.Entry<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>>> tsInfoIter = tsInfoEntry
					.iterator();
			while (tsInfoIter.hasNext()) {
				try {
					java.util.Map.Entry<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>> entry = tsInfoIter
							.next();
					TestSuiteInfoNew tsInfo = entry.getKey();
					Vector<TestCriteriaCrossRefOPResults.CrossRefData> xrefDataV = entry
							.getValue();
					//
					Vector<AsynchClientTask> tasksVLocal = new java.util.Vector<AsynchClientTask>();
					tasksVLocal
							.add(new cbit.vcell.client.task.TFUpdateRunningStatus(
									TestingFrameworkWindowManager.this, tsInfo));
					TestSuiteNew tsNew = getTestingFrameworkWindowPanel()
							.getDocumentManager().getTestSuite(
									tsInfo.getTSKey());
					for (int i = 0; i < xrefDataV.size(); i++) {
						boolean bDone = false;
						for (int j = 0; j < tsNew.getTestCases().length; j++) {
							if (tsNew.getTestCases()[j].getTCKey().equals(
									xrefDataV.elementAt(i).tcaseKey)) {
								for (int k = 0; k < tsNew.getTestCases()[j]
										.getTestCriterias().length; k++) {
									if (tsNew.getTestCases()[j]
											.getTestCriterias()[k]
											.getTCritKey()
											.equals(
													xrefDataV.elementAt(i).tcritKey)) {
										tasksVLocal
												.add(new TFGenerateReport(
														TestingFrameworkWindowManager.this,
														tsNew.getTestCases()[j],
														tsNew.getTestCases()[j]
																.getTestCriterias()[k]));
										bDone = true;
										break;
									}
								}
							}
							if (bDone) {
								break;
							}
						}
					}
					final String END_NOTIFIER = "END NOTIFIER";
					tasksVLocal.add(new AsynchClientTask() {
						public boolean skipIfAbort() {
							return false;
						}

						public boolean skipIfCancel(UserCancelException exc) {
							return false;
						}

						public String getTaskName() {
							return END_NOTIFIER;
						}

						public int getTaskType() {
							return TASKTYPE_NONSWING_BLOCKING;
						}

						public void run(Hashtable hashTable) throws Exception {
							hashTable.put(END_NOTIFIER, END_NOTIFIER);
						}

					});
					tasksVLocal.add(new TFRefresh(
							TestingFrameworkWindowManager.this, tsInfo));

					AsynchClientTask[] tasksArr = new AsynchClientTask[tasksVLocal
							.size()];
					tasksVLocal.copyInto(tasksArr);
					java.util.Hashtable hashLocal = new java.util.Hashtable();
					ClientTaskDispatcher.dispatch(
							getTestingFrameworkWindowPanel(), hashLocal,
							tasksArr, true);
					//Wait for each report to complete before going on to next because report methods are not thread safe?
					while (!hashLocal.contains(END_NOTIFIER)) {
						Thread.sleep(100);
					}
				} catch (Exception e) {
					PopupGenerator.showErrorDialog("Error updating reports\n"
							+ e.getMessage());
					return;
				}
			}
		}
	}).start();
}

public void toggleTestCaseSteadyState(TestCaseNew[] testCases) throws DataAccessException{
	BigDecimal[] testCaseKeys = new BigDecimal[testCases.length];
	boolean[] isSteadyState = new boolean[testCases.length];
	for (int i = 0; i < testCaseKeys.length; i++) {
		testCaseKeys[i] = testCases[i].getTCKey();
		String type = testCases[i].getType();
		if(type.equals(TestCaseNew.EXACT) || type.equals(TestCaseNew.EXACT_STEADY)){
			isSteadyState[i] = (type.equals(TestCaseNew.EXACT)?true:false);
		}else{
			throw new IllegalArgumentException("TestCase "+testCases[i].getVersion().getName()+" MUST be EXACT type");
		}
	}

	EditTestCasesOP etcop =
		new EditTestCasesOP(testCaseKeys,isSteadyState);
	getRequestManager().getDocumentManager().doTestSuiteOP(etcop);
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:55:34 AM)
 * @param exportEvent cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent exportEvent) {
	// just pass them along...
	fireExportMessage(exportEvent);
	/*
	if (exportEvent.getEventTypeID() == ExportEvent.EXPORT_COMPLETE) {
		// try to download the thing
		downloadExportedData(exportEvent);
	}
	*/
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 */
public String generateTestCaseReport(TestCaseNew testCase,TestCriteriaNew onlyThisTCrit,cbit.util.AsynchProgressPopup pp) {

	StringBuffer reportTCBuffer = new StringBuffer();
	if (testCase == null) {
		reportTCBuffer.append("\n\tTEST CASE : "+testCase.getAnnotation()+"\n"+"\tERROR: Test Case is NULL\n");
	}else{

		pp.setMessage(testCase.getVersion().getName()+" "+testCase.getType()+" Getting Simulations");
		// Get the Simulations
		Simulation[] sims = null;
		reportTCBuffer.append("\n\tTEST CASE : "+(testCase.getVersion() != null?testCase.getVersion().getName():"Null")+"\n\tAnnotation : "+testCase.getAnnotation()+"\n");
		try{
			if(testCase instanceof TestCaseNewMathModel){
				MathModelInfo mmInfo = ((TestCaseNewMathModel)testCase).getMathModelInfo();
				MathModel mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
				sims = mathModel.getSimulations();
				reportTCBuffer.append(
					"\tMathModel : "+mmInfo.getVersion().getName()+", "+mmInfo.getVersion().getDate().toString()+"\n");
			}else if(testCase instanceof TestCaseNewBioModel){
				TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;
				//bioTestCase.
				BioModelInfo bmInfo = bioTestCase.getBioModelInfo();
				BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
				cbit.vcell.mapping.SimulationContext[] simContextArr = bioModel.getSimulationContexts();
				if(simContextArr != null && simContextArr.length > 0){
					cbit.vcell.mapping.SimulationContext simContext = null;
					for(int i=0;i<simContextArr.length;i+= 1){
						if(simContextArr[i].getVersion().getVersionKey().compareEqual(bioTestCase.getSimContextKey())){
							simContext = simContextArr[i];
							break;
						}
					}
					if(simContext != null){
						sims = bioModel.getSimulations(simContext);
						reportTCBuffer.append(
							"\tBioModel : "+bmInfo.getVersion().getName()+", "+bmInfo.getVersion().getDate().toString()+"\n");
					}
				}
			}
			if(sims == null || sims.length == 0){
				reportTCBuffer.append("\tERROR "+"No sims found for TestCase "+
							(testCase.getVersion() != null?"name="+testCase.getVersion().getName():"key="+testCase.getTCKey())+"\n");
			}
			
			if(testCase.getTestCriterias() == null || sims.length != testCase.getTestCriterias().length){
				reportTCBuffer.append("\tERROR "+"Num sims="+sims.length+" does not match testCriteria length="+
							(testCase.getTestCriterias() != null?testCase.getTestCriterias().length+"":"null")+
					" for TestCase "+(testCase.getVersion() != null?"name="+testCase.getVersion().getName():"key="+testCase.getTCKey())+"\n");
			}
			
			//Sort
			if(sims.length > 0){
				java.util.Arrays.sort(sims,
					new java.util.Comparator (){
							public int compare(Object o1,Object o2){
								Simulation si1 = (Simulation)o1;
								Simulation si2 = (Simulation)o2;
								return si1.getName().compareTo(si2.getName());
							}
							public boolean equals(Object obj){
								return false;
							}
						}
				);
			}

			TestCriteriaNew[] testCriterias = (onlyThisTCrit != null?new TestCriteriaNew[] {onlyThisTCrit}:testCase.getTestCriterias());
			
			for (int k = 0;k < sims.length; k++) {
				TestCriteriaNew testCriteria = getMatchingTestCriteria(sims[k],testCriterias);
				if(testCriteria != null){
					//if(testCriteria.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_PASSED) ||
						//testCriteria.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_FAILEDVARS)){
							//continue;
					//}
					pp.setMessage((testCase instanceof TestCaseNewMathModel?"(MM)":"(BM)")+" "+
						(onlyThisTCrit == null?"sim "+(k+1)+" of "+sims.length:"sim="+onlyThisTCrit.getSimInfo().getName())+"  "+testCase.getVersion().getName()+" "+testCase.getType());
					reportTCBuffer.append(generateTestCriteriaReport(testCase,testCriteria,sims[k]));
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
			reportTCBuffer.append("\tERROR "+e.getClass().getName()+" mesg="+e.getMessage()+"\n");
			try{
				if(onlyThisTCrit != null){
					updateTCritStatus(onlyThisTCrit,TestCriteriaNew.TCRIT_STATUS_RPERROR,"TestCase Error "+e.getClass().getName()+" "+e.getMessage());
				}else if(testCase.getTestCriterias() != null){
					for(int i=0;i<testCase.getTestCriterias().length;i+= 1){
						updateTCritStatus(testCase.getTestCriterias()[i],TestCriteriaNew.TCRIT_STATUS_RPERROR,"TestCase Error "+e.getClass().getName()+" "+e.getMessage());
					}
				}
			}catch(Throwable e2){
				//
			}
		}
	}

	return reportTCBuffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 * 
 */
private String generateTestCriteriaReport(TestCaseNew testCase,TestCriteriaNew testCriteria,Simulation sim) {

	String simReportStatus = null;
	String simReportStatusMessage = null;
	
	StringBuffer reportTCBuffer = new StringBuffer();
	VariableComparisonSummary failVarSummaries[] = null;
	VariableComparisonSummary allVarSummaries[] = null;
	double absErr = 0;
	double relErr = 0;
	if (testCriteria != null) {
		absErr = testCriteria.getMaxAbsError().doubleValue();
	 	relErr = testCriteria.getMaxRelError().doubleValue();
	}
	
	reportTCBuffer.append("\t\t"+sim.getName() + " : "+"\n");
	try {		
		SimulationInfo refSimInfo = testCriteria.getRegressionSimInfo();
		if (testCase.getType().equals(TestCaseNew.REGRESSION) && refSimInfo == null) {
			reportTCBuffer.append("\t\t\tNo reference SimInfo, SimInfoKey="+testCriteria.getSimInfo().getVersion().getName()+". Cannot perform Regression Test!\n");
			simReportStatus = TestCriteriaNew.TCRIT_STATUS_NOREFREGR;
		}else{
			VCDataIdentifier vcdID = new VCSimulationDataIdentifier(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
			DataManager dataManager = getRequestManager().getDataManager(vcdID, sim.getIsSpatial());
			
			double timeArray[] = dataManager.getDataSetTimes();
			if (timeArray == null || timeArray.length == 0) {
				reportTCBuffer.append("\t\t\tNO DATA : Simulation not run yet.\n");
				simReportStatus = TestCriteriaNew.TCRIT_STATUS_NODATA;
			} else {
				// SPATIAL simulation
				if (sim.getMathDescription().isSpatial()){
					// Get EXACT solution if test case type is EXACT, Compare with numerical
					if (testCase.getType().equals(TestCaseNew.EXACT) || testCase.getType().equals(TestCaseNew.EXACT_STEADY)) {
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResultsWithExact(sim, dataManager,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());
						// Failed var summaries
						failVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					// Get CONSTRUCTED solution if test case type is CONSTRUCTED, Compare with numerical
					} else if (testCase.getType().equals(TestCaseNew.CONSTRUCTED)) {
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResultsWithExact(sim, dataManager,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());
						// Failed var summaries
						failVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					} else if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
						Simulation refSim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(refSimInfo);
						VCDataIdentifier refVcdID = new VCSimulationDataIdentifier(refSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
						DataManager refDataManager = getRequestManager().getDataManager(refVcdID, refSim.getIsSpatial());
						String varsToCompare[] = getVariableNamesToCompare(sim,refSim);
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResults(sim, dataManager, refSim, refDataManager, varsToCompare,testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());
						// Failed var summaries
						failVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}							
					}
				}else{  // NON-SPATIAL CASE
					ODESolverResultSet numericalResultSet = dataManager.getODESolverResultSet();
					// Get EXACT result set if test case type is EXACT, Compare with numerical
					if (testCase.getType().equals(TestCaseNew.EXACT) || testCase.getType().equals(TestCaseNew.EXACT_STEADY)) {
						ODESolverResultSet exactResultSet = MathTestingUtilities.getExactResultSet(sim.getMathDescription(), timeArray, sim.getSolverTaskDescription().getSensitivityParameter());
						String varsToCompare[] = getVariableNamesToCompare(sim,sim);
						SimulationComparisonSummary simCompSummary_exact = MathTestingUtilities.compareResultSets(numericalResultSet,exactResultSet,varsToCompare,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());

						// Get all the variable comparison summaries and the failed ones to print out report for EXACT solution comparison.
						failVarSummaries = simCompSummary_exact.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary_exact.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}							
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					// Get CONSTRUCTED result set if test case type is CONSTRUCTED , compare with numerical
					} else if (testCase.getType().equals(TestCaseNew.CONSTRUCTED)) {
						ODESolverResultSet constructedResultSet = MathTestingUtilities.getConstructedResultSet(sim.getMathDescription(), timeArray);
						String varsToCompare[] = getVariableNamesToCompare(sim,sim);
						SimulationComparisonSummary simCompSummary_constr = MathTestingUtilities.compareResultSets(numericalResultSet,constructedResultSet,varsToCompare,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());

						// Get all the variable comparison summaries and the failed ones to print out report for CONSTRUCTED solution comparison.
						failVarSummaries = simCompSummary_constr.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary_constr.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}							
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					} else if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
						Simulation refSim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(testCriteria.getRegressionSimInfo());
						String varsToTest[] = getVariableNamesToCompare(sim,refSim);
						
						VCDataIdentifier refVcdID = new VCSimulationDataIdentifier(refSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
						DataManager refDataManager = getRequestManager().getDataManager(refVcdID, refSim.getIsSpatial());
						ODESolverResultSet referenceResultSet = refDataManager.getODESolverResultSet();
						double refTimeArray[] = refDataManager.getDataSetTimes();
						SimulationComparisonSummary simCompSummary_regr = null;							
						//if (timeArray.length != refTimeArray.length) {
						simCompSummary_regr = MathTestingUtilities.compareUnEqualResultSets(numericalResultSet, referenceResultSet,varsToTest,testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());
						//} else {
							//simCompSummary_regr = MathTestingUtilities.compareResultSets(numericalResultSet, referenceResultSet, varsToTest);
						//}
						// Get all the variable comparison summaries and the failed ones to print out report for CONSTRUCTED solution comparison.
						failVarSummaries = simCompSummary_regr.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary_regr.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}							
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}													
					}
				}
			}
		}
	} catch (Throwable e) {
		simReportStatus = TestCriteriaNew.TCRIT_STATUS_RPERROR;
		simReportStatusMessage = e.getClass().getName()+" "+e.getMessage();
		reportTCBuffer.append("\t\t"+simReportStatusMessage+"\n");
		e.printStackTrace(System.out);
	}

	try{			
		// Remove any test results already present for testCriteria
		RemoveTestResultsOP removeResultsOP = new RemoveTestResultsOP(new BigDecimal[] {testCriteria.getTCritKey()});
		//testResultsOPsVector.add(removeResultsOP);
		getRequestManager().getDocumentManager().doTestSuiteOP(removeResultsOP)		;
		// Create new AddTestREsultsOP object for the current simulation./testCriteria.
		if(allVarSummaries != null){
			AddTestResultsOP testResultsOP = new AddTestResultsOP(testCriteria.getTCritKey(), allVarSummaries);
			//testResultsOPsVector.add(testResultsOP);			
			// Write the testResults for simulation/TestCriteria into the database ...
			getRequestManager().getDocumentManager().doTestSuiteOP(testResultsOP);
		}
		//Update report status
		updateTCritStatus(testCriteria,simReportStatus,simReportStatusMessage);
	}catch(Throwable e){
		reportTCBuffer.append("\t\tUpdate DB Results failed. "+e.getClass().getName()+" "+e.getMessage()+"\n");
		try{
			getRequestManager().getDocumentManager().doTestSuiteOP(
				new EditTestCriteriaOPReportStatus(testCriteria.getTCritKey(),TestCriteriaNew.TCRIT_STATUS_RPERROR,e.getClass().getName()+" "+e.getMessage())
				);
		}catch(Throwable e2){
			//Nothing more can be done
		}
	}
		
	//}

	return reportTCBuffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 */
public String generateTestSuiteReport(TestSuiteInfoNew testSuiteInfo,AsynchProgressPopup pp) {

	if (testSuiteInfo == null) {
		return "Test Suite is null";
	}
	StringBuffer sb = new StringBuffer();
	try{
		//Iterate thro' testSuiteInfo to add annotation to reportTSBuffer and get all test cases
		// Get TestSuite corresponding to argument testSuiteInfo
		TestSuiteNew testSuite = getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo.getTSKey());
		// Get MathmodelInfos associated with each test suite
		TestCaseNew[] testCases  = null;
		testCases = testSuite.getTestCases();
		if (testCases == null) {
			return testSuiteInfo.toString()+" has not testcases";
		}

		// Iterate thro' test cases to get all simulations and get the variableSimulationSummary, add to buffer
		sb.append("\n"+testSuiteInfo.toString()+"\n");
		for (int j = 0; j < testCases.length; j++) {
			pp.setProgress(1+(int)((((double)j/(double)testCases.length)*100)));
			sb.append(generateTestCaseReport(testCases[j],null,pp));
		}
		
	}catch(Throwable e){
		e.printStackTrace();
		sb.append("ERROR "+e.getClass().getName()+" mesg="+e.getMessage());
	}finally{
		return sb.toString();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getAddTestCaseDialog() {
	return addTestCaseDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getAddTestSuiteDialog() {
	return addTestSuiteDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.AddTestSuitePanel
 */
private AddTestSuitePanel getAddTestSuitePanel() {
	return addTestSuitePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @return java.lang.String
 */
java.awt.Component getComponent() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getEditTestCriteriaDialog() {
	return editTestCriteriaDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.EditTestCriteriaPanel
 */
private EditTestCriteriaPanel getEditTestCriteriaPanel() {
	return editTestCriteriaPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @return java.lang.String
 */
public String getManagerID() {
	return ClientMDIManager.TESTING_FRAMEWORK_WINDOW_ID;
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
 
// Used to obtain the testCriteria (from the testCriterias array) matching the simulation passed as argument.

private TestCriteriaNew getMatchingTestCriteria(Simulation sim, TestCriteriaNew[] tcriterias){
	for (int i = 0; i < tcriterias.length; i++){
		if (tcriterias[i].getSimInfo().getVersion().getVersionKey().equals(sim.getVersion().getVersionKey())) {
			return tcriterias[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
public TestCaseNew getNewTestCase() throws UserCancelException{
	// invoke the testCaseAddPanel to define testCaseInfo parameters.
	// This is where we invoke the TestCaseAddPanel to define a testCase for the test suite ...
	getTestCaseAddPanel().resetTextFields();
	while(true){
		Object choice = showAddTestCaseDialog(getTestCaseAddPanel(), null);
		
		if (choice != null && choice.equals("OK")) {
			try{
				return getTestCaseAddPanel().getNewTestCase();
			}catch(Exception e){
				PopupGenerator.showErrorDialog("Error getting New TestCase:\n"+e.getMessage());
				continue;
			}
		}
		throw UserCancelException.CANCEL_GENERIC;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public TestCriteriaNew getNewTestCriteriaFromUser(String solutionType, TestCriteriaNew origTestCriteria)throws UserCancelException {

	// Reset the text fields in the EditCriteriaPanel.	
	getEditTestCriteriaPanel().setExistingTestCriteria(origTestCriteria);
	getEditTestCriteriaPanel().setSolutionType(solutionType);
	getEditTestCriteriaPanel().resetTextFields();

	while(true){
		// display the editCriteriaPanel.
		Object choice = showEditTestCriteriaDialog(getEditTestCriteriaPanel(), null);
	
		if (choice != null && choice.equals("OK")) {
			TestCriteriaNew tcritNew = getEditTestCriteriaPanel().getNewTestCriteria();
			if(tcritNew instanceof TestCriteriaNewMathModel){
				TestCriteriaNewMathModel tcritNewMM = (TestCriteriaNewMathModel)tcritNew;
				if((tcritNewMM.getRegressionMathModelInfo() == null && tcritNewMM.getRegressionSimInfo() != null)
						||
					(tcritNewMM.getRegressionMathModelInfo() != null && tcritNewMM.getRegressionSimInfo() == null)){
					PopupGenerator.showErrorDialog("Must specify both Reference MathModel and Simulation");
					continue;
				}
			}else if(tcritNew instanceof TestCriteriaNewBioModel){
				TestCriteriaNewBioModel tcritNewBM = (TestCriteriaNewBioModel)tcritNew;
				if((tcritNewBM.getRegressionBioModelInfo() == null && tcritNewBM.getRegressionSimInfo() != null)
						||
					(tcritNewBM.getRegressionBioModelInfo() != null && tcritNewBM.getRegressionSimInfo() == null)){
					PopupGenerator.showErrorDialog("Must specify both Reference BioModel App and Simulation");
					continue;
				}
			}else{
				
			}
			return tcritNew;
		}
	
		throw UserCancelException.CANCEL_GENERIC;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public NewTestSuiteUserInformation getNewTestSuiteInfoFromUser(String tsAnnotation,String duplicateTestSuiteName) throws Exception{

	getAddTestSuitePanel().resetTextFields(tsAnnotation,duplicateTestSuiteName != null);
	while(true){
		Object choice = showAddTestSuiteDialog(getAddTestSuitePanel(), null,duplicateTestSuiteName);
	
		if (choice != null && choice.equals("OK")) {
			return getAddTestSuitePanel().getTestSuiteInfo();
		}
		throw UserCancelException.CANCEL_DB_SELECTION;
	}

}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:08:56 PM)
 * @return cbit.sql.KeyValue
 * @param bmInfo cbit.vcell.biomodel.BioModelInfo
 * @param appName java.lang.String
 */
public KeyValue getSimContextKey(BioModelInfo bmInfo, String appName) throws DataAccessException {
	BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
	if (bioModel!=null){
		cbit.vcell.mapping.SimulationContext simContexts[] = bioModel.getSimulationContexts();
		for (int i = 0; i < simContexts.length; i++){
			if (simContexts[i].getName().equals(appName)){
				return simContexts[i].getVersion().getVersionKey();
			}
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.TestCaseAddPanel
 */
private TestCaseAddPanel getTestCaseAddPanel() {
	return testCaseAddPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public cbit.vcell.client.desktop.TestingFrameworkWindowPanel getTestingFrameworkWindowPanel() {
	return testingFrameworkWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 1:53:11 PM)
 * @return java.lang.String[]
 * @param sim1 cbit.vcell.solver.Simulation
 * @param sim2 cbit.vcell.solver.Simulation
 */
private String[] getVariableNamesToCompare(Simulation sim1, Simulation sim2) {
	java.util.HashSet hashSet = new java.util.HashSet();

	//
	// get Variables from Simulation 1
	//	
	cbit.vcell.math.Variable simVars[] = sim1.getVariables();
	for (int i = 0;simVars!=null && i < simVars.length; i++){
		if (simVars[i] instanceof cbit.vcell.math.VolVariable ||
			simVars[i] instanceof cbit.vcell.math.MemVariable ||
			simVars[i] instanceof cbit.vcell.math.VolumeRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.MembraneRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentRegionVariable){

			hashSet.add(simVars[i].getName());
		}
		if (sim1.getSolverTaskDescription().getSensitivityParameter() != null) {
			if (simVars[i] instanceof cbit.vcell.math.VolVariable) {
				hashSet.add(cbit.vcell.solver.ode.SensVariable.getSensName((cbit.vcell.math.VolVariable)simVars[i], sim1.getSolverTaskDescription().getSensitivityParameter()));
			}
		}
	}

	//
	// add Variables from Simulation 2
	//	
	simVars = sim2.getVariables();
	for (int i = 0;simVars!=null && i < simVars.length; i++){
		if (simVars[i] instanceof cbit.vcell.math.VolVariable ||
			simVars[i] instanceof cbit.vcell.math.MemVariable ||
			simVars[i] instanceof cbit.vcell.math.VolumeRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.MembraneRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentRegionVariable){

			hashSet.add(simVars[i].getName());
		}
		if (sim2.getSolverTaskDescription().getSensitivityParameter() != null) {
			if (simVars[i] instanceof cbit.vcell.math.VolVariable) {
				hashSet.add(cbit.vcell.solver.ode.SensVariable.getSensName((cbit.vcell.math.VolVariable)simVars[i], sim2.getSolverTaskDescription().getSensitivityParameter()));
			}
		}			
	}
	
	return (String[])hashSet.toArray(new String[hashSet.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:28:23 PM)
 */
public void initializeAllPanels() {
	try {
		DocumentManager documentManager = getRequestManager().getDocumentManager();
		getTestingFrameworkWindowPanel().setDocumentManager(documentManager);
		getAddTestSuitePanel().setTestingFrameworkWindowManager(this);
		getTestCaseAddPanel().setTestingFrameworkWindowManager(this);
		getEditTestCriteriaPanel().setTestingFrameworkWindowManager(this);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @return boolean
 */
public boolean isRecyclable() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void loadModel(TestCaseNew testCase) throws DataAccessException{
	
	cbit.vcell.document.VCDocumentInfo vcDocInfo = null;
	if (testCase instanceof TestCaseNewMathModel) {
		TestCaseNewMathModel mathTestCase = (TestCaseNewMathModel)testCase;
		vcDocInfo = getRequestManager().getDocumentManager().getMathModelInfo(mathTestCase.getMathModelInfo().getVersion().getVersionKey());
	} else if (testCase instanceof TestCaseNewBioModel) {
		TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;
		vcDocInfo = getRequestManager().getDocumentManager().getBioModelInfo(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
	}			
	getRequestManager().openDocument(vcDocInfo, this, true);
}


public void queryTCritCrossRef(final TestSuiteInfoNew tsin,final TestCriteriaNew tcrit,final String varName){
	
	try {
		QueryTestCriteriaCrossRefOP queryTestCriteriaCrossRefOP =
			new QueryTestCriteriaCrossRefOP(tsin.getTSKey(),tcrit.getTCritKey(),varName);
		TestCriteriaCrossRefOPResults testCriteriaCrossRefOPResults =
			(TestCriteriaCrossRefOPResults)getRequestManager().getDocumentManager().doTestSuiteOP(queryTestCriteriaCrossRefOP);

		final Vector<TestCriteriaCrossRefOPResults.CrossRefData> xrefDataV = testCriteriaCrossRefOPResults.getCrossRefData();
		final TestSuiteInfoNew[] testSuiteInfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
		Vector<TestSuiteInfoNew> missingTestSuites = new Vector<TestSuiteInfoNew>();
		for (int i = 0; i < testSuiteInfos.length; i++) {
			boolean bFound = false;
			for (int j = 0; j < xrefDataV.size(); j++) {
				if(xrefDataV.elementAt(j).tsVersion.equals(testSuiteInfos[i].getTSID())){
					bFound = true;
					break;
				}
			}
			if(!bFound){
				missingTestSuites.add(testSuiteInfos[i]);
			}
		}
		TestCriteriaCrossRefOPResults.CrossRefData xrefDataSource = null;
		for (int i = 0; i < xrefDataV.size(); i++) {
			if(xrefDataV.elementAt(i).tcritKey.equals(tcrit.getTCritKey())){
				xrefDataSource = xrefDataV.elementAt(i);
				break;
			}
		}
		if(xrefDataSource == null){
			throw new RuntimeException("Couldn't find source Test Criteria in query results.");
		}
		final int numColumns = 8;
		final int XREFDATA_ALLOWANCE = 1;
		final int TSKEY_ALLOWANCE = 1;
		final int XREFDATA_OFFSET = numColumns;
		final int TSDATE_OFFSET = 1;
		final int VARNAME_OFFSET = 3;
		final int TSKEYMISSING_OFFSET = numColumns+1;
		final String[] colNames = new String[numColumns];
		final Object[][] sourceRows = new Object[xrefDataV.size()+missingTestSuites.size()][numColumns+XREFDATA_ALLOWANCE+TSKEY_ALLOWANCE];
		String sourceTestSuite = null;
		colNames[0] = "tsVersion";
		colNames[1] = "tsDate";
		colNames[2] = "tsBaseVersion";
		colNames[3] = "varName";
		colNames[4] = "RelErorr";
		colNames[5] = "limitRelErorr";
		colNames[6] = "limitAbsErorr";
		colNames[7] = "AbsErorr";

		for (int i = 0; i < xrefDataV.size(); i++) {
			sourceRows[i][colNames.length] = xrefDataV.elementAt(i);
			if(xrefDataV.elementAt(i).tcritKey.equals(queryTestCriteriaCrossRefOP.getTestCriterium())){
				sourceTestSuite = xrefDataV.elementAt(i).tsVersion;
			}
			sourceRows[i][0] = xrefDataV.elementAt(i).tsVersion;
			sourceRows[i][2] =
				(xrefDataV.elementAt(i).tsRefVersion == null?
						(xrefDataV.elementAt(i).regressionModelID == null/* && xrefDataV.elementAt(i).regressionMMref==null*/?"":"Ref Model exist BUT outside of TestSuites")
						:xrefDataV.elementAt(i).tsRefVersion);
			sourceRows[i][6] = xrefDataV.elementAt(i).maxAbsErorr;
			sourceRows[i][5] = xrefDataV.elementAt(i).maxRelErorr;
			if(xrefDataV.elementAt(i).varName != null){
				sourceRows[i][VARNAME_OFFSET] = xrefDataV.elementAt(i).varName;
				sourceRows[i][4] = xrefDataV.elementAt(i).varCompSummary.getRelativeError();
				sourceRows[i][7] = xrefDataV.elementAt(i).varCompSummary.getAbsoluteError();
			}else{
				sourceRows[i][VARNAME_OFFSET] = "-No Report-";
				sourceRows[i][4] = null;//"No Report";
				sourceRows[i][7] = null;//"No Report";
			}
			for (int j = 0; j < testSuiteInfos.length; j++) {
				if(xrefDataV.elementAt(i).tsVersion.equals(testSuiteInfos[j].getTSID())){
					sourceRows[i][1] = testSuiteInfos[j].getTSDate();
					break;
				}
			}
		}
		
		for (int i = xrefDataV.size(); i < sourceRows.length; i++) {
			sourceRows[i][0] = missingTestSuites.elementAt(i-xrefDataV.size()).getTSID();
			sourceRows[i][TSDATE_OFFSET] = missingTestSuites.elementAt(i-xrefDataV.size()).getTSDate();
			sourceRows[i][TSKEYMISSING_OFFSET] = missingTestSuites.elementAt(i-xrefDataV.size()).getTSKey();
		}
		
//		Arrays.sort(rows,
//				new Comparator<Object[]>(){
//					public int compare(Object[] o1, Object[] o2) {
//						return ((String)o1[0]).compareToIgnoreCase((String)o2[0]);
////						if(o1[0].equals(o2[0])){
////							return o1[3].compareToIgnoreCase(o2[3]);
////						}
////						return o1[0].compareToIgnoreCase(o2[0]);
//					}
//				}
//			);
		
		final ManageTableModel tableModel = new ManageTableModel(){
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex==TSDATE_OFFSET){
					return Date.class;
				}else if(columnIndex >=4 && columnIndex<= 7){
					return Double.class;
				}
				return String.class;
			}
			public boolean isCellEditable(int row, int column) {
		        return false;
		    }
			public Object getValueAt(int rowIndex, int columnIndex) {
				return ((Object[])rows.get(rowIndex))[columnIndex];
			}
			public int getColumnCount() {
				return colNames.length;
			}
			public String getColumnName(int column) {
				return colNames[column];
			}
			public void sortColumn(final int col, final boolean ascending) {
				Collections.sort((List<Object[]>)rows,
						new Comparator<Object[]>(){
							public int compare(Object[] o1, Object[] o2) {
								if(o1[col] == null && o2[col] == null){
									return 0;
								}
//								if(ascending){
									if(o1[col] == null){
										return 1;
									}
									if(o2[col] == null){
										return -1;
									}
//								}else{
//									if(o1[col] == null){
//										return -1;
//									}
//									if(o2[col] == null){
//										return 1;
//									}
//								}
								if(getColumnClass(col).equals(String.class)){
									if(ascending){
										return ((String)o1[col]).compareToIgnoreCase(((String)o2[col]));
									}else{
										return ((String)o2[col]).compareToIgnoreCase(((String)o1[col]));
									}
								}else if(getColumnClass(col).equals(Date.class)){
									if(ascending){
										return ((Date)o1[col]).compareTo(((Date)o2[col]));
									}
									return ((Date)o2[col]).compareTo(((Date)o1[col]));
								}else if(getColumnClass(col).equals(Double.class)){
									if(ascending){
										return ((Double)o1[col]).compareTo(((Double)o2[col]));
									}
									return ((Double)o2[col]).compareTo(((Double)o1[col]));
									
								}
								throw new RuntimeException("TestSuite XRef Query unexpecte column class "+getColumnClass(col).getName());
							}
						}
					);
			}

		};
		tableModel.setData(Arrays.asList(sourceRows));
		
		//Create table
		final JSortTable table = new JSortTable(tableModel);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(500, 250));

		table.getColumnModel().getColumn(TSDATE_OFFSET).setCellRenderer(
				new DefaultTableCellRenderer(){
//					DateFormat formatter = DateFormat.getDateTimeInstance();
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						return super.getTableCellRendererComponent(table,(value == null?null:((Date)value).toString())/*formatter.format((Date)value)*/, isSelected, hasFocus, row, column);
					}
				}
			);
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(){
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				return super.getTableCellRendererComponent(table,(value == null?null:((Double)value).toString())/*formatter.format((Date)value)*/, isSelected, hasFocus, row, column);
			}			
		};
		table.getColumnModel().getColumn(4).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(5).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(6).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(7).setCellRenderer(dtcr);
//		table.getColumnModel().getColumn(4).setCellRenderer(
//				new DefaultTableCellRenderer(){
//					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//						return super.getTableCellRendererComponent(table,(value == null?null:((Double)value).toString())/*formatter.format((Date)value)*/, isSelected, hasFocus, row, column);
//					}
//				}
//			);

//		table.getTableHeader().setReorderingAllowed(false);

//		final JDialog d = new JDialog();
		final JInternalFrame d = new JInternalFrame();

		//Popup Menu
		final TestCriteriaCrossRefOPResults.CrossRefData xrefDataSourceFinal = xrefDataSource;
		final JPopupMenu queryPopupMenu = new JPopupMenu();
		final JMenuItem changeLimitsMenuItem = new JMenuItem("Change Selected Error Limits...");
		final String OPEN_MODEL = "Open Model(s)";
		final JMenuItem openModelMenuItem = new JMenuItem(OPEN_MODEL);
		final String OPEN_REGRREFMODEL = "Open Regr Ref Model(s)";
		final JMenuItem openRegrRefModelMenuItem = new JMenuItem(OPEN_REGRREFMODEL);
		final String SELECT_REF_IN_TREE = "Select in Tree View";
		final JMenuItem showInTreeMenuItem = new JMenuItem(SELECT_REF_IN_TREE);
		final String SELECT_REGR_REF_IN_TREE = "Select RegrRef TCase in Tree View";
		final JMenuItem showRegrRefInTreeMenuItem = new JMenuItem(SELECT_REGR_REF_IN_TREE);
		
		queryPopupMenu.add(changeLimitsMenuItem);
		queryPopupMenu.add(openModelMenuItem);
		queryPopupMenu.add(openRegrRefModelMenuItem);
		queryPopupMenu.add(showInTreeMenuItem);
		queryPopupMenu.add(showRegrRefInTreeMenuItem);
		
		ActionListener showInTreeActionListener = 
			new ActionListener(){
				public void actionPerformed(ActionEvent actionEvent) {
					int[] selectedRows = table.getSelectedRows();
					if(selectedRows == null || selectedRows.length != 1){
						PopupGenerator.showErrorDialog("Action "+actionEvent.getActionCommand()+" accepts only single selection!");
						return;
					}
					TestCriteriaCrossRefOPResults.CrossRefData xrefData =
						(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRows[0], XREFDATA_OFFSET);
					BigDecimal missingTSKey = (BigDecimal)tableModel.getValueAt(selectedRows[0], TSKEYMISSING_OFFSET);
					if(actionEvent.getActionCommand().equals(SELECT_REF_IN_TREE)){
						getTestingFrameworkWindowPanel().selectInTreeView((xrefData != null?xrefData.tsKey:missingTSKey),(xrefData != null?xrefData.tcaseKey:null),(xrefData != null?xrefData.tcritKey:null));
					}else if(actionEvent.getActionCommand().equals(SELECT_REGR_REF_IN_TREE)){
						if(xrefData == null){
							PopupGenerator.showErrorDialog(d, "No Regression Reference info available.");
							return;
						}
						getTestingFrameworkWindowPanel().selectInTreeView((xrefData != null?xrefData.regressionModelTSuiteID:null),(xrefData != null?xrefData.regressionModelTCaseID:null),(xrefData != null?xrefData.regressionModelTCritID:null));						
					}
					d.setVisible(true);
				}
			};
		showInTreeMenuItem.addActionListener(showInTreeActionListener);
		showRegrRefInTreeMenuItem.addActionListener(showInTreeActionListener);
				
		ActionListener openModelsActionListener = 
		new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				int[] selectedRows = table.getSelectedRows();
				String failureS = "";
				TestCriteriaCrossRefOPResults.CrossRefData xrefData = null;
				int openCount = 0;
				for (int i = 0; i < selectedRows.length; i++) {
					try {
						xrefData =
							(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRows[i], XREFDATA_OFFSET);
						if(xrefData != null && (actionEvent.getActionCommand().equals(OPEN_REGRREFMODEL)?xrefData.regressionModelID != null:true)){
							openCount+= 1;
							cbit.vcell.document.VCDocumentInfo vcDocInfo = null;
							if(xrefData.isBioModel){
								vcDocInfo = getRequestManager().getDocumentManager().getBioModelInfo(new KeyValue((actionEvent.getActionCommand().equals(OPEN_REGRREFMODEL)?xrefData.regressionModelID:xrefData.modelID)));
							}else{
								vcDocInfo = getRequestManager().getDocumentManager().getMathModelInfo(new KeyValue((actionEvent.getActionCommand().equals(OPEN_REGRREFMODEL)?xrefData.regressionModelID:xrefData.modelID)));
							}
							getRequestManager().openDocument(vcDocInfo, TestingFrameworkWindowManager.this, true);
						}
					} catch (Exception e) {
						failureS+= failureS+"key="+xrefData.modelID+" "+e.getMessage()+"\n";
						e.printStackTrace();
					}
				}
				if(failureS.length() > 0 || openCount == 0){
					PopupGenerator.showErrorDialog("Failed to open some models\n"+failureS+(openCount == 0?"Selection(s) had no model(s)":""));
				}
				d.setVisible(true);
			}
		};
		openModelMenuItem.addActionListener(openModelsActionListener);
		openRegrRefModelMenuItem.addActionListener(openModelsActionListener);
		
		changeLimitsMenuItem.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent actionEvent) {
					int[] selectedRows = table.getSelectedRows();
					Vector<TestCriteriaCrossRefOPResults.CrossRefData> changeTCritV = new Vector<TestCriteriaCrossRefOPResults.CrossRefData>();
					for (int i = 0; i < selectedRows.length; i++) {
						TestCriteriaCrossRefOPResults.CrossRefData xrefData =
							(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRows[i], XREFDATA_OFFSET);
						if(xrefData != null){
							boolean bFound = false;
							for (int j = 0; j < changeTCritV.size(); j++) {
								if(changeTCritV.elementAt(j).tcritKey.equals(xrefData.tcritKey)){
									bFound = true;
									break;
								}
							}
							if(!bFound){
								changeTCritV.add(xrefData);
							}
						}
					}
					if(changeTCritV.size() > 0){
						Double relativeErrorLimit = null;
						Double absoluteErrorLimit = null;
						while(true){
							try{
								String ret = PopupGenerator.showInputDialog(d,
										"Enter new TestCriteria Error Limits for '"+xrefDataSourceFinal.simName+"'.  '-'(dash) to keep original value.",
										"RelativeErrorLimit,AbsoluteErrorLimit");
								int commaPosition = ret.indexOf(',');
								if(commaPosition == -1){
									throw new Exception("No comma found separating RelativeErrorLimit AbsoluteErrorLimit");
								}
								if(commaPosition != ret.lastIndexOf(',')){
									throw new Exception("Only 1 comma allowed separating RelativeErrorLimit and AbsoluteErrorLimit");
								}
								final String KEEP_ORIGINAL_VALUE = "-";
								String relativeErrorS = ret.substring(0, commaPosition);
								String absoluteErrorS = ret.substring(commaPosition+1,ret.length());
								if(!relativeErrorS.equals(KEEP_ORIGINAL_VALUE)){
									relativeErrorLimit = Double.parseDouble(relativeErrorS);
								}
								if(!absoluteErrorS.equals(KEEP_ORIGINAL_VALUE)){
									absoluteErrorLimit = Double.parseDouble(absoluteErrorS);
								}
								if((relativeErrorLimit != null && relativeErrorLimit <= 0) || (absoluteErrorLimit != null && absoluteErrorLimit <= 0)){
									throw new Exception("Error limits must be greater than 0");
								}
								break;
							}catch(UserCancelException e){
								d.setVisible(true);
								return;
							}catch(Exception e){
								PopupGenerator.showErrorDialog("Error parsing Error Limits\n"+e.getMessage());
							}
						}
						double[] relErrorLimitArr = new double[changeTCritV.size()];
						double[] absErrorLimitArr = new double[changeTCritV.size()];
						Object[][] rows = new Object[changeTCritV.size()][5];
						for (int j = 0; j < changeTCritV.size(); j++) {
							relErrorLimitArr[j] = (relativeErrorLimit != null?relativeErrorLimit.doubleValue():changeTCritV.elementAt(j).maxRelErorr);
							absErrorLimitArr[j] = (absoluteErrorLimit != null?absoluteErrorLimit.doubleValue():changeTCritV.elementAt(j).maxAbsErorr);
							rows[j][2] = new Double(relErrorLimitArr[j]);
							rows[j][4] = new Double(absErrorLimitArr[j]);
							rows[j][1] = new Double(changeTCritV.elementAt(j).maxRelErorr);
							rows[j][3] = new Double(changeTCritV.elementAt(j).maxAbsErorr);
							rows[j][0] = changeTCritV.elementAt(j).tsVersion;
						}
						try{
							PopupGenerator.showComponentOKCancelTableList(
								d, "Confirm Error Limit Changes",
								new String[] {"TSVersion","Orig RelErrorLimit","New RelErrorLimit","Orig AbsErrorLimit","New AbsErrorLimit"},
								rows,
								null);
						}catch(UserCancelException e){
							d.setVisible(true);
							return;
						}
						
						//Get information needed to generate new TestCriteria Reports
						final String YES_ANSWER = "Yes";
						Hashtable<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>> genReportHash = null;
						String genRepResult = PopupGenerator.showWarningDialog(d, "Generate Reports for changed Test Criterias?", new String[] {YES_ANSWER,"No"}, YES_ANSWER);
						if(genRepResult != null && genRepResult.equals(YES_ANSWER)){
							genReportHash = new Hashtable<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>>();
							for (int i = 0; i < changeTCritV.size(); i++) {
								boolean bFound = false;
								for (int j = 0; j < testSuiteInfos.length; j++) {
									if(changeTCritV.elementAt(i).tsVersion.equals(testSuiteInfos[j].getTSID())){
										bFound = true;
										Vector<TestCriteriaCrossRefOPResults.CrossRefData> tempV = genReportHash.get(testSuiteInfos[j]);
										if(tempV == null){
											tempV = new Vector<TestCriteriaCrossRefOPResults.CrossRefData>();
											genReportHash.put(testSuiteInfos[j],tempV);
										}
										tempV.add(changeTCritV.elementAt(i));
									}
								}
								if(!bFound){
									PopupGenerator.showErrorDialog("Couldn't find testsuiteinfo for testcriteria");
									return;
								}
							}
						}
						
						
						
						
						BigDecimal[] changeTCritBDArr = new BigDecimal[changeTCritV.size()];
						for (int i = 0; i < changeTCritV.size(); i++) {
							changeTCritBDArr[i] = changeTCritV.elementAt(i).tcritKey;
						}
						ChangeTestCriteriaErrorLimitOP changeTestCriteriaErrorLimitOP =
							new ChangeTestCriteriaErrorLimitOP(changeTCritBDArr,absErrorLimitArr,relErrorLimitArr);
						try{
							getTestingFrameworkWindowPanel().getDocumentManager().doTestSuiteOP(changeTestCriteriaErrorLimitOP);
						}catch(Exception e){
							PopupGenerator.showErrorDialog("Failed Changing Error limits for selected "+xrefDataSourceFinal.simName+"\n"+e.getMessage());
							return;
						}
						d.dispose();
						getTestingFrameworkWindowPanel().refreshTree(null);
						if(genReportHash != null){
							updateReports(genReportHash);
						}else{
							new Thread(new Runnable(){
								public void run() {
									TestingFrameworkWindowManager.this.queryTCritCrossRef(tsin, tcrit, varName);
								}
							}).start();
						}
					}else{
						PopupGenerator.showErrorDialog("No selected rows contain Test Criteria.");
					}
				}
			}
		);
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				checkPopup(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				checkPopup(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				checkPopup(e);
			}
			private void checkPopup(MouseEvent mouseEvent){
				if(mouseEvent.isPopupTrigger()){
//Not use because popupmenu will not show at edge
//					if(table.getSelectedRowCount() <= 1){
//						table.getSelectionModel().setSelectionInterval(table.rowAtPoint(mouseEvent.getPoint()),table.rowAtPoint(mouseEvent.getPoint()));
//					}
					doPopup(mouseEvent);
				}
				else{
					queryPopupMenu.setVisible(false);
				}
			}
			private void doPopup(MouseEvent mouseEvent){
//				int selectedRow = table.getSelectedRow();
//				TestCriteriaCrossRefOPResults.CrossRefData xrefData =
//					(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRow, numColumns);
//				queryPopupMenu.add(changeLimitsMenuItem);
//				queryPopupMenu.add(openModelMenuItem);
//				queryPopupMenu.add(openRegrRefModelMenuItem);
//				queryPopupMenu.add(showInTreeMenuItem);
				if(table.getSelectedRowCount() == 0){
					changeLimitsMenuItem.setEnabled(false);
					openModelMenuItem.setEnabled(false);
					openRegrRefModelMenuItem.setEnabled(false);
					showInTreeMenuItem.setEnabled(false);
					showRegrRefInTreeMenuItem.setEnabled(false);
				}else{
					changeLimitsMenuItem.setEnabled(true);
					openModelMenuItem.setEnabled(true);
					openRegrRefModelMenuItem.setEnabled(true);
					showInTreeMenuItem.setEnabled(true);
					if(table.getSelectedRowCount() == 1){
						TestCriteriaCrossRefOPResults.CrossRefData xrefData =
							(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(table.getSelectedRow(), numColumns);
						showRegrRefInTreeMenuItem.setEnabled(xrefData != null && xrefData.regressionModelID != null && xrefData.tsRefVersion != null);
					}
				}
				queryPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			}
		});
		
		//Create dialog
		d.setTitle(
				(xrefDataSource.isBioModel?"BM":"MM")+
				" "+xrefDataSource.tcSolutionType+
				" ("+sourceTestSuite+") "+
				" \""+(xrefDataSource.isBioModel?xrefDataSource.bmName:xrefDataSource.mmName)+
				"\"  ::  "+(xrefDataSource.isBioModel?"app=\""+xrefDataSource.bmAppName+"\"  ::  sim=\""+xrefDataSource.simName+"\"":"sim=\""+xrefDataSource.simName+"\""));
//		d.setModal(false);
		d.getContentPane().add(scrollPane);
		d.setSize(600,400);
//		d.setLocation(300,200);
//		BeanUtils.centerOnComponent(d,null);
//		d.setVisible(true);
		d.setClosable(true);
		d.setResizable(true);
		showDataViewerPlotsFrame(d);

	} catch (DataAccessException e) {
		e.printStackTrace();
		PopupGenerator.showErrorDialog("Error Query TestCriteria Cross Ref:\n"+e.getMessage());
	}
	
}

/**
 * Remove a cbit.vcell.desktop.controls.DataListener.
 */
public void removeDataListener(cbit.vcell.desktop.controls.DataListener newListener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void removeTestCase(TestCaseNew testCase) throws DataAccessException{

	getRequestManager().getDocumentManager().doTestSuiteOP(
			new RemoveTestCasesOP(new BigDecimal[] {testCase.getTCKey()}));
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void removeTestSuite(TestSuiteInfoNew tsInfo) throws DataAccessException{

	TestSuiteNew testSuite = getRequestManager().getDocumentManager().getTestSuite(tsInfo.getTSKey());
	getRequestManager().getDocumentManager().doTestSuiteOP(new RemoveTestSuiteOP(tsInfo.getTSKey()));
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void saveNewTestSuiteInfo(TestSuiteInfoNew newTestSuiteInfo) throws DataAccessException{

	checkNewTestSuiteInfo(newTestSuiteInfo);

	AddTestSuiteOP testSuiteOP =
		new AddTestSuiteOP(
			newTestSuiteInfo.getTSID(),
			newTestSuiteInfo.getTSVCellBuild(),
			newTestSuiteInfo.getTSNumericsBuild(),
			null,newTestSuiteInfo.getTSAnnotation());
	getRequestManager().getDocumentManager().doTestSuiteOP(testSuiteOP);
	
	getTestingFrameworkWindowPanel().refreshTree(newTestSuiteInfo);
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public BioModelInfo selectBioModelInfo() {
	return getRequestManager().selectBioModelInfo(this);
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public MathModelInfo selectMathModelInfo() {
	return getRequestManager().selectMathModelInfo(this);
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public Object[] selectRefSimInfo(BioModelInfo bmInfo,String appName) throws DataAccessException {
	if (bmInfo == null || appName == null || appName.length() == 0) {
		PopupGenerator.showErrorDialog("Selected Reference BioModel is null, choose a reference BioModel before choosing simulation!");
		return null;
	}

	// code for obtaining siminfos from Biomodel and displaying it as a list
	// and displaying the siminfo in the label

	cbit.vcell.mapping.SimulationContext simContext = null;
	//try {
		BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
		for(int i=0;i<bioModel.getSimulationContexts().length;i+= 1){
			if(bioModel.getSimulationContexts()[i].getName().equals(appName)){
				simContext = bioModel.getSimulationContexts()[i];
				break;
			}
		}
		if(simContext != null){
			cbit.vcell.solver.SimulationInfo simInfo = selectSimInfoPrivate(bioModel.getSimulations(simContext));
			return new Object[] {simContext.getName(),simInfo};
		}else{
			PopupGenerator.showErrorDialog("No simcontext found for biomodel "+bmInfo+" app="+appName);
			return null;
		}
	//} catch (cbit.vcell.server.DataAccessException e) {
	//	e.printStackTrace(System.out);
	//}
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public SimulationInfo selectRefSimInfo(MathModelInfo mmInfo) {
	if (mmInfo == null) {
		PopupGenerator.showErrorDialog("Selected Reference MathModel is null, choose a reference MathModel before choosing simulation!");
		return null;
	}

	// code for obtaining siminfos from mathmodel and displaying it as a list
	// and displaying the siminfo in the label

	// Get MathModel from MathModelInfo
	MathModel mathModel = null;
	try {
		 mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
	} catch (cbit.vcell.server.DataAccessException e) {
		e.printStackTrace(System.out);
	}
	return selectSimInfoPrivate(mathModel.getSimulations());
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2004 1:52:50 PM)
 * @return cbit.vcell.solver.SimulationInfo
 * @param sims cbit.vcell.solver.Simulation[]
 */
private SimulationInfo selectSimInfoPrivate(Simulation[] sims) {
	
	// code for obtaining siminfos from Biomodel and displaying it as a list
	// and displaying the siminfo in the label

	//Sort
	if(sims.length > 0){
		java.util.Arrays.sort(sims,
			new java.util.Comparator (){
					public int compare(Object o1,Object o2){
						Simulation si1 = (Simulation)o1;
						Simulation si2 = (Simulation)o2;
						return si1.getName().compareTo(si2.getName());
					}
					public boolean equals(Object obj){
						return false;
					}
				}
		);
	}
	
	String simInfoNames[] = new String[sims.length];
	for (int i = 0; i < simInfoNames.length; i++){
		simInfoNames[i] = sims[i].getSimulationInfo().getName();
	}

	// Display the list of simInfo names in a list for user to choose the simulationInfo to compare with
	// in the case of regression testing.
	String selectedRefSimInfoName = (String)PopupGenerator.showListDialog(this, simInfoNames, "Please select reference simulation");
	if (selectedRefSimInfoName == null) {
		// PopupGenerator.showErrorDialog("Reference SimInfo not selected");
		return null;
	}
	int simIndex = -1;
	for (int i=0;i<simInfoNames.length;i++){
		if (simInfoNames[i].equals(selectedRefSimInfoName)){
			simIndex = i;
		}
	}
	if (simIndex == -1){
		PopupGenerator.showErrorDialog("No such SimInfo Exists : "+selectedRefSimInfoName);
		return null;
	}

	SimulationInfo simInfo = (SimulationInfo)sims[simIndex].getSimulationInfo();
	return simInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
private void setTestingFrameworkWindowPanel(cbit.vcell.client.desktop.TestingFrameworkWindowPanel newTestingFrameworkWindowPanel) {
	testingFrameworkWindowPanel = newTestingFrameworkWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAddTestCaseDialog(JComponent addTCPanel, Component requester) {

	addTCPanel.setPreferredSize(new java.awt.Dimension(600, 200));
	getAddTestCaseDialog().setMessage("");
	getAddTestCaseDialog().setMessage(addTCPanel); 
	getAddTestCaseDialog().setValue(null);
	JDialog d = getAddTestCaseDialog().createDialog(requester, "Add New TestCase:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d);
	return getAddTestCaseDialog().getValue();
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAddTestSuiteDialog(JComponent addTSPanel, Component requester,String duplicateTestSuiteName) {

	addTSPanel.setPreferredSize(new java.awt.Dimension(350, 250));
	getAddTestSuiteDialog().setMessage("");
	getAddTestSuiteDialog().setMessage(addTSPanel);
	getAddTestSuiteDialog().setValue(null);
	JDialog d = getAddTestSuiteDialog().createDialog(requester, (duplicateTestSuiteName != null?"Duplicate TestSuite '"+duplicateTestSuiteName+"'":"Add New TestSuite"));
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d);
	return getAddTestSuiteDialog().getValue();
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:55:40 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
private void showDataViewerPlotsFrame(final javax.swing.JInternalFrame plotFrame) {
	dataViewerPlotsFramesVector.add(plotFrame);
	DocumentWindowManager.showFrame(plotFrame, getTestingFrameworkWindowPanel().getJDesktopPane1());
	plotFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			dataViewerPlotsFramesVector.remove(plotFrame);
		}
	});
}
	
/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 4:44:45 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames) {
	for (int i = 0; i < plotFrames.length; i++){
		showDataViewerPlotsFrame(plotFrames[i]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showEditTestCriteriaDialog(JComponent editTCrPanel, Component requester) {
	editTCrPanel.setPreferredSize(new java.awt.Dimension(400, 300));
	getEditTestCriteriaDialog().setMessage("");
	getEditTestCriteriaDialog().setMessage(editTCrPanel);
	getEditTestCriteriaDialog().setValue(null);
	JDialog d = getEditTestCriteriaDialog().createDialog(requester, "Edit Test Criteria:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d);
	return getEditTestCriteriaDialog().getValue();
}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 4:44:45 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simStatusChanged(SimStatusEvent simStatusEvent) {

	//KeyValue simKey = simStatusEvent.getVCSimulationIdentifier().getSimulationKey();
	//SimulationJobStatus jobStatus = simStatusEvent.getJobStatus();
	//if (simKey == null || jobStatus == null) {
		//// we don't know what it's all about...
		//return;
	//}
	//Simulation[] sims = getBioModel().getSimulations();
	//if (sims == null) {
		//// don't care
		//return;
	//}
	//Simulation simulation = null;
	//for (int i = 0; i < sims.length; i++){
		//if (simKey.equals(sims[i].getKey()) || simKey.equals(sims[i].getSimulationVersion().getParentSimulationReference())) {
			//simulation = sims[i];
			//break;
		//}	
	//}
	//if (simulation == null) {
		//// don't care
		//return;
	//}
	//// we have it - if failure message, notify
	//if (simStatusEvent.getJobStatus().isFailed()) {
		//PopupGenerator.showErrorDialog(this, "Simulation '" + simulation.getName() + "' failed\n" + simStatusEvent.getStatusMessage());
	//}
	//// was the gui on it ever opened? - then update list
	//SimulationContext simContext = null;
	//simulation = null;
	//Enumeration en = getApplicationsHash().keys();
	//while (en.hasMoreElements()) {
		//SimulationContext sc = (SimulationContext)en.nextElement();
		//sims = sc.getSimulations();
		//if (sims != null) {
		//}
		//for (int i = 0; i < sims.length; i++){
			//if (simKey.equals(sims[i].getKey()) || simKey.equals(sims[i].getSimulationVersion().getParentSimulationReference())) {
				//simulation = sims[i];
				//break;
			//}	
		//}
		//if (simulation != null) {
			//simContext = sc;
			//break;
		//}
	//}
	//if (simulation == null || simContext == null) {
		//return;
	//}
	//// the gui was opened...
	//ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(simContext);
	//ClientSimManager simManager = appComponents.getAppEditor().getSimulationWorkspace().getClientSimManager();
	//simManager.updateStatus(simManager.getSimulationStatus(simulation).fromMessage(simStatusEvent.getJobStatus(), simStatusEvent.getProgress()), simulation);
	//// is there new data?
	//if (simStatusEvent.getTimepoint() != null) {
		//fireNewData(new DataEvent(this, simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier()));
	//}
}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 4:44:45 PM)
 */
public void startExport(cbit.vcell.export.server.ExportSpecs exportSpecs) {
	getRequestManager().startExport(this, exportSpecs);
}


/**
 * Insert the method's description here.
 * Creation date: (11/16/2004 6:38:33 AM)
 * @param simInfos cbit.vcell.solver.SimulationInfo[]
 */
public String startSimulations(TestCriteriaNew[] tcrits,AsynchProgressPopup pp) {

	if(tcrits == null || tcrits.length == 0){
		throw new IllegalArgumentException("startSimulations: No TestCriteria arguments");
	}
	
	StringBuffer errors = new StringBuffer();
	for(int i=0;i<tcrits.length;i+= 1){
		try{
			pp.setProgress((int)(1+(((double)i/(double)tcrits.length)*100)));
			pp.setMessage("Trying to run sim "+tcrits[i].getSimInfo().getName());
			getRequestManager().runSimulation(tcrits[i].getSimInfo());
			updateTCritStatus(tcrits[i],TestCriteriaNew.TCRIT_STATUS_SIMRUNNING,null);
		}catch(Throwable e){
			e.printStackTrace();
			errors.append("Failed to start sim "+tcrits[i].getSimInfo().getVersion().getName()+" "+e.getClass().getName()+" mesg="+e.getMessage()+"\n");
			try{
				updateTCritStatus(tcrits[i],TestCriteriaNew.TCRIT_STATUS_SIMFAILED,e.getClass().getName()+" "+e.getMessage());
			}catch(Throwable e2){
				e.printStackTrace();
				errors.append("Failed to start sim "+
					tcrits[i].getSimInfo().getVersion().getName()+" "+e2.getClass().getName()+" mesg="+e2.getMessage()+"\n");				
			}
		}
	}

	if(errors.length() > 0){
		errors.insert(0,"Error starting simulations\n");
		return errors.toString();
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:52:18 AM)
 * @return boolean
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public String startTestSuiteSimulations(TestSuiteInfoNew testSuiteInfo,AsynchProgressPopup pp){

	StringBuffer errors = new StringBuffer();
	try{
		pp.setProgress(1);
		pp.setMessage("Getting TestSuite "+testSuiteInfo.getTSID());
		TestSuiteNew testSuite =
			getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo.getTSKey());
		
		Vector tcritVector = new Vector();
		cbit.vcell.numericstest.TestCaseNew[] testCases = testSuite.getTestCases();
		if(testCases != null){
			for (int i = 0; i < testCases.length; i++){
				TestCriteriaNew[] tCriteria = testCases[i].getTestCriterias();
				if(tCriteria != null){
					for (int j = 0; j < tCriteria.length; j++) {
						tcritVector.add(tCriteria[j]);
					}
				}
			}
			if(tcritVector.size() > 0){
				TestCriteriaNew[] tcritArray = (TestCriteriaNew[])BeanUtils.getArray(tcritVector, TestCriteriaNew.class);
				String errorString = startSimulations(tcritArray,pp);
				if(errorString != null){
					errors.append(errorString+"\n");
				}
			}
		}
	}catch(Throwable e){
		errors.append(e.getClass().getName()+" "+e.getMessage());
	}
	
	if(errors.length() > 0){
		errors.insert(0,"Error starting TestSuite simulations\n");
		return errors.toString();
	}
	return null;
	
}


/**
 * Insert the method's description here.
 * Creation date: (11/16/2004 7:44:27 AM)
 * 
 */
public String updateSimRunningStatus(AsynchProgressPopup pp,TestSuiteInfoNew tsin){

	StringBuffer errors = new StringBuffer();
	
	Vector runningTCrits = new Vector();
	try{
		TestSuiteInfoNew[] tsinfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
		if(tsinfos != null && tsinfos.length > 0){
			for(int i=0;i<tsinfos.length;i+= 1){
				try{
					if(tsin != null && !tsinfos[i].getTSKey().equals(tsin.getTSKey())){
						continue;
					}
					pp.setProgress(i*50/tsinfos.length);
					pp.setMessage("Update SimsRunning, Getting Testsuite "+tsinfos[i].getTSID());
					TestSuiteNew tsn = getRequestManager().getDocumentManager().getTestSuite(tsinfos[i].getTSKey());
					TestCaseNew[] tcnArr = tsn.getTestCases();
					if(tcnArr != null){
						for(int j=0;tcnArr != null && j<tcnArr.length;j+= 1){
							TestCriteriaNew[] tcritArr = tcnArr[j].getTestCriterias();
							if(tcritArr != null){
								for(int k=0;tcritArr != null && k<tcritArr.length;k+= 1){
									if(tcritArr[k].getReportStatus() != null &&
										tcritArr[k].getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_SIMRUNNING) ||
										tcritArr[k].getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE)){
											runningTCrits.add(tcritArr[k]);
										}
								}
							}
						}
					}
				}catch(Throwable e){
					e.printStackTrace();
					errors.append(e.getClass().getName()+" "+e.getMessage());
				}
			}
			for(int i=0;i<runningTCrits.size();i+= 1){
				try{
					TestCriteriaNew tcn = (TestCriteriaNew)runningTCrits.elementAt(i);
					cbit.vcell.solver.SimulationInfo simInfo = tcn.getSimInfo();
					pp.setProgress((int)(50+(i*50/runningTCrits.size())));
					pp.setMessage("Update SimsRunning, Setting Status "+simInfo.getName());
					//Check if there is some status different from "running"
					if(simInfo != null){
						SimulationStatus simStatus = getRequestManager().getServerSimulationStatus(simInfo);
						if(simStatus != null){
							if (simStatus.isFailed()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMFAILED,"Sim msg="+simStatus.getJob0StatusMessage());
							}else if(simStatus.isJob0Completed()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
							}else if(!simStatus.isRunning()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE,
									"Sim jobstatus "+simStatus.toString()+" "+simStatus.getJob0StatusMessage());
							}
						}else{
							updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE,
								"Can't get sim job status "+(simStatus == null?"jobStatus is null":""));
						}
					}
				}catch(Throwable e){
					e.printStackTrace();
					errors.append(e.getClass().getName()+" "+e.getMessage());		
				}
			}
		}
	}catch(Throwable e){
		e.printStackTrace();
		errors.append(e.getClass().getName()+" "+e.getMessage());		
	}

	if(errors.length() > 0){
		errors.insert(0,"Error updating simstatus\n");
		return errors.toString();
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 6:13:58 AM)
 * @param tcrit cbit.vcell.numericstest.TestCriteriaNew
 * @param status java.lang.String
 * @param statusmessage java.lang.String
 */
private void updateTCritStatus(TestCriteriaNew tcrit, String status, String statusMessage)throws DataAccessException{
	
	//try{
		getRequestManager().getDocumentManager().doTestSuiteOP(
				new EditTestCriteriaOPReportStatus(tcrit.getTCritKey(),status,statusMessage)
			);
	//}catch(Throwable e){
		//e.printStackTrace();
		//return e.getClass().getName()+" "+e.getMessage();
	//}
	//return null;
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void updateTestCriteria(TestCriteriaNew origTestCriteria,TestCriteriaNew newTestCriteria)throws DataAccessException{

	EditTestCriteriaOP testCriteriaOP = null;
	if(newTestCriteria instanceof TestCriteriaNewMathModel){
		MathModelInfo regrMMInfo = ((TestCriteriaNewMathModel)newTestCriteria).getRegressionMathModelInfo();
		SimulationInfo regrsimInfo = ((TestCriteriaNewMathModel)newTestCriteria).getRegressionSimInfo();
		testCriteriaOP =
			new EditTestCriteriaOPMathModel(
				origTestCriteria.getTCritKey(),
				(regrMMInfo != null?regrMMInfo.getVersion().getVersionKey():null),
				(regrsimInfo != null?regrsimInfo.getVersion().getVersionKey():null),
				newTestCriteria.getMaxAbsError(),
				newTestCriteria.getMaxRelError()
			);
	}else if(newTestCriteria instanceof TestCriteriaNewBioModel){
		BioModelInfo regrBMInfo = ((TestCriteriaNewBioModel)newTestCriteria).getRegressionBioModelInfo();
		SimulationInfo regrsimInfo = ((TestCriteriaNewBioModel)newTestCriteria).getRegressionSimInfo();
		testCriteriaOP =
			new EditTestCriteriaOPBioModel(
				origTestCriteria.getTCritKey(),
				(regrBMInfo != null?regrBMInfo.getVersion().getVersionKey():null),
				(regrsimInfo != null?regrsimInfo.getVersion().getVersionKey():null),
				newTestCriteria.getMaxAbsError(),
				newTestCriteria.getMaxRelError()
			);
	}
	getRequestManager().getDocumentManager().doTestSuiteOP(testCriteriaOP);
	RemoveTestResultsOP removeTestResults = new RemoveTestResultsOP(new BigDecimal[] {origTestCriteria.getTCritKey()});
	getRequestManager().getDocumentManager().doTestSuiteOP(removeTestResults);
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:52:18 AM)
 * @return boolean
 * @param mathDesc cbit.vcell.math.MathDescription
 * 
 */
public void viewResults(TestCriteriaNew testCriteria) {
	
	VCDataIdentifier vcdID = new VCSimulationDataIdentifier(testCriteria.getSimInfo().getAuthoritativeVCSimulationIdentifier(), 0);

	// get the data manager and wire it up
	try {
		Simulation sim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(testCriteria.getSimInfo());
		DataManager dataManager = getRequestManager().getDataManager(vcdID, sim.getIsSpatial());
		
		DynamicDataManager dynamicDataMgr = getRequestManager().getDynamicDataManager(sim);
		addDataListener(dynamicDataMgr);
		// make the viewer
		boolean expectODEdata = sim.getMathDescription().getGeometry().getDimension() == 0;
		DataViewer viewer = dynamicDataMgr.createViewer(expectODEdata);
		viewer.setDataViewerManager(this);
		addExportListener(viewer);
		
		// create the simCompareWindow - this is just a lightweight window to display the simResults. 
		// It was created originally to compare 2 sims, it can also be used here instead of creating the mor eheavy-weight SimWindow.
		SimulationCompareWindow simCompareWindow = new SimulationCompareWindow(vcdID, viewer);
		if (simCompareWindow != null) {
			// just show it right now...
			final JInternalFrame existingFrame = simCompareWindow.getFrame();
			DocumentWindowManager.showFrame(existingFrame, getTestingFrameworkWindowPanel().getJDesktopPane1());
			
			//SwingUtilities.invokeLater(new Runnable() {
				//public void run() {
					//DocumentWindowManager.showFrame(existingFrame, desktopPane);
				//}
			//});
		}
	} catch (Throwable e) {
		PopupGenerator.showErrorDialog(e.getMessage());
	}
}


public User getUser() {
	return getRequestManager().getDocumentManager().getUser();
}


}