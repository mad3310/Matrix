package com.letv.portal.model.task.service;

import java.util.Map;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.task.TaskResult;

public interface IBaseTaskService {

	public TaskResult execute(Map<String,Object> params) throws Exception;

	public void rollBack(TaskResult tr);
	
	public void callBack(TaskResult tr);

	public void beforExecute(Map<String, Object> params);
	/**
	 * 对调用结果进行复杂的校验,先校验meta>code
	 * @param resultObject
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月1日, 上午11:11:29 .
	 * @version 1.0 .
	 */
	public TaskResult analyzeRestServiceResult(ApiResultObject resultObject);
	/**
	 * 对调用结果进行复杂的校验,先校验meta>code，再校验response>code。
	 * @param resultObject
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月1日, 上午11:10:25 .
	 * @version 1.0 .
	 */
	public TaskResult analyzeComplexRestServiceResult(ApiResultObject resultObject);
	/**
	 * 轮询执行
	 * @param tr
	 * @param interval
	 * @param timeout
	 * @param params	可选参数
	 * @return
	 * @throws InterruptedException
	 * @author linzhanbo .
	 * @since 2016年7月15日, 上午9:39:32 .
	 * @version 1.0 .
	 */
	public TaskResult polling(TaskResult tr,long interval,long timeout,Object... params) throws InterruptedException;
	/**
	 * 轮询执行的任务
	 * @param params	可选参数
	 * @return
	 * @throws InterruptedException
	 * @author linzhanbo .
	 * @since 2016年7月15日, 上午9:39:44 .
	 * @version 1.0 .
	 */
	public ApiResultObject pollingTask(Object... params) throws InterruptedException;
	
	/**
	 * 通用参数验证
	 * @param params
	 * @return
	 */
	public TaskResult validator(Map<String, Object> params) throws Exception;
	
}
