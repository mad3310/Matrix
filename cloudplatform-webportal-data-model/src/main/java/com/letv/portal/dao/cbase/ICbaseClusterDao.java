package com.letv.portal.dao.cbase;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.cbase.CbaseClusterModel;

public interface ICbaseClusterDao extends IBaseDao<CbaseClusterModel> {

	List<CbaseClusterModel> selectByName(String clusterName);

	Integer selectValidClusterCount();
}
