package com.letv.portal.controller.common;

import javax.servlet.http.HttpServletRequest;

import com.letv.portal.service.adminoplog.ClassAoLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.result.ResultObject;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月8日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@ClassAoLog(module = "通用管理")
@Controller("commonSkip")
public class SkipController {
	
	@RequestMapping(value ="/list/zk",method=RequestMethod.GET)
	public ModelAndView toDashboard(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/common/zk_list");
		return mav;
	}
	
	@RequestMapping(value ="/list/timingTask",method=RequestMethod.GET)
	public ModelAndView toTimingTask(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/common/timing_task_list");
		return mav;
	}
    
    @RequestMapping(value ="/list/baseImages",method=RequestMethod.GET)
	public ModelAndView toBaseImages(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/common/base_image_list");
		return mav;
	}

    @RequestMapping(value ="/list/dictMgr",method=RequestMethod.GET)
	public ModelAndView toDictMgr(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/common/dict_mgr");
		return mav;
	}
    	
    @RequestMapping(value ="/operation-logs",method=RequestMethod.GET)
	public ModelAndView toOperationLogs(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/common/operation_logs");
		return mav;
	}
}
