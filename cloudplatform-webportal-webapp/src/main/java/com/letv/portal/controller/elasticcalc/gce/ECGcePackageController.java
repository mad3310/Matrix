/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.controller.elasticcalc.gce;

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
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;

/**
 * 弹性计算GCE服务应用包对外接口
 * 
 * @author linzhanbo .
 * @since 2016年7月6日, 下午2:13:45 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/ecgce/packages")
public class ECGcePackageController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGcePackageController.class);
	
	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResultObject list(Page page,
			HttpServletRequest request, ResultObject obj) {
		Map<String, Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		logger.debug("查询GCE版本列表，参数" + params.toString());
		obj.setData(this.ecGcePackageService.selectPageByParams(page, params));
		return obj;
	}
}
