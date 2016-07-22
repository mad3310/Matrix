package com.letv.portal.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.letv.common.result.ResultObject;
import com.letv.common.session.Executable;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.IpUtil;
import com.letv.portal.model.UserLogin;
import com.letv.portal.proxy.ILoginProxy;
import com.letv.portal.service.impl.oauth.IOauthService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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

		Session session = login(clientId,clientSecret,request);

		if(session != null ) {
			logger.info("login success by client_id:{},client_secret:{}.",clientId,clientSecret);
			sessionService.runWithSession(session, "Usersession changed", new Executable<Session>() {
				@Override
				public Session execute() throws Throwable {
					return null;
				}
			});
			return true;
		}

		//login failed
		logger.info("login failed by client_id:{},client_secret:{}.",clientId,clientSecret);
		responseJson(request, response, "login failed by client_id:{"+clientId+"},client_secret:{"+clientSecret+"}.");
		return false;
	}

	private Session login(String clientId,String clientSecret,HttpServletRequest request) {
		if(StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret))
			return null;
		try {
			Map<String,Object> userDetailInfo = this.oauthService.getUserdetailinfo(clientId, clientSecret);
			String username = (String) userDetailInfo.get("username");
			String email = (String) userDetailInfo.get("email");
			UserLogin userLogin = new UserLogin();
			userLogin.setLoginName(username);
			userLogin.setLoginIp(IpUtil.getIp(request));
			userLogin.setEmail(email);
			Session session = this.loginProxy.saveOrUpdateUserAndLogin(userLogin);
			return session;
		} catch (Exception e) {
			return null;
		}
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

	private void responseJson(HttpServletRequest req, HttpServletResponse res, String message) {
		PrintWriter out = null;
		try {
			res.setContentType("text/html;charset=UTF-8");
			out = res.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("result", 2);
		map.put("data",null);
		map.put("alertMessage", null);
		map.put("msgs", message);
		out.append(JSON.toJSONString(map, SerializerFeature.WriteMapNullValue));
		out.flush();
	}

}