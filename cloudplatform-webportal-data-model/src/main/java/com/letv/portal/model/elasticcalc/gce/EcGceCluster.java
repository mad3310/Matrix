/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;

/**
 * GCE应用包集群
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:31:24 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGceCluster extends BaseModel {

	private static final long serialVersionUID = 465468652039937084L;
	/**
	 * 集群名称
	 */
	private String clusterName;

	/**
	 * gbalancer监控密码
	 */
	private String sstPassword;

	/**
	 * 可用区ID
	 */
	private Long hclusterId;

	/**
	 * ADMIN_USER
	 */
	private String adminUser;

	/**
	 * ADMIN_PASSWORD
	 */
	private String adminPassword;

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

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getSstPassword() {
		return sstPassword;
	}

	public void setSstPassword(String sstPassword) {
		this.sstPassword = sstPassword;
	}

	public Long getHclusterId() {
		return hclusterId;
	}

	public void setHclusterId(Long hclusterId) {
		this.hclusterId = hclusterId;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
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
