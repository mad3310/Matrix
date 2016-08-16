/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.rest.resolver;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.style.StylerUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.support.HandlerMethodInvocationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.letv.common.email.bean.MailMessage;
import com.letv.common.exception.CommonException;
import com.letv.common.exception.MatrixException;
import com.letv.common.exception.ValidateException;
import com.letv.common.session.Session;
import com.letv.common.spring.DefaultMappingExceptionResolver;
import com.letv.common.util.ExceptionUtils;
import com.letv.common.util.JsonUtil;
import com.letv.portal.rest.enumeration.RestAPIFormatter;
import com.letv.portal.rest.exception.ApiException;
import com.letv.portal.rest.model.RestExceptModel;

/**
 * 对外API异常处理
 * @author linzhanbo .
 * @since 2016年8月10日, 上午10:41:50 .
 * @version 1.0 .
 */
public class RestAPIExceptionResolver extends DefaultMappingExceptionResolver {
	
	private Logger logger = LoggerFactory.getLogger(RestAPIExceptionResolver.class);
	
	private String NOTICE_WARN_MAIL_ADDRESS = "linzhanbo@le.com";
	
	private Boolean NOTICE_WARN_MAIL_ENABLED = true;
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest req,
			HttpServletResponse res, Object handler, Exception e) {
		//捕获所有拦截器和controller异常
		String error = e.getMessage();
    	if(e instanceof MatrixException) {
    		error = ((MatrixException) e).getUserMessage();
    		e = ((MatrixException) e).getE();
    	}
    	RestExceptModel rex = format2RestExc(e,req);
    	int statusCode = rex.getHttpStatus().value();
    	String requestAddr = req.getRequestURL().toString();
    	if(statusCode < 500){
    		String warnMsg = MessageFormat.format("Request url: \"{0}\" warning, httpStatus:\"{1}\", errorCode:\"{2}\", errorMessage:\"{3}\"", 
    				requestAddr,rex.getHttpStatus().value(),rex.getErrorCode(),rex.getErrorMessage());
    		if(rex.getException() != null)
        		logger.warn(warnMsg,rex.getException());
    		else
    			logger.warn(warnMsg);
    		Exception except = rex.getException();
    		if(except != null){
    			if(except.getClass().getPackage().getName().indexOf("org.springframework.web")>-1 ||
    					except.getClass().getPackage().getName().indexOf("org.springframework.http")>-1){
    				if(Boolean.valueOf(NOTICE_WARN_MAIL_ENABLED)){
    					Exception ex = rex.getException();
    		    		String stackTraceStr = ExceptionUtils.getRootCauseStackTrace(ex);
    		    		String exceptionMessage = ex.getMessage();
    					sendWarnNoticeMail(req,exceptionMessage,stackTraceStr);
    				}
    			}
    		}
    	} else if(Boolean.valueOf(ERROR_MAIL_ENABLED) ) {
    		Exception ex = rex.getException();
    		String stackTraceStr = ExceptionUtils.getRootCauseStackTrace(ex);
    		String exceptionMessage = ex.getMessage();
			sendErrorMail(req,exceptionMessage,stackTraceStr);
			logger.error(error, ex);
    	}
    	Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put("errorCode", rex.getErrorCode());
		errorMap.put("errorMessage", rex.getErrorMessage());
    	String errorMsg = null;
		try {
			errorMsg = JsonUtil.toJson(errorMap);
		} catch (IOException e1) {
			logger.error("Format data to json string error", e1);
		}
    	String viewName = determineViewName(e, req);
		ModelAndView mav =  getModelAndView(viewName, e, req);
		/*mav.addObject("exception", e.getMessage());
		mav.addObject("Exception", e);*/
		mav.addObject("state",rex.getHttpStatus().value());
		mav.addObject("errorMsg",errorMsg);
		return mav;
	}

	private RestExceptModel format2RestExc(Exception ex,HttpServletRequest req) {
		RestExceptModel rex = new RestExceptModel();
		ApiException aexcept = null;
		String path = req.getRequestURL().toString();
		//请求地址正确，请求method不正确
		if(ex instanceof HttpRequestMethodNotSupportedException){
			HttpRequestMethodNotSupportedException rmnsexcept = (HttpRequestMethodNotSupportedException) ex;
			aexcept = new ApiException(RestAPIFormatter.RequestMethodNotSupported.formatErrorMessage(rmnsexcept.getMethod(),path));
			rex.setApiException(aexcept);
		//当方法参数标注@RequestParam/@PathVariable...进
		} else if(ex instanceof MissingServletRequestParameterException){
			MissingServletRequestParameterException msrqexcept = (MissingServletRequestParameterException) ex;
			aexcept = new ApiException(RestAPIFormatter.ParamtersNotCompletePresent.formatErrorMessage(
					msrqexcept.getParameterType(),msrqexcept.getParameterName(),path));
			rex.setApiException(aexcept);
		//当方法参数赋值不合法，比如方法定义param卫星为int，但接受到的是非int值进来
		} else if(ex instanceof TypeMismatchException){
			aexcept = new ApiException(RestAPIFormatter.TypeMismatch.formatErrorMessage(path));
			rex.setApiException(aexcept);
		//当方法参数为int类型，但客户端没有为该类型赋值。报错
		} else if(ex instanceof HandlerMethodInvocationException){
			HandlerMethodInvocationException miexcept = (HandlerMethodInvocationException) ex;
			aexcept = new ApiException(RestAPIFormatter.MethodInvocationUnLegal.formatErrorMessage(miexcept.getMessage()));
			rex.setApiException(aexcept);
		//对于调用SpringMVC框架出现其他异常进行全面捕获,Spring自身出现异常是服务端报错异常
		//spring-web-3.1.0.RELEASE.jar和spring-webmvc-3.1.0.RELEASE.jar包下路径所有定义异常位置都已此开头
		//TODO:以防对SpringMVC捕获的但是对用户关心、程序员不了解这个异常，同时防止以后升级SpringMVC后出现新的异常，我们对未知异常做已记录，并发通知邮件给程序员，告诉程序员用户调接口时，出现了这个异常，程序员收到这个异常后，判断是否需要处理这个异常
		//告诉用户，如请求Accept不合法，如果需要告诉用户，则继续在上面else if后捕获该异常，告诉用户相应的Http状态码，错误内容。	linzhanbo	2016.8.12 14:58
		} else if(ex.getClass().getPackage().getName().indexOf("org.springframework.web")>-1 ||
				ex.getClass().getPackage().getName().indexOf("org.springframework.http")>-1
				){
			aexcept = new ApiException(RestAPIFormatter.InvalidHTTPRequest.formatErrorMessage(path));
			rex.setApiException(aexcept);
			rex.setException(ex);
		} else if(ex instanceof ApiException){
			aexcept = (ApiException) ex;
			rex.setHttpStatus(aexcept.getHttpStatus());
			rex.setErrorCode(aexcept.getErrorCode());
			rex.setErrorMessage(aexcept.getErrorMessage());
		//兼容以前未转换异常为ApiException接口，不建议不转换异常	待调整完以前接口以后将删除该else if判断，后续开发必须转换异常为ApiException
		} else if(ex instanceof CommonException || ex instanceof ValidateException){
			aexcept = new ApiException(RestAPIFormatter.Error.formatErrorMessage(ex.getMessage()));
			rex.setApiException(aexcept);
		}
		else{
			//ConversionNotSupportedException、HttpMessageNotWritableException、操作数据库失败、freemark失败等
			aexcept = new ApiException(RestAPIFormatter.InternalServerError);
			rex.setApiException(aexcept);
			rex.setException(ex);
		}
		return rex;
	}
	
	protected void sendWarnNoticeMail(HttpServletRequest request,String exceptionMessage,String stackTraceStr)
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
		
		
		MailMessage mailMessage = new MailMessage("乐视云平台web-porta系统", NOTICE_WARN_MAIL_ADDRESS,"Rest API异常警告发生","warnnoticeemail.ftl",params);
		try {
			defaultEmailSender.sendMessage(mailMessage);
		} catch (Exception e) {
			logger.error(getStackTrace(e));
		}
	}
}
