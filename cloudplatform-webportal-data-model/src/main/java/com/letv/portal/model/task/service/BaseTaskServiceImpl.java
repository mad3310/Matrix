package com.letv.portal.model.task.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.model.UserModel;
import com.letv.portal.model.common.ZookeeperInfo;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.common.IZookeeperInfoService;

@Component("baseTaskService")
public  class BaseTaskServiceImpl implements IBaseTaskService{

	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;

	@Autowired
	private IUserService userService;

    @Autowired
    private IZookeeperInfoService zookeeperInfoService;

	private final static Logger logger = LoggerFactory.getLogger(BaseTaskServiceImpl.class);
	@Autowired
	private SchedulingTaskExecutor threadPoolTaskExecutor;
	@Override
	public TaskResult validator(Map<String, Object> params) throws Exception {
		TaskResult tr = new TaskResult();
		if(params == null || params.isEmpty()) {
			tr.setResult("params is empty");
			tr.setSuccess(false);
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = new TaskResult();
		if(params == null || params.isEmpty()) {
			tr.setResult("params is empty");
			tr.setSuccess(false);
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public TaskResult analyzeRestServiceResult(ApiResultObject resultObject){
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(resultObject.getResult());
		if(map == null) {
			tr.setSuccess(false);
			tr.setResult("api connect failed:" + resultObject.getUrl());
			return tr;
		}
		Map<String,Object> meta = (Map<String, Object>) map.get("meta");
		
		boolean isSucess = Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")));
		tr.setSuccess(isSucess);
		if(isSucess) {
			Map<String,Object> response = (Map<String, Object>) map.get("response");
			tr.setResult((String) response.get("message"));
			tr.setParams(response);
		} else {
			tr.setResult((String) meta.get("errorType") +",the api url:" + resultObject.getUrl());
		}
		return tr;
		
	}
	@Override
	@SuppressWarnings("unchecked")
	public TaskResult analyzeComplexRestServiceResult(ApiResultObject resultObject){
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(resultObject.getResult());
		if(CollectionUtils.isEmpty(map)) {
			tr.setSuccess(false);
			tr.setResult("api connect failed");
			return tr;
		}
		Map<String,Object> meta = (Map<String, Object>) map.get("meta");
		Map<String,Object> response = null;
		//如果meta的code为200，再判断response的code
		boolean isSucess = Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")));
		if(isSucess) {
			response = (Map<String, Object>) map.get("response");
			isSucess = Constant.PYTHON_API_RESULT_SUCCESS.equals(String.valueOf(response.get("code")));
		}
		if(isSucess) {
			tr.setResult((String) response.get("message"));
			tr.setParams(response);
		} else {
			tr.setResult((String) meta.get("errorType") +",the api url:" + resultObject.getUrl());
		}
		tr.setSuccess(isSucess);
		return tr;
	}
	public void buildResultToMgr(String buildType,String result,String detail,String to){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buildType", buildType);
		map.put("buildResult", result);
		map.put("errorDetail", detail);
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统", StringUtils.isEmpty(to)?SERVICE_NOTICE_MAIL_ADDRESS:to,"乐视云平台web-portal系统通知","buildForMgr.ftl",map);
		defaultEmailSender.sendMessage(mailMessage);
	}
	
	public void email4User(Map<String,Object> params,Long to,String ftlName){
		UserModel user = this.userService.selectById(to);
		if(null != user) {
			MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",user.getEmail(),"乐视云平台web-portal系统通知",ftlName,params);
			mailMessage.setHtml(true);
			defaultEmailSender.sendMessage(mailMessage);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> transToMap(String params){
		if(StringUtils.isEmpty(params))
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String,Object> jsonResult = new HashMap<String,Object>();
		try {
			jsonResult = resultMapper.readValue(params, Map.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public String transToString(Object params){
		if(params == null)
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		String jsonResult = "";
		try {
			jsonResult = resultMapper.writeValueAsString(params);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public Long getLongFromObject(Object o) {
		Long value = null;
		if(o instanceof String)
			value = Long.parseLong((String) o);
		if(o instanceof Integer)
			value = Long.parseLong(((Integer)o).toString());
		if(o instanceof Long)
			value = (Long) o;
		
		return value;
	}
	public List<ZookeeperInfo> selectMinusedZkByHclusterId(Long hclusterId,int number) {
		List<ZookeeperInfo> zks = this.zookeeperInfoService.selectMinusedZkByHclusterId(hclusterId,number);
		if(zks == null || zks.size()!=number)
			throw new ValidateException("zk numbers not sufficient");
		for (ZookeeperInfo zk : zks) {
			this.zookeeperInfoService.plusOneUsedByZookeeperId(zk.getId());
		}
		return zks;
	}

	@Override
	public void rollBack(TaskResult tr) {
		// TODO Auto-generated method stub
	}

	@Override
	public void callBack(TaskResult tr) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforExecute(Map<String, Object> params) {
		// TODO Auto-generated method stub
	}
	public TaskResult asynchroExecuteTasks(List<Task> tasks,TaskResult tr) {
		if(!tr.isSuccess())
			return tr;
		if(CollectionUtils.isEmpty(tasks)){
			tr.setSuccess(false);
			tr.setResult("there is no available tasks");
			return tr;
		}
		for(Task task:tasks){
			Object obj = task.onExec();
			if(obj == null){
				tr.setSuccess(false);
				tr.setResult("Gets the Task object's execute method returns a value is null");
				return tr;
			}
			ApiResultObject apiResult = null;
			if(obj instanceof ApiResultObject){
				apiResult = (ApiResultObject) obj;
				tr = analyzeRestServiceResult(apiResult);
			}else if(obj instanceof TaskResult){
				tr = (TaskResult) obj;
			}else{
				tr.setSuccess(false);
				tr.setResult("Gets the Task object's execute method returns a value is invalid");
				return tr;
			}
			if(!tr.isSuccess()) {
				tr.setSuccess(false);
				tr.setResult(MessageFormat.format("the {0} error:{1}",apiResult.getUrl(),tr.getResult()));
				return tr;
			}else{
				task.onSuccess(tr);
			}
		}
		return tr;
	}
	@Override
	public TaskResult synchroExecuteTasks(List<Task> tasks,TaskResult tr) {
		if(!tr.isSuccess())
			return tr;
		if(CollectionUtils.isEmpty(tasks)){
			tr.setSuccess(false);
			tr.setResult("there is no available tasks");
			return tr;
		}
		//是否继续
		boolean isContinue = true;
		Map<Future,Task> onions = new HashMap<Future, IBaseTaskService.Task>();
		for(Task task:tasks){
			//使用全局线程池
			Future future = threadPoolTaskExecutor.submit(task);
			onions.put(future, task);
		}
		OUT:
		while(isContinue){
			IUT:
			for(Future future:onions.keySet()){
				if(future.isDone()){
					Object obj = null;
					try {
						obj = future.get();
						//apiResult = (ApiResultObject) future.get();
					} catch (InterruptedException | ExecutionException e) {
						tr.setSuccess(false);
						tr.setResult("Gets the Task object's execute method returns a value failed:"+e.getMessage());
						isContinue = false;
						//终止后续任务工作
						break OUT;
					}
					if(obj == null){
						tr.setSuccess(false);
						tr.setResult("Gets the Task object's execute method returns a value is null");
						isContinue = false;
						//终止后续任务工作
						break OUT;
					}
					ApiResultObject apiResult = null;
					if(obj instanceof ApiResultObject){
						apiResult = (ApiResultObject) obj;
						tr = analyzeRestServiceResult(apiResult);
					}else if(obj instanceof TaskResult){
						tr = (TaskResult) obj;
					}else{
						tr.setSuccess(false);
						tr.setResult("Gets the Task object's execute method returns a value is invalid");
						isContinue = false;
						//终止后续任务工作
						break OUT;
					}
					if(!tr.isSuccess()) {
						tr.setSuccess(false);
						tr.setResult(MessageFormat.format("the {0} error:{1}",apiResult.getUrl(),tr.getResult()));
						isContinue = false;
						//终止后续任务工作
						break OUT;
					}else{
						//成功后执行回调
						onions.get(future).onSuccess(tr);
					}
					onions.remove(future);
					break IUT;
				}
			}
			if(onions.size()<=0){
				isContinue = false;
				break OUT;
			}
		}
		//如果有未完成任务，或者正在进行任务，直接终止其继续执行
		if(!CollectionUtils.isEmpty(onions)){
			for(Future future:onions.keySet()){
				future.cancel(true);
			}
		}
		return tr;
	}
	
}
