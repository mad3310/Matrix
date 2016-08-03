package com.letv.portal.controller.cloudes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.model.es.EsServer;
import com.letv.portal.proxy.IEsProxy;
import com.letv.portal.service.es.IEsContainerService;
import com.letv.portal.service.es.IEsServerService;

@Controller
@RequestMapping("/es")
public class EsController {
	
	@Autowired
	private IEsServerService esServerService;
	@Autowired
	private IEsProxy esProxy;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEsContainerService esContainerService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(EsController.class);
	
	
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		String esName = (String) params.get("esName");
		if(!StringUtils.isEmpty(esName))
			params.put("esName", StringUtil.transSqlCharacter(esName));
		obj.setData(this.esServerService.queryByPagination(page, params));
		return obj;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public @ResponseBody ResultObject detail(@PathVariable Long id){
		ResultObject obj = new ResultObject();
		EsServer es = this.esServerService.selectById(id);
		obj.setData(es);
		return obj;
	}
	
	/**
	  * @Title: list
	  * @Description: 根据esClusterId获取相关esContainer列表
	  * @param gceClusterId
	  * @param result
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  */
	@RequestMapping(value="/{esClusterId}/containers",method=RequestMethod.GET)
	public @ResponseBody ResultObject list(@PathVariable Long esClusterId,ResultObject result) {
		result.setData(this.esContainerService.selectContainersByEsClusterId(esClusterId));
		return result;
	}
	
}
