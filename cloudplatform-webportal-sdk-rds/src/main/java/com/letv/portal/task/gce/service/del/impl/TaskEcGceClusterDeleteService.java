/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.del.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;

/**
 * 删除GCE：删除GCE集群
 * @author linzhanbo .
 * @since 2016年9月9日, 下午4:17:51 .
 * @version 1.0 .
 */
@Service("taskEcGceClusterDeleteService")
public class TaskEcGceClusterDeleteService extends
		BaseTaskEcGceDeleteServiceImpl implements IBaseTaskService {
	private final static Logger logger = LoggerFactory.getLogger(TaskEcGceClusterDeleteService.class);
	@Autowired
	private IGcePythonService gcePythonService;
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		String serverName = (String) params.get("serviceName");
		logger.debug("请求删除GCE集群 " + serverName);
		EcGceCluster cluster = super.getGceCluster(params);
		HostModel host = super.getHost(cluster.getHclusterId());
		ApiResultObject result = this.gcePythonService.deleteCluster(cluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(result);
		if (tr.isSuccess()) {
			logger.debug("请求删除GCE集群成功");
		}
		//TODO 已经不存在处理When api url is http://10.154.156.129:8888/containerCluster?containerClusterName=23_2_dwl_01_1.1.1.1, The data on failure. The error message is as follows:{"meta": {"code": 417, "errorType": "user_visible_error", "errorDetail": "'containerCluster 23_2_dwl_01_1.1.1.1 not existed, no need to remove'"}}
		tr.setParams(params);
		return tr;
	}
}
