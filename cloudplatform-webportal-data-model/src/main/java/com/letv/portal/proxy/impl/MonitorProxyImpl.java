package com.letv.portal.proxy.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.letv.portal.model.*;
import com.letv.portal.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.BackupStatus;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.monitor.MonitorErrorModel;
import com.letv.portal.model.monitor.MonitorViewYModel;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.model.swift.SwiftServer;
import com.letv.portal.proxy.IMonitorProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbContainerService;
import com.letv.portal.service.slb.ISlbServerService;
import com.letv.portal.service.swift.ISwiftServerService;

@Component("monitorProxy")
public class MonitorProxyImpl implements IMonitorProxy{
	private final static Logger logger = LoggerFactory.getLogger(MonitorProxyImpl.class);
	@Autowired
	private IMonitorService monitorService;
	@Autowired
	private IBuildTaskService buildTaskService;
	
	@Autowired
	private IContainerService containerService;
	@Autowired
	private ISlbClusterService slbClusterService;
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private IGceClusterService gceClusterService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IGceServerService gceServerService;
	@Autowired
	private ISwiftServerService swiftServerService;
	@Autowired
	private IGceContainerService gceContainerService;
	@Autowired
	private ISlbServerService slbServerService;
	@Autowired
	private ISlbContainerService slbContainerService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMonitorIndexService monitorIndexService;
	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Value("${delete.monitor.error.days.ago}")
	private int DELETE_MONITOR_ERROR_DAYS_AGO;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Override
	public void collectMclusterServiceData() {
		Map<String,Object> map = new  HashMap<String,Object>();
		List<ContainerModel> contianers = this.containerService.selectVaildNormalContainers(map);
		
		map.clear();
		map.put("status", 1);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(map);
		Date date = new Date();
		logger.info("collectMclusterServiceData start" + date);
		for (MonitorIndexModel index : indexs) {
			for (ContainerModel container : contianers) {
				this.buildTaskService.getContainerServiceData(container.getIpAddr(), index.getDataFromApi(), index.getDetailTable(), date);
			}
		}
		logger.info("collectMclusterServiceData end");
	}
	@Override
	public void collectClusterServiceData() {
		List<SlbCluster> slbClusters = this.slbClusterService.selectByMap(null);
		List<GceCluster> gceClusters = this.gceClusterService.selectByMap(null);
		List<MclusterModel> mclusters = this.mclusterService.selectValidMclusters();

		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 2);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		Date date = new Date();
		logger.info("collectClusterServiceData start" + date);
		for (MonitorIndexModel index : indexs) {
			for (GceCluster gceCluster : gceClusters) {
				this.buildTaskService.getClusterServiceData(gceCluster.getClusterName(),gceCluster.getHclusterId(), index,date);
			}
			for (SlbCluster slbCluster : slbClusters) {
				this.buildTaskService.getClusterServiceData(slbCluster.getClusterName(),slbCluster.getHclusterId(), index,date);
			}
			for (MclusterModel mcluster:mclusters) {
				this.buildTaskService.getClusterServiceData(mcluster.getMclusterName(),mcluster.getHclusterId(), index,date);
			}
		}
        slbClusters.clear();
        gceClusters.clear();
		mclusters.clear();
		logger.info("collectClusterServiceData end");
	}
	@Override
	public void collectOSSServiceData() {
		List<SwiftServer> swifts = this.swiftServerService.selectByMap(null);
		
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 3);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		Date date = new Date();
		logger.info("collectClusterServiceData start" + date);
		for (MonitorIndexModel index : indexs) {
			for (SwiftServer swift : swifts) {
				HostModel host = this.hostService.getHostByHclusterId(swift.getHclusterId());
				StringBuilder key = new StringBuilder();
				UserModel user = this.userService.selectById(swift.getCreateUser());
				key.append(user.getUserName()).append("/").append(swift.getName());
				this.buildTaskService.getOSSServiceData(host.getHostIp(),key.toString(), index,date);
			}
		}
        swifts.clear();
	}
	
	@Override
	@Async
	public void deleteOutData() {
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 1);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);    //得到前一个月
		long date = cal.getTimeInMillis();
		Date monthAgo = new Date(date);
		for (MonitorIndexModel monitorIndexModel : indexs) {
			this.deleteOutData(monitorIndexModel.getDetailTable(), "MONITOR_DATE", monthAgo);
		}
	}
	
	@Override
	@Async
	public void deleteOutData(String tableName, String columnName, Date date) {
		//get max id and min id from table where monitor_date<monthAgo
		//for in  min and max, delete every 5000 by id.
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dbName", tableName);
		map.put("columnName", columnName);
		map.put("date", date);
		List<Map<String,Object>> ids = this.monitorService.selectExtremeIdByMonitorDate(map);
		if(ids.isEmpty() || ids.get(0) == null || ids.get(0).isEmpty()) {
			return;
		}
		Map<String, Object> extremeIds = ids.get(0);
		Long max = ((BigInteger)extremeIds.get("maxId")).longValue();
		Long min = ((BigInteger)extremeIds.get("minId")).longValue();
		if(max == null || max == 0 || max == min)
			return;
		Long j = min;
		for (Long i = min; i <= max; i+=100) {
			j = i-100;
			map.put("min", j);
			map.put("max", i);
			try {
				this.monitorService.deleteOutDataByIndex(map);
			} catch (Exception e) {
				logger.error("monitorService.deleteOutDataByIndex have error : ", e);
				continue;
			}
		}
		map.put("min", max-100);
		map.put("max", max);
		this.monitorService.deleteOutDataByIndex(map);
	}
	
	
	@Override
	public List<MonitorViewYModel> getData(String type, Long serverId,Long chartId, Integer strategy, boolean isTimeAveraging,int format) {
		if("RDS".equals(type.toUpperCase()))
			return this.getDbData(serverId, chartId, strategy, isTimeAveraging,format);
		if("GCE".equals(type.toUpperCase()))
			return this.getGceData(serverId, chartId, strategy, isTimeAveraging,format);
		if("SLB".equals(type.toUpperCase()))
			return this.getSlbData(serverId, chartId, strategy, isTimeAveraging,format);
		if("OSS".equals(type.toUpperCase()))
			return this.getOSSData(serverId, chartId, strategy, isTimeAveraging,format);
		return null;
	}
	private List<MonitorViewYModel> getOSSData(Long serverId, Long chartId,Integer strategy, boolean isTimeAveraging, int format) {
		SwiftServer swift = this.swiftServerService.selectById(serverId);
		if(swift == null || swift.getHclusterId() == null)
			throw new ValidateException("参数不合法");
		
		HostModel host = this.hostService.getHostByHclusterId(swift.getHclusterId());
		
		StringBuilder key = new StringBuilder();
		UserModel user = this.userService.selectById(swift.getCreateUser());
		key.append(user.getUserName()).append("/").append(swift.getName());
		
		List<MonitorViewYModel> data = this.monitorService.getMonitorData(key.toString(), chartId, strategy,isTimeAveraging,format);
		return data;
	}
	@Override
	public List<MonitorViewYModel> getDbData(Long serverId,Long chartId, Integer strategy,boolean isTimeAveraging,int format) {
		DbModel db = this.dbService.selectById(serverId);
		if(db == null || db.getMclusterId() == null)
			throw new ValidateException("参数不合法");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", db.getMclusterId());
		List<ContainerModel> containers = this.containerService.selectNodeContainersByMap(map);	
		if(containers == null || containers.isEmpty())
			throw new ValidateException("参数不合法");
		List<MonitorViewYModel> data = this.monitorService.getMonitorData(containers.get(0).getIpAddr(), chartId, strategy,isTimeAveraging,format);
		return data;
	}
	public List<MonitorViewYModel> getGceData(Long serverId,Long chartId, Integer strategy,boolean isTimeAveraging,int format) {
		GceServer gce = this.gceServerService.selectById(serverId);
		if(gce == null || gce.getGceClusterId() == null)
			throw new ValidateException("参数不合法");
		
		List<GceContainer> containers = this.gceContainerService.selectByGceClusterId(gce.getGceClusterId());
		if(containers == null || containers.isEmpty())
			throw new ValidateException("参数不合法");
		List<MonitorViewYModel> data = this.monitorService.getMonitorData(containers.get(0).getContainerName(), chartId, strategy,isTimeAveraging,format);
		return data;
	}
	public List<MonitorViewYModel> getSlbData(Long serverId,Long chartId, Integer strategy,boolean isTimeAveraging,int format) {
		SlbServer slb = this.slbServerService.selectById(serverId);
		if(slb == null || slb.getSlbClusterId() == null)
			throw new ValidateException("参数不合法");
		List<SlbContainer> containers = this.slbContainerService.selectBySlbClusterId(slb.getSlbClusterId());
		if(containers == null || containers.isEmpty())
			throw new ValidateException("参数不合法");
		List<MonitorViewYModel> data = this.monitorService.getMonitorData(containers.get(0).getContainerName(), chartId, strategy,isTimeAveraging,format);
		return data;
	}
	@Override
	public void addMonitorPartition() {
		Map<String,Object> params = new HashMap<String,Object>();
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(params);
		for (MonitorIndexModel monitorIndexModel : indexs) {
			params.clear();
			params.put("dbName", monitorIndexModel.getDetailTable());
			this.monitorService.addMonitorPartition(params, new Date());
		}
	}
	
	@Override
	public void deleteMonitorPartitionThirtyDaysAgo() {
		Map<String,Object> params = new  HashMap<String,Object>();
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(params);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);    //得到30天以前
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String partitionName1 = "pa"+formatter.format(cal.getTime());
		String partitionName2 = "pb"+formatter.format(cal.getTime());
		for (MonitorIndexModel monitorIndexModel : indexs) {
			params.clear();
			params.put("dbName", monitorIndexModel.getDetailTable());
			params.put("partitionName1", partitionName1);
			params.put("partitionName2", partitionName2);
			this.monitorService.deleteMonitorPartitionThirtyDaysAgo(params);
		}
	}
	@Override
	public void collectMysqlMonitorData() {
		Map<String,Object> map = new  HashMap<String,Object>();
		List<ContainerModel> contianers = this.containerService.selectVaildNormalContainers(map);
		
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 0);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		Date date = new Date();
		logger.info("collectMysqlMonitorData start" + date);
		for (MonitorIndexModel index : indexs) {
			for (ContainerModel container : contianers) {
				this.buildTaskService.getMysqlMonitorServiceData(container, index, date);
			}
		}
	}
	@Override
	public void collectMysqlMonitorBaseData() {
		Map<String,Object> map = new  HashMap<String,Object>();
		List<ContainerModel> contianers = this.containerService.selectVaildNormalContainers(map);
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 4);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		Date date = new Date();
		logger.info("collectMysqlMonitorBaseData start" + date);
		for (MonitorIndexModel index : indexs) {
			for (ContainerModel container : contianers) {
				this.buildTaskService.collectMysqlMonitorBaseData(container, index, date);
			}
		}
		logger.info("collectMysqlMonitorBaseData end");
	}
	@Override
	public void collectMysqlMonitorBaseSpaceData() {
		Map<String,Object> map = new  HashMap<String,Object>();
		List<ContainerModel> contianers = this.containerService.selectVaildNormalContainers(map);
		
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 5);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		Date date = new Date();
		logger.info("collectMysqlMonitorBaseSpaceData start" + date);
		for (ContainerModel container : contianers) {
			map.clear();
			map.put("mclusterId", container.getMclusterId());
			List<DbModel> dbs = this.dbService.selectByMap(map);
			for (DbModel dbModel : dbs) {
				this.buildTaskService.collectMysqlMonitorBaseSpaceData(dbModel.getDbName(),container, indexs, date);
			}
		}
		logger.info("collectMysqlMonitorBaseSpaceData end");
	}
	@Override
	public void monitorErrorReport() {

		Map<String, Object> params = new HashMap<String,Object>();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar curDate = Calendar.getInstance();
		curDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH),curDate.get(Calendar.DATE), 0, 0, 0);
		params.put("end", format.format(new Date(curDate.getTimeInMillis())));
		curDate.add(Calendar.DATE, -1);
		params.put("start", format.format(new Date(curDate.getTimeInMillis())));
		List<Map<String,Object>> errors = this.monitorService.getMonitorErrorModelsByMap(params);
		
		StringBuffer buffer = new StringBuffer();
		for (Map<String, Object> map : errors) {
			buffer.append("<tr>");
			buffer.append("<th width=\"100px\">");
			buffer.append(map.get("tableName"));
			buffer.append("</th>");
			buffer.append("<th width=\"100px\">");
			buffer.append(map.get("count"));
			buffer.append("</th>");
			buffer.append("</tr>");
		}
		params.clear();
		params.put("tableInfo", buffer.toString());
		
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",SERVICE_NOTICE_MAIL_ADDRESS,"乐视云平台监控错误结果通知","monitorErrorReport.ftl",params);
		mailMessage.setHtml(true);
		defaultEmailSender.sendMessage(mailMessage);
	
	}
	@Override
	public void deleteMonitorErrorData() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -DELETE_MONITOR_ERROR_DAYS_AGO);
		deleteOutData("WEBPORTAL_MONITOR_ERROR", "CREATE_TIME", new Date(cal.getTimeInMillis()));
	}
	
	
	
}
