package com.letv.portal.enumeration;

public enum DbStatus implements ByteEnum{
	DEFAULT(0),
	RUNNING(1),  
	BUILDDING(2),
	BUILDFAIL(3),
	AUDITFAIL(4),
	ABNORMAL(5),
	NORMAL(6),
	DESTROYING(7),
	DESTROYFAILED(8);
	
	private final Integer value;
	
	private DbStatus(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
}
