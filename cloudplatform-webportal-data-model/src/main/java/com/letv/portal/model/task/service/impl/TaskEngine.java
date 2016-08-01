package com.letv.portal.model.task.service.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.letv.common.exception.TaskExecuteException;
import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.task.TaskChain;
import com.letv.portal.model.task.TaskChainIndex;
import com.letv.portal.model.task.TaskExecuteStatus;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.TemplateTask;
import com.letv.portal.model.task.TemplateTaskChain;
import com.letv.portal.model.task.TemplateTaskDetail;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.model.task.service.ITaskChainIndexService;
import com.letv.portal.model.task.service.ITaskChainService;
import com.letv.portal.model.task.service.ITaskEngine;
import com.letv.portal.model.task.service.ITemplateTaskChainService;
import com.letv.portal.model.task.service.ITemplateTaskDetailService;
import com.letv.portal.model.task.service.ITemplateTaskService;

@Component("taskEngine")
public class TaskEngine extends ApplicationObjectSupport implements ITaskEngine{

	private final static Logger logger = LoggerFactory.getLogger(TaskEngine.class);
	
	@Autowired
	private ITemplateTaskService templateTaskService;
	@Autowired
	private ITemplateTaskChainService templateTaskChainService;
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	@Autowired
	private ITemplateTaskDetailService templateTaskDetailService;
	@Autowired
	private ITaskChainService taskChainService;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	//用户ID	系统目前获取Session是采用单线程模式，但在另一个线程获取该用户ID，采用这个办法，在进入另一个线程前先获取该ID，因为这个用户ID永久不变，不考虑极端情况
	private Long userId;
	
	@Async
	@Override
	public void run(String templateTaskName) {
		run(templateTaskName,null);
	}

	@Async
	@Override
	public void run(String templateTaskName, Object params) {
		start(templateTaskName, params);
	}

	@Async
	@Override
	public void run(Long templateTaskId) {
		run(templateTaskId, null);
	}
	
	@Async
	@Override
	public void run(Long templateTaskId, Object params) {
		start(templateTaskId, params);
	}
	
	@Async
	@Override
	public void proceed(Long taskChainId) {
		logger.debug("Workflow engine preparation");
		if(null == taskChainId)
			throw new ValidateException("taskChainId is null");
		TaskChain theTaskChain = this.taskChainService.selectById(taskChainId);
		TaskChainIndex theChainIndex = this.taskChainIndexService.selectById(theTaskChain.getChainIndexId());
		//获取当前人ID
		if(sessionService.getSession() != null){
			userId = sessionService.getSession().getUserId();
		}
		logger.debug("Workflow engine to continue working");
		run(theChainIndex, theTaskChain);
	}
	/**
	 * 开始执行任务流
	 * @param templateKey
	 * @param params
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午1:56:39 .
	 * @version 1.0 .
	 */
	private void start(Object templateKey,Object params){
		logger.debug("Workflow engine preparation");
		TemplateTask templateTask = null;
		if(templateKey instanceof String){
			String templateTaskName = null;
			templateTaskName = (String) templateKey;
			if(StringUtils.isEmpty(templateTaskName))
				throw new TaskExecuteException("TemplateTask's name is empty");
			templateTask = this.templateTaskService.selectByName(templateTaskName);
		} else if(templateKey instanceof Long){
			Long templateTaskId = null;
			templateTaskId = (Long) templateTaskId;
			if(null == templateTaskId)
				throw new TaskExecuteException("TemplateTask's id is empty");
			templateTask = this.templateTaskService.selectById(templateTaskId);
		}
		if(null == templateTask)
			throw new TaskExecuteException(MessageFormat.format("When TemplateTask's id/name is {0}, TemplateTask is null", templateKey));
		if(templateTask.isDeleted())
			throw new TaskExecuteException(MessageFormat.format("When TemplateTask's id/name is {0}, TemplateTask is invalid", templateKey));
		//使用流程模板ID获取流程的所有单元定义信息
		List<TemplateTaskChain> ttcs = this.templateTaskChainService.selectByTemplateTaskId(templateTask.getId());
		if(CollectionUtils.isEmpty(ttcs))
			throw new TaskExecuteException(MessageFormat.format("When TemplateTask's id/name is {0}, TemplateTaskChains is null", templateKey));
		//获取当前人ID,为null后面也可以用
		if(sessionService.getSession() != null){
			userId = sessionService.getSession().getUserId();
		}
		logger.debug("Prepare the basic rules for the initialization process");
		//初始化任务流实例信息，返回当前任务流实例信息
		TaskChainIndex theChainIndex = null;
		try {
			theChainIndex = initTask(templateTask,ttcs,params);
		} catch (IOException e) {
			throw new TaskExecuteException("Failed to initialize the flow rule",e);
		}
		logger.debug("Workflow engine is working");
		//获取第一个环节
		TaskChain theFirstTaskChain = this.taskChainService.selectNextChainByIndexAndOrder(theChainIndex.getId(),1);
		//异步执行流程
		run(theChainIndex,theFirstTaskChain);
	}
	/**
	 * 从当前环节开始执行流程
	 * @param taskChain
	 * @param taskChainIndex
	 * @author linzhanbo .
	 * @since 2016年7月27日, 上午10:57:15 .
	 * @version 1.0 .
	 */
	private void run(TaskChainIndex taskChainIndex,TaskChain taskChain){
		logger.debug("Ready to run the {}th links of the process {},the service_name is {},the cluster_name is {}",
				taskChain.getExecuteOrder(),taskChainIndex.getTemplateTask().getName(),
				taskChainIndex.getServiceName(),taskChainIndex.getClusterName());
		//修改流程状态为正在进行中
		taskChainIndex.setStatus(TaskExecuteStatus.DOING);
		taskChainIndex.setStartTime(new Date());
		taskChainIndex.setUpdateUser(userId);
		this.taskChainIndexService.updateBySelective(taskChainIndex);
		//开始执行每一单元实例
		onExecTaskChain(taskChainIndex,taskChain);
	}
	/**
	 * 递归执行任务单元实例
	 * @param taskChainIndex
	 * @param taskChain
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午1:03:19 .
	 * @version 1.0 .
	 */
	@SuppressWarnings("unused")
	private void onExecTaskChain(TaskChainIndex taskChainIndex,TaskChain taskChain){
		logger.debug("The {}th links of the process {} is runnning,the service_name is {},the cluster_name is {}",
				taskChain.getExecuteOrder(),taskChainIndex.getTemplateTask().getName(),
				taskChainIndex.getServiceName(),taskChainIndex.getClusterName());
		IBaseTaskService baseTask = null;
		String errMsg = null;
		TaskResult taskResult = new TaskResult();
		try {
			taskChain  = onBeforeExecTaskChain(taskChain);
			String taskBeanName = taskChain.getTemplateTaskDetail().getBeanName();
			String paramsJsonStr = taskChain.getParams();
			Map<String,Object> params = fromJson(paramsJsonStr);
			baseTask = (IBaseTaskService)getApplicationContext().getBean(taskBeanName);
			if(null == baseTask){
				errMsg = MessageFormat.format("When TemplateTaskDetail's beanName is {0},SpringBean is null", taskBeanName);
				interrupt(taskChainIndex,taskChain,errMsg);
				return;
			}
			//判断是执行新方法beforeExecute还是过期方法beforExecute，规则见isNextRunDest方法详细定义
			boolean isExistBeforeExecute = isNextRunMethod(baseTask.getClass(), "beforeExecute", "beforExecute", new Class[]{Map.class});
			if(isExistBeforeExecute){
				baseTask.beforeExecute(params);
			}else{
				baseTask.beforExecute(params);
			}
			int retry = 1;
			do{
				if(retry > 1)
					Thread.sleep(1000);
				taskResult.setParams(params);
				taskResult = baseTask.execute(params);
				if(taskResult == null){
					errMsg = MessageFormat.format("The return value of the TaskChain's execute method is null,SpringBean is {0}", taskBeanName);
					taskResult.setSuccess(false);
					taskResult.setResult(errMsg);
					//TODO	在这里应该不叫rollback
					baseTask.rollBack(taskResult);
					interrupt(taskChainIndex,taskChain,errMsg);
					return;
				}
				
			}while(retry++ < taskChain.getTemplateTaskDetail().getRetry() && !taskResult.isSuccess());
			//为防止execute方法篡改params，这里手动赋值以为后续onExecTaskChain、rollback和finish使用
			taskResult.setParams(params);
			if(!taskResult.isSuccess()) {
				//TODO	在这里应该不叫rollback
				baseTask.rollBack(taskResult);
				interrupt(taskChainIndex,taskChain,taskResult.getResult());
				return;
			}
			if(taskResult.isSuccess()) {
				//判断是执行新方法afterExecute还是过期方法callBack，规则见isNextRunDest方法详细定义
				boolean isExistAfterExecute = isNextRunMethod(baseTask.getClass(), "afterExecute", "callBack", new Class[]{TaskResult.class});
				if(isExistAfterExecute){
					baseTask.afterExecute(taskResult);
				}else{
					baseTask.callBack(taskResult);
				}
				
			}
			//完成当前环节状态的修改，返回下个环节信息
			TaskChain nextTaskChain = onAfterExecTaskChain(taskChainIndex,taskChain,taskResult);
			logger.debug("The {}th links of the process {} is complete,the service_name is {},the cluster_name is {}",
				taskChain.getExecuteOrder(),taskChainIndex.getTemplateTask().getName(),
				taskChainIndex.getServiceName(),taskChainIndex.getClusterName());
			//递归执行下一环节
			if(nextTaskChain != null) {
				onExecTaskChain(taskChainIndex,nextTaskChain);
			}else{
				//流程执行完进来
				logger.debug("The process {} is complete,the service_name is {},the cluster_name is {}",taskChainIndex.getTemplateTask().getName(),
						taskChainIndex.getServiceName(),taskChainIndex.getClusterName());
			}
		} catch (Exception e) {
			if(baseTask != null){
				//对于该处的若要抛出Exception，不需要在进rollBack,直接interrupt
				try {
					taskResult.setSuccess(false);
					taskResult.setResult(e.getMessage());
					baseTask.rollBack(taskResult);
				} catch (Exception e1) {
					e = e1;
				}
			}
			interrupt(taskChainIndex, taskChain, e.getMessage());
			return;
		}
	}
	/**
	 * 终止流程
	 * @param taskChainIndex	任务流实例信息
	 * @param taskChain	任务单元实例信息
	 * @param errMsg	提示错误信息
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午3:06:36 .
	 * @version 1.0 .
	 */
	private void interrupt(TaskChainIndex taskChainIndex,TaskChain taskChain,String errMsg){
		TaskResult taskResult = new TaskResult();
		taskResult.setSuccess(false);
		taskResult.setResult(errMsg);
		taskChain.setResult(errMsg);
		taskChain.setStatus(TaskExecuteStatus.FAILED);
		taskChain.setEndTime(new Date());
		taskChain.setUpdateUser(userId);
		this.taskChainService.updateBySelective(taskChain);
		taskChainIndex.setStatus(TaskExecuteStatus.FAILED);
		taskChainIndex.setEndTime(new Date());
		taskChainIndex.setUpdateUser(userId);
		this.taskChainIndexService.updateBySelective(taskChainIndex);
		logger.error("The {}th links of the process {} is error,the service_name is {},the cluster_name is {}",
				taskChain.getExecuteOrder(),taskChainIndex.getTemplateTask().getName(),
				taskChainIndex.getServiceName(),taskChainIndex.getClusterName(), new TaskExecuteException(errMsg));
	}
	
	/**
	 * 完成该环节状态的更改，并返回下一环节
	 * @param taskChainIndex
	 * @param taskChain
	 * @param taskResult
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午3:34:27 .
	 * @version 1.0 .
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public TaskChain onAfterExecTaskChain(TaskChainIndex taskChainIndex,TaskChain taskChain,TaskResult taskResult) throws JsonGenerationException, JsonMappingException, IOException {
		taskChain.setStatus(TaskExecuteStatus.SUCCESS);
		taskChain.setResult(taskResult.getResult());
		taskChain.setEndTime(new Date());
		taskChain.setUpdateUser(userId);
		this.taskChainService.updateBySelective(taskChain);
		TaskChain nextTaskChain = this.taskChainService.selectNextChainByIndexAndOrder(taskChain.getChainIndexId(),taskChain.getExecuteOrder()+1);
		//如果没有下一环节，代表流程结束
		if(null == nextTaskChain) {
			taskChainIndex.setStatus(TaskExecuteStatus.SUCCESS);
			taskChainIndex.setEndTime(new Date());
			taskChainIndex.setUpdateUser(userId);
			this.taskChainIndexService.updateBySelective(taskChainIndex);
			return null;
		}
		Object params = taskResult.getParams();
		String paramsJsonStr = toJson(params);
		if(!StringUtils.isEmpty(paramsJsonStr)){
			//上个环节更改后的Params结果将传给下个环节，注意：XXXTaskXXXService代码中尽量少删params
			nextTaskChain.setParams(paramsJsonStr);
			taskChain.setUpdateUser(userId);
			this.taskChainService.updateBySelective(nextTaskChain);
		}
		return nextTaskChain;
	}
	/**
	 * 执行任务单元前执行<br/>
	 * <ol><li>修改当前单元状态为执行中，现在开始执行</li>
	 * <li>修改后面所有环节状态为未执行</li></ol>
	 * @param taskChain
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午2:23:19 .
	 * @version 1.0 .
	 */
	private TaskChain onBeforeExecTaskChain(TaskChain taskChain) {
		if(null == taskChain)
			return taskChain;
		taskChain.setStatus(TaskExecuteStatus.DOING);
		taskChain.setStartTime(new Date());
		taskChain.setUpdateUser(userId);
		this.taskChainService.updateBySelective(taskChain);
		Map<String,Object> backTaskChainsMap = new HashMap<String,Object>();
		backTaskChainsMap.put("executeOrder", taskChain.getExecuteOrder());
		backTaskChainsMap.put("chainIndexId", taskChain.getChainIndexId());
		backTaskChainsMap.put("status", TaskExecuteStatus.UNDO);
		backTaskChainsMap.put("updateUser", userId);
		this.taskChainService.updateAfterDoingChainStatus(backTaskChainsMap);
		return taskChain;
	}
	
	/**
	 * 初始化任务流实例信息
	 * @param templateTaskId	流程模板ID
	 * @param params
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午6:36:07 .
	 * @version 1.0 .
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	private TaskChainIndex initTask(TemplateTask templateTask,List<TemplateTaskChain> templateTaskChains,Object params) throws JsonGenerationException, JsonMappingException, IOException {
		//创建任务流实例
		TaskChainIndex tci = new TaskChainIndex();
		tci.setTaskId(templateTask.getId());
		tci.setStatus(TaskExecuteStatus.UNDO);
		String serviceName = null;
		String clusterName = null;
		String paramstr = null;
		if(params != null && params instanceof Map) {
			Map<String,Object> paramsMap = (Map<String, Object>) params;
			if(!CollectionUtils.isEmpty(paramsMap)){
				paramstr = this.toJson(paramsMap);
				String serName = (String) paramsMap.get("serviceName");
				if(!StringUtils.isEmpty(serName))
					serviceName = serName;
				String cluName = (String) paramsMap.get("clusterName");
				if(!StringUtils.isEmpty(cluName))
					clusterName = cluName;
			}
		}
		if(StringUtils.isEmpty(serviceName))
			serviceName = MessageFormat.format("{0}-service-{1}", templateTask.getName(),System.currentTimeMillis());
		if(StringUtils.isEmpty(serviceName))
			clusterName = MessageFormat.format("{0}-cluster-{1}", templateTask.getName(),System.currentTimeMillis());
		tci.setServiceName(serviceName);
		tci.setClusterName(clusterName);
		tci.setCreateUser(userId);
		this.taskChainIndexService.insert(tci);
		//创建所有任务单元实例
		List<TaskChain> ttcs = new ArrayList<TaskChain>();
		for (TemplateTaskChain ttc : templateTaskChains) {
			TaskChain tc = new TaskChain();
			tc.setTaskId(ttc.getTaskId());
			tc.setTaskDetailId(ttc.getTaskDetailId());
			tc.setExecuteOrder(ttc.getExecuteOrder());
			tc.setChainIndexId(tci.getId());
			tc.setStatus(TaskExecuteStatus.UNDO);
			tc.setCreateUser(userId);
			//设置单元可重试次数
			TemplateTaskDetail ttd = this.templateTaskDetailService.selectById(tc.getTaskDetailId());
			tc.setRetry(ttd.getRetry());
			if(!StringUtils.isEmpty(paramstr))
				tc.setParams(paramstr);
			ttcs.add(tc);
		}
		this.taskChainService.insertBatch(ttcs);
		return this.taskChainIndexService.selectById(tci.getId());
	}
	/**
	 * 判断是执行新的方法还是过期方法
	 * 如果子类有新方法，执行新方法，没有，则检查过期方法，有，执行，没有，上父类规则还如此。
	 * @param clazz
	 * @param destMethodName	新方法
	 * @param deprecatedMethodName	过期的方法
	 * @param parameterTypes
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月27日, 下午7:23:22 .
	 * @version 1.0 .
	 */
	private boolean isNextRunMethod(Class clazz,String destMethodName,String deprecatedMethodName,Class<?>... parameterTypes){
		Method mtd = null;
		try {
			mtd = clazz.getDeclaredMethod(destMethodName, parameterTypes);
			if(mtd != null)
				return true;
		} catch (NoSuchMethodException | SecurityException e) {
		}finally{
			if(null == mtd){
				try {
					mtd = clazz.getDeclaredMethod(deprecatedMethodName, parameterTypes);
				} catch (NoSuchMethodException | SecurityException e1) {}
			}
		}
		if(mtd != null)
			return false;
		Class parentClazz = clazz.getSuperclass();
		if(parentClazz == Object.class)
			return false;
		return isNextRunMethod(parentClazz,destMethodName,deprecatedMethodName,parameterTypes);
	}
	private Map<String,Object> fromJson(String paramsJsonStr) throws JsonParseException, JsonMappingException, IOException{
		if(StringUtils.isEmpty(paramsJsonStr))
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String,Object> jsonResult = resultMapper.readValue(paramsJsonStr, Map.class);
		return jsonResult;
	}
	private String toJson(Object params) throws JsonGenerationException, JsonMappingException, IOException{
		if(params == null)
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		String jsonResult =  resultMapper.writeValueAsString(params);
		return jsonResult;
	}
}
