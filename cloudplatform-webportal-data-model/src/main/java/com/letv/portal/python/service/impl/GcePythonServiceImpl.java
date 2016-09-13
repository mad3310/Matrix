package com.letv.portal.python.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.common.util.HttpClient;
import com.letv.portal.python.service.IGcePythonService;
 
@Service("gcePythonService")
public class GcePythonServiceImpl implements IGcePythonService{
	
	private final static Logger logger = LoggerFactory.getLogger(GcePythonServiceImpl.class);
	
	private final static String URL_HEAD = "http://";
	private final static String URL_PORT = ":8888";	
	private final static String URL_CHECK_PORT = ":6666";
	@Override
	public ApiResultObject checkGCEPackageMirrorCreateStatus(Map<String,String> params,String serverIp,String serverPort) {
		StringBuffer url = new StringBuffer();//eg:"http://10.75.139.35:9999/image/BuildQuery"
		url.append(URL_HEAD).append(serverIp).append(":").append(serverPort).append("/image/BuildQuery");
		String result = HttpClient.post(url.toString(), params);
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject createGCEPackageMirror(Map<String,String> params,String serverIp,String serverPort) {
		StringBuffer url = new StringBuffer();//eg:"http://10.75.139.35:9999/image/BuildPush"
		url.append(URL_HEAD).append(serverIp).append(":").append(serverPort).append("/image/BuildPush");
		String result = HttpClient.post(url.toString(), params);
		return new ApiResultObject(result,url.toString());
	}
	
	@Override
	public ApiResultObject createContainer(Map<String,String> params,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster");
		String result = HttpClient.post(url.toString(), params,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject checkContainerCreateStatus(String gceClusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/createResult/").append(gceClusterName);
		String result = HttpClient.get(url.toString(),username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject pushImage(Map<String, String> params, String ip, String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/server/image/pull");
		String result = HttpClient.post(url.toString(), params);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initZookeeper(String nodeIp,String port,Map<String,String> params) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(":").append(port).append("/admin/conf");
		
		String result = HttpClient.post(url.toString(), params);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initUserAndPwd4Manager(String nodeIp,String port,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(":").append(port).append("/admin/user");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("adminUser", username);
		map.put("adminPassword", password);
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject createContainer1(Map<String, String> params, String ip,String port,
			String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(":").append(port).append("/cluster");
		
		String result = HttpClient.post(url.toString(), params,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject syncContainer2(Map<String, String> params,String nodeIp1,String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster/sync");
		
		String result = HttpClient.post(url.toString(), params,adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject startCluster(String nodeIp1,String port, String adminUser,
			String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster/start");
		
		String result = HttpClient.post(url.toString(), null,adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject createContainer2(Map<String, String> params, String ip,
			String port, String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(":").append(port).append("/cluster/node");
		
		String result = HttpClient.post(url.toString(), params,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject CheckClusterStatus(String nodeIp1, String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster");
		
		String result = HttpClient.get(url.toString(),adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject nginxProxyConfig(Map<String, String> params, String ip,
			String port, String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(":").append(port).append("/cluster/node/config");
		
		String result = HttpClient.post(url.toString(), params,username,password);
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject stop(Map<String, String> params, String nodeIp1,String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster/stop");
		
		String result = HttpClient.post(url.toString(), params,adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject start(Map<String, String> params, String nodeIp1,String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster/start");
		
		String result = HttpClient.post(url.toString(), params,adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject restart(Map<String, String> params, String nodeIp1,String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster/reload");
		
		String result = HttpClient.post(url.toString(), params,adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject checkStatus(String nodeIp1,String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port).append("/cluster");
		
		String result = HttpClient.get(url.toString(),adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject capacity(Map<String, String> params, String ip,String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/server/containers/memory/add");
		String result = HttpClient.post(url.toString(), params,1000,10000,adminUser,adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject startGbalancer(Map<String, String> params,
			String ip, String port, String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(":").append(port).append("/glb/start");
		
		String result = HttpClient.post(url.toString(), params, adminUser, adminPassword);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject configMoxi(Map<String, String> params, String ip, String port) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(":").append(port).append("/cluster/node/config");
		
		String result = HttpClient.post(url.toString(), params);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject startMoxi(String ip, String port) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(":").append(port).append("/cluster/node/start");
		
		String result = HttpClient.post(url.toString(), null);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public String checkClusterCount(String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_CHECK_PORT).append("/containerCluster/sync");
		String result = HttpClient.get(url.toString(),name,password);
		return result;
	}
	@Override
	public ApiResultObject deleteCluster(String clusterName, String hostIp,
			String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/containerCluster?containerClusterName=").append(clusterName);
		String result = HttpClient.detele(url.toString(),name,password);
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject checkClusterDeleteStatus(String clusterName,
			String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_CHECK_PORT).append("/containerCluster/status/").append(clusterName);
		String result = HttpClient.get(url.toString(),name,password);
		return new ApiResultObject(result,url.toString());
	}

}
