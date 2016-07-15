/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;

/**
 * GCE扩展服务
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:08:05 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGceExt extends BaseModel {
	
	private static final long serialVersionUID = 8222540211335468606L;

	/**
	 * GCE主键
	 */
	private Long gceId;

	/**
	 * RDS主键
	 */
	private Long rdsId;

	/**
	 * OCS主键
	 */
	private Long ocsId;

	public Long getGceId() {
		return gceId;
	}

	public void setGceId(Long gceId) {
		this.gceId = gceId;
	}

	public Long getRdsId() {
		return rdsId;
	}

	public void setRdsId(Long rdsId) {
		this.rdsId = rdsId;
	}

	public Long getOcsId() {
		return ocsId;
	}

	public void setOcsId(Long ocsId) {
		this.ocsId = ocsId;
	}
	
}
