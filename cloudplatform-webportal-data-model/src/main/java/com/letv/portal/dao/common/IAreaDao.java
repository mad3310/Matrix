package com.letv.portal.dao.common;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.common.AreaModel;

public interface IAreaDao extends IBaseDao<AreaModel> {
	List<AreaModel> selectAllNonParentArea();
}
