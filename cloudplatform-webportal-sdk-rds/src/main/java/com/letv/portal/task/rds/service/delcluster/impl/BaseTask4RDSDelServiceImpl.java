package com.letv.portal.task.rds.service.delcluster.impl;

import java.awt.Container;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.EsStatus;
import com.letv.portal.enumeration.HostType;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;

@Component("baseRDSDelTaskService")
public class BaseTask4RDSDelServiceImpl extends BaseTaskServiceImpl implements IBaseTaskService{

	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private IDbService dbService;
	private final static Logger logger = LoggerFactory.getLogger(BaseTask4RDSDelServiceImpl.class);
	
	@Override
	public void beforeExecute(Map<String, Object> params) {
		DbModel db = this.getDbServer(params);
		MclusterModel cluster = this.getMcluster(params);
		if(db.getStatus() != DbStatus.DELETING.getValue()) {
			db.setStatus(DbStatus.DELETING.getValue());
			this.dbService.updateBySelective(db);
		}
		if(cluster.getStatus() != MclusterStatus.DELETING.getValue()) {
			cluster.setStatus(MclusterStatus.DELETING.getValue());
			this.mclusterService.updateBySelective(cluster);
		}
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
	public void rollBack(TaskResult tr) {
		String serverName = "";
		if(tr.getParams() !=null)
			serverName =  (String) ((Map<String, Object>) tr.getParams()).get("serviceName");
		//发送错误邮件
		this.buildResultToMgr("RDS服务("+serverName+")创建", tr.isSuccess()?"成功":"失败", tr.getResult(), SERVICE_NOTICE_MAIL_ADDRESS);
		//业务处理
		this.serviceOver(tr);
	}
	
	private void serviceOver(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
		Long mclusterId = getLongFromObject(params.get("mclusterId"));
		Long dbId = getLongFromObject(params.get("dbId"));
		if(mclusterId == null)
			throw new ValidateException("params's mclusterId is null");
		//执行业务
		MclusterModel mclusterModel = this.mclusterService.selectById(mclusterId);
		if(mclusterModel == null)
			throw new ValidateException("mclusterModel is null by mclusterId:" + mclusterId);
		if(tr.isSuccess()) {
			mclusterModel.setStatus(MclusterStatus.RUNNING.getValue());
			this.mclusterService.audit(mclusterModel);
			logger.info("RDS service build success:" + mclusterModel.getMclusterName());
		} else {
			mclusterModel.setStatus(MclusterStatus.BUILDFAIL.getValue());
			this.mclusterService.audit(mclusterModel);
			logger.info("RDS service build failed:" + mclusterModel.getMclusterName());
			if(dbId==null)
				return;
			DbModel dbModel = this.dbService.selectById(dbId);
			if(dbModel.getStatus() != DbStatus.NORMAL.getValue()) {
				dbModel.setStatus(DbStatus.BUILDFAIL.getValue());
				this.dbService.updateBySelective(dbModel);
			}
		}
	}
	
	@Override
	public void callBack(TaskResult tr) {
	}

	public DbModel getDbServer(Map<String, Object> params) {
		Long dbId = getLongFromObject(params.get("dbId"));
		if(null == dbId)
			throw new ValidateException("params's dbId is null");
		
		DbModel db = this.dbService.selectById(dbId);
		if(null == db)
			throw new ValidateException("DbModel is null by dbId:" + dbId);
		
		return db;
	}
	
	public MclusterModel getMcluster(Map<String, Object> params) {
		Long mclusterId = getLongFromObject(params.get("mclusterId"));
		if(mclusterId == null)
			throw new ValidateException("params's mclusterId is null");
		
		MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
		if(null == mcluster)
			throw new ValidateException("MclusterModel is null by mclusterId:" + mclusterId);
		
		return mcluster;
	}

	public List<ContainerModel> getContainers(Map<String, Object> params) {
		Long mclusterId = getLongFromObject(params.get("mclusterId"));
		if(null == mclusterId)
			throw new ValidateException("params's mclusterId is null");
		
		List<ContainerModel> containers = this.containerService.selectContainerByMclusterId(mclusterId);
		if(CollectionUtils.isEmpty(containers))
			throw new ValidateException("ContainerModel is null by mclusterId:" + mclusterId);
		return containers;
	}
	
}
