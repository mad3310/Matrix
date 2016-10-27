/**
 * Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 */
package com.letv.portal.proxy;

import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.usergroup.Group;

/**
 * IGroupProxy
 * @author linzhanbo .
 * @since 2016年10月26日, 10:46 .
 * @version 1.0 .
 */
public interface IGroupProxy extends IBaseProxy<Group> {
    /**
     * 增加用户
     * @param userId    被添加的人的ID
     * @param ownerId   拥有该组的人的ID
     */
    void insertUser(Long userId, Long ownerId);

    /**
     * 删除用户
     * @param userId    被删除的人的ID
     * @param ownerId   拥有该组的人的ID
     */
    void deleteUser(Long userId, Long ownerId);
}
