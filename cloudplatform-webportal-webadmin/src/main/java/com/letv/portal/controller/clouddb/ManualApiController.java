package com.letv.portal.controller.clouddb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.letv.portal.model.ContainerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.adminoplog.AoLogType;
import com.letv.portal.proxy.IMclusterProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.adminoplog.AoLog;
import com.letv.portal.service.adminoplog.ClassAoLog;
import com.letv.portal.zabbixPush.IZabbixPushService;

@Controller
@ClassAoLog(ignore = true)
@RequestMapping("/manualApi")
public class ManualApiController {
	
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	public IZabbixPushService zabbixPushService;
	@Autowired
	public IFixedPushService fixedPushService;
	@Autowired
	private IContainerService containerService;
	
	private final static Logger logger = LoggerFactory.getLogger(ManualApiController.class);
	
	@RequestMapping(value = "/V1/zabbix/{mclusterName}", method=RequestMethod.DELETE)
	public @ResponseBody ResultObject rmZabbix(@PathVariable String mclusterName,ResultObject result) {
		 List<MclusterModel> mclusters  = this.mclusterService.selectByName(mclusterName);
		 if(mclusters.isEmpty())
			 throw new ValidateException("集群不存在");
		 if(mclusters.size()>1) {
			 throw new ValidateException("集群名不唯一");
		 }
		 Map<String, Object> map = new HashMap<String, Object>();
		 map.put("mclusterId", mclusters.get(0).getId());
		 map.put("type","mclustervip");
	     this.zabbixPushService.deleteMutilContainerPushZabbixInfo(this.containerService.selectWithHClusterNameByMap(map));
	     result.getMsgs().add("集群监控删除成功");
	     return result;
	}
	@RequestMapping(value = "/V1/zabbix", method=RequestMethod.POST)
	public @ResponseBody ResultObject addZabbix(@RequestParam String mclusterName,ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectByName(mclusterName);
		if(mclusters.isEmpty())
			throw new ValidateException("集群不存在");
		if(mclusters.size()>1) {
			throw new ValidateException("集群名不唯一");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusters.get(0).getId());
		map.put("type","mclustervip");
		boolean addResult = this.zabbixPushService.createMultiContainerPushZabbixInfo(this.containerService.selectWithHClusterNameByMap(map));
		if(addResult) {
			result.getMsgs().add("集群监控添加成功");
		} else {
			result.getMsgs().add("集群监控添加失败");
		}
		return result;
	}
	
	@RequestMapping(value = "/V1/fixed/{mclusterName}", method=RequestMethod.DELETE)
	public @ResponseBody ResultObject rmFixed(@PathVariable String mclusterName,ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectByName(mclusterName);
		if(mclusters.isEmpty())
			throw new ValidateException("集群不存在");
		if(mclusters.size()>1) {
			throw new ValidateException("集群名不唯一");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusters.get(0).getId());
		this.fixedPushService.deleteMutilContainerPushFixedInfo(this.containerService.selectByMap(map));
		result.getMsgs().add("集群固资信息删除成功");
		return result;
	}
	@RequestMapping(value = "/V1/fixed", method=RequestMethod.DELETE)
	public @ResponseBody ResultObject rmFixed(ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		int sum = 0;
		int success = 0;
		int fail = 0;
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			boolean isSuccess = this.fixedPushService.deleteMutilContainerPushFixedInfo(this.containerService.selectByMap(map));
			if(isSuccess) {
				success++;
			} else {
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("delete mcluster sum:" + sum);
		result.getMsgs().add("delete mcluster success:" + success);
		result.getMsgs().add("delete mcluster fail:" + fail);
		return result;
	}
	@RequestMapping(value = "/V1/fixed", method=RequestMethod.POST)
	public @ResponseBody ResultObject addFixed(@RequestParam  String mclusterName,ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectByName(mclusterName);
		if(mclusters.isEmpty())
			throw new ValidateException("集群不存在");
		if(mclusters.size()>1) {
			throw new ValidateException("集群名不唯一");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusters.get(0).getId());
		boolean addResult = this.fixedPushService.createMutilContainerPushFixedInfo(this.containerService.selectByMap(map));
		if(addResult) {
			result.getMsgs().add("集群固资信息创建成功");
		} else {
			result.getMsgs().add("集群固资信息创建失败");
		}
		return result;
	}
    @RequestMapping(value = "/V1/fixed/byDetail", method=RequestMethod.POST)
	public @ResponseBody ResultObject addFixedByDetail(@RequestParam(required = true)  String ip,@RequestParam(required = true)String name,@RequestParam(required = true)String hostIp,ResultObject result) {
		List<ContainerModel> containers = new ArrayList<ContainerModel>();
		ContainerModel containerModel = new ContainerModel();
		containerModel.setIpAddr(ip);
		containerModel.setContainerName(name);
		containerModel.setHostIp(hostIp);
        containers.add(containerModel);
		boolean addResult = this.fixedPushService.createMutilContainerPushFixedInfo(containers);
		if(addResult) {
			result.getMsgs().add("集群固资信息创建成功");
		} else {
			result.getMsgs().add("集群固资信息创建失败");
		}
		return result;
	}
	@RequestMapping(value = "/V1/fixed/syncAll", method=RequestMethod.POST)
	public @ResponseBody ResultObject syncAll(ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		int sum = 0;
		int success = 0;
		int fail = 0;
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			boolean isSuccess = this.fixedPushService.deleteMutilContainerPushFixedInfo(this.containerService.selectByMap(map));
			if(isSuccess) {
				success++;
			} else {
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("delete mcluster sum:" + sum);
		result.getMsgs().add("delete mcluster success:" + success);
		result.getMsgs().add("delete mcluster fail:" + fail);
		sum = 0;
		success =0;
		fail = 0;
		
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			boolean isSuccess = this.fixedPushService.createMutilContainerPushFixedInfo(this.containerService.selectByMap(map));
			if(isSuccess) {
				success++;
			} else {
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("add mcluster sum:" + sum);
		result.getMsgs().add("add mcluster success:" + success);
		result.getMsgs().add("add mcluster fail:" + fail);
		return result;
	}
	@RequestMapping(value = "/V1/zabbix/syncAll", method=RequestMethod.POST)
	public @ResponseBody ResultObject syncAllZabbix(ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		int sum = 0;
		int success = 0;
		int fail = 0;
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			map.put("type","mclustervip");
			boolean isSuccess = this.zabbixPushService.deleteMutilContainerPushZabbixInfo(this.containerService.selectWithHClusterNameByMap(map));
			if(isSuccess) {
				success++;
			} else {
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("delete mcluster sum:" + sum);
		result.getMsgs().add("delete mcluster success:" + success);
		result.getMsgs().add("delete mcluster fail:" + fail);
		sum = 0;
		success =0;
		fail = 0;
		
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			map.put("type","mclustervip");
			boolean isSuccess = this.zabbixPushService.createMultiContainerPushZabbixInfo(this.containerService.selectWithHClusterNameByMap(map));
			if(isSuccess) {
				success++;
			} else {
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("add mcluster sum:" + sum);
		result.getMsgs().add("add mcluster success:" + success);
		result.getMsgs().add("add mcluster fail:" + fail);
		return result;
	}
	@RequestMapping(value = "/V1/zabbix/delDataContainer", method=RequestMethod.DELETE)
	public @ResponseBody ResultObject delDataContainer(ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		int sum = 0;
		int success = 0;
		int fail = 0;
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			boolean isSuccess = this.zabbixPushService.deleteMutilContainerPushZabbixInfo(this.containerService.selectWithHClusterNameByMap(map));
			if(isSuccess) {
				success++;
			} else {
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("delete mcluster sum:" + sum);
		result.getMsgs().add("delete mcluster success:" + success);
		result.getMsgs().add("delete mcluster fail:" + fail);
		return result;
	}
	
}
