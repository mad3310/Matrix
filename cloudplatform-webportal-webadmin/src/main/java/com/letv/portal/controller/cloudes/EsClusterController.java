package com.letv.portal.controller.cloudes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.portal.service.es.IEsClusterService;

@Controller
@RequestMapping("/es/cluster")
public class EsClusterController {
	
	@Autowired
	private IEsClusterService esClusterService;
	
	private final static Logger logger = LoggerFactory.getLogger(EsClusterController.class);
	

	/**
	 * 获取es集群列表
	 * @param page
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.esClusterService.queryByPagination(page, params));
		return obj;
	}
	
	
}
