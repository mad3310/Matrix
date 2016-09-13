/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.dao.elasticcalc.gce;

import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;

/**
 * IEcGceContainerDao
 * @author linzhanbo .
 * @since 2016年7月1日, 下午1:07:04 .
 * @version 1.0 .
 */
public interface IEcGceContainerDao extends IBaseDao<EcGceContainer> {

	void deleteBySelective(Map<String, Object> map);

}
