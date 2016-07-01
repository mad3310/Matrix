/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IGcePackageImageDao;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageImage;
import com.letv.portal.service.elasticcalc.gce.IGcePackageImageService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE应用包镜像服务
 * @author linzhanbo .
 * @since 2016年6月29日, 下午6:28:57 .
 * @version 1.0 .
 */
@Service("gcePackageImageService")
public class GcePackageImageServiceImpl extends BaseServiceImpl<EcGcePackageImage> implements
		IGcePackageImageService {

	@Resource
	private IGcePackageImageDao gcePackageImageDao;
	
	public GcePackageImageServiceImpl() {
		super(EcGcePackageImage.class);
	}

	@Override
	public IBaseDao<EcGcePackageImage> getDao() {
		return this.gcePackageImageDao;
	}

}
