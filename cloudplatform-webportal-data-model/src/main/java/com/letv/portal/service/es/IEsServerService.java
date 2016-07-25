package com.letv.portal.service.es;

import java.util.List;
import java.util.Map;

import com.letv.portal.model.es.EsServer;
import com.letv.portal.service.IBaseService;

public interface IEsServerService extends IBaseService<EsServer> {

	Map<String, Object> insertEsServerAndCluster(EsServer esServer);
	
	List<EsServer> selectBySelective(Map<String,Object> exParams);
}
