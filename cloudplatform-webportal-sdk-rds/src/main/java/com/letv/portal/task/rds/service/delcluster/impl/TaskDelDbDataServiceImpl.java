package com.letv.portal.task.rds.service.delcluster.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.model.task.service.ITaskAsyncExecuteService;
import com.letv.portal.service.IMclusterService;

@Service("taskDelDbDataService")
public class TaskDelDbDataServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService{

	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private ITaskAsyncExecuteService asyncExecuteService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskDelDbDataServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		MclusterModel mcluster = super.getMcluster(params);
		//删除工作流失败异步重试表记录
		this.asyncExecuteService.deleteByMclusterName(mcluster.getMclusterName());
		this.mclusterService.delete(mcluster);
		
		tr.setResult("success");
		tr.setSuccess(true);
		logger.debug("RDS集群删除-删除数据库记录成功{}", mcluster.getMclusterName());
		tr.setParams(params);
		return tr;
	}
	
}
