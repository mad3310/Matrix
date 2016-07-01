/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.controller.elasticcalc.gce;

import javax.validation.Valid;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.controller.cloudoss.OssServerController;
import com.letv.portal.model.elasticcalc.gce.EcGce;
import com.letv.portal.model.elasticcalc.gce.EcGceExt;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.proxy.IGceProxy;

/**
 * 弹性计算GCE服务对外接口
 * 
 * @author linzhanbo .
 * @since 2016年6月28日, 上午9:43:14 .
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
	private IGceProxy gceProxy;

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResultObject createGce(
			@Valid @ModelAttribute EcGce gce,BindingResult bindResult, @ModelAttribute EcGceExt gceExt,
			ResultObject callbackResult) {//bindResult必须紧跟valid的gce，这样SpringMVC才会对gce的validate结果传给bindResult
		logger.debug("创建GCE");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}

		gce.setCreateUser(this.sessionService.getSession().getUserId());
		if (gceExt != null && (gceExt.getOcsId().longValue() != 0L)
				&& (gceExt.getRdsId().longValue() != 0L)) {
			gceExt.setCreateUser(this.sessionService.getSession().getUserId());
		}
		// TODO 未指定地域和可用区ID
		gce.setAreaId(7L);
		gce.setHclusterId(48L);
		gceProxy.createGce(gce, gceExt);
		callbackResult.setData(gce);
		logger.debug("创建GCE成功! ID:" + gce.getId() + ",Name:" + gce.getGceName());
		return callbackResult;
	}

	@RequestMapping(value = "/uploadPackage", method = RequestMethod.POST)
	public @ResponseBody ResultObject uploadPackage(
			@RequestParam MultipartFile file, @Valid EcGcePackage gcePackage,
			BindingResult bindResult, ResultObject callbackResult) {
		logger.debug("上传应用部署包");
		if (bindResult.hasErrors()) {
			logger.error("校验参数不合法");
			return new ResultObject(bindResult.getAllErrors());
		}
		gcePackage.setCreateUser(this.sessionService.getSession().getUserId());
		try {
			gceProxy.uploadPackage(file,gcePackage);
		} catch (Exception e) {
			e.printStackTrace();
			callbackResult.setResult(0);
			callbackResult.setData(e.getMessage());
			return callbackResult;
		}
		callbackResult.setData(gcePackage);
		return callbackResult;
	}

}
