package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.DbModel;
import com.letv.portal.proxy.IDbUserProxy;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/dbIp")
public class DbIpController {
	
	@Resource
	private IDbUserService dbUserService;
	@Resource
	private IDbUserProxy dbUserProxy;
	@Autowired
	private IDbService dbService;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	private final static Logger logger = LoggerFactory.getLogger(DbIpController.class);
		
	@RequestMapping(value="/{dbId}", method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(@PathVariable Long dbId,ResultObject obj) {
		if(null == dbId) {
			throw new ValidateException("参数不能为空");
		}
		obj.setData(this.dbUserService.selectIpsFromUser(dbId));
		return obj;
	}
	/**
	 * 查询ip是否被其他数据库用户使用，删除之前检查
	 * @param dbId
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/checkIp", method=RequestMethod.POST)   
	public @ResponseBody ResultObject checkIp(Long dbId, String ip, ResultObject obj) {
		if(null == dbId || null == ip) {
			throw new ValidateException("参数不合法");
		}
		obj.setData(this.dbUserService.checkIp(dbId, ip));
		return obj;
	}
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(Long dbId,String ips,ResultObject obj) {
		if(null == dbId || StringUtils.isNullOrEmpty(ips)) {
			throw new ValidateException("参数不能为空");
		} else {
			isAuthorityDb(dbId);
			this.dbUserProxy.saveOrUpdateIps(dbId,ips);
		}
		return obj;
	}
	
	@RequestMapping(value="/{dbId}/{username}", method=RequestMethod.GET)   
	public @ResponseBody ResultObject ips4dbUser(@PathVariable Long dbId,@PathVariable String username,ResultObject obj) {
		if(null == dbId) {
			throw new ValidateException("参数不能为空");
		} else {
			obj.setData(this.dbUserService.selectMarkIps4dbUser(dbId,username));
			return obj;
		}
	}
	
	private void isAuthorityDb(Long dbId) {
		if(null == dbId)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", dbId);
		map.put("createUser", sessionService.getSession().getUserId());
		List<DbModel> dbs = this.dbService.selectByMap(map);
		if(null == dbs  || dbs.isEmpty())
			throw new ValidateException("参数不合法");
	}
}
