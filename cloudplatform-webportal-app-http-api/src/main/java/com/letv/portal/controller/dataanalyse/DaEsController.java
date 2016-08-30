package com.letv.portal.controller.dataanalyse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.letv.common.exception.ApiNotFoundException;
import com.letv.common.exception.CommonException;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.jacksonext.annotation.ExcludeProperty;
import com.letv.common.util.jacksonext.annotation.JsonFilterProperties;
import com.letv.common.web.rest.BaseController;
import com.letv.portal.controller.cloudes.EsServerController;
import com.letv.portal.enumeration.HclusterStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.proxy.IEsProxy;
import com.letv.portal.proxy.IGceProxy;
import com.letv.portal.rest.enumeration.RestAPIFormatter;
import com.letv.portal.rest.exception.ApiException;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.es.IEsServerService;

@Controller
@RequestMapping("/daes")
public class DaEsController {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEsServerService esServerService;
	@Autowired
	private IEsProxy esProxy;
	@Resource
	private IHclusterService hclusterService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(EsServerController.class);
	
	
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody void save(@Valid EsServer esServer, BindingResult bindResult) {
		logger.debug("创建ES");
		if (bindResult.hasErrors()) {
			logger.warn("校验参数不合法");
			throw new ApiException(RestAPIFormatter.ParamsInvalid.formatErrorMessage(bindResult.getAllErrors()));
		}
		//直接从所有GCE集群中找一个集群用
		HclusterModel hclusterModel = new HclusterModel();
		hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
		hclusterModel.setType("ES");
		List<HclusterModel> hcModels = hclusterService.selectHclusterByStatus(hclusterModel);
		if(CollectionUtils.isEmpty(hcModels))
			throw new ApiException(RestAPIFormatter.NoEffectiveCluster);
		esServer.setHclusterId(hcModels.get(0).getId());
		//esServer.setHclusterId(48l);
		esServer.setCreateUser(this.sessionService.getSession().getUserId());
		try{
			this.esProxy.insertAndBuild(esServer);
		}catch(ValidateException ex){
			throw new ApiException(RestAPIFormatter.ServiceIsExist.formatErrorMessage(esServer.getEsName()));
		}
		logger.debug("创建ES成功! ID:{},Name:{}", esServer.getId(), esServer.getEsName());
	}
	
	@RequestMapping(value="/containers",method=RequestMethod.GET)   
	public @ResponseBody List<Map<String,String>> getContainers(String esName, ResultObject obj) {
		logger.debug("获取ES容器列表");
		if(StringUtils.isEmpty(esName)){
			logger.warn("ES名称为空");
			throw new ApiException(RestAPIFormatter.ParamsInvalid.formatErrorMessage("ES名称为空"));
		}
		EsServer esServer = new EsServer();
		esServer.setEsName(esName);
		esServer.setCreateUser(sessionService.getSession().getUserId());
		List<EsContainer> containers = null;
		try{
			containers = esProxy.getContainers(esServer);
		}catch(ValidateException ex){
			String errMsg = ex.getMessage();
			if(!StringUtils.isEmpty(errMsg)){
				if(errMsg.equals(MessageFormat.format("{0}应用不存在", esServer.getEsName()))){
					throw new ApiException(RestAPIFormatter.ServiceIsNonExist.formatErrorMessage(esName));
				}else if(errMsg.equals(MessageFormat.format("{0}应用不可用", esServer.getEsName()))){
					throw new ApiException(RestAPIFormatter.ServiceIsNonUse.formatErrorMessage(esName));
				}else if(errMsg.equals(MessageFormat.format("{0}应用部署失败", esServer.getEsName()))){
					throw new ApiException(RestAPIFormatter.ServiceIsDeployError.formatErrorMessage(esName));
				}else if(errMsg.equals(MessageFormat.format("{0}应用容器列表为空", esServer.getEsName()))){
					throw new ApiException(RestAPIFormatter.ContainersIsEmpty.formatErrorMessage(esName));
				}
			}
		}catch(CommonException ex){
			throw new ApiException(RestAPIFormatter.Deploying.formatErrorMessage(esName));
		}
		List<Map<String,String>> datas = new ArrayList<Map<String,String>>(containers.size());
		for(EsContainer container:containers){
			Map<String,String> map = new HashMap<String, String>(2);
			map.put("ipAddr", container.getIpAddr());
			map.put("containerName", container.getContainerName());
			datas.add(map);
		}
		logger.debug("获取ES容器列表成功! ES名称:{}",esName);
		return datas;
	}
	
}
