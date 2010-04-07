package cbit.vcell.client.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.client.data.OutputContext;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 5:34:33 AM)
 * @author: Ion Moraru
 */
public class ODEDataManager implements DataManager {
	private VCDataManager vcDataManager = null;
	private VCDataIdentifier vcDataIdentifier = null;
	private ODESolverResultSet odeSolverResultSet = null;
	private OutputContext outputContext = null;

private OutputContext getOutputContext() {
		return outputContext;
	}


public void setOutputContext(OutputContext newOutputContext) {
	if (getOutputContext() != null && odeSolverResultSet != null) {
		// remove old output functions, if any
		for (int j = 0; j < getOutputContext().getOutputFunctions().length; j++) {
			for (int i=0;i<odeSolverResultSet.getColumnDescriptionsCount();i++) {
				ColumnDescription colDesc = odeSolverResultSet.getColumnDescriptions(i);
				if (colDesc instanceof FunctionColumnDescription){
					FunctionColumnDescription funcColDesc = (FunctionColumnDescription)colDesc;
					if ( getOutputContext().getOutputFunctions()[j].getName().equals(funcColDesc.getName()) ) {
						try {
							odeSolverResultSet.removeFunctionColumn(funcColDesc);
						} catch (ExpressionException e) {
							e.printStackTrace(System.out);
							throw new RuntimeException("Cannot remove function column from result set."+e.getMessage());
						}
					}
				}
			}
		}
	}
	// add new output functions, if any
	if (newOutputContext != null) {
		for (int i = 0; i < newOutputContext.getOutputFunctions().length; i++) {
			addOutputFunction(newOutputContext.getOutputFunctions()[i], odeSolverResultSet);
		} 
	}
	this.outputContext = newOutputContext;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:46:51 PM)
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 * @throws DataAccessException 
 */
public ODEDataManager(OutputContext outputContext, VCDataManager vcDataManager, VCDataIdentifier vcDataIdentifier) throws DataAccessException {
	setVcDataManager(vcDataManager);
	setVcDataIdentifier(vcDataIdentifier);
	connect();
	setOutputContext(outputContext);
}


/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 * 
 * @param simulationInfo simulation database reference
 * 
 * @returns array of availlable data names.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public DataIdentifier[] getDataIdentifiers() throws DataAccessException {
	return getVCDataManager().getDataIdentifiers(getOutputContext(),getVCDataIdentifier());
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 * 
 * @returns double array of times of availlable data, or null if no data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public double[] getDataSetTimes() throws DataAccessException {
	return getVCDataManager().getDataSetTimes(getVCDataIdentifier());
}

/**
 * gets list of named Functions defined for the resultSet for this Simulation.
 * 
 * @returns array of functions, or null if no functions.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * 
 * @see Function
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions() throws org.vcell.util.DataAccessException {
	return getVCDataManager().getFunctions(outputContext, getVCDataIdentifier());
}


/**
 * retrieves the non-spatial (ODE) results for this Simulation.  This is assumed not to change over the life
 * of the simulation
 * 
 * @returns non-spatial (ODE) data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public ODESolverResultSet getODESolverResultSet() throws DataAccessException {	
	return odeSolverResultSet;
}

/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
public VCDataIdentifier getVCDataIdentifier() {
	return vcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
 * @return cbit.vcell.client.server.VCDataManager
 */
public VCDataManager getVCDataManager() {
	return vcDataManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
 * @param newVcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
private void setVcDataIdentifier(VCDataIdentifier newVcDataIdentifier) {
	vcDataIdentifier = newVcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
 * @param newVcDataManager cbit.vcell.client.server.VCDataManager
 */
private void setVcDataManager(VCDataManager newVcDataManager) {
	vcDataManager = newVcDataManager;
}

private void connect() throws DataAccessException {
	odeSolverResultSet = getVCDataManager().getODEData(getVCDataIdentifier());
}

private void addOutputFunction(AnnotatedFunction function, ODESolverResultSet odeRS) {
// Get the new name and expression for the function and create a new
// functioncolumndescription, check is function is valid. If it is, add it to the list of columns 
// in the ODEResultSet. Else, pop-up an error dialog indicating that function cannot be added.
	FunctionColumnDescription fcd = null;
	String funcName = function.getName();
	Expression funcExp = function.getExpression();
	fcd = new FunctionColumnDescription(funcExp, funcName, null, funcName+" : "+funcExp.infix(), true);

	try {
		odeRS.checkFunctionValidity(fcd);
	} catch (ExpressionException e) {
		javax.swing.JOptionPane.showMessageDialog(null, e.getMessage()+". "+funcName+" not added.", "Error Adding Function ", javax.swing.JOptionPane.ERROR_MESSAGE);
		// Commenting the Stack trace for exception .... annoying to have the exception thrown after dealing with pop-up error message!
		// e.printStackTrace(System.out);
		return;
	}
	try {
		odeRS.addFunctionColumn(fcd);
	} catch (ExpressionException e) {
		javax.swing.JOptionPane.showMessageDialog(null, e.getMessage()+". "+funcName+" not added.", "Error Adding Function ", javax.swing.JOptionPane.ERROR_MESSAGE);
		e.printStackTrace(System.out);
	}
}
}
