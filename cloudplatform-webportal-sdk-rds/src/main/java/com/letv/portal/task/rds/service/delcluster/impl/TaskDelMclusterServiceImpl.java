package com.letv.portal.task.rds.service.delcluster.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.HostType;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;

@Service("taskDelContainerPostInfoService")
public class TaskDelMclusterServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService{

	@Autowired
	private IPythonService pythonService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IMclusterService mclusterService;

    @Value("${python_rds_add_check_time}")
    private long PYTHON_RDS_ADD_CHECK_TIME;
    @Value("${python_rds_add_interval_time}")
    private long PYTHON_RDS_ADD_INTERVAL_TIME;
	private final static Logger logger = LoggerFactory.getLogger(TaskDelMclusterServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		MclusterModel mclusterModel = super.getMcluster(params);
		
		HostModel host = super.getHost(mclusterModel.getHclusterId());
		
		ApiResultObject result = this.pythonService.removeMcluster(mclusterModel.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword());
		
		tr = analyzeRestServiceResult(result);
		
		if (tr.isSuccess()) {
			logger.debug("调用删除RDS集群数据节点成功{}", mclusterModel.getMclusterName());
		}
		tr.setParams(params);
		return tr;
	}
	
	
	@Override
	public void callBack(TaskResult tr) {
	}

	@Override
	public void rollBack(TaskResult tr) {
        String namesstr  =  (String) ((Map<String, Object>) tr.getParams()).get("delName");
        ContainerModel containerModel = this.containerService.selectByName(namesstr);
        if(MclusterStatus.ADDING.getValue() == containerModel.getStatus()) {
            containerModel.setStatus(MclusterStatus.DELETINGFAILED.getValue());
            this.containerService.updateBySelective(containerModel);
        }
        Long mclusterId = getLongFromObject(((Map<String, Object>) tr.getParams()).get("mclusterId"));
        MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
        mcluster.setStatus(MclusterStatus.DELETINGFAILED.getValue());
        this.mclusterService.updateBySelective(mcluster);
//		super.rollBack(tr);
	}
	
}
