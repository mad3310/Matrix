/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.validation.Validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

import com.letv.portal.validation.annotation.GcePackageVersionFormatLimit;

/**
 * GCE应用版本号限制
 * @author linzhanbo .
 * @since 2016年7月1日, 上午9:34:50 .
 * @version 1.0 .
 */
public class GcePackageVersionFormatLimitValidator implements
		ConstraintValidator<GcePackageVersionFormatLimit, String> {
	
	@Override
	public void initialize(GcePackageVersionFormatLimit constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(!StringUtils.isEmpty(value)){
			//校验版本号是否符合x.x.x.x语法
			String reg = "^\\d+.\\d+.\\d+.\\d+$";
			return Pattern.compile(reg).matcher(value).matches();
		}
		return false;
	}

}
