package com.letv.portal.controller.cloudes;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEsServerService esServerService;
	@Autowired
	private IEsProxy esProxy;
	
	
	private final static Logger logger = LoggerFactory.getLogger(EsServerController.class);
	
	
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(EsServer esServer, ResultObject obj) {
		if(null == esServer || StringUtils.isEmpty(esServer.getEsName())){
			throw new ValidateException("参数不合法");
		}
		esServer.setCreateUser(this.sessionService.getSession().getUserId());
		this.esProxy.insertAndBuild(esServer);
		return obj;
	}
	
	
}
