/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.del.impl;

import java.text.MessageFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;

/**
 * 删除GCE：检查集群删除状态
 * @author linzhanbo .
 * @since 2016年9月9日, 下午4:34:08 .
 * @version 1.0 .
 */
@Service("taskEcGceClusterDeleteCheckStatusService")
public class TaskEcGceClusterDeleteCheckStatusService extends
		BaseTaskEcGceDeleteServiceImpl implements IBaseTaskService {
	private final static Logger logger = LoggerFactory.getLogger(TaskEcGceClusterDeleteCheckStatusService.class);
	@Autowired
	private IGcePythonService gcePythonService;
	@Value("${matrix.gce.cluster.delete.check.interval}")
	private long checkInterval;// 间隔checkInterval s检查一次
	@Value("${matrix.gce.cluster.delete.check.timeout}")
	private long checkTimeout;// checkTimeout s超时,超时后停止检查
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;
		String serverName = (String) params.get("serviceName");
		logger.debug("检查集群[" + serverName + "]删除状态");
		Assert.isTrue(checkTimeout > checkInterval, MessageFormat.format(
				"检查镜像状态时定时检查配置的参数非法: [定时时间{0}ms,间隔时间{1}ms]不符合检查条件",
				checkTimeout, checkInterval));
		EcGceCluster gceCluster = super.getGceCluster(params);
		HostModel host = super.getHost(gceCluster.getHclusterId());
		tr = super.polling(tr, checkInterval, checkTimeout,serverName,gceCluster,host);
		if (tr.isSuccess()) {
			logger.debug("删除集群成功");
		}
		tr.setParams(params);
		return tr;
	}
	@Override
	public TaskResult pollingTask(Object... params) {
		//从调用polling时候的赋值中获取
		String serverName = (String) params[0];
		EcGceCluster gceCluster = (EcGceCluster) params[1];
		HostModel host = (HostModel) params[2];
		logger.debug(System.currentTimeMillis()+" 检查集群[" + serverName + "]创建状态");
		ApiResultObject resultObject = gcePythonService.checkClusterDeleteStatus(gceCluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		return analyzeRestServiceResult(resultObject);
	}
}
