package com.letv.common.exception;

/**
 *
 * @author yangjz
 */
public class ValidateException extends RuntimeException {
	
	private String userMessage;
	private String errorCode;
	public ValidateException() {
	}

	public ValidateException(String msg) {
		super(msg);
	}
	public ValidateException(String msg,String errorCode) {
		super(msg);
	}
	public ValidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateException(Throwable cause) {
		super(cause);
	}
	
	public String getUserMessage() {
		return userMessage;
	}
}
