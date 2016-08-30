/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.rest.model;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.letv.portal.rest.exception.ApiException;

/**
 * 接口异常实体类
 * @author linzhanbo .
 * @since 2016年8月10日, 下午4:05:44 .
 * @version 1.0 .
 */
public class RestExceptModel implements Serializable {
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
	/**
	 * 异常对象
	 */
	private Exception exception;
	public RestExceptModel() {
		super();
	}
	public void setApiException(ApiException aexcept) {
		this.httpStatus = aexcept.getHttpStatus();
		this.errorCode = aexcept.getErrorCode();
		this.errorMessage = aexcept.getErrorMessage();
	}
	public RestExceptModel(HttpStatus httpStatus, String errorCode,
			String errorMessage, Exception exception) {
		super();
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.exception = exception;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public Exception getException() {
		return exception;
	}


	public void setException(Exception exception) {
		this.exception = exception;
	}
}
