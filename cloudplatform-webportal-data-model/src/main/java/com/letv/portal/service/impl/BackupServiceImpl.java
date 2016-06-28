package com.letv.portal.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.CommonException;
import com.letv.common.util.CalendarUtil;
import com.letv.portal.dao.IBackupResultDao;
import com.letv.portal.enumeration.BackupStatus;
import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.StrategyModel;
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
	
	@Value("${strategy.backup.reserve.days}")
	private int STRATEGY_BACKUP_RESERVE_DAYS;
	@Value("${strategy.backup.period}")
	private String STRATEGY_BACKUP_PERIOD;
	
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

	@Override
	public StrategyModel selectLastedBackupRecord4Strategy(Map<String, Object> params) {
		
		BackupResultModel backupRet = selectLastedBackupRecord(params);
		
		StrategyModel result = new StrategyModel();
		result.setReserveDays(STRATEGY_BACKUP_RESERVE_DAYS);
		result.setBackupPeriod(STRATEGY_BACKUP_PERIOD);
		
		if(null != backupRet) {
			Date startTime = backupRet.getStartTime();
			result.setBackupDate(startTime);
			result.setFutureBackupDate(getFutureBackupDate(startTime));
		} else {
			result.setBackupDate(null);
			result.setFutureBackupDate(CalendarUtil.startOfDayTomorrow().getTime());
		}
			
		return result;
	}
	
	/*
	 * 根据当前备份开始时间计算下一次备份时间
	 */
	private Date getFutureBackupDate(Date startDate) {
		Date tomorrowDate = CalendarUtil.startOfDayTomorrow().getTime();
		String tomrrowDay = CalendarUtil.getDateString(tomorrowDate, "yyyy-MM-dd");
		String date = CalendarUtil.getDateString(startDate, "HH:mm:ss");
		StringBuffer ret = new StringBuffer();
		ret.append(tomrrowDay).append(" ").append(date);
		Date futureBackupDate = CalendarUtil.parseDate(ret.toString(), "yyyy-MM-dd HH:mm:ss");
		return futureBackupDate;
	}
	
}
