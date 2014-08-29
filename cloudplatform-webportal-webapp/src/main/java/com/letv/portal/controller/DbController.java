package com.letv.portal.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.letv.common.util.HttpUtil;

@Controller
@RequestMapping("/db")
public class DbController {
	@RequestMapping(value="/list")
	public String list(HttpServletRequest request,HttpServletResponse response){
		return "/clouddb/user_db_list";
	}
	
	@RequestMapping(value="/mgrList")
	public String mgrList(HttpServletRequest request,HttpServletResponse response){
		return "/clouddb/mgr_db_list";
	}
	
	@RequestMapping(value="/toForm")
	public String toForm(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("clusterId", request.getParameter("clusterId"));
		return "/clouddb/user_db_applyform";
	}	
	
	@RequestMapping("/list/data")   //http://localhost:8080/db/list/data
	public void listData(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		String flag = request.getParameter("flag");
		if("self".equals(flag)){
			//取出session中用户id，追加到http rest请求中
			map.put("createUser", (String) request.getSession().getAttribute("userId"));
		}
				
		PrintWriter out;
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter();
			out.write(HttpUtil.getResultFromDBAPI(request,"/db/list",map));
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping("/save")   //http://localhost:8080/mcluster/save
	public String save(HttpServletRequest request,HttpServletResponse response) {
		
		//取出session中用户id，追加到http rest请求中
		Map<String,String> map = new HashMap<String,String>();
		map.put("createUser", (String) request.getSession().getAttribute("userId"));
		map.put("status", "1");
		
		String result = HttpUtil.getResultFromDBAPI(request,"/dbApplyStandard/save",map);
		if(result.contains("\"result\":1")) {
			return "redirect:/db/list";
		}
		return "common/error";
	}
	
	@RequestMapping(value="/dbApplyInfo")
	public String dbApplyInfo(HttpServletRequest request,HttpServletResponse response){
		return "/clouddb/user_db_apply_info";
	}
	
	@RequestMapping(value="/mgr/dbApplyInfo")
	public String dbApplyInfo(HttpServletRequest request,HttpServletResponse response){
		return "/clouddb/mgr_db_apply_info";
	}
	
	@RequestMapping("/list/dbApplyInfo")   //http://localhost:8080/db/list/data
	public void listDbApplyInfo(HttpServletRequest request,HttpServletResponse response) {
		
		//取出session中用户id，追加到http rest请求中
		Map<String,String> map = new HashMap<String,String>();
		map.put("createUser", (String) request.getSession().getAttribute("userId"));
				
		PrintWriter out;
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter();
			out.write(HttpUtil.getResultFromDBAPI(request,"/dbApplyStandard/getByDbId",map));
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	

	@RequestMapping(value="/toMgrAudit")
	public String toMgrAduit(HttpServletRequest request,HttpServletResponse response){
		String getHostResult = HttpUtil.getResultFromDBAPI(request,"/host/list",null);
		String getMclusterResult = HttpUtil.getResultFromDBAPI(request,"/mcluster/list",null);
		
		ObjectMapper hostMapper = new ObjectMapper();
		ObjectMapper mclusterMapper = new ObjectMapper();
		Map hostMap = null;
		Map mclusterMap = null;
		try {
			hostMap = hostMapper.readValue(getHostResult, Map.class);
			mclusterMap = mclusterMapper.readValue(getMclusterResult, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("hostList", hostMap.get("data"));
		request.setAttribute("mclusterList", ((Map)mclusterMap.get("data")).get("data"));
		return "/clouddb/mgr_audit_db";
	}

	@RequestMapping("/toMgrAudit/save")   //http://localhost:8080/mcluster/save
	public String toMgrAuditSave(HttpServletRequest request,HttpServletResponse response) {
		
		//取出session中用户id，追加到http rest请求中
		Map<String,String> map = new HashMap<String,String>();
		map.put("createUser", (String) request.getSession().getAttribute("userId"));
		map.put("status", "1");
		
		String result = HttpUtil.getResultFromDBAPI(request,"/db/audit/save",map);
		if(result.contains("\"result\":1")) {
			return "redirect:/db/mgrList";
		}
		return "common/error";
	}
	@RequestMapping("/createDb/onOldCluster/save")   //http://localhost:8080/mcluster/save
	public String createDbOnOldClusterSave(HttpServletRequest request,HttpServletResponse response) {
		
		//取出session中用户id，追加到http rest请求中
		Map<String,String> map = new HashMap<String,String>();
		map.put("createUser", (String) request.getSession().getAttribute("userId"));
		map.put("status", "1");
		
		String result = HttpUtil.getResultFromDBAPI(request,"/db/audit/save",map);
		if(result.contains("\"result\":1")) {
			return "redirect:/db/mgrList";
		}
		return "common/error";
	}
}
