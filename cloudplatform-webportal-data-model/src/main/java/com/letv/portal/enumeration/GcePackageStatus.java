/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.enumeration;

/**
 * GCE应用包状态
 * 
 * @author linzhanbo .
 * @since 2016年6月29日, 上午9:42:46 .
 * @version 1.0 .
 */
public enum GcePackageStatus implements ByteEnum {
	//TODO	镜像包构建过程中，状态只有一个BUILDDING，不明显，后期改造，每个环节一个状态
	DEFAULT(0), 
	RUNNING(1), 
	BUILDDING(2), 
	BUILDFAIL(3), 
	AUDITFAIL(4), 
	ABNORMAL(5), 
	NORMAL(6);

	private final Integer value;

	private GcePackageStatus(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return this.value;
	}
}
