package com.letv.portal.python.service.impl;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.util.HttpClient;
import com.letv.portal.enumeration.DbUserRoleStatus;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.python.service.IPythonService;
 
@Service("pythonService")
public class PythonServiceImpl implements IPythonService{
	
	private final static Logger logger = LoggerFactory.getLogger(PythonServiceImpl.class);
	
	private final static String URL_HEAD = "http://";
	private final static String URL_PORT = ":8888";	
	private final static String GBALANCER_PORT = ":9888";	
	
	@Override
	public String createContainer(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName", mclusterName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String checkContainerCreateStatus(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/createStatus/").append(mclusterName);
		String result = HttpClient.get(url.toString(),username,password);
		return result;
	}

	@Override
	public String initZookeeper(String nodeIp) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/admin/conf");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("zkAddress", "127.0.0.1");
		
		String result = HttpClient.post(url.toString(), map);
		return result;
	}

	@Override
	public String initUserAndPwd4Manager(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/admin/user");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("adminUser", username);
		map.put("adminPassword", password);
		
		String result = HttpClient.post(url.toString(), map);
		return result;
	}

	@Override
	public String postMclusterInfo(String mclusterName,String nodeIp,String nodeName,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", mclusterName);
		map.put("dataNodeIp", nodeIp);
		map.put("dataNodeName", nodeName);
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String initMcluster(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/init?forceInit=false");
	
		String result = HttpClient.get(url.toString(),username,password);
		return result;
	}

	@Override
	public String postContainerInfo(String nodeIp,String nodeName,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/node");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("dataNodeIp", nodeIp);
		map.put("dataNodeName", nodeName);
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String syncContainer(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/sync");
		String result = HttpClient.get(url.toString());
		return result;
	}

	@Override
	public String startMcluster(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/start");

		Map<String,String> map = new HashMap<String,String>();
		map.put("cluster_flag", "new");
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}
	@Override
	public String restartMcluster(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/start");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("cluster_flag", "old");
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String checkContainerStatus(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/check/online_node");
		String result = HttpClient.get(url.toString(),username,password);
		return result;
		
	}

	@Override
	public String createDb(String nodeIp,String dbName,String dbUserName,String ipAddress,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/db");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("dbName", dbName);
		map.put("userName", dbUserName);
		map.put("ip_address", "127.0.0.1");
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}


	@Override
	public String createDbUser(DbUserModel dbUser, String dbName,String nodeIp,String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/dbUser");
				
		Map<String,String> map = new HashMap<String,String>();
		if(DbUserRoleStatus.WR.getValue() == dbUser.getType()) {
			map.put("role", "wr");
		}
		if(DbUserRoleStatus.MANAGER.getValue() == dbUser.getType()) {
			map.put("role", "manager");
		}
		if(DbUserRoleStatus.RO.getValue() == dbUser.getType()) {
			map.put("role", "ro");
		}
		map.put("dbName", dbName);
		map.put("userName", dbUser.getUsername());
		//自动生成密码
		map.put("user_password", dbUser.getPassword());
		map.put("ip_address", dbUser.getAcceptIp());
		map.put("max_queries_per_hour", String.valueOf(dbUser.getMaxQueriesPerHour()));
		map.put("max_updates_per_hour", String.valueOf(dbUser.getMaxUpdatesPerHour()));
		map.put("max_connections_per_hour", String.valueOf(dbUser.getMaxConnectionsPerHour()));
		map.put("max_user_connections", String.valueOf(dbUser.getMaxUserConnections()));
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String startGbalancer(String nodeIp,String user,String pwd,String server,String ipListPort,String port,String args,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(GBALANCER_PORT).append("/glb/start");
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("user", user);
		map.put("passwd", pwd);
		map.put("iplist_port",ipListPort);
		map.put("port", port);
		map.put("args",args);
		map.put("service",server);
		
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	public String deleteDbUser(DbUserModel dbUserModel,String dbName,String nodeIp,String username, String password){
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/dbUser/").append(dbName).append("/").append(dbUserModel.getUsername()).append("/").append(URLEncoder.encode(dbUserModel.getAcceptIp()));
		String result = HttpClient.detele(url.toString(), username, password);
		return result;
	}

	@Override
	public String removeMcluster(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster?containerClusterName=").append(mclusterName);
		String result = HttpClient.detele(url.toString(),username,password);
		return result;
	}

	@Override
	public String startMcluster(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/start");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName", mclusterName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String stopMcluster(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/stop");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName", mclusterName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String startContainer(String containerName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/container/start");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerName", containerName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String stopContainer(String containerName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/container/stop");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerName", containerName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return result;
	}

	@Override
	public String checkMclusterStatus(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/status/").append(mclusterName);
		String result = HttpClient.get(url.toString(),username,password);
		return result;
	}

	@Override
	public String checkContainerStatus(String containerName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/container/status/").append(containerName);
		String result = HttpClient.get(url.toString(),username,password);
		return result;
	}

	@Override
	public String initHcluster(String hostIp) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/admin/user");
		Map<String,String> map = new HashMap<String,String>();
		map.put("adminUser", "root");
		map.put("adminPassword", "root");
		String result = HttpClient.post(url.toString(), map);
		return result;
	}

	@Override
	public String createHost(HostModel hostModel) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostModel.getHostIp()).append(URL_PORT).append("/serverCluster");
		Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", hostModel.getHcluster().getHclusterName());
		map.put("dataNodeIp", hostModel.getHostIp());
		map.put("dataNodeName", hostModel.getHostName());
		String result = HttpClient.post(url.toString(), map,hostModel.getName(),hostModel.getPassword());	
		return result;
	}

	@Override
	public String checkMclusterCount(String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/containerCluster/sync");
		String result = HttpClient.get(url.toString(),name,password);
		return result;
	}

	public String getMclusterStatus(String ip)throws Exception{
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/mcluster/status");
		String result = HttpClient.get(url.toString(),1000,1000);
		return result;
		
	}
	public String getMclusterMonitor(String ip)throws Exception{
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/mcluster/monitor");
	    String result = HttpClient.get(url.toString(),1000,1000);
	    return result;
	}
	
	@Override
	public String getMonitorData(String ip, String index) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append(index);
		String result = HttpClient.get(url.toString(),1000,1000);
		return result;
	}

	@Override
	public String wholeBackup4Db(String ipAddr,String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ipAddr).append(URL_PORT).append("/backup");
		String result = HttpClient.get(url.toString(),1000,5000,name,password);
		return result;
	}

	@Override
	public String checkBackup4Db(String ipAddr) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ipAddr).append(URL_PORT).append("/backup/check");
		String result = HttpClient.get(url.toString(),1000,10000);
		return result;
	}    
	
}
