package com.letv.portal.controller.clouddb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.letv.common.result.ApiResultObject;
import com.letv.common.result.ResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.adminoplog.ClassAoLog;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbContainerService;
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
	@Autowired
	private IGceContainerService gceContainerService;
	@Autowired
	private IGceClusterService gceClusterService;
	@Autowired
	private ISlbContainerService slbContainerService;
	@Autowired
	private ISlbClusterService slbClusterService;
	
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
		 map.put("types", new String[]{"mclustervip"});
		 List<ContainerModel> containers = this.containerService.selectWithHClusterNameByMap(map);
	     this.zabbixPushService.deleteMutilContainerPushZabbixInfo(containers);
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
		map.put("types", new String[]{"mclustervip"});
		List<ContainerModel> containers = this.containerService.selectWithHClusterNameByMap(map);
		ApiResultObject apiResult = this.zabbixPushService.createMultiContainerPushZabbixInfo(containers);
		if(apiResult.getAnalyzeResult()) {
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
			List<ContainerModel> containers = this.containerService.selectByMap(map);
			ApiResultObject apiResult = this.fixedPushService.deleteMutilContainerPushFixedInfo(containers);
			if(apiResult.getAnalyzeResult()) {
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
		List<ContainerModel> containers = this.containerService.selectByMap(map);
		ApiResultObject addResult = this.fixedPushService.createMutilContainerPushFixedInfo(containers);
		if(addResult.getAnalyzeResult()) {
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
        ApiResultObject apiResult = this.fixedPushService.createMutilContainerPushFixedInfo(containers);
		if(apiResult.getAnalyzeResult()) {
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
			List<ContainerModel> containers = this.containerService.selectByMap(map);
			ApiResultObject apiResult = this.fixedPushService.deleteMutilContainerPushFixedInfo(containers);
			if(apiResult.getAnalyzeResult()) {
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
			List<ContainerModel> containers = this.containerService.selectByMap(map);
			ApiResultObject apiResult = this.fixedPushService.createMutilContainerPushFixedInfo(containers);
			if(apiResult.getAnalyzeResult()) {
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
			map.put("types", new String[]{"mclustervip"});
			List<ContainerModel> containers = this.containerService.selectWithHClusterNameByMap(map);
			ApiResultObject apiResult = this.zabbixPushService.deleteMutilContainerPushZabbixInfo(containers);
			if(apiResult.getAnalyzeResult()) {
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
			List<ContainerModel> containers = this.containerService.selectWithHClusterNameByMap(map);
			ApiResultObject apiResult = this.zabbixPushService.createMultiContainerPushZabbixInfo(containers);
			if(apiResult.getAnalyzeResult()) {
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
			map.put("types", new String[]{"mclusternode", "mclusteraddnode"});
			List<ContainerModel> containers = this.containerService.selectWithHClusterNameByMap(map);
			ApiResultObject apiResult = this.zabbixPushService.deleteMutilContainerPushZabbixInfo(containers);
			if(apiResult.getAnalyzeResult()) {
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
	
	@RequestMapping(value = "/V1/zabbix/checkAll", method=RequestMethod.GET)
	public @ResponseBody ResultObject checkAllRdsZabbix(ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		int sum = 0;
		int success = 0;
		int fail = 0;
		StringBuilder builder = new StringBuilder();
		
		String loginResult = zabbixPushService.loginZabbix();
		String auth = "";
	    if(loginResult!=null && loginResult.contains("_succeess")){
			String[] auths = loginResult.split("_");
			auth = auths[0];
			logger.info("登陆zabbix系统成功");
		} else {
			logger.info("登陆zabbix系统失败");
			return null;
		}
		
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			map.put("types", new String[]{"mclustervip"});
			List<ContainerModel> containers = this.containerService.selectWithHClusterNameByMap(map);
			List<String> ret = null;
			if(null!=containers && containers.size()!=0) {
				ret = this.zabbixPushService.getZabbixHostIdByContainerName(containers.get(0).getContainerName(), auth);
			}
			
			if(null!=ret && ret.contains(containers.get(0).getZabbixHosts())) {
				success++;
			} else {
				builder.append(mclusterModel.getId()).append(",");
				fail ++;
			}
			sum++;
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		result.getMsgs().add("check mcluster sum:" + sum);
		result.getMsgs().add("check mcluster success:" + success);
		result.getMsgs().add("check mcluster fail:" + fail);
		result.getMsgs().add("check mcluster fail mclusterIds:[" + builder.toString() +"]");
		logger.info(result.toString());
		return result;
	}
	@RequestMapping(value = "/V1/rds/fixed/pushAll", method=RequestMethod.GET)
	public @ResponseBody ResultObject pushAllRdsFixed(ResultObject result) {
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		int sum = 0;
		int success = 0;
		int fail = 0;
		StringBuilder builder = new StringBuilder();
		for (MclusterModel mclusterModel : mclusters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mclusterId", mclusterModel.getId());
			List<ContainerModel> containers = this.containerService.selectByMap(map);
			ApiResultObject apiResult = this.fixedPushService.createMutilContainerPushFixedInfo(containers);
			if(null!=apiResult && apiResult.getAnalyzeResult()) {
				success++;
			} else {
				builder.append(mclusterModel.getId()).append(",");
				fail ++;
			}
			sum++;
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		result.getMsgs().add("add mcluster sum:" + sum);
		result.getMsgs().add("add mcluster success:" + success);
		result.getMsgs().add("add mcluster fail:" + fail);
		result.getMsgs().add("add mcluster fail mclusterIds:[" + builder.toString() +"]");
		logger.info(result.toString());
		return result;
	}
	
	@RequestMapping(value = "/V1/gce/fixed/pushAll", method=RequestMethod.GET)
	public @ResponseBody ResultObject pushAllGceFixed(ResultObject result) {
		List<GceCluster> gceClusters = this.gceClusterService.selectValidCluster();
		int sum = 0;
		int success = 0;
		int fail = 0;
		StringBuilder builder = new StringBuilder();
		for (GceCluster gce : gceClusters) {
			List<GceContainer> containers = this.gceContainerService.selectByGceClusterId(gce.getId());
			ApiResultObject apiResult = null;
			for (GceContainer container : containers) {
				if(container.getIpAddr().startsWith("10.")) {
					apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
				}
				if(null==apiResult || !apiResult.getAnalyzeResult()) {
					break;
				}
				try {
					Thread.sleep(500l);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if(null!=apiResult && apiResult.getAnalyzeResult()) {
				success++;
			} else {
				builder.append(gce.getId()).append(",");
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("add gcecluster sum:" + sum);
		result.getMsgs().add("add gcecluster success:" + success);
		result.getMsgs().add("add gcecluster fail:" + fail);
		result.getMsgs().add("add gcecluster fail gceclusterIds:[" + builder.toString() +"]");
		logger.info(result.toString());
		return result;
	}
	
	
	@RequestMapping(value = "/V1/slb/fixed/pushAll", method=RequestMethod.GET)
	public @ResponseBody ResultObject pushAllSlbFixed(ResultObject result) {
		List<SlbCluster> slbClusters = this.slbClusterService.selectValidCluster();
		int sum = 0;
		int success = 0;
		int fail = 0;
		StringBuilder builder = new StringBuilder();
		for (SlbCluster slb : slbClusters) {
			List<SlbContainer> containers = this.slbContainerService.selectBySlbClusterId(slb.getId());
			ApiResultObject apiResult = null;
			for (SlbContainer container : containers) {
				if(container.getIpAddr().startsWith("10.")) {
					apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
				}
				if(null==apiResult || !apiResult.getAnalyzeResult()) {
					break;
				}
				try {
					Thread.sleep(500l);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if(null!=apiResult && apiResult.getAnalyzeResult()) {
				success++;
			} else {
				builder.append(slb.getId()).append(",");
				fail ++;
			}
			sum++;
		}
		result.getMsgs().add("add slbcluster sum:" + sum);
		result.getMsgs().add("add slbcluster success:" + success);
		result.getMsgs().add("add slbcluster fail:" + fail);
		result.getMsgs().add("add slbcluster fail slbclusterIds:[" + builder.toString() +"]");
		logger.info(result.toString());
		return result;
	}
	
}
