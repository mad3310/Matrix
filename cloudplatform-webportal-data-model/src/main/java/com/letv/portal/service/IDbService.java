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
	 * @return
	 */
	public Map<String,String> selectCreateParams(Long id);

	/**Methods Name: selectByDbName <br>
	 * Description: 根据数据库名查询数据：用于验证数据库名是否重复<br>
	 * @author name: liuhao1
	 * @param dbName
	 * @return
	 */
	public List<DbModel> selectByDbName(String dbName);

}
