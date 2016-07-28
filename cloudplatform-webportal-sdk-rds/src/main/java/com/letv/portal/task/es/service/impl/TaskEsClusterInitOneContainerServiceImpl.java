package com.letv.portal.task.es.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IEsPythonService;

@Service("taskEsClusterInitOneContainerService")
public class TaskEsClusterInitOneContainerServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsPythonService esPythonService;

	private final static Logger logger = LoggerFactory.getLogger(TaskEsClusterInitOneContainerServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("初始化ES集群");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		EsCluster cluster = super.getEsCluster(params);
		List<EsContainer> containers = super.getContainers(params);
		
		String nodeIp1 = containers.get(0).getIpAddr();
		String username = cluster.getAdminUser();
		String password = cluster.getAdminPassword();
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", cluster.getClusterName());
		
		ApiResultObject result = this.esPythonService.initEsCluster(nodeIp1, map, username, password);
		
		tr = analyzeRestServiceResult(result);
		if (tr.isSuccess()) {
			logger.debug("初始化ES集群成功");
		}
		tr.setParams(params);
		return tr;
	}

}
