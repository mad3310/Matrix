package com.letv.portal.controller.cloudes;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.portal.service.es.IEsContainerService;

@Controller
@RequestMapping("/es/container")
public class EsContainerController {
	
	@Resource
	private IEsContainerService esContainerService;
	
	private final static Logger logger = LoggerFactory.getLogger(EsContainerController.class);
	
	
	
	/**
	 * 根据container id获取container详细
	 * @param containerId
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/{containerId}",method=RequestMethod.GET)
	public @ResponseBody ResultObject list(@PathVariable Long containerId,ResultObject result) {
		result.setData(this.esContainerService.selectById(containerId));
		return result;
	}
	
	/**
	  * @Title: list
	  * @Description: 获取指定页数指定条数的esContainer列表
	  * @param page
	  * @param request
	  * @param obj
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.esContainerService.queryByPagination(page, params));
		return obj;
	}
	
}
