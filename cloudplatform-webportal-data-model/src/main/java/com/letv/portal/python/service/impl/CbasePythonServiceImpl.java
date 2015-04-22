package com.letv.portal.python.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.util.HttpClient;
import com.letv.portal.python.service.ICbasePythonService;

@Service("cbasePythonService")
public class CbasePythonServiceImpl implements ICbasePythonService {

	private final static Logger logger = LoggerFactory
			.getLogger(CbasePythonServiceImpl.class);

	private final static String URL_HEAD = "http://";
	private final static String URL_PORT = ":8888";

	@Override
	public String createContainer(Map<String, String> params, String ip,
			String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT)
				.append("/containerCluster");
		String result = HttpClient.post(url.toString(), params, username,
				password);
		return result;
	}

	@Override
	public String checkContainerCreateStatus(String cbaseClusterName,
			String ip, String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT)
				.append("/containerCluster/status/").append(cbaseClusterName);
		String result = HttpClient.get(url.toString(), username, password);
		return result;
	}

	@Override
	public String initUserAndPwd4Manager(String nodeIp, String port,
			String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(":").append(port)
				.append("/settings/web");

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("password", password);
		map.put("port", port);

		String result = HttpClient.post(url.toString(), map);
		return result;
	}

	@Override
	public String configClusterMemQuota(String nodeIp1, String port,
			String memoryQuota, String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port)
				.append("/pools/default");

		Map<String, String> map = new HashMap<String, String>();
		map.put("memoryQuota", memoryQuota);

		String result = HttpClient.post(url.toString(), map, adminUser,
				adminPassword);
		return result;
	}

	@Override
	public String addNodeToCluster(String srcNodeIp, String addNodeIp,
			String port, String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(srcNodeIp).append(":").append(port)
				.append("/controller/addNode");

		Map<String, String> map = new HashMap<String, String>();
		map.put("hostname", addNodeIp);
		map.put("user", username);
		map.put("password", password);

		String result = HttpClient
				.post(url.toString(), map, username, password);
		return result;
	}

	@Override
	public String CheckClusterStatus(String nodeIp1, String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port)
				.append("/cluster");

		String result = HttpClient
				.get(url.toString(), adminUser, adminPassword);
		return result;
	}

	@Override
	public String stop(Map<String, String> params, String nodeIp1, String port,
			String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port)
				.append("/cluster/stop");

		String result = HttpClient.post(url.toString(), params, adminUser,
				adminPassword);
		return result;
	}

	@Override
	public String start(Map<String, String> params, String nodeIp1,
			String port, String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port)
				.append("/cluster/start");

		String result = HttpClient.post(url.toString(), params, adminUser,
				adminPassword);
		return result;
	}

	@Override
	public String restart(Map<String, String> params, String nodeIp1,
			String port, String adminUser, String adminPassword) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp1).append(":").append(port)
				.append("/cluster/reload");

		String result = HttpClient.post(url.toString(), params, adminUser,
				adminPassword);
		return result;
	}

}
