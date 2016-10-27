/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.usergroup;

import com.letv.common.model.BaseModel;
import com.letv.portal.model.UserModel;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Group extends BaseModel {
    /**
     * 组名
     */
    private String name;

    /**
     * 组描述
     */
    private String descn;

    /**
     * 创建人ID
     */
    private Long userId;
    private UserModel createUserModel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescn() {
        return descn;
    }

    public void setDescn(String descn) {
        this.descn = descn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserModel getCreateUserModel() {
        return createUserModel;
    }

    public void setCreateUserModel(UserModel createUserModel) {
        this.createUserModel = createUserModel;
    }
}