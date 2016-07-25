package com.letv.portal.service.es;

import java.util.List;
import java.util.Map;

import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.service.IBaseService;

public interface IEsClusterService extends IBaseService<EsCluster> {

	Boolean isExistByName(String string);

	List<EsCluster> selectByName(String clusterName);
	
}
