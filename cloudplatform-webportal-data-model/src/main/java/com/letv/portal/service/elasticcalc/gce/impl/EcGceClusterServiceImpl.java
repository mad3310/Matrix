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
import com.letv.portal.dao.elasticcalc.gce.IEcGceClusterDao;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE应用包集群服务
 * @author linzhanbo .
 * @since 2016年6月29日, 上午10:10:06 .
 * @version 1.0 .
 */
@Service("ecGceClusterService")
public class EcGceClusterServiceImpl extends BaseServiceImpl<EcGceCluster> implements
		IEcGceClusterService {
	@Resource
	private IEcGceClusterDao ecGceClusterDao;
	
	public EcGceClusterServiceImpl() {
		super(EcGceCluster.class);
	}

	@Override
	public IBaseDao<EcGceCluster> getDao() {
		return this.ecGceClusterDao;
	}

	@Override
	public Boolean isExistByName(String clusterName) {
		List<EcGceCluster> mclusters = this.ecGceClusterDao.selectByName(clusterName);
		return mclusters.size() == 0?true:false;
	}
}
