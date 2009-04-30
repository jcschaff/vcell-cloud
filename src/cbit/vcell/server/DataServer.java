package cbit.vcell.server;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import org.vcell.util.document.User;

import cbit.vcell.math.*;
import cbit.plot.*;
import cbit.vcell.solvers.*;
import cbit.vcell.simdata.*;
import cbit.vcell.simdata.gui.SpatialSelection;
import cbit.vcell.export.server.*;
import cbit.vcell.solver.*;
/**
 * This interface was generated by a SmartGuide.
 * 
 */
public interface DataServer {
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 6:21:10 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
void addFunction(User user, SimulationInfo simInfo, cbit.vcell.math.Function function) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 6:21:10 PM)
 * @param function cbit.vcell.math.Function[]
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
void addFunctions(User user, SimulationInfo simInfo, cbit.vcell.math.Function function[]) throws DataAccessException;
/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public DataIdentifier[] getDataIdentifiers(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public double[] getDataSetTimes(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:45:30 PM)
 * @return cbit.vcell.export.server.ExportLog
 * @param simInfo cbit.vcell.solver.SimulationInfo
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
ExportLog getExportLog(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 6:21:10 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
 Function[] getFunctions(User user, SimulationInfo simInfo) throws DataAccessException; 
/**
 * Insert the method's description here.
 * Creation date: (1/16/00 11:38:06 PM)
 * @return boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
boolean getIsODEData(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param variable java.lang.String
 * @param time double
 * @param spatialSelection cbit.vcell.simdata.gui.SpatialSelection
 * @exception java.rmi.RemoteException The exception description.
 */
public PlotData getLineScan(User user, SimulationInfo simInfo, String variable, double time, SpatialSelection spatialSelection) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
CartesianMesh getMesh(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (1/13/00 6:21:10 PM)
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.solver.ode.ODESimData getODEData(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return ParticleData
 * @param time double
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(User user, SimulationInfo simInfo, double time) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists(User user, SimulationInfo simInfo) throws DataAccessException;
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.SimDataBlock getSimDataBlock(User user, SimulationInfo simInfo, String varName, double time) throws DataAccessException;
/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param variable java.lang.String
 * @param time double
 * @param begin cbit.vcell.math.CoordinateIndex
 * @param end cbit.vcell.math.CoordinateIndex
 * @exception java.rmi.RemoteException The exception description.
 */
public PlotData getSimpleLineScan(User user, SimulationInfo simInfo, String variable, double time, CoordinateIndex begin, CoordinateIndex end) throws DataAccessException;
/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param x int
 * @param y int
 * @param z int
 * @exception java.rmi.RemoteException The exception description.
 */
public double[][] getTimeSeriesValues(User user, SimulationInfo simInfo, String varName, int[] indices) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 6:21:10 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
void removeFunction(User user, SimulationInfo simInfo, cbit.vcell.math.Function function) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 2:53:44 PM)
 * @return cbit.rmi.event.ExportEvent
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.export.ExportStatus startExport(User user, ExportSpecs exportSpecs) throws DataAccessException;
}
