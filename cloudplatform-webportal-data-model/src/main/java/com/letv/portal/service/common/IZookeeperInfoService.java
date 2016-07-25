package com.letv.portal.service.common;

import java.util.List;

import com.letv.portal.model.common.ZookeeperInfo;
import com.letv.portal.service.IBaseService;

public interface IZookeeperInfoService extends IBaseService<ZookeeperInfo> {

	ZookeeperInfo selectMinusedZk();
	List<ZookeeperInfo> selectMinusedZkByHclusterId(Long hclusterId,int number);

	void batchInsert(String name, String ip, Long hclusterId);
	
	void plusOneUsedByZookeeperId(Long zookeeperId);
}
