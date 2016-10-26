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

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.portal.enumeration.BackupStatus;
import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.StrategyModel;
import com.letv.portal.proxy.IBackupProxy;
import com.letv.portal.service.IBackupService;
import com.letv.portal.service.IMclusterService;

@Controller
@RequestMapping("/backup")
public class BackupController {
	
	@Autowired
	private IBackupService backupService;
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private IBackupProxy backupProxy;
	
	private final static Logger logger = LoggerFactory.getLogger(BackupController.class);
		
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(HttpServletRequest request,Page page,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		if(null == params.get("dbId")) {
			throw new ValidateException("参数不能为空");
		}
		params.put("orderBy", "START_TIME");
		params.put("isAsc", true);
		obj.setData(this.backupService.selectPageByParams(page, params));
		return obj;
	}
	
	@RequestMapping(value="/strategy",method=RequestMethod.GET)   
	public @ResponseBody ResultObject strategy(HttpServletRequest request, ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		if(null == params.get("mclusterId")) {
			throw new ValidateException("参数不能为空");
		}
		params.put("status", BackupStatus.SUCCESS);
		
		StrategyModel ret =backupService.selectLastedBackupRecord4Strategy(params);
		obj.setData(ret);
		return obj;
	}
	
	/*@RequestMapping(value="/full", method=RequestMethod.POST)   
	public @ResponseBody ResultObject wholeBackup4Db(HttpServletRequest request, BackupResultModel mcluster, ResultObject obj) {
		//先判断集群备份开关是否打开
		MclusterModel mclusterModel = mclusterService.selectById(mcluster.getMclusterId());
		if(mclusterModel != null && !mclusterModel.getCanBackup()){
			obj.setResult(0);
			obj.addMsg("备份请求失败，请先联系管理员打开备份权限后再次重试！");
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
	
	@RequestMapping(value="/incr", method=RequestMethod.POST)   
	public @ResponseBody ResultObject incrBackup4Db(HttpServletRequest request, BackupResultModel mcluster, ResultObject obj) {
		//先判断集群备份开关是否打开
		MclusterModel mclusterModel = mclusterService.selectById(mcluster.getMclusterId());
		if(mclusterModel != null && !mclusterModel.getCanBackup()){
			obj.setResult(0);
			obj.addMsg("备份请求失败，请先联系管理员打开备份权限后再次重试！");
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
	}*/
}
