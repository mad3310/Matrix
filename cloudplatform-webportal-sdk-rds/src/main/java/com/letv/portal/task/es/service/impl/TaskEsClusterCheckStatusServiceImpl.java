package com.letv.portal.task.es.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IEsPythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.es.IEsContainerService;

@Service("taskEsClusterCheckStatusService")
public class TaskEsClusterCheckStatusServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsPythonService esPythonService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IEsContainerService esContainerService;
	
	private final static long PYTHON_CREATE_CHECK_TIME = 180000;
	private final static long PYTHON_CHECK_INTERVAL_TIME = 3000;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEsClusterCheckStatusServiceImpl.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		logger.debug("检查ES集群创建状态，获取创建的所有containers");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		EsCluster esCluster = super.getEsCluster(params);
		HostModel host = super.getHost(esCluster.getHclusterId());
		
		Long start = new Date().getTime();
		ApiResultObject result = null;
		
		do {
			result = esPythonService.checkContainerCreateStatus(esCluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
			tr = analyzeComplexRestServiceResult(result);
			if(!tr.isSuccess()) {
				Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
				if(new Date().getTime()-start >PYTHON_CREATE_CHECK_TIME) {
					tr.setResult("check time over:"+result.getUrl());
					break;
				}
			}
		} while (!tr.isSuccess());
		
		if(tr.isSuccess()) {
			logger.debug("创建ES集群成功");
			//respMap肯定不为空，否则analyzeRestServiceResult会报错，如果containers为空，不会进下面for
			Map respMap = (Map)transToMap(result.getResult()).get("response");
			List<Map> containers = (List<Map>)(respMap.get("containers"));
			for (Map map : containers) {
				EsContainer container = new EsContainer();
				BeanUtils.populate(container, map);
				container.setEsClusterId(esCluster.getId());
				container.setIpMask((String) map.get("netMask"));
				container.setStatus(MclusterStatus.RUNNING);
				//物理机集群维护完成后，修改此处，需要关联物理机id
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				/*List<Map> portBindings = (List<Map>) map.get("port_bindings");
				StringBuffer hostPort = new StringBuffer();
				StringBuffer containerPort = new StringBuffer();
				StringBuffer protocol = new StringBuffer();
				for (Map portBinding : portBindings) {
					if("9999".equals(portBinding.get("containerPort"))) {
						container.setMgrBindHostPort((String)portBinding.get("hostPort"));
						continue;
					}
					hostPort.append((String)portBinding.get("hostPort")).append(",");
					containerPort.append((String)portBinding.get("containerPort")).append(",");
					protocol.append((String)portBinding.get("protocol")).append(",");
				}
				container.setBingHostPort(hostPort.length()>0?hostPort.substring(0, hostPort.length()-1):hostPort.toString());
				container.setBindContainerPort(containerPort.length()>0?containerPort.substring(0, containerPort.length()-1):containerPort.toString());
				container.setBingProtocol(protocol.length()>0?protocol.substring(0, protocol.length()-1):protocol.toString());*/
				
				this.esContainerService.insert(container);
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
