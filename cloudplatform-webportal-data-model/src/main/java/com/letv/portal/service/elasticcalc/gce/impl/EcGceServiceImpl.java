/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IEcGceDao;
import com.letv.portal.dao.elasticcalc.gce.IEcGceExtDao;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE服务
 * 
 * @author linzhanbo .
 * @since 2016年6月28日, 下午2:20:20 .
 * @version 1.0 .
 */
@Service("ecGceService")
public class EcGceServiceImpl extends BaseServiceImpl<EcGce> implements
		IEcGceService {
	@Resource
	private IEcGceDao ecGceDao;
	@Resource
	private IEcGceExtDao ecGceExtDao;
	@Autowired
	private IEcGceClusterService ecGceClusterService;
	
	public EcGceServiceImpl() {
		super(EcGce.class);
	}

	@Override
	public IBaseDao<EcGce> getDao() {
		return this.ecGceDao;
	}

	@Override
	public void insertGceExt(EcGceExt gceExt) {
		this.ecGceExtDao.insert(gceExt);
	}

	@Override
	public EcGceExt selectGceExtByGceId(Long gceId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gceId", gceId);
		List<EcGceExt> ecGceExts = this.ecGceExtDao.selectByMap(params);
		if(ecGceExts != null && ecGceExts.size()>0){
			return ecGceExts.get(0);
		}
		return null;
	}

}
