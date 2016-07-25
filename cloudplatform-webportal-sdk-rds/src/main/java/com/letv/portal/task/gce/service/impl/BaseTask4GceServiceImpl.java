package com.letv.portal.task.gce.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.log.LogCluster;
import com.letv.portal.model.log.LogContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.common.IZookeeperInfoService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.log.ILogClusterService;
import com.letv.portal.service.log.ILogContainerService;

@Component("baseGceTaskService")
public class BaseTask4GceServiceImpl extends BaseTaskServiceImpl implements IBaseTaskService{

	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Autowired
	private IHostService hostService;
	@Autowired
	private IGceClusterService gceClusterService;
	@Autowired
	private IGceServerService gceServerService;
	@Autowired
	private IGceContainerService gceContainerService;
	@Autowired
	private ILogClusterService logClusterService;
	@Autowired
	private ILogContainerService logContainerService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IZookeeperInfoService zookeeperInfoService;
	
	
	
	
	private final static Logger logger = LoggerFactory.getLogger(BaseTask4GceServiceImpl.class);
	
	@Override
	public void beforExecute(Map<String, Object> params) {
		GceServer gce = this.getGceServer(params);
		GceCluster cluster = this.getGceCluster(params);
		if(gce.getStatus() != DbStatus.BUILDDING.getValue()) {
			gce.setStatus(DbStatus.BUILDDING.getValue());
			this.gceServerService.updateBySelective(gce);
		}
		if(gce.getStatus() != MclusterStatus.BUILDDING.getValue()) {
			cluster.setStatus(MclusterStatus.BUILDDING.getValue());
			this.gceClusterService.updateBySelective(cluster);
		}
	}
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = new TaskResult();
		if(params == null || params.isEmpty()) {
			tr.setResult("params is empty");
			tr.setSuccess(false);
		}
		tr.setParams(params);
		return tr;
	}

	@Override
	public void rollBack(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		boolean isContinue = (Boolean) params.get("isContinue");
		//发送邮件
		String serverName =  (String) params.get("serviceName");
		this.buildResultToMgr("Gce服务("+serverName+")创建", tr.isSuccess()?"成功":"失败", tr.getResult(), SERVICE_NOTICE_MAIL_ADDRESS);
		//业务处理
		this.serviceOver(tr);
	}
	
	private void serviceOver(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
		GceServer gce = this.getGceServer(params);
		GceCluster cluster = this.getGceCluster(params);
		
		if(tr.isSuccess()) {
			gce.setStatus(DbStatus.NORMAL.getValue());
			cluster.setStatus(MclusterStatus.RUNNING.getValue());
			Map<String, Object> emailParams = new HashMap<String,Object>();
			emailParams.put("gceName", gce.getGceName());
			this.email4User(emailParams, gce.getCreateUser(),"gce/createGce.ftl");
		} else {
			gce.setStatus(DbStatus.BUILDFAIL.getValue());
			cluster.setStatus(MclusterStatus.BUILDFAIL.getValue());
			
			Map<String,Object> nextParams = (Map<String, Object>) params.get("nextParams");
			if(null != nextParams && !nextParams.isEmpty()) {
				GceServer nextGce = this.getGceServer(nextParams);
				GceCluster nextCluster = this.getGceCluster(nextParams);
				nextGce.setStatus(DbStatus.BUILDFAIL.getValue());
				nextCluster.setStatus(MclusterStatus.BUILDFAIL.getValue());
				this.gceServerService.updateBySelective(nextGce);
				this.gceClusterService.updateBySelective(nextCluster);
			}
		}
		this.gceServerService.updateBySelective(gce);
		this.gceClusterService.updateBySelective(cluster);
	}

	@Override
	public void callBack(TaskResult tr) {
		
	}
	
	public GceServer getGceServer(Map<String, Object> params) {
		Long gceId = getLongFromObject(params.get("gceId"));
		if(gceId == null)
			throw new ValidateException("params's gceId is null");
		
		GceServer gceServer = this.gceServerService.selectById(gceId);
		if(gceServer == null)
			throw new ValidateException("gceServer is null by gceId:" + gceId);
		
		return gceServer;
	}
	
	public GceCluster getGceCluster(Map<String, Object> params) {
		Long gceClusterId = getLongFromObject(params.get("gceClusterId"));
		if(gceClusterId == null)
			throw new ValidateException("params's gceClusterId is null");
		
		GceCluster gceCluster = this.gceClusterService.selectById(gceClusterId);
		if(gceCluster == null)
			throw new ValidateException("gceCluster is null by gceClusterId:" + gceClusterId);
		
		return gceCluster;
	}
	public LogCluster getLogCluster(Map<String, Object> params) {
		Map<String, Object> logParams = (Map<String, Object>) params.get("logParams");
		Long logClusterId = getLongFromObject(logParams.get("logClusterId"));
		if(logClusterId == null)
			throw new ValidateException("params's logClusterId is null");
		
		LogCluster logCluster = this.logClusterService.selectById(logClusterId);
		if(logCluster == null)
			throw new ValidateException("logCluster is null by logClusterId:" + logClusterId);
		
		return logCluster;
	}
	
	public HostModel getHost(Long hclusterId) {
		if(hclusterId == null)
			throw new ValidateException("hclusterId is null :" + hclusterId);
		HostModel host = this.hostService.getHostByHclusterId(hclusterId);
		if(host == null)
			throw new ValidateException("host is null by hclusterIdId:" + hclusterId);
		
		return host;
	}
	public List<GceContainer> getContainers(Map<String, Object> params) {
		Long gceClusterId = getLongFromObject(params.get("gceClusterId"));
		if(gceClusterId == null)
			throw new ValidateException("params's gceClusterId is null");
		
		List<GceContainer> gceContainers = this.gceContainerService.selectByGceClusterId(gceClusterId);
		if(gceContainers == null || gceContainers.isEmpty())
			throw new ValidateException("gceCluster is null by gceClusterId:" + gceClusterId);
		return gceContainers;
	}
	public List<LogContainer> getLogContainers(Map<String, Object> params) {
		Map<String, Object> logParams = (Map<String, Object>) params.get("logParams");
		Long logClusterId = getLongFromObject(logParams.get("logClusterId"));
		if(logClusterId == null)
			throw new ValidateException("params's logClusterId is null");
		
		List<LogContainer> logContainers = this.logContainerService.selectByLogClusterId(logClusterId);
		if(logContainers == null || logContainers.isEmpty())
			throw new ValidateException("gceCluster is null by logClusterId:" + logClusterId);
		return logContainers;
	}
	
	public ApiParam getApiParam(GceContainer container,ManageType type,String bindPort) {
		
		String ip = "";
		String port = "";
		
		String ipAddr = container.getIpAddr();
		if(ipAddr.startsWith("10.")) {
			ip = ipAddr;
			port = String.valueOf(type.getValue());
		} else {
			ip = container.getHostIp();
			port = bindPort;
		}
		return new ApiParam(ip,port);
	}

}