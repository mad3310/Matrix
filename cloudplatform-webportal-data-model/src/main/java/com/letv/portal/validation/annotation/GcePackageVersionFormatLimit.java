/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.letv.portal.validation.Validator.GcePackageVersionFormatLimitValidator;

/**
 * GCE应用版本号限制
 * @author linzhanbo .
 * @since 2016年6月30日, 下午6:36:59 .
 * @version 1.0 .
 */
@Constraint(validatedBy = GcePackageVersionFormatLimitValidator.class) //具体的实现
@Target( { java.lang.annotation.ElementType.METHOD,
	java.lang.annotation.ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface GcePackageVersionFormatLimit {
	String message() default "{Gce.package.format.wrongful}"; //提示信息,可以固定写,也可以填写国际化
	Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
	
}
