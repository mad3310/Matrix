package com.letv.portal.service.oauth;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.AuthenticationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.letv.common.exception.OauthException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpsClient;
import com.letv.common.util.JsonUtil;
import com.letv.mms.cache.ICacheService;
import com.letv.mms.cache.factory.CacheFactory;
import com.letv.portal.proxy.ILoginProxy;
import com.letv.portal.service.ILoginService;
import com.letv.portal.service.impl.oauth.IOauthService;
import com.mysql.jdbc.StringUtils;


/**Program Name: OauthLoginApi <br>
 * Description:  提供基础的oauth api服务<br>
 * @author name: howie <br>
 * Written Date: 2015年7月15日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Service("oauthService")
public class OauthServiceImpl  implements IOauthService{
	
	public final static Logger logger = LoggerFactory.getLogger(OauthServiceImpl.class);
	public final static int OAUTH_API_RETRY_COUNT = 3;
	
	@Autowired
	public ILoginService loginManager;
	@Autowired
	public SessionServiceImpl sessionService;
	
	private ICacheService<?> cacheService = CacheFactory.getCache();
	
	@Autowired
	public ILoginProxy loginProxy;

	@Value("${oauth.auth.http}")
	public String OAUTH_AUTH_HTTP;
	@Value("${oauth.token.cache.expire}")
	public long OAUTH_TOKEN_CACHE_EXPIRE;
	
	public String retryOauthApi(String result,String url){
		int i = 1;
		while(StringUtils.isNullOrEmpty(result) && i<=OAUTH_API_RETRY_COUNT) {
			result = HttpsClient.sendXMLDataByGet(url,1000,1000);
			i++;
		}
		return result;
	}
	
	@Override
	public String getAccessToken(String clientId,String clientSecret,String code) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(OAUTH_AUTH_HTTP).append("/accesstoken?grant_type=authorization_code&code=").append(code).append("&client_id=").append(clientId).append("&client_secret=").append(clientSecret);
		logger.debug("getAccessToken :" + buffer.toString());
		String result = HttpsClient.sendXMLDataByGet(buffer.toString(),1000,1000);
		retryOauthApi(result,buffer.toString());
		if(StringUtils.isNullOrEmpty(result))
			throw new OauthException("长时间未操作，请重新登录");
		Map<String,Object> resultMap = this.transResult(result);
		return (String) resultMap.get("access_token");
	}
	
	@Override
	public Map<String,Object> getUserdetailinfo(String accessToken) {
		
		Map<String,Object>  resultMap = (Map<String, Object>) this.cacheService.get(accessToken, null);
		
		if(resultMap != null) 
			return resultMap;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(OAUTH_AUTH_HTTP).append("/userdetailinfo?access_token=").append(accessToken);
		logger.debug("getUserdetailinfo :" + buffer.toString());
		String result = HttpsClient.sendXMLDataByGet(buffer.toString(),1000,1000);
		retryOauthApi(result,buffer.toString());
		if(StringUtils.isNullOrEmpty(result))
			throw new OauthException("长时间未操作，请重新登录");
		resultMap = this.transResult(result);
		if(resultMap !=null)
			this.cacheService.set(accessToken, resultMap,OAUTH_TOKEN_CACHE_EXPIRE);
		
		return resultMap;
	}

	@Override
	public String getAuthorize(String clientId) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(OAUTH_AUTH_HTTP).append("/authorizenoredirect?response_type=code&client_id=").append(clientId);
		logger.debug("getAuthorize :" + buffer.toString());
		String result = HttpsClient.sendXMLDataByGet(buffer.toString(),1000,1000);
		retryOauthApi(result,buffer.toString());
		if(StringUtils.isNullOrEmpty(result))
			throw new OauthException("长时间未操作，请重新登录");
		Map<String,Object> resultMap = this.transResult(result);
		return (String) resultMap.get("code");
	}

	@Override
	public Map<String, Object> getUserdetailinfo(String clientId, String clientSecret) {
		return getUserdetailinfo(getAccessToken(clientId,clientSecret,getAuthorize(clientId)));
	}

	public Map<String,Object> transResult(String result){
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String,Object> jsonResult = new HashMap<String,Object>();
		try {
			jsonResult = resultMapper.readValue(result, Map.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	@Override
	public Map<String, Object> getUserInfo(String accessToken) throws AuthenticationException {
		//从缓存中获取
		Map<String,Object>  resultMap = (Map<String, Object>) this.cacheService.get(accessToken, null);
		if(!CollectionUtils.isEmpty(resultMap)) 
			return resultMap;
		String userinfoUrl = MessageFormat.format("{0}/userdetailinfo?access_token={1}", OAUTH_AUTH_HTTP,accessToken);
		logger.debug("getUserdetailinfo :" + userinfoUrl);
		String result = HttpsClient.sendXMLDataByGet(userinfoUrl,3000,5000);//暂不重构HttpsClient代码
		result = retryOauthApi(result,userinfoUrl);
		if(StringUtils.isNullOrEmpty(result))
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address, the returned data is empty",userinfoUrl));
		try {
			resultMap = new HashMap<String, Object>();
			resultMap = (Map<String, Object>) JsonUtil.fromJson(result,resultMap);
		} catch (Exception e) {
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address is error",userinfoUrl),e);
		}
		if(CollectionUtils.isEmpty(resultMap)){
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address, the returned data can't be formatted",userinfoUrl));
		}
		
		String username = (String) resultMap.get("username");
		String email = (String) resultMap.get("email");
		if(StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(email)){
			String error = (String) resultMap.get("error");
			String error_description = (String) resultMap.get("error_description");
			String errMsg = MessageFormat.format("Get the user information is error,when access_token is {0},errorCode:{1},error_description:{2}",accessToken, error,error_description);
			throw new AuthenticationException(errMsg);
		}
		return resultMap;
	}
	@Override
	public String getUserAccessToken(String clientId, String clientSecret,
			String authCode) throws AuthenticationException {
		String accessUrl = MessageFormat.format("{0}/accesstoken?grant_type=authorization_code&code={1}&client_id={2}&client_secret={3}", 
				OAUTH_AUTH_HTTP,authCode,clientId,clientSecret);
		logger.debug("getAccessToken :" + accessUrl);
		String result = HttpsClient.sendXMLDataByGet(accessUrl,3000,5000);//暂不重构HttpsClient代码
		result = retryOauthApi(result,accessUrl);
		if(StringUtils.isNullOrEmpty(result))
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address, the returned data is empty",accessUrl));
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap = (Map<String, Object>) JsonUtil.fromJson(result,resultMap);
		} catch (Exception e) {
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address is error",accessUrl),e);
		}
		if(CollectionUtils.isEmpty(resultMap)){
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address, the returned data can't be formatted",accessUrl));
		}
		String accessToken = (String) resultMap.get("access_token");
		if(StringUtils.isNullOrEmpty(accessToken)){
			String error = (String) resultMap.get("error");
			String error_description = (String) resultMap.get("error_description");
			String errMsg = MessageFormat.format("access token is error,when clientId is {0},errorCode:{1},error_description:{2}",clientId, error,error_description);
			throw new AuthenticationException(errMsg);
		}
		return accessToken;
	}
	@Override
	public String getUserAuthorizeCode(String clientId) throws AuthenticationException {
		String authorizeUrl = MessageFormat.format("{0}/authorizenoredirect?response_type=code&client_id={1}", OAUTH_AUTH_HTTP,clientId);
		logger.debug("getAuthorize :" + authorizeUrl);
		String result = HttpsClient.sendXMLDataByGet(authorizeUrl,3000,5000);//暂不重构HttpsClient代码
		result = retryOauthApi(result,authorizeUrl);
		if(StringUtils.isNullOrEmpty(result))
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address, the returned data is empty",authorizeUrl));
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap = (Map<String, Object>) JsonUtil.fromJson(result,resultMap);
		} catch (Exception e) {
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address is error",authorizeUrl),e);
		}
		if(CollectionUtils.isEmpty(resultMap)){
			throw new IllegalArgumentException(MessageFormat.format("Requests the {0} address, the returned data can't be formatted",authorizeUrl));
		}
		String code = (String) resultMap.get("code");
		if(StringUtils.isNullOrEmpty(code)){
			String error = (String) resultMap.get("error");
			String error_description = (String) resultMap.get("error_description");
			String errMsg = MessageFormat.format("authorize is error,when clientId is {0},errorCode:{1},error_description:{2}",clientId, error,error_description);
			throw new AuthenticationException(errMsg);
		}
		return code;
	}
	@Override
	public Map<String, Object> getUserInfo(String clientId, String secretId) throws IllegalArgumentException,AuthenticationException {
		String authCode = this.getUserAuthorizeCode(clientId);
		String accessToken  = this.getUserAccessToken(clientId,secretId,authCode);
		Map<String,Object> userDetailInfo = this.getUserInfo(accessToken);
		return userDetailInfo;
	}
	
}