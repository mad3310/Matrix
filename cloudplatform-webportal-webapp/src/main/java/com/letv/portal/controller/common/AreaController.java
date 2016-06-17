package com.letv.portal.controller.common;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.service.common.IAreaService;

@Controller
@RequestMapping("/area")
public class AreaController {
	@Resource
	private IAreaService areaService;

	private final static Logger logger = LoggerFactory.getLogger(AreaController.class);   
	
	@RequestMapping(method = RequestMethod.GET)
    public  @ResponseBody ResultObject selectHclusterByStatus(HclusterModel hclusterModel){
    	ResultObject obj = new ResultObject();
    	obj.setData(this.areaService.selectAllNonParentArea());
    	return obj;
    }
}
