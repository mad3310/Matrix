package com.letv.portal.service.cbase;

import java.util.List;
import java.util.Map;

import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.cbase.CbaseClusterModel;
import com.letv.portal.model.log.LogCluster;
import com.letv.portal.service.IBaseService;

public interface ICbaseClusterService extends IBaseService<CbaseClusterModel> {
	public void asyncClusterCount(Map<String,Object> mm,HclusterModel hcluster);
	List<CbaseClusterModel> selectByName(String clusterName);

	public Integer selectValidClusterCount();
}
