package com.letv.portal.python.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.common.util.HttpClient;
import com.letv.portal.python.service.IEsPythonService;
 
@Service("esPythonService")
public class EsPythonServiceImpl implements IEsPythonService{
	
	private final static Logger logger = LoggerFactory.getLogger(EsPythonServiceImpl.class);
	
	private final static String URL_HEAD = "http://";
	private final static String URL_PORT = ":8888";	
	private final static String ES_PORT = ":9999";	
	private final static int connectionTimeout = 5000;	
	private final static int soTimeout = 10000;	
	
	@Override
	public ApiResultObject createContainer(Map<String,String> params,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster");
		String result = HttpClient.post(url.toString(), params, connectionTimeout, soTimeout, username, password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject checkContainerCreateStatus(String gceClusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/createResult/").append(gceClusterName);
		String result = HttpClient.get(url.toString(), connectionTimeout, soTimeout, username, password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initZookeeper(String nodeIp, Map<String,String> params) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/admin/conf");
		
		String result = HttpClient.post(url.toString(), params, connectionTimeout, soTimeout);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initUserAndPwd4Manager(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/admin/user");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("adminUser", username);
		map.put("adminPassword", password);
		
		String result = HttpClient.post(url.toString(), map, connectionTimeout, soTimeout, username,password);
		return new ApiResultObject(result,url.toString());
	}
	
	@Override
	public ApiResultObject initEsCluster(String nodeIp, Map<String,String> params, String adminUser,
			String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/elasticsearch/cluster/init");
		
		String result = HttpClient.post(url.toString(), params, connectionTimeout, soTimeout, adminUser, adminPassword);
		return new ApiResultObject(result,url.toString());
	}
	
	@Override
	public ApiResultObject syncEsCluster(String nodeIp, Map<String,String> params, String adminUser,
			String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/elasticsearch/cluster/sync");
		
		String result = HttpClient.post(url.toString(), params, connectionTimeout, soTimeout, adminUser, adminPassword);
		return new ApiResultObject(result,url.toString());
	}
	
	@Override
	public ApiResultObject initEsContainer(String nodeIp, Map<String,String> params, String adminUser,
			String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/elasticsearch/node/init");
		
		String result = HttpClient.post(url.toString(), params, connectionTimeout, soTimeout, adminUser, adminPassword);
		return new ApiResultObject(result,url.toString());
	}


	@Override
	public ApiResultObject configEsContainer(String nodeIp, String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/elasticsearch/config");
		String result = HttpClient.post(url.toString(), null, connectionTimeout, soTimeout, adminUser, adminPassword);
		return new ApiResultObject(result,url.toString());
	}
	
	@Override
	public ApiResultObject startElesticSearch(String nodeIp, String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(ES_PORT).append("/elasticsearch/start");
		String result = HttpClient.post(url.toString(), null, connectionTimeout, soTimeout, adminUser, adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	
}
