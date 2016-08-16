package com.letv.portal.rest.enumeration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

public enum RestAPIFormatter {

	//力度不够，阿里云做到具体哪个字段不存在，或不符合规范
	//接收请求参数无效	400
	ParamsInvalid(HttpStatus.BAD_REQUEST,"ParamsInvalid","Request params {0} invalid."),
	@Deprecated
	//请求出错	403	兼容以前接口中直接throw CommonException/ValidateException，未将异常转换为ApiException	后续将删除
	Error(HttpStatus.OK,"Error","Requests error, {0}"),
	//继续进行中	100
	//Doing(HttpStatus.CONTINUE,"Doing","{0} is error, {1}"),
	//无可用集群	400
	NoEffectiveCluster(HttpStatus.BAD_REQUEST,"NoEffectiveCluster","Server does not have a valid cluster."),
	//服务已经存在	403
	ServiceIsExist(HttpStatus.FORBIDDEN,"ServiceIsExist","{0} service already exists."),
	//服务不存在	403
	ServiceIsNonExist(HttpStatus.FORBIDDEN,"ServiceIsNonExist","{0} service does not exist."),
	//服务不可用	403
	ServiceIsNonUse(HttpStatus.FORBIDDEN,"ServiceIsNonUse","{0} service is not available."),
	//应用部署失败	403
	ServiceIsDeployError(HttpStatus.FORBIDDEN,"ServiceIsDeployError","{0} service deployment failed."),
	//容器列表为空	403
	ContainersIsEmpty(HttpStatus.FORBIDDEN,"ContainersIsEmpty","{0} service container list is empty."),
	//部署中	400
	Deploying(HttpStatus.BAD_REQUEST,"Deploying","{0} service deploying."),
	//oauth
	//没有提供HTTP请求头client_id。	400
	MissingClientId(HttpStatus.BAD_REQUEST,"MissingClientId","client_id does not exist in http header."),
	//没有提供HTTP请求头client_secret。	400
	MissingClientSecret(HttpStatus.BAD_REQUEST,"MissingClientSecret","client_secret does not exist in http header."),
	//授权失败	401
	Unauthorized(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authorization failure."),
	//SpringMVC框架部分处理
	//请求地址正确，请求method不正确	405
	RequestMethodNotSupported(HttpStatus.METHOD_NOT_ALLOWED,"RequestMethodNotSupported","Request method {0} not supported when access {1}."),
	//当方法参数标注@RequestParam/@PathVariable...进	400
	ParamtersNotCompletePresent(HttpStatus.BAD_REQUEST,"ParamtersNotCompletePresent","Required type {0} parameter '{1}' is not present when access {2}."),
	//当方法参数赋值不合法，比如方法定义param卫星为int，但接受到的是非int值进来	400
	TypeMismatch(HttpStatus.BAD_REQUEST,"TypeMismatch","Type not matched when access {0}."),
	//当方法参数为int类型，但客户端没有为该类型赋值。报错		400
	MethodInvocationUnLegal(HttpStatus.BAD_REQUEST,"MethodInvocationUnLegal","{0}"),
	//对于调用SpringMVC框架出现其他异常进行全面捕获,Spring自身出现异常是服务端报错异常	400
	InvalidHTTPRequest(HttpStatus.BAD_REQUEST,"InvalidHTTPRequest","Request invalid when access {0}."),
	//404
	AddressNotFound(HttpStatus.NOT_FOUND,"AddressNotFound","The requested address not found"),
	//500
	InternalServerError(HttpStatus.INTERNAL_SERVER_ERROR,"InternalServerError","Internal server error message.")
	;
	
	
	
	/**
	 * 格式化errorMessage数据	接收任意数量参数
	 * @param objects
	 * @author linzhanbo .
	 * @since 2016年8月11日, 下午3:47:59 .
	 * @version 1.0 .
	 */
	public RestAPIFormatter formatErrorMessage(Object...objects){
		errorMessage = MessageFormat.format(errorMessage, objects);
		return this;
	}
	/**
	 * 格式化errorMessage数据	接收spring validate 参数
	 * @param errors
	 * @author linzhanbo .
	 * @since 2016年8月11日, 下午3:53:41 .
	 * @version 1.0 .
	 */
	public RestAPIFormatter formatErrorMessage(List<ObjectError> errors){
		List<String> msgs = new ArrayList<String>();
		for (ObjectError error : errors) {
			msgs.add(((DefaultMessageSourceResolvable)error.getArguments()[0]).getDefaultMessage()+":"+error.getDefaultMessage());
		}
		return formatErrorMessage(msgs.toString());
	}
	
	
	private final HttpStatus httpStatus;
	private final String errorCode;
	private String errorMessage;
	private RestAPIFormatter(HttpStatus htStatus,String erCode,String erMessage) {
		this.httpStatus = htStatus;
		this.errorCode = erCode;
		this.errorMessage = erMessage;
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
