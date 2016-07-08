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
 * jackson扩展过滤属性	<br/>
 * 兼容jackson提供的实体内加@jacksonIgnore等属性
 * @author linzhanbo .
 * @since 2016年7月7日, 下午4:20:25 .
 * @version 1.0 .
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFilterProperties {
	/**
     * 要过滤的属性
     * @return
     */
    IncludeProperty[] includes() default @IncludeProperty(pojo = Object.class, names = "");

    /**
     * 允许的属性
     * @return
     */
    ExcludeProperty[] excluses() default @ExcludeProperty(pojo = Object.class, names = "");
}
