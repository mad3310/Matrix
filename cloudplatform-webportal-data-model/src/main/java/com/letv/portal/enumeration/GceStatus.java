package com.letv.portal.enumeration;

public enum GceStatus implements ByteEnum{
	DEFAULT(0),
	RUNNING(1),  
	BUILDDING(2),
	BUILDFAIL(3),
	AUDITFAIL(4),
	ABNORMAL(5),
	NORMAL(6),
	//TODO 以后去掉上面状态
	AVAILABLE(7),
	NOTAVAILABLE(8);
	;
	
	private final Integer value;
	
	private GceStatus(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
}
