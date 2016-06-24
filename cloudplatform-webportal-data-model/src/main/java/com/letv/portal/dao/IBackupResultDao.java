package com.letv.portal.dao;

import java.util.List;
import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.BackupResultModel;

/**Program Name: IBackupResultDao <br>
 * Description:  备份结果dao<br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年1月5日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBackupResultDao extends IBaseDao<BackupResultModel> {

	void deleteByMclusterId(Long id);

	List<Map<String, Object>> selectExtremeIdByMonitorDate(
			Map<String, Object> map);

	List<BackupResultModel> selectByStatusAndDateOrderByMclusterId(
			Map<String, Object> params);

	List<BackupResultModel> selectByMapGroupByMcluster(Map<String, Object> params);

	public BackupResultModel selectLastedBackupRecord(Map<String, Object> params);
	  
}
