package com.letv.portal.task.es.service.impl;

import java.util.ArrayList;
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

@Service("taskEsStartService")
public class TaskEsStartServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsPythonService esPythonService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEsStartServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("启动ES");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		EsCluster cluster = super.getEsCluster(params);
		List<EsContainer> containers = super.getContainers(params);
		
		final String username = cluster.getAdminUser();
		final String password = cluster.getAdminPassword();
		
		List<Task> tasks = new ArrayList<Task>();
		for(final EsContainer container:containers){
			Task task = new Task<ApiResultObject>() {
				@Override
				public ApiResultObject onExec() {
					String nodeIp = container.getIpAddr();
					return TaskEsStartServiceImpl.this.esPythonService.startElesticSearch(nodeIp, username, password);
				}
			};
			tasks.add(task);
		}
		tr = super.synchroExecuteTasks(tasks,tr);
		if (tr.isSuccess()) {
			logger.debug("启动ES成功");
		}
		tr.setParams(params);
		return tr;
	}
	@Override
	public void callBack(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		String serverName = (String) params.get("serviceName");
    	logger.debug("部署ES {}成功!",serverName);
    	super.rollBack(tr);
	}
}
