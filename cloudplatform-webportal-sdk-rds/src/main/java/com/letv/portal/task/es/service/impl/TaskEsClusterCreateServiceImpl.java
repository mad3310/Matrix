package com.letv.portal.task.es.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.es.EsCluster;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.model.image.Image;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IEsPythonService;
import com.letv.portal.service.image.IImageService;

@Service("taskEsClusterCreateService")
public class TaskEsClusterCreateServiceImpl extends BaseTask4EsServiceImpl implements IBaseTaskService{
	
	@Autowired
	private IEsPythonService esPythonService;
	@Autowired
	private IImageService imageService;
	//容器大小
	public static final String  CONTAINER_MEMORY_SIZE = "4294967296";//4G
	private static final String  CONTAINER_DBDISK_SIZE = "10737418240";
	private static final String DOCKER_SHELL_MEMORY_SIZE = "1073741824";//1G
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEsClusterCreateServiceImpl.class);
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		logger.debug("创建ES集群");
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
			
		EsCluster esCluster = super.getEsCluster(params);
		HostModel host = super.getHost(esCluster.getHclusterId());
		EsServer esServer = super.getEsServer(params);
		
		//从数据库获取image
		Map<String,String> map = new HashMap<String,String>();
		map.put("dictionaryName", "ES");
		map.put("purpose", "default");
		map.put("isUsed", "1");
		List<Image> images = this.imageService.selectByMap(map);
		if(CollectionUtils.isEmpty(images) || images.size()!=1)
			throw new ValidateException("get Image had error, params :" + map.toString());
		
		map.clear();
		map.put("containerClusterName", esCluster.getClusterName());
		map.put("componentType", "elasticsearch");
		long container_size = 0l;
		//容器内存=ES服务内存大小+Docker shell内存大小，用户购买ES指定内存是指定ES服务内存大小。
		if(esServer.getMemorySize()==null)
			container_size = Long.parseLong(CONTAINER_MEMORY_SIZE);
		else
			container_size = esServer.getMemorySize();
		container_size += Long.parseLong(DOCKER_SHELL_MEMORY_SIZE);
		map.put("memory", String.valueOf(container_size));
		if(esServer.getNodeCount() != null && esServer.getNodeCount() > 0)
			map.put("nodeCount",String.valueOf(esServer.getNodeCount()));
		map.put("networkMode", "ip");
		map.put("image", images.get(0).getUrl());
		map.put("dbDisk", params.get("dbDisk")==null || params.get("dbDisk")=="" ? CONTAINER_DBDISK_SIZE : String.valueOf(params.get("dbDisk")));
		
		ApiResultObject result = this.esPythonService.createContainer(map,host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(result);
		if (tr.isSuccess()) {
			logger.debug("请求创建ES集群成功");
		}
		tr.setParams(params);
		return tr;
	}
	
}
