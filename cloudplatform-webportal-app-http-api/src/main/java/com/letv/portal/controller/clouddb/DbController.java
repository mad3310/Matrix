/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.controller.clouddb;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.portal.model.UserModel;
import com.letv.portal.rest.enumeration.RestAPIFormatter;
import com.letv.portal.rest.exception.ApiException;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IUserService;

/**
 * db服务对外接口
 * 
 * @author lisuxiao .
 * @since 2016年9月5日, 下午17:11:14 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/db")
public class DbController {
	
	private final static Logger logger = LoggerFactory.getLogger(DbController.class);

	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Resource
	private IContainerService containerService;
	@Resource
	private IUserService userService;


	/**
	 * 获取有效的vip容器信息(带分页)
	 * @param page
	 * @param request
	 * @param callbackResult
	 * @return
	 */
	@RequestMapping(value = "/validvipcontainers", method = RequestMethod.GET)
	public @ResponseBody ResultObject getVaildVipContainers(Page page, HttpServletRequest request, ResultObject callbackResult) {
		logger.debug("获取DB valid vip container");
		Long userId = this.sessionService.getSession().getUserId();
		if (!authorization(userId)) {
			logger.info("{}无权访问该接口", userId);
			throw new ApiException(RestAPIFormatter.Unauthorized.formatErrorMessage("无权访问该接口"));
		}
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		if(null == params.get("recordsPerPage")) {//默认返回1万条数据
			page.setRecordsPerPage(10000);
		}
		Page result = containerService.queryVaildVipContainersByPagination(page, params);
		callbackResult.setData(result);
		logger.debug("获取DB容器列表成功! 访问用户:{}", userId);
		return callbackResult;
	}
	
	@RequestMapping(value = "/containers", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> getVaildVipContainers(Long hclusterId) {
		logger.debug("查询物理机集群下的vip容器");
		if(null == hclusterId) {
			throw new ApiException(RestAPIFormatter.ParamsInvalid.formatErrorMessage("hclusterId不能为空"));
		}
		Long userId = this.sessionService.getSession().getUserId();
		if (!authorization(userId)) {
			logger.info("{}无权访问该接口", userId);
			throw new ApiException(RestAPIFormatter.Unauthorized);
		}
		List<Map<String, Object>> result = containerService.queryVaildVipContainersByHclusterId(hclusterId);
		logger.debug("查询物理机集群下的vip容器成功! 访问用户:{}", userId);
		return result;
	}
	
	@RequestMapping(value = "/containers/vip", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> getAllVipContainers(Long hclusterId) {
		logger.debug("查询物理机集群下的所有vip容器");
		if(null == hclusterId) {
			throw new ApiException(RestAPIFormatter.ParamsInvalid.formatErrorMessage("hclusterId不能为空"));
		}
		Long userId = this.sessionService.getSession().getUserId();
		if (!authorization(userId)) {
			logger.info("{}无权访问该接口", userId);
			throw new ApiException(RestAPIFormatter.Unauthorized);
		}
		List<Map<String, Object>> result = containerService.queryAllVipContainersByHclusterId(hclusterId);
		logger.debug("查询物理机集群下的所有vip容器成功! 访问用户:{}", userId);
		return result;
	}
	
	@RequestMapping(value = "/containers/data", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> getAllDataContainers(Long hclusterId) {
		logger.debug("查询物理机集群下的所有data容器");
		if(null == hclusterId) {
			throw new ApiException(RestAPIFormatter.ParamsInvalid.formatErrorMessage("hclusterId不能为空"));
		}
		Long userId = this.sessionService.getSession().getUserId();
		if (!authorization(userId)) {
			logger.info("{}无权访问该接口", userId);
			throw new ApiException(RestAPIFormatter.Unauthorized);
		}
		List<Map<String, Object>> result = containerService.queryAllDataContainersByHclusterId(hclusterId);
		logger.debug("查询物理机集群下的所有data容器成功! 访问用户:{}", userId);
		return result;
	}
	
	private boolean authorization(Long userId) {
		UserModel u = userService.getUserById(userId);
		return u.isAdmin();
	}

}
