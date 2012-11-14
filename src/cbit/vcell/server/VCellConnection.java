/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;

import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
/**
 * This interface was generated by a SmartGuide.
 * 
 */
public interface VCellConnection extends Remote {
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 1:17:05 AM)
 * @return cbit.vcell.server.DataSetController
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
DataSetController getDataSetController() throws DataAccessException, RemoteException;
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param simulationInfo cbit.vcell.solver.SimulationInfo
 * @param simulation cbit.vcell.solver.Simulation
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationController getSimulationController() throws RemoteException;
/**
 * Insert the method's description here.
 * Creation date: (3/2/01 11:15:25 PM)
 * @return cbit.vcell.server.URLFinder
 * @exception java.rmi.RemoteException The exception description.
 */
URLFinder getURLFinder() throws RemoteException;
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public UserLoginInfo getUserLoginInfo() throws RemoteException;
/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public UserMetaDbServer getUserMetaDbServer() throws RemoteException, DataAccessException;
void sendErrorReport(Throwable exception) throws RemoteException;

MessageEvent[] getMessageEvents() throws RemoteException;
void reportPerformanceMonitorEvent(PerformanceMonitorEvent performanceMonitorEvent) throws RemoteException;
}
