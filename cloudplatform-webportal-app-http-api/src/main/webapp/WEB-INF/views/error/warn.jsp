<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"%>
<%
	String errorMsg = (String)request.getAttribute("errorMsg");
	Integer state = (Integer)request.getAttribute("state");
	response.setContentType("application/x-javascript;charset=UTF-8");
	response.setStatus(state);
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