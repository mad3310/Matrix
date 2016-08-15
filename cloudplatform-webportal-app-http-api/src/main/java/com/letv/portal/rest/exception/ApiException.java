/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.rest.exception;

import org.springframework.http.HttpStatus;

import com.letv.portal.rest.enumeration.RestAPIFormatter;

/**
 * 异常
 * @author linzhanbo .
 * @since 2016年8月10日, 下午5:46:17 .
 * @version 1.0 .
 */
public class ApiException extends RuntimeException {
	/**
	 * 状态码
	 */
	private HttpStatus httpStatus;
	/**
	 * 异常代码
	 */
	private String errorCode;
	/**
	 * 异常描述
	 */
	private String errorMessage;
	
	public ApiException(RestAPIFormatter raenum){
		super();
		this.httpStatus = raenum.getHttpStatus();
		this.errorCode = raenum.getErrorCode();
		this.errorMessage = raenum.getErrorMessage();
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
