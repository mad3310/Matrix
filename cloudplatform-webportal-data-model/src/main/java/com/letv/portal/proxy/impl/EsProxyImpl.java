package com.letv.portal.proxy.impl;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
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
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	
	@Override
	public void insertAndBuild(EsServer esServer) {
		//1.参数转换防止XSS跨站漏洞
		esServer.setEsName(StringEscapeUtils.escapeHtml(esServer.getEsName()));
		esServer.setDescn(StringEscapeUtils.escapeHtml(esServer.getDescn()));
		//2.校验该用户下ES名称是否已经存在
		Map<String,Object> exParams = new HashMap<String,Object>();
		exParams.put("esName", esServer.getEsName());
		exParams.put("createUser", this.sessionService.getSession().getUserId());
		Integer existLength = this.esServerService.selectByMapCount(exParams);
		if(existLength>0){
			throw new ValidateException(MessageFormat.format("{0}应用已存在", esServer.getEsName()));
		}
		//4.保存ES和ES集群信息
		Map<String,Object> params = this.esServerService.insertEsServerAndCluster(esServer);
		//5.走创建ES流程
		this.taskEngine.run("ES_BUY", params);
	}


	@Override
	public IBaseService<EsServer> getService() {
		return esServerService;
	}

	
}
