/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce;

import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.service.IBaseService;

/**
 * IEcGceClusterService
 * @author linzhanbo .
 * @since 2016年6月29日, 上午10:07:50 .
 * @version 1.0 .
 */
public interface IEcGceClusterService extends IBaseService<EcGceCluster> {
	/**
	 * 使用集群名判断该集群是否存在
	 * @param string
	 * @return
	 * @author linzhanbo .
	 * @since 2016年6月29日, 上午10:15:36 .
	 * @version 1.0 .
	 */
	Boolean isExistByName(String clusterName);
}
