/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.GceImageStatus;
import com.letv.portal.model.elasticcalc.gce.EcGceImage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.elasticcalc.gce.IEcGceImageService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;

/**
 * 购买GCE：检查镜像创建状态
 * 
 * @author linzhanbo .
 * @since 2016年6月30日, 上午9:46:17 .
 * @version 1.0 .
 */
@Component("taskEcGceMirrorCheckStatusService")
public class TaskEcGceMirrorCheckStatusService extends BaseTaskEcGceServiceImpl
		implements IBaseTaskService {
	private final static Logger logger = LoggerFactory
			.getLogger(TaskEcGceMirrorCheckStatusService.class);

	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IEcGceImageService ecGceImageService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;
	@Value("${matrix.gce.mirror.build.server.ip}")
	private String MIRRORBUILDSERVERIP;
	@Value("${matrix.gce.mirror.build.server.port}")
	private String MIRRORBUILDSERVERPORT;
	@Value("${matrix.gce.mirror.build.check.interval}")
	private long checkInterval;// 间隔checkInterval s检查一次
	@Value("${matrix.gce.mirror.build.check.timeout}")
	private long checkTimeout;// checkTimeout s超时,超时后停止检查

	/**
	 * 检查镜像创建状态
	 */
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		String serverName = (String) params.get("serviceName");
		logger.debug("检查镜像[" + serverName + "]创建状态");
		Assert.isTrue(checkTimeout > checkInterval, MessageFormat.format(
				"检查镜像状态时定时检查配置的参数非法: [定时时间{0}ms,间隔时间{1}ms]不符合检查条件",
				checkTimeout, checkInterval));
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;

		EcGcePackage gcePackage = super.getGcePackage(params);
		EcGceImage image = super.getGceImage(params);
		// 请求参数
		Map<String, String> props = new HashMap<String, String>();
		props.put("repo_name", image.getUrl());
		props.put("app_version", gcePackage.getVersion());
		tr = super.polling(tr,checkInterval,checkTimeout,serverName,props);
		if (tr.isSuccess()) {
			logger.debug("镜像 " + image.getUrl() + " 创建成功");
			// 当镜像可用的时候，往应用包的镜像名称字段添加镜像URL
			image.setStatus(GceImageStatus.AVAILABLE.getValue());
			image.setUpdateUser(image.getCreateUser());
			ecGceImageService.update(image);
			gcePackage.setGceImageName(image.getUrl() + ":"
					+ gcePackage.getVersion());// 镜像URL：repo_name:1.10.10.10
			gcePackage.setUpdateUser(gcePackage.getCreateUser());
			ecGcePackageService.updateBySelective(gcePackage);
		}
		tr.setParams(params);
		return tr;
	}
	@Override
	public ApiResultObject pollingTask(Object... params) {
		//从调用polling时候的赋值中获取
		String serverName = (String) params[0];
		Map<String, String> props = (Map<String, String>) params[1];
		logger.debug(System.currentTimeMillis() + " 检查镜像[" + serverName
				+ "]创建状态");
		ApiResultObject  resultObject = gcePythonService
				.checkGCEPackageMirrorCreateStatus(props,
						MIRRORBUILDSERVERIP, MIRRORBUILDSERVERPORT);
		return resultObject;
	}
}
