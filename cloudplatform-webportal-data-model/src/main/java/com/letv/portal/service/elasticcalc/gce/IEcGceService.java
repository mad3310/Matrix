/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce;

import java.util.Map;

import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.service.IBaseService;

/**
 * IEcGceService
 * 
 * @author linzhanbo .
 * @since 2016年6月28日, 下午2:18:02 .
 * @version 1.0 .
 */
public interface IEcGceService extends IBaseService<EcGce> {
	/**
	 * 保存GCE扩展
	 * @param gceExt
	 * @author linzhanbo .
	 * @since 2016年7月6日, 下午1:00:36 .
	 * @version 1.0 .
	 */
	public void insertGceExt(EcGceExt gceExt);
	/**
	 * 使用gceId查询GCE扩展
	 * @param gceId
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月6日, 下午1:00:46 .
	 * @version 1.0 .
	 */
	public EcGceExt selectGceExtByGceId(Long gceId);
	/**
	 * 使用查询条件查询GCE数量
	 * @param exParams
	 * @return
	 * @author linzhanbo .
	 * @since 2016年8月26日, 下午2:47:20 .
	 * @version 1.0 .
	 */
	public Integer selectBySelectiveCount(Map<String,Object> exParams);
}
