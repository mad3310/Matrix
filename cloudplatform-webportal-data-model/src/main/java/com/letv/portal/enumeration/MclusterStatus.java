package com.letv.portal.enumeration;

public enum MclusterStatus implements ByteEnum{
	DEFAULT(0),
	RUNNING(1),  
	BUILDDING(2),
	BUILDFAIL(3),
	AUDITFAIL(4),
	STARTING(7),
	STOPPING(8),
	STOPED(9),
	DESTROYING(10),
	DESTROYED(11),
	NOTEXIT(12),
	DANGER(14),
	CRISIS(15);
	
	private final Integer value;
	
	private MclusterStatus(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
}
