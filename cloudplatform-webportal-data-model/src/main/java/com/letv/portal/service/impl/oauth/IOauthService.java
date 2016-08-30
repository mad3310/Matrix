package com.letv.portal.service.impl.oauth;

import java.util.Map;

import org.apache.http.auth.AuthenticationException;


/**Program Name: OauthLoginApi <br>
 * Description:  提供基础的oauth api服务<br>
 * @author name: howie <br>
 * Written Date: 2015年7月15日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IOauthService {
	
	@Deprecated
	String getAccessToken(String clientId, String clientSecret, String code);
	/**
	 * 获取用户访问token
	 * @param clientId
	 * @param clientSecret
	 * @param authCode
	 * @return
	 * @throws AuthenticationException
	 * @author linzhanbo .
	 * @since 2016年8月11日, 上午10:28:04 .
	 * @version 1.0 .
	 */
	String getUserAccessToken(String clientId, String clientSecret,String authCode) throws AuthenticationException;
	@Deprecated
	Map<String,Object> getUserdetailinfo(String accessToken);
	@Deprecated
	String getAuthorize(String clientId);
	/**
	 * 获取用户认证code
	 * @param clientId
	 * @return
	 * @throws AuthenticationException
	 * @author linzhanbo .
	 * @since 2016年8月11日, 上午10:27:58 .
	 * @version 1.0 .
	 */
	String getUserAuthorizeCode(String clientId) throws AuthenticationException;
	@Deprecated
	Map<String,Object> getUserdetailinfo(String clientId,String clientSecret);
	/**
	 * 获取用户信息
	 * @param clientId
	 * @param secretId
	 * @return
	 * @throws IllegalArgumentException	告诉运维异常
	 * @throws AuthenticationException
	 * @author linzhanbo .
	 * @since 2016年8月11日, 上午10:49:10 .
	 * @version 1.0 .
	 */
	Map<String,Object> getUserInfo(String clientId,String secretId) throws IllegalArgumentException,AuthenticationException;
	/**
	 *  获取用户信息
	 * @param accessToken
	 * @return
	 * @throws AuthenticationException
	 * @author linzhanbo .
	 * @since 2016年8月11日, 上午10:41:50 .
	 * @version 1.0 .
	 */
	Map<String,Object> getUserInfo(String accessToken) throws AuthenticationException;
}