package com.letv.portal.service.elasticcalc.gce;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.controller.elasticcalc.gce.ECGceClusterController;
import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.elasticcalc.gce.EcGceCluster;

public class EcGceClusterTest extends AbstractTest {
	@Autowired
	private IEcGceClusterService ecGceClusterService;
    
    @Test
    public void testGceClusterList() {
    	Map<Object, Object> params = new HashMap<Object, Object>();
    	params.put("userName", "linzhanbo");
		Page page = ecGceClusterService.selectPageByParams(new Page(), params );
    	System.out.println(page.getCurrentPage());
    }
}
