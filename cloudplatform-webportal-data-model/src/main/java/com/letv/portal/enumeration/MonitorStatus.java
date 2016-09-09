package com.letv.portal.enumeration;

public enum MonitorStatus implements ByteEnum{
	
	NORMAL(0),//正常
	GENERAL(1),//异常 
	SERIOUS(2),//危险 
	CRASH(3),//宕机
	TIMEOUT(4),//超时
	EXCEPTION(5),//解析出错
	STRUCTERROR(6);//存在反例
	private final Integer value;

	private MonitorStatus(Integer value) {
		this.value = value;
	}
	@Override
	public Integer getValue() {
		 return value;
	}
}
