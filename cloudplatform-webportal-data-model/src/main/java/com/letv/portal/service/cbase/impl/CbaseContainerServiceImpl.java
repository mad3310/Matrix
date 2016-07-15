package com.letv.portal.service.cbase.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.cbase.ICbaseContainerDao;
import com.letv.portal.model.cbase.CbaseContainerModel;
import com.letv.portal.service.cbase.ICbaseContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("cbaseContainerService")
public class CbaseContainerServiceImpl extends
		BaseServiceImpl<CbaseContainerModel> implements ICbaseContainerService {

	private final static Logger logger = LoggerFactory
			.getLogger(CbaseContainerServiceImpl.class);

	@Resource
	private ICbaseContainerDao cbaseContainerDao;

	public CbaseContainerServiceImpl() {
		super(CbaseContainerModel.class);
	}

	@Override
	public IBaseDao<CbaseContainerModel> getDao() {
		return this.cbaseContainerDao;
	}

	@Override
	public List<CbaseContainerModel> selectByCbaseClusterId(Long cbaseClusterId) {
		return this.cbaseContainerDao
				.selectContainerByCbaseClusterId(cbaseClusterId);
	}

	@Override
	public CbaseContainerModel selectByName(String containerName) {
		return this.cbaseContainerDao.selectByName(containerName);
	}

	@Override
	public void deleteByClusterId(Long id) {
		this.cbaseContainerDao.deleteByClusterId(id);
	}
}
