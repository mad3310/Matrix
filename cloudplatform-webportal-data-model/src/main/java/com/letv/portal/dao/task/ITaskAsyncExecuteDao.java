package com.letv.portal.dao.task;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.task.TaskAsyncExecute;

public interface ITaskAsyncExecuteDao extends IBaseDao<TaskAsyncExecute> {
	TaskAsyncExecute selectByTaskChainId(Long taskChainId);
	
	void deleteByMclusterName(String mclusterName);
}
