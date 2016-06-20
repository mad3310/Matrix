package com.letv.portal.proxy.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.CalendarUtil;
import com.letv.portal.enumeration.BackupStatus;
import com.letv.portal.enumeration.BackupType;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.model.BackupDTO;
import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.proxy.IBackupProxy;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IBackupService;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.IMclusterService;
import com.mysql.jdbc.StringUtils;


@Component("backupProxy")
public class BackupProxyImpl extends BaseProxyImpl<BackupResultModel> implements IBackupProxy{
	
	private final static Logger logger = LoggerFactory.getLogger(BackupProxyImpl.class);
	
	@Autowired
	private IBackupService backupService;
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IHclusterService hclusterService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IPythonService pythonService;
	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Value("${python.db.backup.interval.time}")
	private long DB_BACKUP_INTERVAL_TIME;
	@Value("${python.db.backup.checked.interval.time}")
	private long DB_BACKUP_CHECKED_INTERVAL_TIME;
	@Value("${python.db.backup.checked.term.time}")
	private String DB_BACKUP_CHECKED_TERM_TIME;
	
	@Value("${default.backup.ignore}")
	private String DEFAULT_BACKUP_IGNORE;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Autowired
	private SchedulingTaskExecutor threadPoolTaskExecutor;

	@Override
	public IBaseService<BackupResultModel> getService() {
		return backupService;
	}
	
	@Override
	public void backupTask(final int count) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("type", "rds");
		List<HclusterModel> hclusters = this.hclusterService.selectByMap(params);
		
		for (final HclusterModel hcluster : hclusters) {
			this.threadPoolTaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					backupByHcluster(count,hcluster);
				}
			});
		}
	}
	
	private void backupByHcluster(int count,HclusterModel hcluster) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("hclusterId", hcluster.getId());
		List<MclusterModel> mclusters = mclusterService.selectValidMclustersByMap(params);
		List<MclusterModel> backups = new ArrayList<MclusterModel>();
		List<MclusterModel> checkRecord = new ArrayList<MclusterModel>();
		
		while(mclusters != null && !mclusters.isEmpty()) {
			try {
				// 备份时间检查
				if(isStopBackup()) {
					break;
				}
				
				// 执行count个备份
				for (int i = 0;i<count;i++) {
					if(mclusters.size()<i+1)
						break;
					MclusterModel mcluster = mclusters.get(i);
					
					backups.add(mcluster);
					backup4Db(mclusters.get(i));
				}
				
				checkRecord.addAll(backups);
				
				// 根据状态和时间检查备份状态，如果时间截至，停止整个备份进程并记录未备份记录
				while(checkRecord != null && !checkRecord.isEmpty()) {
					for(int i=0; i<checkRecord.size(); i++) {
						MclusterModel mcluster = checkRecord.get(i);
						long mclusterId = mcluster.getId();
						BackupResultModel analRet = getBackupStatusByID(mclusterId);
						logger.debug("db backup process status: {}", analRet.getStatus());
						if(BackupStatus.BUILDING.equals(analRet.getStatus())) {
							Thread.sleep(DB_BACKUP_CHECKED_INTERVAL_TIME);
							if(isStopBackup()) {
								checkRecord = null;
								break;
							} 
							continue;
						} else {
							// 更新备份结果
							updateBackupResult(mcluster, analRet);
							// 当前检查记录清理
							checkRecord.remove(mcluster);
							i--;
						}
					}
				}
				
				// 清理已经备份的数据，准备下次备份任务
				mclusters.removeAll(backups);
				backups.clear();
				
				logger.debug("continue the next backup.");
			} catch (Exception e) {
				logger.error("db backup exception:{}",e.getMessage());
			}
		}
		
		// 记录在截至时间内尚未进行备份的记录
		if(mclusters != null && !mclusters.isEmpty()) {
			saveBackup(mclusters);
		}
	}

	/*
	 * 从服务器获取当前集群备份状态
	 */
	@Override
	public BackupResultModel getBackupStatusByID(long mclusterId) {
		ContainerModel container = selectValidVipContianer(mclusterId, "mclustervip");
		if(null == container)
			return null;
		ApiResultObject ret = pythonService.checkBackup4Db(container.getIpAddr());
		BackupResultModel result = analysisBackupResult(new BackupResultModel(), ret);
		return result;
	}
	
	// 保存未进行备份的数据
	private void saveBackup(List<MclusterModel> mclusters) {
		for(MclusterModel mcluster : mclusters) {
			ContainerModel container = selectValidVipContianer(mcluster.getId(), "mclustervip");
			List<DbModel> dbModels = dbService.selectDbByMclusterId(mcluster.getId());
			BackupResultModel backup = new BackupResultModel();
			backup.setMclusterId(mcluster.getId());
			backup.setHclusterId(mcluster.getHclusterId());
			backup.setDbId(dbModels.get(0).getId());
			if(null != container) 
				backup.setBackupIp(container.getIpAddr());
			backup.setBackupType(BackupType.NONE.name());
			backup.setStatus(BackupStatus.ABNORMAL);
			backup.setResultDetail("backup doesn't execute, Because there is a complete backup task is not performed");
	        backupService.insert(backup);
		}
	}
	
	/*
	 *  当没有成功的全备份数据时，增量备份无法执行，
	 *  当第一次未进行全备份操作时，只能一直进行备份状态的检测
	 *  直道第一个全备份成功为止
	 */
	private boolean checkFullBackup(MclusterModel mcluster) {

		long mclusterId = mcluster.getId();
		BackupResultModel analRet = getBackupStatusByID(mclusterId);
			
		// 缺少全备份
		if(BackupStatus.ABNORMAL.equals(analRet.getStatus())) {
			return true;
		}
		return false;
	}
	
	/*
	 * 更新最后的备份状态
	 */
	private void updateBackupResult(MclusterModel mcluster, BackupResultModel analRet) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("mclusterId", mcluster.getId());
		BackupResultModel result = backupService.selectLastedBackupRecord(params);
		result.setStatus(analRet.getStatus());
		Date date = new Date();
		result.setEndTime(date);
		backupService.updateBySelective(result);
	}
	
	@Override
	public void backup4Db(MclusterModel mcluster) {	
		Date date = new Date();
		if(mcluster == null)
			return;
		ContainerModel container = selectValidVipContianer(mcluster.getId(), "mclustervip");
		// 只有一个数据库模型
		List<DbModel> dbModels = dbService.selectDbByMclusterId(mcluster.getId());
		if(container == null || dbModels.isEmpty())  {
			//发送告知邮件，数据有问题。
			return;
		}
		
		BackupResultModel backup = backup4Db(mcluster, container);
		
		for (DbModel dbModel : dbModels) {
			//将备份记录写入数据库。
			backup.setMclusterId(mcluster.getId());
			backup.setHclusterId(mcluster.getHclusterId());
			backup.setDbId(dbModel.getId());
			backup.setBackupIp(container.getIpAddr());
			backup.setStartTime(date);
            try {
                this.backupService.insert(backup);
            } catch (Exception e) {
                logger.error("backupService.insert exception:{}",e.getMessage());
                this.backupService.insert(backup);
            }
        }
	}
	
	private BackupResultModel backup4Db(MclusterModel mcluster, ContainerModel container){
	
		BackupResultModel backupResult = new BackupResultModel();
		if(DEFAULT_BACKUP_IGNORE.contains(mcluster.getMclusterName())) {
			backupResult.setStatus(BackupStatus.FAILD);
			backupResult.setResultDetail("Ignore  backup on current mcluster.");
			return backupResult;
		}
		
		BackupDTO result = backupTransaction(mcluster, container);
		
		backupResult.setBackupType(result.getBackupType().name());
		String resultMessage = result.getResult();
		if(StringUtils.isNullOrEmpty(resultMessage)) {
			backupResult.setStatus(BackupStatus.FAILD);
			backupResult.setResultDetail("backup api result is null:" + result.getUrl());
		} else if(resultMessage.contains("\"code\": 200")) {
			backupResult.setStatus(BackupStatus.BUILDING);
		} else {
			backupResult.setStatus(BackupStatus.FAILD);
			backupResult.setResultDetail(resultMessage + ":" + result.getUrl());
		}
		return backupResult;
	}
	
	private BackupDTO backupTransaction(MclusterModel mcluster, ContainerModel container) {
		
		// 获得上一次最新的备份结果
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("mclusterId", mcluster.getId());
		BackupResultModel lastedBackupResult = backupService.selectLastedBackupRecord(params);
		BackupDTO result = null;
		String ip = container.getIpAddr();
		String user = mcluster.getAdminUser();
		String pwd = mcluster.getAdminPassword();
		
		// 没有初始化备份或历史备份记录不存在
		if(checkFullBackup(mcluster) || null == lastedBackupResult) {
			//result = executeBackupByWeek(ip, user, pwd);
			result = pythonService.wholeBackup4Db(ip, user, pwd);
			result.setBackupType(BackupType.FULL);
		} else {
			BackupStatus status = lastedBackupResult.getStatus();
			String backpType = lastedBackupResult.getBackupType();
			// 备份失败
			if(BackupStatus.FAILD.equals(status)) {
				if(BackupType.FULL.equals(backpType)) {
					result = pythonService.wholeBackup4Db(ip, user, pwd);
					result.setBackupType(BackupType.FULL);
				} else {
					result = executeBackupByWeek(ip, user, pwd);
				}
			} else {
				// 备份成功
				result = executeBackupByWeek(ip, user, pwd);
			}
		}
		return result;
	}
	
	/*
	 * 根据星期执行不同备份操作
	 */
	private BackupDTO executeBackupByWeek(String ip, String user, String pwd) {
		BackupDTO result = null;
		int dayOfWeek = CalendarUtil.getDayOfWeek(new Date());
		if(dayOfWeek == 7) {
			result = pythonService.wholeBackup4Db(ip, user, pwd);
			result.setBackupType(BackupType.FULL);
		} else {
			result = pythonService.incrBackup4Db(ip, user, pwd);
			result.setBackupType(BackupType.INCR);
		}
		return result;
	}
	
	private ContainerModel selectValidVipContianer(Long mclusterId,String type){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mclusterId", mclusterId);
		map.put("type", type);
		List<ContainerModel> containers = this.containerService.selectAllByMap(map);
		if(containers.isEmpty()) {
			return null;
		}
		return containers.get(0);
	}

	@Override
	public void checkBackupStatus(BackupResultModel backup) {
		ApiResultObject result = this.pythonService.checkBackup4Db(backup.getBackupIp());
		backup = analysisBackupResult(backup, result);
		
		Date date = new Date();
		backup.setEndTime(date);
		this.backupService.updateBySelective(backup);
		if(BackupStatus.FAILD.equals(backup.getStatus())) {
			logger.error("check backup faild");
			//发送邮件通知
//			sendBackupFaildNotice(backup.getDb().getDbName(),backup.getMcluster().getMclusterName(),backup.getResultDetail(),backup.getStartTime(),backup.getBackupIp());
		}
	}
	
	private BackupResultModel analysisBackupResult(BackupResultModel backup,ApiResultObject resultObject) {
		String result = resultObject.getResult();
		if(StringUtils.isNullOrEmpty(result)) {
			backup.setStatus( BackupStatus.FAILD);
			backup.setResultDetail("Connection refused:" + resultObject.getUrl());
			return backup;
		}
		if(result.contains("\"code\": 200") && result.contains("backup success")) {
			backup.setStatus( BackupStatus.SUCCESS);
			backup.setResultDetail("backup success");
			return backup;
		}
		if(result.contains("\"code\": 200") && result.contains("processing")) {
			backup.setStatus( BackupStatus.BUILDING);
			backup.setResultDetail("backup is processing");
			
			int hours = new Date().getHours();
			int startHours = backup.getStartTime().getHours();
			if(hours-startHours>1 || hours-startHours<0) {
				backup.setStatus( BackupStatus.ABNORMAL);
				backup.setResultDetail("more than one hour for bakcup");
			}
			return backup;
		}
		if(result.contains("\"code\": 200") && result.contains("expired")) {
			backup.setStatus( BackupStatus.FAILD);
			backup.setResultDetail("backup expired:" + resultObject.getUrl());
			return backup;
		}
		if(result.contains("\"code\": 411")) {
			backup.setStatus( BackupStatus.FAILD);
			backup.setResultDetail(result.substring(result.indexOf("\"errorDetail\": \"")+1, result.lastIndexOf("\"},")) + resultObject.getUrl());
			return backup;
		}
		if(result.contains("\"code\": 417") && result.contains("full backup")) {
			backup.setStatus(BackupStatus.ABNORMAL);
			return backup;
		}
		backup.setStatus( BackupStatus.FAILD);
		backup.setResultDetail("api not found:" + resultObject.getUrl());

		return backup;
	}
	
	private void sendBackupFaildNotice(String dbName,String mclusterName,String resultDetail,Date startTime,String backupIp) {
		logger.info("check backup faild:send email--" + dbName + mclusterName);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dbName", dbName);
		params.put("mclusterName", mclusterName);
		params.put("resultDetail", resultDetail);
		params.put("startTime", format.format(startTime));
		params.put("backupIp", backupIp);
		
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",SERVICE_NOTICE_MAIL_ADDRESS,"乐视云平台web-portal系统报警通知","backupFaildNotice.ftl",params);
     	defaultEmailSender.sendMessage(mailMessage);
	}
	
	@Override
	@Async
	public void deleteOutData() {
		Map<String,Object> map = new  HashMap<String,Object>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -15);    //得到前15天
		long date = cal.getTimeInMillis();
		Date monthAgo = new Date(date);
		map.put("startTime", monthAgo);
		
		List<Map<String,Object>> ids = this.backupService.selectExtremeIdByMonitorDate(map);
		if(ids.isEmpty() || ids.get(0) == null || ids.get(0).isEmpty()) {
			return;
		}
		Map<String, Object> extremeIds = ids.get(0);
		Long max = (Long)extremeIds.get("maxId");
		Long min = (Long)extremeIds.get("minId");
		if(max == null || max == 0 || max == min)
			return;
		Long j = min;
		for (Long i = min; i <= max; i+=100) {
			j = i-100;
			map.put("min", j);
			map.put("max", i);
			this.backupService.deleteOutDataByIndex(map);
		}
		map.put("min", max-100);
		map.put("max", max);
		this.backupService.deleteOutDataByIndex(map);
	}

	@Override
	public void backupTaskReport() {
		Map<String, Object> params = new HashMap<String,Object>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar curDate = Calendar.getInstance();
		curDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH),curDate.get(Calendar.DATE), 0, 0, 0);
		params.put("startTime", format.format(new Date(curDate.getTimeInMillis())));
		List<BackupResultModel> backupResults = this.selectByMap(params);
		
		for (BackupResultModel backupResultModel : backupResults) {
			this.checkBackupStatus(backupResultModel);
		}
		backupResults = this.selectByMap(params);
		
		int mclusters = this.mclusterService.selectValidMclusterCount();
		int dbs = this.dbService.selectCountByStatus(DbStatus.NORMAL.getValue());
		int totalDb = backupResults.size();
		int successDb = 0;
		int failedDb = 0;
		int buildingDb = 0;
		int abNormalDb = 0;
		for (BackupResultModel backupResultModel : backupResults) {
			if(BackupStatus.SUCCESS.equals(backupResultModel.getStatus())) {
				successDb++;
				continue;
			}
			if(BackupStatus.FAILD.equals(backupResultModel.getStatus())) {
				failedDb++;
				continue;
			}
			if(BackupStatus.BUILDING.equals(backupResultModel.getStatus())) {
				buildingDb++;
				continue;
			}
			if(BackupStatus.ABNORMAL.equals(backupResultModel.getStatus())) {
				abNormalDb++;
				continue;
			}
		}
		
		backupResults = this.backupService.selectByMapGroupByMcluster(params);
		int totalCluster = backupResults.size();
		int successCluster = 0;
		int failedCluster = 0;
		int buildingCluster = 0;
		int abNormalCluster = 0;
		for (BackupResultModel backupResultModel : backupResults) {
			if(BackupStatus.SUCCESS.equals(backupResultModel.getStatus())) {
				successCluster++;
				continue;
			}
			if(BackupStatus.FAILD.equals(backupResultModel.getStatus())) {
				failedCluster++;
				continue;
			}
			if(BackupStatus.BUILDING.equals(backupResultModel.getStatus())) {
				buildingCluster++;
				continue;
			}
			if(BackupStatus.ABNORMAL.equals(backupResultModel.getStatus())) {
				abNormalCluster++;
				continue;
			}
		}
		
		params.clear();
		params.put("dbCount", dbs);
		params.put("dbBackupCount", totalDb);
		params.put("successDb", successDb);
		params.put("failedDb", failedDb);
		params.put("buildingDb", buildingDb);
		params.put("abNormalDb", abNormalDb);
		
		params.put("clusterCount", mclusters);
		params.put("clusterBackupCount", totalCluster);
		params.put("successCluster", successCluster);
		params.put("failedCluster", failedCluster);
		params.put("buildingCluster", buildingCluster);
		params.put("abNormalCluster", abNormalCluster);
		
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",SERVICE_NOTICE_MAIL_ADDRESS,"乐视云平台web-portal系统备份结果通知","dbBackupReport.ftl",params);
		mailMessage.setHtml(true);
		defaultEmailSender.sendMessage(mailMessage);
	}
	
	@Override
	public BackupDTO wholeBackup4Db(MclusterModel mcluster) {
		BackupCMD cmd = new BackupCMD(){

			@Override
			public BackupDTO invoke(String ip, String user, String pwd) {
				return pythonService.wholeBackup4Db(ip, user, pwd);
			}
			
		};
		
		BackupDTO result = sendBackupCMD(mcluster, cmd);
		
		return result;
	}

	@Override
	public BackupDTO incrBackup4Db(MclusterModel mcluster) {
		
		BackupCMD cmd = new BackupCMD(){

			@Override
			public BackupDTO invoke(String ip, String user, String pwd) {
				return pythonService.incrBackup4Db(ip, user, pwd);
			}
			
		};
		
		BackupDTO result = sendBackupCMD(mcluster, cmd);
		
		return result;
	}
	
	public BackupDTO sendBackupCMD(MclusterModel mcluster, BackupCMD backupCMD) {
		long mclusterId = mcluster.getId();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("id", mclusterId);
		List<MclusterModel> mclusters = mclusterService.selectValidMclustersByMap(params);
		if(null == mclusters || mclusters.isEmpty()) {
			return null;
		}
		String user = mclusters.get(0).getAdminUser();
		String pwd = mclusters.get(0).getAdminPassword();
		
		ContainerModel container = selectValidVipContianer(mclusterId, "mclustervip");
		if(null == container)
			return null;
		String ip = container.getIpAddr();
		
		return backupCMD.invoke(ip, user, pwd);
	}
	
	private boolean isStopBackup() {
		Date realTime = new Date();
		String date = CalendarUtil.getDateString(realTime, "yyyy-MM-dd");
		StringBuffer termTime = new StringBuffer();
		termTime.append(date).append(" ").append(DB_BACKUP_CHECKED_TERM_TIME);
		Date stopDate = CalendarUtil.parseDate(termTime.toString(), "yyyy-MM-dd HH:mm:ss");
		int ret = realTime.compareTo(stopDate);
		if(ret >= 0)
			return true;
		return false;
	}
	
	private interface BackupCMD {
		public BackupDTO invoke(String ip, String user, String pwd);
	}
	
}
