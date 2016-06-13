package com.letv.portal.python.service.impl;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
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
	private final static String OSS_URL_PORT = ":8887";
	private final static String GBALANCER_PORT = ":9888";
	private final static String URL_CHECK_PORT = ":6666";
	private final static String BEEHIVE_PORT = ":6666";

	@Override
	public ApiResultObject createContainer(Map<String,String> params,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster");
		String result = HttpClient.post(url.toString(), params,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject addContainerOnMcluster(Map<String, String> map, String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/containerCluster/node");
		String result = HttpClient.post(url.toString(), map,name,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject checkContainerCreateStatus(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/createResult/").append(mclusterName);
		String result = HttpClient.get(url.toString(),username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject checkContainerAddStatus(String mclusterDataName, String addNames, String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/containerCluster/").append(mclusterDataName).append("/node/").append(addNames);
		String result = HttpClient.get(url.toString(), name, password);
		return new ApiResultObject(result,url.toString());
	}
	
	@Override
	public ApiResultObject checkContainerDelStatus(Map<String, String> params) {
		if(null == params) {
			throw new ValidateException("参数不能为空！");
		}
		StringBuilder builder = new StringBuilder();
		String mclusterDataName = params.get("mclusterDataName");
		String delName = params.get("delName");
		String hostIp = params.get("hostIp");
		String name = params.get("name");
		String password = params.get("password");
				
		if(StringUtils.isEmpty(mclusterDataName) || 
				StringUtils.isEmpty(delName) || 
				StringUtils.isEmpty(hostIp) || 
				StringUtils.isEmpty(name) ||
				StringUtils.isEmpty(password)) {
			builder.append("参数不合法：").append(params.toString()).append(" 都不能为空");
			throw new ValidateException(builder.toString());
		}
		//TODO 抽出共用方法
		builder.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/containerCluster/").append(mclusterDataName).append("/node/").append(delName);
		String result = HttpClient.get(builder.toString(), name, password);
		return new ApiResultObject(result, builder.toString());
	}

	@Override
	public ApiResultObject getZkInfo(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/node/conf/zk");
		String result = HttpClient.get(url.toString(),username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initZookeeper(String nodeIp,Map<String,String> zkParm) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/admin/conf");

		String result = HttpClient.post(url.toString(), zkParm);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initUserAndPwd4Manager(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/admin/user");

		Map<String,String> map = new HashMap<String,String>();
		map.put("adminUser", username);
		map.put("adminPassword", password);

		String result = HttpClient.post(url.toString(), map);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject postMclusterInfo(String mclusterName,String nodeIp,String nodeName,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster");

		Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", mclusterName);
		map.put("dataNodeIp", nodeIp);
		map.put("dataNodeName", nodeName);

		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject initMcluster(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/init?forceInit=false");

		String result = HttpClient.get(url.toString(),username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject postContainerInfo(String nodeIp,String nodeName,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/node");

		Map<String,String> map = new HashMap<String,String>();
		map.put("dataNodeIp", nodeIp);
		map.put("dataNodeName", nodeName);

		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject delContainerInfo(String ipAddr, String containerName, String username, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ipAddr).append(URL_PORT).append("/cluster/node");

		Map<String,String> map = new HashMap<String,String>();
		map.put("dataNodeIp", ipAddr);
		map.put("dataNodeName", containerName);

		String result = HttpClient.detele(url.toString(),map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject delContainerOnMcluster(Map<String, String> map, String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/containerCluster/node/remove");
		String result = HttpClient.post(url.toString(), map,name,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject syncContainer(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/sync");
		String result = HttpClient.get(url.toString());
		return new ApiResultObject(result,url.toString());
	}

    @Override
    public ApiResultObject startNode(String ipAddr, String username, String password) {
        StringBuffer url = new StringBuffer();
        url.append(URL_HEAD).append(ipAddr).append(URL_PORT).append("/node/start");

        Map<String,String> map = new HashMap<String,String>();
        map.put("isNewCluster", "false");
        
        String result = HttpClient.post(url.toString(), map, 5000, 1000*3600, username, password);
        return new ApiResultObject(result,url.toString());
    }

	@Override
	public ApiResultObject startMcluster(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/start");

		Map<String,String> map = new HashMap<String,String>();
		map.put("cluster_flag", "new");

		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject restartMcluster(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/start");

		Map<String,String> map = new HashMap<String,String>();
		map.put("cluster_flag", "old");

		String result = HttpClient.post(url.toString(), map,1000,2000,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject checkContainerStatus(String nodeIp,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/cluster/check/online_node");
		String result = HttpClient.get(url.toString(), 5000, 10000, username, password);
		return new ApiResultObject(result,url.toString());

	}

	@Override
	public ApiResultObject createDb(String nodeIp,String dbName,String dbUserName,String ipAddress,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/db");

		Map<String,String> map = new HashMap<String,String>();
		map.put("dbName", dbName);
		map.put("userName", dbUserName);
		map.put("ip_address", "127.0.0.1");

		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}


	@Override
	public ApiResultObject createDbUser(DbUserModel dbUser, String dbName,String nodeIp,String username, String password) {
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
		return new ApiResultObject(result,url.toString());
	}
	@Override
	public ApiResultObject updateAuthority(DbUserModel dbUser, String dbName, String nodeIp, String username, String password) {
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
		map.put("ip_address", dbUser.getAcceptIp());
		map.put("max_queries_per_hour", String.valueOf(dbUser.getMaxQueriesPerHour()));
		map.put("max_updates_per_hour", String.valueOf(dbUser.getMaxUpdatesPerHour()));
		map.put("max_connections_per_hour", String.valueOf(dbUser.getMaxConnectionsPerHour()));
		map.put("max_user_connections", String.valueOf(dbUser.getMaxUserConnections()));

		String result = HttpClient.put(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}


    @Override
	public ApiResultObject startGbalancer(String nodeIp,String user,String pwd,String server,String ipListPort,String port,String args,String username,String password) {
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
		return new ApiResultObject(result,url.toString());
	}

	public ApiResultObject deleteDbUser(DbUserModel dbUserModel,String dbName,String nodeIp,String username, String password){
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(nodeIp).append(URL_PORT).append("/dbUser/").append(dbName).append("/").append(dbUserModel.getUsername()).append("/").append(URLEncoder.encode(dbUserModel.getAcceptIp()));
		String result = HttpClient.detele(url.toString(), username, password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject removeMcluster(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster?containerClusterName=").append(mclusterName);
		String result = HttpClient.detele(url.toString(),username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject startMcluster(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/start");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName", mclusterName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject stopMcluster(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/containerCluster/stop");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName", mclusterName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject startContainer(String containerName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/container/start");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerName", containerName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject stopContainer(String containerName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/container/stop");
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerName", containerName);
		String result = HttpClient.post(url.toString(), map,username,password);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public String checkMclusterStatus(String mclusterName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_CHECK_PORT).append("/containerCluster/status/").append(mclusterName);
		String result = HttpClient.get(url.toString(),username,password);
		return result;
	}

	@Override
	public String checkContainerStatus(String containerName,String ip,String username,String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_CHECK_PORT).append("/container/status/").append(containerName);
		String result = HttpClient.get(url.toString(),username,password);
		return result;
	}

	@Override
	public ApiResultObject initHcluster(String hostIp) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_PORT).append("/admin/user");
		Map<String,String> map = new HashMap<String,String>();
		map.put("adminUser", "root");
		map.put("adminPassword", "root");
		String result = HttpClient.post(url.toString(), map);
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public ApiResultObject createHost(HostModel hostModel) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostModel.getHostIp()).append(URL_PORT).append("/serverCluster");
		Map<String,String> map = new HashMap<String,String>();
		map.put("clusterName", hostModel.getHcluster().getHclusterName());
		map.put("dataNodeIp", hostModel.getHostIp());
		map.put("dataNodeName", hostModel.getHostName());
		String result = HttpClient.post(url.toString(), map,hostModel.getName(),hostModel.getPassword());
		return new ApiResultObject(result,url.toString());
	}

	@Override
	public String checkMclusterCount(String hostIp, String name, String password) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(hostIp).append(URL_CHECK_PORT).append("/containerCluster/sync");
		String result = HttpClient.get(url.toString(),name,password);
		return result;
	}

	public String getMclusterStatus(String ip){
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/mcluster/status");
		String result = HttpClient.get(url.toString(),1000,1000);
		return result;
	}
	public String getMclusterMonitor(String ip){
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/mcluster/monitor");
		String result = HttpClient.get(url.toString(),1000,1000);
		return result;
	}

	@Override
	public ApiResultObject getMonitorData(String ip, String index) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append(index);
		String result = HttpClient.get(url.toString(),1000,1000);
		return new ApiResultObject(result, url.toString());
	}
	@Override
	public ApiResultObject getClusterMonitorData(String ip, String index) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(BEEHIVE_PORT).append(index);
		String result = HttpClient.get(url.toString(),1000,1000);
		return new ApiResultObject(result, url.toString());
	}
	@Override
	public ApiResultObject getOSSData(String ip, String index) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(OSS_URL_PORT).append(index);
		String result = HttpClient.get(url.toString(),1000,1000);
		return new ApiResultObject(result, url.toString());
	}

	@Override
//	public ApiResultObject wholeBackup4Db(String ipAddr,String name, String password) {
//		StringBuffer url = new StringBuffer();
//		url.append(URL_HEAD).append(ipAddr).append(URL_PORT).append("/backup");
//		String result = HttpClient.get(url.toString(),1000,5000,name,password);
//		return new ApiResultObject(result, url.toString());
//	}

	
	public ApiResultObject checkBackup4Db(String ipAddr) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ipAddr).append(URL_PORT).append("/backup/check");
		String result = HttpClient.get(url.toString(),1000,10000);
		return new ApiResultObject(result, url.toString());
	}
	@Override
	public ApiResultObject getMysqlMonitorData(String ip, String index, Map<String, String> params) {

		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append(index);

		String result = HttpClient.post(url.toString(), params, 1000, 1000, null, null);
		return new ApiResultObject(result, url.toString());
	}
	
	@Override
	public ApiResultObject wholeBackup4Db(String ip, String name, String pwd) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("backup_type", "full");
		return executeBackup4Db(ip, name, pwd, params);
	}
	
	@Override
	public ApiResultObject incrBackup4Db(String ip, String name, String pwd) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("backup_type", "incr");
		return executeBackup4Db(ip, name, pwd, params);
	}
	
	/*
	 * 根据给定的map参数执行备份操作
	 */
	private ApiResultObject executeBackup4Db(String ip, String name, String pwd, Map<String, String> params) {
		StringBuffer url = new StringBuffer();
		url.append(URL_HEAD).append(ip).append(URL_PORT).append("/backup");
		String result = HttpClient.post(url.toString(), params, name, pwd);
		return new ApiResultObject(result, url.toString());
	}
	

}
