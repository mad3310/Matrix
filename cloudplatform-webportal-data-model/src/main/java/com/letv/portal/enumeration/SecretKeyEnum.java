package com.letv.portal.enumeration;


public enum SecretKeyEnum {

	MONITOR("e47179301b5f4e7bb3610db3e95eeaf4");

	private String key;

	private SecretKeyEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
}