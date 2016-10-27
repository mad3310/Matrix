/**
 * Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 */
package com.letv.portal.controller.usergroup;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.model.usergroup.GroupUser;
import com.letv.portal.proxy.IGroupProxy;
import com.letv.portal.service.usergroup.IGroupService;
import com.letv.portal.service.usergroup.IGroupUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户组对外接口
 * @author linzhanbo .
 * @since 2016年10月26日, 10:41 .
 * @version 1.0 .
 */
@Controller
@RequestMapping("/group")
public class GroupController {
    private final static Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired(required = false)
    private SessionServiceImpl sessionService;
    @Autowired
    private IGroupUserService groupUserService;
    @Autowired
    private IGroupProxy groupProxy;
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public @ResponseBody ResultObject list(Page page,HttpServletRequest request, ResultObject obj) {
        Map<String, Object> params = HttpUtil.requestParam2Map(request);
        params.put("ownerId", sessionService.getSession().getUserId());
        String userName = (String) params.get("userName");
        if (!StringUtils.isEmpty(userName))
            params.put("userName", StringUtil.transSqlCharacter(userName));
        logger.debug("查询组用户列表，参数" + params.toString());
        obj.setData(this.groupUserService.queryByPagination(page, params));
        return obj;
    }
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public @ResponseBody ResultObject addUser(@RequestParam("userId") Long userId, ResultObject obj) {
        if(null == userId){
            obj.setResult(0);
            obj.addMsg("用户ID为空");
            return obj;
        }
        logger.debug("用户组增加用户，参数" + userId);
        Long ownerId = sessionService.getSession().getUserId();
        groupProxy.insertUser(userId, ownerId);
        return obj;
    }
    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public @ResponseBody ResultObject delUser(@RequestParam("userId") Long userId, ResultObject obj) {
        if(null == userId){
            obj.setResult(0);
            obj.addMsg("用户ID为空");
            return obj;
        }
        logger.debug("用户组删除用户，参数" + userId);
        Long ownerId = sessionService.getSession().getUserId();
        groupProxy.deleteUser(userId, ownerId);
        return obj;
    }
}