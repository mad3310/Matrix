package com.letv.portal.task.gce.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.letv.common.util.StringUtil;
import com.letv.portal.fixedPush.IFixedPushService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceContainerExt;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.gce.IGceContainerExtService;
import com.letv.portal.service.gce.IGceContainerService;

@Service("taskGceClusterCheckStatusService")
public class TaskGceClusterCheckStatusServiceImpl extends BaseTask4GceServiceImpl implements IBaseTaskService{

	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IGceContainerService gceContainerService;
	@Autowired
	private IGceContainerExtService gceContainerExtService;
    @Autowired
    private IFixedPushService fixedPushService;


    private final static long PYTHON_CREATE_CHECK_TIME = 300000;
	private final static long PYTHON_CHECK_INTERVAL_TIME = 3000;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskGceClusterCheckStatusServiceImpl.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		GceCluster gceCluster = super.getGceCluster(params);
		HostModel host = super.getHost(gceCluster.getHclusterId());
		
		ApiResultObject resultObject  = gcePythonService.checkContainerCreateStatus(gceCluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(resultObject);
		
		Long start = new Date().getTime();
		while(!tr.isSuccess()) {
			Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
			if(new Date().getTime()-start >PYTHON_CREATE_CHECK_TIME) {
				tr.setResult("check time over:"+resultObject.getUrl());
				break;
			}
			resultObject = gcePythonService.checkContainerCreateStatus(gceCluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
			tr = analyzeRestServiceResult(resultObject);
		}
		if(tr.isSuccess()) {
			List<Map> containers = (List<Map>)((Map)transToMap(resultObject.getResult()).get("response")).get("containers");
			for (Map map : containers) {
				GceContainer container = new GceContainer();
				BeanUtils.populate(container, map);
				container.setGceClusterId(gceCluster.getId());
				container.setIpMask((String) map.get("netMask"));
				container.setStatus(MclusterStatus.RUNNING.getValue());
				//物理机集群维护完成后，修改此处，需要关联物理机id
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				
				if(!container.getIpAddr().startsWith("10.")) {
					
					List<Map> portBindings = (List<Map>) map.get("port_bindings");
					StringBuffer hostPort = new StringBuffer();
					StringBuffer containerPort = new StringBuffer();
					StringBuffer protocol = new StringBuffer();
					
					for (Map portBinding : portBindings) {
						if("manager".equals(portBinding.get("type"))) {
							container.setMgrBindHostPort((String)portBinding.get("hostPort"));
							continue;
						}
						if("9999".equals(portBinding.get("containerPort"))) {
							container.setLogBindHostPort((String)portBinding.get("hostPort"));
							continue;
						}
						hostPort.append((String)portBinding.get("hostPort")).append(",");
						containerPort.append((String)portBinding.get("containerPort")).append(",");
						protocol.append((String)portBinding.get("protocol")).append(",");
					}
					container.setBingHostPort(hostPort.length()>0?hostPort.substring(0, hostPort.length()-1):hostPort.toString());
					container.setBindContainerPort(containerPort.length()>0?containerPort.substring(0, containerPort.length()-1):containerPort.toString());
					container.setBingProtocol(protocol.length()>0?protocol.substring(0, protocol.length()-1):protocol.toString());
					
					this.gceContainerService.insert(container);
					if("jetty".equals(map.get("type"))) {
						GceContainerExt ext = new GceContainerExt();
						ext.setContainerId(container.getId());
						for (Map portBinding : portBindings) {
							//保存gceContainer扩展表，记录映射端口
							if("9888".equals(portBinding.get("containerPort"))) {//gbalance端口
								ext.setBindPort((String)portBinding.get("hostPort"));
								ext.setInnerPort((String)portBinding.get("containerPort"));
								ext.setType("glb");
								ext.setDescn("gbalancer映射内外端口");
								this.gceContainerExtService.insert(ext);
							} else if("7777".equals(portBinding.get("containerPort"))) {//moxi端口
								ext.setBindPort((String)portBinding.get("hostPort"));
								ext.setInnerPort((String)portBinding.get("containerPort"));
								ext.setType("moxi");
								ext.setDescn("moxi映射内外端口");
								this.gceContainerExtService.insert(ext);
							}
						}
					}
				} else {
					this.gceContainerService.insert(container);
				}
				ApiResultObject apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
                if(!apiResult.getAnalyzeResult()) {
                    //发送推送失败邮件，流程继续。
                    buildResultToMgr("GCE服务相关系统推送异常", container.getContainerName() +"节点固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                    tr.setResult(apiResult.getResult());
                    break;
                }
			}
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public TaskResult analyzeRestServiceResult(ApiResultObject resultObject) {
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(resultObject.getResult());
		if(map == null) {
			tr.setSuccess(false);
			tr.setResult("api connect failed");
			return tr;
		}
		Map<String,Object> meta = (Map<String, Object>) map.get("meta");
		Map<String,Object> response = null;
		
		boolean isSucess = Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")));
		if(isSucess) {
			response = (Map<String, Object>) map.get("response");
			isSucess = Constant.PYTHON_API_RESULT_SUCCESS.equals(String.valueOf(response.get("code")));
		}
		if(isSucess) {
			tr.setResult((String) response.get("message"));
		} else {
			tr.setResult((String) meta.get("errorType") +",the api url:" + resultObject.getUrl());
		}
		tr.setSuccess(isSucess);
		return tr;
	}

	@Override
	public void callBack(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		String type = (String) params.get("type");
        if(!StringUtils.isEmpty(type)) {
            if("tomcat".equals(type.toLowerCase()) || "resin".equals(type.toLowerCase()))
                super.rollBack(tr);
        }
	}
}
