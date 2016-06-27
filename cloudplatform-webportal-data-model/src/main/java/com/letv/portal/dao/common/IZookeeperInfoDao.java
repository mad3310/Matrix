package com.letv.portal.dao.common;

import java.util.List;
import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.common.ZookeeperInfo;

public interface IZookeeperInfoDao extends IBaseDao<ZookeeperInfo> {

	ZookeeperInfo selectMinusedZk();

	List<ZookeeperInfo> selectMinusedZkByHclusterId(Map<String,Object> params);

	void plusOneUsedByZookeeperId(Long zookeeperId);
}
