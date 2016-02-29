package com.letv.portal.task.slb.service.impl;

import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.task.gce.service.impl.BaseTask4GceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("taskSlbAddFixedPushService")
public class TaskSlbAddFixedPushServiceImpl extends BaseTask4SlbServiceImpl implements IBaseTaskService{

    @Autowired
    private IFixedPushService fixedPushService;

	private final static Logger logger = LoggerFactory.getLogger(TaskSlbAddFixedPushServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;

		//执行业务
		List<SlbContainer> containers = super.getContainers(params);
        boolean flag;
		for (SlbContainer container:containers) {
            if(container.getIpAddr().startsWith("10.")) {
                flag = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
                if(!flag) {
                    //发送推送失败邮件，流程继续。
                    buildResultToMgr("SLB服务相关系统推送异常", super.getServer(params).getSlbName() +"集群固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                    tr.setResult("固资系统数据推送失败");
                    break;
                }
            }
		}
        tr.setSuccess(true);
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void callBack(TaskResult tr) {
	}
	
}
