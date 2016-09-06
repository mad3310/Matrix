package com.letv.portal.annotation.security;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.MD5;

@Aspect
@Component
public class SecurityAspect {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;

	
	@Before("@annotation(sec)")
	public void isValid(JoinPoint jp,SecurityValid sec) {
		Object[] args = jp.getArgs();
		HttpServletRequest request = (HttpServletRequest) args[0];
		String conf = (String)request.getParameter(sec.paramKey());
		String sign = (String)request.getParameter(sec.signKey());
		StringBuffer buffer = new StringBuffer();
		buffer.append(conf);
		buffer.append(sec.secretKey().getKey());
		MD5 m = new MD5();
		String calcuSign = m.getMD5ofStr(buffer.toString()).toLowerCase();
		if(!calcuSign.equals(sign)) {
			throw new ValidateException("该用户操作未授权");
		}
	}
	
	public static void main(String[] args) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("");
		buffer.append("e47179301b5f4e7bb3610db3e95eeaf4");
		MD5 m = new MD5();
		m.getMD5ofStr(buffer.toString()).toLowerCase();
	}
}
