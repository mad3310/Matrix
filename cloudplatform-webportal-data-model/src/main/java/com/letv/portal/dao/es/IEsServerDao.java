package com.letv.portal.dao.es;

import java.util.List;
import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.es.EsServer;

public interface IEsServerDao extends IBaseDao<EsServer> {
	List<EsServer> selectBySelective(Map<String,Object> exParams);
	
	Integer selectCountByStatus(Integer value);
	
	Integer selectBySelectiveCount(Map<String,Object> exParams);
}
