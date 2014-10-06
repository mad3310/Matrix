package com.letv.portal.python.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.SimpleTextEmailSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.util.ConfigUtil;
import com.letv.portal.constant.Constant;
import com.letv.portal.model.BuildModel;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IBuildService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.letv.portal.service.IMclusterService;
import com.mysql.jdbc.StringUtils;

@Service("buildTaskService")
public class BuildTaskServiceImpl implements IBuildTaskService{
	
	private final static Logger logger = LoggerFactory.getLogger(BuildTaskServiceImpl.class);
	
	private static long PYTHON_CREATE_CHECK_TIME = ConfigUtil.getlong("python_create_check_time"); //300000;//单位：ms
	private static long PYTHON_CHECK_INTERVAL_TIME = ConfigUtil.getlong("python_check_interval_time");// 2000;//单位：ms
	
	private static long PYTHON_CREATE_INTERVAL_INIT_TIME = ConfigUtil.getlong("python_create_interval_init_time");//60000;//单位：ms
	
	private static long PYTHON_INIT_CHECK_TIME = ConfigUtil.getlong("python_init_check_time");//600000;//单位：ms
	private static long PYTHON_INIT_CHECK_INTERVAL_TIME = ConfigUtil.getlong("python_init_check_interval_time");//5000;//单位：ms
	
	@Resource
	private IMclusterService mclusterService;
	@Resource
	private IDbUserService dbUserService;
	@Resource
	private IPythonService pythonService;
	@Resource
	private IDbService dbService;
	@Resource
	private IContainerService containerService;
	@Resource
	private IBuildService buildService;
	
	@Value("${error.email.to}")
	private String ERROR_MAIL_ADDRESS;
	
	@Resource
	private SimpleTextEmailSender simpleTextEmailSender;
	
	@Value("${error.email.enabled}")
	private Boolean ERROR_MAIL_ENABLED;
	
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	HashMap mclusterHashMap = new HashMap();
	HashMap DbHashMap = new HashMap();
	
	@Override
	public void buildMcluster(MclusterModel mclusterModel,Long dbId) {
		boolean nextStep = true;
		
		mclusterModel.setStatus(Constant.STATUS_BUILDDING);
		mclusterModel.setAdminUser(mclusterModel.getMclusterName());
		mclusterModel.setAdminPassword(mclusterModel.getMclusterName());
		
		this.mclusterService.insert(mclusterModel);
		
		this.buildService.initStatus(mclusterModel.getId());
		mclusterHashMap.put("id", mclusterModel.getId());
		try {
			if(nextStep) {
				nextStep = createContainer(mclusterModel,dbId);
			}
			//remove python_create_interval_init_time
//			Thread.sleep(PYTHON_CREATE_INTERVAL_INIT_TIME);
			if(nextStep) {
				nextStep = initContainer(mclusterModel,dbId);
			}
			if(nextStep) {
				mclusterHashMap.put("status", Constant.STATUS_OK);
				this.mclusterService.audit(mclusterHashMap);
			}
		} catch (Exception e) {
			BuildModel nextBuild = new BuildModel();
			nextBuild.setMclusterId(mclusterModel.getId());
			nextBuild.setStartTime(new Date());
			nextBuild.setStatus("fail");
			nextBuild.setMsg(e.getMessage());
			this.buildService.updateStatusFail(nextBuild);
			mclusterHashMap.put("status", Constant.STATUS_BUILD_FAIL);
			this.mclusterService.audit(mclusterHashMap);
			if(dbId!=null) {
				DbModel dbModel = new DbModel();
				dbModel.setId(dbId);
				dbModel.setStatus(Constant.STATUS_BUILD_FAIL);
				this.dbService.updateBySelective(dbModel);
			}
			this.buildResultToMgr("mcluster集群", "失败", e.getMessage(), ERROR_MAIL_ADDRESS);
			return;
		}
		if(nextStep && dbId != null) {
			this.buildDb(dbId);
		}
		
	}
	@Override
	public boolean createContainer(MclusterModel mclusterModel,Long dbId) {
		boolean nextStep = true;
		
		int step = 0;
		Date startTime = new Date();
		
		if(nextStep) {
			step++;
			nextStep = this.analysis(transResult(this.pythonService.createContainer(mclusterModel.getMclusterName())), step, startTime, mclusterModel.getId(), dbId);
		}
		if(nextStep) {
			step++;
			Map<String,Object> result =  transResult(pythonService.checkContainerCreateStatus(mclusterModel.getMclusterName()));
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
					nextBuild.setStatus("fail");
					nextBuild.setMsg("time over check");
					this.buildService.updateBySelective(nextBuild);
					mclusterHashMap.put("id", mclusterModel.getId());
					mclusterHashMap.put("status", Constant.STATUS_BUILD_FAIL);
					this.mclusterService.audit(mclusterHashMap);
					this.buildResultToMgr("mcluster集群", "失败", "check create containers time out", ERROR_MAIL_ADDRESS);
					return false;
				}
				result = transResult(pythonService.checkContainerCreateStatus(mclusterModel.getMclusterName()));
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
					container.setContainerName((String) map.get("containerClusterName"));
					container.setHostId((Long)map.get("hostIp"));
				}catch (Exception e) {
					e.printStackTrace();
				}
				this.containerService.insert(container);
			}
		}
		return nextStep;
	}
	
	@Override
	public void buildDb(Long dbId) {
		Integer status = null;
		String resultMsg = "";
		Map<String,String> params = this.dbService.selectCreateParams(dbId);
		try {
			String result = this.pythonService.createDb(params.get("nodeIp"), params.get("dbName"), params.get("dbName"), null, params.get("username"), params.get("password"));
			
			if(analysisResult(transResult(result))) {
				resultMsg = "成功";
				status = Constant.STATUS_OK;
				this.buildResultToUser("DB数据库", params.get("createUser"));
			} else {
				resultMsg = "失败";
				status = Constant.STATUS_BUILD_FAIL;
			}
		} catch (Exception e) {
			resultMsg = "失败";
			status = Constant.STATUS_BUILD_FAIL;
		} finally {
			this.buildResultToMgr("DB数据库", resultMsg, null, ERROR_MAIL_ADDRESS);
			DbHashMap.put("id", dbId);
			DbHashMap.put("status", status);
			this.dbService.updateByMap(DbHashMap);
		}
	}


	@Override
	public void buildUser(String ids) {
		String[] str = ids.split(",");
		String resultMsg = "";
		for (String id : str) {
			//查询所属db 所属mcluster 及container数据
			DbUserModel dbUserModel = this.dbUserService.selectById(Long.parseLong(id));
			try {
				Map<String,String> params = this.dbUserService.selectCreateParams(Long.parseLong(id));
				String result = this.pythonService.createDbUser(dbUserModel, params.get("dbName"), params.get("nodeIp"), params.get("username"), params.get("password"));
				if(analysisResult(transResult(result))) {
					resultMsg="成功";
					dbUserModel.setStatus(Constant.STATUS_OK);
					this.buildResultToUser("DB数据库用户", params.get("createUser"));
				} else {
					resultMsg="失败";
					dbUserModel.setStatus(Constant.STATUS_BUILD_FAIL);
				}
			} catch (Exception e) {
				resultMsg="失败";
				dbUserModel.setStatus(Constant.STATUS_BUILD_FAIL);
			} finally {
				this.buildResultToMgr("DB数据库用户", resultMsg, null, ERROR_MAIL_ADDRESS);
				this.dbUserService.updateStatus(dbUserModel);
			}
		}
		
	}
	
	@Override
	public boolean initContainer(MclusterModel mclusterModel,Long dbId) {
		
		boolean nextStep = true;
		List<ContainerModel> containers = this.containerService.selectByClusterId(mclusterModel.getId());
		
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
		
		Long mclusterId = mclusterModel.getId();
		
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
			nextStep = analysis(transResult(this.pythonService.initMcluster(nodeIp1, username, password)),step,startTime,mclusterId,dbId);
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
					nextBuild.setStatus("fail");
					nextBuild.setMsg("time over check");
					this.buildService.updateBySelective(nextBuild);
					mclusterHashMap.put("id", mclusterModel.getId());
					mclusterHashMap.put("status", Constant.STATUS_BUILD_FAIL);
					this.mclusterService.audit(mclusterHashMap);
					this.buildResultToMgr("mcluster集群", "失败", "check init containers time out", ERROR_MAIL_ADDRESS);
					return false;
				}
				result = transResult(pythonService.checkContainerStatus(nodeIp1, username, password));
			}
			nextStep = analysis(result, step, startTime, mclusterModel.getId(), null);
		}
		return nextStep;
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
			buildModel.setStatus("success");
		} else {
			buildModel.setCode(String.valueOf(meta.get("code")));
			buildModel.setMsg((String)meta.get("errorDetail"));
			buildModel.setStatus("fail");
			flag =  false;
			this.buildResultToMgr("mcluster集群", "失败", (String)meta.get("errorDetail"), ERROR_MAIL_ADDRESS);
			mclusterHashMap.put("id", mclusterId);
			mclusterHashMap.put("status", Constant.STATUS_BUILD_FAIL);
			this.mclusterService.audit(mclusterHashMap);
		}
		this.buildService.updateBySelective(buildModel);
		if(flag) {
			BuildModel nextBuild = new BuildModel();
			nextBuild.setMclusterId(mclusterId);
			nextBuild.setStep(step+1);
			nextBuild.setStartTime(new Date());
			nextBuild.setStatus("building");
			this.buildService.updateBySelective(nextBuild);
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
	public void buildResultToUser(String buildType,String to){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buildType", buildType);
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",to,"乐视云平台web-portal系统通知","buildForUser.ftl",map);
		try {
			defaultEmailSender.sendMessage(mailMessage);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
