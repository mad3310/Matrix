package com.letv.portal.proxy.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.letv.common.exception.TaskExecuteException;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.cbase.CbaseBucketModel;
import com.letv.portal.model.cbase.CbaseClusterModel;
import com.letv.portal.model.task.service.ITaskEngine;
import com.letv.portal.proxy.ICbaseProxy;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.cbase.ICbaseBucketService;
import com.letv.portal.service.cbase.ICbaseClusterService;
import com.letv.portal.util.CommonServiceUtils;

@Component
public class CbaseProxyImpl extends BaseProxyImpl<CbaseBucketModel> implements
		ICbaseProxy {

	private final static Logger logger = LoggerFactory
			.getLogger(CbaseProxyImpl.class);

	@Autowired
	private ICbaseBucketService cbaseBucketService;
    @Autowired
    private ICbaseClusterService cbaseClusterService;
    @Autowired
    private IHostService hostService;
    @Autowired
    private IPythonService pythonService;
	@Autowired
	private ITaskEngine taskEngine;


	@Override
	public void saveAndBuild(CbaseBucketModel cbaseBucketModel) {
		if (cbaseBucketModel == null)
			throw new ValidateException("参数不合法");
		List<CbaseBucketModel> list = this.cbaseBucketService
				.selectByBucketNameForValidate(
						cbaseBucketModel.getBucketName(),
						cbaseBucketModel.getCreateUser());
		if (list.size() > 0) {
			throw new ValidateException("缓存实例已存在!");
		} else {
			Map<String, Object> params = this.cbaseBucketService
					.save(cbaseBucketModel);
			this.build(params);
		}
	}

	private void build(Map<String, Object> params) {
		if ((Integer) params.get("hostCount") == 3) {
			this.taskEngine.run("CBASE_BUY", params);
		} else {
			throw new TaskExecuteException("任务流模板未配置");
		}
	}

	@Override
	public IBaseService<CbaseBucketModel> getService() {
		return cbaseBucketService;
	}

	@Override
	@Async
	public void checkStatus() {
        try {
            List<CbaseClusterModel> list = this.cbaseClusterService.selectByMap(null);
            for (CbaseClusterModel cluster : list) {
                if(MclusterStatus.BUILDDING.getValue() == cluster.getStatus() || MclusterStatus.BUILDFAIL.getValue() == cluster.getStatus())
                    continue;
                this.checkCbaseClusterStatus(cluster);
            }
            list.clear();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ocs checkStatus exception:{}",e.getMessage());
        }
    }

	private void checkCbaseClusterStatus(CbaseClusterModel cluster) {
		HostModel host = this.hostService.getHostByHclusterId(cluster.getHclusterId());
		if(null == host) {
			cluster.setStatus(MclusterStatus.CRISIS.getValue());
			this.cbaseClusterService.updateBySelective(cluster);
			return;
		}
		ApiResultObject result = this.pythonService.checkMclusterStatus(cluster.getCbaseClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		Map map = CommonServiceUtils.transResult(result.getResult());
		if(map.isEmpty()) {
			cluster.setStatus(MclusterStatus.CRISIS.getValue());
			this.cbaseClusterService.updateBySelective(cluster);
			return;
		}

		if(Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(((Map)map.get("meta")).get("code")))) {
			Integer status = CommonServiceUtils.transStatus((String) ((Map) map.get("response")).get("status"));
			cluster.setStatus(status);
			this.cbaseClusterService.updateBySelective(cluster);
			if(status == MclusterStatus.NOTEXIT.getValue() || status == MclusterStatus.DESTROYED.getValue()) {
				this.cbaseClusterService.delete(cluster);
			}
			return;
		}

		if(null !=result.getResult() && result.getResult().contains("not existed")){
			this.cbaseClusterService.delete(cluster);
			return;
		}

		cluster.setStatus(MclusterStatus.CRISIS.getValue());
		this.cbaseClusterService.updateBySelective(cluster);
	}
}
