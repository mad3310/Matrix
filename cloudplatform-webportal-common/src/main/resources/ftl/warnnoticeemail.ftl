*为优化Rest API异常捕获代码，对不确定、防止升级Spring框架引来的未知异常未捕获，在这里对该异常进行特意告知程序员，程序员收到该邮件后，分析异常，确定是否需要对该异常进行特殊处理，以告诉用户更精确的异常错误，并返回对应Http状态码、errorCode、errorMessage给用户。如果不需要，同样需要将该异常放到通用提示错误代码中，告知用户通用的请求失败。

诸位好：

请求ip：${hostIp}

请求url:  ${requestUrl}

请求参数:  ${exceptionParams}

exception sequence id:  ${exceptionId}

对外接口系统发生异常：   ${exceptionMessage}

异常stack：
${exceptionContent}

谢谢