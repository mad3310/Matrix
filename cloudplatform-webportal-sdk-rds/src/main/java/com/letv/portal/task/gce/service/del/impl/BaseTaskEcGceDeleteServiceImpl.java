/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.gce.service.del.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.GcePackageStatus;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGceImage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.BaseTaskServiceImpl;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;
import com.letv.portal.service.elasticcalc.gce.IEcGceImageService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;

@Component("baseTaskEcGceDeleteServiceImpl")
public class BaseTaskEcGceDeleteServiceImpl extends BaseTaskServiceImpl implements IBaseTaskService {
	@Autowired
	private IEcGceClusterService ecGceClusterService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;
    @Autowired
	private IEcGceContainerService ecGceContainerService;
    @Autowired
    private IEcGceImageService ecGceImageService;
	
	public EcGceCluster getGceCluster(Map<String, Object> params) {
		Long gceClusterId = getLongFromObject(params.get("gceClusterId"));
		if(null == gceClusterId)
			throw new ValidateException("params's gceClusterId is null");
		
		EcGceCluster gceCluster = this.ecGceClusterService.selectById(gceClusterId);
		if(null == gceCluster)
			throw new ValidateException("gceCluster is null by gceClusterId:" + gceClusterId);
		return gceCluster;
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
	public EcGceImage getGceImage(Map<String, Object> params) {
		Long gceImageId = getLongFromObject(params.get("gceImageId"));
		if(null == gceImageId)
			throw new ValidateException("params's gceImageId is null");
		
		EcGceImage gceImage = this.ecGceImageService.selectById(gceImageId);
		if(null == gceImage)
			throw new ValidateException("gceImageService is null by gceImageId:" + gceImageId);
		return gceImage;
	}
	@Override
	public void beforeExecute(Map<String, Object> params) {
		EcGcePackage gcePackage = this.getGcePackage(params);
		EcGceCluster cluster = this.getGceCluster(params);

		if(gcePackage.getStatus() !=GcePackageStatus.DESTROYING.getValue()) {
			gcePackage.setStatus(GcePackageStatus.DESTROYING.getValue());
			gcePackage.setUpdateUser(gcePackage.getCreateUser());
			this.ecGcePackageService.updateBySelective(gcePackage);
		}
		if(cluster.getStatus() != MclusterStatus.DESTROYING.getValue()) {
			cluster.setStatus(MclusterStatus.DESTROYING.getValue());
			cluster.setUpdateUser(cluster.getCreateUser());
			this.ecGceClusterService.updateBySelective(cluster);
		}
	}
	@Override
	public void rollBack(TaskResult tr) {
		finish(tr);
	}
	@Override
	public void finish(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		//发送邮件
		//String serverName =  (String) params.get("serviceName");
		//this.buildResultToMgr("Gce服务("+serverName+")删除", tr.isSuccess()?"成功":"失败", tr.getResult(), SERVICE_NOTICE_MAIL_ADDRESS);
		//业务处理
		this.serviceOver(tr);
	}
	private void serviceOver(TaskResult tr) {
		Map<String, Object> params = (Map<String, Object>) tr.getParams();
		EcGcePackage gcePackage = this.getGcePackage(params);
		EcGceCluster cluster = this.getGceCluster(params);
		//String serverName =  (String) params.get("serviceName");
		if(tr.isSuccess()) {
			//删除版本包、基础镜像、删除集群、删除所有容器	//TODO	容器扩展信息在新GCE里没用到
			this.ecGceClusterService.delete(cluster);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("gceId", cluster.getGceId());
			map.put("gcePackageId", cluster.getGcePackageId());
			this.ecGceContainerService.deleteBySelective(map);
			this.ecGcePackageService.delete(gcePackage);
			EcGceImage image = this.getGceImage(params);
			this.ecGceImageService.delete(image);
			/*Map<String, Object> emailParams = new HashMap<String,Object>();
			emailParams.put("gceName", gce.getGceName());
			emailParams.put("ver", gcePackage.getVersion());
			emailParams.put("ips", ips.toString());
			this.email4User(emailParams, gcePackage.getCreateUser(),"elasticcalc/gce/createEcGce.ftl");*/
		} else {
			gcePackage.setStatus(GcePackageStatus.DESTROYFAILED.getValue());
			cluster.setStatus(MclusterStatus.DESTROYFAILED.getValue());
		}
		this.ecGcePackageService.updateBySelective(gcePackage);
		this.ecGceClusterService.updateBySelective(cluster);
	}
}
