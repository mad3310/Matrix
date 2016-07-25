package com.letv.portal.task.gce.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.GceType;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.image.Image;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.image.IImageService;

@Service("taskGceClusterCreateService")
public class TaskGceClusterCreateServiceImpl extends BaseTask4GceServiceImpl implements IBaseTaskService{
	
	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IImageService imageService;
	@Value("${matrix.gce.jetty.default.image}")
	private String MATRIX_GCE_JETTY_DEFAULT_IMAGE;
	@Value("${matrix.gce.nginx.default.image}")
	private String MATRIX_GCE_NGINX_DEFAULT_IMAGE;
	private final static Logger logger = LoggerFactory.getLogger(TaskGceClusterCreateServiceImpl.class);
	
	private final static String  CONTAINER_MEMORY_SIZE = "2147483648";
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		GceCluster gceCluster = super.getGceCluster(params);
		HostModel host = super.getHost(gceCluster.getHclusterId());
		GceServer gceServer = super.getGceServer(params);
		
		
		GceType gceType = gceServer.getType();
		boolean isNginx = gceType.equals(GceType.NGINX) || gceType.equals(GceType.NGINX_PROXY);
		
		//从数据库获取image
		Map<String,String> map = new HashMap<String,String>();
		map.put("dictionaryName", "GCE");
		map.put("isUsed", "1");
		List<Image> images = this.imageService.selectByMap(map);
		if(images == null || images.size()>2) {
			tr.setResult("get two Image had many result, params :" + map.toString());
            tr.setSuccess(false);
			return tr;
		}
		String nginxImage = null;
		String jettyImage = null;
		for (Image image : images) {
			if("nginx".equals(image.getPurpose())) {
				nginxImage = image.getUrl();
			} else if("jetty".equals(image.getPurpose())) {
				jettyImage = image.getUrl();
			}
		}
		
		map.clear();
		map.put("containerClusterName", gceCluster.getClusterName());
		map.put("componentType", gceType.toString().toLowerCase());
		map.put("image", gceServer.getGceImageName());
		map.put("networkMode", "ip");
		map.put("memory",gceServer.getMemorySize()!=null?String.valueOf(gceServer.getMemorySize()):CONTAINER_MEMORY_SIZE);
		if(StringUtils.isEmpty(gceServer.getGceImageName())) {
			if(isNginx) {
				map.put("image", nginxImage==null ? MATRIX_GCE_NGINX_DEFAULT_IMAGE : nginxImage);
			}else {
				map.put("image", jettyImage==null ? MATRIX_GCE_JETTY_DEFAULT_IMAGE : jettyImage);
			}
		}
		
		if(params.get("buyNum") != null && !isNginx) {
			map.put("nodeCount",String.valueOf(params.get("buyNum")));
		}
		if(params.get("netType") != null && !isNginx) {
			map.put("networkMode",(String) params.get("netType"));
		}
		
		if(isNginx)
			map.put("componentType", "nginx");
		
		
		
		ApiResultObject resultObject = this.gcePythonService.createContainer(map,host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(resultObject);
		
		tr.setParams(params);
		return tr;
	}
	
}