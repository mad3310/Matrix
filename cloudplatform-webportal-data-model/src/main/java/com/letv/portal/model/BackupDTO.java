package com.letv.portal.model;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.BackupType;

public class BackupDTO extends ApiResultObject {
	
	private BackupType backupType;

	public BackupDTO(String result, String url) {
		super(result, url);
	}

	public BackupType getBackupType() {
		return backupType;
	}

	public void setBackupType(BackupType backupType) {
		this.backupType = backupType;
	}

}
