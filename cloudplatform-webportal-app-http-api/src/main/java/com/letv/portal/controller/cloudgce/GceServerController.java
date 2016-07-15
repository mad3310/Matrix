package com.letv.portal.controller.cloudgce;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.proxy.IGceProxy;
import com.letv.portal.service.gce.IGceServerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/gce")
public class GceServerController {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IGceServerService gceServerService;
	@Autowired
	private IGceProxy gceProxy;

	private final static Logger logger = LoggerFactory.getLogger(GceServerController.class);
	
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		String gceName = (String) params.get("gceName");
		if(!StringUtils.isEmpty(gceName))
			params.put("gceName", StringUtil.transSqlCharacter(gceName));
		obj.setData(this.gceServerService.selectPageByParams(page, params));
		return obj;
	}
	
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(@Valid @ModelAttribute GceServer gceServer,BindingResult result,Long rdsId,Long ocsId,ResultObject object) {
		if(result.hasErrors())
			return new ResultObject(result.getAllErrors());
		gceServer.setCreateUser(this.sessionService.getSession().getUserId());
		this.gceProxy.saveAndBuild(gceServer,rdsId,ocsId);
		return new ResultObject();
	}

}
