/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;

import com.letv.common.model.BaseModel;
import com.letv.portal.model.UserModel;
import com.letv.portal.validation.annotation.GcePackageVersionFormatLimit;

/**
 * GCE应用包历史版本
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:10:59 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGcePackage extends BaseModel {

	private static final long serialVersionUID = -8384069424122356322L;

	/**
	 * 版本号
	 */
	private String version;
	/**
     * 描述
     */
    private String descn;

	/**
	 * 包后缀名
	 */
	private String suffix;

	/**
	 * S3 bucket名称
	 */
	private String bucketName;

	/**
	 * S3 key
	 */
	private String key;

	/**
	 * PORT_FORWARD
	 */
	private String portForward;

	/**
	 * GCE主键
	 */
	private Long gceId;

	/**
	 * GCE集群ID
	 */
	private Long gceclusterId;

	/**
	 * GCE镜像名称
	 */
	private String gceImageName;

	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * GCE名称	数据库无该字段，添加该字段只是为了方便前端validate
	 */
	private String gceName;
	
	private List<EcGceContainer> containers;
	private UserModel createUserModel;

	@GcePackageVersionFormatLimit(message = "版本号规范必须为x.x.x.x，例如1.1.1.12")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPortForward() {
		return portForward;
	}

	public void setPortForward(String portForward) {
		this.portForward = portForward;
	}
	
	public Long getGceId() {
		return gceId;
	}

	public void setGceId(Long gceId) {
		this.gceId = gceId;
	}

	public Long getGceclusterId() {
		return gceclusterId;
	}

	public void setGceclusterId(Long gceclusterId) {
		this.gceclusterId = gceclusterId;
	}

	public String getGceImageName() {
		return gceImageName;
	}

	public void setGceImageName(String gceImageName) {
		this.gceImageName = gceImageName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@NotNull(message="GCE名称为空")
	public String getGceName() {
		return gceName;
	}

	public void setGceName(String gceName) {
		this.gceName = gceName;
	}

	public List<EcGceContainer> getContainers() {
		return containers;
	}

	public void setContainers(List<EcGceContainer> containers) {
		this.containers = containers;
	}
	
	@Length(max = 300)
	public String getDescn() {
		return descn;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	public UserModel getCreateUserModel() {
		return createUserModel;
	}

	public void setCreateUserModel(UserModel createUserModel) {
		this.createUserModel = createUserModel;
	}

}
