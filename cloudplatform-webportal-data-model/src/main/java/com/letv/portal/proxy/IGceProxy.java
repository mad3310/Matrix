package com.letv.portal.proxy;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.model.gce.GceServer;

public interface IGceProxy extends IBaseProxy<GceServer> {
	
	public void saveAndBuild(GceServer gceServer,Long rdsId,Long ocsId);
	public void start(Long id);
	public void stop(Long id);
	public void restart(Long id);
	public void capacity(Long clusterId, int multiple);
	public void checkStatus();
	/**
	 * 创建GCE
	 * @param gce
	 * @param gceExt
	 * @author linzhanbo .
	 * @since 2016年6月28日, 下午3:13:10 .
	 * @version 1.0 .
	 */
	public void createGce(EcGce gce, EcGceExt gceExt);
	/**
	 * 上传并部署应用包
	 * @param file
	 * @param gcePackage	应用包信息
	 * @author linzhanbo .
	 * @since 2016年7月4日, 上午10:57:16 .
	 * @version 1.0 .
	 */
	public void uploadPackage(MultipartFile file, EcGcePackage gcePackage);
	/**
	 * 上传应用包
	 * @param file
	 * @param gcePackage
	 */
	void uploadPackageNoWorkflow(MultipartFile file, EcGcePackage gcePackage);
	/**
	 * 使用gce部署包获取该部署包所有容器信息
	 * @param gcePackage
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月5日, 下午6:01:27 .
	 * @version 1.0 .
	 */
	public List<EcGceContainer> getGcepackageContainers(EcGcePackage gcePackage);
	/**
	 * 部署应用包
	 * @param ecGcePackage
	 */
	public void deployGCE(EcGcePackage ecGcePackage);
	/**
	 * 删除GCE版本包
	 * @param gcePackageId
	 * @param gceId
	 * @author linzhanbo .
	 * @since 2016年9月9日, 下午3:15:20 .
	 * @version 1.0 .
	 */
	public void deletePackage(Long gcePackageId, Long gceId);
}
