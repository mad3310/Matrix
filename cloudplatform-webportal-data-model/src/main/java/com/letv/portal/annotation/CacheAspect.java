package com.letv.portal.annotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.letv.mms.cache.ICacheService;
import com.letv.mms.cache.factory.CacheFactory;

@Aspect
@Component
public class CacheAspect {
	
	private final static Logger logger = LoggerFactory.getLogger(CacheAspect.class);
	
	private ICacheService<?> cacheService = CacheFactory.getCache();
	
	
	@Around("@annotation(cache)")
	public Object getData(ProceedingJoinPoint pjp, Cache cache) {
		StringBuilder builder = new StringBuilder();
		Object[] obj = pjp.getArgs();
		builder.append(cache.namespace());
		for (Object o : obj) {
			builder.append(JSONObject.toJSONString(o));
		}
		Matcher matcher = Pattern.compile("\\w+").matcher(builder.toString());
		builder.setLength(0);
		while(matcher.find()) {
			builder.append(matcher.group());
		}
		if(builder.length()>200) {//控制key长度，默认最大key长度为250
			builder.setLength(200);
		}
         
        Object memValue = cacheService.get(builder.toString(), null);
         
        if(memValue!=null){
            logger.info("cache获取到{}", builder.toString());
            return memValue;            
        }
        Object object = null;
		try {
			object = pjp.proceed();//执行该方法
			logger.info("调用服务获取到{}", builder.toString());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
         
        cacheService.set(builder.toString(), object, cache.expiration());
        return object;
	}
	
}
