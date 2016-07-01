/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.dao.elasticcalc.gce;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageCluster;
import com.letv.portal.model.gce.GceCluster;

/**
 * IGcePackageClusterDao
 * @author linzhanbo .
 * @since 2016年6月29日, 上午10:11:39 .
 * @version 1.0 .
 */
public interface IGcePackageClusterDao extends IBaseDao<EcGcePackageCluster> {
	/**
	 * 使用集群名称查询集群列表
	 * @param clusterName
	 * @return
	 * @author linzhanbo .
	 * @since 2016年6月29日, 上午10:17:09 .
	 * @version 1.0 .
	 */
	List<EcGcePackageCluster> selectByName(String clusterName);
}
