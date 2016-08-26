package com.letv.portal.task.es.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;

@Service("taskEsFixedPushService")
public class TaskEsFixedPushServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{

    @Autowired
    private IFixedPushService fixedPushService;

	private final static Logger logger = LoggerFactory.getLogger(TaskEsFixedPushServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		//执行业务
		List<EsContainer> containers = super.getContainers(params);
		
		ApiResultObject apiResult = new ApiResultObject();
		apiResult.setAnalyzeResult(false);
		for (EsContainer container:containers) {
        	apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
            if(!apiResult.getAnalyzeResult()) {
                //发送推送失败邮件，流程继续。
                buildResultToMgr("Es服务相关系统推送异常", super.getEsCluster(params).getClusterName() +"集群固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                break;
            }
		}
        tr.setSuccess(apiResult.getAnalyzeResult());
        tr.setResult(apiResult.getResult());
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void callBack(TaskResult tr) {
	}
	
}
