/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;

/**
 * GCE应用包镜像
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:17:13 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGcePackageImage extends BaseModel {

	private static final long serialVersionUID = 6600794159350222116L;
	/**
	 * 镜像名称
	 */
	private String name;

	/**
	 * 镜像地址
	 */
	private String url;

	/**
	 * 镜像拥有者
	 */
	private Long owner;

	/**
	 * 网络类型
	 */
	private String netType;

	/**
	 * GCE主键
	 */
	private Long gceId;

	/**
	 * GCE应用版本ID
	 */
	private Long gcePackageId;

	/**
	 * 状态
	 */
	private Integer status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public Long getGceId() {
		return gceId;
	}

	public void setGceId(Long gceId) {
		this.gceId = gceId;
	}

	public Long getGcePackageId() {
		return gcePackageId;
	}

	public void setGcePackageId(Long gcePackageId) {
		this.gcePackageId = gcePackageId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
