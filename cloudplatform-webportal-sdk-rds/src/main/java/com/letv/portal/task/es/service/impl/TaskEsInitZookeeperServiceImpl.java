package com.letv.portal.task.es.service.impl;

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
import com.letv.portal.model.common.ZookeeperInfo;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.model.task.service.IBaseTaskService.Task;
import com.letv.portal.python.service.IEsPythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.es.IEsClusterService;
import com.letv.portal.service.es.IEsContainerService;

@Service("taskEsInitZookeeperService")
public class TaskEsInitZookeeperServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsPythonService esPythonService;
	@Autowired
	private IEsContainerService esContainerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IEsClusterService esClusterService;

	private final static Logger logger = LoggerFactory.getLogger(TaskEsInitZookeeperServiceImpl.class);

	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("配置Zookeeper地址");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		EsCluster esCluster = super.getEsCluster(params);

		List<EsContainer> containers = super.getContainers(params);
		final List<ZookeeperInfo> zks = super.selectMinusedZkByHclusterId(esCluster.getHclusterId(), 3);

		final Map<String, String> zkParm = new HashMap<String,String>();
		zkParm.put("zkAddress", zks.get(0).getIp());
		zkParm.put("zkPort", zks.get(0).getPort());
		
		List<Task> tasks = new ArrayList<Task>();
		for(final EsContainer container:containers){
			Task task = new Task<ApiResultObject>() {
				@Override
				public ApiResultObject onExec() {
					String nodeIp = container.getIpAddr();
					return TaskEsInitZookeeperServiceImpl.this.esPythonService.initZookeeper(nodeIp,zkParm);
				}
				@Override
				public void onSuccess(ApiResultObject apiResult, TaskResult tr) {
					container.setZookeeperIp(zks.get(0).getIp());
					TaskEsInitZookeeperServiceImpl.this.esContainerService.updateBySelective(container);
				}
			};
			tasks.add(task);
		}
		tr = super.synchroExecuteTasks(tasks,tr);
		if (tr.isSuccess()) {
			logger.debug("配置Zookeeper地址成功");
		}
		tr.setParams(params);
		return tr;
	}

}
