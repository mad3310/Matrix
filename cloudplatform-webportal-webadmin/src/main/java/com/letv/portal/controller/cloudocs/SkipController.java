package com.letv.portal.controller.cloudocs;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.result.ResultObject;
import com.letv.portal.service.adminoplog.ClassAoLog;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: gm <br>
 * Written Date: 2015年6月24日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@ClassAoLog(module="OCS管理")
@Controller("ocsSkip")
public class SkipController {
	
	@RequestMapping(value ="/list/ocs/cluster",method=RequestMethod.GET)
	public ModelAndView toOcsclusterList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudocs/ocs_cluster_list");
		return mav;
	}
	@RequestMapping(value ="/list/ocs/container",method=RequestMethod.GET)
	public ModelAndView toContainerList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudocs/ocs_container_list");
		return mav;
	}
	@RequestMapping(value="/detail/ocs/cluster/{clusterId}", method=RequestMethod.GET)
	public ModelAndView toOcsclusterDetail(@PathVariable Long clusterId,ModelAndView mav) {
		mav.addObject("clusterId",clusterId);
		mav.setViewName("/cloudocs/ocs_cluster_detail");
		return mav;
	}
	@RequestMapping(value="/list/bucket",method=RequestMethod.GET)
	public ModelAndView toOcsList(ModelAndView mav){
		mav.setViewName("/cloudocs/ocs_bucket_list");
		return mav;
	}
	@RequestMapping(value="/detail/bucket/{bucketId}",method=RequestMethod.GET)
	public ModelAndView toOcsDetail(@PathVariable Long bucketId,ModelAndView mav){
		mav.addObject("bucketId",bucketId);
		mav.setViewName("/cloudocs/ocs_bucket_detail");
		return mav;
	}
	@RequestMapping(value="/audit/bucket/{bucketId}",method=RequestMethod.GET)
	public ModelAndView toOcsAudit(@PathVariable Long bucketId, ModelAndView mav){
		mav.addObject("bucketId",bucketId);
		mav.setViewName("/cloudocs/ocs_bucket_audit");
		return mav;
	}
}
