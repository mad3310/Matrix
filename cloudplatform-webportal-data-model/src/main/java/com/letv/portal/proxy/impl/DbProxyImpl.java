package com.letv.portal.proxy.impl;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.proxy.IDbProxy;
import com.letv.portal.proxy.IMclusterProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.mysql.jdbc.StringUtils;

@Component
public class DbProxyImpl extends BaseProxyImpl<DbModel> implements
		IDbProxy{
	
	private final static Logger logger = LoggerFactory.getLogger(DbProxyImpl.class);
	
	@Autowired
	private IDbService dbService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IMclusterProxy mclusterProxy;
	@Autowired
	private IBuildTaskService buildTaskService;
	
	@Override
	public IBaseService<DbModel> getService() {
		return dbService;
	}
	public DbModel dbList(Long dbId){
		DbModel db = this.dbService.selectById(dbId);
		db.setContainers(this.containerService.selectByMclusterId(db.getMclusterId()));
		return db;
	}
	@Override
	public void auditAndBuild(Map<String, Object> params) {
		
		Integer status = (Integer) params.get("status");
		Long mclusterId = (Long) params.get("mclusterId");
		Long dbId = (Long) params.get("dbId");
		String mclusterName = (String) params.get("mclusterName");		
		String auditInfo = (String) params.get("auditInfo");
		
		DbModel dbModel = new DbModel();
		dbModel.setId(dbId);
		dbModel.setStatus(status);
		
		MclusterModel mcluster = new MclusterModel();
		
		if(DbStatus.BUILDDING.getValue() == status) {//审核成功
			//判断mclsuterId是否为空
			if(mclusterId == null) { //创建新的mcluster集群
				mcluster.setMclusterName(mclusterName);
				this.mclusterProxy.insert(mcluster);
				dbModel.setMclusterId(mcluster.getId());
				this.dbService.updateBySelective(dbModel);
				this.buildTaskService.buildMcluster(mcluster,dbId);
			} else {
				dbModel.setMclusterId(mclusterId);
				this.dbService.updateBySelective(dbModel);
				this.buildTaskService.buildDb(dbId);
			}
		} else { //审核失败
			dbModel.setAuditInfo(auditInfo);
			this.dbService.updateBySelective(dbModel);
		}
			
		
	}
	
}
