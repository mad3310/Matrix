/**
 * Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 */
package com.letv.portal.service.usergroup.impl;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.usergroup.IGroupDao;
import com.letv.portal.model.usergroup.Group;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.usergroup.IGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户组服务
 * @author linzhanbo .
 * @since 2016年10月26日, 11:18 .
 * @version 1.0 .
 */
@Service("groupService")
public class GroupServiceImpl extends BaseServiceImpl<Group> implements IGroupService {
    private final static Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    @Autowired
    private IGroupDao groupDao;

    public GroupServiceImpl() {
        super(Group.class);
    }

    @Override
    public IBaseDao<Group> getDao() {
        return groupDao;
    }
}
