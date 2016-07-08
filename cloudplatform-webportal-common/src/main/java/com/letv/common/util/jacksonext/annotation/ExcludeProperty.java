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
 * jackson扩展 排除 json串属性
 * 
 * @author linzhanbo .
 * @since 2016年7月7日, 下午4:21:16 .
 * @version 1.0 .
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeProperty {
	/**
	 * 要忽略字段的POJO <br>
	 * 
	 * @return
	 */
	Class<?> pojo();

	/**
	 * 要忽略的字段名 <br>
	 * 
	 * @return
	 */
	String[] names();
}
