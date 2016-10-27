package com.letv.portal.controller.clouddb;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.model.BackupDTO;
import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.proxy.IBackupProxy;
import com.letv.portal.service.IBackupService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.adminoplog.ClassAoLog;

@ClassAoLog(module="RDS管理/备份与恢复")
@Controller
@RequestMapping("/backup")
public class BackupController {
	
	@Autowired
	private IBackupService backupService;
	@Autowired
	private IBackupProxy backupProxy;
	@Autowired
	private IMclusterService mclusterService;
	
	private final static Logger logger = LoggerFactory.getLogger(BackupController.class);
		
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(HttpServletRequest request,Page page,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("dbName", StringUtil.transSqlCharacter(request.getParameter("dbName")));
		params.put("mclusterName", StringUtil.transSqlCharacter(request.getParameter("mclusterName")));
		params.put("orderBy", "START_TIME");
		params.put("isAsc", true);
		obj.setData(this.backupService.selectPageByParams(page, params));
		return obj;
	}
	
	@RequestMapping(value="/latestLog/list", method=RequestMethod.GET)   
	public @ResponseBody ResultObject latestLog(HttpServletRequest request, Page page, ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("dbName", StringUtil.transSqlCharacter(request.getParameter("dbName")));
		params.put("mclusterName", StringUtil.transSqlCharacter(request.getParameter("mclusterName")));
		params.put("orderBy", "START_TIME");
		params.put("isAsc", true);
		obj.setData(backupService.selectLatestLogPageByParams(page, params));
		return obj;
	}
	 
	@RequestMapping(value="/full", method=RequestMethod.GET)   
	public @ResponseBody ResultObject wholeBackup4Db(HttpServletRequest request, BackupResultModel mcluster, ResultObject obj) {
		//先判断集群备份开关是否打开
		MclusterModel mclusterModel = mclusterService.selectById(mcluster.getMclusterId());
		if(mclusterModel != null && !mclusterModel.getCanBackup()){
			obj.setResult(0);
			obj.addMsg("备份请求失败，请先打开备份开关再次重试！");
			return obj;
		}
		BackupResultModel dto = backupProxy.wholeBackup4Db(mcluster);
		if(null == dto) {
			obj.setResult(0);
			obj.addMsg("备份请求异常, 服务器状态不符合备份要求！");
		} else {
			obj.setData(dto);
		}
		return obj;
	}
	
	@RequestMapping(value="/incr", method=RequestMethod.GET)   
	public @ResponseBody ResultObject incrBackup4Db(HttpServletRequest request, BackupResultModel mcluster, ResultObject obj) {
		//先判断集群备份开关是否打开
		MclusterModel mclusterModel = mclusterService.selectById(mcluster.getMclusterId());
		if(mclusterModel != null && !mclusterModel.getCanBackup()){
			obj.setResult(0);
			obj.addMsg("备份请求失败，请先打开备份开关再次重试！");
			return obj;
		}
		BackupResultModel dto = backupProxy.incrBackup4Db(mcluster);
		if(null == dto) {
			obj.setResult(0);
			obj.addMsg("备份请求异常, 服务器状态不符合备份要求！");
		} else {
			obj.setData(dto);
		}
		return obj;
	}
	
	@RequestMapping(value="/check", method=RequestMethod.GET)   
	public @ResponseBody ResultObject getBackup4Db(HttpServletRequest request, BackupResultModel backupRecord, ResultObject obj) {
		BackupResultModel result = backupProxy.getBackupResulFromService(backupRecord);
		if(null == result) {
			obj.setResult(0);
			obj.addMsg("服务器请求异常！");
		} else {
			obj.setData(result);
		}
		return obj;
	}
	
}
