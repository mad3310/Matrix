package com.letv.portal.service.cloudes;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.controller.cloudes.EsClusterController;
import com.letv.portal.controller.cloudes.EsContainerController;
import com.letv.portal.controller.cloudes.EsController;
import com.letv.portal.junitBase.AbstractTest;
 
public class EsTest extends AbstractTest{

	@Autowired
	private EsClusterController esClusterController;
	@Autowired
	private EsContainerController esContainerController;
	@Autowired
	private EsController esController;
	
	static MockHttpServletRequest request =  new MockHttpServletRequest();
	
	private final static Logger logger = LoggerFactory.getLogger(EsTest.class);
	
	@BeforeClass
	public static void setParams() {
		request.setParameter("currentPage", "1");
    	request.setParameter("recordsPerPage", "15");
	}
    
    @Test
    public void testEsClusterList() {
    	ResultObject ro = esClusterController.list(new Page(), request, new ResultObject());
    	logger.info("cluster result:{}", JSONObject.toJSONString(ro));
    	Assert.assertEquals(1, ro.getResult());
    }
    
    @Test
    public void testEsContainerList() {
    	ResultObject ro = esContainerController.list(new Page(), request, new ResultObject());
    	logger.info("container result:{}", JSONObject.toJSONString(ro));
    	Assert.assertEquals(1, ro.getResult());
    }
    
    @Test
    public void testEsList() {
    	ResultObject ro = esController.list(new Page(), request, new ResultObject());
    	logger.info("es server result:{}", JSONObject.toJSONString(ro));
    	Assert.assertEquals(1, ro.getResult());
    }
    
}
