package cbit.vcell.message.server.htc.slurm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.vcell.util.FileUtils;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.PortableCommandWrapper;
import cbit.vcell.solvers.ExecutableCommand;
import edu.uchc.connjur.wb.LineStringBuilder;

public class SlurmProxy extends HtcProxy {
	
	private final static int SCANCEL_JOB_NOT_FOUND_RETURN_CODE = 1;
	private final static String SCANCEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	protected final static String SLURM_SUBMISSION_FILE_EXT = ".slurm.sub";
	private Map<HtcJobID, JobInfoAndStatus> statusMap;


	// note: full commands use the PropertyLoader.htcPbsHome path.
	private final static String JOB_CMD_SUBMIT = "sbatch";
	private final static String JOB_CMD_DELETE = "scancel";
	private final static String JOB_CMD_STATUS = "sacct";
	//private final static String JOB_CMD_QACCT = "qacct";
	
	//private static String Slurm_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSlurmHome);
	private static String Slurm_HOME = ""; // slurm commands should be in the path (empty prefix)
	private static String htcLogDirExternalString = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
	private static String MPI_HOME_EXTERNAL= PropertyLoader.getProperty(PropertyLoader.MPI_HOME_EXTERNAL,"");
	static {
//		if (!Slurm_HOME.endsWith("/")){
//			Slurm_HOME += "/";
//		}
		if (!htcLogDirExternalString.endsWith("/")){
			htcLogDirExternalString = htcLogDirExternalString+"/";
		}
	}

	public SlurmProxy(CommandService commandService, String htcUser) {
		super(commandService, htcUser);
		statusMap = new HashMap<HtcJobID,JobInfoAndStatus>( );
	}

	@Override
	public HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException {
		if (statusMap.containsKey(htcJobId)) {
			return statusMap.get(htcJobId).status;
		}
		throw new HtcJobNotFoundException("job not found", htcJobId);
	}

	/**
	 * qdel 6894
	 *
vcell has registered the job 6894 for deletion
	 *
	 * qdel 6894
	 *
job 6894 is already in deletion
	 *
	 * qdel 6894
	 *
denied: job "6894" does not exist

	 */


	@Override
	public void killJob(HtcJobID htcJobId) throws ExecutableException, HtcException {

		String[] cmd = new String[]{Slurm_HOME + JOB_CMD_DELETE, Long.toString(htcJobId.getJobNumber())};
		try {
			//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });

			CommandOutput commandOutput = commandService.command(cmd,new int[] { 0, SCANCEL_JOB_NOT_FOUND_RETURN_CODE });

			Integer exitStatus = commandOutput.getExitStatus();
			String standardOut = commandOutput.getStandardOutput();
			if (exitStatus!=null && exitStatus.intValue()==SCANCEL_JOB_NOT_FOUND_RETURN_CODE && standardOut!=null && standardOut.toLowerCase().contains(SCANCEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw new HtcJobNotFoundException(standardOut, htcJobId);
			}
		}catch (ExecutableException e){
			e.printStackTrace();
			if (!e.getMessage().toLowerCase().contains(SCANCEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage(), htcJobId);
			}
		}
	}

	/**
	 * adding MPICH command if necessary
	 * @param ncpus if != 1, {@link #MPI_HOME} command prepended
	 * @param cmds command set
	 * @return new String
	 */
	private final String buildExeCommand(int ncpus,String command) {
		if (ncpus == 1) {
			return command;
		}
		final char SPACE = ' ';
		StringBuilder sb = new StringBuilder( );
		sb.append(MPI_HOME_EXTERNAL);
		sb.append("/bin/mpiexec -np ");
		sb.append(ncpus);
		sb.append(SPACE);
		sb.append(command);
		return sb.toString().trim( );
	}

	@Override
	public HtcProxy cloneThreadsafe() {
		return new SlurmProxy(getCommandService().clone(), getHtcUser());
	}

	@Override
	public String getSubmissionFileExtension() {
		return SLURM_SUBMISSION_FILE_EXT;
	}

	/**
	 * sacct 
	 * 
	 *        JobID    JobName  Partition    Account  AllocCPUS      State ExitCode
	 *        ------------ ---------- ---------- ---------- ---------- ---------- --------
	 *        4989         V_TEST_10+        amd      pi-loew          1 CANCELLED+      0:0
	 *        4990         V_TEST_10+    general      pi-loew          2  COMPLETED      0:0
	 *        4990.batch        batch                 pi-loew          2  COMPLETED      0:0
	 * 
	 * 
	 * allowed fields: 
	 * 
	 * AllocCPUS         AllocGRES         AllocNodes        AllocTRES
	 * Account           AssocID           AveCPU            AveCPUFreq
	 * AveDiskRead       AveDiskWrite      AvePages          AveRSS
	 * AveVMSize         BlockID           Cluster           Comment
	 * ConsumedEnergy    ConsumedEnergyRaw CPUTime           CPUTimeRAW
	 * DerivedExitCode   Elapsed           Eligible          End
	 * ExitCode          GID               Group             JobID
	 * JobIDRaw          JobName           Layout            MaxDiskRead
	 * MaxDiskReadNode   MaxDiskReadTask   MaxDiskWrite      MaxDiskWriteNode
	 * MaxDiskWriteTask  MaxPages          MaxPagesNode      MaxPagesTask
	 * MaxRSS            MaxRSSNode        MaxRSSTask        MaxVMSize
	 * MaxVMSizeNode     MaxVMSizeTask     MinCPU            MinCPUNode
	 * MinCPUTask        NCPUS             NNodes            NodeList
	 * NTasks            Priority          Partition         QOS
	 * QOSRAW            ReqCPUFreq        ReqCPUFreqMin     ReqCPUFreqMax
	 * ReqCPUFreqGov     ReqCPUS           ReqGRES           ReqMem
	 * ReqNodes          ReqTRES           Reservation       ReservationId
	 * Reserved          ResvCPU           ResvCPURAW        Start
	 * State             Submit            Suspended         SystemCPU
	 * Timelimit         TotalCPU          UID               User
	 * UserCPU           WCKey             WCKeyID
	 * 
	 *  
	 *  sacct -u vcell -P -o jobid%25,jobname%25,partition,user,alloccpus,ncpus,ntasks,state%13,exitcode
	 *  
	 *  JobID|JobName|Partition|User|AllocCPUS|NCPUS|NTasks|State|ExitCode
	 *  4989|V_TEST_107541132_0_0|amd|vcell|1|1||CANCELLED by 10001|0:0
	 *  4990|V_TEST_107541132_0_0|general|vcell|2|2||COMPLETED|0:0
	 *  4990.batch|batch||vcell|2|2|1|COMPLETED|0:0
	 *  4991|V_TEST_107548598_0_0|general|vcell|2|2||COMPLETED|0:0
	 *  4991.batch|batch||vcell|2|2|1|COMPLETED|0:0
	 *  
	 *  sacct can specify a particular job:
	 *  
	 *  -j job(.step) , --jobs=job(.step)
	 *  
	 *     Displays information about the specified job(.step) or list of job(.step)s.
	 *     The job(.step) parameter is a comma-separated list of jobs. 
	 *     Space characters are not permitted in this list. 
	 *     NOTE: A step id of 'batch' will display the information about the batch step. 
	 *     The batch step information is only available after the batch job is complete unlike regular steps which are available when they start.
	 *     The default is to display information on all jobs.
	 * @throws IOException 
	 */

	@Override
	public List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException, IOException {
		String states = SlurmJobStatus.RUNNING.shortName+","+
						SlurmJobStatus.CONFIGURING.shortName+","+
						SlurmJobStatus.RESIZING.shortName;
		String[] cmds = {Slurm_HOME + JOB_CMD_STATUS,"-u","vcell","-P","-s",states,"-o","jobid%25,jobname%25,partition,user,alloccpus,ncpus,ntasks,state%13,exitcode"};
		CommandOutput commandOutput = commandService.command(cmds);

		String output = commandOutput.getStandardOutput();
		return extractJobIds(output, statusMap);
	}

	public static List<HtcJobID> extractJobIds(String output, Map<HtcJobID, JobInfoAndStatus> statusMap) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(output));
		String line = reader.readLine();
		if (!line.equals("JobID|JobName|Partition|User|AllocCPUS|NCPUS|NTasks|State|ExitCode")){
			throw new RuntimeException("unexpected first line from sacct: '"+line+"'");
		}
		statusMap.clear();
		while ((line = reader.readLine()) != null){
			String[] tokens = line.split("\\|");
			String jobID = tokens[0];
			String jobName = tokens[1];
			String partition = tokens[2];
			String user = tokens[3];
			String allocCPUs = tokens[4];
			String ncpus = tokens[5];
			String ntasks = tokens[6];
			String state = tokens[7];
			String exitcode = tokens[8];
			if (jobName.equals("batch")){
				continue;
			}
			HtcJobID htcJobID = new HtcJobID(jobID,BatchSystemType.SLURM);
			String errorPath = null;
			String outputPath = null;
			HtcJobInfo htcJobInfo = new HtcJobInfo(htcJobID, true, jobName, errorPath, outputPath);
			HtcJobStatus htcJobStatus = new HtcJobStatus(SlurmJobStatus.parseStatus(state));
			statusMap.put(htcJobID, new JobInfoAndStatus(htcJobInfo, htcJobStatus));
		}
		return new ArrayList<HtcJobID>(statusMap.keySet());
	}

	@Override
	public Map<HtcJobID,HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException {
		HashMap<HtcJobID,HtcJobInfo> jobInfoMap = new HashMap<HtcJobID,HtcJobInfo>();
		for (HtcJobID htcJobID : htcJobIDs){
			HtcJobInfo htcJobInfo = getJobInfo(htcJobID);
			if (htcJobInfo!=null){
				jobInfoMap.put(htcJobID,htcJobInfo);
			}
		}
		return jobInfoMap;
	}
	


	/**
	 * @param htcJobID
	 * @return job info or null
	 */
	public HtcJobInfo getJobInfo(HtcJobID htcJobID) {
		return statusMap.get(htcJobID).info;
	}

	public String[] getEnvironmentModuleCommandPrefix() {
//		ArrayList<String> ar = new ArrayList<String>();
//		ar.add("source");
//		ar.add("/etc/profile.d/modules.sh;");
//		ar.add("module");
//		ar.add("load");
//		ar.add(PropertyLoader.getProperty(PropertyLoader.slurmModulePath, "htc/slurm")+";");
//		return ar.toArray(new String[0]);
		return new String[0];
	}

	/**
	 * write bash script for submission
	 * @param jobName
	 * @param sub_file
	 * @param commandSet
	 * @param ncpus
	 * @param memSize
	 * @param postProcessingCommands
	 * @return String containing script
	 */
	String generateScript(String jobName, ExecutableCommand.Container commandSet, int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands) {
		final boolean isParallel = ncpus > 1;


		LineStringBuilder lsb = new LineStringBuilder();

		lsb.write("#!/usr/bin/env bash");
		String partition = "vcell";
		lsb.write("#SBATCH --partition=" + partition);
		lsb.write("#SBATCH -J " + jobName);
		lsb.write("#SBATCH -o " + htcLogDirExternalString+jobName+".slurm.log");
		lsb.write("#SBATCH -e " + htcLogDirExternalString+jobName+".slurm.log");
		String nodelist = PropertyLoader.getProperty(PropertyLoader.htcNodeList, null);
		if (nodelist!=null && nodelist.trim().length()>0) {
			lsb.write("#SBATCH --nodelist="+nodelist);
		}
		lsb.write("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles");
		lsb.write("source /usr/share/Modules/init/bash");
		lsb.write("module load singularity");
		//			sw.append("#$ -l mem=" + (int)(memSize + SLURM_MEM_OVERHEAD_MB) + "mb");

		//int JOB_MEM_OVERHEAD_MB = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jobMemoryOverheadMB));

		//long jobMemoryMB = (JOB_MEM_OVERHEAD_MB+((long)memSize));
//		lsb.write("#$ -j y");
		//		    sw.append("#$ -l h_vmem="+jobMemoryMB+"m\n");
//		lsb.write("#$ -cwd");
		String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);

		//
		// Initialize Singularity
		//
		lsb.write("echo \"job running on host `hostname -f`\"");
		lsb.newline();
		lsb.write("echo \"id is `id`\"");
		lsb.newline();
		lsb.write("echo \"bash version is `bash --version`\"");
		lsb.newline();
		lsb.write("echo ENVIRONMENT");
		lsb.write("env");
		lsb.newline();
		String singularity_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_singularity_image);
		String docker_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_docker_name);
		lsb.write("cmd_prefix=");
		lsb.write("if command -v singularity >/dev/null 2>&1; then");
		lsb.write("   # singularity command exists");
		lsb.write("   if [ ! -e "+singularity_image+" ] ; then");
		lsb.write("      echo \"singularity image "+singularity_image+" not found, building from docker image\"");
		lsb.write("      echo \"assuming Singularity version 2.4 or later is installed on host system.\"");
		lsb.write("      singularity build "+singularity_image+" docker://"+docker_image);
		lsb.write("      stat=$?");
		lsb.write("      if [ $stat -ne 0 ]; then");
		lsb.write("         echo \"failed to build singularity image, returning $stat to Slurm\"");
		lsb.write("         exit $stat");
		lsb.write("      fi");
		lsb.write("   else");
		lsb.write("      echo \"singularity image "+singularity_image+" found\"");
		lsb.write("   fi");
		lsb.write("   cmd_prefix=\"singularity run --bind "+primaryDataDirExternal+":/simdata "+singularity_image+" \"");
		lsb.write("else");
		lsb.write("   if command -v docker >/dev/null 2>&1; then");
		String jmsurl_external=PropertyLoader.getRequiredProperty(PropertyLoader.jmsURLExternal);
		String jmsuser=PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
		String jmspswd=PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue,PropertyLoader.jmsPasswordFile);
		String serverid=PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
		String softwareVersion=PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
		jmsurl_external = jmsurl_external.replace("(","\\(").replace(")","\\)");
		String environmentVars = " -e jmsurl_internal=\""+jmsurl_external+"\""+
								" -e jmsuser="+jmsuser+
								" -e jmspswd="+jmspswd+
								" -e serverid="+serverid+
								" -e datadir_internal="+primaryDataDirExternal+
								" -e softwareVersion="+softwareVersion;
		lsb.write("       cmd_prefix=\"docker run --rm -v "+primaryDataDirExternal+":/simdata "+environmentVars+" "+docker_image+" \"");
		lsb.write("   fi");
		lsb.write("fi");
		lsb.write("echo \"cmd_prefix is '${cmd_prefix}'\"");

		lsb.newline();
		/**
		 * excerpt from vcell-batch Dockerfile
		 * 
		 * ENV softwareVersion=VCell_7.0_build_99 \
		 *   serverid=TEST2 \
		 *   jmsurl="failover:(tcp://vcell-service.cam.uchc.edu:61616)" \
		 *   jmsuser=clientUser \
		 *   jmspswd=dummy \
		 *   solverprimarydata=/simdata/ \
		 *   mongodbhost="vcell-service.cam.uchc.edu" \
		 *   mongodbport=27017 \
		 *   jmsblob_minsize=100000
		 */
//		String serverid = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
//		PropertyLoader.api
//		String jmsurlExternal = PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL);
//		lsb.write("SINGULARITY_serverid="+serverid);

		if (isParallel) {
			// #SBATCH
//			lsb.append("#$ -pe mpich ");
//			lsb.append(ncpus);
//			lsb.newline();
			
			lsb.append("#SBATCH -n " + ncpus);
			lsb.newline();

			lsb.append("#$ -v LD_LIBRARY_PATH=");
			lsb.append(MPI_HOME_EXTERNAL+"/lib");
			lsb.write(":"+primaryDataDirExternal);
		}
		lsb.newline();
	
		final boolean hasExitProcessor = commandSet.hasExitCodeCommand();
	//	lsb.write("run_in_container=\"singularity /path/to/data:/simdata /path/to/image/vcell-batch.img);
		if (hasExitProcessor) {
			ExecutableCommand exitCmd = commandSet.getExitCodeCommand();
			exitCmd.stripPathFromCommand();
			lsb.write("callExitProcessor( ) {");
			lsb.append("\techo exitCommand = ");
			lsb.write("$cmd_prefix " + exitCmd.getJoinedCommands("$1"));
			lsb.append('\t');
			lsb.write("$cmd_prefix " + exitCmd.getJoinedCommands());
			lsb.write("}");
			lsb.write("echo");
		}

		for (ExecutableCommand ec: commandSet.getExecCommands()) {
			lsb.write("echo");
			ec.stripPathFromCommand();
			String cmd= ec.getJoinedCommands();
			if (ec.isParallel()) {
				if (isParallel) {
					cmd = buildExeCommand(ncpus, cmd);
				}
				else {
					throw new UnsupportedOperationException("parallel command " + ec.getJoinedCommands() + " called in non-parallel submit");
				}
			}
			lsb.append("echo command = ");
			lsb.write("$cmd_prefix " + cmd);

			lsb.write("(");
			if (ec.getLdLibraryPath()!=null){
				lsb.write("    export LD_LIBRARY_PATH="+ec.getLdLibraryPath().path+":$LD_LIBRARY_PATH");
			}
			lsb.write("    "+"$cmd_prefix " + cmd);
			lsb.write(")");
			lsb.write("stat=$?");

			lsb.append("echo ");
			lsb.append("$cmd_prefix " + cmd);
			lsb.write("returned $stat");

			lsb.write("if [ $stat -ne 0 ]; then");
			if (hasExitProcessor) {
				lsb.write("\tcallExitProcessor $stat");
			}
			lsb.write("\techo returning $stat to Slurm");
			lsb.write("\texit $stat");
			lsb.write("fi");
		}

		Objects.requireNonNull(postProcessingCommands);
		PortableCommandWrapper.insertCommands(lsb.sb, postProcessingCommands);
		lsb.newline();
		if (hasExitProcessor) {
			lsb.write("callExitProcessor 0");
		}
		lsb.newline();
		return lsb.sb.toString();
	}

	@Override
	public HtcJobID submitJob(String jobName, String sub_file_external, ExecutableCommand.Container commandSet, int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands) throws ExecutableException {
		try {
			String text = generateScript(jobName, commandSet, ncpus, memSize, postProcessingCommands);

			File tempFile = File.createTempFile("tempSubFile", ".sub");

			writeUnixStyleTextFile(tempFile, text);

			// move submission file to final location (either locally or remotely).
			if (LG.isDebugEnabled()) {
				LG.debug("<<<SUBMISSION FILE>>> ... moving local file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file_external+"'");
			}
			commandService.pushFile(tempFile,sub_file_external);
			if (LG.isDebugEnabled()) {
				LG.debug("<<<SUBMISSION FILE START>>>\n"+FileUtils.readFileToString(tempFile)+"\n<<<SUBMISSION FILE END>>>\n");
			}
			tempFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		/**
		 * 
		 * > sbatch /share/apps/vcell2/deployed/test/htclogs/V_TEST_107643258_0_0.slurm.sub
		 * Submitted batch job 5174
		 * 
		 */
		String[] completeCommand = new String[] {Slurm_HOME + JOB_CMD_SUBMIT, sub_file_external};
		CommandOutput commandOutput = commandService.command(completeCommand);
		String jobid = commandOutput.getStandardOutput().trim();
		final String EXPECTED_STDOUT_PREFIX = "Submitted batch job ";
		if (jobid.startsWith(EXPECTED_STDOUT_PREFIX)){
			jobid = jobid.replace(EXPECTED_STDOUT_PREFIX, "");
		}else{
			throw new ExecutableException("unexpected response from '"+JOB_CMD_SUBMIT+"' while submitting simulation: '"+jobid+"'");
		}
		return new HtcJobID(jobid,BatchSystemType.SLURM);
	}

	/**
	 * package job info and status
	 */
	public static class JobInfoAndStatus {
		final HtcJobInfo info;
		final HtcJobStatus status;
		/**
		 * @param info not null
		 * @param status not null
		 */
		JobInfoAndStatus(HtcJobInfo info, HtcJobStatus status) {
			Objects.requireNonNull(info);
			Objects.requireNonNull(status);
			this.info = info;
			this.status = status;
		}
		@Override
		public String toString() {
			return info.toString() + ": "  + status.toString();
		}

	}
}
