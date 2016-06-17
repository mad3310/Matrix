package com.letv.portal.task.rds.service.add.impl;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.task.rds.service.impl.BaseTask4RDSServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

@Service("taskMclusterAddStartService")
public class TaskMclusterStartServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{

	@Autowired
	private IPythonService pythonService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IMclusterService mclusterService;

    @Value("${python_rds_add_check_time}")
    private long PYTHON_RDS_ADD_CHECK_TIME;
    @Value("${python_rds_add_interval_time}")
    private long PYTHON_RDS_ADD_INTERVAL_TIME;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskMclusterStartServiceImpl.class);
	
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

		String username = mclusterModel.getAdminUser();
		String password = mclusterModel.getAdminPassword();
        boolean startFlag = true;
        boolean checkFlag = true;
        ContainerModel vip = this.containerService.selectValidVipContianer(mclusterId, "mclustervip", null);

        for(ContainerModel container:containers) {
            String ipAddr = container.getIpAddr();
            Long start = new Date().getTime();
            while(startFlag) {
                ApiResultObject result = this.pythonService.startNode(ipAddr, username, password);
                if(new Date().getTime()-start >PYTHON_RDS_ADD_CHECK_TIME) {
                    tr.setSuccess(false);
                    tr.setResult("check time over:" + result.getUrl());
                    startFlag = false;
                    continue;
                }
                if(result.getResult().contains("\"code\":417")) {
                    Thread.sleep(PYTHON_RDS_ADD_INTERVAL_TIME);
                    startFlag = true;
                    continue;
                }
                tr = analyzeRestServiceResult(result);
                startFlag = false;
            }
            if(!tr.isSuccess()) {
                break;
            }
            startFlag = true;
            start = new Date().getTime();
            while(checkFlag) {
                ApiResultObject result = this.pythonService.checkContainerStatus(vip.getIpAddr(), mclusterModel.getAdminUser(), mclusterModel.getAdminPassword());
                if(new Date().getTime()-start >PYTHON_RDS_ADD_CHECK_TIME) {
                    tr.setSuccess(false);
                    tr.setResult("check time over:" + result.getUrl());
                    startFlag = false;
                    break;
                }
                if(null != result.getResult() && result.getResult().contains(container.getIpAddr())){
                    tr.setSuccess(true);
                    checkFlag = false;
                }
                Thread.sleep(PYTHON_RDS_ADD_INTERVAL_TIME);
            }
            checkFlag = true;
		}
		tr.setParams(params);
		return tr;
	}
	@Override
	public void callBack(TaskResult tr) {
	}

    @Override
    public void rollBack(TaskResult tr) {
        String namesstr  =  (String) ((Map<String, Object>) tr.getParams()).get("addNames");
        String[] addNames = namesstr.split(",");
        for (String addName:addNames) {
            ContainerModel containerModel = this.containerService.selectByName(addName);
            if(MclusterStatus.ADDING.getValue() == containerModel.getStatus()) {
                containerModel.setStatus(MclusterStatus.ADDINGFAILED.getValue());
                this.containerService.updateBySelective(containerModel);
            }
        }
        Long mclusterId = getLongFromObject(((Map<String, Object>) tr.getParams()).get("mclusterId"));
        MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
        mcluster.setStatus(MclusterStatus.ADDINGFAILED.getValue());
        this.mclusterService.updateBySelective(mcluster);
//		super.rollBack(tr);
    }
}
