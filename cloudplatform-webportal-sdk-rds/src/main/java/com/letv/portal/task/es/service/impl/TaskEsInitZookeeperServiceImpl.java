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
	
	private final static int PYTHON_CHECK_TIME = 500;
	private final static int PYTHON_CHECK_INTERVAL_TIME = 5000;
	
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
			Task task = new Task<TaskResult>() {
				@Override
				public TaskResult onExec() {
					//Beehive构建集群成功后，通过IP池策略会为每个容器分配一个IP，对于Beehive本地环境，该IP有效，容器间可以互调，但对于走网关访问的Matrix，由于网关不能及时更新这个IP，所以有时会出现
					//Beehive内部可以及时互联，但是Matrix却ping不通，因此使用的策略是，间隔5s请求一次该IP，重试500次，总5*500=2500s，如果该事件内请求HTTP失败，则认为是真正意义的容器创建失败。
					int num = PYTHON_CHECK_TIME;
					String nodeIp = container.getIpAddr();
					TaskResult trtmp = new TaskResult();
					trtmp.setSuccess(false);
					do{
						if(!trtmp.isSuccess()){
							if(num<=0){
								return trtmp;
							}
							if(num--<PYTHON_CHECK_TIME){
								try {
									Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						ApiResultObject api = esPythonService.initZookeeper(nodeIp,zkParm);
						trtmp = analyzeRestServiceResult(api);
					}while(!trtmp.isSuccess());
					return trtmp;
				}
				@Override
				public void onSuccess(TaskResult tr) {
					container.setZookeeperIp(zks.get(0).getIp());
					esContainerService.updateBySelective(container);
				}
			};
			tasks.add(task);
		}
		tr = super.asynchroExecuteTasks(tasks,tr);
		if (tr.isSuccess()) {
			logger.debug("配置Zookeeper地址成功");
		}
		tr.setParams(params);
		return tr;
	}

}
