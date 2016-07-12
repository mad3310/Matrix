package com.letv.common.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.exception.ApiNotFoundException;
import com.letv.common.exception.CommonException;
import com.letv.common.exception.MatrixException;
import com.letv.common.exception.OauthException;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;


/**
 * 异常处理
 * 
 */
public class DefaultMappingExceptionResolver extends SimpleMappingExceptionResolver {
	private Logger logger = LoggerFactory.getLogger(DefaultMappingExceptionResolver.class);
	
	
	@Value("${error.email.to}")
	private String ERROR_MAIL_ADDRESS;
	
	@Value("${error.email.enabled}")
	private Boolean ERROR_MAIL_ENABLED;
	
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Autowired
	private SessionServiceImpl sessionManager;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest req, HttpServletResponse res, Object handler,
            Exception e) {
    	String error = e.getMessage();
    	if(e instanceof MatrixException) {
    		error = ((MatrixException) e).getUserMessage();
    		e = ((MatrixException) e).getE();
    	}
		e = transE(e);
		if(e instanceof OauthException || e instanceof CommonException || e instanceof ValidateException) {
			//do not send email.
			logger.debug(e.getMessage());
		} else if(Boolean.valueOf(ERROR_MAIL_ENABLED) ) {
			String stackTraceStr = com.letv.common.util.ExceptionUtils.getRootCauseStackTrace(e);
			String exceptionMessage = e.getMessage();
			sendErrorMail(req,error +":" +exceptionMessage,stackTraceStr);
			logger.error(error, e);
		}
    	String viewName = determineViewName(e, req);
		if (viewName == null) 
			return null;
		boolean isAjaxRequest = (req.getHeader("x-requested-with") != null)? true:false;
		
		String clientType = req.getHeader("clientType");
		
		if (isAjaxRequest || !StringUtils.isEmpty(clientType)) {
			responseJson(req,res,error);
			return null;
		} else {
			Integer statusCode = determineStatusCode(req, viewName);
			if (statusCode != null) {
				applyStatusCodeIfPossible(req, res, statusCode);
			}
			ModelAndView mav =  getModelAndView(viewName, e, req);
			mav.addObject("exception", error);
			mav.addObject("Exception", e);
			return mav;
		}
    }
    private Exception transE(Exception e) {
    	if(e instanceof TypeMismatchException) {
    		e = new ApiNotFoundException("参数传递异常");
    	} 
    	if(e instanceof HttpRequestMethodNotSupportedException) {
    		e = new ApiNotFoundException("请求URL方法不支持");
    	} 
    	return e;
    }

	/**
	 * 生成json并返回给客户端
	 * 
	 * @param req
	 * @param res
	 * @param message
	 */
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
	
	private void sendErrorMail(HttpServletRequest request,String exceptionMessage,String stackTraceStr)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("exceptionContent", stackTraceStr);
		params.put("requestUrl", request.getRequestURL());
		
		Session session = sessionManager.getSession();
		params.put("exceptionId", session == null ? "error session" : session.getUserName());
		
		String requestValue = getRequestValue(request);
		params.put("exceptionParams",  StringUtils.isBlank(requestValue) ? "无" : requestValue);
		params.put("exceptionMessage",  exceptionMessage == null ? "无" : exceptionMessage);
		
		params.put("hostIp", request.getRemoteHost());
		
		
		MailMessage mailMessage = new MailMessage("乐视云平台web-porta系统", ERROR_MAIL_ADDRESS,"异常错误发生","erroremail.ftl",params);
		try {
			defaultEmailSender.sendMessage(mailMessage);
		} catch (Exception e) {
			logger.error(getStackTrace(e));
		}
	}
	
	private String getStackTrace(Throwable t) 
	{
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        fillStackTrace(t, pw);
        return writer.toString();
    }
	
	private void fillStackTrace(Throwable t, PrintWriter pw)
    {
		Throwable ex = ExceptionUtils.getRootCause(t);
		if(null == ex)
			ex = t;
        ex.printStackTrace(pw);
    }
	
	private String getRequestValue(HttpServletRequest request)
	{
		StringBuilder sb = new StringBuilder();
		Map<String,Object> requestParams = (Map<String,Object>)request.getParameterMap();
		Set<String> keySets = requestParams.keySet();
		for(String key:keySets)
		{
			String[] valueArrays = (String[])requestParams.get(key);
			sb.append(key);
			sb.append("=");
			sb.append(getRequestItemValue(valueArrays));
			sb.append("&");
		}
		String result = sb.toString();
		return result;
		
	}
	
	private String getRequestItemValue(String[] valueArrays)
	{
		String valueStr = "";
		for(String value:valueArrays)
		{
			valueStr += value;
		}
		return valueStr;
	}
}
