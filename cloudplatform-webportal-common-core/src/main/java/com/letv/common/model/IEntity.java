package com.letv.common.model;

import java.io.Serializable;
import java.sql.Timestamp;

public interface IEntity extends Cloneable, Serializable {
	Serializable getId();
	
	Timestamp getCreateTime();
	
	void setCreateTime(Timestamp createTime);
	
	Long getCreateUser();
	
	void setCreateUser(Long createUser);
	
	Timestamp getUpdateTime();
	
	void setUpdateTime(Timestamp updateTime);
	
	Long getUpdateUser();
	
	void setUpdateUser(Long updateUser);
}
