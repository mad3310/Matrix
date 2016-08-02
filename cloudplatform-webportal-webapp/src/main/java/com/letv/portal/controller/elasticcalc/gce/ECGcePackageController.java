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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.jacksonext.annotation.ExcludeProperty;
import com.letv.common.util.jacksonext.annotation.JsonFilterProperties;
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;
import com.letv.portal.proxy.IGceProxy;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;

/**
 * 弹性计算GCE服务应用包对外接口
 * 
 * @author linzhanbo .
 * @since 2016年7月6日, 下午2:13:45 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/ecgce/packages")
public class ECGcePackageController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGcePackageController.class);
	
	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;
	@Autowired
	private IGceProxy gceProxy;

	@RequestMapping(value="/{gceId}",method = RequestMethod.GET)
	public @ResponseBody ResultObject list(@PathVariable("gceId") Long gceId,Page page,
			HttpServletRequest request, ResultObject obj) {
		Map<String, Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		params.put("gceId", gceId);
		logger.debug("查询GCE版本列表，参数" + params.toString());
		obj.setData(this.ecGcePackageService.selectPageByParams(page, params));
		return obj;
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
		try{
			gceProxy.uploadPackageNoWorkflow(file, gcePackage);
		} catch (ValidateException e) {
			callbackResult.setResult(0);
			callbackResult.addMsg(e.getMessage());
			return callbackResult;
		} catch (Exception e) {
			logger.error("上传应用部署包失败:" + e.getMessage(),e);
			callbackResult.setResult(0);
			callbackResult.addMsg("系统出现异常，请联系系统管理员!");
			return callbackResult;
		}
		callbackResult.setData(gcePackage);
		logger.debug("上传GCE应用部署包成功! GCE名称:{},版本号:{}", gcePackage.getGceName(),
				gcePackage.getVersion());
		return callbackResult;
	}
	
	@RequestMapping(value = "/deploy/{gcePackageId}", method = RequestMethod.GET)
	public @ResponseBody ResultObject deploy(@PathVariable Long gcePackageId,@RequestParam Long gceId,ResultObject callbackResult) {
		logger.debug("部署应用包");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", gcePackageId);
		map.put("gceId", gceId);
		map.put("createUser", this.sessionService.getSession().getUserId());
		List<EcGcePackage> ecGcePackages = ecGcePackageService.selectByMap(map);
		if(CollectionUtils.isEmpty(ecGcePackages) || ecGcePackages.size() > 1)
		{
			logger.error("部署包不存在");
			callbackResult.setResult(0);
			callbackResult.addMsg("部署包不存在");
			return callbackResult;
		}
		EcGcePackage ecGcePackage = ecGcePackages.get(0);
		try{
			gceProxy.deployGCE(ecGcePackage);
		} catch (Exception e) {
			logger.error("部署应用包失败:" + e.getMessage(),e);
			callbackResult.setResult(0);
			callbackResult.addMsg("系统出现异常，请联系系统管理员!");
			return callbackResult;
		}
		logger.debug("部署应用包成功! GCE Id:{},版本号:{}", ecGcePackage.getGceId(),
				ecGcePackage.getVersion());
		return callbackResult;
	}
}
