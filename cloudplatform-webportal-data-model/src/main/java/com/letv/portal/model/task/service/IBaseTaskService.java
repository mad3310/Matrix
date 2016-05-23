package com.letv.portal.model.task.service;

import java.util.Map;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.task.TaskResult;

public interface IBaseTaskService {

	public TaskResult execute(Map<String,Object> params) throws Exception;

	public void rollBack(TaskResult tr);
	
	public void callBack(TaskResult tr);

	public void beforExecute(Map<String, Object> params);

	public TaskResult analyzeRestServiceResult(ApiResultObject resultObject);
	
	/**
	 * 通用参数验证
	 * @param params
	 * @return
	 */
	public TaskResult validator(Map<String, Object> params) throws Exception;
	
}
