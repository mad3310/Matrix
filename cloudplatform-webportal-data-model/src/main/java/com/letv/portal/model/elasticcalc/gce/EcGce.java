/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import javax.validation.constraints.Pattern;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.letv.common.model.BaseModel;
import com.letv.portal.validation.annotation.IdValid;
import com.letv.portal.validation.annotation.NumberLimit;

/**
 * GCE
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:01:19 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGce extends BaseModel {

	private static final long serialVersionUID = 7880591318544198735L;

    /**
     * GCE名称
     */
    private String gceName;

    /**
     * 描述
     */
    private String descn;

    /**
     * 地域
     */
    private Long areaId;

    /**
     * 可用区ID
     */
    private Long hclusterId;

    /**
     * 服务类型
     */
    private String type;

    /**
     * LOG_ID
     */
    private Long logId;

    /**
     * 内存大小
     */
    private Long memorySize;

    /**
     * 实例数
     */
    private Integer instanceNum;

    /**
     * 状态
     */
    private Integer status;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z_0-9]{1,15}$",message = "内容必须以字母开头，允许字母数字下划线，长度在2-16字节内")
	public String getGceName() {
		return gceName;
	}
    @Length(max = 50)
	public String getDescn() {
		return descn;
	}
    //不传
	public Long getAreaId() {
		return areaId;
	}
    @IdValid(service = "hclusterService",message = "物理机集群id不合法")
	public Long getHclusterId() {
		return hclusterId;
	}

	public String getType() {
		return type;
	}
	public Long getLogId() {
		return logId;
	}

	@NumberLimit(limits = {1073741824L,2147483648L,4294967296L},message = "内存大小必须在1073741824,2147483648,4294967296之中")
	public Long getMemorySize() {
		return memorySize;
	}
	//TODO
	@NumberLimit(limits = {1,2,3,4,5},message = "购买数量必须在1-5之中")
	public Integer getInstanceNum() {
		return instanceNum;
	}

	public Integer getStatus() {
		return status;
	}

	public void setGceName(String gceName) {
		this.gceName = gceName;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public void setHclusterId(Long hclusterId) {
		this.hclusterId = hclusterId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public void setMemorySize(Long memorySize) {
		this.memorySize = memorySize;
	}

	public void setInstanceNum(Integer instanceNum) {
		this.instanceNum = instanceNum;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
    
	

}
