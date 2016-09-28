package com.letv.portal.model.task;

import com.letv.common.model.BaseModel;

/**
 * 
 * 任务单元详细定义信息
 * 
 * @author linzhanbo .
 * @since 2016年7月26日, 下午3:57:45 .
 */
public class TemplateTaskDetail extends BaseModel {

	private static final long serialVersionUID = 4318996252018673374L;
	
	private String name;
	private String descn;
	private String beanName;
	private String params;
	private String taskType;
	
	private int retry;
	private int asyncRetry;//异步重试
	private int version;
	
	public int getAsyncRetry() {
		return asyncRetry;
	}
	public void setAsyncRetry(int asyncRetry) {
		this.asyncRetry = asyncRetry;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescn() {
		return descn;
	}
	public void setDescn(String descn) {
		this.descn = descn;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
}
