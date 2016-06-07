package com.letv.portal.model.common;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;
/**
 * 地区信息
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AreaModel extends BaseModel{
	private static final long serialVersionUID = 3497965985607790962L;
	
	private String code; //地区编码
	private String name; //地区名称
	private Long parentId; //父节点id
	private String descn;
	
	public String getDescn() {
		return descn;
	}
	public void setDescn(String descn) {
		this.descn = descn;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
}
