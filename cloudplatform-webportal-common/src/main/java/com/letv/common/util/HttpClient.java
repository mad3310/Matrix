package com.letv.common.util;

import com.mysql.jdbc.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Program Name: HttpClient <br>
 * Description: httpclient封装<br>
 * 
 * @author name: liuhao1 <br>
 *         Written Date: 2014年9月11日 <br>
 *         Modified By: <br>
 *         Modified Date: <br>
 */
@SuppressWarnings("deprecation")
public class HttpClient {
	private final static Logger logger = LoggerFactory
			.getLogger(HttpClient.class);

	public static String post(String url, Map<String, String> params) {
		return post(url, params, null, null);
	}

	public static String post(String url, Map<String, String> params,
			String username, String password) {
		return post(url,params,0,0,username,password);
	}
    public static String post(String url, Map<String, String> params,
                              int connectionTimeout, int soTimeout) {
        return post(url,params,connectionTimeout,soTimeout,null,null);
    }

	public static String post(String url, Map<String, String> params,
			int connectionTimeout, int soTimeout, String username,
			String password) {

		DefaultHttpClient httpclient = getHttpclient(connectionTimeout,
				soTimeout, username, password);
		String body;

		logger.info("create httppost:" + url);
		HttpPost post = postForm(url, params);

		body = invoke(httpclient, post);

		httpclient.getConnectionManager().shutdown();

		return body;
	}

	public static String postObject(String url, Object obj, String username,
			String password) {
		DefaultHttpClient httpclient = getHttpclient(username, password);
		String body = null;
		logger.info("create httppost:" + url);
		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setHeader("Content-Type", "application/json");
			httppost.setEntity(new StringEntity(obj.toString()));
			body = invoke(httpclient, httppost);
			httpclient.getConnectionManager().shutdown();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return body;
	}

    public static String put(String url,Map<String, String> params) {
        return put(url,params, null, null);
    }
    public static String put(String url, Map<String, String> params,String username, String password) {
        return put(url,params, 0, 0, username, password);
    }
    public static String put(String url,Map<String, String> params, int connectionTimeout, int soTimeout) {
        return put(url, params,connectionTimeout, soTimeout, null, null);
    }
    public static String put(String url,Map<String, String> params,int connectionTimeout,int soTimeout,String username,String password) {
        DefaultHttpClient httpclient = getHttpclient(connectionTimeout,
                soTimeout, username, password);
        String body;

        logger.info("create httpput:" + url);
        HttpPut put = putForm(url,params);
        body = invoke(httpclient, put);
        httpclient.getConnectionManager().shutdown();
        return body;
    }


	public static String get(String url) {
		return get(url, null, null);
	}

	public static String get(String url, String username, String password) {
		return get(url,0,0,username,password);
	}

    public static String get(String url, int connectionTimeout, int soTimeout) {
        return get(url, connectionTimeout, soTimeout, null, null);
    }
    
    /**
     * 单独传入参数的get请求调用
     * @param url 不带任何参数的url
     * @param params 请求参数
     * @param connectionTimeout 
     * @param soTimeout
     * @return
     * add by lisuxiao 2016-05-09
     */
    public static String get(String url, Map<String, String> params, int connectionTimeout, int soTimeout) {
    	return get(urlJoinParams(url, params), connectionTimeout, soTimeout, null, null);
    }

    public static String get(String url, int connectionTimeout, int soTimeout,
                             String username, String password) {
        DefaultHttpClient httpclient = getHttpclient(connectionTimeout,
                soTimeout, username, password);
        String body;

        logger.info("create httpget:" + url);
        HttpGet get = new HttpGet(url);
        body = invoke(httpclient, get);

        httpclient.getConnectionManager().shutdown();

        return body;
    }

	public static String detele(String url, String username, String password) {
		DefaultHttpClient httpclient = getHttpclient(username, password);
		String body;
		logger.info("create httpdelete:" + url);
		HttpDelete delete = new HttpDelete(url);
		body = invoke(httpclient, delete);
		httpclient.getConnectionManager().shutdown();
		return body;
	}
	public static String detele(String url, Map<String, String> params,String username, String password) {
		DefaultHttpClient httpclient = getHttpclient(username, password);
		String body;
        logger.info("create HttpDeleteWithBody:" + url);
        HttpDeleteWithBody deleteWithBody = deleteForm(url, params);
        body = invoke(httpclient, deleteWithBody);
        httpclient.getConnectionManager().shutdown();
        return body;
	}
	
	/**
	 * 根据传入的url和参数拼接get请求路径
	 * @param url 不带任何参数的url
	 * @param params 请求参数
	 * @return
	 * add by lisuxiao 2016-05-09
	 */
	private static String urlJoinParams(String url, Map<String, String> params) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(url);
		buffer.append("?");
		Set<Entry<String, String>> entrys = params.entrySet();
		for(Map.Entry<String, String> entry : entrys) {
			buffer.append(entry.getKey()).append("=").append(entry.getValue());
			buffer.append("&");
		}
		buffer.deleteCharAt(buffer.lastIndexOf("&"));
		return buffer.toString();
	}
	
	private static String invoke(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {
		HttpResponse response = null;
		String body = null;
		try {
			response = sendRequest(httpclient, httpost);
			body = paseResponse(response);
		} catch (IOException e) {
			e.printStackTrace();
			body = "{"
					+ "\"meta\": {"
					+ "\"code\": 400"
					+ "},"
					+ "\"response\": {"
					+ "\"message\": \""+e.getMessage()+"\""
					+ "}"
					+ "}";
		}
		return body;
	}

	private static String paseResponse(HttpResponse response) {
		logger.info("get response from http server..");
		if (response == null) {
			logger.info("get response from http server.. failed");
			return null;
		}
		HttpEntity entity = response.getEntity();

		logger.info("response status: " + response.getStatusLine());
		String charset = EntityUtils.getContentCharSet(entity);
		if(null != charset) {
			logger.info(charset);
		}

		String body = null;
		try {
			body = EntityUtils.toString(entity);
			logger.info(body);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return body;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient,
			HttpUriRequest httpost) throws ClientProtocolException, IOException {
		logger.info("execute post...");
		HttpResponse response = httpclient.execute(httpost);
		return response;
	}
	private static HttpPost postForm(String url, Map<String, String> params) {
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		if (params != null && !params.isEmpty()) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				nvps.add(new BasicNameValuePair(key, params.get(key)));
				logger.info("param-->" + key + ":" + params.get(key));
			}
		}

		try {
			logger.info("set utf-8 form entity to httppost");
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpost;
	}

	private static HttpPut putForm(String url, Map<String, String> params) {
		HttpPut httpput = new HttpPut(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		if (params != null && !params.isEmpty()) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				nvps.add(new BasicNameValuePair(key, params.get(key)));
				logger.info("param-->" + key + ":" + params.get(key));
			}
		}

		try {
			logger.info("set utf-8 form entity to httppost");
			httpput.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpput;
	}
	private static HttpDeleteWithBody deleteForm(String url, Map<String, String> params) {
		HttpDeleteWithBody deleteWithBody = new HttpDeleteWithBody(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		if (params != null && !params.isEmpty()) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				nvps.add(new BasicNameValuePair(key, params.get(key)));
				logger.info("param-->" + key + ":" + params.get(key));
			}
		}

		try {
			logger.info("set utf-8 form entity to httppost");
			deleteWithBody.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return deleteWithBody;
	}

    private static DefaultHttpClient getHttpclient(String username,
                                                   String password) {
        return getHttpclient(0,0,username,password);
    }

    private static DefaultHttpClient getHttpclient(int connectionTimeout,
			int soTimeout, String username, String password) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		if (!StringUtils.isNullOrEmpty(username)) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			UsernamePasswordCredentials usernamePassword = new UsernamePasswordCredentials(
					username, password);
			credsProvider.setCredentials(AuthScope.ANY, usernamePassword);
			httpclient.setCredentialsProvider(credsProvider);
		}
        if(0!=connectionTimeout) {
		/*
		 * 设置超时时间
		 */
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
            HttpConnectionParams.setSoTimeout(params, soTimeout);
        }

		/*
		 * 设置重试策略
		 */
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				/*
				 * 不进行重试
				 */
				/*
				 * if (executionCount >= 2) { // 如果超过最大重试次数，那么就不要继续了 return
				 * false; } if (exception instanceof NoHttpResponseException) {
				 * // 如果服务器丢掉了连接，那么就重试 return true; } if (exception instanceof
				 * SSLHandshakeException) { // 不要重试SSL握手异常 return false; }
				 * HttpRequest request = (HttpRequest) context
				 * .getAttribute(ExecutionContext.HTTP_REQUEST); boolean
				 * idempotent = !(request instanceof
				 * HttpEntityEnclosingRequest); if (idempotent) { //
				 * 如果请求被认为是幂等的，那么就重试 return true; }
				 */
				return false;
			}
		};
		httpclient.setHttpRequestRetryHandler(myRetryHandler);

		return httpclient;
	}

}
