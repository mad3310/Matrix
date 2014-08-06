package com.letv.mms.model;

public class BaseModel implements IEntity,ISoftDelete{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5822755615614280336L;
	
	private Long id;
	
	private boolean deleted;

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;		
	}

	@Override
	public Long getId() {
		return id;
	}

	@Deprecated
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	
}
