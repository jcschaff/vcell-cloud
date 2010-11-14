package cbit.vcell.microscopy;

import org.vcell.optimization.OptSolverResultSet;

import cbit.function.DefaultScalarFunction;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.opt.ImplicitObjectiveFunction;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

public class FRAPOptimization {
	
	static double FTOL = 1.0e-6;
	public static double epsilon = 1e-8;
	static double penalty = 1E4;
	public static double largeNumber = 1E8; 
		
	//This function generates average intensity under different ROIs according to each time points for EXPERIMENTAL data.
	//the results returns double[roi length][time points with prebleach time points removed]. 
	public static double[][] dataReduction(FRAPData argFrapData,int argStartRecoveryIndex, ROI[] expRois, double[] normFactor) 
	{ 
		int roiLen = expRois.length;
		int numRefTimePoints = argFrapData.getImageDataset().getSizeT();
		// data set which is normalized with prebleach time points
		double[][] baseData = new double[roiLen][numRefTimePoints];
        // data set which is normalized and removed time points in prebleach
		double[][] newData = null;
		double[] avgBkIntensity = argFrapData.getAvgBackGroundIntensity();
		
		newData = new double[roiLen][numRefTimePoints-argStartRecoveryIndex];
		
		for(int i = 0; i < roiLen; i++)
		{
			baseData[i] = FRAPDataAnalysis.getAverageROIIntensity(argFrapData, expRois[i], normFactor, avgBkIntensity);
			//remove prebleach
			for(int j = 0; j < numRefTimePoints - argStartRecoveryIndex; j++)
			{
				newData[i][j]  = baseData[i][j+argStartRecoveryIndex];
			}
		}
		return newData;
	}

	//This function generates average intensity under different ROIs according to each time points for REFERENCE data.
	//the results returns double[roi length][time points].
	public static double[][] dataReduction(
			VCDataManager vcDataManager,VCSimulationDataIdentifier vcSimdataID, double[] rawSimTimePoints,
			ROI[] expRois, ClientTaskStatusSupport progressListener, boolean isRefSim) throws Exception{ 

		if(progressListener != null){
			progressListener.setMessage("Reading data, generating ROI averages");
		}
		int roiLen = expRois.length;
		double[] simTimes = rawSimTimePoints;
		double[][] newData = new double[roiLen][simTimes.length];
		double[] simData = null;
		if(isRefSim)
		{
			for (int j = 0; j < simTimes.length; j++) {
				simData = vcDataManager.getSimDataBlock(null,vcSimdataID, FRAPStudy.SPECIES_NAME_PREFIX_MOBILE,simTimes[j]).getData();
				for(int i = 0; i < roiLen; i++){
					newData[i][j] = AnnotatedImageDataset.getAverageUnderROI(simData, expRois[i].getPixelsXYZ(), null,0.0);
				}
				if(progressListener != null){
					progressListener.setProgress((int)(((double)(j+1))/((double)simTimes.length) *100));
				}
			}
		}
		else
		{
			for (int j = 0; j < simTimes.length; j++) {
				simData = vcDataManager.getSimDataBlock(null,vcSimdataID, FRAPStudy.SPECIES_NAME_PREFIX_COMBINED,simTimes[j]).getData();
				for(int i = 0; i < roiLen; i++){
					newData[i][j] = AnnotatedImageDataset.getAverageUnderROI(simData, expRois[i].getPixelsXYZ(), null,0.0);
				}
				if(progressListener != null){
					progressListener.setProgress((int)(((double)(j+1))/((double)simTimes.length) *100));
				}
			}
		}
		return newData;
	}
	
	 //remove prebleach time points
	public static double[] timeReduction(double[] argTimeStamps, int argStartRecoveryIndex )
	{
		double[] newTimeStamps = new double[argTimeStamps.length - argStartRecoveryIndex];
		for(int i = 0; i < (argTimeStamps.length - argStartRecoveryIndex); i++)
		{
			newTimeStamps[i] = argTimeStamps[i+argStartRecoveryIndex]-argTimeStamps[argStartRecoveryIndex];
		}
		return newTimeStamps;
	}
	
	public static double getValueFromParameters_oneDiffRate(double diffData, double mobileFrac, double bleachWhileMonitoringRate, double  firstPostBleach, double timePoint)
	{
		double imMobileFrac = 1 - mobileFrac;
		double result = (mobileFrac * diffData + imMobileFrac * firstPostBleach) * Math.exp(-(bleachWhileMonitoringRate * timePoint));
		
		return result;
	}
	
	public static double getValueFromParameters_twoDiffRates(double mFracFast, double fastData, double mFracSlow, double slowData, double bleachWhileMonitoringRate, double  firstPostBleach, double timePoint)
	{
		double immobileFrac = 1 - mFracFast - mFracSlow;
		double result = (mFracFast * fastData + mFracSlow * slowData + immobileFrac * firstPostBleach) * Math.exp(-(bleachWhileMonitoringRate * timePoint));
		
		return result;
	}
	
	public static double getErrorByNewParameters_oneDiffRate(double refDiffRate, double[] newParams, double[][] refData,
			                                                 double[][] expData, double[] refTimePoints, double[] expTimePoints, 
			                                                 int roiLen, boolean[] errorOfInterest, double[][] measurementErrors,
			                                                 Parameter fixedParam, boolean bApplyMeasurementError) throws Exception
	{
		double error = 0;
		// trying 3 parameters
		double diffRate = 0;
		double mobileFrac = 1;
		double bleachWhileMonitoringRate = 0;
		
		double[][] diffData = null;
		
		if(newParams != null && newParams.length > 0)
		{
			if(fixedParam == null)
			{
				diffRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
				mobileFrac = newParams[FRAPModel.INDEX_PRIMARY_FRACTION];
				bleachWhileMonitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
			}
			else // there is a fixedParameter
			{
				if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
				{
					diffRate = fixedParam.getInitialGuess();
					mobileFrac = newParams[FRAPModel.INDEX_PRIMARY_FRACTION - 1];
					bleachWhileMonitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE - 1];
				}
				else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
				{
					diffRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
					mobileFrac = fixedParam.getInitialGuess();
					bleachWhileMonitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE - 1];
				}
				else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
				{
					diffRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
					mobileFrac = newParams[FRAPModel.INDEX_PRIMARY_FRACTION];
					bleachWhileMonitoringRate = fixedParam.getInitialGuess(); 
				} 
			}
						
			diffData = FRAPOptimization.getValueByDiffRate(refDiffRate,
                    diffRate,
                    refData,
                    refTimePoints,
                    expTimePoints,
                    roiLen);
			//get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			if(diffData != null)
			{
				for(int i = 0; i < roiLen; i++)
				{
					firstPostBleach[i] = diffData[i][0];
				}
			}
			//compute error against exp data
			if(errorOfInterest != null)
			{
				for(int i=0; i<roiLen; i++)
				{
					if(errorOfInterest[i])
					{
						for(int j=0; j<expTimePoints.length; j++)
						{
							double difference = expData[i][j] - FRAPOptimization.getValueFromParameters_oneDiffRate(diffData[i][j], mobileFrac, bleachWhileMonitoringRate, firstPostBleach[i], expTimePoints[j]);
							if(bApplyMeasurementError)
							{
								difference = difference/measurementErrors[i][j];
							}
//							double difference = expData[i][j]- (mobileFrac * diffData[i][j] + imMobileFrac * firstPostBleach[i]) * Math.exp(-(bleachWhileMonitoringRate*expTimePoints[j]));
							error = error + difference * difference;
						}
					}
				}
			}
			return error;
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}
	}
	
	public static double getErrorByNewParameters_twoDiffRates(double refDiffRate, double[] newParams, double[][] refData, 
			                                                  double[][] expData, double[] refTimePoints, double[] expTimePoints,
			                                                  int roiLen, boolean[] errorOfInterest, double[][] measurementErrors, 
			                                                  Parameter fixedParam, boolean bApplyMeasurementError) throws Exception
	{
		double error = 0;
		// trying 5 parameters
		double diffFastRate = 0;
		double mFracFast = 1;
		double monitoringRate = 0;
		double diffSlowRate = 0;
		double mFracSlow = 1;
		
		double[][] fastData = null;
		double[][] slowData = null;
		if(newParams != null && newParams.length > 0)
		{
			if(fixedParam == null)
			{
				diffFastRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
				mFracFast = newParams[FRAPModel.INDEX_PRIMARY_FRACTION];
				monitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
				diffSlowRate = newParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE];
				mFracSlow = newParams[FRAPModel.INDEX_SECONDARY_FRACTION];
			}
			else // there is a fixedParameter
			{
				if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
				{
					diffFastRate = fixedParam.getInitialGuess();
					mFracFast = newParams[FRAPModel.INDEX_PRIMARY_FRACTION - 1];
					monitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE - 1];
					diffSlowRate = newParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE - 1];
					mFracSlow = newParams[FRAPModel.INDEX_SECONDARY_FRACTION - 1];
				}
				else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
				{
					diffFastRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
					mFracFast = fixedParam.getInitialGuess();
					monitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE - 1];
					diffSlowRate = newParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE - 1];
					mFracSlow = newParams[FRAPModel.INDEX_SECONDARY_FRACTION - 1];
				}
				else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
				{
					diffFastRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
					mFracFast = newParams[FRAPModel.INDEX_PRIMARY_FRACTION];
					monitoringRate = fixedParam.getInitialGuess(); 
					diffSlowRate = newParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE - 1];
					mFracSlow = newParams[FRAPModel.INDEX_SECONDARY_FRACTION - 1];
				} 
				else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE]))
				{
					diffFastRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
					mFracFast = newParams[FRAPModel.INDEX_PRIMARY_FRACTION];
					monitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
					diffSlowRate = fixedParam.getInitialGuess();
					mFracSlow = newParams[FRAPModel.INDEX_SECONDARY_FRACTION - 1];
				}
				else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION]))
				{
					diffFastRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
					mFracFast = newParams[FRAPModel.INDEX_PRIMARY_FRACTION];
					monitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
					diffSlowRate = newParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE];
					mFracSlow = fixedParam.getInitialGuess();
				}
			}
			
			fastData = FRAPOptimization.getValueByDiffRate(refDiffRate,
                    diffFastRate,
                    refData,
                    refTimePoints,
                    expTimePoints,
                    roiLen);
			
			slowData = FRAPOptimization.getValueByDiffRate(refDiffRate,
                    diffSlowRate,
                    refData,
                    refTimePoints,
                    expTimePoints,
                    roiLen);
			
			//get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			if(fastData != null)
			{
				for(int i = 0; i < roiLen; i++)
				{
					firstPostBleach[i] = fastData[i][0];
				}
			}
			//compute error against exp data
			for(int i=0; i<roiLen; i++)
			{
				if(errorOfInterest != null && errorOfInterest[i])
				{
					for(int j=0; j<expTimePoints.length; j++)
					{
						double newValue = getValueFromParameters_twoDiffRates(mFracFast, fastData[i][j], mFracSlow, slowData[i][j], monitoringRate, firstPostBleach[i], expTimePoints[j]);
//						double newValue = (mFracFast * fastData[i][j] + mFracSlow * slowData[i][j] + immobileFrac * firstPostBleach[i]) * Math.exp(-(monitoringRate * expTimePoints[j]));
						double difference = expData[i][j] - newValue;
						if(bApplyMeasurementError)
						{
							difference = difference/measurementErrors[i][j];
						}
						error = error + difference * difference;
					}
				}
			}
			//add penalty for wrong parameter set
			if(mFracFast + mFracSlow > 1)
			{
				double mFracError = (mFracFast + mFracSlow - 1);
				error = error + (mFracError + mFracError * mFracError) * penalty;
			}
			if(diffSlowRate > diffFastRate)
			{
				double rateError = diffSlowRate - diffFastRate;
				error = error + (rateError + rateError * rateError) * penalty;
			}
			//System.out.println("error:" + error);
			return error;
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}
	}
	
	public static double[][] getValueByDiffRate(double refDiffRate, double newDiffRate, double[][] refData, double[] refTimePoints, double[] expTimePoints, int roiLen/*, double refTimeInterval*/) throws Exception
	{
		double[][] result = new double[roiLen][expTimePoints.length];
		int preTimeIndex = 0;
		int postTimeIndex = 0;
		int idx = 0;
		for(int j = 0; j < expTimePoints.length; j++)
		{	
			// find corresponding time points in reference data 
			double estimateTime = (newDiffRate/refDiffRate) * expTimePoints[j];
			for( ;idx < refTimePoints.length; idx ++)
			{
				if(estimateTime < (refTimePoints[idx] + FRAPOptimization.epsilon))
				{
					break;
				}
			}
			postTimeIndex = idx;
			
			if(postTimeIndex <= 0)//negtive newDiffRate will cause array index out of bound exception
			{
				preTimeIndex = 0;
				postTimeIndex = preTimeIndex;
			}
			else if(postTimeIndex > 0 && postTimeIndex < refTimePoints.length )
			{
				if((estimateTime > (refTimePoints[postTimeIndex] - FRAPOptimization.epsilon)) &&  (estimateTime  < (refTimePoints[postTimeIndex] + FRAPOptimization.epsilon)))
				{
					preTimeIndex = postTimeIndex;
				}
				else 
				{
					preTimeIndex = postTimeIndex - 1;
				}
			}
			else if(postTimeIndex >= refTimePoints.length)  
			{
				preTimeIndex = refTimePoints.length -1;
				postTimeIndex = preTimeIndex;
			}
			double preTimeInRefData = refTimePoints[preTimeIndex];
			double postTimeInRefData = refTimePoints[postTimeIndex];
			double proportion = 0;
			
			if((postTimeInRefData-preTimeInRefData) != 0)
			{
				proportion = ((estimateTime-preTimeInRefData)/(postTimeInRefData-preTimeInRefData));
			}
			
			for(int i = 0; i < roiLen; i++) 
			{
				//get data from reference data according to the estimate time.
				if(preTimeIndex == postTimeIndex)
				{
					result[i][j] = refData[i][preTimeIndex];
				}
				else 
				{
					double preDataValue = refData[i][preTimeIndex];
					double postDataValue = refData[i][postTimeIndex];
					double estimateDataValue = preDataValue + proportion *(postDataValue-preDataValue);
					result[i][j] = estimateDataValue;
				}
			}
		}
		return result;
	}
	
	//estimate best parameters and return the least error
	public static double estimate(FRAPOptData argOptData, Parameter[] inParams, String[] outParaNames, double[] outParaVals, boolean[] eoi) throws Exception
	{
		/*PowellSolver  solver = new PowellSolver();
		LookupTableObjectiveFunction func = new LookupTableObjectiveFunction(argOptData);
		
		//best point found. we have only one dimension here.
		double[] p = new double[1];
		p[0] = iniDiffGuess;
		//current direction set
		double[][] xi = new double[1][1];
		for (int i=0;i<1;i++)
		{
		   for (int j=0;j<1;j++)
		   {
		       xi[i][j]=(i == j ? 1.0 : 0.0);
		   }
		}
		//run powell with initial guess, initial direction set and objective function
		double minError = solver.powell(1, p, xi, FTOL, func);
		parameters[FRAPOptData.idxOptDiffRate] = p[0];
		parameters[FRAPOptData.idxMinError] = minError;*/
		//long startTime =System.currentTimeMillis();
		// create optimization solver 
		PowellOptimizationSolver optSolver = new PowellOptimizationSolver();
		// create optimization spec
		OptimizationSpec optSpec = new OptimizationSpec();
		DefaultScalarFunction scalarFunc = new LookupTableObjectiveFunction(argOptData, eoi); // add opt function 
		optSpec.setObjectiveFunction(new ImplicitObjectiveFunction(scalarFunc));
		// create solver spec 
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_POWELL, FRAPOptimization.FTOL);
		// create solver call back
		OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();
		// create optimization result set
		OptimizationResultSet optResultSet = null;
		for (int i = 0; i < inParams.length; i++) { //add parameters
			optSpec.addParameter(inParams[i]);
		}
		optResultSet = optSolver.solve(optSpec, optSolverSpec, optSolverCallbacks);
		OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
		//if the parameters are 5, we have to go over again to see if we get the best answer.
		if(inParams.length == 5)//5 parameters
		{
			OptimizationSpec optSpec2 = new OptimizationSpec();
			optSpec2.setObjectiveFunction(new ImplicitObjectiveFunction(scalarFunc));
			Parameter[] inParamsFromResult = generateInParamSet(inParams, optSolverResultSet.getBestEstimates());
			for (int i = 0; i < inParamsFromResult.length; i++) { //add parameters
				optSpec2.addParameter(inParamsFromResult[i]);
			}
			OptimizationResultSet tempOptResultSet = optSolver.solve(optSpec2, optSolverSpec, optSolverCallbacks);
			OptSolverResultSet  tempOptSolverResultSet = tempOptResultSet.getOptSolverResultSet();
			if(optSolverResultSet.getLeastObjectiveFunctionValue() > tempOptSolverResultSet.getLeastObjectiveFunctionValue())
			{
				optSolverResultSet = tempOptSolverResultSet;
			}
		}
		//System.out.println("obj function value:"+optResultSet.getObjectiveFunctionValue());
		//System.out.println("");
		// copy results to output parameters
		String[] names = optSolverResultSet.getParameterNames();
		double[] values = optSolverResultSet.getBestEstimates();
		for (int i = 0; i < names.length; i++) 
		{
			outParaNames[i] = names[i];
			outParaVals[i] = values[i];
		}
		//long endTime =System.currentTimeMillis();
		//System.out.println("total: " + ( endTime - startTime) );
		return  optSolverResultSet.getLeastObjectiveFunctionValue();
	}
	//for second run of optimization for diffusion with two diffusing components
	private static Parameter[] generateInParamSet(Parameter[] inputParams, double newValues[])
	{
		Parameter[] result = new Parameter[inputParams.length];
		Parameter primaryRate = inputParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE];
		Parameter primaryFrac = inputParams[FRAPModel.INDEX_PRIMARY_FRACTION];
		Parameter bwmRate = inputParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
		Parameter secondaryRate = inputParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE];
		Parameter secondaryFrac = inputParams[FRAPModel.INDEX_SECONDARY_FRACTION];
		
		if(newValues[FRAPModel.INDEX_PRIMARY_DIFF_RATE] < primaryRate.getLowerBound())
		{
			newValues[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = primaryRate.getLowerBound();
		}
		if(newValues[FRAPModel.INDEX_PRIMARY_DIFF_RATE] > primaryRate.getUpperBound())
		{
			newValues[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = primaryRate.getUpperBound();
		}
		if(newValues[FRAPModel.INDEX_PRIMARY_FRACTION] < primaryFrac.getLowerBound())
		{
			newValues[FRAPModel.INDEX_PRIMARY_FRACTION] = primaryFrac.getLowerBound();
		}
		if(newValues[FRAPModel.INDEX_PRIMARY_FRACTION] > primaryFrac.getUpperBound())
		{
			newValues[FRAPModel.INDEX_PRIMARY_FRACTION] = primaryFrac.getUpperBound();
		}
		if(newValues[FRAPModel.INDEX_BLEACH_MONITOR_RATE] < bwmRate.getLowerBound())
		{
			newValues[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = bwmRate.getLowerBound();
		}	
		if(newValues[FRAPModel.INDEX_BLEACH_MONITOR_RATE] > bwmRate.getUpperBound())
		{
			newValues[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = bwmRate.getUpperBound();
		}
		if(newValues[FRAPModel.INDEX_SECONDARY_DIFF_RATE] < secondaryRate.getLowerBound())
		{
			newValues[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = secondaryRate.getLowerBound();
		}
		if(newValues[FRAPModel.INDEX_SECONDARY_DIFF_RATE] > secondaryRate.getUpperBound())
		{
			newValues[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = secondaryRate.getUpperBound();
		}
		if(newValues[FRAPModel.INDEX_SECONDARY_FRACTION] < secondaryFrac.getLowerBound())
		{
			newValues[FRAPModel.INDEX_SECONDARY_FRACTION] = secondaryFrac.getLowerBound();
		}
		if(newValues[FRAPModel.INDEX_SECONDARY_FRACTION] > secondaryFrac.getUpperBound())
		{
			newValues[FRAPModel.INDEX_SECONDARY_FRACTION] = secondaryFrac.getUpperBound();
		}
		
		
		result[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(primaryRate.getName(), 
                                                    primaryRate.getLowerBound(), 
                                                    primaryRate.getUpperBound(),
                                                    primaryRate.getScale(),
                                                    newValues[FRAPModel.INDEX_PRIMARY_DIFF_RATE]);
		
		result[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(primaryFrac.getName(), 
													primaryFrac.getLowerBound(), 
													primaryFrac.getUpperBound(),
													primaryFrac.getScale(),
													newValues[FRAPModel.INDEX_PRIMARY_FRACTION]);
		result[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(bwmRate.getName(),
												    bwmRate.getLowerBound(),
												    bwmRate.getUpperBound(),
												    bwmRate.getScale(),
												    newValues[FRAPModel.INDEX_BLEACH_MONITOR_RATE]);
		result[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = new Parameter(secondaryRate.getName(), 
													secondaryRate.getLowerBound(), 
													secondaryRate.getUpperBound(),
													secondaryRate.getScale(),
													newValues[FRAPModel.INDEX_SECONDARY_DIFF_RATE]);
		result[FRAPModel.INDEX_SECONDARY_FRACTION] = new Parameter(secondaryFrac.getName(), 
													secondaryFrac.getLowerBound(), 
													secondaryFrac.getUpperBound(),
													secondaryFrac.getScale(),
													newValues[FRAPModel.INDEX_SECONDARY_FRACTION]);
       
 		return result;
	}
	//for exp data mainly, can be used for sim and opt data as well
	public static SimpleReferenceData doubleArrayToSimpleRefData(double[][] origData, double[] timePoints, int startingIndex, boolean[] selectedROIs) /*throws Exception*/
	{
		if(origData != null && timePoints != null && selectedROIs != null)
		{
			int numSelectedROITypes = 0;
			for(int i=0; i<selectedROIs.length; i++)
			{
				if(selectedROIs[i])
				{
					numSelectedROITypes ++;
				}
			}
			
			String[] columnNames = new String[numSelectedROITypes+1];
			double[] weights = new double[numSelectedROITypes+1];
			double[][] data = new double[numSelectedROITypes+1][];
			//set time
			columnNames[0] = "t";
			weights[0] = 1.0;
			double[] truncatedTimes = new double[timePoints.length-startingIndex];
			System.arraycopy(timePoints, startingIndex, truncatedTimes, 0, truncatedTimes.length);
			data[0] = truncatedTimes;
			//set data
			int colCounter =  0;//already take "t" colume into account, "t" column is at the column index 0.
			for (int j = 0; j < FRAPData.VFRAP_ROI_ENUM.values().length; j++) {
				if(!selectedROIs[j])//skip unselected ROIs
				{
					continue;
				}
				colCounter ++; 
				columnNames[colCounter] = FRAPData.VFRAP_ROI_ENUM.values()[j].name();
				weights[colCounter] = 1.0;
//				double[] allTimesData = origData[j];
//				double[] truncatedTimesData = new double[truncatedTimes.length];
//				System.arraycopy(allTimesData, startingIndex, truncatedTimesData, 0, truncatedTimes.length);
				data[colCounter] = origData[j]; //original data has been truncated from dimension reduced exp data function.
			}
			return new SimpleReferenceData(columnNames, weights, data);
			
		}
		return null;
	}
	
	//used by opt or sim data, no truncation of time/data. @param startingIndex is used to shift opt/sim times to compare with exp times
	public static ODESolverResultSet doubleArrayToSolverResultSet(double[][] origData, double[] timePoints, double timePointOffset, boolean[] selectedROIs) /*throws Exception*/
	{
		if(origData != null && timePoints != null && selectedROIs != null)
		{
			int numSelectedROITypes = 0;
			for(int i=0; i<selectedROIs.length; i++)
			{
				if(selectedROIs[i])
				{
					numSelectedROITypes ++;
				}
			}
			
			ODESolverResultSet newOdeSolverResultSet = new ODESolverResultSet();
			newOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < selectedROIs.length; j++) {
				if(!selectedROIs[j]){continue;}
				String currentROIName = FRAPData.VFRAP_ROI_ENUM.values()[j].name();
				String name = currentROIName;
				newOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			
			//set time
			for (int j = 0; j < timePoints.length; j++) {
				double[] row = new double[numSelectedROITypes+1];
				row[0] = timePoints[j] + timePointOffset;
				newOdeSolverResultSet.addRow(row);
			}
			//set data
			int columncounter = 0;
			for (int j = 0; j < selectedROIs.length; j++) {
				if(!selectedROIs[j]){continue;}
					double[] values = origData[j];
					for (int k = 0; k < values.length; k++) {
						newOdeSolverResultSet.setValue(k, columncounter+1, values[k]);
					}
				columncounter++;
			}
			
			return newOdeSolverResultSet;
		}
		return null;
	}
	
	/*
	 * From netCDF file, the rawdata contains average data over time under difference 8 roi rings,
	 * which are stored in colume1 to colume8. colume0 stores the time average data from region0
	 * which is the area beyond ring1-8.
	 * Here we want to generate a double array which includes all roi data (bleached, bg, cell, ring1 to ring8)
	 */
	public static double[][] extendSimToFullROIData(FRAPData fData, double[][] rawData, int numTimePoints)
	{
		double[][] results = null;

		int numRois = FRAPData.VFRAP_ROI_ENUM.values().length;
		if(rawData != null && rawData.length > 0)
		{
			//get bleached roi data from ring1, ring2 and ring3 data
			double[] ring1Data = rawData[1];
			double[] ring2Data = rawData[2];
			double[] ring3Data = rawData[3];
			int numRing1Pixels = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()).getNonzeroPixelsCount();
			int numRing2Pixels = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()).getNonzeroPixelsCount();
			int numRing3Pixels = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()).getNonzeroPixelsCount();
			results = new double[numRois][numTimePoints];
			int totalRoiRings = 8;
			int ring1IdxInRawData = 1;
			int ring1IdxDiff = 2; //because ring1 index in restuls is 3  
			//move ring1-8(colume 1-8) in rawData to the last 8 columes in results
			for(int i=ring1IdxInRawData; i<(ring1IdxInRawData+totalRoiRings); i++)
			{
				results[i+ring1IdxDiff] = rawData[i];
			}
			//get bleached roi time average data and store it in results colume 0
			double[] bleachedRoiData = new double[numTimePoints];
			for(int i=0; i<numTimePoints; i++)
			{
				bleachedRoiData[i]= (ring1Data[i]*numRing1Pixels + ring2Data[i]*numRing2Pixels + ring3Data[i] *numRing3Pixels)/(numRing1Pixels+numRing2Pixels+numRing3Pixels); 
			}
			results[0] = bleachedRoiData;
			//put rawData[0] to the second and third column in results (for background and cell)
			//they are not used for optimization, and since we have no way to get the time average data for them
			//we then use rawData colume 0 to fill them.
			results[1]=rawData[0];
			results[2]=rawData[0];        
		}			                
		return results;
	}
	
}
