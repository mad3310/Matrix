package com.letv.portal.service.es.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.es.IEsContainerDao;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.service.es.IEsContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("esContainerService")
public class EsContainerServiceImpl extends BaseServiceImpl<EsContainer> implements IEsContainerService{
	
	private final static Logger logger = LoggerFactory.getLogger(EsContainerServiceImpl.class);
	
	@Resource
	private IEsContainerDao esContainerDao;

	public EsContainerServiceImpl() {
		super(EsContainer.class);
	}

	@Override
	public IBaseDao<EsContainer> getDao() {
		return this.esContainerDao;
	}

	@Override
	public List<EsContainer> selectContainersByEsClusterId(Long esClusterId) {
		return esContainerDao.selectContainersByEsClusterId(esClusterId);
	}

	@Override
	public EsContainer selectByName(String containerName) {
		return null;
	}


}
