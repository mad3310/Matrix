package com.letv.portal.proxy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.util.CommonServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.exception.TaskExecuteException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.SlbBackEndType;
import com.letv.portal.enumeration.SlbBackendStatus;
import com.letv.portal.enumeration.SlbStatus;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.slb.SlbBackendServer;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbConfig;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.model.task.service.ITaskEngine;
import com.letv.portal.proxy.ISlbProxy;
import com.letv.portal.python.service.ISlbPythonService;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.slb.ISlbBackendServerService;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbConfigService;
import com.letv.portal.service.slb.ISlbContainerService;
import com.letv.portal.service.slb.ISlbServerService;

@Component
public class SlbProxyImpl extends BaseProxyImpl<SlbServer> implements
		ISlbProxy{
	
	private final static Logger logger = LoggerFactory.getLogger(SlbProxyImpl.class);
	
	@Autowired
	private ISlbServerService slbServerService;
	@Autowired
	private ISlbClusterService slbClusterService;
	@Autowired
	private IGceServerService gceServerService;
	@Autowired
	private ISlbContainerService slbContainerService;
	@Autowired
	private IGceContainerService gceContainerService;
	@Autowired
	private ISlbPythonService slbPythonService;
    @Autowired
    private IPythonService pythonService;
	@Autowired
	private ISlbConfigService slbConfigService;
	@Autowired
	private ISlbBackendServerService slbBackendServerService;
	@Autowired
	private IBaseTaskService baseSlbTaskService;
	@Autowired
	private ITaskEngine taskEngine;
    @Autowired
    private IHostService hostService;

	@Value("${db.auto.build.count}")
	private int DB_AUTO_BUILD_COUNT;

	@Override
	public void saveAndBuild(SlbServer slbServer) {
		Map<String,Object> params = this.slbServerService.save(slbServer);
		this.build(params);
	}

	private void build(Map<String,Object> params) {
    	this.taskEngine.run("SLB_BUY",params);
	}
	
	@Override
	public IBaseService<SlbServer> getService() {
		return slbServerService;
	}

	@Override
	@Async
	public void restart(Long id) {
		SlbServer slb = this.selectById(id);
		slb.setStatus(SlbStatus.STARTING.getValue());
		this.slbServerService.updateBySelective(slb);
		
		SlbCluster cluster = this.slbClusterService.selectById(slb.getSlbClusterId());
		List<SlbContainer> containers = this.slbContainerService.selectBySlbClusterId(cluster.getId());
		
		this.commitProxyConfig(slb,cluster,containers);
		this.restart(slb,cluster,containers);
		this.checkStatus(slb, cluster, containers,"STARTED","SLB服务重启失败");
	}
	@Override
	@Async
	public void start(Long id) {
		SlbServer slb = this.selectById(id);
		slb.setStatus(SlbStatus.STARTING.getValue());
		this.slbServerService.updateBySelective(slb);
		
		SlbCluster cluster = this.slbClusterService.selectById(slb.getSlbClusterId());
		List<SlbContainer> containers = this.slbContainerService.selectBySlbClusterId(cluster.getId());
		this.commitProxyConfig(slb,cluster,containers);
		this.start(slb,cluster,containers);
		this.checkStatus(slb, cluster, containers,"STARTED","SLB服务启动失败");
	}

	@Override
	@Async
	public void stop(Long id) {
		SlbServer slb = this.selectById(id);
		slb.setStatus(SlbStatus.STOPPING.getValue());
		this.slbServerService.updateBySelective(slb);
		
		SlbCluster cluster = this.slbClusterService.selectById(slb.getSlbClusterId());
		List<SlbContainer> containers = this.slbContainerService.selectBySlbClusterId(cluster.getId());
		
		this.stop(slb,cluster,containers);
		this.checkStatus(slb, cluster, containers,"STOP","SLB服务停止失败");
	}
	
	@Override
	@Async
	public void delete(SlbServer slb) {
		this.slbServerService.delete(slb);
	}
	
	private boolean commitProxyConfig(SlbServer slb,SlbCluster cluster,List<SlbContainer> containers) {
		List<Map<String,String>> params = getProxyConfig(slb);
		
		TaskResult tr = new TaskResult();
		for (int i = 0; i < containers.size(); i++) {
			SlbContainer container = containers.get(i);
			for (Map<String,String> param : params) {
				if(i > 0)
					param.put("state", "BACKUP");
				ApiResultObject result = this.slbPythonService.commitProxyConfig(param, container.getIpAddr(), cluster.getAdminUser(), cluster.getAdminPassword());
				tr = this.baseSlbTaskService.analyzeRestServiceResult(result);
				if(!tr.isSuccess()) {
					slb.setStatus(SlbStatus.ABNORMAL.getValue());
					this.slbServerService.updateBySelective(slb);
					throw new TaskExecuteException("SLB service commit porxyConfig error:" + tr.getResult());
				}
			}
		}
		return tr.isSuccess();
	}
	private boolean restart(SlbServer slb,SlbCluster cluster,List<SlbContainer> containers) {
		ApiResultObject result = this.slbPythonService.restart(null,containers.get(0).getIpAddr(), cluster.getAdminUser(), cluster.getAdminPassword());
		TaskResult tr = this.baseSlbTaskService.analyzeRestServiceResult(result);
		if(!tr.isSuccess()) {
			slb.setStatus(SlbStatus.ABNORMAL.getValue());
			this.slbServerService.updateBySelective(slb);
			throw new TaskExecuteException("SLB service restart error:" + tr.getResult());
		}
		return tr.isSuccess();
	}
	private boolean stop(SlbServer slb,SlbCluster cluster,List<SlbContainer> containers) {
		ApiResultObject result = this.slbPythonService.stop(null,containers.get(0).getIpAddr(), cluster.getAdminUser(), cluster.getAdminPassword());
		TaskResult tr = this.baseSlbTaskService.analyzeRestServiceResult(result);
		if(!tr.isSuccess()) {
			slb.setStatus(SlbStatus.ABNORMAL.getValue());
			this.slbServerService.updateBySelective(slb);
			throw new TaskExecuteException("SLB service stop error:" + tr.getResult());
		}
		return tr.isSuccess();
	}
	private boolean start(SlbServer slb,SlbCluster cluster,List<SlbContainer> containers) {
		ApiResultObject result = this.slbPythonService.start(null,containers.get(0).getIpAddr(), cluster.getAdminUser(), cluster.getAdminPassword());
		TaskResult tr = this.baseSlbTaskService.analyzeRestServiceResult(result);
		if(!tr.isSuccess()) {
			slb.setStatus(SlbStatus.ABNORMAL.getValue());
			this.slbServerService.updateBySelective(slb);
			throw new TaskExecuteException("SLB service start error:" + tr.getResult());
		}
		return tr.isSuccess();
	}
	private String checkStatus(SlbServer slb,SlbCluster cluster,List<SlbContainer> containers) {
		 TaskResult tr = new TaskResult();
		 ApiResultObject result = this.slbPythonService.checkStatus(containers.get(0).getIpAddr(), cluster.getAdminUser(), cluster.getAdminPassword());
		tr = this.baseSlbTaskService.analyzeRestServiceResult(result);
		if(!tr.isSuccess()) {
			slb.setStatus(SlbStatus.ABNORMAL.getValue());
			this.slbServerService.updateBySelective(slb);
			throw new TaskExecuteException("SLB service check start error:" + tr.getResult());
		}
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		return (String) ((Map<String,Object>)params.get("data")).get("status");
	}
	private void checkStatus(SlbServer slb,SlbCluster cluster,List<SlbContainer> containers,String expectStatus,String exception) {
		String status = "";
		for (int i = 0; i < 3; i++) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			status = this.checkStatus(slb,cluster,containers);
			if(expectStatus.equals(status))
				break;
		}
		if("".equals(status))
			throw new TaskExecuteException(exception);
		if("STARTED".equals(status))
			slb.setStatus(SlbStatus.NORMAL.getValue());
		if("STOP".equals(status))
			slb.setStatus(SlbStatus.STOPED.getValue());
		this.slbServerService.updateBySelective(slb);
	}
	
	private List<Map<String,String>> getProxyConfig(SlbServer slb) {
		List<SlbConfig> configs = this.slbConfigService.selectBySlbServerId(slb.getId());
		
		List<Map<String,String>> params = new ArrayList<Map<String,String>>();
		List<SlbBackendServer> backendServers = null;
		
		for (SlbConfig config : configs) {
			backendServers = this.slbBackendServerService.selectBySlbConfigId(config.getId());
			if(backendServers !=null && !backendServers.isEmpty()) {
				Map<String,String> param = new HashMap<String,String>();
				param.put("service", config.getAgentType().toString().toLowerCase());
				param.put("port", config.getFrontPort());
				param.put("vip", slb.getIp());
				param.put("addr", "0.0.0.0");
				param.put("state", "MASTER");
				StringBuffer sb = new StringBuffer();
				for (SlbBackendServer backendServer : backendServers) {
					if(SlbBackEndType.CUSTOM.equals(backendServer.getType())) {
						sb.append(backendServer.getServerIp()).append(":").append(backendServer.getPort()).append(",");
						
					} else {
						GceServer gceServer = gceServerService.selectGceAndProxyById(backendServer.getGceId());
						Long gceClusterId = gceServer.getGceServerProxy()==null?gceServer.getGceClusterId():gceServer.getGceServerProxy().getGceClusterId();
						List<GceContainer> gceContainers = this.gceContainerService.selectByGceClusterId(gceClusterId);
						String port = "8001";
						if(SlbBackEndType.RDS.equals(backendServer.getType())) {
							port="3306";
						}
						
						for (GceContainer gceContainer : gceContainers) {
							if("jetty".equals(gceContainer.getType()))
								port="8080";
							sb.append(gceContainer.getIpAddr()).append(":").append(port).append(",");
						}
					}
					backendServer.setStatus(SlbBackendStatus.WORK.getValue());
					slbBackendServerService.updateBySelective(backendServer);
				}
				param.put("iplist_port", sb.length()>0?sb.substring(0, sb.length()-1):sb.toString());
				params.add(param);
			}
		}
		return params;
	}

	@Override
	@Async
	public void checkStatus() {
		List<SlbCluster> list = this.slbClusterService.selectByMap(null);
		for (SlbCluster cluster : list) {
			if(MclusterStatus.BUILDDING.getValue() == cluster.getStatus() || MclusterStatus.BUILDFAIL.getValue() == cluster.getStatus())
				continue;
			this.checkSlbClusterStatus(cluster);
		}
		list.clear();
	}

	private void checkSlbClusterStatus(SlbCluster cluster) {
		HostModel host = this.hostService.getHostByHclusterId(cluster.getHclusterId());
		if(null == host) {
			cluster.setStatus(MclusterStatus.CRISIS.getValue());
			this.slbClusterService.updateBySelective(cluster);
			return;
		}
		ApiResultObject result = this.pythonService.checkMclusterStatus(cluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		Map map = CommonServiceUtils.transResult(result.getResult());
		if(map.isEmpty()) {
			cluster.setStatus(MclusterStatus.CRISIS.getValue());
			this.slbClusterService.updateBySelective(cluster);
			return;
		}

		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(((Map)map.get("meta")).get("code")))) {
			Integer status = CommonServiceUtils.transStatus((String) ((Map) map.get("response")).get("status"));
			cluster.setStatus(status);
			this.slbClusterService.updateBySelective(cluster);
			if(status == MclusterStatus.NOTEXIT.getValue() || status == MclusterStatus.DESTROYED.getValue()) {
				this.slbClusterService.delete(cluster);
			}
			return;
		}

		if(null !=result.getResult() && result.getResult().contains("not existed")){
			this.slbClusterService.delete(cluster);
			return;
		}

		cluster.setStatus(MclusterStatus.CRISIS.getValue());
		this.slbClusterService.updateBySelective(cluster);
	}
	
	
}
