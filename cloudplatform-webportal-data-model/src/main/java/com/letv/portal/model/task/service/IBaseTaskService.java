package com.letv.portal.model.task.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.task.TaskResult;

public interface IBaseTaskService {
	/**
	 * 执行任务单元
	 * @param params
	 * @return
	 * @throws Exception
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午5:36:35 .
	 * @version 1.0 .
	 */
	public TaskResult execute(Map<String,Object> params) throws Exception;
	/**
	 * 回滚任务单元
	 * @param tr
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午5:36:59 .
	 * @version 1.0 .
	 */
	public void rollBack(TaskResult tr);
	/**
	 * 完成任务流
	 * @param tr
	 * @author linzhanbo .
	 * @since 2016年7月28日, 下午3:46:48 .
	 * @version 1.0 .
	 */
	public void finish(TaskResult tr);
	/**
	 * 任务单元执行后执行<br/>
	 * <ol><li>
	 * <b style="line-height: 1.6;"><font color="#ff0000">方法已过时</font></b><span style="line-height: 1.6;">，强烈建议使用afterExecute方法，虽然二者作用相同，后续升级流程时，会删掉该方法。</span>
	 * </li>
	 * <li><span style="line-height: 1.6;">该方法在execute方法后执行</span></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果当前子类既重写callBack方法，也重写afterExecute方法，不包含父类重写，只执行afterExecute</font></b></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果当前子类仅有callBack方法，那执行callBack</font></b></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果当前子类既都没有，父类的规则也同上重写，只执行afterExecute</font></b></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果需要结束流程，建议使用finish接口代替rollback接口</font></b></li></ol>
	 * 
	 * @param tr
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午5:37:14 .
	 * @version 1.0 .
	 */
	@Deprecated
	public void callBack(TaskResult tr);
	/**
	 * 任务单元执行后执行
	 * @param tr
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午6:00:43 .
	 * @version 1.0 .
	 */
	public void afterExecute(TaskResult tr);
	
	/**
	 * 任务单元执行前执行
	 * @param params
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午6:06:58 .
	 * @version 1.0 .
	 */
	public void beforeExecute(Map<String, Object> params);
	/**
	 * 任务单元执行前执行
	 * <ol><li>
	 * <b style="line-height: 1.6;"><font color="#ff0000">方法已过时</font></b><span style="line-height: 1.6;">，强烈建议使用beforeExecute方法，虽然二者作用相同，后续升级流程时，会删掉该方法。</span>
	 * </li>
	 * <li><span style="line-height: 1.6;">该方法在execute方法前执行</span></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果当前子类既重写beforExecute方法，也重写beforeExecute方法，不包含父类重写，只执行beforeExecute</font></b></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果当前子类仅有beforExecute方法，那执行beforExecute</font></b></li>
	 * <li><b style="line-height: 1.6;"><font color="#ff0000">如果当前子类既都没有，父类的规则也同上重写，只执行beforeExecute</font></b></li></ol>
	 * @param params
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午6:00:27 .
	 * @version 1.0 .
	 */
	@Deprecated
	public void beforExecute(Map<String, Object> params);
	/**
	 * 对调用结果进行复杂的校验,先校验meta>code<br/><br/>
	 * 注意：<span style="line-height: 1.6;">由于目前每种服务对于返回结果json串没有一个固定统一格式，所以当ApiResultObject返回的result为出错时，
	 * <b><font color="#ff0000">BaseTaskServiceImpl类返回TaskResult的result为整个result，未对error进行过滤</font></b>。<br></span>
	 * <div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>如：result为</div>
	 * <div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>
	 * <span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>
	 * <span style="line-height: 1.6;">
	 * {"meta":&nbsp;{"code":&nbsp;417,&nbsp;"errorType":&nbsp;"user_visible_error",&nbsp;"errorDetail":&nbsp;"u'containerCluster&nbsp;27_2_admin036a2_2&nbsp;not&nbsp;existed'"}}，</span>
	 * </div><div>&nbsp; &nbsp; 则
	 * <span style="line-height: 1.6;">BaseTaskServiceImpl返回的是整个</span><span style="line-height: 1.6;">&nbsp;</span></div>
	 * <div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>
	 * <span style="line-height: 1.6;">{"meta":&nbsp;{"code":&nbsp;417,&nbsp;"errorType":&nbsp;"user_visible_error",&nbsp;"errorDetail":&nbsp;"u'containerCluster&nbsp;27_2_admin036a2_2&nbsp;not&nbsp;existed'"}}</span>
	 * <span style="line-height: 1.6;"><br></span></div><div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span><font color="#ff0000">
	 * <b>如果需要对error进行过滤，请在每种服务的基类BaseXXXServiceImpl对该方法进行重写</b></font>。</div>
	 * @param resultObject
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月1日, 上午11:11:29 .
	 * @version 1.0 .
	 */
	public TaskResult analyzeRestServiceResult(ApiResultObject resultObject);
	/**
	 * 对调用结果进行复杂的校验,先校验meta>code，再校验response>code。<br/><br/>
	 * 注意：<span style="line-height: 1.6;">由于目前每种服务对于返回结果json串没有一个固定统一格式，所以当ApiResultObject返回的result为出错时，
	 * <b><font color="#ff0000">BaseTaskServiceImpl类返回TaskResult的result为整个result，未对error进行过滤</font></b>。<br></span>
	 * <div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>如：result为</div>
	 * <div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>
	 * <span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>
	 * <span style="line-height: 1.6;">
	 * {"meta":&nbsp;{"code":&nbsp;417,&nbsp;"errorType":&nbsp;"user_visible_error",&nbsp;"errorDetail":&nbsp;"u'containerCluster&nbsp;27_2_admin036a2_2&nbsp;not&nbsp;existed'"}}，</span>
	 * </div><div>&nbsp; &nbsp; 则
	 * <span style="line-height: 1.6;">BaseTaskServiceImpl返回的是整个</span><span style="line-height: 1.6;">&nbsp;</span></div>
	 * <div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span>
	 * <span style="line-height: 1.6;">{"meta":&nbsp;{"code":&nbsp;417,&nbsp;"errorType":&nbsp;"user_visible_error",&nbsp;"errorDetail":&nbsp;"u'containerCluster&nbsp;27_2_admin036a2_2&nbsp;not&nbsp;existed'"}}</span>
	 * <span style="line-height: 1.6;"><br></span></div><div><span style="line-height: 1.6;">&nbsp; &nbsp;&nbsp;</span><font color="#ff0000">
	 * <b>如果需要对error进行过滤，请在每种服务的基类BaseXXXServiceImpl对该方法进行重写</b></font>。</div>
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
	/**
	 * 串行执行多任务
	 * @param tasks
	 * @param tr
	 * @return
	 */
	public TaskResult asynchroExecuteTasks(List<Task> tasks,TaskResult tr);
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
		 * @param tr
		 */
		public void onSuccess(TaskResult tr){}
		@Override
		public T call() throws Exception {
			return onExec();
		}
	};
	
}
