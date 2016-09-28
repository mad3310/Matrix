package com.letv.portal.python.service;

import java.util.Map;

import com.letv.common.result.ApiResultObject;


/**Program Name: IGcePythonService <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年3月31日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IGcePythonService {
	/**
	 * 检查集群删除状态
	 * @param clusterName
	 * @param hostIp
	 * @param name
	 * @param password
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月9日, 下午5:23:44 .
	 * @version 1.0 .
	 */
	public ApiResultObject checkClusterDeleteStatus(String clusterName,String hostIp, String name, String password);
	/**
	 * 删除集群
	 * @param clusterName	集群名称
	 * @param hostIp
	 * @param name
	 * @param password
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月9日, 下午4:31:35 .
	 * @version 1.0 .
	 */
	public ApiResultObject deleteCluster(String clusterName, String hostIp,String name, String password);
	/**
	 * 检查GCE部署包镜像创建状态
	 * @param params
	 * @param serverIp
	 * @param serverPort
	 * @return
	 * @author linzhanbo .
	 * @since 2016年6月30日, 下午3:02:40 .
	 * @version 1.0 .
	 */
	public ApiResultObject checkGCEPackageMirrorCreateStatus(Map<String,String> params,String serverIp,String serverPort);
	/**
	 * 创建GCE部署包镜像
	 * @param params
	 * @param serverIp
	 * @return
	 * @author linzhanbo .
	 * @since 2016年6月30日, 下午3:00:23 .
	 * @version 1.0 .
	 */
	public ApiResultObject createGCEPackageMirror(Map<String,String> params,String serverIp,String serverPort);
	/**Methods Name: createContainer <br>
	 * Description: 创建container，执行一次,传入mclusterName<br>
	 * @author name: liuhao1
	 * @param mclusterName
	 * @return
	 */
	public ApiResultObject createContainer(Map<String,String> params,String ip,String username,String password);
	
	/**Methods Name: checkContainerCreateStatus <br>
	 * Description: 检查container创建状态,通过检查策略进行检查<br>
	 * @author name: liuhao1
	 * @return
	 */
	public ApiResultObject checkContainerCreateStatus(String mclusterName,String ip,String username,String password);

	/**
	 * push image
	 * @param params
	 * @param ip
	 * @param username
	 * @param password
	 * @return
	 */
	ApiResultObject pushImage(Map<String,String> params,String ip,String username,String password);
	
	/**Methods Name: initZookeeper <br>
	 * Description: 初始化zookeeper节点<br>
	 * @author name: liuhao1
	 * @param nodeIp 初始化的ip地址
	 * @return
	 */

	public ApiResultObject initZookeeper(String nodeIp,String port,Map<String,String> params);
	
	/**Methods Name: initUserAndPwd4Manager <br>
	 * Description: 初始化mcluster管理用户名密码<br>
	 * @author name: liuhao1
	 * @param nodeIp 初始化的ip地址
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	public ApiResultObject initUserAndPwd4Manager(String nodeIp,String port,String username,String password);
	
	public ApiResultObject createContainer1(Map<String,String> params,String ip,String port,String username,String password);
	public ApiResultObject createContainer2(Map<String,String> params,String ip,String port,String username,String password);

	public ApiResultObject syncContainer2(Map<String,String> params,String nodeIp1,String port,String adminUser, String adminPassword);

	public ApiResultObject startCluster(String nodeIp1,String port,String adminUser,String adminPassword);
	public ApiResultObject CheckClusterStatus(String nodeIp1,String port,String adminUser,String adminPassword);
	
	public ApiResultObject nginxProxyConfig(Map<String,String> params,String ip,String port,String username,String password);
	
	public ApiResultObject start(Map<String, String> params, String nodeIp1,String port,String adminUser, String adminPassword);
	public ApiResultObject stop(Map<String, String> params, String nodeIp1,String port,String adminUser, String adminPassword);
	public ApiResultObject restart(Map<String, String> params, String nodeIp1,String port,String adminUser, String adminPassword);
	public ApiResultObject checkStatus(String nodeIp1,String port,String adminUser, String adminPassword);
	
	public ApiResultObject capacity(Map<String,String> params,String ip,String adminUser, String adminPassword);
	
	/**
	  * @Title: startGbalancer
	  * @Description: 配置并启动container上的gbalancer
	  * @param params 接口参数
	  * @param ip container地址
	  * @param adminUser 用户名
	  * @param adminPassword 密码
	  * @return ApiResultObject   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月7日 下午6:09:41
	  */
	public ApiResultObject startGbalancer(Map<String,String> params,String ip,String port, String adminUser, String adminPassword);
	/**
	 * @Title: configMoxi
	 * @Description: 配置gcecontainer上的moxi
	 * @param params 接口参数
	 * @param ip container地址
	 * @param adminUser 用户名
	 * @param adminPassword 密码
	 * @return ApiResultObject   
	 * @throws 
	 * @author lisuxiao
	 * @date 2015年7月7日 下午6:09:41
	 */
	public ApiResultObject configMoxi(Map<String,String> params,String ip,String port);
	
	/**
	 * @Title: startMoxi
	 * @Description: 启动gcecontainer上的moxi
	 * @param params 接口参数
	 * @param ip container地址
	 * @param adminUser 用户名
	 * @param adminPassword 密码
	 * @return ApiResultObject   
	 * @throws 
	 * @author lisuxiao
	 * @date 2015年7月7日 下午6:09:41
	 */
	public ApiResultObject startMoxi(String ip,String port);

	public String checkClusterCount(String hostIp, String name, String password);
	
}
