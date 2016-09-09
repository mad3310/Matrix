package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.common.paging.impl.Page;
import com.letv.portal.model.MclusterModel;

/**Program Name: IMclusterService <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IMclusterService extends IBaseService<MclusterModel> {
	
	/**Methods Name: findPagebyParams <br>
	 * Description: 根据查询条件查出分页数据<br>
	 * @author name: liuhao1
	 * @param params
	 * @param page
	 * @return
	 */
	public Page findPagebyParams(Map<String,Object> params,Page page);
	
	/**Methods Name: audit <br>
	 * Description: 改变status状态<br>
	 * @author name: liuhao1
	 * @param mclusterModel
	 */
	void audit(MclusterModel mclusterModel);

	/**Methods Name: selectByClusterName <br>
	 * Description: 根据mcluster名称查询数据：用于验证mcluster名是否重复<br>
	 * @author name: liuhao1
	 * @param applyCode
	 * @return
	 */
	public List<MclusterModel> selectByName(String mclusterName);

	public List<MclusterModel> select4Run();

	public boolean isExistByName(String mclusterName);

	/**Methods Name: selectValidMclusters <br>
	 * Description: 选择有效的mcluster集群: RUNNING(1),STARTING(7),STOPPING(8),STOPED(9),DANGER(13),CRISIS(14).<br>
	 * @author name: liuhao1
	 * @return
	 */
	public List<MclusterModel> selectValidMclusters();
	public Integer selectValidMclusterCount();
	/**Methods Name: selectValidMclusters <br>
	 * Description: 选择有效的mcluster集群: RUNNING(1),STARTING(7),STOPPING(8),STOPED(9),DANGER(13),CRISIS(14).<br>
	 * @author name: liuhao1
	 * @param stage 根据stage获取部分集群
	 * @return
	 */
	public List<MclusterModel> selectValidMclusters(int count);
	public List<MclusterModel> selectValidMclusters(int count,Map<String,Object> params);
	
	/**Methods Name: selectValidMclusters <br>
	 * Description: 选择有效的mcluster集群，并包含MclusterName和HclusterName: RUNNING(1),STARTING(7),STOPPING(8),STOPED(9),DANGER(13),CRISIS(14).<br>
	 * @author name: liuhao1
	 * @return
	 */
	public List<MclusterModel> selectValidMclustersByMap(Map<String, Object> params);

	public List<MclusterModel> selectNextValidMclusterById(Long mclusterId,Long hclusterId,
			int addNewCount);
	
	/**
	 * 修改集群所属用户
	 * @param mclusterId
	 */
	void updateUser(Long mclusterId, Long userId);


}