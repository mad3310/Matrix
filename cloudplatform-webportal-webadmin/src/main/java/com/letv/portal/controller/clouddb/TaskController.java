package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.util.HttpUtil;
import com.letv.portal.enumeration.HclusterStatus;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.adminoplog.AoLogType;
import com.letv.portal.model.task.TaskChain;
import com.letv.portal.model.task.TemplateTask;
import com.letv.portal.model.task.TemplateTaskChain;
import com.letv.portal.model.task.TemplateTaskDetail;
import com.letv.portal.model.task.service.ITaskChainIndexService;
import com.letv.portal.model.task.service.ITaskChainService;
import com.letv.portal.model.task.service.ITaskEngine;
import com.letv.portal.model.task.service.ITemplateTaskChainService;
import com.letv.portal.model.task.service.ITemplateTaskDetailService;
import com.letv.portal.model.task.service.ITemplateTaskService;
import com.letv.portal.service.adminoplog.AoLog;
import com.letv.portal.service.adminoplog.ClassAoLog;

/**Program Name: JobController <br>
 * Description:  任务的相关操作<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月9日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@ClassAoLog(module="通用管理/任务管理")
@Controller
@RequestMapping("/task")
public class TaskController {
	@Autowired
	private ITemplateTaskService templateTaskService;
	@Autowired
	private ITemplateTaskDetailService templateTaskDetailService;
	@Autowired
	private ITemplateTaskChainService templateTaskChainService;
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	@Autowired
	private ITaskChainService taskChainService;
	
	@Autowired
	private ITaskEngine taskEngine;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskController.class);

	/**Methods Name: taskList <br>
	 * Description: 查询任务流列表<br>
	 * @author name: yaokuo
	 * @param 
	 * @param request
	 */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject taskList(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.templateTaskService.selectPageByParams(page, params));
		return obj;
	}
	/**Methods Name: taskUnitList <br>
	 * Description: 查询任务单元列表<br>
	 * @author name: yaokuo
	 * @param 
	 * @param request
	 */
	@RequestMapping(value ="/unit",method=RequestMethod.GET)   
	public @ResponseBody ResultObject taskUnitList(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.templateTaskDetailService.selectPageByParams(page, params));
		return obj;
	}
	/**Methods Name: taskMonitorList <br>
	 * Description: 查询任务监控列表<br>
	 * @author name: yaokuo
	 * @param page
	 * @param request
	 */
	@RequestMapping(value ="/monitor",method=RequestMethod.GET)   
	public @ResponseBody ResultObject taskMonitorList(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.taskChainIndexService.queryByPagination(page, params));
		return obj;
	}
	/**Methods Name: taskMonitorDetail <br>
	 * Description: 查询任务监控详情<br>
	 * @author name: yaokuo
	 * @param page
	 * @param request
	 */
	@RequestMapping(value ="/monitor/detail/{chainIndexId}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject taskMonitorDetail(@PathVariable Long chainIndexId,HttpServletRequest request,ResultObject obj) {
		obj.setData(this.taskChainService.selectAllChainByIndexId(chainIndexId));
		return obj;
	}
	@AoLog(desc="重启任务",type=AoLogType.RESTART)
	@RequestMapping(value ="/restart",method=RequestMethod.POST)   
	public @ResponseBody ResultObject restartTask(Long taskChainId,ResultObject obj) {
		if(null == taskChainId)
			throw new ValidateException("参数不合法");
		/*改为异步*/
		TaskChain tc = this.taskChainService.selectById(taskChainId);
    	this.taskEngine.run(tc);
    	
		return obj;
	}
	/**Methods Name: taskDetail <br>
	 * Description: 查询任务流详情<br>
	 * @author name: yaokuo
	 * @param id
	 * @param request
	 */
	@RequestMapping(value ="/detail/{id}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject taskDetail(@PathVariable Long id,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("templateTask",this.templateTaskService.selectById(id));
		result.put("templateTaskDetail",this.templateTaskChainService.selectByTemplateTaskId(id));
		obj.setData(result);
		return obj;
	}
	/**Methods Name: getTaskUnit <br>
	 * Description: 查询任务流详情<br>
	 * @author name: yaokuo
	 * @param id
	 * @param request
	 */
	@RequestMapping(value ="/unit/{type}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject getTaskUnit(@PathVariable String type,HttpServletRequest request,ResultObject obj) {
		obj.setData(this.templateTaskDetailService.selectByTemplateTaskType(type));
		return obj;
	}
	
	/**
     * Methods Name: saveTask <br>
     * Description: 保存templateTask信息<br>
     * @author name: yaokuo 
     * @param TemplateTask
     * @param request
     * @return
     */
	@AoLog(desc="创建templateTask信息",type=AoLogType.INSERT)
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResultObject saveTask(TemplateTask templateTaskModal, HttpServletRequest request) {
		ResultObject obj = new ResultObject();
		this.templateTaskService.insert(templateTaskModal);
		return obj;
	}
	/**
	 * Methods Name: saveTaskUnit <br>
	 * Description: 保存templateTaskUnit信息<br>
	 * @author name: yaokuo 
	 * @param TemplateTask
	 * @param request
	 * @return
	 */
	@AoLog(desc="创建templateTaskUnit信息",type=AoLogType.INSERT)
	@RequestMapping(value="/unit",method = RequestMethod.POST)
	public @ResponseBody ResultObject saveTaskUnit(TemplateTaskDetail templateTaskDetailModal, HttpServletRequest request) {
		ResultObject obj = new ResultObject();
		this.templateTaskDetailService.insert(templateTaskDetailModal);
		return obj;
	}
	/**
	 * Methods Name: addTaskUnit <br>
	 * Description: 保存taskUnit信息<br>
	 * @author name: yaokuo 
	 * @param TemplateTask
	 * @param request
	 * @return
	 */
	@AoLog(desc="创建taskUnit信息",type=AoLogType.INSERT)
	@RequestMapping(value="/stream",method = RequestMethod.POST)
	public @ResponseBody ResultObject addTaskUnit(TemplateTaskChain templateTaskChain, HttpServletRequest request) {
		ResultObject obj = new ResultObject();
		this.templateTaskChainService.insert(templateTaskChain);
		return obj;
	}
	/**
	 * Methods Name: delTaskUnit <br>
	 * Description: 删除taskUnit信息<br>
	 * @author name: yaokuo 
	 * @param TemplateTask
	 * @param request
	 * @return
	 */
	@AoLog(desc="删除taskUnit信息",type=AoLogType.DELETE)
	@RequestMapping(value="/stream/{id}",method = RequestMethod.DELETE)
	public @ResponseBody ResultObject delTaskUnit(@PathVariable Long id, HttpServletRequest request) {
		TemplateTaskChain templateTaskChain = new TemplateTaskChain();
		templateTaskChain.setId(id);
		ResultObject obj = new ResultObject();
		this.templateTaskChainService.delete(templateTaskChain);
		return obj;
	}
	/**
	 * Methods Name: validate <br>
	 * Description: 验证任务流是否存在<br>
	 * @author name: yaokuo 
	 * @param name
	 * @param request
	 * @return
	 */
	@AoLog(desc="验证任务流是否存在",type=AoLogType.VALIDATE,ignore = true)
	@RequestMapping(value="/validate",method=RequestMethod.POST)
	public @ResponseBody Map<String,Object> validate(String name,HttpServletRequest request) {
		if(StringUtils.isEmpty(name))
			throw new ValidateException("参数不合法");
		TemplateTask templateTask = this.templateTaskService.selectByName(name);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("valid", templateTask==null?true:false);
		return map;
	}
}
