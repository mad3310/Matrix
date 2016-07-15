package com.letv.portal.controller.cloudimage;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.letv.portal.service.adminoplog.ClassAoLog;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转 
 * @author name: lisuxiao <br>
 * Written Date: 2015年7月1日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@ClassAoLog(module="通用管理/基础镜像管理")
@Controller("imageSkip")
public class SkipController {
	
	@RequestMapping(value ="/list/image",method=RequestMethod.GET)
	public ModelAndView toMclusterList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/cloudimage/image_list");
		return mav;
	}
	
}
