package com.letv.portal.task.es.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.model.task.service.IBaseTaskService.Task;
import com.letv.portal.python.service.IEsPythonService;

@Service("taskEsContainersInitService")
public class TaskEsContainersInitServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsPythonService esPythonService;

	private final static Logger logger = LoggerFactory.getLogger(TaskEsContainersInitServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("初始化node");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		final EsCluster cluster = super.getEsCluster(params);
		final List<EsContainer> containers = super.getContainers(params);
		
		final String username = cluster.getAdminUser();
		final String password = cluster.getAdminPassword();
		
		final Map<String,String> map = new HashMap<String,String>();
		
		List<Task> tasks = new ArrayList<Task>();
		for(final EsContainer container:containers){
			Task task = new Task<ApiResultObject>() {
				@Override
				public ApiResultObject onExec() {
					int index = containers.indexOf(container)+1;
					String nodeIp = container.getIpAddr();
					map.put("dataNodeIp", nodeIp);
					map.put("dataNodeName", MessageFormat.format("d-logs-{0}-n-{1}", cluster.getClusterName(),index));
					return esPythonService.initEsContainer(nodeIp, map, username, password);
				}
			};
			tasks.add(task);
		}
		tr = super.asynchroExecuteTasks(tasks,tr);
		if (tr.isSuccess()) {
			logger.debug("初始化node成功");
		}
		tr.setParams(params);
		return tr;
	}

}
