package com.letv.portal.fixedPush.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.letv.common.exception.ValidateException;
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
	@Value("${fixedpush.add.url}")
	private String fixedPushAddUrl;//灵枢新增固资推送地址
	@Value("${fixedpush.add.token}")
	private String fixedPushAddToken;//灵枢新增固资推送地址token
	@Value("${fixedpush.del.url}")
	private String fixedPushDelUrl;//灵枢删除固资推送地址
	@Value("${fixedpush.del.token}")
	private String fixedPushDelToken;//灵枢删除固资推送地址token

	public Boolean createMutilContainerPushFixedInfo(List<ContainerModel> containers){
        boolean flag = true;
		for(ContainerModel c:containers) {
			flag = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "add");
            if(!flag)
                break;
		}
		return flag;
	}

	@Override
	public Boolean deleteMutilContainerPushFixedInfo(List<ContainerModel> containers){
        boolean flag = true;
		for(ContainerModel c:containers) {
			flag = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "delete");
            if(!flag)
                break;
		}
		return flag;
	}
	public Boolean sendFixedInfo(String hostIp,String name,String ip,String type) {
		String sn = getSnByHostIp(hostIp);
		logger.debug("推送固资根据HostIp:[{}}获取sn:[{}]", hostIp, sn);
		if(StringUtils.isEmpty(sn)) {
			throw new ValidateException("根据hostIp未获取到sn，hostIp:" + hostIp);
		}
		
		StringBuffer buffer = new StringBuffer();
		if("add".equals(type)) {//新增固资
			buffer.append(fixedPushAddUrl).append("?");
			buffer.append("link_token").append("=").append(fixedPushAddToken).append("&");
		} else if("delete".equals(type)) {//删除固资
			buffer.append(fixedPushDelUrl).append("?");
			buffer.append("link_token").append("=").append(fixedPushDelToken).append("&");
		} else {
			logger.debug("由于type未识别，未推送固资");
			return false;
		}
		buffer.append("ip").append("=").append(ip).append("&");
		buffer.append("name").append("=").append(name).append("&");
		buffer.append("hostIp").append("=").append(sn);
		String ret = HttpClient.get(buffer.toString(), 5000, 10000);
		
		logger.info("固资推送结果：{}", ret);
		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
		if(null != resultMap && (int)resultMap.get("Code")==0) {
			logger.debug("固资推送成功hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
			return true;
		} else {
			logger.debug("固资推送失败hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
			return false;
		}
		
	}
	
	/**
	 * 根据宿主机ip获取对应的sn
	 * @param hostIp
	 * @return
	 */
	private String getSnByHostIp(String hostIp) {
		return HttpClient.get(fixedPushSnUrl + hostIp);			
	}


}
