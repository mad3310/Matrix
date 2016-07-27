package com.letv.portal.fixedPush.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.HttpClient;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbContainerService;



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
	@Value("${fixedpush.checkall.url}")
	private String fixedCheckAllUrl;//灵枢对账接口url
	@Value("${fixedpush.checkall.token}")
	private String fixedCheckAllToken;//灵枢对账接口 token
	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Autowired
	private IMclusterService mclusterService;
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

	public ApiResultObject createMutilContainerPushFixedInfo(List<ContainerModel> containers){
		ApiResultObject ret = new ApiResultObject();
		ret.setAnalyzeResult(false);
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
		ret.setAnalyzeResult(false);
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
	
	
	
	public void fixedCheckAllFromCmdb() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("link_token", fixedCheckAllToken);
		//从cmdb查询所有推送的信息
		String ret = HttpClient.get(fixedCheckAllUrl, params, 10000, 120000);
		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
		if(null == resultMap) {
			throw new ValidateException("cmdb对账接口无返回值");
		}
		Object code = resultMap.get("code");
		if(null!=code && (Integer)code==0) {
			JSONArray array = (JSONArray) resultMap.get("data");
			List<Map> list = JSON.parseArray(array.toJSONString(), Map.class);
			
			Map<String, Integer> map = new HashMap<String, Integer>(list.size());
			StringBuilder builder = new StringBuilder();
			
			for (Map info : list) {
				builder.setLength(0);
				builder.append(info.get("hostName")).append("+").append(info.get("sn"));
				map.put(builder.toString(), 1);
			}
			List<Map<String, Object>> rdsMissDatas = rdsFixedCheck(map);
			List<Map<String, Object>> gceMissDatas = gceFixedCheck(map);
			List<Map<String, Object>> slbMissDatas = slbFixedCheck(map);
			List<Map<String, Object>> cmdbExtraDatas = new ArrayList<Map<String, Object>>();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if(entry.getValue()==1) {
					Map<String, Object> cmdbExtraData = new HashMap<String, Object>();
					String[] hostNameAndSn = entry.getKey().split("\\+");
					cmdbExtraData.put("containerName", hostNameAndSn[0]);
					cmdbExtraData.put("ip", hostNameAndSn[1]);
					cmdbExtraDatas.add(cmdbExtraData);
				}
			}
			
			logger.debug("rdsMissDatas:{}", rdsMissDatas.toString());
			logger.debug("gceMissDatas:{}", gceMissDatas.toString());
			logger.debug("slbMissDatas:{}", slbMissDatas.toString());
			logger.debug("cmdbExtraDatas:{}", cmdbExtraDatas.toString());
			
			//当对账结果不为空时，发送邮件
			if(CollectionUtils.isNotEmpty(cmdbExtraDatas) || CollectionUtils.isNotEmpty(rdsMissDatas) 
					|| CollectionUtils.isNotEmpty(gceMissDatas) || CollectionUtils.isNotEmpty(slbMissDatas)) {
				Map<String, Object> mailParams = new HashMap<String, Object>();
				mailParams.put("cmdbTableInfo", getMailInfo(cmdbExtraDatas));
				mailParams.put("rdsTableInfo", getMailInfo(rdsMissDatas));
				mailParams.put("gceTableInfo", getMailInfo(gceMissDatas));
				mailParams.put("slbTableInfo", getMailInfo(slbMissDatas));
				
				MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",SERVICE_NOTICE_MAIL_ADDRESS,"乐视云平台固资对账通知","fixedCheckReport.ftl",mailParams);
				mailMessage.setHtml(true);
				defaultEmailSender.sendMessage(mailMessage);
			}
		}
	}
	
	private String getMailInfo(List<Map<String, Object>> datas) {
		StringBuffer buffer = new StringBuffer();
		for (Map<String, Object> data : datas) {
			buffer.append("<tr>");
			buffer.append("<th width=\"100px\">");
			buffer.append(data.get("containerName"));
			buffer.append("</th>");
			buffer.append("<th width=\"100px\">");
			buffer.append(data.get("ip"));
			buffer.append("</th>");
			buffer.append("</tr>");
		}
		return buffer.toString();
	}
	
	/**
	 * matrix存储的rds信息与cmdb存储的推送信息比对
	 * @param map cmdb存储的推送信息
	 * @return matrix未推送的rds信息
	 */
	private List<Map<String, Object>> rdsFixedCheck(Map<String, Integer> map) {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> diffs = new ArrayList<Map<String, Object>>();
		List<MclusterModel> mclusters  = this.mclusterService.selectValidMclustersByMap(null);
		StringBuilder builder = new StringBuilder();
		for (MclusterModel mclusterModel : mclusters) {
			params.put("mclusterId", mclusterModel.getId());
			List<ContainerModel> containers = this.containerService.selectByMap(params);
			for (ContainerModel containerModel : containers) {
				builder.setLength(0);
				builder.append(containerModel.getContainerName()).append("+").append(containerModel.getIpAddr());
				if(map.containsKey(builder.toString())) {
					map.put(builder.toString(), 2);
					continue;
				}
				Map<String, Object> diff = new HashMap<String, Object>();
				diff.put("containerName", containerModel.getContainerName());
				diff.put("ip", containerModel.getIpAddr());
				diffs.add(diff);
			}
		}
		return diffs;
	}
	/**
	 * matrix存储的gce信息与cmdb存储的推送信息比对
	 * @param map cmdb存储的推送信息
	 * @return matrix未推送的gce信息
	 */
	private List<Map<String, Object>> gceFixedCheck(Map<String, Integer> map) {
		List<GceCluster> gceClusters = this.gceClusterService.selectValidCluster();
		StringBuilder builder = new StringBuilder();
		List<Map<String, Object>> diffs = new ArrayList<Map<String, Object>>();
		
		for (GceCluster gce : gceClusters) {
			List<GceContainer> containers = this.gceContainerService.selectByGceClusterId(gce.getId());
			for (GceContainer gceContainer : containers) {
				builder.setLength(0);
				builder.append(gceContainer.getContainerName()).append("+").append(gceContainer.getIpAddr());
				if(map.containsKey(builder.toString())) {
					map.put(builder.toString(), 2);
					continue;
				}
				Map<String, Object> diff = new HashMap<String, Object>();
				diff.put("containerName", gceContainer.getContainerName());
				diff.put("ip", gceContainer.getIpAddr());
				diffs.add(diff);
			}
		}
		return diffs;
	}
	/**
	 * matrix存储的slb信息与cmdb存储的推送信息比对
	 * @param map cmdb存储的推送信息
	 * @return matrix未推送的slb信息
	 */
	private List<Map<String, Object>> slbFixedCheck(Map<String, Integer> map) {
		StringBuilder builder = new StringBuilder();
		List<Map<String, Object>> diffs = new ArrayList<Map<String, Object>>();
		
		List<SlbCluster> slbClusters = this.slbClusterService.selectValidCluster();
		for (SlbCluster slb : slbClusters) {
			List<SlbContainer> containers = this.slbContainerService.selectBySlbClusterId(slb.getId());
			for (SlbContainer slbContainer : containers) {
				builder.setLength(0);
				builder.append(slbContainer.getContainerName()).append("+").append(slbContainer.getIpAddr());
				if(map.containsKey(builder.toString())) {
					map.put(builder.toString(), 2);
					continue;
				}
				Map<String, Object> diff = new HashMap<String, Object>();
				diff.put("containerName", slbContainer.getContainerName());
				diff.put("ip", slbContainer.getIpAddr());
				diffs.add(diff);
			}
		}
		return diffs;
	}
	

}
