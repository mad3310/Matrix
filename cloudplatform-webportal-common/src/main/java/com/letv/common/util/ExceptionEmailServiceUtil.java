package com.letv.common.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.email.bean.MailMessage;
import com.letv.common.email.impl.DefaultEmailSender;

@Service("exceptionEmailServiceUtil")
public class ExceptionEmailServiceUtil {

	@Value("${error.email.from}")
	private String errorEmailFrom;

	@Value("${error.email.to}")
	private String errorEmailTo;

	@Value("${error.email.enabled}")
	private String errorEmailEnabledStr;
	private boolean errorEmailEnabled;
	
	@Autowired
	private DefaultEmailSender defaultEmailSender;

	@PostConstruct
	public void init() {
		errorEmailEnabled = Boolean.parseBoolean(errorEmailEnabledStr);
	}

	public void sendErrorEmail(String exceptionMessage, String exceptionContent, String function) {
		Map<String, Object> mailMessageModel = new HashMap<String, Object>();
		mailMessageModel.put("exceptionMessage", exceptionMessage);
		mailMessageModel.put("exceptionContent", exceptionContent);
		mailMessageModel.put("requestUrl", "功能: " + function);
		sendErrorEmail(mailMessageModel);
	}
	
	public void sendErrorEmail(Map<String, Object> mailMessageModel) {
		if (errorEmailEnabled) {
			MailMessage mailMessage = new MailMessage(errorEmailFrom,
					errorEmailTo, "异常错误发生", "erroremail.ftl", mailMessageModel);
			mailMessage.setHtml(false);
			defaultEmailSender.sendMessage(mailMessage);
		}
	}
	
	public void sendExceptionEmail(Exception exception, String function, Long userId, String contextMessage) {
		Map<String, Object> mailMessageModel = new HashMap<String, Object>();
		mailMessageModel.put("requestUrl", "功能: " + function);
		mailMessageModel.put("exceptionId", "用户ID：" + userId);
		mailMessageModel.put("exceptionParams", "上下文信息：" + contextMessage);
		String exceptionMessage = exception.getMessage();
		mailMessageModel.put("exceptionMessage", exceptionMessage);
		mailMessageModel.put("exceptionContent",
				ExceptionUtils.getRootCauseStackTrace(exception));

		sendErrorEmail(mailMessageModel);
	}


}
