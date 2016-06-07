package com.letv.portal.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;
/**
 * Program Name: HclusterModel <br>
 * Description:  物理机集群<br>
 * @author name: wujun <br>
 * Written Date: 2014年10月21日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class HclusterModel extends BaseModel{
	private static final long serialVersionUID = 3497965985607790962L;
	
	private String hclusterName; //集群名称
	private Integer status; //状态:
	private Integer descn; //描述:
	private String hclusterNameAlias;//别名
	
	private String type;//集群用途
	private Long areaId;//地区id
	
	
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	public String getHclusterNameAlias() {
		return hclusterNameAlias;
	}
	public void setHclusterNameAlias(String hclusterNameAlias) {
		this.hclusterNameAlias = hclusterNameAlias;
	}
	public Integer getDescn() {
		return descn;
	}
	public void setDescn(Integer descn) {
		this.descn = descn;
	}
	public String getHclusterName() {
		return hclusterName;
	}
	public void setHclusterName(String hclusterName) {
		this.hclusterName = hclusterName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
