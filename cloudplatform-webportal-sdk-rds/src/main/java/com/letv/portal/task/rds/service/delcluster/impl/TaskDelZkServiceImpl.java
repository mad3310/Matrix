package com.letv.portal.task.rds.service.delcluster.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;

@Service("taskDelZkService")
public class TaskDelZkServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService{

	@Autowired
	private IPythonService pythonService;
    
	private final static Logger logger = LoggerFactory.getLogger(TaskDelZkServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.validator(params);
		if(!tr.isSuccess())
			return tr;
		
		MclusterModel mclusterModel = super.getMcluster(params);
		List<ContainerModel> containers = super.getVipContainers(params);
		
		ApiResultObject result = this.pythonService.removeClusterZkInfo(containers.get(0).getIpAddr(),
				mclusterModel.getAdminUser(),mclusterModel.getAdminPassword());
		
		tr = analyzeRestServiceResult(result);
		
		if (tr.isSuccess()) {
			logger.debug("RDS集群删除-调用删除RDS集群zk信息成功{}", mclusterModel.getMclusterName());
		}
		tr.setParams(params);
		return tr;
	}
	
}
