package com.letv.portal.proxy;

import com.letv.portal.model.es.EsServer;

public interface IEsProxy extends IBaseProxy<EsServer> {
	
	public void insertAndBuild(EsServer esServer);
	
}
