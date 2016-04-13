package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.common.paging.impl.Page;
import com.letv.portal.model.ContainerModel;



/**Program Name: IContainerService <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月22日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IContainerService extends IBaseService<ContainerModel> {
	
	Page findPagebyParams(Map<String,Object> params,Page page);
	
	List<ContainerModel> selectByMclusterId(Long mclusterId);
	
	List<ContainerModel> selectVipByClusterId(Long mclusterId);

	 void deleteByMclusterId(Long mclusterId);

	 ContainerModel selectByName(String containerName);
	
	 List<ContainerModel> selectContainerByMclusterId(Long clusterId);
	 List<ContainerModel> selectAllByMap(Map<String,Object> map);

	 List<ContainerModel> selectVaildVipContainers(Map<String,Object> params);

	ContainerModel selectValidVipContianer(Long mclusterId, String type, Map<String, Object> params);

	List<ContainerModel> selectVaildNormalContainers(Map<String, Object> params);
	
	List<ContainerModel> selectWithHClusterNameByMap(Map<String, Object> params);
	
	/**
	 * 查询container数量（非vip）
	 * @param map
	 * @return
	 */
	Integer selectCountNodeContainers(Map<String,Object> map);
	List<ContainerModel> selectNodeContainersByMap(Map<String,Object> map);

}
