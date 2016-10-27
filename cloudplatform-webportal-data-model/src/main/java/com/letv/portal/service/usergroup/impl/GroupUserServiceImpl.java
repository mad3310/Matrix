/**
 * Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 */
package com.letv.portal.service.usergroup.impl;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.usergroup.IGroupUserDao;
import com.letv.portal.model.usergroup.GroupUser;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.usergroup.IGroupUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户与组关联服务
 * @author linzhanbo .
 * @since 2016年10月26日, 11:30 .
 * @version 1.0 .
 */
@Service("groupUserService")
public class GroupUserServiceImpl extends BaseServiceImpl<GroupUser> implements IGroupUserService {
    private final static Logger logger = LoggerFactory.getLogger(GroupUserServiceImpl.class);
    @Autowired
    private IGroupUserDao groupUserDao;

    public GroupUserServiceImpl() {
        super(GroupUser.class);
    }

    @Override
    public IBaseDao<GroupUser> getDao() {
        return groupUserDao;
    }
}
