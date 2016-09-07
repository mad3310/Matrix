package com.letv.portal.controller.elasticcalc.gce;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.letv.common.util.HttpUtil;
import com.letv.portal.service.adminoplog.ClassAoLog;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;

@ClassAoLog(module = "GCE管理/容器管理")
@Controller
@RequestMapping("/ecgce/container")
public class ECGceContainerController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGceContainerController.class);
	@Autowired
	private IEcGceContainerService ecGceContainerService;
	/**
	 * 获取容器列表
	 * @param page
	 * @param request
	 * @param obj
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月1日, 上午10:15:41 .
	 * @version 1.0 .
	 */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.ecGceContainerService.queryByPagination(page, params));
		return obj;
	}
	@RequestMapping(value="/{id}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject detail(@PathVariable Long id,ResultObject obj) {
		obj.setData(this.ecGceContainerService.selectById(id));
		return obj;
	}
}
