package com.letv.portal.task.rds.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.zabbixPush.IZabbixPushService;

@Service("taskZabbixPushService")
public class TaskZabbixPushServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{

	@Autowired
	private IContainerService containerService;
	@Autowired
	private IZabbixPushService zabbixPushService;
	@Autowired
	private IMclusterService mclusterService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskZabbixPushServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		Long mclusterId = getLongFromObject(params.get("mclusterId"));
		if(mclusterId == null)
			throw new ValidateException("params's mclusterId is null");
		//执行业务
		MclusterModel mclusterModel = this.mclusterService.selectById(mclusterId);
		if(mclusterModel == null)
			throw new ValidateException("mclusterModel is null by mclusterId:" + mclusterId);
		
		List<ContainerModel> containers = this.containerService.selectVipByClusterId(mclusterId);
		if(containers.isEmpty())
			throw new ValidateException("containers is empty by mclusterId:" + mclusterId);
		
		ApiResultObject apiResult = zabbixPushService.createMultiContainerPushZabbixInfo(containers);
		tr.setResult(apiResult.getResult());
		if(!apiResult.getAnalyzeResult()) {
			//发送推送失败邮件，流程继续。
			buildResultToMgr("RDS服务相关系统推送异常", mclusterModel.getAdminPassword() +"集群zabbix系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
		}
		
		tr.setSuccess(apiResult.getAnalyzeResult());
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void rollBack(TaskResult tr) {
	}
	
	
}
