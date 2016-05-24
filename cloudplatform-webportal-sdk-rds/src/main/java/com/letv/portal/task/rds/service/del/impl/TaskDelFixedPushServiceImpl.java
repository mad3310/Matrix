package com.letv.portal.task.rds.service.del.impl;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.task.rds.service.impl.BaseTask4RDSServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("taskDelFixedPushService")
public class TaskDelFixedPushServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{

	@Autowired
	private IContainerService containerService;
	@Autowired
	private IFixedPushService fixedPushService;
	@Autowired
	private IMclusterService mclusterService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskDelFixedPushServiceImpl.class);
	
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

		String namesstr = (String)params.get("delName");
		List<ContainerModel> containers = new ArrayList<ContainerModel>();
		containers.add(this.containerService.selectByName(namesstr));

		if(containers.isEmpty())
			throw new ValidateException("containers is empty by name:" + namesstr);
		
		ApiResultObject apiResult = fixedPushService.deleteMutilContainerPushFixedInfo(containers);
		if(!apiResult.getAnalyzeResult()) {
			//发送推送失败邮件，流程继续。
			buildResultToMgr("RDS服务相关系统推送异常", mclusterModel.getMclusterName() +"集群固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
			tr.setResult(apiResult.getResult());
		} else {
			for(ContainerModel containerModel:containers) {
				this.containerService.delete(containerModel);
			}
		}

		tr.setSuccess(true);
		tr.setParams(params);
		return tr;
	}
    @Override
    public void callBack(TaskResult tr) {
    	Long mclusterId = getLongFromObject(((Map<String, Object>) tr.getParams()).get("mclusterId"));
		MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
		mcluster.setStatus(MclusterStatus.RUNNING.getValue());
		this.mclusterService.updateBySelective(mcluster);
    }

    @Override
    public void rollBack(TaskResult tr) {
//		super.rollBack(tr);
    }
	
}
