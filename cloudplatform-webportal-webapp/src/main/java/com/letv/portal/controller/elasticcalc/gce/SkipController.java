package com.letv.portal.controller.elasticcalc.gce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月8日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller("ecGceSkip")
public class SkipController {
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEcGceService ecGceService;
	
	/**
	 * Methods Name: gceList<br>
	 * Description: 跳转gce列表
	 * @author name: yaokuo
	 * @param mav
	 * @return
	 */
	@RequestMapping(value ="/list/ecgce",method=RequestMethod.GET)
	public ModelAndView toEcSlbList(ModelAndView mav){
		mav.setViewName("/elasticcalc/gce/gceList");
		return mav;
	}
	
	/**Methods Name: gceDetail <br>
	 * Description: 跳转至gce详情<br>
	 * @author name: yaokuo
	 * @param gceId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value ="/detail/ecgce/{gceId}",method=RequestMethod.GET)
	public ModelAndView ecgceDetail(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		EcGce ecGce = ecGceService.selectById(gceId);
		mav.addObject(ecGce);
		mav.setViewName("/elasticcalc/gce/layout");
		return mav;
	}
	
	/**
	 * Methods Name: gceCreate<br>
	 * Description: 跳转gce创建页面
	 * @author name: yaokuo
	 * @param mav
	 * @return
	 */
	@RequestMapping(value ="/detail/ecgceCreate",method=RequestMethod.GET)
	public ModelAndView toEcGceCreate(ModelAndView mav){
		mav.setViewName("/elasticcalc/gce/gceCreate");
		return mav;
	}
	
	@RequestMapping(value ="/detail/ecgceBaseInfo/{gceId}",method=RequestMethod.GET)
	public ModelAndView toEcGceBaseInfo(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		mav.addObject("gceId",gceId);
		mav.setViewName("/elasticcalc/gce/baseInfo");
		return mav;
	}
	
	@RequestMapping(value ="/detail/versionManger/{gceId}",method=RequestMethod.GET)
	public ModelAndView toEcVersionManger(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		EcGce ecGce = ecGceService.selectById(gceId);
		mav.addObject(ecGce);
		mav.setViewName("/elasticcalc/gce/versionManger");
		return mav;
	}
	
	@RequestMapping(value ="/monitor/ecgce/cpu/{gceId}",method=RequestMethod.GET)
	public ModelAndView toEcCpuUsed(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		mav.addObject("gceId",gceId);
		mav.setViewName("/elasticcalc/gce/monitor/cpu");
		return mav;
	}
	@RequestMapping(value ="/monitor/ecgce/network/{gceId}",method=RequestMethod.GET)
	public ModelAndView toEcNetwork(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		mav.addObject("gceId",gceId);
		mav.setViewName("/elasticcalc/gce/monitor/network");
		return mav;
	}
	@RequestMapping(value ="/monitor/ecgce/memory/{gceId}",method=RequestMethod.GET)
	public ModelAndView toEcMemory(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		mav.addObject("gceId", gceId);
		mav.setViewName("/elasticcalc/gce/monitor/memory");
		return mav;
	}
	@RequestMapping(value ="/monitor/ecgce/disk/{gceId}",method=RequestMethod.GET)
	public ModelAndView toEcDisk(@PathVariable Long gceId,ModelAndView mav){
		isAuthorityGce(gceId);
		mav.addObject("gceId",gceId);
		mav.setViewName("/elasticcalc/gce/monitor/disk");
		return mav;
	}
	
	private void isAuthorityGce(Long gceId) {
		if(gceId == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", gceId);
		map.put("createUser", sessionService.getSession().getUserId());
		List<EcGce> gces = this.ecGceService.selectByMap(map);
		if(CollectionUtils.isEmpty(gces))
			throw new ValidateException("参数不合法");
	}
}
