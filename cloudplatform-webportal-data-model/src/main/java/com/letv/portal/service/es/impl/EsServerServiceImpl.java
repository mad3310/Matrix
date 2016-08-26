package com.letv.portal.service.es.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.dao.QueryParam;
import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.portal.dao.es.IEsServerDao;
import com.letv.portal.enumeration.EsStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.service.es.IEsClusterService;
import com.letv.portal.service.es.IEsContainerService;
import com.letv.portal.service.es.IEsServerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("esServerService")
public class EsServerServiceImpl extends BaseServiceImpl<EsServer> implements IEsServerService{
	
	private final static Logger logger = LoggerFactory.getLogger(EsServerServiceImpl.class);
	
	@Resource
	private IEsServerDao esServerDao;
	@Autowired
	private IEsClusterService esClusterService;
	@Autowired
	private IEsContainerService esContainerService;
	@Value("${es.code}")
	private String ES_CODE;
	
	public EsServerServiceImpl() {
		super(EsServer.class);
	}

	@Override
	public IBaseDao<EsServer> getDao() {
		return this.esServerDao;
	}
	
	@Override
	public void insert(EsServer t) {
		if(t == null)
			throw new ValidateException("参数不合法");
		t.setStatus(EsStatus.NORMAL);
		super.insert(t);
	}

	@Override
	public Map<String, Object> insertEsServerAndCluster(EsServer esServer) {
		esServer.setStatus(EsStatus.BUILDDING);
		
		StringBuffer clusterName = new StringBuffer();
		clusterName.append(esServer.getCreateUser()).append("_").append(ES_CODE).append("_").append(esServer.getEsName());
		
		/*function 验证clusterName是否存在*/
		int i = 0;
		boolean isExist = true;
		while(isExist) {
			isExist= this.esClusterService.isExistByName(clusterName.toString());
			if(isExist)
				clusterName.append(++i);
		}
		
		EsCluster esCluster = new EsCluster();
		esCluster.setHclusterId(esServer.getHclusterId());
		esCluster.setClusterName(clusterName.toString());
		esCluster.setStatus(MclusterStatus.BUILDDING);
		esCluster.setAdminUser("root");
		esCluster.setAdminPassword(clusterName.toString());
		
		this.esClusterService.insert(esCluster);
		
		esServer.setEsClusterId(esCluster.getId());
		
		this.esServerDao.insert(esServer);
		
		Map<String,Object> params = new HashMap<String,Object>();
    	params.put("esClusterId", esCluster.getId());
    	params.put("esId", esServer.getId());
    	params.put("serviceName", esServer.getEsName());
    	params.put("clusterName", esCluster.getClusterName());
		return params;
	}
	
	public <K, V> Page selectPageByParams(Page page, Map<K, V> params){
		page = super.selectPageByParams(page, params);
		List<EsServer> esServers = (List<EsServer>) page.getData();
		
		for(EsServer esServer : esServers){
			List<EsContainer> esContainers = this.esContainerService.selectContainersByEsClusterId(esServer.getEsClusterId());
			esServer.setEsContainers(esContainers);
		}
		page.setData(esServers);
		return page;
	}
	
	public EsServer selectById(Long id){
		EsServer esServer = this.esServerDao.selectById(id);
		return esServer;
	}

	@Override
	public List<EsServer> selectBySelective(Map<String,Object> exParams) {
		return esServerDao.selectBySelective(exParams);
	}

	@Override
	public EsServer selectByIdWithContainers(Long id) {
		EsServer esServer = this.esServerDao.selectById(id);
		List<EsContainer> esContainers = this.esContainerService.selectContainersByEsClusterId(esServer.getEsClusterId());
		esServer.setEsContainers(esContainers);
		return esServer;
	}

	@Override
	public Integer selectCountByStatus(Integer value) {
		return this.esServerDao.selectCountByStatus(value);
	}
	
	@Override
	public <K, V> Integer selectByMapCount(Map<K, V> map) {
		QueryParam param = new QueryParam();
		param.setParams(map);
		return this.esServerDao.selectByMapCount(param);
	}

}
