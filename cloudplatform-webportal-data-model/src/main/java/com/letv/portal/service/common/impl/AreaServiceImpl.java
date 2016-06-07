package com.letv.portal.service.common.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.common.IAreaDao;
import com.letv.portal.model.common.AreaModel;
import com.letv.portal.service.common.IAreaService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("areaService")
public class AreaServiceImpl extends BaseServiceImpl<AreaModel> implements IAreaService{
	
	private final static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);
	
	@Resource
	private IAreaDao areaDao;

	public AreaServiceImpl() {
		super(AreaModel.class);
	}

	@Override
	public IBaseDao<AreaModel> getDao() {
		return this.areaDao;
	}

	@Override
	public List<AreaModel> selectAllNonParentArea() {
		return this.areaDao.selectAllNonParentArea();
	}

}
