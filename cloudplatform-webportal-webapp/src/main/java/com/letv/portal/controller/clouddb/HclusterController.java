package com.letv.portal.controller.clouddb;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.portal.enumeration.HclusterStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.service.IHclusterService;

@Controller
@RequestMapping("/hcluster")
public class HclusterController {
	@Resource
	private IHclusterService hclusterService;

	private final static Logger logger = LoggerFactory
			.getLogger(HclusterController.class);   
	/**
	 * Methods Name: selectHclusterByStatus <br>
	 * Description: 查询出所有状态为1即运行的hcluster<br>
	 * @author name: wujun
	 * @param hclusterModel
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
    public  @ResponseBody ResultObject selectHclusterByStatus(HclusterModel hclusterModel){
    	ResultObject obj = new ResultObject();
    	hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
    	obj.setData(this.hclusterService.selectHclusterByStatus(hclusterModel));
    	return obj;
    }
	@RequestMapping(value="/{type}",method = RequestMethod.GET)
	public  @ResponseBody ResultObject selectHclusterByStatusAndType(@PathVariable String type,HclusterModel hclusterModel){
		ResultObject obj = new ResultObject();
		hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
		hclusterModel.setType(type);
		obj.setData(this.hclusterService.selectHclusterByStatus(hclusterModel));
		return obj;
	}
	@RequestMapping(value="/{areaId}/{type}",method = RequestMethod.GET)
	public  @ResponseBody ResultObject selectHclusterByStatusAndType(@PathVariable String type,@PathVariable Long areaId,HclusterModel hclusterModel){
		ResultObject obj = new ResultObject();
		hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
		hclusterModel.setType(type);
		hclusterModel.setAreaId(areaId);
		obj.setData(this.hclusterService.selectHclusterByStatus(hclusterModel));
		return obj;
	}
	/**
	 * Methods Name: forbidHcluster <br>
	 * Description: 禁用hcluster集群<br>
	 * @author name: wujun
	 */
	@RequestMapping(value = "/forbid", method = RequestMethod.POST)
	public @ResponseBody ResultObject  forbidHcluster(HclusterModel hclusterModel){
		ResultObject obj = new ResultObject();
		hclusterModel.setStatus(HclusterStatus.FORBID.getValue());
		this.hclusterService.update(hclusterModel);
		return obj;
	}
}
