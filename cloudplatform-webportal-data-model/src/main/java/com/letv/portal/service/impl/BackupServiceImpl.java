package com.letv.portal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.CommonException;
import com.letv.portal.dao.IBackupResultDao;
import com.letv.portal.model.BackupResultModel;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IBackupService;

/**Program Name: BackupServiceImpl <br>
 * Description:  备份结果service<br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年1月5日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Service("backupService")
public class BackupServiceImpl extends BaseServiceImpl<BackupResultModel> implements IBackupService{
	
	private final static Logger logger = LoggerFactory.getLogger(BackupServiceImpl.class);
	
	@Resource
	private IBackupResultDao backupResultDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public BackupServiceImpl() {
		super(BackupResultModel.class);
	}

	@Override
	public IBaseDao<BackupResultModel> getDao() {
		return this.backupResultDao;
	}

	@Override
	public void deleteByMclusterId(Long id) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mclusterId", id);
		List<BackupResultModel> backups = this.backupResultDao.selectByMap(map);
		for (BackupResultModel backup : backups) {
			super.delete(backup);
		}
	}

	@Override
	public void deleteOutDataByIndex(Map<String, Object> map) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from ").append("WEBPORTAL_BACKUP_RESULT").append(" where id between ? and ?");
		logger.debug("delete sql:" + sql.toString());
		try {
			jdbcTemplate.update(sql.toString(), new Object[] {map.get("min"),map.get("max")},
			          new int[] {java.sql.Types.INTEGER,java.sql.Types.INTEGER});
		} catch (Exception e) {
			throw new CommonException("delete monitor data error:" + sql.toString());
		}
		
	}

	@Override
	public List<Map<String, Object>> selectExtremeIdByMonitorDate(
			Map<String, Object> map) {
		return this.backupResultDao.selectExtremeIdByMonitorDate(map);
	}

	@Override
	public List<BackupResultModel> selectByStatusAndDateOrderByMclusterId(
			Map<String, Object> params) {
		return this.backupResultDao.selectByStatusAndDateOrderByMclusterId(params);
	}

	@Override
	public List<BackupResultModel> selectByMapGroupByMcluster(Map<String, Object> params) {
		return this.backupResultDao.selectByMapGroupByMcluster(params);
	}

	@Override
	public BackupResultModel selectLastedBackupRecord(Map<String, Object> params) {
		return backupResultDao.selectLastedBackupRecord(params);
	}
}
