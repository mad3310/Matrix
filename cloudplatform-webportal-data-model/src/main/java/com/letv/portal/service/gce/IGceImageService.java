package com.letv.portal.service.gce;

import com.letv.portal.model.gce.GceImage;
import com.letv.portal.service.IBaseService;

public interface IGceImageService extends IBaseService<GceImage> {

	public GceImage selectByUrl(String url);

	void pushImage(Long id, String hclusterIds);
	
	/**
	 * 根据多用户名称保存镜像
	 * @param userNames 多个用户名称，以逗号分割
	 * @param gceImage 镜像参数
	 */
	void saveInfoWithUserNames(String userNames, GceImage gceImage);
}
