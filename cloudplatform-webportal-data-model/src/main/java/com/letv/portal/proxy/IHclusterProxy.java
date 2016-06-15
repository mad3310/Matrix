package com.letv.portal.proxy;

import com.letv.portal.model.HclusterModel;

/**
 * 物理机代理类
 * @author lisuxiao
 *
 */
public interface IHclusterProxy extends IBaseProxy<HclusterModel> {

	/**
	 * 保存记录并把ip池加入cmdb
	 * @param hclusterModel
	 */
	public void insertAndRegisteIpsToCmdb(HclusterModel hclusterModel);
	/**
	 * 更新记录并更新cmdb ip池
	 * @param hclusterModel
	 */
	public void updateAndRegisteIpsToCmdb(HclusterModel hclusterModel);
}
