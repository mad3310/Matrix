package com.letv.portal.clouddb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.service.IDbService;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月8日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller
public class SkipController {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IDbService dbService;
	
	@Value("${oauth.auth.http}")
	private String OAUTH_AUTH_HTTP;
	@Value("${webportal.local.http}")
	private String WEBPORTAL_LOCAL_HTTP;
	/**
	 * Methods Name: dbInfo<br>
	 * Description: 跳转基本信息页面
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/detail/baseInfo/{dbId}",method=RequestMethod.GET)
	public ModelAndView tobaseInfo(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/baseInfo");
		return mav;
	}
	/**Methods Name: dbDetail <br>
	 * Description: 跳转至db详情<br>
	 * @author name: liuhao1
	 * @param dbId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value ="/detail/db/{dbId}",method=RequestMethod.GET)
	public ModelAndView dbDetail(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/layout");
		return mav;
	}

	/**
	 * Methods Name: dbInfo<br>
	 * Description: 跳转数据库列表
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/list/db",method=RequestMethod.GET)
	public ModelAndView toDbList(ModelAndView mav){
		mav.setViewName("/clouddb/dbList");
		return mav;
	}
	/**
	 * Methods Name: dashBoard<br>
	 * Description: 跳转dashBoard
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/dashboard",method=RequestMethod.GET)
	public ModelAndView toDashBoard(ModelAndView mav){
		mav.setViewName("/clouddb/dashBoard");
		return mav;
	}
	/**
	 * Methods Name: accountManager<br>
	 * Description: 跳转用户管理
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/detail/account/{dbId}",method=RequestMethod.GET)
	public ModelAndView toAccountManager(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/accountManager");
		return mav;
	}
	/**
	 * Methods Name: securityManager<br>
	 * Description: 跳转安全管理
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/detail/security/{dbId}",method=RequestMethod.GET)
	public ModelAndView toSecurityManager(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/securityManager");
		return mav;
	}
	
	@RequestMapping(value ="/monitor/dbLink/{dbId}",method=RequestMethod.GET)
	public ModelAndView toMonitor(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/monitor/dbLink");
		return mav;
	}
	/**
	 * Methods Name: dbCreate<br>
	 * Description: 跳转数据库创建页面
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/detail/dbCreate",method=RequestMethod.GET)
	public ModelAndView toDbCreate(ModelAndView mav){
		mav.setViewName("/clouddb/dbCreate");
		return mav;
	}
	
	@RequestMapping(value ="/jettyMonitor",method=RequestMethod.GET)
	public @ResponseBody ResultObject jettyMonitor(ResultObject obj){
		return obj;
	}
	
	@RequestMapping(value ="/list/backup/{dbId}",method=RequestMethod.GET)
	public ModelAndView toDbBackup(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/backupRecover");
		return mav;
	}
	@RequestMapping(value ="/toLogin",method=RequestMethod.GET)
	public ModelAndView toLogin(ModelAndView mav){
		StringBuffer buffer = new StringBuffer();
		buffer.append(OAUTH_AUTH_HTTP).append("/index?redirect_uri=").append(WEBPORTAL_LOCAL_HTTP).append("/oauth/callback");
		mav.addObject("loginURI", buffer.toString());
		mav.setViewName("/toLogin");
		return mav;
	}
}
