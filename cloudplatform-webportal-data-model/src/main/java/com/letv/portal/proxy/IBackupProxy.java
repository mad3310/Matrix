package com.letv.portal.proxy;

import java.util.Date;

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
	/**
	 * 备份任务
	 * @param count 每个集群启动的备份数量
	 * @param waitMaxTime 一个数据库备份等待最大时间
	 * @param backupTime 备份日期
	 */
	public void backupTask(Integer count, Integer waitMaxTime, Date backupTime);
	
	/**Methods Name: wholeBackup4Db <br>
	 * Description: 数据库备份 <br>
	 * @author name: liuhao1
	 */
	public Boolean backup4Db(MclusterModel mcluster, Date backupDate);
	
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
	public BackupResultModel wholeBackup4Db(BackupResultModel backupRecord);
	
	/**
	 * 增量备份
	 * @param params
	 * @return
	 */
	public BackupResultModel incrBackup4Db(BackupResultModel backupRecord);
	
	/**
	 * 根据集群状态查询备份状态
	 * @param mclusterId
	 * @return
	 */
	public BackupResultModel getBackupStatusByID(long mclusterId);
	
	/**
	 * 备份状态实时结果
	 * @param backupRecord
	 * @return
	 */
	public BackupResultModel getBackupResulFromService(BackupResultModel backupRecord);
	
}
