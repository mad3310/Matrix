package com.letv.portal.controller.cloudes;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.proxy.IEsProxy;
import com.letv.portal.service.es.IEsServerService;

@Controller
@RequestMapping("/es")
public class EsServerController {
	
	@Autowired
	private IEsServerService esServerService;
	@Autowired
	private IEsProxy esProxy;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(EsServerController.class);
	
	
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(@Valid EsServer esServer, BindingResult bindResult, ResultObject obj) {
		logger.debug("创建ES");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}
		//界面赋值
		esServer.setHclusterId(48l);
		esServer.setCreateUser(this.sessionService.getSession().getUserId());
		this.esProxy.insertAndBuild(esServer);
		logger.debug("创建ES成功! ID:{},Name:{}", esServer.getId(), esServer.getEsName());
		return obj;
	}
	
	
}
