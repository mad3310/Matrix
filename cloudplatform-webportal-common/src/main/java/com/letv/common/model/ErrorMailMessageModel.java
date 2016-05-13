package com.letv.common.model;

import java.util.HashMap;
import java.util.Map;

public class ErrorMailMessageModel {
	
	private String requestUrl;
    private String exceptionId;
    private String exceptionParams;
    private String exceptionMessage;
    private String exceptionContent;
	
	public ErrorMailMessageModel(String exceptionId, String requestUrl, 
			String exceptionParams, String exceptionMessage, String exceptionContent) {
		this.requestUrl = requestUrl;
		this.exceptionId = exceptionId;
		this.exceptionParams = exceptionParams;
		this.exceptionMessage = exceptionMessage;
		this.exceptionContent = exceptionContent;
	}
	
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public String getExceptionId() {
		return exceptionId;
	}

	public String getExceptionParams() {
		return exceptionParams;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public String getExceptionContent() {
		return exceptionContent;
	}

	public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("requestUrl", requestUrl);
        map.put("exceptionId", exceptionId);
        map.put("exceptionParams", exceptionParams);
        map.put("exceptionMessage", exceptionMessage);
        map.put("exceptionContent", exceptionContent);
        return map;
    }
}
