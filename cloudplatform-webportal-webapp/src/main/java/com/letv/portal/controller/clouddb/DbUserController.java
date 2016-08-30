package com.letv.portal.controller.clouddb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.proxy.IDbUserProxy;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.mysql.jdbc.StringUtils;

/**Program Name: DbUserController <br>
 * Description:  db用户<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月13日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller
@RequestMapping("/dbUser")
public class DbUserController {
	
	@Resource
	private IDbUserService dbUserService;
	@Resource
	private IDbService dbService;
	
	@Resource
	private IDbUserProxy dbUserProxy;
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	private final static Logger logger = LoggerFactory.getLogger(DbUserController.class);
		
	/**Methods Name: list <br>
	 * Description: dbUser列表<br>
	 * @author name: liuhao1 20141225
	 * @param dbId
	 * @return
	 */
	@RequestMapping(value="/{dbId}", method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(@PathVariable Long dbId,ResultObject obj) {
		isAuthorityDb(dbId);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dbId", dbId);
		List<DbUserModel> dbUsers = this.dbUserService.selectGroupByName(params);
		obj.setData(dbUsers);
		return obj;
	}

    /**
     * 创建数据库用户
     * @param dbUserModel
     * @param types
     * @param ips
     * @param obj
     * @return
     */
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody ResultObject save(DbUserModel dbUserModel,String types,String ips,ResultObject obj) {
		isAuthorityDb(dbUserModel.getDbId());
		if(!this.dbUserService.isLegalDbUserName(dbUserModel.getUsername(), dbUserModel.getDbId())) {
			throw new ValidateException("账号名称包含关键字或与数据库同名");
		}
		if(StringUtils.isNullOrEmpty(types) || StringUtils.isNullOrEmpty(ips)|| types.contains("undefined") || ips.contains("undefined")) {
			throw new ValidateException("参数不合法");
		}
		//XSS跨站漏洞
		dbUserModel.setDescn(StringEscapeUtils.escapeHtml(dbUserModel.getDescn()));
		this.dbUserProxy.saveAndBuild(dbUserModel,ips,types);
		return obj;
	}

    /**
     * 修改数据库用户密码
     * @param dbId
     * @param username
     * @param password
     * @param obj
     * @return
     */
	@RequestMapping(value="/security/{username}",method=RequestMethod.POST)
	public  @ResponseBody ResultObject updateUserPwd(Long dbId,String username,String password,ResultObject obj) {
		if(dbId == null || StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)) {
			throw new ValidateException("参数不能为空");
		}
		isAuthorityDb(dbId);
		this.dbUserProxy.updateSecurity(dbId,username,password);
		return obj;
	}

	/**Methods Name: updateDbUser <br>
	 * Description: 权限修改（增加ip、修改权限）<br>
	 * @author name: liuhao1
	 * @param dbUserModel
	 * @param types
	 * @param ips
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/authority/{username}",method=RequestMethod.POST)
	public @ResponseBody ResultObject updateUserAuthority(DbUserModel dbUserModel,String types,String ips,ResultObject obj) {
		if(StringUtils.isNullOrEmpty(types) || StringUtils.isNullOrEmpty(ips) || types.contains("undefined") || ips.contains("undefined")) {
			throw new ValidateException("参数不合法");
		}
		isAuthorityDb(dbUserModel.getDbId());
		this.dbUserProxy.updateUserAuthority(dbUserModel,ips,types);
		return obj;
	}

    /**
     * 修改数据库用户描述
     * @param dbUserModel
     * @param obj
     * @return
     */
	@RequestMapping(value="/descn/{username}",method=RequestMethod.POST)
	public @ResponseBody ResultObject updateUserDescn(DbUserModel dbUserModel,ResultObject obj) {
		if(StringUtils.isNullOrEmpty(dbUserModel.getUsername()) || dbUserModel.getDbId() == null) {
			throw new ValidateException("参数不能为空");
		}
		isAuthorityDb(dbUserModel.getDbId());
		this.dbUserService.updateDescnByUsername(dbUserModel);
		return obj;
	}

	/**
	 * Methods Name: validate <br>
	 * Description: 校验dbUser用户是否存在
	 * @author name: wujun
	 * @param dbUserModel
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/validate",method=RequestMethod.POST)
	public @ResponseBody Map<String,Object> validate(DbUserModel dbUserModel,HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		List<DbUserModel> list = this.dbUserService.selectByIpAndUsername(dbUserModel);
		map.put("valid", list.size()>0?false:true);
		return map;
	}
	/**
	 * Methods Name: deleteDbUserById <br>
	 * Description: 删除dbUser用户
	 * @author name: wujun
	 * @param dbUserId
	 * @param dbUserModel
	 * @return
	 */
	@RequestMapping(value="/{dbUserId}",method=RequestMethod.DELETE)
	public  @ResponseBody ResultObject deleteDbUserById(@PathVariable String dbUserId,DbUserModel dbUserModel) {
		ResultObject obj = new ResultObject();
		isAuthorityDbUser(Long.parseLong(dbUserId));
		this.dbUserProxy.deleteDbUser(dbUserId);
		return obj;
	}
	@RequestMapping(value="/{dbId}/{username}",method=RequestMethod.DELETE)
	public  @ResponseBody ResultObject deleteDbUserById(@PathVariable Long dbId,@PathVariable String username,ResultObject obj) {
		if(null == dbId || StringUtils.isNullOrEmpty(username)) {
			throw new ValidateException("参数不能为空");
		}
		isAuthorityDb(dbId);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dbId", dbId);
		map.put("username", username);
		this.dbUserProxy.deleteAndBuild(dbId,username);
		return obj;
	}

	private void isAuthorityDb(Long dbId) {
		if(dbId == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", dbId);
		map.put("createUser", sessionService.getSession().getUserId());
		List<DbModel> dbs = this.dbService.selectByMap(map);
		if(dbs == null || dbs.isEmpty())
			throw new ValidateException("参数不合法");
	}
	
	private void isAuthorityDbUser(Long dbUserId) {
		if(dbUserId == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", dbUserId);
		map.put("createUser", sessionService.getSession().getUserId());
		List<DbUserModel> dbUsers = this.dbUserService.selectByMap(map);
		if(dbUsers == null || dbUsers.isEmpty())
			throw new ValidateException("参数不合法");
	}
	
}
