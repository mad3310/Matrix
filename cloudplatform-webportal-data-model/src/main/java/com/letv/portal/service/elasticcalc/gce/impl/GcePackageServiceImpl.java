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
import com.letv.portal.dao.elasticcalc.gce.IGcePackageDao;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageCluster;
import com.letv.portal.service.elasticcalc.gce.IGcePackageClusterService;
import com.letv.portal.service.elasticcalc.gce.IGcePackageService;
import com.letv.portal.service.impl.BaseServiceImpl;

/**
 * GCE部署包服务
 * 
 * @author linzhanbo .
 * @since 2016年6月29日, 上午9:50:22 .
 * @version 1.0 .
 */
@Service("gcePackageService")
public class GcePackageServiceImpl extends BaseServiceImpl<EcGcePackage>
		implements IGcePackageService {
	
	@Resource
	private IGcePackageDao gcePackageDao;
	@Resource
	private IGcePackageClusterService gcePackageClusterService;
	
	
	@Value("${gce.code}")
	private String GCE_CODE;
	
	
	public GcePackageServiceImpl() {
		super(EcGcePackage.class);
	}

	@Override
	public IBaseDao<EcGcePackage> getDao() {
		return this.gcePackageDao;
	}

	@Override
	public Map<String, Object> save(EcGce gce, EcGcePackage gcePackage) {
		Map<String,Object> params = new HashMap<String,Object>();
		//创建GcePackage
		this.gcePackageDao.insert(gcePackage);
		//创建GcePackageCluster
		String gcePackName = gce.getGceName() + "_" + gcePackage.getVersion();
		StringBuffer packClusterName = new StringBuffer();//"createUserId_GCE_CODE_gceName_version_第几个实例";
		packClusterName.append(gcePackage.getCreateUser()).append("_").append(GCE_CODE).append("_").append(gcePackName);
		
		/*function 验证clusterName是否存在*/
		Boolean isExist= this.gcePackageClusterService.isExistByName(packClusterName.toString());
		int i = 0;
		while(!isExist) {
			isExist= this.gcePackageClusterService.isExistByName(packClusterName.toString() +(i+1));
			i++;
		}
		if(i>0)
			packClusterName.append(i);
		EcGcePackageCluster gcePackCluster = new EcGcePackageCluster();
		gcePackCluster.setClusterName(packClusterName.toString());
		//sst_password
		gcePackCluster.setHclusterId(gce.getHclusterId());
		gcePackCluster.setAdminUser("root");
		gcePackCluster.setAdminPassword(gcePackCluster.getClusterName());
		gcePackCluster.setGceId(gce.getId());
		gcePackCluster.setGcePackageId(gcePackage.getId());
		gcePackCluster.setStatus(MclusterStatus.BUILDDING.getValue());
		gcePackCluster.setCreateUser(gcePackage.getCreateUser());
		this.gcePackageClusterService.insert(gcePackCluster);
		//更改GcePackage，添加clusterid信息.
		gcePackage.setGceclusterId(gcePackCluster.getId());
		gcePackage.setUpdateUser(gcePackage.getCreateUser());
		this.gcePackageDao.update(gcePackage);
		params.put("gcePackageId", gcePackage.getId());
    	params.put("gcePackageVersion", gcePackage.getVersion());
    	params.put("gcePackageSuffix", gcePackage.getSuffix());
    	params.put("gcePackageBucketName", gcePackage.getBucketName());
    	params.put("gcePackageKey", gcePackage.getKey());
    	
    	params.put("gcePackageClusterId", gcePackCluster.getId());
    	params.put("gcePackageClusterName", gcePackCluster.getClusterName());
    	
    	params.put("serviceName", gcePackName);
    	params.put("clusterName", gcePackCluster.getClusterName());
		return params;
	}
}
