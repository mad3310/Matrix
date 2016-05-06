package com.letv.portal.model.gce;

import com.letv.common.model.BaseModel;
import com.letv.portal.enumeration.GceImageStatus;
import com.letv.portal.model.UserModel;
import com.letv.portal.validation.annotation.StringLimit;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GceImage extends BaseModel {
	
	private static final long serialVersionUID = -7999485658204466572L;

	private Long owner;//所属用户
	
	private String url;
	private String tag;
	private String name;
	private String type;//jetty,nginx
	private GceImageStatus status;
	private String  logUrl;
	private String descn;//镜像描述
	private String netType;
	
	private UserModel createUserModel;

	public Long getOwner() {
		return owner;
	}
    @NotNull
    //@URL
	public String getUrl() {
		return url;
	}
    @NotNull
    @Length(max = 50)
	public String getTag() {
		return tag;
	}

    @NotNull
    @Length(max = 50)
	public String getName() {
		return name;
	}
    @NotNull
    @StringLimit(limits = {"jetty","nginx"},message = "类型必须在jetty和nginx之内")
	public String getType() {
		return type;
	}

	public GceImageStatus getStatus() {
		return status;
	}

	public String getLogUrl() {
		return logUrl;
	}
    @Length(max = 200)
	public String getDescn() {
		return descn;
	}
    @NotNull
    @StringLimit(limits = {"bridge","ip"},message = "网络类型必须在bridge和ip之内")
	public String getNetType() {
		return netType;
	}

	public UserModel getCreateUserModel() {
		return createUserModel;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStatus(GceImageStatus status) {
		this.status = status;
	}

	public void setLogUrl(String logUrl) {
		this.logUrl = logUrl;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public void setCreateUserModel(UserModel createUserModel) {
		this.createUserModel = createUserModel;
	}
}
