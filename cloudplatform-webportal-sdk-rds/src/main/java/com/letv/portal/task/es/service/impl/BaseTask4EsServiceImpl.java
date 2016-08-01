package com.letv.portal.task.es.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.EsStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.common.IZookeeperInfoService;
import com.letv.portal.service.es.IEsClusterService;
import com.letv.portal.service.es.IEsContainerService;
import com.letv.portal.service.es.IEsServerService;

@Component("baseTask4EsService")
public class BaseTask4EsServiceImpl extends BaseTaskServiceImpl implements IBaseTaskService{

	@Autowired
	private IEsClusterService esClusterService;
	@Autowired
	private IEsServerService esServerService;
	@Autowired
	private IEsContainerService esContainerService;
	
	private final static Logger logger = LoggerFactory.getLogger(BaseTask4EsServiceImpl.class);
	
	@Override
	public void beforExecute(Map<String, Object> params) {
		EsServer es = this.getEsServer(params);
		EsCluster cluster = this.getEsCluster(params);
		if(es.getStatus() != EsStatus.BUILDDING) {
			es.setStatus(EsStatus.BUILDDING);
			this.esServerService.updateBySelective(es);
		}
		if(cluster.getStatus() != MclusterStatus.BUILDDING) {
			cluster.setStatus(MclusterStatus.BUILDDING);
			this.esClusterService.updateBySelective(cluster);
		}
	}
	
	@Override
	public void beforeExecute(Map<String, Object> params) {
		EsServer es = this.getEsServer(params);
		EsCluster cluster = this.getEsCluster(params);
		if(es.getStatus() != EsStatus.BUILDDING) {
			es.setStatus(EsStatus.BUILDDING);
			this.esServerService.updateBySelective(es);
		}
		if(cluster.getStatus() != MclusterStatus.BUILDDING) {
			cluster.setStatus(MclusterStatus.BUILDDING);
			this.esClusterService.updateBySelective(cluster);
		}
	}
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		return validator(params);
	}

	@Override
	public void rollBack(TaskResult tr) {
		finish(tr);
	}
	
	@Override
	public void finish(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		//发送邮件
		this.buildResultToMgr("es服务创建", tr.isSuccess()?"创建成功":"创建失败", tr.getResult(), SERVICE_NOTICE_MAIL_ADDRESS);
		//业务处理
		this.serviceOver(tr);
	}
	private void serviceOver(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
		EsServer es = this.getEsServer(params);
		EsCluster cluster = this.getEsCluster(params);
		
		if(tr.isSuccess()) {
			es.setStatus(EsStatus.NORMAL);
			cluster.setStatus(MclusterStatus.RUNNING);
			Map<String, Object> emailParams = new HashMap<String,Object>();
			emailParams.put("esName", es.getEsName());
			this.email4User(emailParams, es.getCreateUser(),"es/createEs.ftl");
		} else {
			es.setStatus(EsStatus.BUILDFAIL);
			cluster.setStatus(MclusterStatus.BUILDFAIL);
		}
		this.esServerService.updateBySelective(es);
		this.esClusterService.updateBySelective(cluster);
	}

	public EsServer getEsServer(Map<String, Object> params) {
		Long esId = getLongFromObject(params.get("esId"));
		if(null == esId)
			throw new ValidateException("params's esId is null");
		
		EsServer esServer = this.esServerService.selectById(esId);
		if(null == esServer)
			throw new ValidateException("EsServer is null by esId:" + esId);
		
		return esServer;
	}
	
	public EsCluster getEsCluster(Map<String, Object> params) {
		Long esClusterId = getLongFromObject(params.get("esClusterId"));
		if(esClusterId == null)
			throw new ValidateException("params's esClusterId is null");
		
		EsCluster esCluster = this.esClusterService.selectById(esClusterId);
		if(null == esCluster)
			throw new ValidateException("EsCluster is null by esClusterId:" + esClusterId);
		
		return esCluster;
	}

	public List<EsContainer> getContainers(Map<String, Object> params) {
		Long esClusterId = getLongFromObject(params.get("esClusterId"));
		if(null == esClusterId)
			throw new ValidateException("params's esClusterId is null");
		
		List<EsContainer> esContainers = this.esContainerService.selectContainersByEsClusterId(esClusterId);
		if(CollectionUtils.isEmpty(esContainers))
			throw new ValidateException("esContainers is null by esClusterId:" + esClusterId);
		return esContainers;
	}
	
}
