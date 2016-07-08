/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IGcePackageContainerDao;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageContainer;
import com.letv.portal.service.elasticcalc.gce.IGcePackageContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE应用包容器服务
 * @author linzhanbo .
 * @since 2016年7月1日, 下午1:05:40 .
 * @version 1.0 .
 */
@Service("gcePackageContainerService")
public class GcePackageContainerServiceImpl extends BaseServiceImpl<EcGcePackageContainer>
		implements IGcePackageContainerService {
	@Resource
	private IGcePackageContainerDao gcePackageContainerDao;
	
	public GcePackageContainerServiceImpl() {
		super(EcGcePackageContainer.class);
	}

	@Override
	public IBaseDao<EcGcePackageContainer> getDao() {
		return this.gcePackageContainerDao;
	}
}
