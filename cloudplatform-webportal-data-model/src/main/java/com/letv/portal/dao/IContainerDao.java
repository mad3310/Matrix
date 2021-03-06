package com.letv.portal.dao;

import java.util.List;
import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.common.dao.QueryParam;
import com.letv.portal.model.ContainerModel;

/**Program Name: IContainerDao <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IContainerDao extends IBaseDao<ContainerModel> {
	
	
	
	/**Methods Name: selectByClusterId <br>
	 * Description: 根据cluster 查出N个节点<br>
	 * @author name: liuhao1
	 * @param clusterId
	 * @return
	 */
	public List<ContainerModel> selectByMclusterId(Long clusterId);
	public void deleteByMclusterId(Long mclusterId);
	public void updateHostIpByName(ContainerModel container);
	public ContainerModel selectByName(String containerName);
	public  List<ContainerModel> selectContainerByMclusterId(Long clusterId);
	public  List<ContainerModel> selectAllByMap(Map map);
	public List<ContainerModel> selectVipIps4Monitor();
	public List<ContainerModel> selectValidByMap(Map<String, Object> params);
	/**
	 * 获取container信息（包含物理机集群名称）
	 * @param params
	 * @return
	 */
	public List<ContainerModel> selectWithHClusterNameByMap(Map<String, Object> params);

	List<ContainerModel> selectVipByClusterId(Long mclusterId);
	
	Integer selectCountNodeContainers(Map<String, Object> map);
	
	List<ContainerModel> selectNodeContainersByMap(Map<String, Object> map);
	
	/**
	 * 查询有效vip容器信息
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> queryVaildVipContainers(QueryParam param);
	/**
	 * 查询有效vip容器数量
	 * @param param
	 * @return
	 */
	Integer queryVaildVipContainersCount(QueryParam param);
	
	void updateUserByMclusterId(Long mclusterId, Long userId);
	/**
	 * 根据物理机集群id查询有效vip容器信息
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> queryVaildVipContainersByHclusterId(Long hclusterId);
	/**
	 * 根据物理机集群id查询所有vip容器信息
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> queryAllVipContainersByHclusterId(Long hclusterId);
	/**
	 * 根据物理机集群id查询所有data容器信息
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> queryAllDataContainersByHclusterId(Long hclusterId);
}
