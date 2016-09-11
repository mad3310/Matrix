package com.letv.portal.task.rds.service.delcluster.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;

@Service("taskDelFixedService")
public class TaskDelFixedServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService{

	@Autowired
	private IFixedPushService fixedPushService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskDelFixedServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		List<ContainerModel> containers = super.getContainers(params);
		
		ApiResultObject apiResult = fixedPushService.deleteMutilContainerPushFixedInfo(containers);
		
		tr.setResult(apiResult.getResult());
		tr.setSuccess(apiResult.getAnalyzeResult());
		tr.setParams(params);
		return tr;
	}
	
}
