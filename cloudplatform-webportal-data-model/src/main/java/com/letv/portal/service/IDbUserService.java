package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.common.paging.impl.Page;
import com.letv.portal.model.DbUserModel;

/**Program Name: IDbUserService <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月1日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IDbUserService extends IBaseService<DbUserModel> {
	
	/**Methods Name: selectByDbId <br>
	 * Description: 根据dbId查询dbUser列表<br>
	 * @author name: liuhao1
	 * @param dbId
	 * @return
	 */
	public List<DbUserModel> selectByDbId(Long dbId);
	
	/**Methods Name: findPagebyParams <br>
	 * Description: dbUser列表<br>
	 * @author name: liuhao1
	 * @param params
	 * @param page
	 * @return
	 */
	public Page findPagebyParams(Map<String,Object> params,Page page);
	
	/**Methods Name: selectCreateParams <br>
	 * Description: 查询创建用户相关参数<br>
	 * @author name: liuhao1
	 * @param id
	 * @param isVip 
	 * @return
	 */
	public Map<String,Object> selectCreateParams(Long id, boolean isVip);

	/**Methods Name: updateStatus <br>
	 * Description: 根据数据库用户名和ip查询数据：用于验证数据库用户名和ip是否重复<br>
	 * @author name: yaokuo
	 * @param dbUserModel
	 */
	public List<DbUserModel> selectByIpAndUsername(DbUserModel dbUserModel);
	/**
	 * Methods Name: insertAndAcceptIp <br>
	 * Description: 插入dbUser对应Ip
	 * @author name: wujun
	 * @param dbUserModel
	 */
	public void insertDbUserAndAcceptIp(DbUserModel dbUserModel);
    /**
     * Methods Name: updateStatus <br>
     * Description: 更新状态
     * @author name: wujun
     * @param dbUserModel
     */
	public void updateStatus(DbUserModel dbUserModel);
	/**
	 * Methods Name: updateDbUser <br>
	 * Description: 修改dbUser信息<br>
	 * @author name: wujun
	 * @param dbUserModel
	 */
	public void updateDbUser(DbUserModel dbUserModel);
	/**
	 * Methods Name: deleteDbUser <br>
	 * Description: 删除dbUser信息<br>
	 * @author name: wujun
	 * @param dbUserModel
	 * @param dbUserId 
	 */
	 public void deleteDbUser(String dbUserId);

	/**Methods Name: deleteByDbId <br>
	 * Description: 根据db删除用户<br>
	 * @author name: liuhao1
	 */
	public void deleteByDbId(Long dbId);
	/**
	 * Methods Name: buildDbUser <br>
	 * Description: 审批DbUser<br>
	 * @author name: wujun
	 * @param dbUserId
	 */
	public void buildDbUser(String dbUserId);

	/**Methods Name: selectGroupByName <br>
	 * Description: 按照参数查出用户，并按照用户名分组<br>
	 * @author name: liuhao1
	 * @param params
	 * @return
	 */
	public List<DbUserModel> selectGroupByName(Map<String, Object> params);
	
	/**Methods Name: selectMarkIps4dbUser <br>
	 * Description: 新增及修改用户时，ip选择数据，包括未选择数据和已选择数据<br>
	 * @author name: liuhao1
	 * @param dbId
	 * @param username
	 * @return
	 */
	public List<Map<String,Object>> selectMarkIps4dbUser(Long dbId,String username);

	/**Methods Name: selectByDbIdAndUsername <br>
	 * Description: 根据用户名和dbId查出dbUser列表<br>
	 * @author name: liuhao1
	 * @param dbId
	 * @param username
	 * @return
	 */
	public List<DbUserModel> selectByDbIdAndUsername(Long dbId, String username);
	
	/**Methods Name: selectIpsFromUser <br>
	 * Description: 根据dbId和默认只读用户名获取ip列表<br>
	 * @author name: liuhao1
	 * @param dbId
	 * @return
	 */
	public List<String> selectIpsFromUser(Long dbId);
	
	public void createDefalutAdmin(Long dbId);

	/**Methods Name: updateDescnByUsername <br>
	 * Description: 修改描述<br>
	 * @author name: liuhao1
	 * @param dbUserModel
	 */
	public void updateDescnByUsername(DbUserModel dbUserModel);
	
	/**
	 * 判断数据库账号是否合法
	 * @param dbUserName
	 * @param dbId
	 * @return
	 */
	public boolean isLegalDbUserName(String dbUserName, Long dbId);
	
	/**
	 * 检查ip是否已被使用
	 * @param dbId 数据库id
	 * @param ip 
	 * @return true-使用，false-未使用
	 */
	public boolean checkIp(Long dbId, String ip);
	
	/**
	 * 根据dbId更新更新db用户表
	 * @param mclusterId
	 * @param userId
	 */
	void updateUserByDbId(Long mclusterId, Long userId);
	
}
