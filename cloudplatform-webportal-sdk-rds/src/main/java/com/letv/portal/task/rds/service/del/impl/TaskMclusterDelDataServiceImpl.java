package com.letv.portal.task.rds.service.del.impl;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.image.Image;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.image.IImageService;
import com.letv.portal.task.rds.service.impl.BaseTask4RDSServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taskMclusterDelDataService")
public class TaskMclusterDelDataServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{
	
	@Autowired
	private IPythonService pythonService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IMclusterService mclusterService;

	private final static Logger logger = LoggerFactory.getLogger(TaskMclusterDelDataServiceImpl.class);

	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
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
		HostModel host = this.hostService.getHostByHclusterId(mclusterModel.getHclusterId());
		if(host == null || mclusterModel.getHclusterId() == null)
			throw new ValidateException("host is null by hclusterIdId:" + mclusterModel.getHclusterId());
		
		//从数据库获取image
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName", mclusterModel.getMclusterName());
		map.put("containerNameList", (String) params.get("delName"));
		ApiResultObject result = this.pythonService.delContainerOnMcluster(map, host.getHostIp(), host.getName(), host.getPassword());
		tr = analyzeRestServiceResult(result);
		if(tr.isSuccess())
		tr.setParams(params);
		return tr;
	}
	@Override
	public void callBack(TaskResult tr) {
//		super.callBack(tr);
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
