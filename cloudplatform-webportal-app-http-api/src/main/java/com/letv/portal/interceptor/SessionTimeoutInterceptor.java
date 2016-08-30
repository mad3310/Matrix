package com.letv.portal.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.letv.common.session.Executable;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.IpUtil;
import com.letv.portal.model.UserLogin;
import com.letv.portal.proxy.ILoginProxy;
import com.letv.portal.rest.enumeration.RestAPIFormatter;
import com.letv.portal.rest.exception.ApiException;
import com.letv.portal.service.impl.oauth.IOauthService;

/**
 * 处理session超时的拦截器
 */
@Component
public class SessionTimeoutInterceptor  implements HandlerInterceptor{
	private final static Logger logger = LoggerFactory.getLogger(SessionTimeoutInterceptor.class);

	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IOauthService oauthService;
	@Autowired
	private ILoginProxy loginProxy;

	public String[] allowUrls;

	public void setAllowUrls(String[] allowUrls) {
		this.allowUrls = allowUrls;
	}

	private boolean allowUrl(HttpServletRequest request) {
		String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
		if("/".equals(requestUrl)) {
			return true;
		}
		//特殊url过滤
		if(null != allowUrls && allowUrls.length>=1) {
			for(String url : allowUrls) {
				if(requestUrl.contains(url)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
							 Object arg2) throws Exception {

		if(allowUrl(request))
			return true;
		String clientId = request.getHeader("client_id");
		String clientSecret = request.getHeader("client_secret");
		if(StringUtils.isEmpty(clientId))
			throw new ApiException(RestAPIFormatter.MissingClientId);
		if(StringUtils.isEmpty(clientSecret))
			throw new ApiException(RestAPIFormatter.MissingClientSecret);
		
		Session session = login(clientId,clientSecret,request);
		//如果login不抛出异常，session必然有值
		logger.info("login success by client_id:{},client_secret:{}.",clientId,clientSecret);
		sessionService.runWithSession(session, "Usersession changed", new Executable<Session>() {
			@Override
			public Session execute() throws Throwable {
				return null;
			}
		});
		return true;
	}

	private Session login(String clientId,String clientSecret,HttpServletRequest request) {
		Map<String,Object> userDetailInfo = new HashMap<String, Object>();
		try {
			userDetailInfo = this.oauthService.getUserInfo(clientId, clientSecret);
		} catch (AuthenticationException e) {
			throw new ApiException(RestAPIFormatter.Unauthorized);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		String username = (String) userDetailInfo.get("username");
		String email = (String) userDetailInfo.get("email");
		UserLogin userLogin = new UserLogin();
		userLogin.setLoginName(username);
		userLogin.setLoginIp(IpUtil.getIp(request));
		userLogin.setEmail(email);
		Session session = this.loginProxy.saveOrUpdateUserAndLogin(userLogin);
		return session;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
								HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
						   Object arg2, ModelAndView arg3) throws Exception {
	}

}
