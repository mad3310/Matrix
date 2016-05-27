package com.letv.portal.model.monitor;

import java.io.Serializable;


public class BaseMonitor implements Serializable {
	
	private static final long serialVersionUID = -4453351061673231608L;
	
	private int result;
	
	public BaseMonitor() {
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
}
