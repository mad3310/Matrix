package com.letv.portal.proxy.impl;


import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.common.exception.ValidateException;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.model.task.service.ITaskEngine;
import com.letv.portal.proxy.IEsProxy;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.es.IEsServerService;

@Component
public class EsProxyImpl extends BaseProxyImpl<EsServer> implements IEsProxy{
	
	private final static Logger logger = LoggerFactory.getLogger(EsProxyImpl.class);
	
	@Autowired
	private IEsServerService esServerService;
	@Autowired
	private ITaskEngine taskEngine;
	@Autowired
	private IHostService hostService;
	
	
	@Override
	public void insertAndBuild(EsServer esServer) {	
		if(null == esServer)
			throw new ValidateException("参数不合法");
		
		//参数转换防止XSS跨站漏洞
		esServer.setEsName(StringEscapeUtils.escapeHtml(esServer.getEsName()));
		esServer.setDescn(StringEscapeUtils.escapeHtml(esServer.getDescn()));
		
		Map<String,Object> params = this.esServerService.insertEsServerAndCluster(esServer);
		
		this.taskEngine.run("ES_BUY", params);
	}


	@Override
	public IBaseService<EsServer> getService() {
		return esServerService;
	}

	
}
