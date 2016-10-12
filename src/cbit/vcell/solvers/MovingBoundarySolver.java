/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.io.File;
import java.io.PrintWriter;

import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;

/**
 * This interface was generated by a SmartGuide.
 *
 */
public class MovingBoundarySolver extends SimpleCompiledSolver {
//	private SimResampleInfoProvider simResampleInfoProvider;
	private Geometry resampledGeometry = null;
	private final String inputFilename;
	public final static String MOVING_BOUNDARY_FILE_END = "mb.h5";

//	public static final int HESM_KEEP_AND_CONTINUE = 0;
//	public static final int HESM_THROW_EXCEPTION = 1;
//	public static final int HESM_OVERWRITE_AND_CONTINUE = 2;


/**
 * This method was created by a SmartGuide.
 * @param mathDesc cbit.vcell.math.MathDescription
 * @param platform cbit.vcell.solvers.Platform
 * @param directory java.lang.String
 * @param simID java.lang.String
 * @param clientProxy cbit.vcell.solvers.ClientProxy
 */
public MovingBoundarySolver (SimulationTask simTask, File dir, SessionLog sessionLog, boolean bMsging) throws SolverException {
	super(simTask, dir, sessionLog, bMsging);
	if (! simTask.getSimulation().isSpatial()) {
		throw new SolverException("Cannot use MovingBoundary on non-spatial simulation");
	}
//	this.simResampleInfoProvider = (VCSimulationDataIdentifier)simTask.getSimulationJob().getVCDataIdentifier();
	inputFilename = getBaseName() + "mb.xml";
}

@Override
protected String[] getMathExecutableCommand() {
	String exeName = PropertyLoader.getRequiredProperty(PropertyLoader.MOVING_BOUNDARY_EXE);
	return new String[] {exeName,"--config",inputFilename};
}


/**
 * Insert the method's description here.
 * Creation date: (12/9/2002 4:53:30 PM)
 */
public void cleanup() {
	// nothing special needed
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/2004 5:31:41 PM)
 * @return cbit.vcell.simdata.AnnotatedFunction[]
 * @param simulation cbit.vcell.solver.Simulation
 */
//@Override
//public Vector<AnnotatedFunction> createFunctionList() {
//	//Try to save existing user defined functions
//	Vector<AnnotatedFunction> annotatedFunctionVector = new Vector<AnnotatedFunction>();
//	try{
//		annotatedFunctionVector = simTask.getSimulationJob().getSimulationSymbolTable().createAnnotatedFunctionsList(simTask.getSimulation().getMathDescription());
//		String functionFileName = getBaseName() + FUNCTIONFILE_EXTENSION;
//		File existingFunctionFile = new File(functionFileName);
//		if(existingFunctionFile.exists()){
//			Vector<AnnotatedFunction> oldUserDefinedFunctions =
//				new Vector<AnnotatedFunction>();
//			Vector<AnnotatedFunction> allOldFunctionV =
//				FunctionFileGenerator.readFunctionsFile(existingFunctionFile, simTask.getSimulationJob().getSimulationJobID());
//			for(int i = 0; i < allOldFunctionV.size(); i += 1){
//				if(allOldFunctionV.elementAt(i).isOldUserDefined()){
//					oldUserDefinedFunctions.add(allOldFunctionV.elementAt(i));
//				}
//			}
//
//			annotatedFunctionVector.addAll(oldUserDefinedFunctions);
//		}
//	}catch(Exception e){
//		e.printStackTrace();
//		//ignore
//	}
//	return annotatedFunctionVector;
//}
/**
 * Insert the method's description here.
 * Creation date: (6/27/01 3:25:11 PM)
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
protected ApplicationMessage getApplicationMessage(String message) {
	//
	// "data:iteration:time"  .... sent every time data written for FVSolver
	// "progress:xx.x%"        .... sent every 1% for FVSolver
	//
	//
	if (message.startsWith(DATA_PREFIX)){
		double timepoint = Double.parseDouble(message.substring(message.lastIndexOf(SEPARATOR)+1));
		setCurrentTime(timepoint);
		return new ApplicationMessage(ApplicationMessage.DATA_MESSAGE,getProgress(),timepoint,null,message);
	}else if (message.startsWith(PROGRESS_PREFIX)){
		String progressString = message.substring(message.lastIndexOf(SEPARATOR)+1,message.indexOf("%"));
		double progress = Double.parseDouble(progressString)/100.0;
		double startTime = simTask.getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
		double endTime = simTask.getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
		setCurrentTime(startTime + (endTime-startTime)*progress);
		return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE,progress,-1,null,message);
	}else{
		throw new RuntimeException("unrecognized message");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 8:47:08 AM)
 * @return java.lang.String
 */
public static String getDescription() {
	return "Finite Volume, Structured Grid";
}


/**
 * This method was created by a SmartGuide.
 */
protected void initialize() throws SolverException {
	writeFunctionsFile();

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_INIT));
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,SimulationMessage.MESSAGE_SOLVER_RUNNING_START));

	try (PrintWriter pw = new PrintWriter(inputFilename)) {
		MovingBoundaryFileWriter mbfw = new MovingBoundaryFileWriter(pw, simTask, resampledGeometry, bMessaging, getBaseName()) ;
		mbfw.write();
	} catch (Exception e) {
		throw new SolverException("Can't open input file "+ inputFilename, e);
	}

	setMathExecutable(new MathExecutable(getMathExecutableCommand(),getSaveDirectory()));

}

/*
@Override
protected String[] getMathExecutableCommand() {
	String exeSuffix = System.getProperty(PropertyLoader.exesuffixProperty); // ".exe";
	String baseName = "MovingBoundary" ;
	File exeFile = new File(getSaveDirectory(), baseName + exeSuffix);
	return new String[] { exeFile.getAbsolutePath() };
}
*/



public Geometry getResampledGeometry() throws SolverException {
	if (resampledGeometry == null) {
		// clone and resample geometry
		try {
			resampledGeometry = (Geometry) BeanUtils.cloneSerializable(simTask.getSimulation().getMathDescription().getGeometry());
			GeometrySurfaceDescription geoSurfaceDesc = resampledGeometry.getGeometrySurfaceDescription();
			ISize newSize = simTask.getSimulation().getMeshSpecification().getSamplingSize();
			geoSurfaceDesc.setVolumeSampleSize(newSize);
			geoSurfaceDesc.updateAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SolverException(e.getMessage());
		}
	}
	return resampledGeometry;
}

/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:33:03 PM)
 */
public void propertyChange(java.beans.PropertyChangeEvent event) {
	super.propertyChange(event);

	if (event.getSource() == getMathExecutable() && event.getPropertyName().equals("applicationMessage")) {
		String messageString = (String)event.getNewValue();
		if (messageString==null || messageString.length()==0){
			return;
		}
		ApplicationMessage appMessage = getApplicationMessage(messageString);
		if (appMessage!=null && appMessage.getMessageType() == ApplicationMessage.DATA_MESSAGE) {
			fireSolverPrinted(appMessage.getTimepoint());
		}
	}
}
}
