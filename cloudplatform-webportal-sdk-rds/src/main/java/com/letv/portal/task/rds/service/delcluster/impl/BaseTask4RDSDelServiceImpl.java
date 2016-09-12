package com.letv.portal.task.rds.service.delcluster.impl;

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
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;

@Component("baseTask4RDSDelService")
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
		if(db.getStatus() != DbStatus.DESTROYING.getValue()) {
			db.setStatus(DbStatus.DESTROYING.getValue());
			this.dbService.updateBySelective(db);
		}
		if(cluster.getStatus() != MclusterStatus.DESTROYING.getValue()) {
			cluster.setStatus(MclusterStatus.DESTROYING.getValue());
			this.mclusterService.updateBySelective(cluster);
		}
	}
	
	@Override
	public void rollBack(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
        DbModel db = getDbServer(params);
        if(DbStatus.DESTROYING.getValue() == db.getStatus()) {
            db.setStatus(DbStatus.DESTROYFAILED.getValue());
            dbService.updateBySelective(db);
        }
        MclusterModel mcluster = getMcluster(params);
        if(MclusterStatus.DESTROYING.getValue() == mcluster.getStatus()) {
        	mcluster.setStatus(MclusterStatus.DESTROYFAILED.getValue());
        	this.mclusterService.updateBySelective(mcluster);
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
