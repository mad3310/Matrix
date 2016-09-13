/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IEcGceContainerDao;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE应用包容器服务
 * @author linzhanbo .
 * @since 2016年7月1日, 下午1:05:40 .
 * @version 1.0 .
 */
@Service("ecGceContainerService")
public class EcGceContainerServiceImpl extends BaseServiceImpl<EcGceContainer>
		implements IEcGceContainerService {
	@Resource
	private IEcGceContainerDao ecGceContainerDao;
	
	public EcGceContainerServiceImpl() {
		super(EcGceContainer.class);
	}

	@Override
	public IBaseDao<EcGceContainer> getDao() {
		return this.ecGceContainerDao;
	}

	@Override
	public void deleteBySelective(Map<String, Object> map) {
		this.ecGceContainerDao.deleteBySelective(map);
	}
}
