package com.letv.portal.task.gce.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageCluster;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageImage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;

/**
 * 购买GCE：创建集群
 * 
 * @author linzhanbo .
 * @since 2016年6月30日, 下午1:03:38 .
 * @version 1.0 .
 */
@Service("taskEcGceClusterCreateService")
public class TaskEcGceClusterCreateServiceImpl extends BaseTaskEcGceServiceImpl
		implements IBaseTaskService {
	private final static Logger logger = LoggerFactory
			.getLogger(TaskEcGceClusterCreateServiceImpl.class);

	@Autowired
	private IGcePythonService gcePythonService;

	@Value("matrix.gce.container.memory.default.size")
	private String CONTAINER_MEMORY_DEFAULT_SIZE;

	/**
	 * 创建集群
	 */
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		logger.debug("创建集群");
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;
		EcGce gce = super.getGce(params);
		EcGcePackage gcePackage = super.getGcePackage(params);
		EcGcePackageImage image = super.getGcePackageImage(params);
		EcGcePackageCluster cluster = super.getGcePackageCluster(params);
		HostModel host = super.getHost(cluster.getHclusterId());
		Map<String, String> props = new HashMap<String, String>();
		props.put("containerClusterName", cluster.getClusterName());
		props.put("componentType", gce.getType());
		props.put("image", gcePackage.getGceImageName());
		props.put("networkMode", image.getNetType());
		props.put(
				"memory",
				gce.getMemorySize() != null ? String.valueOf(gce
						.getMemorySize()) : CONTAINER_MEMORY_DEFAULT_SIZE);
		props.put("nodeCount", String.valueOf(gce.getInstanceNum()));
		String serverName = (String) params.get("serviceName");
		logger.debug("请求Python服务创建[" + serverName + "]应用容器，请求参数: "
				+ props.toString());
		ApiResultObject resultObject = this.gcePythonService.createContainer(
				props, host.getHostIp(), host.getName(), host.getPassword());
		// ApiResultObject resultObject =
		// this.gcePythonService.createContainer(props,"10.154.156.129",host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(resultObject);
		if (tr.isSuccess()) {
			logger.debug("请求创建集群成功");
		}
		tr.setParams(params);
		return tr;
	}

}
