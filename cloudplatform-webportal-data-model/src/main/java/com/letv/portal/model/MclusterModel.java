package com.letv.portal.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;

/**Program Name: MclusterModel <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月12日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class MclusterModel extends BaseModel {
	
	private static final long serialVersionUID = 8873122802478974943L;
	
	private String mclusterName; //名称
	private String adminUser;
	private String adminPassword;
	private Integer status; //状态
	
	private String sstPwd; //gbalancer监控密码
	private Integer type; //手动创建、自动创建
	
	private Long hclusterId;
	private HclusterModel hcluster;
	
	private UserModel createUserModel;
	//集群是否可备份
	private boolean canBackup;
	
	public boolean getCanBackup() {
		return canBackup;
	}
	public void setCanBackup(boolean canBackup) {
		this.canBackup = canBackup;
	}
	public String getMclusterName() {
		return mclusterName;
	}
	public void setMclusterName(String mclusterName) {
		this.mclusterName = mclusterName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	
	public UserModel getCreateUserModel() {
		return createUserModel;
	}
	public void setCreateUserModel(UserModel createUserModel) {
		this.createUserModel = createUserModel;
	}
	
	public String getSstPwd() {
		return sstPwd;
	}
	public void setSstPwd(String sstPwd) {
		this.sstPwd = sstPwd;
	}
	
	public Long getHclusterId() {
		return hclusterId;
	}
	public void setHclusterId(Long hclusterId) {
		this.hclusterId = hclusterId;
	}
	public HclusterModel getHcluster() {
		return hcluster;
	}
	public void setHcluster(HclusterModel hcluster) {
		this.hcluster = hcluster;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
