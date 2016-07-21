package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.DbModel;
import com.letv.portal.service.IDbService;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月8日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller("dbSkip")
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
		isAuthorityDb(dbId);
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
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/layout");
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
		mav.setViewName("/dashBoard");
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
		isAuthorityDb(dbId);
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
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/securityManager");
		return mav;
	}
	
	@RequestMapping(value ="/monitor/dbLink/{dbId}",method=RequestMethod.GET)
	public ModelAndView toMonitor(@PathVariable Long dbId,ModelAndView mav){
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/monitor/COMDML");
		return mav;
	}
	
	@RequestMapping(value ="/monitor/InnoDB/buffer/{dbId}",method=RequestMethod.GET)
	public ModelAndView toMonitorInnoDB(@PathVariable Long dbId,ModelAndView mav){
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/monitor/InnoDB");
		return mav;
	}
	
	@RequestMapping(value ="/monitor/QPS/TPS/{dbId}",method=RequestMethod.GET)
	public ModelAndView toMonitorQPS(@PathVariable Long dbId,ModelAndView mav){
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/monitor/QPS_TPS");
		return mav;
	}
	@RequestMapping(value ="/monitor/cpu/{dbId}",method=RequestMethod.GET)
	public ModelAndView tocpuUsed(@PathVariable Long dbId,ModelAndView mav){
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/monitor/cpuUsed");
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
	
	@RequestMapping(value ="/list/backup/{dbId}",method=RequestMethod.GET)
	public ModelAndView toDbBackup(@PathVariable Long dbId,ModelAndView mav){
		isAuthorityDb(dbId);
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/backupRecover");
		return mav;
	}
	@RequestMapping(value ="/toLogin")
	public ModelAndView toLogin(ModelAndView mav){
		StringBuffer buffer = new StringBuffer();
		buffer.append(OAUTH_AUTH_HTTP).append("/index?redirect_uri=").append(WEBPORTAL_LOCAL_HTTP).append("/oauth/callback");
		mav.addObject("loginURI", buffer.toString());
		mav.setViewName("/toLogin");
		return mav;
	}
	
	@RequestMapping(value ="/rds",method=RequestMethod.GET)
	public ModelAndView toRDS(@RequestParam(value="lang",required=false) String lang,ModelAndView mav){
		if(StringUtils.isEmpty(lang)){
			String defaultLang = "zh-cn";
			mav.setViewName("redirect:/rds?lang="+defaultLang);
		}else{
			mav.addObject("lang", lang);
			mav.setViewName("/rds/index");
		}
		return mav;
	}

	
	private void isAuthorityDb(Long dbId) {
		if(dbId == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", dbId);
		map.put("createUser", sessionService.getSession().getUserId());
		List<DbModel> dbs = this.dbService.selectByMap(map);
		if(dbs == null || dbs.isEmpty())
			throw new ValidateException("参数不合法");
	}
}
