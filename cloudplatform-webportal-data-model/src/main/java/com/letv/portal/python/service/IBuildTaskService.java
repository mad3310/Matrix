package com.letv.portal.python.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.letv.portal.model.*;
import com.letv.portal.model.monitor.BaseMonitor;
import com.letv.portal.model.monitor.ContainerMonitorModel;
import com.letv.portal.model.swift.SwiftServer;


/**Program Name: IBuildTaskService <br>
 * Description:  mcluster db等创建调度<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBuildTaskService { 
	
	/**Methods Name: buildUser <br>
	 * Description: 创建用户<br>
	 * @author name: liuhao1
	 * @param ids
	 */
	public void buildUser(String ids);
	/**Methods Name: updateUser <br> 
	 * Description: 修改用户<br>
	 * @author name: liuhao1
	 * @param ids
	 */
	public void updateAuthority(String ids);

	/**Methods Name: buildDb <br>
	 * Description: 创建数据库<br>
	 * @author name: liuhao1
	 * @param dbId
	 */
	public void buildDb(Long dbId);
	
	/**Methods Name: removeMcluster <br>
	 * Description: <br>
	 * @author name: liuhao1
	 * @param mcluster
	 */
	public void removeMcluster(MclusterModel mcluster);
	/**
	 * Methods Name: deleteUser <br>
	 * Description: 删除 DbUser<br>
	 * @author name: wujun
	 * @param ids 多个dbUserId
	 */
	public void deleteDbUser(String ids);

	/**Methods Name: startMcluster <br>
	 * Description: 启动container集群<br>
	 * @author name: liuhao1
	 * @param mcluster
	 */
	public void startMcluster(MclusterModel mcluster);

	/**Methods Name: stopMcluster <br>
	 * Description: 停止container集群<br>
	 * @author name: liuhao1
	 * @param mcluster
	 */
	public void stopMcluster(MclusterModel mcluster);

	/**Methods Name: startContainer <br>
	 * Description: 启动单个container<br>
	 * @author name: liuhao1
	 * @param container
	 */
	public void startContainer(ContainerModel container);

	/**Methods Name: stopContainer <br>
	 * Description: 停止单个container<br>
	 * @author name: liuhao1
	 * @param container
	 */
	public void stopContainer(ContainerModel container);

	/**Methods Name: checkMclusterStatus <br>
	 * Description: 检查container集群状态<br>
	 * @author name: liuhao1
	 * @param mcluster
	 */
	public void checkMclusterStatus(MclusterModel mcluster);
	
	/**Methods Name: checkContainerStatus <br>
	 * Description: 检查container集群<br>
	 * @author name: liuhao1
	 * @param container
	 */
	public void checkContainerStatus(ContainerModel container);
	/**
	 * Methods Name: createHost <br>
	 * Description: 创建host<br>
	 * @author name: wujun
	 * @param hostModel
	 * @return
	 */
	public void createHost(HostModel hostModel);

	/**Methods Name: checkMclusterCount <br>
	 * Description: 检查container集群数量一致性<br>
	 * @author name: liuhao1
	 */
	public void checkMclusterCount(HclusterModel hcluster);
    /**
     * Methods Name: getMonitorData <br>
     * Description: 获得集群的监控数据<br>
     * @author name: wujun
     * @param ips
     * @return
     */
	public List<ContainerMonitorModel> getMonitorData(List<ContainerModel> containerModels);
	/**
	 * Methods Name: getMonitorDetailNodeAndDbData <br>
	 * Description: 集群监控详细信息展示<br>
	 * @author name: wujun
	 * @param ip
	 * @return
	 */
	public ContainerMonitorModel getMonitorDetailNodeAndDbData(String ip,String mclusterName);	
	/**
	 * Methods Name: getMonitorDetailClusterData <br>
	 * Description: 集群监控主要信息展示<br>
	 * @author name: wujun
	 * @param ip
	 * @return
	 */
	public ContainerMonitorModel getMonitorDetailClusterData(String ip);
    
	/**Methods Name: getContainerServiceData <br>
	 * Description: 动态获取监控数据，入库<br>
	 * @author name: liuhao1
	 * @param container
	 * @param index
	 * @param date
	 * @return
	 */
	public void getContainerServiceData(ContainerModel container, MonitorIndexModel index,Date date);
	public void getContainerServiceData(String ipAddr, String dataFromApi, String detailTable,Date date);

	public BaseMonitor getMonitorData(String ip, Long monitorType);

	public void getClusterServiceData(String clusterName,Long hclusterId, MonitorIndexModel index, Date date);
	public void getOSSServiceData(String hostIp, String string,
			MonitorIndexModel index, Date date);
	
	/**
	  * @Title: getMysqlMonitorServiceData
	  * @Description: 获取数据及查询已有监控输入，入库
	  * @param container
	  * @param index
	  * @param date void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月28日 下午1:36:43
	  */
	public void getMysqlMonitorServiceData(ContainerModel container, MonitorIndexModel index,Date date);
	/**
	  * @Title: collectMysqlMonitorBaseData
	  * @Description: 获取mysql监控基础表数据
	  * @param container
	  * @param index
	  * @param date void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月28日 下午6:04:32
	  */
	public void collectMysqlMonitorBaseData(ContainerModel container, MonitorIndexModel index,Date date);
	/**
	  * @Title: collectMysqlMonitorBaseSpaceData
	  * @Description: 获取数据库和表大小
	  * @param dbName
	  * @param container
	  * @param index
	  * @param date void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月30日 下午5:11:18
	  */
	public void collectMysqlMonitorBaseSpaceData(String dbName, ContainerModel container, List<MonitorIndexModel> indexs,Date date);
	
	/**
	 * 检查数据库表反例和无主键
	 */
	void checkDbStruc();
}
