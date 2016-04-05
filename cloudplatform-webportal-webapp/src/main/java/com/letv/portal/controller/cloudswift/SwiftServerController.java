package com.letv.portal.controller.cloudswift;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.OssServerVisibility;
import com.letv.portal.model.swift.SwiftServer;
import com.letv.portal.proxy.ISwiftServerProxy;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.swift.ISwiftServerService;

@Controller
@RequestMapping("/oss")
public class SwiftServerController {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	@Autowired
	private ISwiftServerService swiftServerService;
	@Autowired
	private ISwiftServerProxy swiftServerProxy;
	@Autowired
	private IHostService hostService;
	
	private final static Logger logger = LoggerFactory.getLogger(SwiftServerController.class);
	
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		obj.setData(this.swiftServerService.selectPageByParams(page, params));
		return obj;
	}
	
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(SwiftServer swift,ResultObject obj) {
		if(swift == null || StringUtils.isEmpty(swift.getName()))
			throw new ValidateException("参数不合法");
		swift.setCreateUser(this.sessionService.getSession().getUserId());
		swift.setStatus(DbStatus.BUILDDING.getValue());
		swift.setVisibilityLevel(OssServerVisibility.PRIVATE);
		this.swiftServerProxy.saveAndBuild(swift);
		return obj;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public @ResponseBody ResultObject detail(@PathVariable Long id){
		isAuthoritySwift(id);
		ResultObject obj = new ResultObject();
		SwiftServer swift = this.swiftServerService.selectById(id);
		swift.setHosts(this.hostService.selectByHclusterId(swift.getHclusterId()));
		obj.setData(swift);
		return obj;
	}	
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public @ResponseBody ResultObject delete(@PathVariable Long id){
		isAuthoritySwift(id);
		ResultObject obj = new ResultObject();
		this.swiftServerProxy.deleteAndBuild(id);
		return obj;
	}	
	@RequestMapping(value="/{id}/file",method=RequestMethod.GET)
	public @ResponseBody ResultObject getFile(@PathVariable Long id, String directory){
		isAuthoritySwift(id);
		ResultObject obj = new ResultObject();
		obj.setData(this.swiftServerProxy.getFiles(id,directory));
		return obj;
	}	
	@RequestMapping(value="/{id}/file",method=RequestMethod.POST)
	public ModelAndView postFile(@PathVariable Long id, @RequestParam MultipartFile file,@RequestParam String directory,ModelAndView mav){
		isAuthoritySwift(id);
        this.swiftServerProxy.postFiles(id,file,directory);
        mav.addObject("swiftId", id);
		mav.setViewName("/cloudswift/fileManage");
		return mav;
	}	
	@RequestMapping(value="/{id}/file/del",method=RequestMethod.POST)
	public  @ResponseBody ResultObject  deleteFile(@PathVariable Long id,@RequestParam String file,@RequestParam boolean isFolder){
		ResultObject obj = new ResultObject();
		isAuthoritySwift(id);
		this.swiftServerProxy.deleteFile(id,file,isFolder);
		return obj;
	}	
	@RequestMapping(value="/{id}/folder",method=RequestMethod.POST)
	public @ResponseBody ResultObject addFolder(@Valid FolderModel folderModel, @PathVariable Long id){
		isAuthoritySwift(id);
		this.swiftServerProxy.addFolder(id,folderModel.getFile(),folderModel.getDirectory());
		ResultObject obj = new ResultObject();
		return obj;
	}	
	@RequestMapping(value="/{id}/file/prefixUrl",method=RequestMethod.GET)
	public @ResponseBody ResultObject prefixUrl(@PathVariable Long id){
		isAuthoritySwift(id);
		ResultObject obj = new ResultObject();
		SwiftServer  server = this.swiftServerService.selectById(id);
		server.setHosts(this.hostService.selectByHclusterId(server.getHclusterId()));
		StringBuffer sb = new StringBuffer();
		sb.append("https://").append(server.getHosts().get(0).getHostIp()).append(":443/").append("v1/AUTH_").append(sessionService.getSession().getUserName()).append("/").append(server.getName()).append("/");
		obj.setData(sb);
		return obj;
	}	
	@RequestMapping(value="/config",method=RequestMethod.POST)
	public @ResponseBody ResultObject config(Long id,Long storeSize,String visibilityLevel){
		if(storeSize ==null || StringUtils.isEmpty(visibilityLevel))
			throw new ValidateException("参数不合法");
		isAuthoritySwift(id);
		ResultObject obj = new ResultObject();
		this.swiftServerProxy.changeService(id,visibilityLevel,storeSize);
		return obj;
	}	
	
	private void isAuthoritySwift(Long id) {
		if(id == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", id);
		map.put("createUser", sessionService.getSession().getUserId());
		List<SwiftServer> swifts = this.swiftServerService.selectByMap(map);
		if(swifts == null || swifts.isEmpty())
			throw new ValidateException("参数不合法");
	}
}
