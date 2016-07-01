package com.letv.portal.proxy;

import org.springframework.web.multipart.MultipartFile;

import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
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
	 * 上传并保存应用包
	 * @param file
	 * @param gcePackage	应用包信息
	 * @author linzhanbo .
	 * @since 2016年6月28日, 下午5:57:38 .
	 * @version 1.0 .
	 */
	public void uploadPackage(MultipartFile file, EcGcePackage gcePackage);
}
