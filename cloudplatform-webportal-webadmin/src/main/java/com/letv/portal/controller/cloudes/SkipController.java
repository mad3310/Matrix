package com.letv.portal.controller.cloudes;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.letv.portal.service.adminoplog.ClassAoLog;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月8日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@ClassAoLog(module="ES管理")
@Controller("esSkip")
public class SkipController {
	
	@RequestMapping(value ="/list/es/cluster_list",method=RequestMethod.GET)
	public ModelAndView toEsClusterList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/elasticsearch/cluster_list");
		return mav;
	}
	
	@RequestMapping(value="/detail/cluster/{clusterId}", method=RequestMethod.GET)   
	public ModelAndView toEsDetail(@PathVariable Long clusterId,ModelAndView mav) {
		mav.addObject("clusterId",clusterId);
		mav.setViewName("/elasticsearch/cluster_detail");
		return mav;
	}
	
	@RequestMapping(value ="/list/es/container_list",method=RequestMethod.GET)
	public ModelAndView toEsContainers(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/elasticsearch/container_list");
		return mav;
	}
	
	@RequestMapping(value="/detail/es/container/{containerId}", method=RequestMethod.GET)   
	public ModelAndView toEsContainerDetail(@PathVariable Long containerId,ModelAndView mav) {
		mav.addObject("containerId",containerId);
		mav.setViewName("/elasticsearch/container_detail");
		return mav;
	}

}
