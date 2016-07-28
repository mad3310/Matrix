package com.letv.portal.task.cbase.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.cbase.CbaseBucketModel;
import com.letv.portal.model.cbase.CbaseClusterModel;
import com.letv.portal.model.image.Image;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.ICbasePythonService;
import com.letv.portal.service.image.IImageService;

@Service("taskCbaseClusterCreateService")
public class TaskCbaseClusterCreateServiceImpl extends
		BaseTask4CbaseServiceImpl implements IBaseTaskService {

	@Value("${matrix.cbase.default.image}")
	private String MATRIX_CBASE_DEFAULT_IMAGE;
	@Autowired
	private IImageService imageService;
	
	@Autowired
	private ICbasePythonService cbasePythonService;
	private final static Logger logger = LoggerFactory
			.getLogger(TaskCbaseClusterCreateServiceImpl.class);

	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;

		CbaseClusterModel cbaseCluster = super.getCbaseCluster(params);
		CbaseBucketModel bucket = super.getCbaseBucket(params);
		HostModel host = super.getHost(cbaseCluster.getHclusterId());
		Long hostSize = getLongFromObject(params.get("hostSize"));
		String mountDir = "[{'/opt/letv/cbase/var/lib/couchbase/data':'/opt/letv/cbase/var/lib/couchbase/data/"
				+ cbaseCluster.getCbaseClusterName() + "', 'ro':False}]";

		// per cluster node mem quota = (bucket mem quota / hostSize) + 100MB
		long tmpPerClusterNodeMemQuotaMB = Integer.valueOf(bucket
				.getRamQuotaMB()) / hostSize;
		long perClusterNodeMemQuotaMB = (long) tmpPerClusterNodeMemQuotaMB + 100;
		String memory = String.valueOf(perClusterNodeMemQuotaMB * 1024 * 1024);

		//从数据库获取image
		Map<String,String> map = new HashMap<String,String>();
		map.put("dictionaryName", "OCS");
		map.put("purpose", "default");
		map.put("isUsed", "1");
		List<Image> images = this.imageService.selectByMap(map);
		if(images!=null && images.size()>1)
			throw new ValidateException("get one Image had many result, params :" + map.toString());
		
		map.clear();
		map.put("containerClusterName", cbaseCluster.getCbaseClusterName());
		map.put("componentType", "cbase");
		map.put("networkMode", "ip");
		map.put("nodeCount", String.valueOf(hostSize));
		map.put("mountDir", mountDir);
		map.put("memory", memory);
		map.put("image", images==null||images.size()==0||images.get(0).getUrl()==null ? MATRIX_CBASE_DEFAULT_IMAGE : images.get(0).getUrl());

		ApiResultObject result = this.cbasePythonService.createContainer(map,
				host.getHostIp(), host.getName(), host.getPassword());
		tr = analyzeRestServiceResult(result);

		tr.setParams(params);
		return tr;
	}

}
