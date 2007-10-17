package cbit.vcell.messaging.server;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.DataAccessException;
import static cbit.vcell.messaging.MessageConstants.*;
import cbit.vcell.messaging.JmsUtils;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimDataServer extends JmsRpcServer {
	private RpcDataServerImpl rpcDataServerImpl = null;
	private static String filter = "(" + MESSAGE_TYPE_PROPERTY + "='" + MESSAGE_TYPE_RPC_SERVICE_VALUE  + "') AND (" 
		+ SERVICE_TYPE_PROPERTY + "='" + SERVICETYPE_DATA_VALUE + "')";	

/**
 * Scheduler constructor comment.
 */
public SimDataServer(int serviceOrdinal, boolean bExportOnly, String logdir) throws Exception {
	super(bExportOnly ? SERVICETYPE_DATAEXPORT_VALUE : SERVICETYPE_DATA_VALUE, serviceOrdinal, JmsUtils.getQueueSimDataReq(), 
		filter + " AND (" + SERVICETYPE_DATAEXPORT_VALUE + (bExportOnly ? " is NOT NULL)" : " is NULL)"), logdir);	
}


/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 9:34:57 AM)
 */
public RpcServerImpl getRpcServerImpl() throws DataAccessException {
	if (rpcDataServerImpl == null) {
		try {
			rpcDataServerImpl = new RpcDataServerImpl(rpcServerMessaging, log);			
		} catch (java.io.FileNotFoundException ex) {
			log.exception(ex);
			throw new cbit.vcell.server.DataAccessException(ex.getMessage());
		}
	}

	return rpcDataServerImpl;
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + SimDataServer.class.getName() + " serviceOrdinal [EXPORTONLY] [logdir]");
		System.exit(1);
	}
	
	try {
		PropertyLoader.loadProperties();
		
		int serviceOrdinal = Integer.parseInt(args[0]);		
		String logdir = null;
		boolean bExportOnly = false;		
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("EXPORTONLY")) {
				bExportOnly = true;
				if (args.length > 2) {	
					logdir = args[2];
				}
			} else {
				logdir = args[1];
			}
		}
		
        SimDataServer simDataServer = new SimDataServer(serviceOrdinal, bExportOnly, logdir);
        simDataServer.start();
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
}
}