package com.letv.portal.service.es.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.log.ILogContainerDao;
import com.letv.portal.model.log.LogContainer;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.log.ILogContainerService;

@Service("esContainerService")
public class EsContainerServiceImpl extends BaseServiceImpl<LogContainer> implements ILogContainerService{
	
	private final static Logger logger = LoggerFactory.getLogger(EsContainerServiceImpl.class);
	
	@Resource
	private ILogContainerDao logContainerDao;

	public EsContainerServiceImpl() {
		super(LogContainer.class);
	}

	@Override
	public IBaseDao<LogContainer> getDao() {
		return this.logContainerDao;
	}

	@Override
	public List<LogContainer> selectByLogClusterId(Long logClusterId) {
		return this.logContainerDao.selectContainerByLogClusterId(logClusterId);
	}

	@Override
	public LogContainer selectByName(String containerName) {
		return this.logContainerDao.selectByName(containerName);
	}

}
