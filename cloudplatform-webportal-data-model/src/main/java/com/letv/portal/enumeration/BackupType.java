package com.letv.portal.enumeration;

public enum BackupType implements ByteEnum {
	NONE(0),
	FULL(1),
	INCR(2);
	
	private final Integer value;
	
	private BackupType(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(BackupType.FULL);
		System.out.println(BackupType.FULL.name());
	}

}
