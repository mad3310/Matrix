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

@Service("taskEsClusterSyncContainersService")
public class TaskEsClusterSyncContainersServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{
	
	@Autowired
	private IEsPythonService esPythonService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEsClusterSyncContainersServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		EsCluster cluster = super.getEsCluster(params);
		List<EsContainer> containers = super.getContainers(params);
		
		String username = cluster.getAdminUser();
		String password = cluster.getAdminPassword();
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", cluster.getClusterName());
		
		for (int i = 0; i < containers.size()-1; i++) {
			EsContainer container = containers.get(i);
			String nodeIp = container.getIpAddr();
			ApiResultObject resultObject = this.esPythonService.syncEsCluster(nodeIp, map, username, password);

			tr = analyzeRestServiceResult(resultObject);
			if(!tr.isSuccess()) {
				tr.setResult("the" + (i+1) +"node error:" + tr.getResult());
				break;
			} 
		}
		
		tr.setParams(params);
		return tr;
	}

}
