package com.letv.portal.controller.clouddb;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.UserModel;
import com.letv.portal.service.IUserService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
	@Resource
	private IUserService  userService;
	@Autowired
	private SessionServiceImpl sessionService;

	
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@RequestMapping(value="/{userId}", method=RequestMethod.GET)   
	public @ResponseBody ResultObject getUserById(@PathVariable Long userId) {
		ResultObject obj = new ResultObject();
		obj.setData(this.userService.getUserById(userId));
		return obj;
	}
	
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject getUserBySession(ResultObject obj) {
		Long userId = this.sessionService.getSession().getUserId();
		UserModel u = this.userService.getUserById(userId);
		obj.setData(u);
		return obj;
	}
	@RequestMapping(value = "/email/{emailAddr}",method=RequestMethod.GET)
	public @ResponseBody ResultObject getUsers(@PathVariable String emailAddr,ResultObject obj) {
		logger.debug("使用邮箱地址匹配查询所有用户信息");
		if(StringUtils.isEmpty(emailAddr)){
			return obj;
		}
		obj.setData(userService.selectByEmail(emailAddr));
		return obj;
	}
}
