package com.letv.portal.task.rds.service.del.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.RetryUtil;
import com.letv.common.util.function.IRetry;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.task.rds.service.impl.BaseTask4RDSServiceImpl;

@Service("taskMclusterCheckDelDataStatusService")
public class TaskMclusterCheckDelDataStatusServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService {

	@Value("${python_create_check_time}")
	private long PYTHON_CREATE_CHECK_TIME;
	@Value("${python_check_interval_time}")
	private long PYTHON_CHECK_INTERVAL_TIME;
	
	@Autowired
	private IPythonService pythonService;
	
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IMclusterService mclusterService;
	
	@Autowired
	private IHostService hostService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskMclusterCheckDelDataStatusServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.validator(params);
		if(!tr.isSuccess()) {
			return tr;
		}
		Long mclusterId = getLongFromObject(params.get("mclusterId"));
		if(null == mclusterId) {
			throw new ValidateException("params's mclusterId is null");
		}
		//执行业务
		MclusterModel mclusterModel = this.mclusterService.selectById(mclusterId);
		if(null == mclusterModel) {
			throw new ValidateException("mclusterModel is null by mclusterId:" + mclusterId);
		}
		HostModel host = this.hostService.getHostByHclusterId(mclusterModel.getHclusterId());
		if(null == host || null == mclusterModel.getHclusterId()) {
			throw new ValidateException("host is null by hclusterIdId:" + mclusterModel.getHclusterId());
		}
		
		final Map<String, String> checkParams = new HashMap<String, String>();
		checkParams.put("mclusterDataName", mclusterModel.getMclusterName());
		checkParams.put("delName", (String) params.get("delName"));
		checkParams.put("hostIp", host.getHostIp());
		checkParams.put("name", host.getName());
		checkParams.put("password", host.getPassword());
		
		IRetry<Object, Boolean> iRetry = new IRetry<Object, Boolean>() {
			@Override
			public Object execute() {
				return pythonService.checkContainerDelStatus(checkParams);
			}
			
			@Override
			public Object analyzeResult(Object r) {
				return analyzeRestServiceResult((ApiResultObject) r);
			}
			
			@Override
			public Boolean judgeAnalyzeResult(Object o) {
				return ((TaskResult)o).isSuccess();
			}
		};
		
		Map<String, Object> obj = RetryUtil.retryByTime(iRetry, PYTHON_CREATE_CHECK_TIME, PYTHON_CHECK_INTERVAL_TIME);
		
		if((Boolean)obj.get("judgeAnalyzeResult")) {//分析结果正常
			tr = (TaskResult) obj.get("analyzeResult");
		} else {
			ApiResultObject result = (ApiResultObject) obj.get("executeResult");
			tr.setResult("check time over:"+result.getUrl());
		}
		tr.setParams(params);
		return tr;
	}
	
	
	@Override
	public TaskResult analyzeRestServiceResult(ApiResultObject result) {
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(result.getResult());
		if(null == map) {
			tr.setSuccess(false);
			tr.setResult("api connect failed:" + result.getUrl());
			return tr;
		}
		Map<String,Object> meta = (Map<String, Object>) map.get("meta");
		
		boolean isSucess = Constant.PYTHON_API_RESPONSE_JUDGE.equals(String.valueOf(meta.get("code")));
		if(isSucess) {
			tr.setResult("delete success");
		} else {
			tr.setResult((String) meta.get("errorType") +",the api url:" + result.getUrl());
		}
		tr.setSuccess(isSucess);
		return tr;
	}

	@Override
	public void rollBack(TaskResult tr) {
		Long mclusterId = getLongFromObject(((Map<String, Object>) tr.getParams()).get("mclusterId"));
		MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
		mcluster.setStatus(MclusterStatus.DELETINGFAILED.getValue());
		this.mclusterService.updateBySelective(mcluster);
	}
	
}
