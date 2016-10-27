package com.letv.common.model;

import java.sql.Timestamp;

/**Program Name: BaseModel <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月7日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class BaseModel implements IEntity,ISoftDelete{

	private static final long serialVersionUID = 5822755615614280336L;
	
	private Long id;
	
	private boolean deleted;
	
	private Timestamp createTime;
	private Long createUser;
	private Timestamp updateTime;
	private Long updateUser;

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;		
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getCreateTime() {
		return createTime;
	}
	
	@Override
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	@Override
	public Long getCreateUser() {
		return createUser;
	}
	
	@Override
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	
	@Override
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	
	@Override
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public Long getUpdateUser() {
		return updateUser;
	}
	
	@Override
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
}
