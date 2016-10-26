package com.letv.portal.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.letv.common.util.IpUtil;
import com.letv.portal.model.UserLogin;
import com.letv.portal.service.impl.oauth.IOauthService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.letv.common.result.ResultObject;
import com.letv.common.session.Executable;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.ConfigUtil;
import com.letv.portal.proxy.ILoginProxy;

/**
 * 处理session超时的拦截器
 */
@Component
public class SessionTimeoutInterceptor  implements HandlerInterceptor{
	private final static Logger logger = LoggerFactory.getLogger(SessionTimeoutInterceptor.class);
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	@Autowired
	private ILoginProxy loginProxy;

	@Autowired
	private IOauthService oauthService;
	
	public String[] allowUrls;//还没发现可以直接配置不拦截的资源，所以在代码里面来排除
	
	public void setAllowUrls(String[] allowUrls) {
		this.allowUrls = allowUrls;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");  
		
		//特殊url过滤
		if(null != allowUrls && allowUrls.length>=1) {
			for(String url : allowUrls) {  
				if(requestUrl.contains(url)) {  
					return true;  
				}  
			}
		}
		if("/".equals(requestUrl)) {
			return true; 
		}
		Session session = (Session) request.getSession().getAttribute(Session.USER_SESSION_REQUEST_ATTRIBUTE);
		String clientType = request.getHeader("clientType");
		if(!StringUtils.isEmpty(clientType)) {
			String token = request.getHeader("authtoken");
			if(StringUtils.isEmpty(token)  || "".equals(token)) {
				responseJson(request,response,"长时间未操作，请重新登录");
				return false;
			}

			session  = this.validateToken(token, IpUtil.getIp(request));
		}
		if(session == null ) {
			logger.debug("please login");
			boolean isAjaxRequest = (request.getHeader("x-requested-with") != null)? true:false;
			
			if (isAjaxRequest) {
				responseJson(request,response,"长时间未操作，请重新登录");
			} else {
				RequestDispatcher rd = request.getRequestDispatcher("/toLogin");
				rd.forward(request, response);
			}
			return false;
		} else {
			sessionService.runWithSession(session, "Usersession changed", new Executable<Session>(){
				@Override
				public Session execute() throws Throwable {
					return null;
				}
			});
		}
		return true;
	}
	private Session validateToken(String token,String ip) {
		//判断是否为移动端机器，如果是，通过token验证
		Map<String, Object> userDetailInfo = this.oauthService.getUserdetailinfo(token);

		if(userDetailInfo == null)
			return null;

		String username = (String) userDetailInfo.get("username");
		String email = (String) userDetailInfo.get("email");
		if(email.endsWith("le.com"))
			email = email.replace("le.com","letv.com");

		if(StringUtils.isEmpty(username))
			return null;

		UserLogin userLogin = new UserLogin();
		userLogin.setLoginName(username);
		userLogin.setLoginIp(ip);
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
	
	private void responseJson(HttpServletRequest req, HttpServletResponse res, String message) {
    	PrintWriter out = null;
		try {
			res.setContentType("text/html;charset=UTF-8");
			out = res.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultObject resultObject = new ResultObject(0);
		resultObject.addMsg(message);
		out.append(JSON.toJSONString(resultObject, SerializerFeature.WriteMapNullValue));
		out.flush();
	}

}