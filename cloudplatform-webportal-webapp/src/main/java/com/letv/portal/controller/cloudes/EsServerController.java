package com.letv.portal.controller.cloudes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
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
		esServer.setCreateUser(this.sessionService.getSession().getUserId());
		Long esServerId = this.esProxy.insertAndBuild(esServer);
		obj.setData(esServerId);
		logger.debug("创建ES成功! ID:{},Name:{}", esServer.getId(), esServer.getEsName());
		return obj;
	}
	
	
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		String esName = (String) params.get("esName");
		if(!StringUtils.isEmpty(esName))
			params.put("esName", StringUtil.transSqlCharacter(esName));
		obj.setData(this.esServerService.queryByPagination(page, params));
		return obj;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public @ResponseBody ResultObject detail(@PathVariable Long id){
		isAuthorityEs(id);
		ResultObject obj = new ResultObject();
		EsServer es = this.esServerService.selectByIdWithContainers(id);
		obj.setData(es);
		return obj;
	}
	
	
	private void isAuthorityEs(Long id) {
		if(id == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", id);
		map.put("createUser", sessionService.getSession().getUserId());
		List<EsServer> gces = this.esServerService.selectByMap(map);
		if(gces == null || gces.isEmpty())
			throw new ValidateException("参数不合法");
	}
	
}
