package com.letv.portal.dao.es;

import java.util.List;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.es.EsContainer;

public interface IEsContainerDao extends IBaseDao<EsContainer> {

	public List<EsContainer> selectContainersByEsClusterId(Long clusterId);

	public EsContainer selectByName(String containerName);
}
