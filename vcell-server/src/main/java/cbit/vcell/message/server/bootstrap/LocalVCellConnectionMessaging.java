/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import java.io.FileNotFoundException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.SimpleMessageService;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.ErrorUtils;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.PerformanceMonitoringFacility;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellConnection;

/**
 * The user's connection to the Virtual Cell.  It is obtained from the VCellServer
 * after the user has been authenticated.
 * Creation date: (Unknown)
 * @author: Jim Schaff.
 */
@SuppressWarnings("serial")
public class LocalVCellConnectionMessaging implements VCellConnection, ExportListener ,DataJobListener{
	private long MAX_TIME_WITHOUT_POLLING_MS = PropertyLoader.getLongProperty(PropertyLoader.vcellClientTimeoutMS, 10*MessageConstants.MINUTE_IN_MS);
	private LocalDataSetControllerMessaging dataSetControllerMessaging = null;
	private LocalSimulationControllerMessaging simulationControllerMessaging = null;
	private LocalUserMetaDbServerMessaging userMetaDbServerMessaging = null;
	
	private SimpleMessageService messageService = null;

	private VCMessagingService vcMessagingService = null;
	private VCMessageSession vcMessageSessionData = null;
	private VCMessageSession vcMessageSessionSim = null;
	private VCMessageSession vcMessageSessionDb = null;
	
	private boolean bClosed = false;
	
	private UserLoginInfo userLoginInfo;
	
	private SessionLog fieldSessionLog = null;

	private PerformanceMonitoringFacility performanceMonitoringFacility;

	private ClientTopicMessageCollector clientMessageCollector = null;
	
	public LocalVCellConnectionMessaging(UserLoginInfo userLoginInfo, 
		SessionLog sessionLog, VCMessagingService vcMessagingService, ClientTopicMessageCollector clientMessageCollector) 
		throws FileNotFoundException {
		
		this.userLoginInfo = userLoginInfo;
		this.fieldSessionLog = sessionLog;
		this.vcMessagingService = vcMessagingService;
		
		this.clientMessageCollector = clientMessageCollector;
		messageService = new SimpleMessageService(userLoginInfo.getUser());
		this.clientMessageCollector.addMessageListener(messageService);
		
		sessionLog.print("new LocalVCellConnectionMessaging(" + userLoginInfo.getUser().getName() + ")");	
		
		performanceMonitoringFacility = new PerformanceMonitoringFacility(userLoginInfo.getUser()	);	
	}	

	
/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 10:42:29 AM)
 */
public void close() {
	
	bClosed = true;
	
	clientMessageCollector.removeMessageListener(messageService);

	if (vcMessageSessionData!=null){
		vcMessageSessionData.close();
	}
	if (vcMessageSessionDb!=null){
		vcMessageSessionDb.close();
	}
	if (vcMessageSessionSim!=null){
		vcMessageSessionSim.close();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 3:32:25 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
	
	// if it's from one of our jobs, pass it along so it will reach the client
	if (getUserLoginInfo().getUser().equals(event.getUser())) {
		messageService.messageEvent(event);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 2:59:05 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent event) {
	// if it's from one of our jobs, pass it along so it will reach the client
	if (getUserLoginInfo().getUser().equals(event.getUser())) {
		messageService.messageEvent(event);
	}
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
public DataSetController getDataSetController() throws DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getDataSetController()");
	if (dataSetControllerMessaging == null) {
		vcMessageSessionData = vcMessagingService.createProducerSession();
		dataSetControllerMessaging = new LocalDataSetControllerMessaging(getUserLoginInfo(), vcMessageSessionData, fieldSessionLog);
	}
	return dataSetControllerMessaging;
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 4:34:02 PM)
 * @return cbit.vcell.messaging.event.SimpleMessageServiceMessaging
 */
SimpleMessageService getMessageService() {
	return messageService;
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.solvers.MathController
 * @param mathDesc cbit.vcell.math.MathDescription
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationController getSimulationController() {
	if (simulationControllerMessaging == null){
		vcMessageSessionSim = vcMessagingService.createProducerSession();
		simulationControllerMessaging = new LocalSimulationControllerMessaging(getUserLoginInfo(), vcMessageSessionSim, fieldSessionLog);
	}
	return simulationControllerMessaging;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @
 */
public UserLoginInfo getUserLoginInfo() {
	return userLoginInfo;
}


/**
 * This method was created by a SmartGuide.
 * @return DBManager
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public UserMetaDbServer getUserMetaDbServer() throws DataAccessException {
	fieldSessionLog.print("LocalVCellConnectionMessaging.getUserMetaDbServer(" + getUserLoginInfo().getUser() + ")");
	if (userMetaDbServerMessaging == null) {
		vcMessageSessionDb = vcMessagingService.createProducerSession();
		userMetaDbServerMessaging = new LocalUserMetaDbServerMessaging(getUserLoginInfo(), vcMessageSessionDb, fieldSessionLog);
	}
	return userMetaDbServerMessaging;
}

/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 11:22:31 AM)
 */
public boolean isTimeout() throws java.rmi.RemoteException {
//	System.out.println("messageService.timeSinceLastPoll()="+messageService.timeSinceLastPoll()+" ms");
//	return messageService.timeSinceLastPoll() > 10000;//MAX_TIME_WITHOUT_POLLING_MS;
	return messageService.timeSinceLastPoll() > MAX_TIME_WITHOUT_POLLING_MS;
}

/**
 * @inheritDoc
 */
@Override
public void sendErrorReport(Throwable exception) {
	VCMongoMessage.sendClientException(exception, getUserLoginInfo());
	ErrorUtils.sendErrorReport(exception, null);
}

@Override
public void sendErrorReport(Throwable exception, ExtraContext extra) {
	VCMongoMessage.sendClientException(exception, getUserLoginInfo());
	ErrorUtils.sendErrorReport(exception,(extra == null?null:extra.toString()));
	
}

public MessageEvent[] getMessageEvents() {
	MessageEvent[] messageEvents = messageService.getMessageEvents();
	VCMongoMessage.sendClientMessageEventsDelivered(messageEvents, getUserLoginInfo());
	return messageEvents;
}


public void reportPerformanceMonitorEvent(PerformanceMonitorEvent performanceMonitorEvent) {
	performanceMonitoringFacility.performanceMonitorEvent(performanceMonitorEvent);
}

}
