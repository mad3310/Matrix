/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.enumeration;

/**
 * GCE应用包镜像状态
 * @author linzhanbo .
 * @since 2016年6月29日, 下午6:10:10 .
 * @version 1.0 .
 */
public enum GcePackageImageStatus implements ByteEnum {
	AVAILABLE(1),  
	NOTAVAILABLE(2);
	
	private final Integer value;
	
	private GcePackageImageStatus(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
}
