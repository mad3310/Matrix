package com.letv.portal.model.task;

import com.letv.common.model.BaseModel;

/**
 * 任务流模板定义信息
 * 
 * @author linzhanbo .
 * @since 2016年7月26日, 下午3:54:05 .
 * @version 1.0 .
 */
public class TemplateTask extends BaseModel {

	private static final long serialVersionUID = 1343462845762405347L;
	
	private String name;
	private String descn;
	private int version;
	private String taskType;
	
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
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
}
