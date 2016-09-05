package com.letv.portal.task.gce.service.impl;

import java.text.MessageFormat;
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
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.GcePackageStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.model.elasticcalc.gce.EcGceImage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;
import com.letv.portal.service.elasticcalc.gce.IEcGceImageService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;

@Component("baseEcGceTaskService")
public class BaseTaskEcGceServiceImpl extends BaseTaskServiceImpl implements IBaseTaskService{

	@Autowired
	private IEcGceClusterService ecGceClusterService;
	@Autowired
	private IEcGceService gceService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;
	@Autowired
	private IEcGceImageService ecGceImageService;
	@Autowired
	private IEcGceContainerService ecGceContainerService;
	
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
	public void beforeExecute(Map<String, Object> params) {
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
		String serverName =  (String) params.get("serviceName");
		this.buildResultToMgr("Gce服务("+serverName+")创建", tr.isSuccess()?"成功":"失败", tr.getResult(), SERVICE_NOTICE_MAIL_ADDRESS);
		//业务处理
		this.serviceOver(tr);
	}
	
	private void serviceOver(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
		EcGcePackage gcePackage = this.getGcePackage(params);
		EcGceCluster cluster = this.getGceCluster(params);
		//String serverName =  (String) params.get("serviceName");
		EcGce gce = this.getGce(params);
		if(tr.isSuccess()) {
			StringBuffer ips = new StringBuffer();
			List<EcGceContainer> containers = this.getGceContainers(params);
			for(EcGceContainer container:containers){
				ips.append(MessageFormat.format("<a href=\"http://{0}:{1}\">http://{2}:{3}</a><br/>", 
						container.getIpAddr(),container.getBindHostPort(),container.getIpAddr(),container.getBindHostPort()));
			}
			gcePackage.setStatus(GcePackageStatus.NORMAL.getValue());
			cluster.setStatus(MclusterStatus.RUNNING.getValue());
			Map<String, Object> emailParams = new HashMap<String,Object>();
			emailParams.put("gceName", gce.getGceName());
			emailParams.put("ver", gcePackage.getVersion());
			emailParams.put("ips", ips.toString());
			this.email4User(emailParams, gcePackage.getCreateUser(),"elasticcalc/gce/createEcGce.ftl");
		} else {
			gcePackage.setStatus(GcePackageStatus.BUILDFAIL.getValue());
			cluster.setStatus(MclusterStatus.BUILDFAIL.getValue());
		}
		this.ecGcePackageService.updateBySelective(gcePackage);
		this.ecGceClusterService.updateBySelective(cluster);
	}
	
	private List<EcGceContainer> getGceContainers(Map<String, Object> params) {
		Long gceId = getLongFromObject(params.get("gceId"));
		if(null == gceId)
			throw new ValidateException("params's gceId is null");
		Long gcePackageId = getLongFromObject(params.get("gcePackageId"));
		if(null == gcePackageId)
			throw new ValidateException("params's gcePackageId is null");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("gceId", gceId);
		map.put("gcePackageId", gcePackageId);
		List<EcGceContainer> containers = ecGceContainerService.selectByMap(map);
		if(CollectionUtils.isEmpty(containers))
			throw new ValidateException("gceContainerService is null by gceId:" + gceId);
		return containers;
	}
	
	public EcGceImage getGceImage(Map<String, Object> params) {
		Long gceImageId = getLongFromObject(params.get("gceImageId"));
		if(null == gceImageId)
			throw new ValidateException("params's gceImageId is null");
		
		EcGceImage gceImage = this.ecGceImageService.selectById(gceImageId);
		if(null == gceImage)
			throw new ValidateException("gceImageService is null by gceImageId:" + gceImageId);
		return gceImage;
	}

	public EcGcePackage getGcePackage(Map<String, Object> params) {
		Long gcePackageId = getLongFromObject(params.get("gcePackageId"));
		if(null == gcePackageId)
			throw new ValidateException("params's gcePackageId is null");
		
		EcGcePackage gcePackage = this.ecGcePackageService.selectById(gcePackageId);
		if(null == gcePackage)
			throw new ValidateException("gcePackageService is null by gcePackageId:" + gcePackageId);
		return gcePackage;
	}
	
	public EcGce getGce(Map<String, Object> params) {
		Long gceId = getLongFromObject(params.get("gceId"));
		if(null == gceId)
			throw new ValidateException("params's gceId is null");
		
		EcGce gce = this.gceService.selectById(gceId);
		if(null == gce)
			throw new ValidateException("gceService is null by gceId:" + gceId);
		return gce;
	}

	
	public EcGceCluster getGceCluster(Map<String, Object> params) {
		Long gceClusterId = getLongFromObject(params.get("gceClusterId"));
		if(null == gceClusterId)
			throw new ValidateException("params's gceClusterId is null");
		
		EcGceCluster gceCluster = this.ecGceClusterService.selectById(gceClusterId);
		if(null == gceCluster)
			throw new ValidateException("gceCluster is null by gceClusterId:" + gceClusterId);
		
		return gceCluster;
	}

}
