/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.del.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;

/**
 * 删除GCE：同步固资系统
 * @author linzhanbo .
 * @since 2016年9月9日, 下午5:36:31 .
 * @version 1.0 .
 */
@Service("taskEcGceClusterDelFixedPushService")
public class TaskEcGceClusterDelFixedPushService extends
		BaseTaskEcGceDeleteServiceImpl implements IBaseTaskService {
	private final static Logger logger = LoggerFactory.getLogger(TaskEcGceClusterDelFixedPushService.class);
    @Autowired
    private IFixedPushService fixedPushService;
    @Autowired
	private IEcGceContainerService ecGceContainerService;
    
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;
		String serverName = (String) params.get("serviceName");
		logger.debug("请求删除固资上记录集群[" + serverName + "]数据");
        EcGceCluster gceCluster = super.getGceCluster(params);
        Map<String, Object> map = new HashMap<String, Object>();
		map.put("gceId", gceCluster.getGceId());
		map.put("gcePackageId", gceCluster.getGcePackageId());
        List<EcGceContainer> containers = this.ecGceContainerService.selectByMap(map);
		for(EcGceContainer container:containers){
			ApiResultObject apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"delete");
            if(!apiResult.getAnalyzeResult()){
            	tr.setSuccess(apiResult.getAnalyzeResult());
                tr.setResult(apiResult.getResult());
                //发送固资失败邮件
                buildResultToMgr("GCE服务相关系统推送异常", container.getContainerName() +"节点固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                break;
            }
		}
		tr.setParams(params);
		return tr;
	}
	/*@Override
	public ApiResultObject deleteMutilContainerPushFixedInfo(List<ContainerModel> containers){
		ApiResultObject ret = new ApiResultObject();
		ret.setAnalyzeResult(false);
		List<ContainerModel> success = new ArrayList<ContainerModel>();
		for(ContainerModel c:containers) {
			ret = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "delete");
            if(!ret.getAnalyzeResult()) {//删除失败
            	for (ContainerModel containerModel : success) {//把删除成功的再添加上去
            		sendFixedInfo(containerModel.getHostIp(), containerModel.getContainerName(), containerModel.getIpAddr(), "add");
				}
            	break;
            } else {
            	success.add(c);
            }
		}
		return ret;
	}*/
	@Override
	public void afterExecute(TaskResult tr) {
		super.finish(tr);
	}
}
