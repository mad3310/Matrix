package com.letv.portal.model.task.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
	/**
	 * 线程池模式并行执行多任务
	 * @param tasks
	 * @param tr
	 * @return
	 */
	public TaskResult synchroExecuteTasks(List<Task> tasks,TaskResult tr);
	/**
	 * 异步执行的任务
	 * @author linzhanbo
	 * @param <T>
	 */
	abstract class Task<T> implements Callable<T>{
		/**
		 * 执行任务
		 * @return
		 */
		public abstract T onExec();
		/**
		 * 任务执行成功后回调
		 * @param t
		 * @param tr
		 */
		public void onSuccess(T t, TaskResult tr){}
		@Override
		public T call() throws Exception {
			return onExec();
		}
	};
	
}
