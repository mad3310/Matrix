/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.GcePackageImageStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageImage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.elasticcalc.gce.IGcePackageImageService;
import com.letv.portal.service.elasticcalc.gce.IGcePackageService;

/**
 * 购买GCE：检查镜像创建状态
 * 
 * @author linzhanbo .
 * @since 2016年6月30日, 上午9:46:17 .
 * @version 1.0 .
 */
@Component("taskGceMirrorCheckStatusService")
public class TaskGceMirrorCheckStatusService extends BaseTask4GceServiceImpl
		implements IBaseTaskService {
	private final static Logger logger = LoggerFactory
			.getLogger(TaskGceMirrorCheckStatusService.class);

	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IGcePackageImageService gcePackageImageService;
	@Autowired
	private IGcePackageService gcePackageService;
	@Value("${matrix.gce.mirror.build.server.ip}")
	private String MIRRORBUILDSERVERIP;
	@Value("${matrix.gce.mirror.build.server.port}")
	private String MIRRORBUILDSERVERPORT;
	@Value("${matrix.gce.mirror.build.check.interval}")
	private long checkInterval;// 间隔checkInterval s检查一次
	@Value("${matrix.gce.mirror.build.check.timeout}")
	private long checkTimeout;// checkTimeout s超时,超时后停止检查

	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		String serverName = (String) params.get("serviceName");
		logger.debug("检查镜像[" + serverName + "]创建状态");
		Assert.isTrue(checkTimeout > checkInterval, "检查镜像状态时定时检查配置的参数非法: [定时时间"
				+ checkTimeout + "ms,间隔时间" + checkInterval + "ms]不符合检查条件");
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;

		EcGcePackage gcePackage = super.getGcePackage(params);
//		EcGce gce = super.getGce(params);
		/*
		 * EcGcePackageCluster gcePackageCluster = super
		 * .getGcePackageCluster(params);
		 */
//		HostModel host = super.getHost(gce.getHclusterId());
		EcGcePackageImage image = super.getGcePackageImage(params);
		// 返回结果
		ApiResultObject resultObject = null;
		// 请求参数
		Map<String, String> props = new HashMap<String, String>();
		props.put("repo_name", image.getUrl());
		props.put("app_version", gcePackage.getVersion());

		long beginTime = System.currentTimeMillis();
		tr.setSuccess(false);
		while (!tr.isSuccess()) {
			// 循环的第一次肯定不会进该if，因为代码开头Assert.isTrue过，所以resultObject.url必然在轮训后有值
			if (System.currentTimeMillis() - beginTime > checkTimeout) {
				tr.setSuccess(false);
				tr.setResult("check time over:" + resultObject.getUrl());
				break;
			} else {
				resultObject = gcePythonService
						.checkGCEPackageMirrorCreateStatus(props,
								MIRRORBUILDSERVERIP, MIRRORBUILDSERVERPORT);
				tr = analyzeRestServiceResult(resultObject);
			}
			Thread.sleep(checkInterval);
		}
		if (tr.isSuccess()) {
			// 当镜像可用的时候，往应用包的镜像名称字段添加镜像URL
			image.setStatus(GcePackageImageStatus.AVAILABLE.getValue());
			image.setUpdateUser(image.getCreateUser());
			gcePackageImageService.update(image);
			gcePackage.setGceImageName(image.getUrl());
			gcePackage.setUpdateUser(gcePackage.getCreateUser());
			gcePackageService.updateBySelective(gcePackage);
		}
		tr.setParams(params);
		return tr;
	}
}
