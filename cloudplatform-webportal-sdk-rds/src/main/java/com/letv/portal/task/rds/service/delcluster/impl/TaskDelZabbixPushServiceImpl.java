package com.letv.portal.task.rds.service.delcluster.impl;

import java.util.List;
import java.util.Map;

import org.elasticsearch.common.lang3.StringUtils;
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
import com.letv.portal.zabbixPush.IZabbixPushService;

@Service("taskDelZabbixPushService")
public class TaskDelZabbixPushServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService{

	@Autowired
	private IContainerService containerService;
	@Autowired
    private IZabbixPushService zabbixPushService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskDelZabbixPushServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.validator(params);
		if(!tr.isSuccess())
			return tr;
		
		Long mclusterId = getLongFromObject(params.get("mclusterId"));
		if(mclusterId == null)
			throw new ValidateException("params's mclusterId is null");

		MclusterModel mcluster = super.getMcluster(params);
		List<ContainerModel> containers = this.containerService.selectVipByClusterId(mclusterId);
		if(null != containers && containers.size()==1 && 
				StringUtils.isNotEmpty(containers.get(0).getZabbixHosts())) {
			 ApiResultObject apiResult = this.zabbixPushService.deleteMutilContainerPushZabbixInfo(containers);
			 tr.setResult(apiResult.getResult());
			 tr.setSuccess(apiResult.getAnalyzeResult());
		} else {
			tr.setSuccess(true);
			tr.setResult("该集群未成功进行zabbix推送，跳过该步骤!");
		}
        
		if (tr.isSuccess()) {
			logger.debug("RDS集群删除-删除zabbix成功{}", mcluster.getMclusterName());
		}
		tr.setParams(params);
		return tr;
	}
	
}
