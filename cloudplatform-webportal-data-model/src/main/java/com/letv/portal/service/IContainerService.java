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
	
	/**Methods Name: findPagebyParams <br>
	 * Description: 根据查询条件查出分页数据<br>
	 * @author name: liuhao1
	 * @param params
	 * @param page
	 * @return
	 */
	public Page findPagebyParams(Map<String,Object> params,Page page);
	
	/**Methods Name: selectByMclusterId <br>
	 * Description: 根据clusterId查出节点<br>
	 * @author name: liuhao1
	 * @param clusterId
	 * @return
	 */
	public List<ContainerModel> selectByMclusterId(Long mclusterId);
	
	/**Methods Name: selectByClusterId <br>
	 * Description: 根据clusterId查出节点<br>
	 * @author name: liuhao1
	 * @param clusterId
	 * @return
	 */
	public List<ContainerModel> selectNormalByClusterId(Long mclusterId);

	public void deleteByMcluster(Long mclusterId);
	
	
}
