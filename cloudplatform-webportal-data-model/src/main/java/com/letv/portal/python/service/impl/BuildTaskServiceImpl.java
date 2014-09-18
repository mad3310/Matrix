package com.letv.portal.python.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
	
	private static long PYTHON_CREATE_CHECK_TIME = 300;//ConfigUtil.getint("python_check_time");
	private static long PYTHON_INIT_CHECK_TIME = 600;//ConfigUtil.getint("python_check_time");
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
	
	
	@Override
	public void buildMcluster(MclusterModel mclusterModel,String dbId) {
		boolean nextStep = true;
		mclusterModel.setId(UUID.randomUUID().toString());
		
		mclusterModel.setStatus(Constant.STATUS_BUILDDING);
		mclusterModel.setAdminUser(mclusterModel.getMclusterName());
		mclusterModel.setAdminPassword(mclusterModel.getMclusterName());
		
		this.mclusterService.insert(mclusterModel);
		
		this.buildService.initStatus(mclusterModel.getId());
		
		try {
			if(nextStep) {
				nextStep = createContainer(mclusterModel,dbId);
			}
			Thread.sleep(300000);
			if(nextStep) {
				nextStep = initContainer(mclusterModel,dbId);
			}
			if(nextStep) {
				this.mclusterService.audit(new MclusterModel(mclusterModel.getId(),Constant.STATUS_OK));
			}
		} catch (Exception e) {
			BuildModel nextBuild = new BuildModel();
			nextBuild.setMclusterId(mclusterModel.getId());
			nextBuild.setStartTime(new Date());
			nextBuild.setStatus("fail");
			nextBuild.setMsg(e.getMessage());
			this.buildService.updateStatusFail(nextBuild);
			this.mclusterService.audit(new MclusterModel(mclusterModel.getId(),Constant.STATUS_BUILD_FAIL));
			if(!StringUtils.isNullOrEmpty(dbId)) {
				this.dbService.updateBySelective(new DbModel(dbId,Constant.STATUS_BUILD_FAIL));
			}
			return;
		}
		if(nextStep || !StringUtils.isNullOrEmpty(dbId)) {
			this.buildDb(dbId);
		}
		
	}
	@Override
	public boolean createContainer(MclusterModel mclusterModel,String dbId) {
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
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Date checkDate = new Date();
				long  diff = checkDate.getTime() - checkStartDate.getTime();
				long time = diff/1000;
				if(time >PYTHON_CREATE_CHECK_TIME) {
					BuildModel nextBuild = new BuildModel();
					nextBuild.setMclusterId(mclusterModel.getId());
					nextBuild.setStep(step);
					nextBuild.setStartTime(new Date());
					nextBuild.setStatus("fail");
					nextBuild.setMsg("time over check");
					this.buildService.updateBySelective(nextBuild);
					this.mclusterService.audit(new MclusterModel(mclusterModel.getId(),Constant.STATUS_BUILD_FAIL));
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
					container.setClusterId(mclusterModel.getId());
					container.setIpMask((String) map.get("netMask"));
					container.setContainerName((String) map.get("containerClusterName"));
					container.setClusterNodeName((String)map.get("containerName"));
				}catch (Exception e) {
					e.printStackTrace();
				}
				this.containerService.insert(container);
			}
		}
		return nextStep;
	}
	
	@Override
	public void buildDb(String dbId) {
		String status = "";
		Map<String,String> params = this.dbService.selectCreateParams(dbId);
		try {
			String result = this.pythonService.createDb(params.get("nodeIp"), params.get("dbName"), params.get("dbName"), null, params.get("username"), params.get("password"));
			
			if(analysisResult(transResult(result))) {
				status = Constant.STATUS_OK;
			} else {
				status = Constant.STATUS_BUILD_FAIL;
			}
		} catch (Exception e) {
			status = Constant.STATUS_BUILD_FAIL;
		} finally {
			//保存用户创建成功状态
			this.dbService.updateBySelective(new DbModel(dbId,status));
		}
	}


	@Override
	public void buildUser(String ids) {
		String[] str = ids.split(",");
		for (String id : str) {
			//查询所属db 所属mcluster 及container数据
			DbUserModel dbUserModel = this.dbUserService.selectById(id);
			try {
				Map<String,String> params = this.dbUserService.selectCreateParams(id);
				String result = this.pythonService.createDbUser(dbUserModel, params.get("dbName"), params.get("nodeIp"), params.get("username"), params.get("password"));
				if(analysisResult(transResult(result))) {
					dbUserModel.setStatus(Constant.STATUS_OK);
				} else {
					dbUserModel.setStatus(Constant.STATUS_BUILD_FAIL);
				}
			} catch (Exception e) {
				dbUserModel.setStatus(Constant.STATUS_BUILD_FAIL);
			} finally {
				//保存用户创建成功状态
				this.dbUserService.updateStatus(dbUserModel);
			}
		}
		
	}
	
	@Override
	public boolean initContainer(MclusterModel mclusterModel,String dbId) {
		
		boolean nextStep = true;
		List<ContainerModel> containers = this.containerService.selectByClusterId(mclusterModel.getId());
		
		int step = 2;
		//假设数据
		String username = mclusterModel.getAdminUser();
		String password = mclusterModel.getAdminPassword();
		
		String mclusterName = mclusterModel.getMclusterName();
		
		String nodeIp1 = containers.get(0).getIpAddr();
		String nodeName1 = containers.get(0).getClusterNodeName();
		
		String nodeIp2 = containers.get(1).getIpAddr();
		String nodeName2 = containers.get(1).getClusterNodeName();

		String nodeIp3 = containers.get(2).getIpAddr();
		String nodeName3 = containers.get(2).getClusterNodeName();
		
		String mclusterId = mclusterModel.getId();
		
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
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Date checkDate = new Date();
				long  diff = checkDate.getTime() - checkStartDate.getTime();
				long time = diff/1000;
				if(time >PYTHON_INIT_CHECK_TIME) {
					BuildModel nextBuild = new BuildModel();
					nextBuild.setMclusterId(mclusterModel.getId());
					nextBuild.setStep(step);
					nextBuild.setStartTime(new Date());
					nextBuild.setStatus("fail");
					nextBuild.setMsg("time over check");
					this.buildService.updateBySelective(nextBuild);
					this.mclusterService.audit(new MclusterModel(mclusterModel.getId(),Constant.STATUS_BUILD_FAIL));
					return false;
					
				}
				result = transResult(pythonService.checkContainerStatus(nodeIp1, username, password));
			}
			nextStep = analysis(result, step, startTime, mclusterModel.getId(), null);
		}
		return nextStep;
	}
	
	private boolean analysis(Map<String,Object> jsonResult,int step,Date startTime,String mclusterId,String dbId){
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
			
			this.mclusterService.audit(new MclusterModel(mclusterId,Constant.STATUS_BUILD_FAIL));
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
	
}
