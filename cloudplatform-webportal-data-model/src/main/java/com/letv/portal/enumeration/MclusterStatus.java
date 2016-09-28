package com.letv.portal.enumeration;

public enum MclusterStatus implements ByteEnum{
	DEFAULT(0),
	RUNNING(1),  
	BUILDDING(2),
	BUILDFAIL(3),
	AUDITFAIL(4),//审核失败
	STARTING(7),
	STOPPING(8),
	STOPED(9),
	DESTROYING(10),//集群删除中
	DESTROYED(11),//集群已删除
	DESTROYFAILED(19),//集群删除失败
	NOTEXIT(12),
	DANGER(13),
	CRISIS(14),
	ADDING(15),//扩容中
    ADDINGFAILED(16),//扩容失败
	DELETING(17),//缩容中
    DELETINGFAILED(18);//缩容失败
	
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
