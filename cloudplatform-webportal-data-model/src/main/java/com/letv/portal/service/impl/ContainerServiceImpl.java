package com.letv.portal.service.impl;

import com.letv.common.dao.IBaseDao;
import com.letv.common.dao.QueryParam;
import com.letv.common.paging.impl.Page;
import com.letv.portal.dao.IContainerDao;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.service.IContainerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("containerService")
public class ContainerServiceImpl extends BaseServiceImpl<ContainerModel> implements
		IContainerService {
	
	@Resource
	private IContainerDao containerDao;

	public ContainerServiceImpl() {
		super(ContainerModel.class);
	}

	@Override
	public IBaseDao<ContainerModel> getDao() {
		return this.containerDao;
	}

	@Override
	public Page findPagebyParams(Map<String, Object> params, Page page) {
		QueryParam param = new QueryParam(params,page);
		page.setData(this.containerDao.selectPageByMap(param));
		page.setTotalRecords(this.containerDao.selectByMapCount(params));
		return page;
		
	}
	
	@Override
	public void insert(ContainerModel t) {
		super.insert(t);
	}

	@Override
	public List<ContainerModel> selectByMclusterId(Long mclusterId) {
		return this.containerDao.selectByMclusterId(mclusterId);
	}
	@Override
	public List<ContainerModel> selectVipByClusterId(Long mclusterId) {
		return this.containerDao.selectVipByClusterId(mclusterId);
	}

	@Override
	public void deleteByMclusterId(Long mclusterId) {
		this.containerDao.deleteByMclusterId(mclusterId);
		
	}

	@Override
	public ContainerModel selectByName(String containerName) {
		return this.containerDao.selectByName(containerName);
	}
	public  List<ContainerModel> selectContainerByMclusterId(Long clusterId){
		return this.containerDao.selectContainerByMclusterId(clusterId);
	}
	
	public  List<ContainerModel> selectAllByMap(Map<String, Object> map){
		return this.containerDao.selectAllByMap(map);	
	}

	@Override
	public List<ContainerModel> selectVaildVipContainers(Map<String,Object> params) {
		return this.selectValidContianerByType("mclustervip", params);
	}
	@Override
	public List<ContainerModel> selectVaildNormalContainers(Map<String,Object> params) {
		return this.selectValidContianerByType("mclusternode", params);
	}
	
	private List<ContainerModel> selectValidContianerByType(String type,Map<String,Object> params){
		if(params == null)
			params = new HashMap<String,Object>();
		params.put("type", type);
		return this.containerDao.selectValidByMap(params);
	}
	
	@Override
	public ContainerModel selectValidVipContianer(Long mclusterId,String type,Map<String,Object> params){
		if(params == null)
			params = new HashMap<String,Object>();
		params.put("mclusterId", mclusterId);
		params.put("type", type);
		List<ContainerModel> containers = this.selectAllByMap(params);
		if(containers.isEmpty()) {
			return null;
		}
		return containers.get(0);
	}
	

}
