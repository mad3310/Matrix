package com.letv.portal.controller.cloudgce;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.portal.model.gce.GceImage;
import com.letv.portal.service.gce.IGceImageService;

@Controller
@RequestMapping("/gceImg")
public class GceImgController {
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IGceImageService gceImageService;
	

	private final static Logger logger = LoggerFactory.getLogger(GceImgController.class);

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody ResultObject imageList(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("owner", sessionService.getSession().getUserId());
		obj.setData(this.gceImageService.selectPageByParams(page, params));
		return obj;
	}

	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody ResultObject addImage(@Valid @ModelAttribute GceImage gceImage,BindingResult result) {
		ResultObject obj = new ResultObject();
        if(result.hasErrors())
            return new ResultObject(result.getAllErrors());
        gceImage.setOwner(sessionService.getSession().getUserId());
		this.gceImageService.insert(gceImage);
        obj.setData(gceImage.getUrl());
		return obj;
	}
	
	@RequestMapping(value="/group", method=RequestMethod.POST)
	public @ResponseBody ResultObject addImageWithIds(@RequestParam String userNames, @Valid @ModelAttribute GceImage gceImage,BindingResult result) {
		ResultObject obj = new ResultObject();
		if(result.hasErrors())
			return new ResultObject(result.getAllErrors());
		this.gceImageService.saveInfoWithUserNames(userNames, gceImage);
		return obj;
	}

	@RequestMapping(method=RequestMethod.DELETE)
	public @ResponseBody ResultObject delImage(@PathVariable Long id) {
		ResultObject obj = new ResultObject();
		if(id == null)
			throw new ValidateException("镜像id无效");
		GceImage gceImage = this.gceImageService.selectById(id);
		if(gceImage == null)
			throw new ValidateException("镜像id无效");
        if(!gceImage.getOwner().equals(sessionService.getSession().getUserId()))
            throw new ValidateException("当前用户操作无权限");
		this.gceImageService.delete(gceImage);
        return obj;
	}

	@RequestMapping(value="/{id}",method=RequestMethod.POST)
	public @ResponseBody ResultObject updateImage(@PathVariable Long id,@Valid @ModelAttribute GceImage gceImage,BindingResult result) {
		ResultObject obj = new ResultObject();
        if(result.hasErrors())
            return new ResultObject(result.getAllErrors());
        if(id == null)
			throw new ValidateException("参数不合法");
		GceImage oldGceImage = this.gceImageService.selectById(id);
		if(oldGceImage == null)
			throw new ValidateException("参数不合法");
        if(!gceImage.getOwner().equals(sessionService.getSession().getUserId()))
            throw new ValidateException("当前用户操作无权限");

		this.gceImageService.updateBySelective(gceImage);
		return obj;
	}

}
