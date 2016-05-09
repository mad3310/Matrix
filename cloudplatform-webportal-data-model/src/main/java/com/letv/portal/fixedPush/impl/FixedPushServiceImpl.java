package com.letv.portal.fixedPush.impl;

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
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("ip", ip);
		params.put("name", name);
		params.put("hostIp", sn);
		String ret;
		if("add".equals(type)) {//新增固资
			params.put("link_token", fixedPushAddToken);
			try {
				ret = HttpClient.get(fixedPushAddUrl, params, 5000, 10000);
			} catch (Exception e) {
				logger.error("invoke cmdb api push fixed failure:", e);
				throw new RuntimeException("推送固资调用cmdb接口失败:", e);
			}
		} else if("delete".equals(type)) {//删除固资
			params.put("link_token", fixedPushDelToken);
			try {
				ret = HttpClient.get(fixedPushDelUrl, params, 5000, 10000);
			} catch (Exception e) {
				logger.error("invoke cmdb api push fixed failure:", e);
				throw new RuntimeException("推送固资调用cmdb接口失败:", e);
			}
		} else {
			logger.error("由于type未识别，未推送固资");
			throw new ValidateException("由于type未识别，未推送固资， type:" + type);
		}
		
		logger.info("固资推送结果：{}", ret);
		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
		Object code = resultMap.get("Code");
		if(null != resultMap && code instanceof Integer && (int)code==0) {
			logger.debug("固资推送成功hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
			return true;
		} else {
			logger.debug("固资推送失败hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
			throw new ValidateException("由于type未识别，未推送固资， type:" + type);
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
