package com.letv.portal.python.service.impl;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.util.ConfigUtil;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.BuildStatus;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.DbUserStatus;
import com.letv.portal.enumeration.HostType;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.enumeration.MclusterType;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.BuildModel;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.UserModel;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IBuildService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.IUserService;
import com.letv.portal.zabbixPush.IZabbixPushService;
import com.mysql.jdbc.StringUtils;

@Service("buildTaskService")
public class BuildTaskServiceImpl implements IBuildTaskService{
	
	private final static Logger logger = LoggerFactory.getLogger(BuildTaskServiceImpl.class);
	
	private static long PYTHON_CREATE_CHECK_TIME = ConfigUtil.getlong("python_create_check_time"); //300000;//单位：ms
	private static long PYTHON_CHECK_INTERVAL_TIME = ConfigUtil.getlong("python_check_interval_time");// 2000;//单位：ms
	
	private static long PYTHON_CREATE_INTERVAL_INIT_TIME = ConfigUtil.getlong("python_create_interval_init_time");//60000;//单位：ms
	
	private static long PYTHON_INIT_CHECK_TIME = ConfigUtil.getlong("python_init_check_time");//600000;//单位：ms
	private static long PYTHON_INIT_CHECK_INTERVAL_TIME = ConfigUtil.getlong("python_init_check_interval_time");//5000;//单位：ms
	
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private IDbUserService dbUserService;
	@Autowired
	private IPythonService pythonService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IBuildService buildService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IFixedPushService fixedPushService;
	@Autowired 
	private IZabbixPushService zabbixPushService;
	
	@Value("${error.email.to}")
	private String ERROR_MAIL_ADDRESS;
	
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Override
	@Async
	public void buildMcluster(MclusterModel mclusterModel) {
		this.buildMcluster(mclusterModel, null);
	}
	
	@Override
	@Async
	public void buildMcluster(MclusterModel mclusterModel,Long dbId) {
		boolean nextStep = true;
		
		this.buildService.initStatus(mclusterModel.getId());
		HostModel host = getHostByHclusterId(mclusterModel.getHclusterId());
		try {
			if(nextStep) {
				nextStep = createContainer(mclusterModel,dbId,host);
			}
			if(nextStep) {
				nextStep = initContainer(mclusterModel,dbId);
			}
			if(nextStep) {
				mclusterModel.setStatus(MclusterStatus.RUNNING.getValue());
				this.mclusterService.audit(mclusterModel);
				this.buildResultToMgr("mcluster集群" + mclusterModel.getMclusterName() + "创建", "成功","", ERROR_MAIL_ADDRESS);
			}
		} catch (Exception e) {
			BuildModel nextBuild = new BuildModel();
			nextBuild.setMclusterId(mclusterModel.getId());
			nextBuild.setStartTime(new Date());
			nextBuild.setStatus(BuildStatus.FAIL.getValue());
			nextBuild.setMsg(e.getMessage());
			this.buildService.updateByStatus(nextBuild);
			mclusterModel.setStatus(MclusterStatus.BUILDFAIL.getValue());
			this.mclusterService.audit(mclusterModel);
			if(dbId!=null) {
				DbModel dbModel = new DbModel();
				dbModel.setId(dbId);
				dbModel.setStatus(DbStatus.BUILDFAIL.getValue());
				this.dbService.updateBySelective(dbModel);
			}
			this.buildResultToMgr("mcluster集群" + mclusterModel.getMclusterName() + "创建", "失败", e.getMessage(), ERROR_MAIL_ADDRESS);
			return;
		}
		if(nextStep && dbId != null) {
			this.buildDb(dbId);
		}
	}
	
	@Override
	public boolean createContainer(MclusterModel mclusterModel,Long dbId,HostModel host) {
		boolean nextStep = true;
		
		int step = 0;
		Date startTime = new Date();
		
		if(nextStep) {
			step++;
			nextStep = this.analysis(transResult(this.pythonService.createContainer(mclusterModel.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword())), step, startTime, mclusterModel.getId(), dbId);
		}
		if(nextStep) {
			step++;
			Map<String,Object> result =  transResult(pythonService.checkContainerCreateStatus(mclusterModel.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword()));
			Date checkStartDate = new Date();
			while(!analysisCheckCreateResult(result)) {
				try {
					Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Date checkDate = new Date();
				long  diff = checkDate.getTime() - checkStartDate.getTime();
				if(diff >PYTHON_CREATE_CHECK_TIME) {
					BuildModel nextBuild = new BuildModel();
					nextBuild.setMclusterId(mclusterModel.getId());
					nextBuild.setStep(step);
					nextBuild.setStartTime(new Date());
					nextBuild.setStatus(BuildStatus.FAIL.getValue());
					nextBuild.setMsg("time over check");
					this.buildService.updateByStep(nextBuild);
					
					if(dbId!=null) {
						DbModel dbModel = new DbModel();
						dbModel.setId(dbId);
						dbModel.setStatus(DbStatus.BUILDFAIL.getValue());
						this.dbService.updateBySelective(dbModel);
					}
					mclusterModel.setStatus(MclusterStatus.BUILDFAIL.getValue());
					this.mclusterService.audit(mclusterModel);
					
					this.buildResultToMgr("mcluster集群" + mclusterModel.getMclusterName() +"创建", "失败", "check create containers time out", ERROR_MAIL_ADDRESS);
					return false;
				}
				result = transResult(pythonService.checkContainerCreateStatus(mclusterModel.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword()));
			}
			nextStep = analysis(result, step, startTime, mclusterModel.getId(), dbId);
			//保存container信息
			
			List<Map> containers = (List<Map>) ((Map)result.get("response")).get("containers");
			
			for (Map map : containers) {
				ContainerModel container = new ContainerModel();
				try {
					BeanUtils.populate(container, map);
					container.setMclusterId(mclusterModel.getId());
					container.setIpMask((String) map.get("netMask"));
					container.setContainerName((String) map.get("containerName"));
					container.setStatus(MclusterStatus.RUNNING.getValue());
					//物理机集群维护完成后，修改此处，需要关联物理机id
					container.setHostIp((String) map.get("hostIp"));
					HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
					if(null != hostModel) {
						container.setHostId(hostModel.getId());
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				this.containerService.insert(container);
			}
		}
		return nextStep;
	}
	
	@Override
	@Async
	public void buildDb(Long dbId) {
		Integer status = null;
		String resultMsg = "";
		String detail = "";
		Map<String,Object> params = this.dbService.selectCreateParams(dbId);
		try {
			String result = this.pythonService.createDb((String)params.get("nodeIp"), (String)params.get("dbName"), (String)params.get("dbName"), null, (String)params.get("username"), (String)params.get("password"));
			
			if(analysisResult(transResult(result))) {
				resultMsg = "成功";
				status = DbStatus.NORMAL.getValue();
				this.buildResultToUser("DB数据库" + params.get("dbName") + "创建",((BigInteger)params.get("createUser")).longValue());
                buildUser(createDefalutAdmin(dbId).toString());
			} else {
				resultMsg = "失败";
				status = DbStatus.BUILDFAIL.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = "失败";
			detail = e.getMessage();
			status = DbStatus.BUILDFAIL.getValue();
		} finally {
			this.buildResultToMgr("DB数据库" + params.get("dbName") + "创建", resultMsg, detail, ERROR_MAIL_ADDRESS);
			DbModel dbModel = new DbModel();
			dbModel.setId(dbId);
			dbModel.setStatus(status);
			this.dbService.updateBySelective(dbModel);
		}
	}
    

	@Override
	@Async
	public void buildUser(String ids) {
		String[] str = ids.split(",");
		String resultMsg = "";
		String detail = "";
		for (String id : str) {
			//查询所属db 所属mcluster 及container数据
			DbUserModel dbUserModel = this.dbUserService.selectById(Long.parseLong(id));
			Map<String,Object> params = this.dbUserService.selectCreateParams(Long.parseLong(id));
			try {
				String result = this.pythonService.createDbUser(dbUserModel, (String)params.get("dbName"), (String)params.get("nodeIp"), (String)params.get("username"), (String)params.get("password"));
				if(analysisResult(transResult(result))) {
					resultMsg="成功";
					dbUserModel.setStatus(DbUserStatus.NORMAL.getValue());
					Map response = (Map) transResult(result).get("response");
					String userPwd = (String) response.get("user_password");
					this.buildResultToUser("DB数据库("+params.get("dbName")+")用户" + dbUserModel.getUsername() + "(密码:"+userPwd+")创建", ((BigInteger)params.get("createUser")).longValue());
				} else {
					resultMsg="失败";
					dbUserModel.setStatus(DbUserStatus.BUILDFAIL.getValue());
				}
			} catch (Exception e) {
				resultMsg="失败";
				detail = e.getMessage();
				dbUserModel.setStatus(DbUserStatus.BUILDFAIL.getValue());
			} finally {
				this.buildResultToMgr("DB数据库("+params.get("dbName")+")用户" + dbUserModel.getUsername() + "创建", resultMsg, detail, ERROR_MAIL_ADDRESS);
				this.dbUserService.updateStatus(dbUserModel);
			}
		}
		
	}
	@Override
	@Async
	public void updateUser(String ids) {
		String[] str = ids.split(",");
		String resultMsg = "";
		String detail = "";
		for (String id : str) {
			//查询所属db 所属mcluster 及container数据
			DbUserModel dbUserModel = this.dbUserService.selectById(Long.parseLong(id));
			Map<String,Object> params = this.dbUserService.selectCreateParams(Long.parseLong(id));
			try {
				String result = this.pythonService.createDbUser(dbUserModel, (String)params.get("dbName"), (String)params.get("nodeIp"), (String)params.get("username"), (String)params.get("password"));
				if(analysisResult(transResult(result))) {
					resultMsg="成功";
					dbUserModel.setStatus(DbUserStatus.NORMAL.getValue());
					this.buildResultToUser("DB数据库("+params.get("dbName")+")用户" + dbUserModel.getUsername() + "修改", ((BigInteger)params.get("createUser")).longValue());
				} else {
					resultMsg="失败";
					dbUserModel.setStatus(DbUserStatus.BUILDFAIL.getValue());
				}
			} catch (Exception e) {
				resultMsg="失败";
				detail = e.getMessage();
				dbUserModel.setStatus(DbUserStatus.BUILDFAIL.getValue());
			} finally {
				this.buildResultToMgr("DB数据库("+params.get("dbName")+")用户" + dbUserModel.getUsername() + "修改", resultMsg, detail, ERROR_MAIL_ADDRESS);
				this.dbUserService.updateStatus(dbUserModel);
			}
		}
		
	}
	/**
	 * Methods Name: deleteDbUser <br>
	 * Description: 删除 DbUser<br>
	 * @author name: wujun
	 * @param dbUserId
	 */
	@Override
	@Async
	public void deleteDbUser(String ids){
		String[] str = ids.split(",");	
		String resultMsg = "";
		String detail = "";
		for (String id : str) {
		    DbUserModel dbUserModel = this.dbUserService.selectById(Long.parseLong(id));
		    if(DbStatus.NORMAL.getValue() == dbUserModel.getStatus()) {
		    	Map<String,Object> params = this.dbUserService.selectCreateParams(Long.parseLong(id));
		    	try {
		    		String result = this.pythonService.deleteDbUser(dbUserModel, (String)params.get("dbName"), (String)params.get("nodeIp"), (String)params.get("username"), (String)params.get("password"));
		    		if(analysisResult(transResult(result))) {
		    			resultMsg="用户删除成功";
		    			this.buildResultToUser("DB数据库("+params.get("dbName")+")用户" + dbUserModel.getUsername() + "删除",((BigInteger)params.get("createUser")).longValue());
		    		} else {
		    			resultMsg="用户删除失败";
		    		}
		    	} catch (Exception e) {
		    		detail = e.getMessage();
		    		resultMsg="用户删除失败";
		    	}finally{
		    		this.buildResultToMgr("DB数据库("+params.get("dbName")+")用户" + dbUserModel.getUsername() + "删除", resultMsg, detail, ERROR_MAIL_ADDRESS);
		    	}
		    }
		}
	}
	
	@Override
	public boolean initContainer(MclusterModel mclusterModel,Long dbId) {
		
		boolean nextStep = true;
		List<ContainerModel> containers = this.containerService.selectByMclusterId(mclusterModel.getId());
		
		int step = 2;
		//假设数据
		String username = mclusterModel.getAdminUser();
		String password = mclusterModel.getAdminPassword();
		
		String mclusterName = mclusterModel.getMclusterName();
		
		String nodeIp1 = containers.get(0).getIpAddr();
		String nodeName1 = containers.get(0).getContainerName();
		
		String nodeIp2 = containers.get(1).getIpAddr();
		String nodeName2 = containers.get(1).getContainerName();

		String nodeIp3 = containers.get(2).getIpAddr();
		String nodeName3 = containers.get(2).getContainerName();
		
		String vipNodeIp = containers.get(3).getIpAddr();
		String vipNodeName = containers.get(3).getContainerName();
		
		Long mclusterId = mclusterModel.getId();
		String sstPwd = "";
		/*mcluster-manager测试用集群
		
		物理机
		10.200.91.142
		10.200.91.143
		10.200.91.144
		root/dabingge$1985
		*/
		
		Date startTime = new Date();
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.initZookeeper(nodeIp1)),step,startTime,mclusterId,dbId);
		}
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.initUserAndPwd4Manager(nodeIp1,username,password)),step,startTime,mclusterId,dbId);
		}
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.postMclusterInfo(mclusterName, nodeIp1, nodeName1, username, password)),step,startTime,mclusterId,dbId);
		}
		if(nextStep) {
			step++;
			startTime = new Date();
			Map map = transResult(this.pythonService.initMcluster(nodeIp1, username, password));
			nextStep = analysis(map,step,startTime,mclusterId,dbId);
			if(nextStep) {
				//保存sstPwd，启动启动gbalancer时使用。
				sstPwd = (String) ((Map)map.get("response")).get("sst_user_password");
				MclusterModel mcluster = new MclusterModel();
				mcluster.setId(mclusterId);
				mcluster.setSstPwd(sstPwd);
				this.mclusterService.updateBySelective(mcluster);
			}
		}
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.syncContainer(nodeIp2, username, password)),step,startTime,mclusterId,dbId);
		} 
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.postContainerInfo(nodeIp2, nodeName2, username, password)),step,startTime,mclusterId,dbId);
		} 
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.syncContainer(nodeIp3, username, password)),step,startTime,mclusterId,dbId);
		} 
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.postContainerInfo(nodeIp3, nodeName3, username, password)),step,startTime,mclusterId,dbId);
		} 
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysis(transResult(this.pythonService.startMcluster(nodeIp1, username, password)),step,startTime,mclusterId,dbId);
		} 
		if(nextStep) {
			step++;
			Map<String,Object> result =  transResult(pythonService.checkContainerStatus(nodeIp1, username, password));
			Date checkStartDate = new Date();
			while(!analysisCheckInitResult(result)) {
				try {
					Thread.sleep(PYTHON_INIT_CHECK_INTERVAL_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Date checkDate = new Date();
				long  diff = checkDate.getTime() - checkStartDate.getTime();
				if(diff >PYTHON_INIT_CHECK_TIME) {
					BuildModel nextBuild = new BuildModel();
					nextBuild.setMclusterId(mclusterModel.getId());
					nextBuild.setStep(step);
					nextBuild.setStartTime(new Date());
					nextBuild.setStatus(BuildStatus.FAIL.getValue());
					nextBuild.setMsg("time over check");
					this.buildService.updateByStep(nextBuild);
					mclusterModel.setStatus(MclusterStatus.BUILDFAIL.getValue());
					this.mclusterService.audit(mclusterModel);
					if(dbId!=null) {
						DbModel dbModel = new DbModel();
						dbModel.setId(dbId);
						dbModel.setStatus(DbStatus.BUILDFAIL.getValue());
						this.dbService.updateBySelective(dbModel);
					}
					this.buildResultToMgr("mcluster集群"+mclusterModel.getMclusterName(), "失败", "check init containers time out", ERROR_MAIL_ADDRESS);
					return false;
				}
				result = transResult(pythonService.checkContainerStatus(nodeIp1, username, password));
			}
			nextStep = analysis(result, step, startTime, mclusterModel.getId(), null);
		}
		if(nextStep) {
			step++;
			startTime = new Date();
			StringBuffer ipListPort = new StringBuffer();
			ipListPort.append(nodeIp1).append(":3306,").append(nodeIp2).append(":3306,").append(nodeIp3).append(":3306");
			nextStep = analysis(transResult(this.pythonService.startGbalancer(vipNodeIp, "monitor", sstPwd, ipListPort.toString(), "3306", "–daemon", username, password)),step,startTime,mclusterId,dbId);
		}
		
		if(nextStep) {
			step++;
			startTime = new Date();
			StringBuffer ipListPort = new StringBuffer();
			ipListPort.append(nodeIp1).append(":8888,").append(nodeIp2).append(":8888,").append(nodeIp3).append(":8888");
			nextStep = analysis(transResult(this.pythonService.startGbalancer(vipNodeIp, "monitor", sstPwd, ipListPort.toString(), "8888", "–daemon", username, password)),step,startTime,mclusterId,dbId);
		}
		/**
		 * 固资备案
		 */
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysisToFixedOrZabbix(fixedPushService.createMutilContainerPushFixedInfo(containers),step,startTime,mclusterId,dbId);
		}
		/**
		 * zabbix推送
		 */
		if(nextStep) {
			step++;
			startTime = new Date();
			nextStep = analysisToFixedOrZabbix(zabbixPushService.createMultiContainerPushZabbixInfo(containers),step,startTime,mclusterId,dbId);
		}
		return nextStep;
	}
	
	private boolean analysisToFixedOrZabbix(Boolean sendFlag,int step,Date startTime,Long mclusterId,Long dbId){
		BuildModel buildModel = new BuildModel();
		
		buildModel.setMclusterId(mclusterId);
		buildModel.setDbId(dbId);
		buildModel.setStep(step);
		buildModel.setStartTime(startTime);
		buildModel.setEndTime(new Date());
		boolean flag = true;
		if(sendFlag){
			buildModel.setStatus(BuildStatus.SUCCESS.getValue());
		}else {
			flag =  false;
			buildModel.setStatus(BuildStatus.FAIL.getValue());
			this.buildResultToMgr("mcluster集群", "失败", "固资或者zabbix", ERROR_MAIL_ADDRESS);
			MclusterModel mclusterModel = new MclusterModel();
			mclusterModel.setId(mclusterId);
			mclusterModel.setStatus(MclusterStatus.BUILDFAIL.getValue());
			this.mclusterService.audit(mclusterModel);
		}
		this.buildService.updateByStep(buildModel);
		if(flag) {
			BuildModel nextBuild = new BuildModel();
			nextBuild.setMclusterId(mclusterId);
			nextBuild.setStep(step+1);
			nextBuild.setStartTime(new Date());
			nextBuild.setStatus(BuildStatus.BUILDING.getValue());
			this.buildService.updateByStep(nextBuild);
		}
		return flag;
	}
	
	private boolean analysis(Map<String,Object> jsonResult,int step,Date startTime,Long mclusterId,Long dbId){
		Map<String,Object> meta = (Map)jsonResult.get("meta");
		BuildModel buildModel = new BuildModel();
		
		buildModel.setMclusterId(mclusterId);
		buildModel.setDbId(dbId);
		buildModel.setStep(step);
		buildModel.setStartTime(startTime);
		buildModel.setEndTime(new Date());
		
		boolean flag = true;
		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")))) {
			Map<String,Object> response = (Map)jsonResult.get("response");
			buildModel.setCode((String)response.get("code"));
			buildModel.setMsg((String) response.get("message"));
			buildModel.setStatus(BuildStatus.SUCCESS.getValue());
		} else {
			buildModel.setCode(String.valueOf(meta.get("code")));
			buildModel.setMsg((String)meta.get("errorDetail"));
			buildModel.setStatus(BuildStatus.FAIL.getValue());
			flag =  false;
			this.buildResultToMgr("mcluster集群", "失败", (String)meta.get("errorDetail"), ERROR_MAIL_ADDRESS);
			MclusterModel mclusterModel = new MclusterModel();
			mclusterModel.setId(mclusterId);
			mclusterModel.setStatus(MclusterStatus.BUILDFAIL.getValue());
			this.mclusterService.audit(mclusterModel);
		}
		this.buildService.updateByStep(buildModel);
		if(flag) {
			BuildModel nextBuild = new BuildModel();
			nextBuild.setMclusterId(mclusterId);
			nextBuild.setStep(step+1);
			nextBuild.setStartTime(new Date());
			nextBuild.setStatus(BuildStatus.BUILDING.getValue());
			this.buildService.updateByStep(nextBuild);
		}
		return flag;
	}
	
	private Map<String,Object> transResult(String result){
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String,Object> jsonResult = new HashMap<String,Object>();
		try {
			jsonResult = resultMapper.readValue(result, Map.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	private boolean analysisResult(Map result){
		boolean flag = false;
		Map meta = (Map) result.get("meta");
		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")))) {
			flag = true;
		} 
		return flag;
	}
	private boolean analysisCheckCreateResult(Map result){
		boolean flag = false;
		Map meta = (Map) result.get("meta");
		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")))) {
			if(Constant.PYTHON_API_RESULT_SUCCESS.equals(((Map)result.get("response")).get("code"))) {
				flag = true;
			}
		} 
		return flag;
	}
	private boolean analysisCheckInitResult(Map result){
		boolean flag = false;
		Map meta = (Map) result.get("meta");
		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")))) {
			if(Constant.MCLUSTER_INIT_STATUS_RUNNING.equals(((Map)result.get("response")).get("message"))) {
				flag = true;
			}
		} 
		return flag;
	}
	
	@Override
	public void buildResultToMgr(String buildType,String result,String detail,String to){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buildType", buildType);
		map.put("buildResult", result);
		map.put("errorDetail", detail);
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统", StringUtils.isNullOrEmpty(to)?ERROR_MAIL_ADDRESS:to,"乐视云平台web-portal系统通知","buildForMgr.ftl",map);
		try {
			defaultEmailSender.sendMessage(mailMessage);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	@Override
	public void buildResultToUser(String buildType,Long to){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buildType", buildType);
		UserModel user = this.userService.selectById(to);
		if(null != user) {
			MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",user.getEmail(),"乐视云平台web-portal系统通知","buildForUser.ftl",map);
			try {
				defaultEmailSender.sendMessage(mailMessage);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			
		}
	}
	@Override
	@Async
	public void removeMcluster(MclusterModel mcluster) {
		HostModel host = getHostByHclusterId(mcluster.getHclusterId());
		String result = this.pythonService.removeMcluster(mcluster.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword());
		if(analysisResult(transResult(result))) {
			logger.info("invoke remove mcluster api success");
		} else {
			logger.info("invoke remove mcluster api error");
		}
	}

	@Override
	@Async
	public void startMcluster(MclusterModel mcluster) {
		HostModel host = getHostByHclusterId(mcluster.getHclusterId());
		String result = this.pythonService.startMcluster(mcluster.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword());
		if(analysisResult(transResult(result))) {
			logger.info("invoke start mcluster api success");
		} else {
			logger.error("invoke start mcluster api error");
		}
		
	}

	@Override
	@Async
	public void stopMcluster(MclusterModel mcluster) {
		HostModel host = getHostByHclusterId(mcluster.getHclusterId());
		String result = this.pythonService.stopMcluster(mcluster.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword());
		if(analysisResult(transResult(result))) {
			logger.info("invoke stop mcluster api success");
		} else {
			logger.error("invoke stop mcluster api error");
		}
		
	}

	@Override
	@Async
	public void startContainer(ContainerModel container) {
		HostModel host = this.hostService.selectById(container.getHostId());
		String result = this.pythonService.startContainer(container.getContainerName(),host.getHostIp(),host.getName(),host.getPassword());
		if(analysisResult(transResult(result))) {
			logger.info("invoke start container api success");
		} else {
			logger.error("invoke start container api error");
		}
	}

	@Override
	@Async
	public void stopContainer(ContainerModel container) {
		HostModel host = this.hostService.selectById(container.getHostId());
		String result = this.pythonService.stopContainer(container.getContainerName(),host.getHostIp(),host.getName(),host.getPassword());
		if(analysisResult(transResult(result))) {
			logger.info("invoke start container api success");
		} else {
			logger.error("invoke start container api error");
		}
	}

	@Override
	public void checkMclusterStatus(MclusterModel mcluster) {
		HostModel host = getHostByHclusterId(mcluster.getHclusterId());
		String result = this.pythonService.checkMclusterStatus(mcluster.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword());
		Map map = this.transResult(result);
		Integer status = transStatus((String)((Map)map.get("response")).get("status"));
		mcluster.setStatus(status);
		this.mclusterService.updateBySelective(mcluster);
		if(status == MclusterStatus.NOTEXIT.getValue() && status == MclusterStatus.DESTROYED.getValue()) {
			this.mclusterService.delete(mcluster);
		}
	}

	@Override
	public void checkContainerStatus(ContainerModel container) {
		HostModel host = this.hostService.selectById(container.getHostId());
		String result = this.pythonService.checkContainerStatus(container.getContainerName(),host.getHostIp(),host.getName(),host.getPassword());
		Map map = this.transResult(result);
		Integer status = transStatus((String)((Map)map.get("response")).get("status"));
		container.setStatus(status);
		this.containerService.updateBySelective(container);
		if(status == MclusterStatus.NOTEXIT.getValue() && status == MclusterStatus.DESTROYED.getValue()) {
			this.containerService.delete(container);
		}
	}
	
	public Integer transStatus(String statusStr){
		// { "meta": {"code": 200}, "response": {"status": " starting / started / stopping / stopped / destroying / destroyed / not exist / failed", "message": ""  } }
		Integer status = null;
		if("starting".equals(statusStr)) {
			status = MclusterStatus.STARTING.getValue();
		} else if("started".equals(statusStr)) {
			status = MclusterStatus.RUNNING.getValue();
		} else if("stopping".equals(statusStr)) {
			status = MclusterStatus.STOPPING.getValue();
		} else if("stopped".equals(statusStr)) {
			status = MclusterStatus.STOPED.getValue();
		} else if("destroying".equals(statusStr)) {
			status = MclusterStatus.DESTROYING.getValue();
		} else if("destroyed".equals(statusStr)) {
			status = MclusterStatus.DESTROYED.getValue();
		} else if("not exist".equals(statusStr)) {
			status = MclusterStatus.NOTEXIT.getValue();
		} else if("failed".equals(statusStr)) {
			
		} else if("danger".equals(statusStr)) {
			status = MclusterStatus.DANGER.getValue();
		} else if("crisis".equals(statusStr)) {
			status = MclusterStatus.CRISIS.getValue();
		}
		return status;
	}

	public void createHost(HostModel hostModel){
		if(analysisResult(transResult(pythonService.initHcluster(hostModel.getHostIp())))){
			if(analysisResult(transResult(pythonService.createHost(hostModel))));
			logger.debug("调用phyhonAPI创建host成功");
		}

	}
	/**
	 * Methods Name: createDefalutAmin <br>
	 * Description: 创建默认管理员<br>
	 * @author name: wujun
	 * @return
	 */
	public Long createDefalutAdmin(Long dbId){
		DbUserModel dbUserModel = new DbUserModel();
		dbUserModel.setDbId(dbId);
		dbUserModel.setUsername("admin");
		dbUserModel.setPassword("admin");
		dbUserModel.setAcceptIp("%");
		dbUserModel.setMaxConcurrency(1000);
		dbUserService.insert(dbUserModel);
		Long id = dbUserModel.getId();
		return id;
	}
	
	
	private HostModel getHostByHclusterId(Long hclusterId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("hclusterId", hclusterId);
		map.put("type", HostType.MASTER.getValue());
		return this.hostService.selectByMap(map).get(0);
	}

	@Override
	public void checkMclusterCount() {
		HostModel host = this.hostService.selectByMap(null).get(0);
		String result = this.pythonService.checkMclusterCount(host.getHostIp(),host.getName(),host.getPassword());
		Map map = this.transResult(result);
		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(((Map)map.get("meta")).get("code")))) {
			List<Map> data = (List<Map>) ((Map) map.get("response")).get("data");
			
			for (Map mm : data) {
				String mclusterName = (String) mm.get("clusterName");
				List<MclusterModel> list = this.mclusterService.selectByName(mclusterName);
				if(list.size() <= 0) {
					this.addHandMcluster(mm);
				} else {
					
					List<Map> cms = (List<Map>) mm.get("nodeInfo");
					for (Map cm : cms) {
						ContainerModel container = new ContainerModel();
						container.setContainerName((String) cm.get("containerName"));
						container.setHostIp((String) cm.get("hostIp"));
						HostModel hostModel = this.hostService.selectByIp((String) cm.get("hostIp"));
						if(null != hostModel) {
							container.setHostId(hostModel.getId());
						}
						this.containerService.updateHostIpByName(container);
					}
				}
			}
		}
	}
	
	private void addHandMcluster(Map mm) {
		MclusterModel mcluster = new MclusterModel();
		mcluster.setMclusterName((String) mm.get("clusterName"));
		mcluster.setStatus(MclusterStatus.DEFAULT.getValue());	
		mcluster.setAdminUser("root");
		mcluster.setAdminPassword((String) mm.get("clusterName"));
		mcluster.setType(MclusterType.HAND.getValue());
		mcluster.setHclusterId(ConfigUtil.getlong("default.hcluster.id"));
		mcluster.setDeleted(true);
		this.mclusterService.insert(mcluster);
		List<Map> cms = (List<Map>) mm.get("nodeInfo");
		for (Map cm : cms) {
			ContainerModel container = new ContainerModel();
			try {
				BeanUtils.populate(container, cm);
				container.setMclusterId(mcluster.getId());
				container.setIpMask((String) cm.get("netMask"));
				container.setContainerName((String) cm.get("containerName"));
				container.setStatus(MclusterStatus.RUNNING.getValue());
				container.setHostIp((String) cm.get("hostIp"));
				HostModel hostModel = this.hostService.selectByIp((String) cm.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			this.containerService.insert(container);
		}
	}
	
}
