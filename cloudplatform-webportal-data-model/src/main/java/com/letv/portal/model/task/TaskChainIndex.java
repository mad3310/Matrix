package com.letv.portal.model.task;

import java.util.Date;

import com.letv.common.model.BaseModel;

/**
 * 
 * 任务流实例信息
 * 
 * @author linzhanbo .
 * @since 2016年7月26日, 下午4:02:42 .
 * @version 1.0 .
 */
public class TaskChainIndex extends BaseModel {

	private static final long serialVersionUID = -3034786226461925814L;
	
	public Long taskId;
	private TaskExecuteStatus status;
	
	private Date startTime;
	private Date endTime;
	private TemplateTask templateTask;
	
	private String serviceName;
	private String clusterName;
	
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public TaskExecuteStatus getStatus() {
		return status;
	}
	public void setStatus(TaskExecuteStatus status) {
		this.status = status;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public TemplateTask getTemplateTask() {
		return templateTask;
	}
	public void setTemplateTask(TemplateTask templateTask) {
		this.templateTask = templateTask;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
}
