package com.letv.portal.model.task.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.ValidateException;
import com.letv.portal.dao.task.ITaskChainDao;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.cbase.CbaseBucketModel;
import com.letv.portal.model.cbase.CbaseClusterModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.model.task.TaskChain;
import com.letv.portal.model.task.TaskChainIndex;
import com.letv.portal.model.task.TaskExecuteStatus;
import com.letv.portal.model.task.service.ITaskChainIndexService;
import com.letv.portal.model.task.service.ITaskChainService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.cbase.ICbaseBucketService;
import com.letv.portal.service.cbase.ICbaseClusterService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbServerService;

@Service("taskChainService")
public class TaskChainServiceImpl extends BaseServiceImpl<TaskChain> implements
		ITaskChainService {

	private final static Logger logger = LoggerFactory
			.getLogger(TaskChainServiceImpl.class);

	@Resource
	private ITaskChainDao taskChainDao;
	@Autowired
	private IGceServerService gceServerService;
	@Autowired
	private ISlbServerService slbServerService;
	@Autowired
	private ICbaseBucketService cbaseBucketService;
	@Autowired
	private IGceClusterService gceClusterService;
	@Autowired
	private ISlbClusterService slbClusterService;
	@Autowired
	private ICbaseClusterService cbaseClusterService;
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IMclusterService mclusterService;
	
	public TaskChainServiceImpl() {
		super(TaskChain.class);
	}

	@Override
	public IBaseDao<TaskChain> getDao() {
		return taskChainDao;
	}

	@Override
	public TaskChain selectNextChainByIndexAndOrder(Long chainIndexId,
			int executeOrder) {
		TaskChain tc = new TaskChain();
		tc.setChainIndexId(chainIndexId);
		tc.setExecuteOrder(executeOrder);
		return this.taskChainDao.selectNextChainByIndexAndOrder(tc);
	}

	@Override
	public TaskChain selectFailedChainByIndex(long chainIndexId) {
		TaskChain tc = new TaskChain();
		tc.setChainIndexId(chainIndexId);
		tc.setStatus(TaskExecuteStatus.FAILED);
		return this.taskChainDao.selectFailedChainByIndex(tc);
	}

	@Override
	public List<TaskChain> selectAllChainByIndexId(Long chainIndexId) {
		return this.taskChainDao.selectAllChainByIndexId(chainIndexId);
	}

	@Override
	public void updateAfterDoingChainStatus(Map<String, Object> params) {
		this.taskChainDao.updateAfterDoingChainStatus(params);

	}

	private int getStepByTaskChainIndexId(Long taskChainIndexId) {
		List<TaskChain> taskChains = this
				.selectAllChainByIndexId(taskChainIndexId);

		if (taskChains.get(taskChains.size() - 1).getStatus() == TaskExecuteStatus.SUCCESS) {
			return 0;// 返回创建成功
		}
		for (TaskChain taskChain : taskChains) {
			if (taskChain.getStatus() == TaskExecuteStatus.FAILED) {
				return -1;// 返回创建失败
			} else if (taskChain.getStatus() == TaskExecuteStatus.DOING) {
				return taskChain.getExecuteOrder();// 返回此步所在任务中的顺序
			}
		}
		return 1;// 都没有，则认为正在执行第一步
	}
	
	@Override
	public int getStepByGceId(Long gceId) {
		if (gceId == null)
			throw new ValidateException("参数不合法");
		GceServer gce = this.gceServerService.selectById(gceId);
		GceCluster gceCluster = this.gceClusterService.selectById(gce
				.getGceClusterId());
		if (gce == null)
			throw new ValidateException("参数不合法");
		String serviceName = gce.getGceName();
		String clusterName = gceCluster.getClusterName();
		TaskChainIndex taskChainIndex = this.taskChainIndexService
				.selectByServiceAndClusterName(serviceName, clusterName);
		return this.getStepByTaskChainIndexId(taskChainIndex.getId());
	}

	@Override
	public int getStepBySlbId(Long slbId) {
		if (slbId == null)
			throw new ValidateException("参数不合法");
		SlbServer slb = this.slbServerService.selectById(slbId);
		SlbCluster slbCluster = this.slbClusterService.selectById(slb
				.getSlbClusterId());
		if (slb == null)
			throw new ValidateException("参数不合法");
		String serviceName = slb.getSlbName();
		String clusterName = slbCluster.getClusterName();
		TaskChainIndex taskChainIndex = this.taskChainIndexService
				.selectByServiceAndClusterName(serviceName, clusterName);
		return this.getStepByTaskChainIndexId(taskChainIndex.getId());
	}
	@Override
	public int getStepByCacheId(Long cacheId) {
		if (cacheId == null)
			throw new ValidateException("参数不合法");
		CbaseBucketModel bucket = this.cbaseBucketService.selectById(cacheId);
		if (bucket == null)
			throw new ValidateException("参数不合法");
		CbaseClusterModel cbaseCluster = this.cbaseClusterService
				.selectById(bucket.getCbaseClusterId());
		String serviceName = bucket.getBucketName();
		String clusterName = cbaseCluster.getCbaseClusterName();
		TaskChainIndex taskChainIndex = this.taskChainIndexService
				.selectByServiceAndClusterName(serviceName, clusterName);
		return this.getStepByTaskChainIndexId(taskChainIndex.getId());
	}

	@Override
	public int getStepByDbId(Long dbId) {
		if (dbId == null)
			throw new ValidateException("参数不合法");
		DbModel db = this.dbService.selectById(dbId);
		if (db == null)
			throw new ValidateException("参数不合法");
		MclusterModel mcluster = this.mclusterService.selectById(db.getMclusterId());
		if (mcluster == null)
			throw new ValidateException("参数不合法");
		String serviceName = db.getDbName();
		String clusterName = mcluster.getMclusterName();
		TaskChainIndex taskChainIndex = this.taskChainIndexService
				.selectByServiceAndClusterName(serviceName, clusterName);
		if(taskChainIndex == null)
			return 0;
		return this.getStepByTaskChainIndexId(taskChainIndex.getId());
	}

	@Override
	public void insertBatch(List<TaskChain> taskChains) {
		taskChainDao.insertBatch(taskChains);
	}
}
