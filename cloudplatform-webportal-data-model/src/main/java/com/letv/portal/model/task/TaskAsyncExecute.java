package com.letv.portal.model.task;

import com.letv.common.model.BaseModel;

/**
 * 异步推送类
 *
 */
public class TaskAsyncExecute extends BaseModel {

	private static final long serialVersionUID = 4894529482499154716L;

	/*
	 * 工作流id
	 */
	private Long taskChainId;
	/*
	 * 集群名称
	 */
	private String clusterName;
	/*
	 * 已重试次数，默认为0
	 */
	private Integer count;
	/*
	 * 描述
	 */
	private String descn;
	
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public Long getTaskChainId() {
		return taskChainId;
	}
	public void setTaskChainId(Long taskChainId) {
		this.taskChainId = taskChainId;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getDescn() {
		return descn;
	}
	public void setDescn(String descn) {
		this.descn = descn;
	}
	
}
