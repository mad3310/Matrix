package com.letv.portal.controller.cloudes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
@Controller("elasticsearch")
public class SkipController {
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEcGceService ecGceService;
	
	@RequestMapping(value ="/elasticsearch",method=RequestMethod.GET)
	public ModelAndView toES(@RequestParam(value="lang",required=false) String lang,ModelAndView mav){
		if(StringUtils.isEmpty(lang)){
			String defaultLang = "zh-cn";
			mav.setViewName("redirect:/elasticsearch?lang="+defaultLang);
		}else{
			mav.addObject("lang", lang);
			mav.setViewName("/elasticsearch/index");
		}
		return mav;
	}
	
}
