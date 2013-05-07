package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.vcell.rest.common.SimulationTaskRepresentation;
import org.vcell.rest.common.SimulationTaskResource;

public class SimulationTaskServerResource extends WadlServerResource implements SimulationTaskResource {

	private String simid;
	
	
    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("simulationTask");
        return result;
    }

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        String simTaskIdAttribute = getAttribute("simTaskId");

        if (simTaskIdAttribute != null) {
            this.simid = simTaskIdAttribute;
            setName("Resource for simulation \"" + this.simid + "\"");
            setDescription("The resource describing the simulation task id \"" + this.simid + "\"");
        } else {
            setName("simulation task resource");
            setDescription("The resource describing a simulation task");
        }
    }
	

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo("user",false,"string",ParameterStyle.TEMPLATE,"VCell user id"));
        parameterInfos.add(new ParameterInfo("simTaskID",false,"string",ParameterStyle.TEMPLATE,"VCell simulation task id simkey_jobid"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	@Override
	public SimulationTaskRepresentation get_json() {
		SimulationTaskRepresentation sim = new SimulationTaskRepresentation();
		sim.simKey="123";
		return sim;
	}

	@Override
	public void put_json(SimulationTaskRepresentation simulation) {
		// TODO Auto-generated method stub

	}

}
