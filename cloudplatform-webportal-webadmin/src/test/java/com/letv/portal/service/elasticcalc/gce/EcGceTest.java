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
import com.letv.portal.model.elasticcalc.gce.EcGcePackage;

public class EcGceTest extends AbstractTest {
	@Autowired
	private IEcGcePackageService ecGcePackageService;
	@Autowired
	private IEcGceImageService ecGceImageService;
    
    @Test
    public void testGceClusterList() {
    	Map<Object, Object> params = new HashMap<Object, Object>();
    	//params.put("gceId", "11");
		Page page = ecGcePackageService.selectPageByParams(new Page(), params);
    	System.out.println(page.toString());
    }
}
