package com.letv.portal.proxy;

import java.util.List;

import com.letv.portal.model.es.EsContainer;
import com.letv.portal.model.es.EsServer;

public interface IEsProxy extends IBaseProxy<EsServer> {
	
	public void insertAndBuild(EsServer esServer);
	/**
	 * 使用ES基础信息获取该ES的容器列表
	 * @param esServer
	 * @return
	 */
	public List<EsContainer> getContainers(EsServer esServer);
	
}
