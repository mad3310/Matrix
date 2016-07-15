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
import com.letv.common.exception.ValidateException;
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
		backupTask(count, null, null);
	}
	
	@Override
	public void backupTask(final Integer count, Integer waitMaxTime, Date backupTime) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("type", "rds");
		List<HclusterModel> hclusters = this.hclusterService.selectByMap(params);
		//最大等待时间为空时，默认60分钟
		final Integer waitTime = null==waitMaxTime||waitMaxTime<=0 ? 60 : waitMaxTime;
		//备份日期为空时，默认当前时间
		final Date backupDate = null==backupTime ? new Date() : backupTime;
		for (final HclusterModel hcluster : hclusters) {
			this.threadPoolTaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if(null != hcluster.getUpgrade() && hcluster.getUpgrade()) {//已升级，调用新接口
						logger.debug("{}集群备份接口已升级，调用新接口", hcluster.getHclusterName());
						backupByHcluster(count, hcluster, waitTime, backupDate);
					} else {//未升级，调用老接口
						logger.debug("{}集群备份接口未升级，调用老接口", hcluster.getHclusterName());
						oldBackupByHcluster(count, hcluster);
					}
				}
			});
		}
	}
	
	/******old method start*******/
	private void oldBackupByHcluster(int count,HclusterModel hcluster) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("hclusterId", hcluster.getId());

        List<MclusterModel> mclusters = this.mclusterService.selectValidMclustersByMap(params);
        List<MclusterModel> backups = new ArrayList<MclusterModel>();

        while(mclusters != null && !mclusters.isEmpty()) {
            try {
                for (int i = 0;i<count;i++) {
                    if(mclusters.size()<i+1)
                        break;
                    backups.add(mclusters.get(i));
                    this.wholeBackup4Db(mclusters.get(i));
                }
                mclusters.removeAll(backups);
                backups.clear();
				Thread.sleep(DB_BACKUP_INTERVAL_TIME);
			} catch (Exception e) {
                logger.error("db backup exception:{}",e.getMessage());
			}
		}
	}
	
	private void wholeBackup4Db(MclusterModel mcluster) {
		Date date = new Date();
		if(mcluster == null)
			return;
		ContainerModel container = this.selectValidVipContianer(mcluster.getId(), "mclustervip");
		List<DbModel> dbModels = this.dbService.selectDbByMclusterId(mcluster.getId());
		if(container == null || dbModels.isEmpty())  {
			//发送告知邮件，数据有问题。
			return;
		}
		
		BackupResultModel backup = this.wholeBackup4Db(mcluster,container);
		
		for (DbModel dbModel : dbModels) {
			//将备份记录写入数据库。
			backup.setMclusterId(mcluster.getId());
			backup.setHclusterId(mcluster.getHclusterId());
			backup.setDbId(dbModel.getId());
			backup.setBackupIp(container.getIpAddr());
			backup.setStartTime(date);
			backup.setBackupType(BackupType.FULL.name());
			if(!BackupStatus.BUILDING.equals(backup.getStatus())) {
				backup.setEndTime(new Date());
			}
            try {
                this.backupService.insert(backup);
            } catch (Exception e) {
                logger.error("backupService.insert exception:{}",e.getMessage());
                this.backupService.insert(backup);
            }
        }
	}
	
	private BackupResultModel wholeBackup4Db(MclusterModel mcluster,ContainerModel container){
		BackupResultModel backupResult = new BackupResultModel();
		if(DEFAULT_BACKUP_IGNORE.contains(mcluster.getMclusterName())) {
			backupResult.setStatus(BackupStatus.FAILD);
			backupResult.setResultDetail("Ignore  backup on current mcluster.");
			return backupResult;
		}
		ApiResultObject result = this.pythonService.oldWholeBackup4Db(container.getIpAddr(),mcluster.getAdminUser(),mcluster.getAdminPassword());
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
	/******old method end******/
	
	private void backupByHcluster(int count,HclusterModel hcluster, Integer waitTime, Date backupDate) {
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
					
					if(backup4Db(mcluster, backupDate)) {//备份调用成功
						backups.add(mcluster);
					} else {//未进行备份
						mclusters.remove(i);
						i--;
						continue;
					}
				}
				
				checkRecord.addAll(backups);
				
				int time = 0;
				
				// 根据状态和时间检查备份状态，如果时间截至，停止整个备份进程并记录未备份记录
				while(checkRecord != null && !checkRecord.isEmpty()) {
					for(int i=0; i<checkRecord.size(); i++) {
						MclusterModel mcluster = checkRecord.get(i);
						long mclusterId = mcluster.getId();
						BackupResultModel analRet = getBackupStatusByID(mclusterId);
						logger.debug("db backup process status: {}", analRet.getStatus());
						if(BackupStatus.BUILDING.equals(analRet.getStatus())) {
							//超过最大等待时间
							if(time > waitTime) {
								logger.debug("beyond max wait time ,continue next backup");
								// 当前检查记录清理
								checkRecord.remove(mcluster);
								i--;
							} else {
								logger.debug("backup status is building ,sleeping ...{}", DB_BACKUP_CHECKED_INTERVAL_TIME);
								Thread.sleep(DB_BACKUP_CHECKED_INTERVAL_TIME);
								time = (int) (time + DB_BACKUP_CHECKED_INTERVAL_TIME/60000);
								if(isStopBackup()) {
									checkRecord = null;
									break;
								}
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
				logger.error("db backup exception:{}", e);
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
		BackupResultModel backupResult = new BackupResultModel();
		BackupResultModel result = analysisBackupResult(backupResult, ret);
		logger.debug("{}集群备份状态：{}, 详细结果：{}", container.getContainerName(), result.getStatus(), result.getResultDetail());
		return result;
	}
	
	// 保存未进行备份的数据
	private void saveBackup(List<MclusterModel> mclusters) {
		BackupResultModel backup = new BackupResultModel();
		Date date = new Date();
		backup.setStartTime(date);
		backup.setEndTime(date);
		backup.setBackupType(BackupType.NONE.name());
		backup.setStatus(BackupStatus.ABNORMAL);
		backup.setResultDetail("backup doesn't execute, because over the backup time!");
		for(MclusterModel mcluster : mclusters) {
			ContainerModel container = selectValidVipContianer(mcluster.getId(), "mclustervip");
			List<DbModel> dbModels = dbService.selectDbByMclusterId(mcluster.getId());
			if(container != null && !dbModels.isEmpty())  {//数据节点异常，忽略备份
				backup.setMclusterId(mcluster.getId());
				backup.setHclusterId(mcluster.getHclusterId());
				backup.setDbId(dbModels.get(0).getId());
				backup.setBackupIp(container.getIpAddr());
		        backupService.insert(backup);
			}
		}
	}
	
	
	/*
	 * 更新最后的备份状态
	 */
	private void updateBackupResult(MclusterModel mcluster, BackupResultModel analRet) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("mclusterId", mcluster.getId());
		BackupResultModel result = backupService.selectLastedBackupRecord(params);
		//只有buiding状态才进行更新
		if(BackupStatus.BUILDING.equals(result.getStatus())) {
			result.setStatus(analRet.getStatus());
			result.setResultDetail(analRet.getResultDetail());
			result.setEndTime(new Date());
			backupService.updateBySelective(result);
		}
	}
	
	@Override
	public Boolean backup4Db(MclusterModel mcluster, Date backupDate) {	
		Date date = new Date();
		if(mcluster == null)
			return false;
		ContainerModel container = selectValidVipContianer(mcluster.getId(), "mclustervip");
		// 只有一个数据库模型
		List<DbModel> dbModels = dbService.selectDbByMclusterId(mcluster.getId());
		if(container == null || dbModels.isEmpty())  {
			//数据有问题。
			return false;
		}
		
		BackupResultModel backup = backup4Db(mcluster, container, backupDate);
		
		if(null != backup) {
			for (DbModel dbModel : dbModels) {
				//将备份记录写入数据库。
				backup.setMclusterId(mcluster.getId());
				backup.setHclusterId(mcluster.getHclusterId());
				backup.setDbId(dbModel.getId());
				backup.setBackupIp(container.getIpAddr());
				backup.setStartTime(date);
				if(!BackupStatus.BUILDING.equals(backup.getStatus())) {
					backup.setEndTime(new Date());
				}
	            this.backupService.insert(backup);
	        }
			return true;
		} else {
			return false;
		}
		
	}
	
	private BackupResultModel backup4Db(MclusterModel mcluster, ContainerModel container, Date backupDate){
	
		BackupResultModel backupResult = new BackupResultModel();
		if(DEFAULT_BACKUP_IGNORE.contains(mcluster.getMclusterName())) {
			backupResult.setStatus(BackupStatus.FAILD);
			backupResult.setResultDetail("Ignore  backup on current mcluster.");
			return backupResult;
		}
		
		BackupDTO result = backupTransaction(mcluster, container, backupDate);
		if(null == result) {//上次备份还处于备份中，本次不进行备份操作
			return null;
		}
		
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
	
	private BackupDTO backupTransaction(MclusterModel mcluster, ContainerModel container, Date backupDate) {
		
		// 获得上一次最新的备份结果
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("mclusterId", mcluster.getId());
		BackupResultModel lastedBackupResult = backupService.selectLastedBackupRecord(params);
		BackupDTO result = null;
		String ip = container.getIpAddr();
		String user = mcluster.getAdminUser();
		String pwd = mcluster.getAdminPassword();
		
		BackupResultModel analRet = getBackupStatusByID(mcluster.getId());
		
		//数据库中记录如果不为空并且是备份中状态，根据服务器最新状态更新
		if(null != lastedBackupResult && BackupStatus.BUILDING.equals(lastedBackupResult.getStatus())) {
			if(BackupStatus.BUILDING.equals(analRet.getStatus())) {
				return null;
			} else {
				lastedBackupResult.setStatus(analRet.getStatus());
				lastedBackupResult.setEndTime(new Date());
				lastedBackupResult.setResultDetail(analRet.getResultDetail());
				backupService.updateBySelective(lastedBackupResult);
			}
		} 
		
		// 没有初始化备份或历史备份记录不存在(当没有成功的全备份数据时，增量备份无法执行)
		if(BackupStatus.ABNORMAL.equals(analRet.getStatus()) || null == lastedBackupResult) {
			result = pythonService.wholeBackup4Db(ip, user, pwd);
			result.setBackupType(BackupType.FULL);
		} else {
			BackupStatus status = lastedBackupResult.getStatus();
			String backpType = lastedBackupResult.getBackupType();
			
			if(BackupStatus.SUCCESS.equals(status)) {// 备份成功
				result = executeBackupByWeek(ip, user, pwd, backupDate);
			} else {// 备份失败
				if(BackupType.FULL.name().equals(backpType)) {
					result = pythonService.wholeBackup4Db(ip, user, pwd);
					result.setBackupType(BackupType.FULL);
				} else {
					result = executeBackupByWeek(ip, user, pwd, backupDate);
				}
				
			}
		}
		return result;
	}
	
	/*
	 * 根据星期执行不同备份操作
	 */
	private BackupDTO executeBackupByWeek(String ip, String user, String pwd, Date backupDate) {
		BackupDTO result = null;
		int dayOfWeek = CalendarUtil.getDayOfWeek(backupDate);
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
			backup.setResultDetail(result.substring(result.indexOf("\"errorDetail\": \"")+1, result.lastIndexOf("\"},")));
			return backup;
		}
		backup.setStatus(BackupStatus.FAILD);
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
			if(BackupStatus.BUILDING.equals(backupResultModel.getStatus())) {
				this.checkBackupStatus(backupResultModel);
			}
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
	public BackupResultModel wholeBackup4Db(BackupResultModel backupRecord) {
		
		
		BackupCMD cmd = new BackupCMD(){

			@Override
			public BackupDTO invoke(String ip, String user, String pwd) {
				return pythonService.wholeBackup4Db(ip, user, pwd);
			}
			
		};
		
		BackupResultModel result = sendBackupCMD(backupRecord, cmd, BackupType.FULL);
		
		return result;
	}

	@Override
	public BackupResultModel incrBackup4Db(BackupResultModel backupRecord) {
		
		BackupCMD cmd = new BackupCMD(){

			@Override
			public BackupDTO invoke(String ip, String user, String pwd) {
				return pythonService.incrBackup4Db(ip, user, pwd);
			}
			
		};
		
		BackupResultModel result = sendBackupCMD(backupRecord, cmd, BackupType.INCR);
		
		return result;
	}
	
	public BackupResultModel sendBackupCMD(BackupResultModel backupRecord, BackupCMD backupCMD, BackupType backupType) {
		long backupResultId = backupRecord.getId();
		BackupResultModel originRecord = backupService.selectById(backupResultId);
		
		//判断所属物理机集群是否已升级，没有升级，返回null
		long hclusterId = originRecord.getHclusterId();
		HclusterModel hclusterModel = this.hclusterService.selectById(hclusterId);
		if(!hclusterModel.getUpgrade()) {
			return null;
		}
		
		long mclusterId = backupRecord.getMclusterId();
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
		
		BackupResultModel serviceBackupRet = getBackupStatusByID(mclusterId);
		BackupStatus checkStatus = serviceBackupRet.getStatus();
		
		if(BackupType.INCR.equals(backupType) && BackupStatus.ABNORMAL.equals(serviceBackupRet.getStatus())) {
			throw new ValidateException("请先进行全量备份！");
		}
		
		// 如果数据库记录或服务器上备份正在进行，返回空; 否则，发送备份操作
		if(BackupStatus.BUILDING.equals(originRecord.getStatus()) || BackupStatus.BUILDING == checkStatus) {
			return null;
		} else {
			backupCMD.invoke(ip, user, pwd);
			originRecord.setStartTime(new Date());
			originRecord.setStatus(BackupStatus.BUILDING);
			originRecord.setBackupType(backupType.name());
			originRecord.setResultDetail(null);
			originRecord.setEndTime(null);
			backupService.insert(originRecord);
		}
		return originRecord;
	}
	
	@Override
	public BackupResultModel getBackupResulFromService(BackupResultModel backupRecord) {
		long mclusterId = backupRecord.getMclusterId();
		long resultId = backupRecord.getId();
		BackupResultModel originRecord = backupService.selectById(resultId);
		if(BackupStatus.BUILDING.equals(originRecord.getStatus())) {//数据库中记录为备份中的才进行服务端查询
			BackupResultModel checkResult = getBackupStatusByID(mclusterId);
			//服务端状态不为备份中的才进行更新
			if(BackupStatus.BUILDING != checkResult.getStatus()) {
				originRecord.setStatus(checkResult.getStatus());
				originRecord.setResultDetail(checkResult.getResultDetail());
				originRecord.setEndTime(new Date());
				backupService.updateBySelective(originRecord);
			}
		}
		return originRecord;
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
