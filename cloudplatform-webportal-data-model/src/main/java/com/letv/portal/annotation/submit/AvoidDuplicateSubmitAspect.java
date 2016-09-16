package com.letv.portal.annotation.submit;

import java.util.Calendar;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.CookieUtil;
import com.letv.mms.cache.ICacheService;
import com.letv.mms.cache.IMemcachedCache;
import com.letv.mms.cache.factory.CacheFactory;
import com.letv.mms.cache.impl.RemoteCacheServiceImpl;

@Aspect
@Component
public class AvoidDuplicateSubmitAspect {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	private static ICacheService<?> cacheService = CacheFactory.getCache();
	private static IMemcachedCache memcachedCache = null;
	
	static {
		if(cacheService instanceof RemoteCacheServiceImpl) {
			memcachedCache = ((RemoteCacheServiceImpl)cacheService).getCache();
		}
	}
	
	
	@Before("@annotation(sec)")
	public void execute(JoinPoint jp,AvoidDuplicateSubmit sec) {
		Object[] args = jp.getArgs();
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];
		
		boolean needSaveSession = sec.needSaveToken();
        if (needSaveSession) {
        	String uuid = UUID.randomUUID().toString();
            request.getSession(false).setAttribute("token", uuid);
            CookieUtil.addCookie(response, "token", uuid, 0);
        }

        boolean needRemoveSession = sec.needRemoveToken();
        if (needRemoveSession) {
        	String serverToken = (String) request.getSession(false).getAttribute("token");
        	Cookie c = CookieUtil.getCookieByName(request, "token");
     		String clientToken = c.getValue();
            if (isRepeatSubmit(serverToken, clientToken)) {
                throw new ValidateException("请勿重复提交！");
            }
            request.getSession(false).removeAttribute("token");
            if(null != memcachedCache) {
            	memcachedCache.delete(serverToken);
            }
        }
		
	}
	
	private boolean isRepeatSubmit(String serverToken, String clientToken) {
        if (serverToken == null) {
            return true;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        //memcached add 失败，即没有获取到锁，返回true
        if(null != memcachedCache && !memcachedCache.add(serverToken, 1, cal.getTime())) {
        	return true;
        }
        
        if (clientToken == null) {
            return true;
        }
        if (!serverToken.equals(clientToken)) {
            return true;
        }
        return false;
    }
	
}
