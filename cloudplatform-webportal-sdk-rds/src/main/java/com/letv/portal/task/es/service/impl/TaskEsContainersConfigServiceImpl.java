package com.letv.portal.task.es.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IEsPythonService;

@Service("taskEsContainersConfigService")
public class TaskEsContainersConfigServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsPythonService esPythonService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEsContainersConfigServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("配置ES");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		EsCluster cluster = super.getEsCluster(params);
		List<EsContainer> containers = super.getContainers(params);
		if(CollectionUtils.isEmpty(containers)){
			tr.setSuccess(false);
			tr.setResult("there is no available containers");
			return tr;
		}
		final String username = cluster.getAdminUser();
		final String password = cluster.getAdminPassword();
		List<Task> tasks = new ArrayList<Task>();
		for(final EsContainer container:containers){
			Task task = new Task<ApiResultObject>() {
				@Override
				public ApiResultObject onExec() {
					String nodeIp = container.getIpAddr();
					return TaskEsContainersConfigServiceImpl.this.esPythonService.configEsContainer(nodeIp, username, password);
				}
			};
			tasks.add(task);
		}
		tr = super.synchroExecuteTasks(tasks,tr);
		if (tr.isSuccess()) {
			logger.debug("配置ES成功");
		}
		tr.setParams(params);
		return tr;
	}
	
}
