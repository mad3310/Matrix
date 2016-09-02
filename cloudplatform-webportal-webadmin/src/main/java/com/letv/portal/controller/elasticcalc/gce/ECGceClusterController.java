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
import com.letv.common.util.HttpUtil;
import com.letv.portal.service.adminoplog.ClassAoLog;
import com.letv.portal.service.elasticcalc.gce.IEcGceClusterService;

@ClassAoLog(module = "GCE管理/集群管理")
@Controller
@RequestMapping("/ecgce/cluster")
public class ECGceClusterController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGceClusterController.class);
	@Autowired
	private IEcGceClusterService ecGceClusterService;
	/**
	 * 获取集群列表
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
		obj.setData(this.ecGceClusterService.selectPageByParams(page, params));
		return obj;
	}
}
