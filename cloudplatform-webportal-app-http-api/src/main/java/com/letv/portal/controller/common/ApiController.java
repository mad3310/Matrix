/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.controller.common;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.UserModel;
import com.letv.portal.rest.enumeration.RestAPIFormatter;
import com.letv.portal.rest.exception.ApiException;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.IUserService;

/**
 * 服务对外接口
 * 
 * @author lisuxiao .
 * @since 2016年9月5日, 下午17:11:14 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/api")
public class ApiController {
	
	private final static Logger logger = LoggerFactory.getLogger(ApiController.class);

	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Resource
	private IContainerService containerService;
	@Resource
	private IHclusterService hclusterService;
	@Resource
	private IUserService userService;


	/**
	 * 获取所有容器信息
	 * @return
	 */
	@RequestMapping(value = "/hcluster", method = RequestMethod.GET)
	public @ResponseBody List<HclusterModel> getVaildVipContainers(HttpServletRequest request) {
		logger.debug("查询所有物理机集群");
		Long userId = this.sessionService.getSession().getUserId();
		if (!authorization(userId)) {
			logger.info("{}无权访问该接口", userId);
			throw new ApiException(RestAPIFormatter.Unauthorized.formatErrorMessage("无权访问该接口"));
		}
		List<HclusterModel> result = hclusterService.selectByMap(null);
		logger.debug("查询所有物理机集群成功! 访问用户:{}", userId);
		return result;
	}
	
	private boolean authorization(Long userId) {
		UserModel u = userService.getUserById(userId);
		return u.isAdmin();
	}

}
