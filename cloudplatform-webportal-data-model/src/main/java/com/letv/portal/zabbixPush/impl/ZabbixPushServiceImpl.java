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
	public Boolean createMultiContainerPushZabbixInfo(List<ContainerModel> containerModels) {
		if(containerModels.isEmpty())
			return false;
		
		int count =0;
		String result=loginZabbix();
		String auth = "";
	    if(result!=null && result.contains("_succeess")){
			String[] auths = result.split("_");
			auth = auths[0];
			logger.info("登陆zabbix系统成功");
		} else {
			logger.info("登陆zabbix系统失败");
			return false;
		}
	    
		for(ContainerModel c:containerModels){
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
				logger.info("洛杉矶id推送：groupId-{}，hostId-{}", ZABBIX_LSJ_HOST_GROUPID, ZABBIX_LSJ_HOST_PROXY_HOSTID);
				params = new HostParam(templateId,ZABBIX_LSJ_HOST_GROUPID,ZABBIX_LSJ_HOST_PROXY_HOSTID);
			} else {
				logger.info("其他id推送：groupId-{}，hostId-{}", ZABBIX_HOST_GROUPID, ZABBIX_HOST_PROXY_HOSTID);
				params = new HostParam(templateId,ZABBIX_HOST_GROUPID,ZABBIX_HOST_PROXY_HOSTID);
			}
			
			params.setHost(c.getContainerName());

			InterfacesModel interfacesModel = new InterfacesModel();
			interfacesModel.setIp(c.getIpAddr());

			List<InterfacesModel> list = new ArrayList<InterfacesModel>();
			list.add(interfacesModel);
			params.setInterfaces(list);

			zabbixPushModel.setParams(params);
			String hostId =	pushZabbixInfo(zabbixPushModel,c.getId());

			if(StringUtils.isNullOrEmpty(hostId))
				return false;
			
			//临时方案，以LSJ开头的使用洛杉矶的id
			UserMacroParam macro = null;
			if(c.getHcluster().getHclusterName().startsWith("LSJ")) {
				macro = new UserMacroParam(hostId,ZABBIX_LSJ_HOST_USERMACRO);
			} else {
				macro = new UserMacroParam(hostId,ZABBIX_HOST_USERMACRO);
			}
			
			zabbixPushModel.setParams(macro);
			zabbixPushModel.setMethod("usermacro.create");
			usermacroCreate(zabbixPushModel);
		}
		return true;
	}
	@Override
	public Boolean deleteSingleContainerPushZabbixInfo(
			ContainerModel containerModel) {
		Boolean flag = false;
		if(containerModel!=null){
			ZabbixPushDeleteModel zabbixPushDeleteModel = new ZabbixPushDeleteModel();
			List<String> list = new ArrayList<String>();
			list.add(containerModel.getZabbixHosts());
			zabbixPushDeleteModel.setParams(list);
			flag  = pushDeleteZabbixInfo(zabbixPushDeleteModel);					
		}		
		return flag;
	}
	/**
	 * Methods Name: deleteMutilContainerPushZabbixInfo <br>
	 * Description: 删除多个zabbix信息<br>
	 * @author name: wujun
	 * @param list
	 * @return
	 */
	@Override
	public Boolean deleteMutilContainerPushZabbixInfo(
			List<ContainerModel> list) {
		Boolean flag = false;
		int count =0;
		for(ContainerModel c:list){
			flag = deleteSingleContainerPushZabbixInfo(c);	
			if(!flag)
				break;
		}
	     return flag;
	}

	/**
	 * Methods Name: createContainerPushZabbixInfo <br>
	 * Description: 创建container时向zabbix系统推送信息<br>
	 * @author name: wujun
	 * @param zabbixPushModel
	 * @return
	 */
	public String pushZabbixInfo(ZabbixPushModel zabbixPushModel,Long containerId){
		String hostId = "";
		try {
			String result = analysisResultMap(transResult(sendZabbixInfo(zabbixPushModel)));				
			if(result.contains("_succeess")){
				String[] rs = result.split("_");
				hostId = rs[0];
				ContainerModel containerModel = new ContainerModel();
				containerModel.setId(containerId);
				containerModel.setZabbixHosts(hostId);
				containerService.updateBySelective(containerModel);
				logger.debug("推送zabbix系统成功"+result);
			}else {			
				String[] rs = result.split("_");
				result = rs[0];
				logger.debug("推送zabbix系统失败"+result);
			}					
			
		} catch (Exception e) {
			  logger.debug("推送zabbix系统失败"+e.getMessage());
		}
		return hostId;
	}; 
	public boolean usermacroCreate(ZabbixPushModel zabbixPushModel){
		String result = analysisResultMap(transResult(sendZabbixInfo(zabbixPushModel)));
		if(StringUtils.isNullOrEmpty(result) || !result.contains("_succeess")) {
			return false;
		}
		return true;
	}; 
	
	/**
	 * Methods Name: createContainerPushZabbixInfo <br>
	 * Description: 创建container时向zabbix系统推送信息<br>
	 * @author name: wujun
	 * @param zabbixPushDeleteModel
	 * @return
	 */
	public Boolean pushDeleteZabbixInfo(ZabbixPushDeleteModel zabbixPushDeleteModel){
		Boolean flag = false;
	    String result=loginZabbix();
	    if(result!=null){
			if(result.contains("_succeess")){
				String[] auths = result.split("_");
				String auth = auths[0];
				logger.debug("登陆zabbix系统成功");
				try {
					zabbixPushDeleteModel.setAuth(auth);
					result = analysisResultMap(transResult(sendZabbixInfo(zabbixPushDeleteModel)));				
					if(result.contains("_succeess")){
						String[] rs = result.split("_");
						result = rs[0];
						flag = true;
						logger.debug("推送zabbix系统成功"+result);
					}else {			
						String[] rs = result.split("_");
						result = rs[0];
						logger.debug("推送zabbix系统失败"+result);
					}					
					
				} catch (Exception e) {
				  logger.debug("推送zabbix系统失败"+e.getMessage());
				}
			}else {
				logger.debug("登陆zabbix系统失败");
			}	    	
	    }

		//loginZabbix(name,password);//login
		//sendZabbixInfo(object)//发送消息
		return flag;
	}; 
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
		logger.debug("登陆zabbix系统失败"+e.getMessage());
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
				if(resulteMap.get("hostids")!=null){
					String[] arg =	resulteMap.get("hostids").toString().split("\"");
					result = arg[1];
					result+="_succeess";
				}		
			}
		}
		return result;
	}

	
}

