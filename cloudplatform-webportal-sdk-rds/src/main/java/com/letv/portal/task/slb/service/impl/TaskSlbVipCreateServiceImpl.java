package com.letv.portal.task.slb.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.letv.portal.fixedPush.IFixedPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.ISlbPythonService;
import com.letv.portal.service.slb.ISlbServerService;

@Service("taskSlbVipCreateService")
public class TaskSlbVipCreateServiceImpl extends BaseTask4SlbServiceImpl implements IBaseTaskService{

	@Autowired
	private ISlbPythonService slbPythonService;
	@Autowired
	private ISlbServerService slbServerService;
	@Autowired
	private IFixedPushService fixedPushService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskSlbVipCreateServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		//执行业务
		SlbCluster cluster = super.getCluster(params);
		SlbServer server = super.getServer(params);
		HostModel host = super.getHost(cluster.getHclusterId());
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("num", "1");
		
		ApiResultObject result = this.slbPythonService.getVipIp(map,host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(result);
		
		if(tr.isSuccess()) {
			Map data = (Map) ((Map)transToMap(result.getResult()).get("response"));
			StringBuffer ipBuffer = new StringBuffer();
			List<String> ips = (List<String>) data.get("ip");
			for (String ip : ips) {
				ipBuffer.append(ip).append(",");
				if(ip.startsWith("10.")) {
					ApiResultObject apiResult = this.fixedPushService.sendFixedInfo(host.getHostIp(),server.getSlbName()+"_vip",ip,"add");
					if(!apiResult.getAnalyzeResult()) {
						//发送推送失败邮件，流程继续。
						buildResultToMgr("SLB服务相关系统推送异常", server.getSlbName()+"vip ip固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
						tr.setResult(apiResult.getResult());
						break;
					}
				}
			}
			server.setIp(ipBuffer.length()>0?ipBuffer.substring(0, ipBuffer.length()-1):ipBuffer.toString());
			this.slbServerService.updateBySelective(server);
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void callBack(TaskResult tr) {
		super.rollBack(tr);
	}
	
}
