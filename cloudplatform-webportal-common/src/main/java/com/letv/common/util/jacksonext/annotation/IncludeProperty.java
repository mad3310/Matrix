/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.common.util.jacksonext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * jackson扩展	 包含	 json串属性
 * 
 * @author linzhanbo .
 * @since 2016年7月7日, 下午4:18:05 .
 * @version 1.0 .
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeProperty {
	/**
	 * 目标POJO
	 * 
	 * @return
	 */
	Class<?> pojo();

	/**
	 * 允许序列化的属性名数组
	 * 
	 * @return
	 */
	String[] names();
}
