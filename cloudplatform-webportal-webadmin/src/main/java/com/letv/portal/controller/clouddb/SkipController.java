package com.letv.portal.controller.clouddb;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.result.ResultObject;
import com.letv.portal.service.adminoplog.ClassAoLog;

/**Program Name: SkipController <br>
 * Description:  用于页面跳转       list、detail、form、……<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年10月8日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller("dbSkip")
public class SkipController {
	
	@RequestMapping(value ="/dashboard",method=RequestMethod.GET)
	public ModelAndView toDashboard(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/dashboard");
		return mav;
	}
	@RequestMapping(value ="/list/mcluster",method=RequestMethod.GET)
	public ModelAndView toMclusterList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/mcluster_list");
		return mav;
	}
	@RequestMapping(value ="/list/container",method=RequestMethod.GET)
	public ModelAndView toContainerList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/container_list");
		return mav;
	}
	@RequestMapping(value ="/list/job/stream",method=RequestMethod.GET)
	public ModelAndView toJobStreamList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/job_stream_list");
		return mav;
	}
	@RequestMapping(value ="/list/job/unit",method=RequestMethod.GET)
	public ModelAndView toJobUnitList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/job_unit_list");
		return mav;
	}
	@RequestMapping(value ="/list/job/monitor",method=RequestMethod.GET)
	public ModelAndView toJobMonitorList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/job_monitor_list");
		return mav;
	}
	@RequestMapping(value ="/detail/job/stream/{jobStreamId}",method=RequestMethod.GET)
	public ModelAndView toJobStreamDetail(ModelAndView mav,@PathVariable Long jobStreamId){
		mav.addObject("jobStreamId",jobStreamId);
		mav.setViewName("/clouddb/job_stream_detail");
		return mav;
	}

	@RequestMapping(value="/detail/mcluster/{mclusterId}", method=RequestMethod.GET)   
	public ModelAndView toMclusterDetail(@PathVariable Long mclusterId,ModelAndView mav) {
		mav.addObject("mclusterId",mclusterId);
		mav.setViewName("/clouddb/mcluster_detail");
		return mav;
	}
	@RequestMapping(value ="/moniter/icon",method=RequestMethod.GET)
	public ModelAndView toMoniterIcon(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/moniter_icon");
		return mav;
	}
	/**Methods Name: toList <br>
	 * Description: db页面跳转<br>
	 * @author name: wujun
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/list/db",method=RequestMethod.GET)
	public ModelAndView toDbList(ModelAndView mav){
		mav.setViewName("/clouddb/db_list");
		return mav;
	}
	/**
	 * Methods Name: toDbDetail <br>
	 * Description: toDbDetail跳转
	 * @author name: wujun
	 * @return
	 */
	@RequestMapping(value="/detail/db/{dbId}",method=RequestMethod.GET)
	public ModelAndView toDbDetail(@PathVariable Long dbId,ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/db_detail");
		return mav;
	}
	/**
	 * Methods Name: toDbUserList <br>
	 * Description: toDbUserList跳转
	 * @author name: wujun
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/list/dbUser",method=RequestMethod.GET)
	public ModelAndView toDbUserList(ModelAndView mav){
		mav.setViewName("/clouddb/db_user_list");
		return mav;
	}
	/**
	 * Methods Name: toDbAudit <br>
	 * Description: Db审批跳转
	 * @author name: wujun
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/audit/db/{dbId}",method=RequestMethod.GET)
	public ModelAndView toDbAudit(@PathVariable Long dbId, ModelAndView mav){
		mav.addObject("dbId",dbId);
		mav.setViewName("/clouddb/db_audit");
		return mav;
	}
	/**
	 * Methods Name: toHcluster <br>
	 * Description: Host集群跳转
	 * @author name: wujun
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/list/hcluster",method=RequestMethod.GET)
	public ModelAndView toHcluster(ModelAndView mav){
		mav.setViewName("/clouddb/hcluster_list");
		return mav;
	}
	/**
	 * Methods Name: toHclusterDetail <br>
	 * Description: hcluster详情
	 * @author name: wujun
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/detail/hcluster/{hclusterId}",method=RequestMethod.GET)
	public ModelAndView toHclusterDetail(@PathVariable Long hclusterId,ModelAndView mav){
		mav.addObject("hclusterId",hclusterId);
		mav.setViewName("/clouddb/hcluster_detail");
		return mav;
	}
	/**
	 * Methods Name: toMonitorCotainer <br>
	 * Description: container集群监控
	 * @author name: wujun
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/list/mcluster/monitor/{type}",method=RequestMethod.GET)
	public ModelAndView toMonitorCotainer(@PathVariable int type,ModelAndView mav){
		String viewName= "";
		if(type == 1) {
			viewName = "/clouddb/mcluster_monitor_list";
		} else if(type == 2) {
			viewName = "/clouddb/monitor_node_list";
		} else if(type == 3) {
			viewName = "/clouddb/monitor_db_list";
		}
		mav.setViewName(viewName);
		return mav;
	}
	@RequestMapping(value="/list/mcluster/monitorView",method=RequestMethod.GET)
	public ModelAndView toMonitorMclusterDashboard(ModelAndView mav){
		mav.setViewName("/clouddb/mcluster_warning_dashboard");
		return mav;
	}

	/**
	 * Methods Name: toMonitorCotainerDetail <br>
	 * Description: container集群监控详情
	 * @author name: wujun
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/detail/mcluster/{ip}/monitor",method=RequestMethod.GET)
	public ModelAndView toMonitorCotainerDetail(@PathVariable String ip,ModelAndView mav){
		mav.addObject("ip",ip);
		mav.setViewName("/clouddb/mcluster_monitor_detail");
		return mav;
	}

	/**
	 * Methods Name: toMonitorCotainerDetail <br>
	 * Description: container集群监控视图
	 * @author name: yaokuo
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/view/mcluster/monitor",method=RequestMethod.GET)
	public ModelAndView toMonitorCotainerView(ModelAndView mav){
		mav.setViewName("/clouddb/mcluster_monitor_view");
		return mav;
	}
	@RequestMapping(value="/view/mcluster/monitor/topN",method=RequestMethod.GET)
	public ModelAndView toMonitorCotainerTopNView(ModelAndView mav){
		mav.setViewName("/clouddb/mcluster_monitor_view_topN");
		return mav;
	}
	
	/**
	 * Methods Name: toMonitorMclusterListDetail <br>
	 * Description: mcluster监控详情
	 * @author name: yaokuo
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/detail/mcluster/monitor/list/{ip}/{type}",method=RequestMethod.GET)
	public ModelAndView toMonitorMclusterListDetail(@PathVariable String ip,@PathVariable int type,ModelAndView mav){
		mav.addObject("ip",ip);
		
		String viewName = "";
		if(type == 1) {
			viewName = "/clouddb/mcluster_monitor_list_detail";
		} else if(type == 2) {
			viewName = "/clouddb/monitor_node_list_detail";
		} else if(type == 3) {
			viewName = "/clouddb/monitor_db_list_detail";
		}
		mav.setViewName(viewName);
		return mav;
	}

	@RequestMapping(value="/list/backup",method=RequestMethod.GET)
	public ModelAndView toBackupView(ModelAndView mav){
		mav.setViewName("/clouddb/backup_recover");
		return mav;
	}
	
	@RequestMapping(value="/list/rds/backup",method=RequestMethod.GET)
	public ModelAndView toBackupRdsView(ModelAndView mav){
		mav.setViewName("/clouddb/backup_recover_rds");
		return mav;
	}
	
	@RequestMapping(value ="/list/rds/node/health",method=RequestMethod.GET)
	public ModelAndView toRdsNodeHealthList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/rds_monitor_node_health");
		return mav;
	}
	
	@RequestMapping(value ="/list/rds/node/resource",method=RequestMethod.GET)
	public ModelAndView toRdsNodeResourceList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/rds_monitor_node_resource");
		return mav;
	}
	
	@RequestMapping(value ="/list/rds/node/keyBuffer",method=RequestMethod.GET)
	public ModelAndView toRdsNodeKeyBufferList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/rds_monitor_node_key_buffer");
		return mav;
	}
	
	@RequestMapping(value ="/list/rds/node/innodb",method=RequestMethod.GET)
	public ModelAndView toRdsNodeInnodbList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/rds_monitor_node_innodb");
		return mav;
	}
	
	@RequestMapping(value ="/list/rds/node/tableSpace/{dbSpaceId}",method=RequestMethod.GET)
	public ModelAndView toRdsNodeTableSpaceList(@PathVariable int dbSpaceId,ModelAndView mav,HttpServletRequest request){
		mav.addObject("dbSpaceId",dbSpaceId);
		mav.setViewName("/clouddb/rds_monitor_node_table_space");
		return mav;
	}
	
	@RequestMapping(value ="/list/rds/node/dbSpace",method=RequestMethod.GET)
	public ModelAndView toRdsNodeDbSpaceList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/rds_monitor_node_db_space");
		return mav;
	}
	@RequestMapping(value ="/list/rds/node/galera",method=RequestMethod.GET)
	public ModelAndView toRdsNodeGaleraList(ModelAndView mav,HttpServletRequest request){
		mav.setViewName("/clouddb/rds_monitor_node_galera");
		return mav;
	}
}
