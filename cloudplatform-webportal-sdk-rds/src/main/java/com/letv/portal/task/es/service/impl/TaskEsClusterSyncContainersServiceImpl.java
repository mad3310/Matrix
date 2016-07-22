package com.letv.portal.task.es.service.impl;

import java.util.ArrayList;
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
import com.letv.portal.model.task.service.IBaseTaskService.Task;
import com.letv.portal.python.service.IEsPythonService;

@Service("taskEsClusterSyncContainersService")
public class TaskEsClusterSyncContainersServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{
	
	@Autowired
	private IEsPythonService esPythonService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEsClusterSyncContainersServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("ES集群数据同步");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		EsCluster cluster = super.getEsCluster(params);
		List<EsContainer> containers = super.getContainers(params);
		
		final String username = cluster.getAdminUser();
		final String password = cluster.getAdminPassword();
		
		final Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", cluster.getClusterName());
		
		List<Task> tasks = new ArrayList<Task>();
		for(final EsContainer container:containers){
			Task task = new Task<ApiResultObject>() {
				@Override
				public ApiResultObject onExec() {
					String nodeIp = container.getIpAddr();
					return esPythonService.syncEsCluster(nodeIp, map, username, password);
				}
			};
			tasks.add(task);
		}
		tr = super.asynchroExecuteTasks(tasks,tr);
		if (tr.isSuccess()) {
			logger.debug("ES集群数据同步成功");
		}
		tr.setParams(params);
		return tr;
	}

}
