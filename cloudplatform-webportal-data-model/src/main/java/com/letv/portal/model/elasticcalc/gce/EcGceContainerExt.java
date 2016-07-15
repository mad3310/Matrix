/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;

/**
 * GCE应用包容器扩展
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:37:31 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGceContainerExt extends BaseModel {

	private static final long serialVersionUID = -3679868452526250537L;
	/**
	 * GCE容器ID
	 */
    private Long containerId;

    /**
     * 绑定端口
     */
    private String bindPort;

    /**
     * 内部端口
     */
    private String innerPort;

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String descn;

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public String getBindPort() {
		return bindPort;
	}

	public void setBindPort(String bindPort) {
		this.bindPort = bindPort;
	}

	public String getInnerPort() {
		return innerPort;
	}

	public void setInnerPort(String innerPort) {
		this.innerPort = innerPort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescn() {
		return descn;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}
    
}
