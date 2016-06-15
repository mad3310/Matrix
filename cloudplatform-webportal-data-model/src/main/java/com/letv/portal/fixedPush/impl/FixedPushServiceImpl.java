package com.letv.portal.fixedPush.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.HttpClient;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.ContainerModel;



/**
 * Program Name: FixedPush <br>
 * Description:  与固资系统交互实现接口
 * @author name: wujun <br>
 * Written Date: 2014年10月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Service("fixedPushService")
public class FixedPushServiceImpl implements IFixedPushService{
	
	
	private final static Logger logger = LoggerFactory.getLogger(FixedPushServiceImpl.class);
	
	@Value("${fixedpush.sn.url}")
	private String fixedPushSnUrl;//查询sn地址
	@Value("${fixedpush.sn.token}")
	private String fixedPushSnToken;//查询sn token
	@Value("${fixedpush.add.url}")
	private String fixedPushAddUrl;//灵枢新增固资推送地址
	@Value("${fixedpush.add.token}")
	private String fixedPushAddToken;//灵枢新增固资推送地址token
	@Value("${fixedpush.del.url}")
	private String fixedPushDelUrl;//灵枢删除固资推送地址
	@Value("${fixedpush.del.token}")
	private String fixedPushDelToken;//灵枢删除固资推送地址token
	@Value("${fixedpush.manageip.url}")
	private String fixedPushManageIpUrl;//灵枢管理ip
	@Value("${fixedpush.manageip.token}")
	private String fixedPushManageIpToken;//灵枢管理ip token

	public ApiResultObject createMutilContainerPushFixedInfo(List<ContainerModel> containers){
		ApiResultObject ret = new ApiResultObject();
		List<ContainerModel> success = new ArrayList<ContainerModel>();
		for(ContainerModel c:containers) {
			ret = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "add");
			if(!ret.getAnalyzeResult()) {//添加失败
				for (ContainerModel containerModel : success) {//把添加成功的删除
            		sendFixedInfo(containerModel.getHostIp(), containerModel.getContainerName(), containerModel.getIpAddr(), "delete");
				}
				break;
			} else {
            	success.add(c);
            }
		}
		return ret;
	}
	

	@Override
	public ApiResultObject deleteMutilContainerPushFixedInfo(List<ContainerModel> containers){
		ApiResultObject ret = new ApiResultObject();
		List<ContainerModel> success = new ArrayList<ContainerModel>();
		for(ContainerModel c:containers) {
			ret = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "delete");
            if(!ret.getAnalyzeResult()) {//删除失败
            	for (ContainerModel containerModel : success) {//把删除成功的再添加上去
            		sendFixedInfo(containerModel.getHostIp(), containerModel.getContainerName(), containerModel.getIpAddr(), "add");
				}
            	break;
            } else {
            	success.add(c);
            }
		}
		return ret;
	}
	
	
	public ApiResultObject sendFixedInfo(String hostIp,String name,String ip,String type) {
		ApiResultObject apiResult = new ApiResultObject();
		String sn = getSnByHostIp(hostIp);
		logger.debug("推送固资根据HostIp:[{}}获取sn:[{}]", hostIp, sn);
		if(StringUtils.isEmpty(sn)) {
			logger.error("根据hostIp未获取到sn，hostIp:{}", hostIp);
			apiResult.setAnalyzeResult(false);
			apiResult.setResult("根据hostIp未获取到sn，hostIp:"+ hostIp);
			return apiResult;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("ip", ip);
		params.put("name", name);
		params.put("hostIp", sn);
		String ret;
		if("add".equals(type)) {//新增固资
			params.put("link_token", fixedPushAddToken);
			ret = HttpClient.get(fixedPushAddUrl, params, 5000, 10000);
		} else if("delete".equals(type)) {//删除固资
			params.put("link_token", fixedPushDelToken);
			ret = HttpClient.get(fixedPushDelUrl, params, 5000, 10000);
		} else {
			logger.error("由于type未识别，未推送固资");
			throw new ValidateException("由于type未识别，未推送固资， type:" + type);
		}
		
		logger.debug("固资推送结果：{}", ret);
		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
		if(null == resultMap) {
			apiResult.setAnalyzeResult(false);
			apiResult.setResult("调用固资系统失败");
			return apiResult;
		}
		
		Object code = resultMap.get("Code");
		if(null!=code && (Integer)code==0) {
			logger.debug("固资推送成功hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
			apiResult.setAnalyzeResult(true);
			apiResult.setResult((String) resultMap.get("Msg"));
		} else {
			logger.error("固资推送失败结果：{}", ret);
			logger.error("固资推送失败hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
			apiResult.setAnalyzeResult(false);
			StringBuilder builder = new StringBuilder();
			builder.append("固资推送失败,")
			.append("ip[").append(ip).append("]")
			.append(",type[").append(type).append("]")
			.append(",失败原因:").append((String) resultMap.get("Msg"));
			apiResult.setResult(builder.toString());
		}
		return apiResult;
	}
	
	/**
	 * 根据宿主机ip获取对应的sn
	 * @param hostIp
	 * @return
	 */
	private String getSnByHostIp(String hostIp) {
		StringBuilder builder = new StringBuilder();
		builder.append(fixedPushSnUrl).append("?ip=").append(hostIp).append("&link_token=").append(fixedPushSnToken);
		String ret = HttpClient.get(builder.toString(), 5000, 10000);
		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
		Object code = resultMap.get("Code");
		if(null!=code && (Integer)code==0) {
			return (String) resultMap.get("Msg");
		}
		return null;			
	}


	@Override
	public ApiResultObject manageContainerIps(String ips, String type) {
		ApiResultObject apiResult = new ApiResultObject();
		Map<String, String> params = new HashMap<String, String>();
		params.put("ip", ips);
		params.put("type", type);
		params.put("link_token", fixedPushManageIpToken);
		String ret = HttpClient.get(fixedPushManageIpUrl, params, 5000, 10000);
		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
		Object code = resultMap.get("Code");
		if(null!=code && (Integer)code==0) {
			apiResult.setAnalyzeResult(true);
			apiResult.setResult((String) resultMap.get("Msg"));
		} else {
			apiResult.setAnalyzeResult(false);
			apiResult.setResult(ret);
		}
		return apiResult;
	}
	

}
