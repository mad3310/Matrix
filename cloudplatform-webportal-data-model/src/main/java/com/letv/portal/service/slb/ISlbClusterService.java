package com.letv.portal.service.slb;

import java.util.List;
import java.util.Map;

import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.service.IBaseService;

public interface ISlbClusterService extends IBaseService<SlbCluster> {

	public Boolean isExistByName(String string);
	
	public List<SlbCluster> selectByName(String clusterName);

	public void asyncClusterCount(Map<String,Object> mm,HclusterModel hcluster);

	public Integer selectValidClusterCount();
	
	public List<SlbCluster> selectValidCluster();
}
