package com.letv.portal.task.rds.service.add.impl;

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

@Service("taskAddFixedPushService")
public class TaskAddFixedPushServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{

	@Autowired
	private IContainerService containerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IFixedPushService fixedPushService;
	@Autowired
	private IMclusterService mclusterService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskAddFixedPushServiceImpl.class);
	
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

		String namesstr = (String)params.get("addNames");
		String[] addNames = namesstr.split(",");
		List<ContainerModel> containers = new ArrayList<ContainerModel>();
		for (String addName:addNames) {
			containers.add(this.containerService.selectByName(addName));
		}

		if(containers.isEmpty())
			throw new ValidateException("containers is empty by name:" + namesstr);
		
		ApiResultObject apiResult = fixedPushService.createMutilContainerPushFixedInfo(containers);
		tr.setResult(apiResult.getResult());
		if(!apiResult.getAnalyzeResult()) {
			//发送推送失败邮件，流程继续。
			buildResultToMgr("RDS服务相关系统推送异常", mclusterModel.getMclusterName() +"集群固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
		}
		
		tr.setSuccess(apiResult.getAnalyzeResult());
		tr.setParams(params);
		return tr;
	}
    @Override
    public void callBack(TaskResult tr) {
    	Map<String, Object> params = (Map<String, Object>) tr.getParams();
    	String namesstr  =  (String) params.get("addNames");
        String[] containerNames = namesstr.split(",");
        changeContainerStatusByNames(containerNames, MclusterStatus.RUNNING);
        
        Long mclusterId = getLongFromObject(params.get("mclusterId"));
        changeMclusterStatus(mclusterId, MclusterStatus.RUNNING);
    }

    @Override
    public void rollBack(TaskResult tr) {
    	Map<String, Object> params = (Map<String, Object>) tr.getParams();
    	String namesstr  =  (String) params.get("addNames");
        String[] containerNames = namesstr.split(",");
        changeContainerStatusByNames(containerNames, MclusterStatus.ADDINGFAILED);
        
        Long mclusterId = getLongFromObject(params.get("mclusterId"));
        changeMclusterStatus(mclusterId, MclusterStatus.ADDINGFAILED);
    }
    
    /**
     * 修改节点状态
     * @param containerNames 集群名称
     * @param status 状态
     */
    private void changeContainerStatusByNames(String[] containerNames, MclusterStatus status) {
    	for (String name : containerNames) {
            ContainerModel containerModel = this.containerService.selectByName(name);
            containerModel.setStatus(status.getValue());
            this.containerService.updateBySelective(containerModel);
        }
    }
    
    /**
     * 修改集群状态
     * @param mclusterId 集群id
     * @param status 状态
     */
    private void changeMclusterStatus(Long mclusterId, MclusterStatus status) {
    	MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
        mcluster.setStatus(status.getValue());
        this.mclusterService.updateBySelective(mcluster);
    }
	
}
