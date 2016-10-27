package com.letv.portal.enumeration;

public enum HclusterStatus implements ByteEnum{

	DEFAULT(0),
	RUNNING(1),  //运行中
	BUILDDING(2),
	BUILDFAIL(3),
	AUDITFAIL(4),
	ABNORMAL(5),
	NORMAL(6),
	FORBID(15);//禁止
	
	private final Integer value;
	
	private HclusterStatus(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
}
