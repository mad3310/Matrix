package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.common.util.StringUtil;
import com.letv.portal.enumeration.HclusterStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.adminoplog.AoLogType;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.adminoplog.AoLog;
import com.letv.portal.service.adminoplog.ClassAoLog;

@ClassAoLog(module="通用管理/物理机集群列表")
@Controller
@RequestMapping("/hcluster")
public class HclusterController {
	@Resource
	private IHclusterService hclusterService;

	private final static Logger logger = LoggerFactory
			.getLogger(HclusterController.class);

    /**
     * Methods Name: list <br>
     * Description: 展示hcluster的分页<br>
     * @author name: wujun
     * @param currentPage
     * @param recordsPerPage
     * @param hclusterName
     * @param request
     * @return
     */
	@RequestMapping(value = "/{currentPage}/{recordsPerPage}/{hclusterName}", method = RequestMethod.GET)
	public @ResponseBody ResultObject oldList(@PathVariable int currentPage,
			@PathVariable int recordsPerPage,
			@PathVariable String hclusterName, HttpServletRequest request) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setRecordsPerPage(recordsPerPage);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hclusterName", StringUtil.transSqlCharacter(hclusterName));

		ResultObject obj = new ResultObject();
		obj.setData(this.hclusterService.findPagebyParams(params, page));
		return obj;
	}
	
	/**
     * Methods Name: list <br>
     * Description: 展示hcluster的分页<br>
     * @author name: wujun
     * @param currentPage
     * @param recordsPerPage
     * @param hclusterName
     * @param request
     * @return
     */
	@RequestMapping(value = "/list",method = RequestMethod.GET)
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.hclusterService.selectPageByParams(page, params));
		return obj;
	}

	/**
	 * Methods Name: list <br>
	 * Description: 查找hcluster信息通过hclusterId<br>
	 * @author name: wujun
	 * @param hclusterId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{hclusterId}", method = RequestMethod.GET)
	public @ResponseBody ResultObject list(@PathVariable Long hclusterId) {
		ResultObject obj = new ResultObject();
		obj.setData(this.hclusterService.selectByHclusterId(hclusterId));
		return obj;
	}

	/**
	 * Methods Name: list <br>
	 * Description: 根据查询条件查出数据<br>
	 * @author name: wujun
	 * @param request
	 * @return
	 */
	/*@RequestMapping(value = "/{hclusterName}", method = RequestMethod.GET)
	public @ResponseBody ResultObject list(@PathVariable String hclusterName,
			HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("hclusterName", hclusterName);
		ResultObject obj = new ResultObject();
		obj.setData(this.hclusterService.selectByName(map));
		return obj;
	}*/

    /**
     * Methods Name: saveHcluster <br>
     * Description: 保存hcluster信息<br>
     * @author name: wujun
     * @param hclusterModel
     * @param request
     * @return
     */
	@AoLog(desc="创建hcluster信息",type=AoLogType.INSERT)
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResultObject saveHcluster(HclusterModel hclusterModel,
			HttpServletRequest request) {
		hclusterModel.setStatus(HclusterStatus.RUNNING.getValue());
		ResultObject obj = new ResultObject();
		this.hclusterService.insert(hclusterModel);
		return obj;
	}

	/**
	 * Methods Name: deleteHostByID <br>
	 * Description: 删除hcluster信息通过hclusterId
	 * @author name: wujun
	 * @param hclusterId
	 * @param request
	 */
	@AoLog(desc="删除hcluster信息通过hclusterId",type=AoLogType.DELETE)
	@RequestMapping(value = "/{hclusterId}", method = RequestMethod.DELETE)
	public @ResponseBody ResultObject deleteHclusterByID(
			@PathVariable Long hclusterId, HttpServletRequest request) {
		ResultObject obj = new ResultObject();
		HclusterModel hclusterModel = new HclusterModel();
		hclusterModel.setId(hclusterId);
		this.hclusterService.delete(hclusterModel);
		return obj;
	}

	/**
	 * Methods Name: HclusterId <br>
	 * Description: 修改hcluster的相关信息
	 * @author name: wujun
	 * @param hclusterId
	 * @param request
	 */
	@AoLog(desc="修改hcluster的相关信息",type=AoLogType.UPDATE)
	@RequestMapping(value = "/{hclusterId}", method = RequestMethod.POST)
	public void updateHclusterId(HclusterModel hclusterModel) {

	}

	/**
	 * Methods Name: validate <br>
	 * Description: 校验hcluster是否存在通过名字(模糊查询)
	 * @author name: wujun
	 * @param hclusterName
	 * @param request
	 * @return
	 */
	@AoLog(desc="校验hcluster是否存在通过名字(模糊查询)",type=AoLogType.VALIDATE,ignore = true)
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> validateIp(String hclusterName,
			HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hclusterName", hclusterName);
		List<HclusterModel> list = this.hclusterService.selectByMap(map);
		map.put("valid", list.size() > 0 ? false : true);
		return map;
	}
    /**
     * Methods Name: isExistHostOnHcluster <br>
     * Description: 删除hcluster之前校验是否存在host<br>
     * @author name: wujun
     * @param hclusterId
     * @param request
     * @return
     */
	@AoLog(desc="删除hcluster之前校验是否存在host",type=AoLogType.VALIDATE,ignore = true)
	@RequestMapping(value = "/isExistHostOnHcluster/validate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> isExistHostOnHcluster(
			String hclusterId, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", hclusterId);
		List<HclusterModel> list = this.hclusterService
				.isExistHostOnHcluster(map);
		map.put("valid", list.size() > 0 ? false : true);
		return map;
	}
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
    	obj.setData(this.hclusterService.selectHclusterByStatus(hclusterModel));
    	return obj;
    }
	@RequestMapping(value="/byType/{type}",method = RequestMethod.GET)
	public  @ResponseBody ResultObject selectHclusterByStatusAndType(@PathVariable String type,HclusterModel hclusterModel){
		ResultObject obj = new ResultObject();
		hclusterModel.setType(type);
		obj.setData(this.hclusterService.selectHclusterByStatus(hclusterModel));
		return obj;
	}
	/**
	 * Methods Name: forbidHcluster <br>
	 * Description: 禁用hcluster集群<br>
	 * @author name: wujun
	 */
	@AoLog(desc="禁用hcluster集群",type=AoLogType.UPDATE)
	@RequestMapping(value = "/forbid", method = RequestMethod.POST)
	public @ResponseBody ResultObject  forbidHcluster(HclusterModel hclusterModel){
		ResultObject obj = new ResultObject();
		hclusterModel.setStatus(HclusterStatus.FORBID.getValue());
		this.hclusterService.update(hclusterModel);
		return obj;
	}
	
	@AoLog(desc="停用hcluster集群", type=AoLogType.UPDATE)
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public @ResponseBody ResultObject  stopHcluster(HclusterModel hclusterModel){
		ResultObject obj = new ResultObject();
		hclusterModel.setStatus(HclusterStatus.DEFAULT.getValue());
		this.hclusterService.update(hclusterModel);
		return obj;
	}
	
}
