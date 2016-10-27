/**
 * Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 */
package com.letv.portal.proxy.impl;

import com.letv.common.exception.ValidateException;
import com.letv.portal.model.UserModel;
import com.letv.portal.model.usergroup.Group;
import com.letv.portal.model.usergroup.GroupUser;
import com.letv.portal.proxy.IGroupProxy;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.usergroup.IGroupService;
import com.letv.portal.service.usergroup.IGroupUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户组代理
 * @author linzhanbo .
 * @since 2016年10月26日, 11:15 .
 * @version 1.0 .
 */
@Component
public class GroupProxyImpl extends BaseProxyImpl<Group> implements IGroupProxy {
    private final static Logger logger = LoggerFactory.getLogger(GroupProxyImpl.class);

    @Autowired
    private IGroupService groupService;
    @Autowired
    private IGroupUserService groupUserService;
    @Resource
    private IUserService userService;
    @Override
    public IBaseService<Group> getService() {
        return groupService;
    }

    @Override
    public void insertUser(Long userId, Long ownerId) {
        //判断该用户是否存在
        UserModel userModel = userService.selectById(userId);
        if(null == userModel)
            throw new ValidateException("用户不存在");
        //判断是否已经添加用户
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("userId",userId);
        params.put("ownerId", ownerId);
        List<GroupUser> users = groupUserService.selectByMap(params);
        if (!CollectionUtils.isEmpty(users) && users.size() > 0)
            throw new ValidateException(MessageFormat.format("用户{0}已经存在",users.get(0).getUserInfo().getEmail()));
        //如果group不存在，先创建group，已经存在，则使用该组
        params.remove("ownerId");
        params.put("userId",ownerId);
        List<Group> groups = groupService.selectByMap(params);
        Group group = new Group();
        if(CollectionUtils.isEmpty(groups)){
            group.setName("我的组织");
            group.setUserId(ownerId);
            groupService.insert(group);
        }else
            group = groups.get(0);
        //插入用户
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupId(group.getId());
        groupUser.setOwnerId(ownerId);
        groupUser.setUserId(userId);
        groupUserService.insert(groupUser);
    }

    @Override
    public void deleteUser(Long userId, Long ownerId) {
        //判断用户是否存在
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("userId",userId);
        params.put("ownerId", ownerId);
        //判断该用户是否存在
        UserModel userModel = userService.selectById(userId);
        if(null == userModel)
            throw new ValidateException("用户不存在");
        List<GroupUser> users = groupUserService.selectByMap(params);
        if (CollectionUtils.isEmpty(users))
            throw new ValidateException(MessageFormat.format("用户{0}已移除",userModel.getEmail()));
        groupUserService.delete(users.get(0));
    }
}
