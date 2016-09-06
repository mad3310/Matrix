package com.letv.portal.annotation.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.letv.portal.enumeration.SecretKeyEnum;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SecurityValid {
	String paramKey() default "";//参数的key
	
	String signKey() default "";//签名的key

	SecretKeyEnum secretKey();//使用哪个secretKey
}
