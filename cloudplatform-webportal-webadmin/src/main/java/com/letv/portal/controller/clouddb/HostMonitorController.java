package com.letv.portal.controller.clouddb;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.portal.service.IMonitorService;
/**
 * 物理机监控
 * @author lisuxiao
 *
 */
@Controller
@RequestMapping("/monitor/host")
public class HostMonitorController {
	
	@Resource
	private IMonitorService monitorService;
	
	/**
	 * 从es取出物理机磁盘监控
	 * @param hostId
	 * @param chartId
	 * @param strategy
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/{hostId}/{chartId}/{strategy}", method=RequestMethod.GET)
	public @ResponseBody ResultObject getHostDiskMonitorFromES(@PathVariable Long hostId, @PathVariable Long chartId, @PathVariable Integer strategy, ResultObject result) {
		result.setData(this.monitorService.getHostDiskMonitorData(hostId, chartId, strategy));
		return result;
	}

}
