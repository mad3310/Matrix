/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.common.util.jacksonext.springframework.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.util.jacksonext.FilterPropertyHandler;
import com.letv.common.util.jacksonext.impl.JavassistFilterPropertyHandler;

/**
 * 捕获SpringMVC的controller方法，添加JSON串过滤AOP
 * 
 * @author linzhanbo .
 * @since 2016年7月7日, 下午5:46:38 .
 * @version 1.0 .
 */
@Aspect
public class JsonFilterPropertyAspect {
	public static final Logger logger = LoggerFactory
			.getLogger(JsonFilterPropertyAspect.class);
	/**
	 * 切入对象
	 * 切入com.letv.portal.controller包及子包下所有类的所有方法
	 * 
	 * @author linzhanbo .
	 * @since 2016年7月8日, 下午2:10:11 .
	 * @version 1.0 .
	 */
	@Pointcut("execution(* com.letv.portal.controller..*(..))")
	private void anyMethod() {
	}

	/**
	 * 任意方法的环绕通知
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 * @author linzhanbo .
	 * @since 2016年7月8日, 下午2:01:07 .
	 * @version 1.0 .
	 */
	@Around("anyMethod()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("JsonFilterPropertyAspect watch");
		}
		Object returnVal = pjp.proceed();// 执行目标方法
		try {
			FilterPropertyHandler filterPropertyHandler = new JavassistFilterPropertyHandler(
					true);
			Method method = ((MethodSignature) pjp.getSignature()).getMethod();
			// 过滤方法属性
			returnVal = filterPropertyHandler.filterProperties(method,
					returnVal);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return returnVal;
	}
}
