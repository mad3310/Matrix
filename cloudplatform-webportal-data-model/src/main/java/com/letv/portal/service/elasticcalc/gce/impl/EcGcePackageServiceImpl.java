/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.service.elasticcalc.gce.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.elasticcalc.gce.IEcGcePackageDao;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE部署包服务
 * 
 * @author linzhanbo .
 * @since 2016年6月29日, 上午9:50:22 .
 * @version 1.0 .
 */
@Service("ecGcePackageService")
public class EcGcePackageServiceImpl extends BaseServiceImpl<EcGcePackage>
		implements IEcGcePackageService {
	
	@Resource
	private IEcGcePackageDao ecGcePackageDao;
	@Resource
	private IEcGceClusterService ecGceClusterService;
	
	
	@Value("${gce.code}")
	private String GCE_CODE;
	
	
	public EcGcePackageServiceImpl() {
		super(EcGcePackage.class);
	}

	@Override
	public IBaseDao<EcGcePackage> getDao() {
		return this.ecGcePackageDao;
	}

	@Override
	public Map<String, Object> insertGceAndGcePackage(EcGce gce, EcGcePackage gcePackage) {
		Map<String,Object> params = new HashMap<String,Object>();
		//创建GcePackage
		this.ecGcePackageDao.insert(gcePackage);
		return insertGceCuster(gce,gcePackage);
	}
	@Override
	public Map<String, Object> insertGceCuster(EcGce gce, EcGcePackage gcePackage) {
		Map<String,Object> params = new HashMap<String,Object>();
		//创建GcePackageCluster
		String gcePackName = gce.getGceName() + "_" + gcePackage.getVersion();
		StringBuffer packClusterName = new StringBuffer();//"createUserId_GCE_CODE_gceName_version_第几个实例";
		packClusterName.append(gcePackage.getCreateUser()).append("_").append(GCE_CODE).append("_").append(gcePackName);
		
		/*function 验证clusterName是否存在*/
		Boolean isExist= this.ecGceClusterService.isExistByName(packClusterName.toString());
		int i = 0;
		while(!isExist) {
			isExist= this.ecGceClusterService.isExistByName(packClusterName.toString() +(i+1));
			i++;
		}
		if(i>0)
			packClusterName.append(i);
		EcGceCluster gcePackCluster = new EcGceCluster();
		gcePackCluster.setClusterName(packClusterName.toString());
		//sst_password
		gcePackCluster.setHclusterId(gce.getHclusterId());
		gcePackCluster.setAdminUser("root");
		gcePackCluster.setAdminPassword(gcePackCluster.getClusterName());
		gcePackCluster.setGceId(gce.getId());
		gcePackCluster.setGcePackageId(gcePackage.getId());
		gcePackCluster.setStatus(MclusterStatus.BUILDDING.getValue());
		gcePackCluster.setCreateUser(gcePackage.getCreateUser());
		this.ecGceClusterService.insert(gcePackCluster);
		//更改GcePackage，添加clusterid信息.
		gcePackage.setGceclusterId(gcePackCluster.getId());
		gcePackage.setUpdateUser(gcePackage.getCreateUser());
		this.ecGcePackageDao.update(gcePackage);
		params.put("gcePackageId", gcePackage.getId());
    	params.put("gcePackageVersion", gcePackage.getVersion());
    	params.put("gcePackageSuffix", gcePackage.getSuffix());
    	params.put("gcePackageBucketName", gcePackage.getBucketName());
    	params.put("gcePackageKey", gcePackage.getKey());
    	
    	params.put("gceClusterId", gcePackCluster.getId());
    	params.put("gceClusterName", gcePackCluster.getClusterName());
    	
    	params.put("serviceName", gcePackName);
    	params.put("clusterName", gcePackCluster.getClusterName());
		return params;
	}
}
