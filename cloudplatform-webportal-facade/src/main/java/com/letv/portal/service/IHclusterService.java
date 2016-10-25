package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.common.paging.impl.Page;
import com.letv.portal.model.HclusterModel;



/**Program Name: IHclusterService <br>
 * Description:  <br>
 * @author name: wujun <br>
 * Written Date: 2014年10月21日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IHclusterService extends IBaseService<HclusterModel> {

	
	/**Methods Name: findPagebyParams <br>
	 * Description: 根据查询条件查出分页数据<br>
	 * @author name: liuhao1
	 * @param params
	 * @param page
	 * @return
	 */
	public Page findPagebyParams(Map<String,Object> params,Page page);
	public Map<String, Object> selectByHclusterId(Long hclusterId);
	public List<HclusterModel> selectByName(Map<String,String> map);
	public List<HclusterModel> isExistHostOnHcluster(Map<String,Object> map);
	public List<HclusterModel> selectHclusterByStatus(HclusterModel hclusterModel);
	public void updateStatus(HclusterModel hclusterModel);

	Integer selectCountByType(String rds);
}
