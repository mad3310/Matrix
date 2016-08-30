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
	ApiException excpet = new ApiException(RestAPIFormatter.InternalServerError);
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
<%--
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*,
	com.letv.common.result.ResultObject,
	com.letv.common.exception.ValidateException,
	com.letv.common.exception.CommonException,
	com.alibaba.fastjson.JSON,
	com.alibaba.fastjson.serializer.SerializerFeature"%>
<% response.setContentType("application/x-javascript;charset=UTF-8");%>
<%
	Exception ex = (Exception) request.getAttribute("Exception");
	ResultObject callbackResult = new ResultObject();
	if(ex instanceof CommonException){
		callbackResult.setResult(3);
		callbackResult.setAlertMessage(ex.getMessage());
	//ValidateException可以归属到else返回，但为了表示和DefaultMappingExceptionResolver
	//类doResolveException对ValidateException做了特殊处理保持一致，因此在这里分开
	} else if(ex instanceof ValidateException){
		callbackResult.setResult(0);
		callbackResult.setAlertMessage(ex.getMessage());
	} else{
		callbackResult.setResult(0);
		callbackResult.setAlertMessage(ex.getMessage());
	}
	Map<String,Object> map = new HashMap<String,Object>();
	map.put("result", callbackResult.getResult());
	map.put("data", null);
	map.put("alertMessage", callbackResult.getAlertMessage());
	map.put("msgs", new Object[]{});
	String json = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
	out.flush();
%>
<%=json%>
 --%>