package com.letv.portal.model.task.service;

import com.letv.portal.model.task.TaskAsyncExecute;
import com.letv.portal.service.IBaseService;

public interface ITaskAsyncExecuteService extends IBaseService<TaskAsyncExecute> {
	
	TaskAsyncExecute selectByTaskChainId(Long taskChainId);
	
	/**
	 * 工作流失败步骤异步重试
	 */
	void taskAsyncRetry();
	
	void deleteByMclusterName(String mclusterName);

}
