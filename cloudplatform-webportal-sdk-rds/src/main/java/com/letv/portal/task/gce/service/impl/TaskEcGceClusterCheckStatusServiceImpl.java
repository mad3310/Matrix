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
import com.letv.portal.model.elasticcalc.gce.EcGcePackageCluster;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageContainer;
import com.letv.portal.model.elasticcalc.gce.EcGcePackageImage;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.elasticcalc.gce.IGcePackageContainerService;
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
	private IGcePackageContainerService gcePackageContainerService;
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
		EcGcePackageImage image = super.getGcePackageImage(params);
		// 返回结果
		ApiResultObject resultObject = null;
		
		EcGcePackageCluster gceCluster = super.getGcePackageCluster(params);
		HostModel host = super.getHost(gceCluster.getHclusterId());

		long beginTime = System.currentTimeMillis();
		tr.setSuccess(false);
		while (!tr.isSuccess()) {
			// 循环的第一次肯定不会进该if，因为代码开头Assert.isTrue过，所以resultObject.url必然在轮训后有值
			if (System.currentTimeMillis() - beginTime > checkTimeout) {
				tr.setSuccess(false);
				tr.setResult("check time over:" + resultObject.getUrl());
				break;
			} else {
				logger.debug(System.currentTimeMillis()+" 检查集群[" + serverName + "]创建状态");
				//TODO	与致新联调，测试环境地址
				resultObject = gcePythonService
						.checkContainerCreateStatus(gceCluster.getClusterName(),
								"10.154.156.129",host.getName(),host.getPassword());
				/*resultObject = gcePythonService
						.checkContainerCreateStatus(gceCluster.getClusterName(),
								host.getHostIp(),host.getName(),host.getPassword());*/
				tr = analyzeComplexRestServiceResult(resultObject);
			}
			Thread.sleep(checkInterval);
		}
		if (tr.isSuccess()) {
			logger.debug("创建集群成功");
			/*{
			  "meta": {
			    "code": 200
			  },
			  "response": {
			    "code": "000000",
			    "containers": [
			      {
			        "containerName": "d-jty-30_2_web6_lynzabo_test-n-1",
			        "zookeeperId": null,
			        "ipAddr": "10.154.255.183",
			        "hostIp": "10.154.156.131",
			        "mountDir": "{u'/var/log': u'/srv/docker/vfs/dir/0048092efb0f2bf3f32d5a040c1a509c68e1987c851a5c5e8fc5ba1fe424323d'}",
			        "containerClusterName": "30_2_web6_lynzabo_test",
			        "netMask": "255.255.0.0",
			        "memory": 1073741824,
			        "gateAddr": "10.154.0.1",
			        "type": "jetty"
			      },
			      {
			        "containerName": "d-jty-30_2_web6_lynzabo_test-n-2",
			        "zookeeperId": null,
			        "ipAddr": "10.154.255.180",
			        "hostIp": "10.154.156.36",
			        "mountDir": "{u'/var/log': u'/srv/docker/vfs/dir/e65e355e9ee302aa8fe38e78f0d196510a10d21a5a0a535cfb839548ec03197f'}",
			        "containerClusterName": "30_2_web6_lynzabo_test",
			        "netMask": "255.255.0.0",
			        "memory": 1073741824,
			        "gateAddr": "10.154.0.1",
			        "type": "jetty"
			      }
			    ]
			  }
			}*/
			List<Map> containers = (List<Map>)((Map)transToMap(resultObject.getResult()).get("response")).get("containers");
			for (Map map : containers) {
				EcGcePackageContainer container = new EcGcePackageContainer();
				BeanUtils.populate(container, map);
				container.setGceId(gceCluster.getGceId());
				container.setGcePackageId(gceCluster.getGcePackageId());
				container.setGcePackageClusterId(gceCluster.getId());
				container.setIpMask(image.getNetType());
				container.setStatus(MclusterStatus.RUNNING.getValue());
				container.setCreateUser(image.getCreateUser());
				//物理机集群维护完成后，修改此处，需要关联物理机id
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				this.gcePackageContainerService.insert(container);
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
	public void callBack(TaskResult tr) {
		Map<String,Object> params = (Map<String, Object>) tr.getParams();
		String type = (String) params.get("type");
        if(!StringUtils.isEmpty(type)) {
            if("tomcat".equals(type.toLowerCase())){
            	String serverName = (String) params.get("serviceName");
            	logger.debug("部署GCE{}成功!",serverName);
            	super.rollBack(tr);
            }
        }
	}

}
