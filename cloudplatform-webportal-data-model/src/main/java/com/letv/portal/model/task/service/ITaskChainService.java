package com.letv.portal.model.task.service;

import java.util.List;
import java.util.Map;

import com.letv.portal.model.task.TaskChain;
import com.letv.portal.service.IBaseService;

public interface ITaskChainService extends IBaseService<TaskChain> {

	TaskChain selectNextChainByIndexAndOrder(Long chainIndexId, int executeOrder);

	TaskChain selectFailedChainByIndex(long chainIndexId);

	List<TaskChain> selectAllChainByIndexId(Long chainIndexId);

	void updateAfterDoingChainStatus(Map<String, Object> params);

	public int getStepByGceId(Long gceId);

	public int getStepBySlbId(Long slbId);

	public int getStepByCacheId(Long cacheId);

	public int  getStepByDbId(Long dbId);
	
	public void insertBatch(List<TaskChain> taskChains);

}
