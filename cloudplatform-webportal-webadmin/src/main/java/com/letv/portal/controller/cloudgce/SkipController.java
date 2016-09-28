package com.letv.portal.controller.cloudgce;

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
@ClassAoLog(module="GCE管理")
@Controller("gceSkip")
public class SkipController {
	
	@RequestMapping(value ="/list/gce/image",method=RequestMethod.GET)
	public ModelAndView toDashboard(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudgce/gce_image_list");
		return mav;
	}
	
	@RequestMapping(value ="/list/gce/cluster",method=RequestMethod.GET)
	public ModelAndView toGceClusterList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudgce/gce_cluster_list");
		return mav;
	}
	
	@RequestMapping(value ="/list/gce/container",method=RequestMethod.GET)
	public ModelAndView toGceContainerList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudgce/gce_container_list");
		return mav;
	}
	
	@RequestMapping(value ="/list/gce",method=RequestMethod.GET)
	public ModelAndView toGceServerList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudgce/gce_list");
		return mav;
	}

	@RequestMapping(value ="/list/gce-version",method=RequestMethod.GET)
	public ModelAndView toGceVersionList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudgce/gce_version_list");
		return mav;
	}
	
	/**
	  * @Title: toGceClusterDetail
	  * @Description: 跳转到gce cluster详情页面
	  * @param gceClusterId
	  * @param mav
	  * @return ModelAndView   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年6月25日 下午1:54:48
	  */
	@RequestMapping(value="/detail/gce/cluster/{gceClusterId}", method=RequestMethod.GET)   
	public ModelAndView toGceClusterDetail(@PathVariable Long gceClusterId,ModelAndView mav) {
		mav.addObject("gceClusterId",gceClusterId);
		mav.setViewName("/cloudgce/gce_cluster_detail");
		return mav;
	}
}
