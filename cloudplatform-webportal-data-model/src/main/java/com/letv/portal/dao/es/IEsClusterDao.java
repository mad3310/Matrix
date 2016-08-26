package com.letv.portal.dao.es;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.es.EsCluster;

public interface IEsClusterDao extends IBaseDao<EsCluster> {

	List<EsCluster> selectByName(String clusterName);
	
	Integer selectValidClusterCount();

}
