package com.letv.portal.python.service;

import java.util.HashMap;
import java.util.Map;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.BackupDTO;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.model.HostModel;

/**Program Name: IPythonService <br>
 * Description:  与底层python rest交互接口<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月10日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */

public interface IPythonService {
	
	/**Methods Name: createContainer <br>
	 * Description: 创建container，执行一次,传入mclusterName<br>
	 * @author name: liuhao1
	 * @param params
	 * @param ip
	 * @param username
	 * @param password
	 * @return
	 */
	ApiResultObject createContainer(Map<String,String> params,String ip,String username,String password);

	ApiResultObject addContainerOnMcluster(Map<String, String> map, String hostIp, String name, String password);

	ApiResultObject getZkInfo(String nodeIp,String username,String password);
	
	/**Methods Name: checkContainerCreateStatus <br>
	 * Description: 检查container创建状态,通过检查策略进行检查<br>
	 * @author name: liuhao1
	 * @return
	 */
	ApiResultObject checkContainerCreateStatus(String mclusterName,String ip,String username,String password);
	ApiResultObject checkContainerAddStatus(String mclusterDataName, String addNames, String hostIp, String name, String password);
	/**
	 * 检查删除container状态,通过检查策略进行检查
	 * @param params 必须参数：
	 * 					mclusterDataName-集群名称
	 * 					delName-节点名称
	 * 					hostIp-集群所在主机ip
	 * 					name-主机用户名
	 * 					password-主机密码
	 * @return
	 */
	ApiResultObject checkContainerDelStatus(Map<String, String> params);
	
	/**Methods Name: initZookeeper <br>
	 * Description: 初始化zookeeper节点<br>
	 * @author name: liuhao1
	 * @param nodeIp 初始化的ip地址
	 * @return
	 */

	ApiResultObject initZookeeper(String nodeIp,Map<String,String> params);
	
	/**Methods Name: initUserAndPwd4Manager <br>
	 * Description: 初始化mcluster管理用户名密码<br>
	 * @author name: liuhao1
	 * @param nodeIp 初始化的ip地址
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject initUserAndPwd4Manager(String nodeIp,String username,String password);
	
	/**Methods Name: postMclusterInfo <br>
	 * Description: 提交mcluster集群信息<br>
	 * @author name: liuhao1
	 * @param mclusterName  mcluster名称
	 * @param nodeIp  节点ip
	 * @param nodeName 节点名称
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject postMclusterInfo(String mclusterName,String nodeIp,String nodeName,String username,String password);
	
	/**Methods Name: initMcluster <br>
	 * Description: 初始化集群<br>
	 * @author name: liuhao1
	 * @param nodeIp  节点ip
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject initMcluster(String nodeIp,String username,String password);

	/**Methods Name: postContainerInfo <br>
	 * Description: 提交节点信息<br>
	 * @author name: liuhao1
	 * @param nodeIp  节点ip
	 * @param nodeName 节点名称
	 * @return
	 */
	ApiResultObject postContainerInfo(String nodeIp,String nodeName,String username,String password);
	ApiResultObject delContainerInfo(String ipAddr, String containerName, String username, String password);
	ApiResultObject delContainerOnMcluster(Map<String, String> map, String hostIp, String name, String password);
	
	/**Methods Name: syncContainer <br>
	 * Description: 同步节点信息<br>
	 * @author name: liuhao1
	 * @param nodeIp  同步的主节点ip
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject syncContainer(String nodeIp,String username,String password);
	
	/**Methods Name: startMcluster <br>
	 * Description: 启动集群<br>
	 * @author name: liuhao1
	 * @param nodeIp  启动的主节点ip
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject startMcluster(String nodeIp,String username,String password);
	/**Methods Name: restartMcluster <br>
	 * Description: 重新启动集群<br>
	 * @author name: liuhao1
	 * @param nodeIp  启动的主节点ip
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject restartMcluster(String nodeIp,String username,String password);
	
	/**Methods Name: checkContainerStatus <br>
	 * Description: 检查集群状态：检查节点状态<br>
	 * @author name: liuhao1
	 * @param nodeIp  检查的主节点ip
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject checkContainerStatus(String nodeIp,String username,String password);
	
	/**Methods Name: createDb <br>
	 * Description: 创建数据库<br>
	 * @author name: liuhao1
	 * @param nodeIp
	 * @param dbName
	 * @param dbUserName
	 * @param ipAddress
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	ApiResultObject createDb(String nodeIp,String dbName,String dbUserName,String ipAddress,String username,String password);
	

	/**Methods Name: createDbUser <br>
	 * Description: 创建数据库用户<br>
	 * @author name: liuhao1
	 * @param dbUserModel
	 * @param dbName
	 * @param username
	 * @param password
	 * @return
	 */
	ApiResultObject createDbUser(DbUserModel dbUserModel,String dbName,String nodeIp,String username,String password);
	
	/**Methods Name: startGbalancer <br>
	 * Description: 启动gbalancer<br>
	 * @author name: liuhao1
	 * @param nodeIp
	 * @param user
	 * @param pwd
	 * @param ipListPort
	 * @param port
	 * @param args
	 * @param username
	 * @param password
	 * @return
	 */
	ApiResultObject startGbalancer(String nodeIp,String user,String pwd,String server,String ipListPort,String port,String args,String username,String password);
	/**
	 * Methods Name: deleteDbUser <br>
	 * Description: 删除DbUser<br>
	 * @author name: wujun
	 * @param userName
	 * @param dbNameip
	 * @param ipAddress
	 * @param nodeIp
	 * @param username
	 * @param password
	 */
	ApiResultObject deleteDbUser(DbUserModel dbUserModel,String dbName,String nodeIp,String username, String password);

	/**Methods Name: removeMcluster <br>
	 * Description: 删除container集群<br>
	 * @author name: liuhao1
	 * @param mclusterName
	 */
	ApiResultObject removeMcluster(String mclusterName,String ip,String username,String password);

	/**Methods Name: startMcluster <br>
	 * Description: 启动container集群<br>
	 * @author name: liuhao1
	 * @param mclusterName
	 * @return
	 */
	ApiResultObject startMcluster(String mclusterName,String ip,String username,String password);

	/**Methods Name: stopMcluster <br>
	 * Description: 停止container集群<br>
	 * @author name: liuhao1
	 * @param mclusterName
	 * @return
	 */
	ApiResultObject stopMcluster(String mclusterName,String ip,String username,String password);
	
	/**Methods Name: startContainer <br>
	 * Description: 启动container<br>
	 * @author name: liuhao1
	 * @param containerName
	 * @return
	 */
	ApiResultObject startContainer(String containerName,String ip,String username,String password);

	/**Methods Name: stopContainer <br>
	 * Description: 停止container<br>
	 * @author name: liuhao1
	 * @param containerName
	 * @return
	 */
	ApiResultObject stopContainer(String containerName,String ip,String username,String password);

	/**Methods Name: checkMclusterStatus <br>
	 * Description: 检查container集群状态<br>
	 * @author name: liuhao1
	 * @param mclusterName
	 * @return
	 */
	String checkMclusterStatus(String mclusterName,String ip,String username,String password);

	/**Methods Name: checkContainerStatus <br>
	 * Description: 检查container状态<br>
	 * @author name: liuhao1
	 * @param containerName
	 * @return
	 */
	String checkContainerStatus(String containerName,String ip,String username,String password);
	/**
	 * Methods Name: initHcluster <br>
	 * Description: 创建host之前做接口验证<br>
	 * @author name: wujun
	 * @return
	 */
	ApiResultObject initHcluster(String hostIp);
	/**
	 * Methods Name: createHost <br>
	 * Description: 创建host<br>
	 * @author name: wujun
	 */
	ApiResultObject createHost(HostModel hostModel);

	/**Methods Name: checkMclusterCount <br>
	 * Description: 检查container集群一致性<br>
	 * @author name: liuhao1
	 * @param hostIp
	 * @param name
	 * @param password
	 * @return
	 */
	String checkMclusterCount(String hostIp, String name, String password);
	/**
	 * Methods Name: MonitorMclusterStatus <br>
	 * Description: 通过vip结点Ip抓取集群状态<br>
	 * @author name: wujun
	 * @param ip
	 * @return
	 */
	String getMclusterStatus(String ip);
    /**
     * Methods Name: getMclusterMonitor <br>
     * Description: 通过vip结点ip抓取集群数据库状态<br>
     * @author name: wujun
     * @param ip
     * @return
     */
	String getMclusterMonitor(String ip);
	
	
	/**Methods Name: getMonitorData <br>
	 * Description: 常规数据记录：供图表展示使用<br>
	 * @author name: liuhao1
	 * @param ip
	 * @param index
	 * @return
	 */
	ApiResultObject getMonitorData(String ip,String index);
	ApiResultObject getClusterMonitorData(String ip, String index);

	/**Methods Name: wholeBackup4Db <br>
	 * Description: db数据库备份<br>
	 * @author name: liuhao1
	 * @param ipAddr
	 * @param name
	 * @param password
	 */
	BackupDTO wholeBackup4Db(String ipAddr, String name, String password);
	/**
	 * 老接口，部分集群升级时使用
	 * @param ipAddr
	 * @param name
	 * @param password
	 * @return
	 */
	ApiResultObject oldWholeBackup4Db(String ipAddr, String name, String password);
	
	/**
	 * Description: db数据库增量备份
	 * @param ip    ip地址
	 * @param name  用户名
	 * @param pwd   密码
	 * @return
	 */
	public BackupDTO incrBackup4Db(String ip, String name, String pwd);

	/**Methods Name: checkBackup4Db <br>
	 * Description: 检查db备份结果<br>
	 * @author name: liuhao1
	 * @param ipAddr
	 */
	ApiResultObject checkBackup4Db(String ipAddr);
	ApiResultObject getOSSData(String ip, String index);
	
	/**
	  * @Title: getMysqlMonitorData
	  * @Description: 获取mysql监控信息
	  * @param ip
	  * @param index
	  * @param params
	  * @return String   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月28日 下午6:54:54
	  */
	ApiResultObject getMysqlMonitorData(String ip,String index, Map<String, String> params);


	ApiResultObject updateAuthority(DbUserModel dbUserModel, String dbName, String nodeIp, String username, String password);

	ApiResultObject startNode(String ipAddr, String username, String password);
	
}
