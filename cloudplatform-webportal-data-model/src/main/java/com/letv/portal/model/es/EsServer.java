package com.letv.portal.model.es;

import java.util.List;

import com.letv.common.model.BaseModel;
import com.letv.portal.enumeration.EsStatus;

public class EsServer extends BaseModel {
	
	private static final long serialVersionUID = -7999485658204466572L;

	/*
	 * es名称
	 */
	private String EsName;
	/*
	 * 状态
	 */
	private EsStatus status;
	/*
	 * 描述
	 */
	private String descn;
	/*
	 * 物理机集群id
	 */
	private Long hclusterId;
	/*
	 * es集群
	 */
	private EsCluster esCluster;
	/*
	 * es集群id
	 */
	private Long esClusterId;
	/*
	 * 内存大小
	 */
	private Long memorySize;
	/*
	 * es节点
	 */
	private List<EsContainer> esContainers;
	
	public Long getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(Long memorySize) {
		this.memorySize = memorySize;
	}
	public Long getEsClusterId() {
		return esClusterId;
	}
	public void setEsClusterId(Long esClusterId) {
		this.esClusterId = esClusterId;
	}
	public String getEsName() {
		return EsName;
	}
	public void setEsName(String esName) {
		EsName = esName;
	}
	public EsStatus getStatus() {
		return status;
	}
	public void setStatus(EsStatus status) {
		this.status = status;
	}
	public String getDescn() {
		return descn;
	}
	public void setDescn(String descn) {
		this.descn = descn;
	}
	public Long getHclusterId() {
		return hclusterId;
	}
	public void setHclusterId(Long hclusterId) {
		this.hclusterId = hclusterId;
	}
	public EsCluster getEsCluster() {
		return esCluster;
	}
	public void setEsCluster(EsCluster esCluster) {
		this.esCluster = esCluster;
	}
	public List<EsContainer> getEsContainers() {
		return esContainers;
	}
	public void setEsContainers(List<EsContainer> esContainers) {
		this.esContainers = esContainers;
	}
	
}
