package com.letv.portal.dao.slb;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.slb.SlbCluster;

public interface ISlbClusterDao extends IBaseDao<SlbCluster> {

	List<SlbCluster> selectByName(String clusterName);

	Integer selectValidClusterCount();
	
	List<SlbCluster> selectValidCluster();
}
