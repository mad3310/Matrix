package com.letv.portal.proxy;

import java.util.Map;

import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.MclusterModel;

/**Program Name: IBackupProxy <br>
 * Description:  数据库db备份<br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年1月4日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBackupProxy extends IBaseProxy<BackupResultModel> {

	/**Methods Name: backupTask <br>
	 * Description: 备份任务：0:all  1:0点备份 2:2点备份 3:4点备份 4:6点备份<br>
	 * @author name: liuhao1
	 * @param stage
	 */
	public void backupTask(int stage);
	
	/**Methods Name: wholeBackup4Db <br>
	 * Description: 数据库备份 <br>
	 * @author name: liuhao1
	 */
	public void backup4Db(MclusterModel mcluster);
	
	/**Methods Name: checkBackupStatus <br>
	 * Description: 检查某备份结果<br>
	 * @author name: liuhao1
	 * @param result
	 */
	public void checkBackupStatus(BackupResultModel result);
	
	public void deleteOutData();

	public void backupTaskReport();
	
	/**
	 * 全量备份
	 * @param params
	 * @return
	 */
	public BackupResultModel wholeBackup4Db(MclusterModel mcluster);
	
	/**
	 * 增量备份
	 * @param params
	 * @return
	 */
	public BackupResultModel incrBackup4Db(MclusterModel mcluster);
	
	
}
