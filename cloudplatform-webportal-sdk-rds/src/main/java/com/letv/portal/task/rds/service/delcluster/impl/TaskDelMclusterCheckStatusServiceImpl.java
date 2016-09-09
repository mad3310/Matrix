package com.letv.portal.task.rds.service.delcluster.impl;

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

@Service("taskMclusterCheckDelDataStatusService")
public class TaskDelMclusterCheckStatusServiceImpl extends BaseTask4RDSDelServiceImpl implements IBaseTaskService {

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
	
	private final static Logger logger = LoggerFactory.getLogger(TaskDelMclusterCheckStatusServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.validator(params);
		if(!tr.isSuccess()) {
			return tr;
		}
		final MclusterModel mclusterModel = super.getMcluster(params);
		final HostModel host = super.getHost(mclusterModel.getHclusterId());
		
		IRetry<Object, Boolean> iRetry = new IRetry<Object, Boolean>() {
			@Override
			public Object execute() {
				return pythonService.checkMclusterStatus(mclusterModel.getMclusterName(),host.getHostIp(),host.getName(),host.getPassword());
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
			tr.setSuccess(false);
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
		
		boolean isSucess = Constant.PYTHON_API_RESPONSE_JUDGE.equals(String.valueOf(meta.get("code")))
				&& result.getResult().contains("not existed");
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
