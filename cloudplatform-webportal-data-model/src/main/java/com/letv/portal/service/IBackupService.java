package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.common.dao.QueryParam;
import com.letv.common.paging.impl.Page;
import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.StrategyModel;

/**Program Name: IBackupService <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年1月4日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBackupService extends IBaseService<BackupResultModel> {

	void deleteByMclusterId(Long id);

	public void deleteOutDataByIndex(Map<String, Object> map);
	public List<Map<String, Object>> selectExtremeIdByMonitorDate(
			Map<String, Object> map);

	List<BackupResultModel> selectByStatusAndDateOrderByMclusterId(
			Map<String, Object> params);

	List<BackupResultModel> selectByMapGroupByMcluster(Map<String, Object> params);
	
	/**
	 * 获得最新的备份记录，不包含未进行备份的记录
	 * @param params               查询条件
	 * @return BackupResultModel   备份记录
	 */
	public BackupResultModel selectLastedBackupRecord(Map<String, Object> params);

	/**
	 * 获得最近备份成功的记录
	 */
	public StrategyModel selectLastedBackupRecord4Strategy(Map<String, Object> params);
	
	/**
	 * 获得最新的备份记录分页
	 * @param page
	 * @param params
	 * @return
	 */
	public Page selectLatestLogPageByParams(Page page, Map<String, Object> params);
}
