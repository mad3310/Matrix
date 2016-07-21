/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.controller.elasticcalc.gce;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.jacksonext.annotation.ExcludeProperty;
import com.letv.common.util.jacksonext.annotation.JsonFilterProperties;
import com.letv.portal.enumeration.HclusterStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.proxy.IGceProxy;
import com.letv.portal.service.IHclusterService;

/**
 * 弹性计算GCE服务对外接口
 * 
 * @author linzhanbo .
 * @since 2016年6月28日, 上午9:43:14 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/ecgce")
public class ECGceController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGceController.class);

	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IGceProxy gceProxy;
	@Resource
	private IHclusterService hclusterService;

	@JsonFilterProperties(excluses = {
			@ExcludeProperty(pojo = ResultObject.class, names = { "callback" }),
			@ExcludeProperty(pojo = EcGce.class, names = { "status", "areaId",
					"logId", "hclusterId", "id", "deleted", "createUser" }) })
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResultObject createGce(
			@Valid @ModelAttribute EcGce gce, BindingResult bindResult,
			@ModelAttribute EcGceExt gceExt, ResultObject callbackResult) {// bindResult必须紧跟valid的gce，这样SpringMVC才会对gce的validate结果传给bindResult
		logger.debug("创建GCE");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}

		gce.setCreateUser(this.sessionService.getSession().getUserId());
		if (gceExt != null && (gceExt.getOcsId().longValue() != 0L)
				&& (gceExt.getRdsId().longValue() != 0L)) {
			gceExt.setCreateUser(this.sessionService.getSession().getUserId());
		}
		// TODO 未指定地域
		gce.setAreaId(7L);
		// TODO 致新暂用方法，直接从所有GCE集群中找一个集群用
		HclusterModel hclusterModel = new HclusterModel();
		hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
		hclusterModel.setType("GCE");
		List<HclusterModel> hcModels = hclusterService
				.selectHclusterByStatus(hclusterModel);
		if(CollectionUtils.isEmpty(hcModels))
			throw new ValidateException("无可用集群");
		gce.setHclusterId(hcModels.get(0).getId());
		gceProxy.createGce(gce, gceExt);
		callbackResult.setData(gce);
		logger.debug("创建GCE成功! ID:{},Name:{}", gce.getId(), gce.getGceName());
		return callbackResult;
	}

	@JsonFilterProperties(excluses = {
			@ExcludeProperty(pojo = ResultObject.class, names = { "callback" }),
			@ExcludeProperty(pojo = EcGcePackage.class, names = { "key",
					"status", "bucketName", "gceId", "gceclusterId", "id",
					"deleted", "createUser", "updateUser" }) })
	@RequestMapping(value = "/uploadPackage", method = RequestMethod.POST)
	public @ResponseBody ResultObject uploadPackage(
			@RequestParam MultipartFile file, @Valid EcGcePackage gcePackage,
			BindingResult bindResult, ResultObject callbackResult) {
		logger.debug("上传应用部署包");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}
		gcePackage.setCreateUser(this.sessionService.getSession().getUserId());
		gceProxy.uploadPackage(file, gcePackage);
		callbackResult.setData(gcePackage);
		logger.debug("上传GCE应用部署包成功! GCE名称:{},版本号:{}", gcePackage.getGceName(),
				gcePackage.getVersion());
		return callbackResult;
	}

	@JsonFilterProperties(excluses = {
			@ExcludeProperty(pojo = ResultObject.class, names = { "callback" }),
			@ExcludeProperty(pojo = EcGceContainer.class, names = {
					"status", "gcePackageClusterId", "containerName",
					"mountDir", "hostIp"/* 所属物理机IP */, "gceId", "hostId",
					"gcePackageId", "id", "deleted", "createUser", "createTime" }) })
	@RequestMapping(value = "/containers", method = RequestMethod.GET)
	public @ResponseBody ResultObject getContainers(
			@Valid EcGcePackage gcePackage, BindingResult bindResult,
			ResultObject callbackResult) {
		logger.debug("获取GCE的版本容器信息");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}
		gcePackage.setCreateUser(this.sessionService.getSession().getUserId());
		List<EcGceContainer> containers = gceProxy
				.getGcepackageContainers(gcePackage);
		callbackResult.setData(containers);
		logger.debug("获取GCE应用部署包容器列表成功! GCE名称:{},版本号:{}",
				gcePackage.getGceName(), gcePackage.getVersion());
		return callbackResult;
	}

}
