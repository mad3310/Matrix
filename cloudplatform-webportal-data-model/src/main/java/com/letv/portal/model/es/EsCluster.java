package com.letv.portal.model.es;

import com.letv.common.model.BaseModel;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.UserModel;

public class EsCluster extends BaseModel {
	
	private static final long serialVersionUID = -8757063845109274144L;

	/*
	 * 集群名称
	 */
	private String clusterName;
	/*
	 * 集群状态
	 */
	private MclusterStatus status;
	/*
	 * 物理机集群id
	 */
	private Long hclusterId;
	/*
	 * 服务调用用户名
	 */
	private String adminUser;
	/*
	 * 服务调用密码
	 */
	private String adminPassword;
	
	private HclusterModel hcluster;

	private UserModel createUserModel;
	
	public HclusterModel getHcluster() {
		return hcluster;
	}
	public void setHcluster(HclusterModel hcluster) {
		this.hcluster = hcluster;
	}
	public UserModel getCreateUserModel() {
		return createUserModel;
	}
	public void setCreateUserModel(UserModel createUserModel) {
		this.createUserModel = createUserModel;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public MclusterStatus getStatus() {
		return status;
	}
	public void setStatus(MclusterStatus status) {
		this.status = status;
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
	
}
