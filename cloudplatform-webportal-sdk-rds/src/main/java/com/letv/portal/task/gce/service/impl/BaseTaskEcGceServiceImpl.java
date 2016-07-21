package com.letv.portal.task.gce.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.GcePackageStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGceImage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGceImageService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;

@Component("baseEcGceTaskService")
public class BaseTaskEcGceServiceImpl extends BaseTaskServiceImpl implements IBaseTaskService{

	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Autowired
	private IHostService hostService;
	@Autowired
	private IEcGceClusterService ecGceClusterService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEcGceService gceService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;
	@Autowired
	private IEcGceImageService ecGceImageService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(BaseTaskEcGceServiceImpl.class);
	
	@Override
	public void beforExecute(Map<String, Object> params) {
		EcGcePackage gcePackage = this.getGcePackage(params);
		EcGceCluster cluster = this.getGceCluster(params);

		if(gcePackage.getStatus() != DbStatus.BUILDDING.getValue()) {
			gcePackage.setStatus(DbStatus.BUILDDING.getValue());
			gcePackage.setUpdateUser(gcePackage.getCreateUser());
			this.ecGcePackageService.updateBySelective(gcePackage);
		}
		if(cluster.getStatus() != MclusterStatus.BUILDDING.getValue()) {
			cluster.setStatus(MclusterStatus.BUILDDING.getValue());
			cluster.setUpdateUser(cluster.getCreateUser());
			this.ecGceClusterService.updateBySelective(cluster);
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
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		//发送邮件
		String serverName =  (String) params.get("serviceName");
		this.buildResultToMgr("Gce服务("+serverName+")创建", tr.isSuccess()?"成功":"失败", tr.getResult(), SERVICE_NOTICE_MAIL_ADDRESS);
		//业务处理
		this.serviceOver(tr);
	}
	private void serviceOver(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
		EcGcePackage gcePackage = this.getGcePackage(params);
		EcGceCluster cluster = this.getGceCluster(params);
		String serverName =  (String) params.get("serviceName");
		if(tr.isSuccess()) {
			gcePackage.setStatus(GcePackageStatus.NORMAL.getValue());
			cluster.setStatus(MclusterStatus.RUNNING.getValue());
			Map<String, Object> emailParams = new HashMap<String,Object>();
			emailParams.put("gceName", serverName);
			this.email4User(emailParams, gcePackage.getCreateUser(),"gce/createGce.ftl");
		} else {
			gcePackage.setStatus(GcePackageStatus.BUILDFAIL.getValue());
			cluster.setStatus(MclusterStatus.BUILDFAIL.getValue());
		}
		this.ecGcePackageService.updateBySelective(gcePackage);
		this.ecGceClusterService.updateBySelective(cluster);
	}

	@Override
	public void callBack(TaskResult tr) {
		
	}
	
	public EcGceImage getGceImage(Map<String, Object> params) {
		Long gceImageId = getLongFromObject(params.get("gceImageId"));
		if(gceImageId == null)
			throw new ValidateException("params's gceImageId is null");
		
		EcGceImage gceImage = this.ecGceImageService.selectById(gceImageId);
		if(gceImage == null)
			throw new ValidateException("gceImageService is null by gceImageId:" + gceImageId);
		return gceImage;
	}

	public EcGcePackage getGcePackage(Map<String, Object> params) {
		Long gcePackageId = getLongFromObject(params.get("gcePackageId"));
		if(gcePackageId == null)
			throw new ValidateException("params's gcePackageId is null");
		
		EcGcePackage gcePackage = this.ecGcePackageService.selectById(gcePackageId);
		if(gcePackage == null)
			throw new ValidateException("gcePackageService is null by gcePackageId:" + gcePackageId);
		return gcePackage;
	}
	
	public EcGce getGce(Map<String, Object> params) {
		Long gceId = getLongFromObject(params.get("gceId"));
		if(gceId == null)
			throw new ValidateException("params's gceId is null");
		
		EcGce gce = this.gceService.selectById(gceId);
		if(gce == null)
			throw new ValidateException("gceService is null by gceId:" + gceId);
		return gce;
	}

	
	public EcGceCluster getGceCluster(Map<String, Object> params) {
		Long gceClusterId = getLongFromObject(params.get("gceClusterId"));
		if(gceClusterId == null)
			throw new ValidateException("params's gceClusterId is null");
		
		EcGceCluster gceCluster = this.ecGceClusterService.selectById(gceClusterId);
		if(gceCluster == null)
			throw new ValidateException("gceCluster is null by gceClusterId:" + gceClusterId);
		
		return gceCluster;
	}

	public HostModel getHost(Long hclusterId) {
		if(hclusterId == null)
			throw new ValidateException("hclusterId is null :" + hclusterId);
		HostModel host = this.hostService.getHostByHclusterId(hclusterId);
		if(host == null)
			throw new ValidateException("host is null by hclusterIdId:" + hclusterId);
		
		return host;
	}
}
