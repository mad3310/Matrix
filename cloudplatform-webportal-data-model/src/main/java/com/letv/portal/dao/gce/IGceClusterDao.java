package com.letv.portal.dao.gce;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.gce.GceCluster;

public interface IGceClusterDao extends IBaseDao<GceCluster> {

	List<GceCluster> selectByName(String clusterName);

	Integer selectValidClusterCount();
	
	List<GceCluster> selectValidCluster();
}
