package com.letv.portal.service.es.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.es.IEsClusterDao;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.es.IEsClusterService;
import com.letv.portal.service.es.IEsContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("esClusterService")
public class EsClusterServiceImpl extends BaseServiceImpl<EsCluster> implements IEsClusterService{
	
	private final static Logger logger = LoggerFactory.getLogger(EsClusterServiceImpl.class);
	
	@Resource
	private IEsClusterDao esClusterDao;
	@Resource
	private IEsContainerService esContainerService;
	@Resource
	private IHostService hostService;
	
	public EsClusterServiceImpl() {
		super(EsCluster.class);
	}

	@Override
	public IBaseDao<EsCluster> getDao() {
		return this.esClusterDao;
	}

	@Override
	public Boolean isExistByName(String clusterName) {
		List<EsCluster> mclusters = this.esClusterDao.selectByName(clusterName);
		return mclusters.size() == 0 ? false : true;
	}
	
	@Override
	public List<EsCluster> selectByName(String clusterName) {
		return this.esClusterDao.selectByName(clusterName);
	}

	@Override
	public Integer selectValidClusterCount() {
		return this.esClusterDao.selectValidClusterCount();
	}
	
}
