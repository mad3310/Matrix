package com.letv.portal.controller.clouddb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.letv.portal.service.adminoplog.ClassAoLog;

@ClassAoLog(ignore=true)
@Controller
public class BaseController {
	@RequestMapping(value="/403",method=RequestMethod.GET)   
	public String noAuthor(HttpServletRequest request,HttpServletResponse response) {
		return "/error/403";
	}
	@RequestMapping(value="/404",method=RequestMethod.GET)   
	public String notFound(HttpServletRequest request,HttpServletResponse response) {
		return "/error/404";
	}
	@RequestMapping(value="/500",method=RequestMethod.GET)   
	public String webportalException(HttpServletRequest request,HttpServletResponse response) {
		return "/error/500";
	}
	@RequestMapping(value="/browserError",method=RequestMethod.GET)   
	public String browserErrorException(HttpServletRequest request,HttpServletResponse response) {
		return "/error/browserError";
	}
}
