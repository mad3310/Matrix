/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IGceDao;
import com.letv.portal.dao.elasticcalc.gce.IGceExtDao;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.service.elasticcalc.gce.IGceService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE服务
 * 
 * @author linzhanbo .
 * @since 2016年6月28日, 下午2:20:20 .
 * @version 1.0 .
 */
@Service("gceService")
public class GceServiceImpl extends BaseServiceImpl<EcGce> implements
		IGceService {
	@Resource
	private IGceDao gceDao;
	@Resource
	private IGceExtDao gceExtDao;
	@Autowired
	private IGceClusterService gceClusterService;
	
	public GceServiceImpl() {
		super(EcGce.class);
	}

	@Override
	public IBaseDao<EcGce> getDao() {
		return this.gceDao;
	}

	@Override
	public void saveGceExt(EcGceExt gceExt) {
		this.gceExtDao.insert(gceExt);
	}

}
