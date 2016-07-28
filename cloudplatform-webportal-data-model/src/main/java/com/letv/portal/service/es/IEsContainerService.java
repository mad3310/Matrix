package com.letv.portal.service.es;

import java.util.List;

import com.letv.portal.model.es.EsContainer;
import com.letv.portal.service.IBaseService;

public interface IEsContainerService extends IBaseService<EsContainer> {
	
	public List<EsContainer> selectContainersByEsClusterId(Long esClusterId);

	public EsContainer selectByName(String containerName);
}
