package com.letv.portal.controller.cloudes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.compiler.ast.Pair;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.CommonException;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.enumeration.HclusterStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.proxy.IEsProxy;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.es.IEsServerService;

@Controller
@RequestMapping("/es")
public class EsServerController {
	
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
	public @ResponseBody Map<String,Object> save(@Valid EsServer esServer, BindingResult bindResult, ResultObject obj) {
		logger.debug("创建ES");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			//重新new一个，无所谓了
			obj = new ResultObject(bindResult.getAllErrors());
			return callbackData(obj);
		}
		//直接从所有GCE集群中找一个集群用
		HclusterModel hclusterModel = new HclusterModel();
		hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
		hclusterModel.setType("ES");
		List<HclusterModel> hcModels = hclusterService.selectHclusterByStatus(hclusterModel);
		if(CollectionUtils.isEmpty(hcModels))
			throw new ValidateException("无可用集群");
		esServer.setHclusterId(hcModels.get(0).getId());
		//esServer.setHclusterId(48l);
		esServer.setCreateUser(this.sessionService.getSession().getUserId());
		try{
			this.esProxy.insertAndBuild(esServer);
		}catch(ValidateException ex){
			logger.error(ex.getMessage());
			obj.setResult(0);
			obj.addMsg(ex.getMessage());
			return callbackData(obj);
		}
		Map<String,String> datas = new HashMap<String, String>(1);
		datas.put("esName", esServer.getEsName());
		obj.setData(datas);
		logger.debug("创建ES成功! ID:{},Name:{}", esServer.getId(), esServer.getEsName());
		return callbackData(obj);
	}
	
	public Map<String,Object> callbackData(ResultObject obj){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("result", obj.getResult());
		map.put("data", obj.getData());
		map.put("alertMessage", obj.getAlertMessage());
		map.put("msgs", obj.getMsgs());
		return map;
	}
	@RequestMapping(value="/containers",method=RequestMethod.GET)   
	public @ResponseBody Map<String,Object> getContainers(String esName, ResultObject obj) {
		logger.debug("获取ES容器列表");
		if(StringUtils.isEmpty(esName)){
			logger.error("ES名称为空");
			obj.setResult(0);
			obj.addMsg("ES名称为空");
			return callbackData(obj);
		}
		EsServer esServer = new EsServer();
		esServer.setEsName(esName);
		esServer.setCreateUser(sessionService.getSession().getUserId());
		List<EsContainer> containers = null;
		try{
			containers = esProxy.getContainers(esServer);
		}catch(ValidateException ex){
			obj.setResult(0);
			obj.addMsg(ex.getMessage());
			return callbackData(obj);
		}catch(CommonException ex){
			obj.setResult(3);
			obj.addMsg(ex.getMessage());
			return callbackData(obj);
		}
		List<Map<String,String>> datas = new ArrayList<Map<String,String>>(containers.size());
		for(EsContainer container:containers){
			Map<String,String> map = new HashMap<String, String>(2);
			map.put("ipAddr", container.getIpAddr());
			map.put("containerName", container.getContainerName());
			datas.add(map);
		}
		obj.setData(datas);
		logger.debug("获取ES容器列表成功! ES名称:{}",esName);
		return callbackData(obj);
	}
	
}
