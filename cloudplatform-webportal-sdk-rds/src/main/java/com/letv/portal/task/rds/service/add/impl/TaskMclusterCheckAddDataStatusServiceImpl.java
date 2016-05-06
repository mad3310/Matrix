package com.letv.portal.task.rds.service.add.impl;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
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
import com.letv.portal.task.rds.service.impl.BaseTask4RDSServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("taskMclusterCheckAddDataStatusService")
public class TaskMclusterCheckAddDataStatusServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{

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
	
	private final static Logger logger = LoggerFactory.getLogger(TaskMclusterCheckAddDataStatusServiceImpl.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		String mclusterDataName = mclusterModel.getMclusterName();
		String addNames = (String) params.get("addNames");
		ApiResultObject result = pythonService.checkContainerAddStatus(mclusterDataName,addNames.substring(0,addNames.length()-1), host.getHostIp(), host.getName(), host.getPassword());
		tr = analyzeRestServiceResult(result);
		
		Long start = new Date().getTime();
		while(!tr.isSuccess()) {
			Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
			if(new Date().getTime()-start >PYTHON_CREATE_CHECK_TIME) {
				tr.setResult("check time over:"+result.getUrl());
				break;
			}
			result = pythonService.checkContainerAddStatus(mclusterDataName,addNames.substring(0,addNames.length()-1), host.getHostIp(), host.getName(), host.getPassword());
			tr = analyzeRestServiceResult(result);
		}
		if(tr.isSuccess()) {
			List<Map> containers = (List<Map>)((Map)transToMap(result.getResult()).get("response")).get("containers");
			for (Map map : containers) {
				ContainerModel container = new ContainerModel();
				BeanUtils.populate(container, map);
				container.setMclusterId(mclusterModel.getId());
				container.setType("mclusteraddnode");
				container.setIpMask((String) map.get("netMask"));
				container.setContainerName((String) map.get("containerName"));
				container.setStatus(MclusterStatus.ADDING.getValue());
				//物理机集群维护完成后，修改此处，需要关联物理机id
				container.setHostIp((String) map.get("hostIp"));
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				this.containerService.insert(container);
			}
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public TaskResult analyzeRestServiceResult(ApiResultObject result) {
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(result.getResult());
		if(map == null) {
			tr.setSuccess(false);
			tr.setResult("api connect failed:" + result.getUrl());
			return tr;
		}
		Map<String,Object> meta = (Map<String, Object>) map.get("meta");
		Map<String,Object> response = null;
		
		boolean isSucess = Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")));
		if(isSucess) {
			response = (Map<String, Object>) map.get("response");
			isSucess = Constant.PYTHON_API_RESULT_SUCCESS.equals(String.valueOf(response.get("code")));
		}
		if(isSucess) {
			tr.setResult((String) response.get("message"));
		} else {
			tr.setResult((String) meta.get("errorType") +",the api url:" + result.getUrl());
		}
		tr.setSuccess(isSucess);
		return tr;
	}

	@Override
	public void callBack(TaskResult tr) {
//		super.callBack(tr);
	}

	@Override
	public void rollBack(TaskResult tr) {
		Long mclusterId = getLongFromObject(((Map<String, Object>) tr.getParams()).get("mclusterId"));
		MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
		mcluster.setStatus(MclusterStatus.ADDINGFAILED.getValue());
		this.mclusterService.updateBySelective(mcluster);
//		super.rollBack(tr);
	}
	
}
