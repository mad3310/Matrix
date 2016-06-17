package com.letv.portal.service.common;

import java.util.List;

import com.letv.portal.model.common.AreaModel;
import com.letv.portal.service.IBaseService;

public interface IAreaService extends IBaseService<AreaModel> {
	/**
	 * 获取所有非父节点地区信息
	 */
	List<AreaModel> selectAllNonParentArea();

}
