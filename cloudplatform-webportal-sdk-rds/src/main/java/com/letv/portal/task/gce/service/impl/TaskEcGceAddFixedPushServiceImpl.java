/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;

/**
 * 购买GCE：同步固资系统
 * @author linzhanbo .
 * @since 2016年8月15日, 下午2:23:26 .
 * @version 1.0 .
 */
@Service("taskEcGceAddFixedPushService")
public class TaskEcGceAddFixedPushServiceImpl extends BaseTaskEcGceServiceImpl
		implements IBaseTaskService {
	private final static Logger logger = LoggerFactory
			.getLogger(TaskEcGceAddFixedPushServiceImpl.class);
	

	@Autowired
	private IEcGceContainerService ecGceContainerService;
    @Autowired
    private IFixedPushService fixedPushService;
	
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		String serverName = (String) params.get("serviceName");
		logger.debug("同步集群[" + serverName + "]到固资系统");
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;
		EcGceCluster gceCluster = super.getGceCluster(params);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("gceId", gceCluster.getGceId());
		map.put("gcePackageId", gceCluster.getGcePackageId());
		List<EcGceContainer> containers = this.ecGceContainerService.selectByMap(map);
		for(EcGceContainer container:containers){
			ApiResultObject apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
            if(!apiResult.getAnalyzeResult()) {
                //发送推送失败邮件，流程继续。
                buildResultToMgr("GCE服务相关系统推送异常", container.getContainerName() +"节点固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                tr.setSuccess(apiResult.getAnalyzeResult());
                tr.setResult(apiResult.getResult());
                break;
            }
		}
		tr.setParams(params);
		return tr;
	}
	@Override
	public void rollBack(TaskResult tr) {
	}
}
