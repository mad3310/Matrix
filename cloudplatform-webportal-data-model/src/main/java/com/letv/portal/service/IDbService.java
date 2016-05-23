package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.common.paging.impl.Page;
import com.letv.portal.model.DbModel;


/**Program Name: IDbService <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月20日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IDbService extends IBaseService<DbModel> {
	
	/**Methods Name: findPagebyParams <br>
	 * Description: 根据查询条件查出分页数据<br>
	 * @author name: liuhao1
	 * @param params
	 * @param page
	 * @return
	 */
	public Page findPagebyParams(Map<String,Object> params,Page page);
	
	/**Methods Name: selectCreateParams <br>
	 * Description: 查询创建dbUser相关参数<br>
	 * @author name: liuhao1
	 * @param id
	 * @param isVip
	 * @return
	 */
	public Map<String,Object> selectCreateParams(Long id,boolean isVip);

	/**Methods Name: selectByDbNameForValidate <br>
	 * Description: 根据数据库名查询数据：用于验证数据库名是否重复<br>
	 * @author name: liuhao1
	 * @param dbName
	 * @return
	 */
	public List<DbModel> selectByDbNameForValidate(String dbName,Long createUser);

	/**Methods Name: deleteByMclusterId <br>
	 * Description: 根据container集群id删除对应db<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 */
	public void deleteByMclusterId(Long mclusterId);
	
	public DbModel dbList(Long dbId);
	
	/**Methods Name: selectDbByMclusterId <br>
	 * Description: 根据集群id查找db<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 * @return
	 */
	public List<DbModel> selectDbByMclusterId(Long mclusterId);

	public Map<String, Object> getGbaConfig(Long dbId);

	public Integer selectCountByStatus(Integer _parameter);
	
	/**
	  * @Title: selectPendingAuditDb
	  * @Description: 获取所有待审核db信息
	  * @return List<DbModel>   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月18日 下午8:18:52
	  */
	public List<DbModel> selectPendingAuditDb();
	/**
	  * @Title: getPagePendingAuditDb
	  * @Description: 获取分页待审核db信息
	  * @param page
	  * @param params
	  * @return Page   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月22日 上午9:23:09
	  */
	public Page getPagePendingAuditDb(Page page, Map<String, Object> params);

}
