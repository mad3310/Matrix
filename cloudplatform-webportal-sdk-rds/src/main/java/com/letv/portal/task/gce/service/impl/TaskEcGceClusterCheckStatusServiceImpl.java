package com.letv.portal.task.gce.service.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;
import com.letv.portal.model.elasticcalc.gce.EcGceContainer;
import com.letv.portal.model.elasticcalc.gce.EcGceImage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.elasticcalc.gce.IEcGceContainerService;
/**
 * 购买GCE：检查集群创建状态
 * @author linzhanbo .
 * @since 2016年7月1日, 上午10:27:18 .
 * @version 1.0 .
 */
@Service("taskEcGceClusterCheckStatusService")
public class TaskEcGceClusterCheckStatusServiceImpl extends BaseTaskEcGceServiceImpl implements IBaseTaskService{
	private final static Logger logger = LoggerFactory
			.getLogger(TaskEcGceClusterCheckStatusServiceImpl.class);
	
	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IEcGceContainerService ecGceContainerService;
    @Autowired
    private IFixedPushService fixedPushService;
	
	@Value("${matrix.gce.cluster.create.check.interval}")
	private long checkInterval;// 间隔checkInterval s检查一次
	@Value("${matrix.gce.cluster.create.check.timeout}")
	private long checkTimeout;// checkTimeout s超时,超时后停止检查
	/**
	 * 检查集群创建状态
	 */
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		String serverName = (String) params.get("serviceName");
		logger.debug("检查集群[" + serverName + "]创建状态");
		Assert.isTrue(checkTimeout > checkInterval, MessageFormat.format(
				"检查镜像状态时定时检查配置的参数非法: [定时时间{0}ms,间隔时间{1}ms]不符合检查条件",
				checkTimeout, checkInterval));
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;
		EcGceImage image = super.getGceImage(params);
		
		EcGceCluster gceCluster = super.getGceCluster(params);
		HostModel host = super.getHost(gceCluster.getHclusterId());
		tr = super.polling(tr, checkInterval, checkTimeout,serverName,gceCluster,host);
		if (tr.isSuccess()) {
			logger.debug("创建集群成功");
			//只需判断关键属性containers属性
			Map<String,Object> response = (Map<String, Object>) tr.getParams();
			Object containersObj = response.get("containers");
			if(null == containersObj){
				tr.setSuccess(false);
				tr.setResult("get 'containers' data error:"+tr.getResult());
				tr.setParams(params);
				return tr;
			}
			List<Map> containers = (List<Map>)containersObj;
			for (Map map : containers) {
				EcGceContainer container = new EcGceContainer();
				BeanUtils.populate(container, map);
				container.setGceId(gceCluster.getGceId());
				container.setGcePackageId(gceCluster.getGcePackageId());
				container.setGceClusterId(gceCluster.getId());
				container.setIpMask(image.getNetType());
				container.setStatus(MclusterStatus.RUNNING.getValue());
				container.setCreateUser(image.getCreateUser());
				//物理机集群维护完成后，修改此处，需要关联物理机id
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				this.ecGceContainerService.insert(container);
				ApiResultObject apiResult = this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"add");
                if(!apiResult.getAnalyzeResult()) {
                    //发送推送失败邮件，流程继续。
                    buildResultToMgr("GCE服务相关系统推送异常", container.getContainerName() +"节点固资系统数据推送失败，请运维人员重新推送", tr.getResult(), null);
                    tr.setResult(apiResult.getResult());
                    break;
                }
			}
			
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public void afterExecute(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		String type = (String) params.get("type");
        if(!StringUtils.isEmpty(type)) {
            if("tomcat".equals(type.toLowerCase())){
            	String serverName = (String) params.get("serviceName");
            	logger.debug("部署GCE{}成功!",serverName);
            	super.finish(tr);
            }
        }
	}
	
	@Override
	public ApiResultObject pollingTask(Object... params) {
		//从调用polling时候的赋值中获取
		String serverName = (String) params[0];
		EcGceCluster gceCluster = (EcGceCluster) params[1];
		HostModel host = (HostModel) params[2];
		logger.debug(System.currentTimeMillis()+" 检查集群[" + serverName + "]创建状态");
		//TODO	与致新联调，测试环境地址
		ApiResultObject resultObject = gcePythonService
				.checkContainerCreateStatus(gceCluster.getClusterName(),
						"10.154.156.129",host.getName(),host.getPassword());
		/*ApiResultObject resultObject = gcePythonService
				.checkContainerCreateStatus(gceCluster.getClusterName(),
						host.getHostIp(),host.getName(),host.getPassword());*/
		return resultObject;
	}

}
