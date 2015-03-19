package com.letv.portal.controller.cloudgce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.exception.ValidateException;
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
@Controller("gceSkip")
public class SkipController {
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IDbService dbService;
	
	/**
	 * Methods Name: gceList<br>
	 * Description: 跳转gce列表
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/list/gce",method=RequestMethod.GET)
	public ModelAndView toSlbList(ModelAndView mav){
		mav.setViewName("/cloudgce/gceList");
		return mav;
	}
	
	/**Methods Name: gceDetail <br>
	 * Description: 跳转至gce详情<br>
	 * @author name: yaokuo
	 * @param slbId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value ="/detail/gce/{gceId}",method=RequestMethod.GET)
	public ModelAndView gceDetail(@PathVariable Long gceId,ModelAndView mav){
		//isAuthorityDb(slbId);
		mav.addObject("gceId",gceId);
		mav.setViewName("/cloudgce/layout");
		return mav;
	}
	
	/**
	 * Methods Name: gceCreate<br>
	 * Description: 跳转gce创建页面
	 * @author name: yaokuo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value ="/detail/gceCreate",method=RequestMethod.GET)
	public ModelAndView toGceCreate(ModelAndView mav){
		mav.setViewName("/cloudgce/gceCreate");
		return mav;
	}
	
	@RequestMapping(value ="/detail/gceBaseInfo/{gceId}",method=RequestMethod.GET)
	public ModelAndView toGceCreate(@PathVariable Long gceId,ModelAndView mav){
		mav.addObject("gceId",gceId);
		mav.setViewName("/cloudgce/baseInfo");
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
