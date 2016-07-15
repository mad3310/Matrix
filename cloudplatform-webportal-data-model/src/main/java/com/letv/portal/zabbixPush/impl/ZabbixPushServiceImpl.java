package com.letv.portal.zabbixPush.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.HttpClient;
import com.letv.portal.fixedPush.impl.FixedPushServiceImpl;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.zabbix.HostParam;
import com.letv.portal.model.zabbix.InterfacesModel;
import com.letv.portal.model.zabbix.UserMacroParam;
import com.letv.portal.model.zabbix.ZabbixPushDeleteModel;
import com.letv.portal.model.zabbix.ZabbixPushModel;
import com.letv.portal.service.IContainerService;
import com.letv.portal.zabbixPush.IZabbixPushService;
import com.mysql.jdbc.StringUtils;

@Service("zabbixPushService")
public class ZabbixPushServiceImpl implements IZabbixPushService{
	private final static Logger logger = LoggerFactory.getLogger(FixedPushServiceImpl.class);	

	@Value("${zabbix.post.url}")
	private String ZABBIX_POST_URL;
	@Value("${zabbix.name}")
	private String ZABBIX_NAME;
	@Value("${zabbix.pwd}")
	private String ZABBIX_PWD;
	@Value("${zabbix.template.normal}")
	private String ZABBIX_TEMPLATE_NORMAL;
	@Value("${zabbix.template.vip}")
	private String ZABBIX_TEMPLATE_VIP;
	@Value("${zabbix.host.groupid}")
	private String ZABBIX_HOST_GROUPID;
	@Value("${zabbix.host.proxy_hostid}")
	private String ZABBIX_HOST_PROXY_HOSTID;
	@Value("${zabbix.host.usermacro}")
	private String ZABBIX_HOST_USERMACRO;
	
	@Value("${zabbix.lsj.host.groupid}")
	private String ZABBIX_LSJ_HOST_GROUPID;//洛杉矶单独的groupId
	@Value("${zabbix.lsj.host.proxy_hostid}")
	private String ZABBIX_LSJ_HOST_PROXY_HOSTID;//洛杉矶单独的hostId
	@Value("${zabbix.lsj.host.usermacro}")
	private String ZABBIX_LSJ_HOST_USERMACRO;//洛杉矶单独的usermacro
	
	@Autowired
	private IContainerService containerService;
	@Override
	public ApiResultObject createMultiContainerPushZabbixInfo(List<ContainerModel> containerModels) {
		ApiResultObject apiResult = new ApiResultObject();
		List<String> zabbixHosts = new ArrayList<String>();
		if(containerModels.isEmpty()) {
			apiResult.setAnalyzeResult(false);
			apiResult.setResult("参数为空");
			return apiResult;
		}
		
		String result=loginZabbix();
		String auth = "";
	    if(result!=null && result.contains("_succeess")){
			String[] auths = result.split("_");
			auth = auths[0];
			logger.info("登陆zabbix系统成功");
		} else {
			logger.info("登陆zabbix系统失败");
			apiResult.setAnalyzeResult(false);
			apiResult.setResult("登陆zabbix系统失败");
			return apiResult;
		}
	    
		for(ContainerModel c:containerModels){
			apiResult = addSingleContainerPushZabbixInfo(c, auth);

			if(!apiResult.getAnalyzeResult()) {
				//推送部分失败后删除已成功的推送
				deleteContainerZabbixInfoByZabbixHosts(zabbixHosts, auth);
				return apiResult;
			} else {
				zabbixHosts.add(apiResult.getResult());
			}
		}
		return apiResult;
	}
	
	/**
	 * 根据zabbix返回的host删除zabbix推送
	 * @param zabbixHosts
	 */
	private void deleteContainerZabbixInfoByZabbixHosts(List<String> zabbixHosts, String auth) {
		ZabbixPushDeleteModel zabbixPushDeleteModel = new ZabbixPushDeleteModel();
		List<String> list = new ArrayList<String>();
		for (String host : zabbixHosts) {
			list.clear();
			list.add(host);
			zabbixPushDeleteModel.setParams(list);
			pushDeleteZabbixInfo(zabbixPushDeleteModel, auth);	
		}
	}
	
	public ApiResultObject addSingleContainerPushZabbixInfo(
			ContainerModel c, String auth) {
		ApiResultObject result = null;
		if(null != c) {
			ZabbixPushModel zabbixPushModel = new ZabbixPushModel();
			zabbixPushModel.setAuth(auth);
			String templateId = "";
			if("mclusternode".equals(c.getType()) || "mclusteraddnode".equals(c.getType())) {
				templateId = ZABBIX_TEMPLATE_NORMAL;
			} else if("mclustervip".equals(c.getType())) {
				templateId = ZABBIX_TEMPLATE_VIP;
			}
			//临时方案，以LSJ开头的使用洛杉矶的id
			HostParam params = null;
			if(c.getHcluster().getHclusterName().startsWith("LSJ")) {
				logger.info("洛杉矶地区id推送：groupId-{}，hostId-{}", ZABBIX_LSJ_HOST_GROUPID, ZABBIX_LSJ_HOST_PROXY_HOSTID);
				params = new HostParam(templateId,ZABBIX_LSJ_HOST_GROUPID,ZABBIX_LSJ_HOST_PROXY_HOSTID);
			} else {
				logger.info("非洛杉矶地区id推送：groupId-{}，hostId-{}", ZABBIX_HOST_GROUPID, ZABBIX_HOST_PROXY_HOSTID);
				params = new HostParam(templateId,ZABBIX_HOST_GROUPID,ZABBIX_HOST_PROXY_HOSTID);
			}
			
			params.setHost(c.getContainerName());

			InterfacesModel interfacesModel = new InterfacesModel();
			interfacesModel.setIp(c.getIpAddr());

			List<InterfacesModel> list = new ArrayList<InterfacesModel>();
			list.add(interfacesModel);
			params.setInterfaces(list);

			zabbixPushModel.setParams(params);
			result = pushZabbixInfo(zabbixPushModel,c.getId());	
			
			if(result.getAnalyzeResult()) {//成功
				//临时方案，以LSJ开头的使用洛杉矶的id
				UserMacroParam macro = null;
				if(c.getHcluster().getHclusterName().startsWith("LSJ")) {
					macro = new UserMacroParam(result.getResult(), ZABBIX_LSJ_HOST_USERMACRO);
				} else {
					macro = new UserMacroParam(result.getResult(), ZABBIX_HOST_USERMACRO);
				}
				
				zabbixPushModel.setParams(macro);
				zabbixPushModel.setMethod("usermacro.create");
				usermacroCreate(zabbixPushModel, result);
				if(!result.getAnalyzeResult()) {//失败后删除该条推送
					List<String> zabbixHosts = new ArrayList<String>();
					zabbixHosts.add(result.getResult());
					deleteContainerZabbixInfoByZabbixHosts(zabbixHosts, auth);
				}
			}
			
			
		}		
		return result;
	}
	
	@Override
	public ApiResultObject deleteSingleContainerPushZabbixInfo(
			ContainerModel containerModel, String auth) {
		ApiResultObject result = null;
		if(containerModel!=null){
			ZabbixPushDeleteModel zabbixPushDeleteModel = new ZabbixPushDeleteModel();
			List<String> list = new ArrayList<String>();
			list.add(containerModel.getZabbixHosts());
			zabbixPushDeleteModel.setParams(list);
			result = pushDeleteZabbixInfo(zabbixPushDeleteModel, auth);					
		}		
		return result;
	}
	/**
	 * Methods Name: deleteMutilContainerPushZabbixInfo <br>
	 * Description: 删除多个zabbix信息<br>
	 * @author name: wujun
	 * @param list
	 * @return
	 */
	@Override
	public ApiResultObject deleteMutilContainerPushZabbixInfo(List<ContainerModel> list) {
		ApiResultObject apiResult = new ApiResultObject();
		List<ContainerModel> success = new ArrayList<ContainerModel>();
		
		String result = loginZabbix();
		String auth = "";
	    if(result!=null && result.contains("_succeess")){
			String[] auths = result.split("_");
			auth = auths[0];
			logger.info("登陆zabbix系统成功");
		} else {
			logger.info("登陆zabbix系统失败");
			apiResult.setAnalyzeResult(false);
			apiResult.setResult("登陆zabbix系统失败");
			return apiResult;
		}
		
		for(ContainerModel c:list){
			apiResult = deleteSingleContainerPushZabbixInfo(c, auth);	
			if(null != apiResult && !apiResult.getAnalyzeResult()) {//删除推送失败
				for(ContainerModel suc : success) {
					addSingleContainerPushZabbixInfo(suc, auth);
				}
				break;
			} else {
				success.add(c);
			}
		}
	     return apiResult;
	}

	/**
	 * Methods Name: createContainerPushZabbixInfo <br>
	 * Description: 创建container时向zabbix系统推送信息<br>
	 * @author name: wujun
	 * @param zabbixPushModel
	 * @return
	 */
	public ApiResultObject pushZabbixInfo(ZabbixPushModel zabbixPushModel,Long containerId){
		ApiResultObject apiResult = new ApiResultObject();
		StringBuilder builder = new StringBuilder();
		try {
			String result = analysisResultMap(transResult(sendZabbixInfo(zabbixPushModel)));			
			String[] rs = null==result ? new String[]{""} : result.split("_");
			String hostId = rs[0];
			if(result.contains("_succeess")){
				ContainerModel containerModel = new ContainerModel();
				containerModel.setId(containerId);
				containerModel.setZabbixHosts(hostId);
				containerService.updateBySelective(containerModel);
				logger.info("推送zabbix系统成功{}", result);
				apiResult = getApiResultObject(true, hostId);
			} else {			
				builder.append("推送zabbix系统失败").append(result);
				apiResult = getApiResultObject(false, builder.toString());
			}					
			
		} catch (Exception e) {
			builder.append("推送zabbix系统失败").append(e.getMessage());
			apiResult = getApiResultObject(false, builder.toString());
		}
		return apiResult;
	}; 
	
	public void usermacroCreate(ZabbixPushModel zabbixPushModel, ApiResultObject apiResult){
		String result = analysisResultMap(transResult(sendZabbixInfo(zabbixPushModel)));
		if(StringUtils.isNullOrEmpty(result) || !result.contains("_succeess")) {
			apiResult.setAnalyzeResult(false);
		}
	}; 
	
	/**
	 * Methods Name: createContainerPushZabbixInfo <br>
	 * Description: 创建container时向zabbix系统推送信息<br>
	 * @author name: wujun
	 * @param zabbixPushDeleteModel
	 * @return
	 */
	public ApiResultObject pushDeleteZabbixInfo(ZabbixPushDeleteModel zabbixPushDeleteModel, String auth){
		ApiResultObject apiResult = new ApiResultObject();
		StringBuilder builder = new StringBuilder();
	    if(null != auth){
			try {
				zabbixPushDeleteModel.setAuth(auth);
				String result = analysisResultMap(transResult(sendZabbixInfo(zabbixPushDeleteModel)));		
				String[] rs = null==result ? new String[]{""} : result.split("_");
				String hostId = rs[0];
				if(result.contains("_succeess")){
					builder.append("推送zabbix系统成功").append(hostId);
					apiResult = getApiResultObject(true, builder.toString());
				}else {			
					builder.append("推送zabbix系统失败").append(hostId);
					apiResult = getApiResultObject(false, builder.toString());
				}					
				
			} catch (Exception e) {
				builder.append("推送zabbix系统失败").append(e.getMessage());
				apiResult = getApiResultObject(false, builder.toString());
			}
	    } else {
	    	builder.append("zabbix登录认证为空");
	    	apiResult = getApiResultObject(false, builder.toString());
	    }
		return apiResult;
	}; 
	
	private ApiResultObject getApiResultObject(boolean analyzeResult, String result) {
		logger.info("调用zabbix结果：{}", result);
		ApiResultObject apiResult = new ApiResultObject();
		apiResult.setAnalyzeResult(analyzeResult);
		apiResult.setResult(result);
		return apiResult;
	}
	/**
	 * Methods Name: loginZabbix <br>
	 * Description:登陆zabbix系统<br>
	 * @author name: wujun
	 * @return
	 */
	public String loginZabbix(){
		String loginResult = null;
		String url=ZABBIX_POST_URL;	
		String jsonString ="{\"jsonrpc\":\"2.0\",\"method\":\"user.login\",\"params\":{\"user\":\""+ZABBIX_NAME+"\",\"password\":\""+ZABBIX_PWD+"\"},\"id\":1}"; 
		String result = HttpClient.postObject(url, jsonString,null,null);
		try {
			loginResult = analysisResult(transResult(result));
		} catch (Exception e) {
			logger.info("登陆zabbix系统失败"+e.getMessage());
		}
		return loginResult;
	}; 
	/**
	 * Methods Name: sendZabbixInfo <br>
	 * Description: 向zabbix系统发送信息<br>
	 * @author name: wujun
	 * @throws Exception 
	 */ 
	public String sendZabbixInfo(Object object){
		String url=ZABBIX_POST_URL;			
		String fixedPushString =  JSON.toJSON(object).toString();
		logger.info("params:" + fixedPushString);
		String result = HttpClient.postObject(url, fixedPushString,null,null);		
		return result;
	};
	/**
	 * Methods Name: receviceFixedInfo <br>
	 * Description: 接受zabbix系统的信息<br>
	 * @author name: wujun
	 */
	public String receviceZabbixInfo(ZabbixPushModel zabbixPushModel)throws Exception{
		return null;
	};
	
	
	private Map<Object, Object> transResult(String result){
		Map<Object, Object> jsonResult = new HashMap<Object, Object>();
		jsonResult = JSON.parseObject(result, Map.class);
		return jsonResult;
	}
	
	
	private String analysisResult(Map<Object, Object> map)throws Exception{
		String result = null;
		if(map!=null){
			result = (String) map.get("result");
			if("".equals(result)||null==result){
			    result = (String)map.get("error");
			    result+="_error";
			}else{
				result+="_succeess";
			}
		}
		return result;
	}
	
	private String analysisResultMap(Map<Object, Object> map){
		Map<Object, Object> resulteMap = new HashMap<Object, Object>();
		String result = null; 
		if(map!=null){
			resulteMap = (Map<Object, Object>) map.get("result");
			if("".equals(resulteMap)||null==resulteMap){
			    result = ((Map<Object, Object>)map.get("error")).get("data").toString();
			    result+="_error";
			}else{
				String[] arg = null;
				if(null != resulteMap.get("hostids")){
					arg = resulteMap.get("hostids").toString().split("\"");
				} else if(null != resulteMap.get("hostmacroids")) {
					arg = resulteMap.get("hostmacroids").toString().split("\"");
				}
				if(null != arg) {
					result = arg[1];
					result+="_succeess";
				}
			}
		}
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public List<String> getZabbixHostIdByContainerName(String containerName, String auth){
		List<String> hostIds = new ArrayList<String>();
		String url = ZABBIX_POST_URL;	
		String jsonString ="{\"jsonrpc\":\"2.0\",\"method\":\"host.get\",\"params\":{\"output\":\"extend\",\"filter\":{\"host\":\""+containerName+"\"}},\"auth\":\""+auth+"\",\"id\":1}"; 
		String result = HttpClient.postObject(url, jsonString,null,null);
		Map<Object, Object> ret = transResult(result);
		List<Object> list = (List<Object>) ret.get("result");
		for (Object object : list) {
			Map<Object, Object> info = (Map<Object, Object>) object;
			hostIds.add((String) info.get("hostid"));
		}
		return hostIds;
	};
	
}

