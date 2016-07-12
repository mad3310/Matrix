package com.letv.portal.model.task.service.impl;

import java.security.Provider.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.letv.common.exception.TaskExecuteException;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.MclusterStatus;
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
	
	@Autowired
	private ITemplateTaskService templateTaskService;
	@Autowired
	private ITemplateTaskDetailService templateTaskDetailService;
	@Autowired
	private ITemplateTaskChainService templateTaskChainService;
	@Autowired
	private ITaskChainService taskChainService;
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	
	@Override
	public TaskChainIndex init(String taskName,Object params) {
		
		if(null == taskName)
			throw new TaskExecuteException("taskId is null");
		
		TemplateTask task = this.templateTaskService.selectByName(taskName);
		if(null == task)
			throw new TaskExecuteException("task is null by name:" + taskName);
		return init(task.getId(), params);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public TaskChainIndex init(Long taskId,Object params) {
		
		if(null == taskId)
			throw new TaskExecuteException("taskId is null");
		
		List<TemplateTaskChain> ttcs = this.templateTaskChainService.selectByTemplateTaskId(taskId); //select TemplateTaskChain from table by task_template id order by execute field.
		
		if(null == ttcs || ttcs.isEmpty())
			throw new TaskExecuteException("TemplateTaskChain is null by taskId");
			
		//create TaskChainIndex
		TaskChainIndex tci = new TaskChainIndex();
		tci.setTaskId(taskId);
		tci.setStatus(TaskExecuteStatus.UNDO);
		if(params != null && params instanceof Map) {
			tci.setServiceName((String) ((Map<String,Object>)params).get("serviceName"));
			tci.setClusterName((String) ((Map<String,Object>)params).get("clusterName"));
		}
		this.taskChainIndexService.insert(tci);
		
		//create TaskChains
		for (TemplateTaskChain ttc : ttcs) {
			TaskChain tc = new TaskChain();
			tc.setTaskId(ttc.getTaskId());
			tc.setTaskDetailId(ttc.getTaskDetailId());
			tc.setExecuteOrder(ttc.getExecuteOrder());
			tc.setChainIndexId(tci.getId());
			tc.setStatus(TaskExecuteStatus.UNDO);
			TemplateTaskDetail ttd = this.templateTaskDetailService.selectById(tc.getTaskDetailId());
			tc.setRetry(ttd.getRetry());
			if(ttc.getExecuteOrder() == 1) {
				tc.setParams(this.transToString(params));
			}
			this.taskChainService.insert(tc);
		}
		return tci;
	}
	
	@Override
	@Async
	public void run(Long taskId) {
		run(taskId,null);
	}
	
	@Override
	@Async
	public void run(Long taskId,Object params) {
		run(init(taskId,params));
	}
	@Override
	@Async
	public void run(String taskName) {
		run(taskName,null);
	}
	
	@Override
	@Async
	public void run(String taskName,Object params) {
		run(init(taskName,params));
	}
	
	@Override
	@Async
	public void run(TaskChainIndex tci) {
		TaskChain tc = this.taskChainService.selectNextChainByIndexAndOrder(tci.getId(),1); //select first TaskChain or doing TaskChain from table by taskChain order。
		run(tc,tci);
	}
	
	@Override
	@Async
	public void run(TaskChain tc) {
		TaskChainIndex tci = this.taskChainIndexService.selectById(tc.getChainIndexId());
		run(tc,tci);
	}
	
	@Override
	public void run(TaskChain tc,TaskChainIndex tci) {
		tci.setStatus(TaskExecuteStatus.DOING);
		tci.setStartTime(new Date());
		this.taskChainIndexService.updateBySelective(tci);
		
		execute(tc,tci);
	}
	
	@Override
	public TaskChain beforeExecute(TaskChain tc) {
		if(null == tc)
			return tc;
		tc.setStatus(TaskExecuteStatus.DOING);
		tc.setStartTime(new Date());
		this.taskChainService.updateBySelective(tc);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("executeOrder", tc.getExecuteOrder());
		map.put("chainIndexId", tc.getChainIndexId());
		map.put("status", TaskExecuteStatus.UNDO);
		this.taskChainService.updateAfterDoingChainStatus(map);
		return tc;
	}
	
	@Override
	public TaskChain afterExecute(TaskChain tc,TaskResult tr) {
		if(tc == null || tr == null)
			throw new TaskExecuteException("afterExecute TaskChain or TaskResult is null");
			
		tc.setStatus(tr.isSuccess()?TaskExecuteStatus.SUCCESS:TaskExecuteStatus.FAILED);
		tc.setResult(tr.getResult());
		tc.setEndTime(new Date());
		this.taskChainService.updateBySelective(tc);
		
		if(!tr.isSuccess()) {
			TaskChainIndex tci = this.taskChainIndexService.selectById(tc.getChainIndexId());
			tci.setStatus(TaskExecuteStatus.FAILED);
			tci.setEndTime(new Date());
			this.taskChainIndexService.updateBySelective(tci);
			return null;
		}
		
		TaskChain nTc = this.taskChainService.selectNextChainByIndexAndOrder(tc.getChainIndexId(),tc.getExecuteOrder()+1);
		if(nTc == null) {
			TaskChainIndex tci = this.taskChainIndexService.selectById(tc.getChainIndexId());
			tci.setStatus(TaskExecuteStatus.SUCCESS);
			tci.setEndTime(new Date());
			this.taskChainIndexService.updateBySelective(tci);
		} else if(null != tr.getParams()){
			nTc.setParams(this.transToString(tr.getParams()));
			this.taskChainService.updateBySelective(nTc);
		}
		return nTc;
	}
	
	@Override
	public void execute(TaskChain tc,TaskChainIndex tci) {
		IBaseTaskService baseTask = null;
		TaskResult tr = new TaskResult();
		try {
			//修改当前环节状态为TaskExecuteStatus.DOING，剩下的环节状态为TaskExecuteStatus.UNDO
			tc = beforeExecute(tc);
			if(tc == null)
				throw new TaskExecuteException("execute TaskChain is null");
			//使用环节实例中环节详细ID获取该环节详细定义信息，后面使用该里面的beanName
			TemplateTaskDetail ttd = this.templateTaskDetailService.selectById(tc.getTaskDetailId());
			
			if(null == ttd)
				throw new TaskExecuteException("execute TemplateTaskDetail is null by id");

			String taskBeanName = ttd.getBeanName();
			String paramStr = tc.getParams();
			Map<String,Object> params = transToMap(paramStr);
			
			baseTask = (IBaseTaskService)getApplicationContext().getBean(taskBeanName);
			//将params传入进去，修改当前GCE状态为DbStatus.BUILDDING，GCE集群状态为MclusterStatus.BUILDDING
			baseTask.beforExecute(params);
			tr.setParams(params);
			tr = baseTask.execute(params);
			if(tr == null)
				throw new TaskExecuteException("task execute result is null");
			
			int retry = 1;
			while(retry < ttd.getRetry() && !tr.isSuccess()) {
				Thread.sleep(1000);
				tr = baseTask.execute(params);
				retry++;
			}
			if(!tr.isSuccess()) {
				baseTask.rollBack(tr);
			}
			if(tr.isSuccess()) {
				baseTask.callBack(tr);
			}
			
			tc = afterExecute(tc,tr);
			if(tc != null) {
				execute(tc,tci);
			}
		} catch (Exception e) {
			tr.setSuccess(false);
			e.printStackTrace();
			tr.setResult(e.getMessage());
			tc.setResult(e.getMessage());
			tc.setStatus(TaskExecuteStatus.FAILED);
			tc.setEndTime(new Date());
			this.taskChainService.updateBySelective(tc);
			tci.setStatus(TaskExecuteStatus.FAILED);
			tci.setEndTime(new Date());
			this.taskChainIndexService.updateBySelective(tci);
			if(baseTask != null)
				baseTask.rollBack(tr);
		} 
	}

	private Map<String,Object> transToMap(String params){
		if(StringUtils.isEmpty(params))
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String,Object> jsonResult = new HashMap<String,Object>();
		try {
			jsonResult = resultMapper.readValue(params, Map.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	private String transToString(Object params){
		if(params == null)
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		String jsonResult = "";
		try {
			jsonResult = resultMapper.writeValueAsString(params);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
}
