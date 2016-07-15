package com.letv.portal.python.service;

import java.util.Map;

import com.letv.common.result.ApiResultObject;


public interface IEsPythonService {
	
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
	
	/**Methods Name: initZookeeper <br>
	 * Description: 初始化zookeeper节点<br>
	 * @author name: liuhao1
	 * @param nodeIp 初始化的ip地址
	 * @return
	 */

	public ApiResultObject initZookeeper(String nodeIp,Map<String,String> params);
	
	/**Methods Name: initUserAndPwd4Manager <br>
	 * Description: 初始化mcluster管理用户名密码<br>
	 * @author name: liuhao1
	 * @param nodeIp 初始化的ip地址
	 * @param username 集群用户名
	 * @param password 集群密码
	 * @return
	 */
	public ApiResultObject initUserAndPwd4Manager(String nodeIp,String username,String password);
	
	/**
	 * 初始化ES集群
	 * @param nodeIp
	 * @param params
	 * @param adminUser
	 * @param adminPassword
	 * @return
	 */
	public ApiResultObject initEsCluster(String nodeIp, Map<String,String> params, String adminUser, String adminPassword);
	/**
	 * 同步集群节点
	 * @param nodeIp
	 * @param params
	 * @param adminUser
	 * @param adminPassword
	 * @return
	 */
	public ApiResultObject syncEsCluster(String nodeIp, Map<String,String> params, String adminUser, String adminPassword);
	/**
	 * 初始化节点
	 * @param nodeIp
	 * @param params
	 * @param adminUser
	 * @param adminPassword
	 * @return
	 */
	public ApiResultObject initEsContainer(String nodeIp, Map<String,String> params, String adminUser, String adminPassword);
	/**
	 * 配置es节点
	 * @param nodeIp
	 * @param params
	 * @param adminUser
	 * @param adminPassword
	 * @return
	 */
	public ApiResultObject configEsContainer(String nodeIp, String adminUser, String adminPassword);

	public ApiResultObject startElesticSearch(String nodeIp, String adminUser, String adminPassword);

}
