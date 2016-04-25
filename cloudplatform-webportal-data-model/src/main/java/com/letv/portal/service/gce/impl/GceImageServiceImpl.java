package com.letv.portal.service.gce.impl;

import javax.annotation.Resource;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.GceImageStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.UserModel;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IUserService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.gce.IGceImageDao;
import com.letv.portal.model.gce.GceImage;
import com.letv.portal.service.gce.IGceImageService;
import com.letv.portal.service.impl.BaseServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("gceImageService")
public class GceImageServiceImpl extends BaseServiceImpl<GceImage> implements IGceImageService{
	
	private final static Logger logger = LoggerFactory.getLogger(GceImageServiceImpl.class);
	
	@Resource
	private IGceImageDao gceImageDao;
	@Resource
	private IHostService hostService;
	@Resource
	private IGcePythonService gcePythonService;
	@Autowired
	private IUserService userService;


	public GceImageServiceImpl() {
		super(GceImage.class);
	}

	@Override
	public IBaseDao<GceImage> getDao() {
		return this.gceImageDao;
	}

	@Override
	public GceImage selectByUrl(String url) {
		return this.gceImageDao.selectByUrl(url);
	}

	@Override
	public void pushImage(Long id, String hclusterIds) {
		if(null == id || StringUtils.isEmpty(hclusterIds))
			throw new ValidateException("参数不合法");
		GceImage gceImage = this.selectById(id);
		if(gceImage == null)
			throw new ValidateException("参数不合法");
		String[] arrHcluster = hclusterIds.split(",");
		if(null == arrHcluster || 0 == hclusterIds.length())
			throw new ValidateException("参数不合法");

		Map<String,String> params = new HashMap<String,String>();
		params.put("image",gceImage.getUrl());
		for (String hclusterId:arrHcluster) {
			List<HostModel> hostModels = this.hostService.selectByHclusterId(Long.parseLong(hclusterId));
			for (HostModel host:hostModels) {
				ApiResultObject apiResultObject = this.gcePythonService.pushImage(params, host.getHostIp(), host.getName(), host.getPassword());
				logger.info("push gceImage to host:{},{}",apiResultObject.getUrl(),apiResultObject.getResult());
			}
		}
	}

	@Override
	public void insert(GceImage gceImage) {
		if(null == gceImage.getStatus()) {
			gceImage.setStatus(GceImageStatus.AVAILABLE);
		}
		super.insert(gceImage);
	}

	@Override
	public void saveInfoWithUserNames(String userNames, GceImage gceImage) {
		String[] operatorNames = userNames.split(",");
		for (String name : operatorNames) {
			UserModel user = this.userService.getUserByName(name);
			gceImage.setOwner(user.getId());
			super.insert(gceImage);
		}
	}
}
