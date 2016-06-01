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
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;

@Service("taskMclusterStartGlb8888Service")
public class TaskMclusterStartGlb8888ServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{

	@Autowired
	private IPythonService pythonService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IMclusterService mclusterService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskMclusterStartGlb8888ServiceImpl.class);
	
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
		
		List<ContainerModel> containers = this.containerService.selectByMclusterId(mclusterId);
		if(containers.isEmpty())
			throw new ValidateException("containers is empty by mclusterId:" + mclusterId);
		String nodeIp1 = containers.get(0).getIpAddr();
		String nodeIp2 = containers.get(1).getIpAddr();
		String nodeIp3 = containers.get(2).getIpAddr();
		String vipNodeIp = containers.get(3).getIpAddr();
		String username = mclusterModel.getAdminUser();
		String password = mclusterModel.getAdminPassword();
		
		StringBuffer ipListPort = new StringBuffer();
		ipListPort.append(nodeIp1).append(":8888,").append(nodeIp2).append(":8888,").append(nodeIp3).append(":8888");
		ApiResultObject result = this.pythonService.startGbalancer(vipNodeIp, "monitor", mclusterModel.getSstPwd(),"http", ipListPort.toString(), "8888", "-daemon", username, password);
		tr = analyzeRestServiceResult(result);
		
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void callBack(TaskResult tr) {
		super.rollBack(tr);
	}
	
}
