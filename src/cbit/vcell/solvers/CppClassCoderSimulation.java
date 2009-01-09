package cbit.vcell.solvers;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.util.*;
import java.util.*;
import java.io.*;

import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.*;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.*;
import cbit.vcell.messaging.JmsUtils;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class CppClassCoderSimulation extends CppClassCoder {
	private SimulationJob simulationJob = null;
	private String baseDataName = null;

/**
 * VarContextCppCoder constructor comment.
 * @param name java.lang.String
 */
protected CppClassCoderSimulation(CppCoderVCell cppCoderVCell, SimulationJob argSimulationJob, String baseDataName) 
{
	super(cppCoderVCell,"UserSimulation", "Simulation");
	this.simulationJob = argSimulationJob;
	this.baseDataName = baseDataName;
}


/**
 * This method was created by a SmartGuide.
 * @param out java.io.PrintWriter
 */
protected void writeConstructor(java.io.PrintWriter out) throws Exception {
	out.println(getClassName()+"::"+getClassName()+"(CartesianMesh *mesh)");
	out.println(": Simulation(mesh)");
	out.println("{");
	out.println("\tVolumeRegionVariable *volumeRegionVar = 0;");
	out.println("\tMembraneRegionVariable *membraneRegionVar = 0;");
	out.println("\tVolumeVariable *volumeVar = 0;");
	out.println("\tMembraneVariable *membraneVar = 0;");
	out.println("\tContourVariable *contourVar = 0;");
	out.println("\tODESolver *odeSolver = 0;");
	out.println("\tSparseLinearSolver *slSolver = 0;");
	out.println("\tEqnBuilder *builder = 0;");
	out.println("\tSparseMatrixEqnBuilder  *smbuilder = 0;");
	out.println("\tlong sizeX = mesh->getNumVolumeX();");
	out.println("\tlong sizeY = mesh->getNumVolumeY();");
	out.println("\tlong sizeZ = mesh->getNumVolumeZ();");
	out.println("\tint numSolveRegions = 0;");
	out.println("\tint *solveRegions = 0;");
	out.println("\tint numVolumeRegions = mesh->getNumVolumeRegions();");
	out.println("\tstring varname, units;");
	out.println("");

	Simulation simulation = simulationJob.getWorkingSim();
	Variable variables[] = simulation.getVariables();
	for (int i=0;i<variables.length;i++){
	  	Variable var = (Variable)variables[i];
	  	String units;
	  	if (var instanceof VolVariable){
	  		units = "uM";
	  		VolVariable volVar = (VolVariable)var;
	  		out.println("\tvarname = \"" + volVar.getName() + "\";");
	  		out.println("\tunits = \"" + units+ "\";");
	  		out.println("\tvolumeVar = new VolumeVariable(sizeX, sizeY, sizeZ, varname, units);");
	  		
	  		//
	  		// need to specify which SubDomains should be solved for
	  		//
	  		Vector<SubDomain> listOfSubDomains = new Vector<SubDomain>();
	  		int totalNumCompartments = 0;
	  		StringBuffer compartmentNames = new StringBuffer();
	  		Enumeration<SubDomain> subDomainEnum = simulation.getMathDescription().getSubDomains();
	  		while (subDomainEnum.hasMoreElements()){
		  		SubDomain subDomain = (SubDomain)subDomainEnum.nextElement();
		  		if (subDomain instanceof CompartmentSubDomain){
			  		CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)subDomain;
			  		totalNumCompartments++;
			  		Equation varEquation = subDomain.getEquation(var);
			  		if (varEquation != null) {
			  			if (!(varEquation instanceof PdeEquation) || !((PdeEquation)varEquation).isDummy(simulation, compartmentSubDomain)){
				  			listOfSubDomains.add(compartmentSubDomain);
				  			int handle = simulation.getMathDescription().getHandle(compartmentSubDomain);
				  			compartmentNames.append(compartmentSubDomain.getName()+"("+handle+") ");
				  		}
			  		}
		  		}
	  		}
	  		if (totalNumCompartments == listOfSubDomains.size()){
		  		//
		  		// every compartments has an equation, set numSolveRegions accordingly
		  		//
		  		out.println("\t// solving for all regions");
		  		out.println("\tnumSolveRegions = 0;  // flag specifying to solve for all regions");
		  		out.println("\tsolveRegions = NULL;");
	  		} else if (listOfSubDomains.size() > 0){
		  		//
		  		// only solve for some compartments
		  		//
			  	out.println("\t// solving for only regions belonging to ("+compartmentNames.toString()+"), first 'numSolveRegions' elements used");
			  	out.println("\tsolveRegions = new int[numVolumeRegions];");
		  		
		  		//
		  		//  build list of regions belonging to the required SubDomains
		  		//
				out.println("\tnumSolveRegions = 0;");
		  		out.println("\tfor (int i = 0; i < numVolumeRegions; i++){");
		  		out.println("\t\tVolumeRegion *volRegion = mesh->getVolumeRegion(i);");
			  	for (int j = 0; j < listOfSubDomains.size(); j++){
					CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)listOfSubDomains.elementAt(j);
				  	int handle = simulation.getMathDescription().getHandle(compartmentSubDomain);
					out.println("\t\tif (volRegion->getFeature()->getHandle() == (FeatureHandle)(0xff & "+handle+")){  // test if this region is same as '"+compartmentSubDomain.getName()+"'");
					out.println("\t\t\tsolveRegions[numSolveRegions ++] = volRegion->getId();");
					out.println("\t\t}");
				}
				out.println("\t}");
	  		}
	  		
	  		if (listOfSubDomains.size() > 0) {
		  		if (simulation.getMathDescription().isPDE(volVar)) {
		  			if (simulation.getMathDescription().isPdeSteady(volVar)) {
		  				out.println("\tsmbuilder = new EllipticVolumeEqnBuilder(volumeVar,mesh, numSolveRegions, solveRegions);");
		  			} else {
		  				out.println("\tsmbuilder = new SparseVolumeEqnBuilder(volumeVar,mesh," + (simulation.getMathDescription().hasVelocity(volVar) ? "false" : "true") + ", numSolveRegions, solveRegions);");
		  			}
	  				out.println("\tslSolver = new SparseLinearSolver(volumeVar,smbuilder,"+simulation.hasTimeVaryingDiffusionOrAdvection(volVar)+");");
	  				out.println("\taddSolver(slSolver);");
		  		}else{
		  			out.println("\todeSolver = new ODESolver(volumeVar,mesh,numSolveRegions,solveRegions);");
		  			out.println("\tbuilder = new EqnBuilderReactionForward(volumeVar,mesh,odeSolver);");
		  			out.println("\todeSolver->setEqnBuilder(builder);");
		  			out.println("\taddSolver(odeSolver);");
		  		}
	  		}
	  		out.println("\taddVariable(volumeVar);");
	  		out.println();
	  	}else if (var instanceof MemVariable) { // membraneVariable
		  	units = "molecules/squm";
	  		MemVariable memVar = (MemVariable)var;
	  		out.println("\tvarname = \"" + memVar.getName() + "\";");
	  		out.println("\tunits = \"" + units+ "\";");		  		
	  		out.println("\tmembraneVar = new MembraneVariable(mesh->getNumMembraneElements(),varname, units);");
		  	if (simulation.getMathDescription().isPDE(memVar)) {
		  		out.println("\tsmbuilder = new MembraneEqnBuilderDiffusion(membraneVar,mesh);");
	  			out.println("\tslSolver = new SparseLinearSolver(membraneVar,smbuilder,"+simulation.hasTimeVaryingDiffusionOrAdvection(memVar)+");");	  			
	  			out.println("\taddSolver(slSolver);");
		  		out.println("\taddVariable(membraneVar);");
		  	} else {		  		
		  		out.println("\t// solving for all regions");
		  		out.println("\tnumSolveRegions = 0;  // flag specifying to solve for all regions");
		  		out.println("\tsolveRegions = NULL;");
		  		out.println("\todeSolver = new ODESolver(membraneVar,mesh,numSolveRegions,solveRegions);");
	  			out.println("\tbuilder = new MembraneEqnBuilderForward(membraneVar,mesh,odeSolver);");
	  			out.println("\todeSolver->setEqnBuilder(builder);");
	  			out.println("\taddSolver(odeSolver);");
		  		out.println("\taddVariable(membraneVar);");
		  	}
	  	}else if (var instanceof FilamentVariable) { // contourVariable
	  		units = "molecules/um";
	  		FilamentVariable filamentVar = (FilamentVariable)var;
	  		out.println("\t// solving for all regions");
	  		out.println("\tnumSolveRegions = 0;  // flag specifying to solve for all regions");
	  		out.println("\tsolveRegions = NULL;");
	  		out.println("\tvarname = \"" + filamentVar.getName() + "\";");
	  		out.println("\tunits = \"" + units+ "\";");  		
	  		out.println("\tcontourVar = new ContourVariable(mesh->getNumMembraneElements(), varname, units);");
  			out.println("\todeSolver = new ODESolver(contourVar,mesh,numSolveRegions,solveRegions);");
  			out.println("\tbuilder = new ContourEqnBuilderForward(contourVar,mesh,odeSolver);");
  			out.println("\todeSolver->setEqnBuilder(builder);");
  			out.println("\taddSolver(odeSolver);");
	  		out.println("\taddVariable(contourVar);");
	  	}else if (var instanceof VolumeRegionVariable) { // volumeRegionVariable
	  		units = "uM";
	  		VolumeRegionVariable volumeRegionVar = (VolumeRegionVariable)var;
	  		out.println("\t// solving for all regions");
	  		out.println("\tnumSolveRegions = 0;  // flag specifying to solve for all regions");
	  		out.println("\tsolveRegions = NULL;");
	  		out.println("\tvarname = \"" + volumeRegionVar.getName() + "\";");
	  		out.println("\tunits = \"" + units+ "\";");		  		  		
	  		out.println("\tvolumeRegionVar = new VolumeRegionVariable(mesh->getNumVolumeRegions(), varname, units);");
  			out.println("\todeSolver = new ODESolver(volumeRegionVar,mesh,numSolveRegions,solveRegions);");
  			out.println("\tbuilder = new VolumeRegionEqnBuilder(volumeRegionVar,mesh,odeSolver);");
  			out.println("\todeSolver->setEqnBuilder(builder);");
  			out.println("\taddSolver(odeSolver);");
	  		out.println("\taddVariable(volumeRegionVar);");
	  	}else if (var instanceof MembraneRegionVariable) { // membraneRegionVariable
	  		units = "molecules/um^2";
	  		MembraneRegionVariable membraneRegionVar = (MembraneRegionVariable)var;
	  		out.println("\t// solving for all regions");
	  		out.println("\tnumSolveRegions = 0;  // flag specifying to solve for all regions");
	  		out.println("\tsolveRegions = NULL;");
	  		out.println("\tvarname = \"" + membraneRegionVar.getName() + "\";");
	  		out.println("\tunits = \"" + units+ "\";");		  		  		
	  		out.println("\tmembraneRegionVar = new MembraneRegionVariable(mesh->getNumMembraneRegions(), varname, units);");
  			out.println("\todeSolver = new ODESolver(membraneRegionVar,mesh,numSolveRegions,solveRegions);");
  			out.println("\tbuilder = new MembraneRegionEqnBuilder(membraneRegionVar,mesh,odeSolver);");
  			out.println("\todeSolver->setEqnBuilder(builder);");
  			out.println("\taddSolver(odeSolver);");
	  		out.println("\taddVariable(membraneRegionVar);");
	  	}	
	}		  	
	out.println("}");
}


/**
 * This method was created by a SmartGuide.
 * @param printWriter java.io.PrintWriter
 */
public void writeDeclaration(java.io.PrintWriter out) {
	out.println("//---------------------------------------------");
	out.println("//  class " + getClassName());
	out.println("//---------------------------------------------");

	out.println("class " + getClassName() + " : public " + getParentClassName());
	out.println("{");
	out.println(" public:");
	out.println("\t"+getClassName() + "(CartesianMesh *mesh);");
	out.println("};");
}


/**
 * This method was created by a SmartGuide.
 * @param out java.io.PrintWriter
 */
protected void writeGetSimTool(java.io.PrintWriter out) throws Exception {

	Simulation simulation = simulationJob.getWorkingSim();
	SolverTaskDescription taskDesc = simulation.getSolverTaskDescription();
	if (taskDesc==null){
		throw new Exception("task description not defined");
	}	

	out.println("");
	out.println("SimTool *getSimTool()");
	out.println("{");
	out.println("");
//	char fs = File.separatorChar;
//	String baseDataName = "SIMULATION" + fs + mathDesc.getName() + fs + "UserData" ;
	StringBuffer newBaseDataName = new StringBuffer();
	for (int i=0;i<baseDataName.length();i++){
		if (baseDataName.charAt(i) == '\\'){
			newBaseDataName.append(baseDataName.charAt(i));
			newBaseDataName.append(baseDataName.charAt(i));
		}else{
			newBaseDataName.append(baseDataName.charAt(i));
		}
	}
	out.println("\tchar tempString[1024];");
	out.println();
	
	out.println("\tSimTool::create();");	
	out.println("\tsprintf(tempString, \"%s%c" + simulationJob.getSimulationJobID() + "\\0\", outputPath, DIRECTORY_SEPARATOR);");
	out.println("\tSimTool::getInstance()->setBaseFilename(tempString);");	
	out.println("\tSimTool::getInstance()->setTimeStep("+taskDesc.getTimeStep().getDefaultTimeStep()+");");
	out.println("\tSimTool::getInstance()->setEndTimeSec("+taskDesc.getTimeBounds().getEndingTime()+");");
	if (taskDesc.getOutputTimeSpec().isDefault()){
		out.println("\tSimTool::getInstance()->setKeepEvery("+((DefaultOutputTimeSpec)taskDesc.getOutputTimeSpec()).getKeepEvery()+");");
	}else{
		throw new RuntimeException("unexpected OutputTime specification type :"+taskDesc.getOutputTimeSpec().getClass().getName());
	}
	if (simulation.getSolverTaskDescription().isStopAtSpatiallyUniform()) {
		out.println("\tSimTool::getInstance()->setCheckSpatiallyUniform();");
		out.println("\tSimTool::getInstance()->setSpatiallyUniformAbsErrorTolerance(" + taskDesc.getErrorTolerance().getAbsoluteErrorTolerance() + ");");
	}
	//out.println("\tSimTool::getInstance()->setStoreEnable(TRUE);");
	//out.println("\tSimTool::getInstance()->setFileCompress(FALSE);");
	out.println();
	
	out.println("\tVCellModel* model = new UserVCellModel();");
	out.println("\tSimTool::getInstance()->setModel(model);");
	out.println();
	
	out.println("\tSimulationMessaging::getInstVar()->setWorkerEvent(new WorkerEvent(JOB_STARTING, \"initializing mesh...\"));");
	out.println("\tsprintf(tempString, \"%s%c" + simulationJob.getSimulationJobID() + ".vcg\\0\", outputPath, DIRECTORY_SEPARATOR);");
	out.println("\tCartesianMesh* mesh = new CartesianMesh();");
	out.println("\tifstream ifs(tempString);");
	out.println("\tif (!ifs.is_open()){");
	out.println("\t\tstringstream ss;");
	out.println("\t\tss << \"Can't open geometry file '\" <<  tempString << \"'\";");
	out.println("\t\tthrow ss.str();");
	out.println("\t}");
	out.println("\tcout << \"Reading mesh from file '\" << tempString << \"'\" << endl;");
	out.println("\tmesh->initialize(ifs);");
	out.println("\tSimulationMessaging::getInstVar()->setWorkerEvent(new WorkerEvent(JOB_STARTING, \"mesh initialized\"));");
	out.println();
	
	out.println("\tSimulation* sim = new UserSimulation(mesh);");
	out.println("\tSimTool::getInstance()->setSimulation(sim);");
	out.println("\tsim->initSimulation();");	
	out.println("\tSimTool::getInstance()->loadFinal();   // initializes to the latest file if it exists");
	out.println("\treturn SimTool::getInstance();");
	out.println("}");
}


/**
 * This method was created by a SmartGuide.
 * @param printWriter java.io.PrintWriter
 */
public void writeImplementation(java.io.PrintWriter out) throws Exception {
	out.println("//---------------------------------------------");
	out.println("//  main routine");
	out.println("//---------------------------------------------");
	writeMain(out);
	out.println("");
	writeGetSimTool(out);
	out.println("");
	out.println("//---------------------------------------------");
	out.println("//  class " + getClassName());
	out.println("//---------------------------------------------");
	writeConstructor(out);
	out.println("");
}


/**
 * This method was created by a SmartGuide.
 * @param out java.io.PrintWriter
 */
protected void writeMain(java.io.PrintWriter out) throws Exception {

	Simulation simulation = simulationJob.getWorkingSim();
	FieldFunctionArguments[] fieldFuncArgs = simulation.getMathDescription().getFieldFunctionArguments();
	//FieldDataIdentifierSpec[] fieldDataIDSs = simulationJob.getFieldDataIdentifierSpecs();
	SolverTaskDescription taskDesc = simulation.getSolverTaskDescription();
	if (taskDesc==null){
		throw new Exception("task description not defined");
	}	
	
	out.println("#include <sys/stat.h>");
	out.println("#include <sstream>");
	out.println("using namespace std;");
	out.println();
		
	String parentPath = new File(baseDataName).getParent();
	StringBuffer newParentPath = new StringBuffer();
	for (int i = 0; i < parentPath.length(); i ++){
		if (baseDataName.charAt(i) == '\\'){
			newParentPath.append(baseDataName.charAt(i));
			newParentPath.append(baseDataName.charAt(i));
		}else{
			newParentPath.append(baseDataName.charAt(i));
		}
	}	
	out.println("static char* outputPath = \"" + newParentPath +"\";");
	out.println();

	out.println("#ifndef VCELL_CORBA");
	out.println("//-------------------------------------------");
	out.println("//   BATCH (NON-CORBA) IMPLEMENTATION");
	out.println("//-------------------------------------------");
	out.println("");
	out.println("#ifdef VCELL_MPI");
	out.println("#include <mpi.h>");
	out.println("#endif");
	out.println("");

	if (fieldFuncArgs != null && fieldFuncArgs.length > 0) {
		out.println();
		for (int i = 0; i < fieldFuncArgs.length; i ++) {
			out.println("FieldData* " + TokenMangler.getEscapedGlobalFieldVariableName_C(fieldFuncArgs[i]) + " = 0;");
		}
	}

	out.println("SimTool* getSimTool();");
	
	out.println("int vcellExit(int returnCode, char* returnMsg) {");
	out.println("\tif (!SimulationMessaging::getInstVar()->isStopRequested()) {");
	out.println("\t\tif (returnCode != 0) {");
	out.println("\t\t\tSimulationMessaging::getInstVar()->setWorkerEvent(new WorkerEvent(JOB_FAILURE, returnMsg));");
	out.println("\t\t}");
	out.println("#ifdef USE_MESSAGING");
	out.println("\t\tSimulationMessaging::getInstVar()->waitUntilFinished();");
	out.println("#endif");
	out.println("\t}");

	if (fieldFuncArgs != null && fieldFuncArgs.length > 0) {
		out.println();
		out.println("\tdelete SimulationMessaging::getInstVar();");	
		for (int i = 0; i < fieldFuncArgs.length; i ++) {
			out.println("\tdelete " +  TokenMangler.getEscapedGlobalFieldVariableName_C(fieldFuncArgs[i]) + ";");
		}
		out.println();
	}

	out.println("\treturn returnCode;");
	out.println("}");
	
	out.println("int main(int argc, char *argv[])");
	out.println("{");
		
	out.println("");
	out.println("#ifdef VCELL_MPI");
	out.println("\tint ierr = MPI_Init(&argc,&argv);");
	out.println("\tassert(ierr == MPI_SUCCESS);");
	out.println("#endif");
	out.println("");

	out.println("\tint returnCode = 0;");
	out.println("\tstring returnMsg;");
	// Fei Changes Begin
	out.println("\ttry {");
	out.println("\t\tint taskID = -1;");
	out.println("\t\tbool bSimZip = true;");
	out.println("\t\tfor (int i = 1; i < argc; i ++) {");
	out.println("\t\t\tif (!strcmp(argv[i], \"-nz\")) {");
	out.println("\t\t\t\tbSimZip = false;");
	out.println("\t\t\t} else if (!strcmp(argv[i], \"-d\")) {");
	out.println("\t\t\t\ti ++;");
	out.println("\t\t\t\toutputPath = argv[i];");
	out.println("\t\t\t} else if (!strcmp(argv[i], \"-tid\")) {");
	out.println("\t\t\t\ti ++;");
	out.println("\t\t\t\tif (i >= argc) {");
	out.println("\t\t\t\t\tcout << \"Missing taskID!\" << endl;");
	out.println("\t\t\t\t\texit(1);");
	out.println("\t\t\t\t}");
	out.println("\t\t\t\tfor (int j = 0; j < (int)strlen(argv[i]); j ++) {");
	out.println("\t\t\t\t\tif (argv[i][j] < '0' || argv[i][j] > '9') {");
	out.println("\t\t\t\t\t\tcout << \"Wrong argument : \" << argv[i] << \", taskID must be an integer!\" << endl;");
	out.println("\t\t\t\t\t\tcout << \"Arguments : [-d output] [-nz] [-tid taskID]\" <<  endl;");
	out.println("\t\t\t\t\t\texit(1);");
	out.println("\t\t\t\t\t}");
	out.println("\t\t\t\t}");
	out.println("\t\t\t\ttaskID = atoi(argv[i]);");
	out.println("\t\t\t}");	
	out.println("\t\t}");
	out.println("\t\tstruct stat buf;");	
	out.println("\t\tif (stat(outputPath, &buf)) {");
	out.println("\t\t\tcerr << \"Output directory [\" << outputPath <<\"] doesn't exist\" << endl;");
	out.println("\t\t\texit(1);");
	out.println("\t\t}");
	
	out.println("\t\tif (taskID == -1) { // no messaging");
	out.println("\t\t\tSimulationMessaging::create();");
	out.println("\t\t} else {");
	out.println("#ifdef USE_MESSAGING");
	out.println("\t\t\tchar* broker = \"" + JmsUtils.getJmsUrl() + "\";");
    out.println("\t\t\tchar *smqusername = \"" + JmsUtils.getJmsUserID() + "\";");
    out.println("\t\t\tchar *password = \"" + JmsUtils.getJmsPassword() + "\";");
    out.println("\t\t\tchar *qname = \"" + JmsUtils.getQueueWorkerEvent() + "\";");  
	out.println("\t\t\tchar* tname = \"" + JmsUtils.getTopicServiceControl() + "\";");
	out.println("\t\t\tchar* vcusername = \"" + simulation.getVersion().getOwner().getName() + "\";");
	out.println("\t\t\tjint simKey = " + simulation.getVersion().getVersionKey() + ";");
	out.println("\t\t\tjint jobIndex = " + simulationJob.getJobIndex() + ";");
	out.println("\t\t\tSimulationMessaging::create(broker, smqusername, password, qname, tname, vcusername, simKey, jobIndex, taskID);");
	out.println("#endif");
	out.println("\t\t}");
	out.println("#ifdef USE_MESSAGING");
	out.println("\t\tSimulationMessaging::getInstVar()->start(); // start the thread");
	out.println("#endif");

	if (fieldFuncArgs != null && fieldFuncArgs.length > 0) {
		out.println();
		out.println("\t\tchar tempString[1024];");
			
		for (int i = 0; i < fieldFuncArgs.length; i ++) {
			String fieldName = fieldFuncArgs[i].getFieldName();
			String varName = fieldFuncArgs[i].getVariableName();
			File fieldFile = new File(baseDataName + FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fieldFuncArgs[i]));
			String fieldDataID = "_VCell_FieldData_" + i;
			out.println("\t\tsprintf(tempString, \"%s%c" + fieldFile.getName() + "\\0\", outputPath, DIRECTORY_SEPARATOR);");
			String varType = "";
			if (fieldFuncArgs[i].getVariableType().equals(VariableType.VOLUME)) {
				varType = "VAR_VOLUME";
			} else if (fieldFuncArgs[i].getVariableType().equals(VariableType.MEMBRANE)) {
				varType = "VAR_MEMBRANE";
			} else {
				varType = "VAR_UNKNOWN";
			}
			String constructorArg = i + "," + varType + ",\"" + fieldDataID + "\",\"" + fieldName + "\",\"" + varName + "\"," + fieldFuncArgs[i].getTime().infix() + ", tempString";		
			out.println("\t\t" +  TokenMangler.getEscapedGlobalFieldVariableName_C(fieldFuncArgs[i]) + " = new FieldData(" + constructorArg + ");");
		}		
	}
	
	out.println();
	out.println("\t\tSimTool *pSimTool = getSimTool();");
	out.println("\t\tif (bSimZip == false) {");
	out.println("\t\t\tSimTool::getInstance()->requestNoZip();");
	out.println("\t\t}");	
	if (taskDesc.getTaskType() == SolverTaskDescription.TASK_UNSTEADY){
		out.println("\t\tpSimTool->start();");
	}else{
		out.println("\t\tpSimTool->startSteady("+taskDesc.getErrorTolerance().getAbsoluteErrorTolerance()+","+taskDesc.getTimeBounds().getEndingTime()+");");
	}		

	out.println("\t}catch (const char *exStr){");
	out.println("\t\treturnMsg = \"Exception while running : \";");
	out.println("\t\treturnMsg += exStr;");
	out.println("\t\treturnCode = 1;");
	out.println("\t}catch (string& exStr){");
	out.println("\t\treturnMsg = \"Exception while running : \";");
	out.println("\t\treturnMsg += exStr;");
	out.println("\t\treturnCode = 1;");
   	out.println("\t}catch (...){");
	out.println("\t\treturnMsg = \"Unknown exception while running ... \";");
	out.println("\t\treturnCode = 1;");
	out.println("\t}");

	out.println("#ifdef VCELL_MPI");
	out.println("\tMPI_Finalize();");
	out.println("#endif");

	out.println("\treturn vcellExit(returnCode, (char*)returnMsg.c_str());");
	out.println("}");
   	
	out.println("#else  // end not VCELL_CORBA");
	out.println("//-------------------------------------------");
	out.println("//   CORBA IMPLEMENTATION");
	out.println("//-------------------------------------------");
	out.println("#include <OB/CORBA.h>");
	out.println("#include <OB/Util.h>");
	out.println("");
	out.println("#include <Simulation_impl.h>");
	out.println("");
	out.println("#include <stdlib.h>");
	out.println("#include <errno.h>");
	out.println("");
	out.println("#ifdef HAVE_FSTREAM");
	out.println("#   include <fstream>");
	out.println("#else");
	out.println("#   include <fstream.h>");
	out.println("#endif");
	out.println("");
	out.println("int main(int argc, char* argv[], char*[])");
	out.println("{");
	out.println("\ttry {");
	out.println("\t\t//");
	out.println("\t\t// Create ORB and BOA");
	out.println("\t\t//");
	out.println("\t\tCORBA_ORB_var orb = CORBA_ORB_init(argc, argv);");
	out.println("\t\tCORBA_BOA_var boa = orb -> BOA_init(argc, argv);");
	out.println();
	out.println("\t\torb->conc_model(CORBA_ORB::ConcModelThreaded);");
	out.println("\t\tboa->conc_model(CORBA_BOA::ConcModelThreadPool);");
	out.println("\t\tboa->conc_model_thread_pool(4);");
	out.println();
	out.println("\t\t//");
	out.println("\t\t// Create implementation object");
	out.println("\t\t//");
	out.println("\t\tmathService_Simulation_var p = new Simulation_impl(getSimTool());");
	out.println();
	out.println("\t\t//");
	out.println("\t\t// Save reference");
	out.println("\t\t//");
	out.println("\t\tCORBA_String_var s = orb -> object_to_string(p);");
	out.println();
	out.println("\t\tconst char* refFile = \"Simulation.ref\";");
	out.println("\t\tofstream out(refFile);");
	out.println("\t\tif(out.fail()) {");
	out.println("\t\t\tcerr << argv[0] << \": can't open '\" << refFile << \"': \" << strerror(errno) << endl;");
	out.println("\t\t\treturn 1;");
	out.println("\t\t}");
	out.println();
	out.println("\t\tout << s << endl;");
	out.println("\t\tout.close();");
	out.println();
	out.println("\t\t//");
	out.println("\t\t// Run implementation");
	out.println("\t\t//");
	out.println("\t\tboa -> impl_is_ready(CORBA_ImplementationDef::_nil());");
	out.println("\t} catch(CORBA_SystemException& ex) {");
	out.println("\t\tOBPrintException(ex);");
	out.println("\t\treturn 1;");
	out.println("\t}");
	out.println("");
	out.println("\t return 0;");
	out.println("}");
	out.println();
	out.println("#endif // end VCELL_CORBA");
	out.println();
}
}