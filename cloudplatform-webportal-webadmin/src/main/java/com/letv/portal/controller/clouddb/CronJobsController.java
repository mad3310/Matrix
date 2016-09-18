package com.letv.portal.controller.clouddb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.MonitorIndexModel;
import com.letv.portal.model.adminoplog.AoLogType;
import com.letv.portal.model.task.service.ITaskAsyncExecuteService;
import com.letv.portal.proxy.IBackupProxy;
import com.letv.portal.proxy.IContainerProxy;
import com.letv.portal.proxy.ICronJobsProxy;
import com.letv.portal.proxy.IMclusterProxy;
import com.letv.portal.proxy.IMonitorProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IMonitorIndexService;
import com.letv.portal.service.IMonitorService;
import com.letv.portal.service.adminoplog.AoLog;
import com.letv.portal.service.adminoplog.ClassAoLog;

@ClassAoLog(module="后台任务",ignore=true)
@Controller
@RequestMapping("/cronJobs")
public class CronJobsController {
	
	@Autowired
	private IMonitorProxy monitorProxy;
	@Autowired
	private IMclusterProxy mclusterProxy;
	@Autowired
	private IContainerProxy containerProxy;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IBackupProxy backupProxy;
	@Autowired
	private IMonitorService monitorService;
	@Autowired
	private IMonitorIndexService monitorIndexService;
	@Autowired
	private IFixedPushService fixedPushService;
	@Autowired
	private IBuildTaskService buildTaskService;
	@Autowired
	private ITaskAsyncExecuteService taskAsyncExecuteService;

	@Autowired
	private ICronJobsProxy cronJobsProxy;
	
	private final static Logger logger = LoggerFactory.getLogger(CronJobsController.class);
		
	/**Methods Name: collectMclusterMonitorData <br>
	 * Description: 收集图表监控数据<br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/mcluster/monitor",method=RequestMethod.GET)   
	public @ResponseBody ResultObject collectMclusterMonitorData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.collectMclusterServiceData();
		return obj;
	}
	@RequestMapping(value="/monitor/syncToEs/{dbName}/{strategy}",method=RequestMethod.GET)
	public @ResponseBody ResultObject syncToEs(@PathVariable String dbName,@PathVariable int strategy,ResultObject obj) {
		this.monitorService.syncMonitorFromDbToEs(dbName,strategy);
		return obj;
	}
    @RequestMapping(value="/monitor/syncToEs/{dbName}/{start}/{end}",method=RequestMethod.GET)
     public @ResponseBody ResultObject syncToEs1(@PathVariable String dbName,@PathVariable String start,@PathVariable String end,ResultObject obj) {
        try {
            SimpleDateFormat compactDate = new SimpleDateFormat(
                    "yyyyMMddHHmm");

            this.monitorService.syncMonitorFromDbToEs(dbName,compactDate.parse(start),compactDate.parse(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }
	/**Methods Name: collectMclusterMonitorData <br>
	 * Description: 收集图表监控数据2<br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/cluster/monitor",method=RequestMethod.GET)   
	public @ResponseBody ResultObject collectClusterMonitorData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.collectClusterServiceData();
		return obj;
	}
	@RequestMapping(value="/oss/monitor",method=RequestMethod.GET)   
	public @ResponseBody ResultObject collectOSSMonitorData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.collectOSSServiceData();
		return obj;
	}
	/**Methods Name: checkMclusterStatus <br>
	 * Description: 检查mcluster健康状况<br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/mcluster/status/check",method=RequestMethod.GET)
	public @ResponseBody ResultObject checkMclusterStatus(HttpServletRequest request,ResultObject obj) {
		logger.info("check mcluster status");
    	this.mclusterProxy.checkStatus();
    	return obj;
	}
	/**Methods Name: checkContainerStatus <br>
	 * Description: 检查container健康状况<br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/container/status/check",method=RequestMethod.GET)   
	public @ResponseBody ResultObject checkContainerStatus(HttpServletRequest request,ResultObject obj) {
		logger.info("check container status");
    	this.containerProxy.checkStatus();
    	return obj;
	}
	/**Methods Name: checkMclusterCount <br>
	 * Description: 检查RDS集群数量一致性<br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/mcluster/count/check",method=RequestMethod.GET)   
	public @ResponseBody ResultObject checkMclusterCount(HttpServletRequest request,ResultObject obj) {
		logger.info("check Mcluster Count");
    	this.mclusterProxy.checkCount();
    	return obj;
	}
	
	/**Methods Name: checkGceClusterCount <br>
	 * Description: 检查服务集群一致性<br>
	 * @author name: howie
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/cluster/count/check",method=RequestMethod.GET)   
	public @ResponseBody ResultObject checkGceClusterCount(HttpServletRequest request,ResultObject obj) {
		logger.info("check gceCluster Count");
		this.cronJobsProxy.checkCount();
		return obj;
	}
	/**Methods Name: deleteMonitorMonthAgo <br>
	 * Description: 删除一个月之前监控数据<br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@AoLog(desc="删除一个月之前监控数据",type=AoLogType.DELETE,ignore=true)
	@RequestMapping(value="/mcluster/monitor",method=RequestMethod.DELETE)   
	public @ResponseBody ResultObject deleteMonitorMonthAgo(HttpServletRequest request,ResultObject obj) {
		logger.info("deleteMonitorMonthAgo");
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 1);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);    //得到前一个月
		long date = cal.getTimeInMillis();
		Date monthAgo = new Date(date);
		for (MonitorIndexModel monitorIndexModel : indexs) {
			this.monitorProxy.deleteOutData(monitorIndexModel.getDetailTable(), "MONITOR_DATE", monthAgo);
		}
    	return obj;
	}
	
	/**Methods Name: dbBackup <br>
	 * Description: db数据库备份<br>
	 * @author name: liuhao1
	 * @param count  
	 * @param request
	 * @param obj
	 * @return
	 */
	@AoLog(desc="db数据库备份",type=AoLogType.INSERT,ignore=true)
	@RequestMapping(value="/db/backup",method=RequestMethod.POST)   
	public @ResponseBody ResultObject dbBackup(int count, HttpServletRequest request,ResultObject obj) {
		logger.info("db backup");
		if(count ==0 || count<0)
			count = 5;
		
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		String backupDate = null==params.get("backupDate") ? null : (String)params.get("backupDate") ;
		Integer backupMaxTime = null==params.get("backupMaxTime") ? null : Integer.parseInt((String)params.get("backupMaxTime"));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date d = new Date();
		try {
			if(null != backupDate) {
				d = sdf.parse(backupDate);
			}
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		this.backupProxy.backupTask(count, backupMaxTime, d);
		return obj;
	}

	/**Methods Name: dbBackup <br>
	 * Description: db数据库备份报告<br>
	 * @author name: liuhao1
	 * @param count  
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/db/backup/report",method=RequestMethod.GET)   
	public @ResponseBody ResultObject dbBackupReport(HttpServletRequest request,ResultObject obj) {
		this.backupProxy.backupTaskReport();
		return obj;
	}
	
	/**Methods Name: deleteBackupHalfMonthAgo <br>
	 * Description: <br>
	 * @author name: liuhao1
	 * @param request
	 * @param obj
	 * @return
	 */
	@AoLog(desc="删除半个月以前的备份",type=AoLogType.DELETE,ignore=true)
	@RequestMapping(value="/db/backup",method=RequestMethod.DELETE)   
	public @ResponseBody ResultObject deleteBackupHalfMonthAgo(HttpServletRequest request,ResultObject obj) {
		logger.info("deleteBackupHalfMonthAgo");
    	this.backupProxy.deleteOutData();
    	return obj;
	}
	
	/**Methods Name: deleteMonitorPartitionMonthAgo <br>
	 * Description: 删除30天之前分区<br>
	 * @author name: 
	 * @param request
	 * @param obj
	 * @return
	 */
	@AoLog(desc="删除30天之前分区",type=AoLogType.DELETE,ignore=true)
	@RequestMapping(value="/monitor/deletePartition",method=RequestMethod.DELETE)   
	public @ResponseBody ResultObject deleteMonitorPartitionThirtyDaysAgo(HttpServletRequest request,ResultObject obj) {
		logger.info("deleteMonitorPartitionThirtyDaysAgo");
		this.monitorProxy.deleteMonitorPartitionThirtyDaysAgo();
    	return obj;
	}
	
	/**Methods Name: addMonitorPartition <br>
	 * Description: 添加监控表分区<br>
	 * @author name:
	 * @param request
	 * @param obj
	 * @return
	 */
	@AoLog(desc="添加监控表分区",type=AoLogType.INSERT,ignore=true)
	@RequestMapping(value="/monitor/addPartition",method=RequestMethod.GET)   
	public @ResponseBody ResultObject addMonitorPartition(HttpServletRequest request,ResultObject obj) {
		logger.info("addMonitorPartition");
		this.monitorProxy.addMonitorPartition();
		return obj;
	}
	
	
	/**
	  * @Title: collectMysqlMonitorData
	  * @Description: 收集mysql监控数据保存到mysql页面展示表（监控监控、资源监控、键缓存监控、InnoDB监控）
	  * @param request
	  * @param obj
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月3日 上午9:46:51
	  */
	@RequestMapping(value="/monitor/collectMysqlMonitorData",method=RequestMethod.GET)   
	public @ResponseBody ResultObject collectMysqlMonitorData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.collectMysqlMonitorData();
		return obj;
	}
	
	/**
	  * @Title: collectMysqlMonitorBaseData
	  * @Description: 调用Python接口维护mysql监控历史数据
	  * @param request
	  * @param obj
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月3日 上午9:47:57
	  */
	@RequestMapping(value="/monitor/collectMysqlMonitorBaseData",method=RequestMethod.GET)   
	public @ResponseBody ResultObject collectMysqlMonitorBaseData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.collectMysqlMonitorBaseData();
		return obj;
	}
	
	/**
	  * @Title: collectMysqlMonitorBaseSpaceData
	  * @Description: 保存数据库大小和表大小历史及维护页面展示表（表空间监控）
	  * @param request
	  * @param obj
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月3日 上午9:49:46
	  */
	@RequestMapping(value="/monitor/collectMysqlMonitorBaseSpaceData",method=RequestMethod.GET)   
	public @ResponseBody ResultObject collectMysqlMonitorBaseSpaceData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.collectMysqlMonitorBaseSpaceData();
		return obj;
	}
	
	/**
	  * @Title: monitorErrorReport
	  * @Description: 发送监控错误报告
	  * @param request
	  * @param obj
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月10日 下午2:13:52
	  */
	@RequestMapping(value="/monitor/error/report",method=RequestMethod.GET)   
	public @ResponseBody ResultObject monitorErrorReport(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.monitorErrorReport();
		return obj;
	}
	/**
	  * @Title: deleteMonitorErrorData
	  * @Description: 删除监控错误历史数据
	  * @param request
	  * @param obj
	  * @return ResultObject   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月10日 下午3:57:28
	  */
	@RequestMapping(value="/monitor/error/clear",method=RequestMethod.GET)   
	public @ResponseBody ResultObject deleteMonitorErrorData(HttpServletRequest request,ResultObject obj) {
		this.monitorProxy.deleteMonitorErrorData();
		return obj;
	}
	
	/**
	 * 固资信息对账
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/fixed/check",method=RequestMethod.GET)   
	public @ResponseBody ResultObject fixedCheck(HttpServletRequest request,ResultObject obj) {
		this.fixedPushService.fixedCheckAllFromCmdb();
		return obj;
	}
	
	
	/**
	 * 数据库表反例检查
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/db/struc/check",method=RequestMethod.GET)   
	public @ResponseBody ResultObject nopkCheck(HttpServletRequest request,ResultObject obj) {
		this.buildTaskService.checkDbStruc();
		return obj;
	}
	/**
	 * 工作流异步重试
	 * @param request
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/task/async/retry",method=RequestMethod.GET)   
	public @ResponseBody ResultObject taskAsyncRetry(HttpServletRequest request,ResultObject obj) {
		this.taskAsyncExecuteService.taskAsyncRetry();
		return obj;
	}

	/*@AoLog(desc="增加集群节点",type=AoLogType.UPDATE)
	@RequestMapping(value = "/dilatation", method=RequestMethod.POST)
	public @ResponseBody ResultObject dilatation(Long mclusterId,int count,ResultObject result) {
		if(mclusterId == null || count <=0)
			throw new ValidateException("参数不合法");
		this.mclusterProxy.addContainerOnMcluster(mclusterId,count);
		return result;
	}
	@AoLog(desc="删除单个容器",type=AoLogType.DELETE)
	@RequestMapping(value="/container/{containerId}",method=RequestMethod.DELETE)
	public @ResponseBody ResultObject delete(@PathVariable Long containerId,ResultObject result) {
		if(null == containerId)
			throw new ValidateException("参数不合法");
		ContainerModel containerModel = this.containerService.selectById(containerId);
		if(null == containerModel)
			throw new ValidateException("参数不合法");
		this.containerProxy.deleteAndBuild(containerModel);
		return result;
	}*/
}
