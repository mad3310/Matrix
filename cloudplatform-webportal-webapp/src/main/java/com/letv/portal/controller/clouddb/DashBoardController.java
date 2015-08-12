package com.letv.portal.controller.clouddb;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.proxy.IDashBoardProxy;
import com.letv.portal.service.openstack.OpenStackSession;

@Controller
@RequestMapping("/dashboard")
public class DashBoardController {

	@Resource
	private IDashBoardProxy dashBoardProxy;

	@Resource
	private SessionServiceImpl sessionService;

	private final static Logger logger = LoggerFactory
			.getLogger(DashBoardController.class);

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public @ResponseBody ResultObject list(ResultObject result) {
		Map<String, Integer> appResource = this.dashBoardProxy
				.selectAppResource();
		int totalVMNumber = ((OpenStackSession) sessionService.getSession()
				.getOpenStackSession()).getVMManager().totalNumber();
		appResource.put("vm", totalVMNumber);
		result.setData(appResource);
		return result;
	}

	@RequestMapping(value="/monitor/db/storage",method=RequestMethod.GET)
	public @ResponseBody ResultObject dbStorage(ResultObject result) {
		result.setData(this.dashBoardProxy.selectDbStorage());
		return result;
	}
	@RequestMapping(value="/monitor/db/connect",method=RequestMethod.GET)
	public @ResponseBody ResultObject dbConnect(ResultObject result) {
		result.setData(this.dashBoardProxy.selectDbConnect());
		return result;
	}
}
