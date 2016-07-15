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
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.GceImageStatus;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceImage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.elasticcalc.gce.IEcGceImageService;

/**
 * 购买GCE：创建镜像
 * 
 * @author linzhanbo .
 * @since 2016年6月29日, 下午1:51:29 .
 * @version 1.0 .
 */
@Service("taskEcGceMirrorCreateService")
public class TaskEcGceMirrorCreateService extends BaseTaskEcGceServiceImpl
		implements IBaseTaskService {
	private final static Logger logger = LoggerFactory
			.getLogger(TaskEcGceMirrorCreateService.class);
	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IEcGceImageService ecGceImageService;
	@Value("${matrix.gce.awss3.endpoint}")
	private String AWSS3ENDPOINT;
	@Value("${matrix.gce.awss3.accessKey}")
	private String AWSS3ACCESSKEY;
	@Value("${matrix.gce.awss3.secretKey}")
	private String AWSS3SECRETKEY;
	@Value("${matrix.gce.mirror.build.server.ip}")
	private String MIRRORBUILDSERVERIP;
	@Value("${matrix.gce.mirror.build.server.port}")
	private String MIRRORBUILDSERVERPORT;
	@Value("${matrix.gce.mirror.docker.addr.prefix}")
	private String MIRRORDOCKERADDRPREFIX;

	/**
	 * 创建镜像
	 */
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("创建应用镜像");
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;
		EcGcePackage gcePackage = super.getGcePackage(params);
		EcGce gce = super.getGce(params);

		// 请求参数
		Map<String, String> props = new HashMap<String, String>();

		props.put("owner", String.valueOf(gcePackage.getCreateUser()));
		props.put("app_type", gce.getType());
		props.put("app_name", gce.getGceName());
		props.put(
				"appfile_name",
				gce.getGceName() + "-" + gcePackage.getVersion()
						+ gcePackage.getSuffix());
		props.put("awss3endpoint", AWSS3ENDPOINT);
		props.put("accessKey", AWSS3ACCESSKEY);
		props.put("secretKey", AWSS3SECRETKEY);
		props.put("appfile_s3_bucket", gcePackage.getBucketName());
		props.put("appfile_s3_key", gcePackage.getKey());
		// 生成docker地址给python，省去python拼接生成，同时方便第二个环节取校验创建镜像状态。
		StringBuffer distMirrorUrl = new StringBuffer();
		// dockerapp.et.letv.com/matrix/200.10.100
		distMirrorUrl.append(MIRRORDOCKERADDRPREFIX)
				.append(gcePackage.getCreateUser()).append(".")
				.append(gce.getId()).append(".").append(gcePackage.getId());
		props.put("repo_name", distMirrorUrl.toString());
		props.put("app_version", gcePackage.getVersion());

		String serverName = (String) params.get("serviceName");
		logger.debug("请求Python服务创建[" + serverName + "]应用镜像，请求参数: "+props.toString());
		ApiResultObject resultObject = this.gcePythonService
				.createGCEPackageMirror(props, MIRRORBUILDSERVERIP,
						MIRRORBUILDSERVERPORT);

		tr = analyzeRestServiceResult(resultObject);
		if (tr.isSuccess()) {
			logger.debug("请求创建成功");
			EcGceImage image = new EcGceImage();
			image.setName(serverName);
			image.setUrl(distMirrorUrl.toString());
			image.setOwner(gcePackage.getCreateUser());
			image.setNetType("ip");//TODO 沟通过，只有IP
			image.setGceId(gce.getId());
			image.setGcePackageId(gcePackage.getId());
			image.setStatus(GceImageStatus.NOTAVAILABLE.getValue());
			image.setCreateUser(gcePackage.getCreateUser());
			this.ecGceImageService.insert(image);
			params.put("gceImageId", image.getId());
		}
		tr.setParams(params);
		return tr;
	}
}
