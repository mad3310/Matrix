package com.letv.portal.service.gce;

import java.util.List;
import java.util.Map;

import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.service.IBaseService;

public interface IGceClusterService extends IBaseService<GceCluster> {

	Boolean isExistByName(String string);

	List<GceCluster> selectByName(String clusterName);

	void asyncClusterCount(Map<String,Object> mm,HclusterModel hcluster);

	public Integer selectValidClusterCount();
	
	public List<GceCluster> selectValidCluster();
}
