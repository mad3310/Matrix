package com.letv.portal.task.slb.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.letv.portal.fixedPush.IFixedPushService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.ISlbPythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.slb.ISlbContainerService;

@Service("taskSlbChecktatusService")
public class TaskSlbChecktatusServiceImpl extends BaseTask4SlbServiceImpl implements IBaseTaskService{

	private final static long PYTHON_CREATE_CHECK_TIME = 180000;
	private final static long PYTHON_CHECK_INTERVAL_TIME = 3000;
	@Autowired
	private ISlbPythonService slbPythonService;
	@Autowired
	private ISlbContainerService slbContainerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IFixedPushService fixedPushService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskSlbChecktatusServiceImpl.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		SlbServer server = super.getServer(params);
		SlbCluster cluster = super.getCluster(params);
		HostModel host = super.getHost(cluster.getHclusterId());
		
		ApiResultObject result = slbPythonService.checkContainerCreateStatus(cluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(result);
		
		Long start = new Date().getTime();
		while(!tr.isSuccess()) {
			Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
			if(new Date().getTime()-start >PYTHON_CREATE_CHECK_TIME) {
				tr.setResult("check time over:"+result.getUrl());
				break;
			}
			result = slbPythonService.checkContainerCreateStatus(cluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
			tr = analyzeRestServiceResult(result);
		}
		if(tr.isSuccess()) {
			List<Map> containers = (List<Map>)((Map)transToMap(result.getResult()).get("response")).get("containers");
			for (Map map : containers) {
				SlbContainer container = new SlbContainer();
				BeanUtils.populate(container, map);
				container.setSlbClusterId(cluster.getId());
				container.setIpMask((String) map.get("netMask"));
				container.setContainerName((String) map.get("containerName"));
				container.setStatus(MclusterStatus.RUNNING.getValue());
				//物理机集群维护完成后，修改此处，需要关联物理机id
				container.setHostIp((String) map.get("hostIp"));
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				this.slbContainerService.insert(container);
				if(container.getIpAddr().startsWith("10.")) {
					ApiResultObject apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
					if(!apiResult.getAnalyzeResult()) {
						//发送推送失败邮件，流程继续。
						buildResultToMgr("SLB服务相关系统推送异常", container.getContainerName() +"节点固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
						tr.setResult(apiResult.getResult());
					}
				}
			}
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public TaskResult analyzeRestServiceResult(ApiResultObject result) {
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(result.getResult());
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
			tr.setResult((String) meta.get("errorType") +",the api url:" + result.getUrl());
		}
		tr.setSuccess(isSucess);
		return tr;
	}
	
}
