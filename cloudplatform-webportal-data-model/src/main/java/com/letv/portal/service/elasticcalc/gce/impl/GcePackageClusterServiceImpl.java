/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IGcePackageClusterDao;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageCluster;
import com.letv.portal.service.elasticcalc.gce.IGcePackageClusterService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE应用包集群服务
 * @author linzhanbo .
 * @since 2016年6月29日, 上午10:10:06 .
 * @version 1.0 .
 */
@Service("gcePackageClusterService")
public class GcePackageClusterServiceImpl extends BaseServiceImpl<EcGcePackageCluster> implements
		IGcePackageClusterService {
	@Resource
	private IGcePackageClusterDao gcePackageClusterDao;
	
	public GcePackageClusterServiceImpl() {
		super(EcGcePackageCluster.class);
	}

	@Override
	public IBaseDao<EcGcePackageCluster> getDao() {
		return this.gcePackageClusterDao;
	}

	@Override
	public Boolean isExistByName(String clusterName) {
		List<EcGcePackageCluster> mclusters = this.gcePackageClusterDao.selectByName(clusterName);
		return mclusters.size() == 0?true:false;
	}
}
