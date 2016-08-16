<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*,java.io.IOException,
	com.letv.portal.rest.enumeration.RestAPIFormatter,
	com.letv.portal.rest.exception.ApiException,
	com.letv.common.util.JsonUtil,
	org.slf4j.Logger,
	org.slf4j.LoggerFactory"%>
<%
	Logger logger = LoggerFactory.getLogger(this.getClass());
	ApiException excpet = new ApiException(RestAPIFormatter.AddressNotFound);
	Map<String,String> errorMap = new HashMap<String,String>();
	errorMap.put("errorCode", excpet.getErrorCode());
	errorMap.put("errorMessage", excpet.getErrorMessage());
	String errorMsg = null;
	try {
		errorMsg = JsonUtil.toJson(errorMap);
	} catch (IOException e1) {
		logger.error("Format data to json string error", e1);
	}
	response.setContentType("application/x-javascript;charset=UTF-8");
	response.setStatus(excpet.getHttpStatus().value());
	out.clear();
	out.print(errorMsg);
	out.flush();
%>