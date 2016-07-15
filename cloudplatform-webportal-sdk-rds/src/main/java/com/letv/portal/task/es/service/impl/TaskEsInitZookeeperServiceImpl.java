package com.letv.portal.task.es.service.impl;

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
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		EsCluster esCluster = super.getEsCluster(params);

		List<EsContainer> containers = super.getContainers(params);
		List<ZookeeperInfo> zks = super.selectMinusedZkByHclusterId(esCluster.getHclusterId(), 3);

		Map<String, String> zkParm = new HashMap<String,String>();
		zkParm.put("zkAddress", zks.get(0).getIp());
		zkParm.put("zkPort", zks.get(0).getPort());
		
		for (int i = 0; i < containers.size()-1; i++) {
			EsContainer container = containers.get(i);
			String nodeIp = container.getIpAddr();
			ApiResultObject resultObject = this.esPythonService.initZookeeper(nodeIp, zkParm);

			tr = analyzeRestServiceResult(resultObject);
			if(!tr.isSuccess()) {
				tr.setResult("the" + (i+1) +"node error:" + tr.getResult());
				break;
			} else {
				container.setZookeeperIp(zks.get(0).getIp());
				this.esContainerService.updateBySelective(container);
			}
		}

		tr.setParams(params);
		return tr;
	}

}
