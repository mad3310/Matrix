package com.letv.portal.controller.cloudslb;

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
@ClassAoLog(module="SLB管理")
@Controller("slbSkip")
public class SkipController {
	
	@RequestMapping(value ="/list/slb/cluster",method=RequestMethod.GET)
	public ModelAndView toMclusterList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudslb/slb_cluster_list");
		return mav;
	}
	@RequestMapping(value="/detail/slb/cluster/{clusterId}", method=RequestMethod.GET)
	public ModelAndView toMclusterDetail(@PathVariable Long clusterId,ModelAndView mav) {
		mav.addObject("clusterId",clusterId);
		mav.setViewName("/cloudslb/slb_cluster_detail");
		return mav;
	}
	@RequestMapping(value ="/list/slb/container",method=RequestMethod.GET)
	public ModelAndView toContainerList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudslb/slb_container_list");
		return mav;
	}
	@RequestMapping(value ="/list/slb",method=RequestMethod.GET)
	public ModelAndView toSlbList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudslb/slb_list");
		return mav;
	}

}
