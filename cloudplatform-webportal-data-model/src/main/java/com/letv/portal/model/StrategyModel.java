package com.letv.portal.model;

import java.util.Date;

public class StrategyModel {

	// 保留天数
	private int reserveDays;
	// 备份周期
	private String backupPeriod;
	// 备份时间，最后一次成功备份时间
	private Date backupDate;
	// 预计下次备份时间
	private Date futureBackupDate;
	
	public int getReserveDays() {
		return reserveDays;
	}
	public void setReserveDays(int reserveDays) {
		this.reserveDays = reserveDays;
	}
	public String getBackupPeriod() {
		return backupPeriod;
	}
	public void setBackupPeriod(String backupPeriod) {
		this.backupPeriod = backupPeriod;
	}
	public Date getBackupDate() {
		return backupDate;
	}
	public void setBackupDate(Date backupDate) {
		this.backupDate = backupDate;
	}
	public Date getFutureBackupDate() {
		return futureBackupDate;
	}
	public void setFutureBackupDate(Date futureBackupDate) {
		this.futureBackupDate = futureBackupDate;
	}
	
	
	
	
}
