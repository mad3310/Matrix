package com.letv.portal.proxy.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.proxy.IHclusterProxy;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IHclusterService;

@Component
public class HclusterProxyImpl extends BaseProxyImpl<HclusterModel> implements
		IHclusterProxy{
	
	private final static Logger logger = LoggerFactory.getLogger(HclusterProxyImpl.class);

	@Resource
	private IHclusterService hclusterService;
	@Resource
	private IFixedPushService fixedPushService;
	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Override
	public IBaseService<HclusterModel> getService() {
		return hclusterService;
	}
	
	@Override
	public void insertAndRegisteIpsToCmdb(HclusterModel hclusterModel) {
		this.hclusterService.insert(hclusterModel);
		String ips = hclusterModel.getContainerIps();
		ApiResultObject apiResult = this.fixedPushService.manageContainerIps(ips, "add");
		if(apiResult.getAnalyzeResult()) {
			sendResultToMgr("固资管理ip接口添加推送失败，请手动操作", apiResult.getResult(), 
					"需要添加的ip："+ips, null);
		}
	}

	@Override
	public void updateAndRegisteIpsToCmdb(HclusterModel hclusterModel) {
		String newIps = hclusterModel.getContainerIps();
		String oldIps = this.hclusterService.selectById(hclusterModel.getId()).getContainerIps();
		 
		this.hclusterService.update(hclusterModel);
		
		ApiResultObject deleteResult = this.fixedPushService.manageContainerIps(oldIps, "delete");
		ApiResultObject addResult = null;
		if(deleteResult.getAnalyzeResult()) {
			addResult = this.fixedPushService.manageContainerIps(newIps, "add");
		} 
		if(!deleteResult.getAnalyzeResult() || null==addResult || !addResult.getAnalyzeResult()){
			StringBuilder builder = new StringBuilder();
			if(!deleteResult.getAnalyzeResult()) {
				builder.append("需要删除的ip：").append(oldIps);
			}
			
			builder.append(" 需要添加的ip：").append(newIps);
			sendResultToMgr(null, "固资管理ip接口推送失败，请手动操作", builder.toString(), null);
		}
		
	}
	
	private void sendResultToMgr(String buildType,String result,String detail,String to){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buildType", buildType);
		map.put("buildResult", result);
		map.put("errorDetail", detail);
		MailMessage mailMessage = new MailMessage(null, StringUtils.isEmpty(to)?SERVICE_NOTICE_MAIL_ADDRESS:to,"固资推送管理ip异常","buildForMgr.ftl",map);
		defaultEmailSender.sendMessage(mailMessage);
	}

}
