package com.letv.portal.controller.elasticcalc.gce;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.portal.service.adminoplog.ClassAoLog;
import com.letv.portal.service.elasticcalc.gce.IEcGceImageService;
import com.letv.portal.service.elasticcalc.gce.IEcGcePackageService;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;

@ClassAoLog(module = "GCE管理/应用管理")
@Controller
@RequestMapping("/ecgce")
public class ECGceController {
	private final static Logger logger = LoggerFactory
			.getLogger(ECGceController.class);
	@Autowired
	private IEcGceService ecGceService;
	@Autowired
	private IEcGcePackageService ecGcePackageService;
	@Autowired
	private IEcGceImageService ecGceImageService;
	/**
	 * 获取GCE列表
	 * @param page
	 * @param request
	 * @param obj
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月1日, 上午10:15:41 .
	 * @version 1.0 .
	 */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.ecGceService.selectPageByParams(page, params));
		return obj;
	}
	/**
	 * GCE详情
	 * @param id
	 * @param obj
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月1日, 下午6:27:25 .
	 * @version 1.0 .
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject detail(@PathVariable Long id,ResultObject obj) {
		obj.setData(this.ecGceService.selectById(id));
		return obj;
	}
	/**
	 * 获取GCE应用的所有版本
	 * @param id
	 * @param page
	 * @param request
	 * @param obj
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月1日, 下午3:00:22 .
	 * @version 1.0 .
	 */
	@RequestMapping(value="/package/{id}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject listPackages(@PathVariable Long id,Page page,HttpServletRequest request,ResultObject obj) {
		if(id == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("gceId", id);
		obj.setData(this.ecGcePackageService.selectPageByParams(page, params));
		return obj;
	}
	/**
	 * 版本包详情
	 * @param id
	 * @param obj
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月1日, 下午6:27:25 .
	 * @version 1.0 .
	 */
	@RequestMapping(value="/package/detail/{gcePackageId}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject detailPackage(@PathVariable Long gcePackageId,@RequestParam Long gceId,ResultObject obj) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", gcePackageId);
		params.put("gceId", gceId);
		obj.setData(this.ecGcePackageService.selectByMap(params));
		return obj;
	}
	/**
	 * GCE所有镜像
	 * @param id
	 * @param page
	 * @param request
	 * @param obj
	 * @return
	 * @author linzhanbo .
	 * @since 2016年9月1日, 下午6:27:36 .
	 * @version 1.0 .
	 */
	@RequestMapping(value="/image/{id}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject listImages(@PathVariable Long id,Page page,HttpServletRequest request,ResultObject obj) {
		if(id == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("gceId", id);
		obj.setData(this.ecGceImageService.selectPageByParams(page, params));
		return obj;
	}
}
