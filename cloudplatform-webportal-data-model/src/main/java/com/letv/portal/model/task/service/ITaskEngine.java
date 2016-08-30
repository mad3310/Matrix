package com.letv.portal.model.task.service;


/**
 * 
 * 工作流引擎入口
 * 
 * @author linzhanbo .
 * @since 2016年7月26日, 下午5:51:26 .
 * @version 1.0 .
 */
public interface ITaskEngine {

	/**
	 * 启动流程
	 * @param templateTaskName	流程模板名称
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午5:17:20 .
	 * @version 1.0 .
	 */
	public void run(String templateTaskName);
	
	/**
	 * 启动流程
	 * @param templateTaskName	流程模板名称
	 * @param params	参数
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午5:18:00 .
	 * @version 1.0 .
	 */
	public void run(String templateTaskName, Object params);
	
	/**
	 * 启动流程
	 * @param templateTaskId	流程模板ID
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午5:25:49 .
	 * @version 1.0 .
	 */
	public void run(Long templateTaskId);
	
	/**
	 * 启动流程
	 * @param templateTaskId	流程模板ID
	 * @param params	参数
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午5:28:35 .
	 * @version 1.0 .
	 */
	public void run(Long templateTaskId, Object params);
	
	/**
	 * 继续运行流程
	 * @param taskChainId	任务单元实例ID
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午5:48:59 .
	 * @version 1.0 .
	 */
	public void proceed(Long taskChainId);

}
