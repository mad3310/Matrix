/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce;

import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.service.IBaseService;

/**
 * IGceServerService
 * 
 * @author linzhanbo .
 * @since 2016年6月28日, 下午2:18:02 .
 * @version 1.0 .
 */
public interface IGceService extends IBaseService<EcGce> {
	public void saveGceExt(EcGceExt gceExt);
}
