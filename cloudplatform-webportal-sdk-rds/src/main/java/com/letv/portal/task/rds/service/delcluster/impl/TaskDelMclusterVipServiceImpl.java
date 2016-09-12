package com.letv.portal.task.rds.service.delcluster.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;

@Service("taskDelMclusterVipService")
public class TaskDelMclusterVipServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService{
	
	@Autowired
	private IPythonService pythonService;

	private final static Logger logger = LoggerFactory.getLogger(TaskDelMclusterVipServiceImpl.class);

	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.validator(params);
		if(!tr.isSuccess())
			return tr;
		
		MclusterModel mclusterModel = super.getMcluster(params);
		
		HostModel host = super.getHost(mclusterModel.getHclusterId());
		
		ApiResultObject result = this.pythonService.removeMcluster(
				mclusterModel.getMclusterName()+Constant.MCLUSTER_NODE_TYPE_VIP_SUFFIX,host.getHostIp(),host.getName(),host.getPassword());
		
		tr = analyzeRestServiceResult(result);
		
		if (tr.isSuccess()) {
			logger.debug("RDS集群删除-调用删除RDS集群vip节点成功{}", mclusterModel.getMclusterName());
		}
		tr.setParams(params);
		return tr;
	}
	
}
