package com.letv.portal.task.gce.service.impl;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("taskGceAddFixedPushService")
public class TaskGceAddFixedPushServiceImpl extends BaseTask4GceServiceImpl implements IBaseTaskService{

    @Autowired
    private IFixedPushService fixedPushService;

	private final static Logger logger = LoggerFactory.getLogger(TaskGceAddFixedPushServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		//执行业务
		List<GceContainer> containers = super.getContainers(params);
		ApiResultObject apiResult;
		for (GceContainer container:containers) {
            if(container.getIpAddr().startsWith("10.")) {
            	apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
                if(!apiResult.getAnalyzeResult()) {
                    //发送推送失败邮件，流程继续。
                    buildResultToMgr("GCE服务相关系统推送异常", super.getGceCluster(params).getClusterName() +"集群固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                    tr.setResult(apiResult.getResult());
                    break;
                }
            }
		}
        tr.setSuccess(true);
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void callBack(TaskResult tr) {
	}
	
}
