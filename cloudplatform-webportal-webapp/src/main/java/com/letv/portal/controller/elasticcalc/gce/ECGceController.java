/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.controller.elasticcalc.gce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.proxy.IGceProxy;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;

/**
 * 弹性计算GCE服务对外接口
 * @author linzhanbo .
 * @since 2016年7月5日, 下午2:08:18 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/ecgce")
public class ECGceController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGceController.class);
	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEcGceService ecGceService;
	@Autowired
	private IGceProxy gceProxy;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResultObject list(Page page,
			HttpServletRequest request, ResultObject obj) {
		Map<String, Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		String gceName = (String) params.get("gceName");
		if (!StringUtils.isEmpty(gceName))
			params.put("gceName", StringUtil.transSqlCharacter(gceName));
		logger.debug("查询GCE列表，参数" + params.toString());
		obj.setData(this.ecGceService.queryByPagination(page, params));
		return obj;
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResultObject createGce(
			@Valid @ModelAttribute EcGce gce, BindingResult bindResult,
			@ModelAttribute EcGceExt gceExt, ResultObject callbackResult) {// bindResult必须紧跟valid的gce，这样SpringMVC才会对gce的validate结果传给bindResult
		logger.debug("创建GCE");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}

		gce.setCreateUser(this.sessionService.getSession().getUserId());
		if (gceExt != null) {
			if(gceExt.getOcsId()!=null && gceExt.getOcsId().longValue() != 0l && gceExt.getRdsId()!=null && gceExt.getRdsId().longValue() != 0L)
				gceExt.setCreateUser(this.sessionService.getSession().getUserId());
		}
		// TODO 未指定地域
		gce.setAreaId(7L);
		try {
			gceProxy.createGce(gce, gceExt);
		} catch (ValidateException e) {
			callbackResult.setResult(0);
			callbackResult.addMsg(e.getMessage());
			return callbackResult;
		} catch (Exception e) {
			logger.error("创建GCE失败:" + e.getMessage(),e);
			callbackResult.setResult(0);
			callbackResult.addMsg("系统出现异常，请联系系统管理员!");
			return callbackResult;
		}
		callbackResult.setData(gce);
		logger.debug("创建GCE成功! ID:{},Name:{}", gce.getId(), gce.getGceName());
		return callbackResult;
	}

	// FIXME 待测试
	@RequestMapping(value = "/restart", method = RequestMethod.POST)
	public @ResponseBody ResultObject restart(Long id, ResultObject obj) {
		isAuthorityGce(id);
		this.gceProxy.restart(id);
		return obj;
	}

	// FIXME 待测试
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public @ResponseBody ResultObject start(Long id, ResultObject obj) {
		isAuthorityGce(id);
		this.gceProxy.start(id);
		return obj;
	}

	// FIXME 待测试
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public @ResponseBody ResultObject stop(Long id, ResultObject obj) {
		isAuthorityGce(id);
		this.gceProxy.stop(id);
		return obj;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResultObject detail(@PathVariable Long id) {
		isAuthorityGce(id);
		ResultObject obj = new ResultObject();
		EcGce gce = this.ecGceService.selectById(id);
		obj.setData(gce);
		return obj;
	}

	@RequestMapping(value = "/ext/{id}", method = RequestMethod.GET)
	public @ResponseBody ResultObject getGceExtByGceId(@PathVariable Long id) {
		isAuthorityGce(id);
		ResultObject obj = new ResultObject();
		EcGceExt gceExt = this.ecGceService.selectGceExtByGceId(id);
		if (gceExt != null)
			obj.setData(gceExt);
		return obj;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody ResultObject delete(@PathVariable Long id,
			ResultObject obj) {
		List<EcGce> gces = isAuthorityGce(id);
		EcGce gce = gces.get(0);
		//TODO	以后需要添加级联删除关联的业务数据
		this.ecGceService.delete(gce);
		return obj;
	}

	private List<EcGce> isAuthorityGce(Long id) {
		if (id == null)
			throw new ValidateException("参数不合法");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("createUser", sessionService.getSession().getUserId());
		List<EcGce> gces = this.ecGceService.selectByMap(map);
		if (gces == null || gces.isEmpty())
			throw new ValidateException("GCE不存在");
		return gces;
	}
}
