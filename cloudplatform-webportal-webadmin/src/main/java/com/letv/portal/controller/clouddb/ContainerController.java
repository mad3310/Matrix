package com.letv.portal.controller.clouddb;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.letv.common.exception.ValidateException;
import com.letv.portal.model.ContainerModel;
import org.apache.commons.lang.StringUtils;
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
import com.letv.portal.model.adminoplog.AoLogType;
import com.letv.portal.proxy.IContainerProxy;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.adminoplog.AoLog;
import com.letv.portal.service.adminoplog.ClassAoLog;

@ClassAoLog(module="RDS管理/集群管理")
@Controller
@RequestMapping("/container")
public class ContainerController {
	
	@Resource
	private IContainerProxy containerProxy;
	@Resource
	private IContainerService containerService;
	
	private final static Logger logger = LoggerFactory.getLogger(ContainerController.class);
	
	/**Methods Name: list <br>
	 * Description: 根据mclusterId获取相关container列表<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	
	@RequestMapping(value="/{mclusterId}",method=RequestMethod.GET)
	public @ResponseBody ResultObject list(@PathVariable Long mclusterId,ResultObject result) {
		result.setData(this.containerService.selectByMclusterId(mclusterId));
		return result;
	}
	/**Methods Name: list <br>
	 * Description: 查询container列表<br>
	 * @author name: yaokuo
	 * @param request
	 */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.containerService.selectPageByParams(page, params));
		return obj;
	}
	

	/**Methods Name: start <br>
	 * Description: 启动单个container<br>
	 * @author name: liuhao1
	 * @param containerId
	 * @param result
	 * @return
	 */
	@AoLog(desc="启动单个容器",type=AoLogType.START)
	@RequestMapping(value = "/start", method=RequestMethod.POST) 
	public @ResponseBody ResultObject start(Long containerId,ResultObject result) {
		this.containerProxy.start(containerId);
		return result;
	}
	
	/**Methods Name: stop <br>
	 * Description: 关闭单个container<br>
	 * @author name: liuhao1
	 * @param containerId
	 * @param result
	 * @return
	 */
	@AoLog(desc="停止单个容器",type=AoLogType.STOP)
	@RequestMapping(value = "/stop", method=RequestMethod.POST) 
	public @ResponseBody ResultObject stop(Long containerId,ResultObject result) {
		this.containerProxy.stop(containerId);
		return result;
	}

	@AoLog(desc="删除单个容器",type=AoLogType.DELETE)
	@RequestMapping(value="/{containerId}",method=RequestMethod.DELETE)
	public @ResponseBody ResultObject delete(@PathVariable Long containerId,ResultObject result) {
        if(null == containerId)
            throw new ValidateException("参数不合法");
        ContainerModel containerModel = this.containerService.selectById(containerId);
        if(null == containerModel)
            throw new ValidateException("参数不合法");
        if("mclusternode".equals(containerModel.getType())) {
        	throw new ValidateException("初始节点不能缩容");
        }
        this.containerProxy.deleteAndBuild(containerModel);
		return result;
	}
}
