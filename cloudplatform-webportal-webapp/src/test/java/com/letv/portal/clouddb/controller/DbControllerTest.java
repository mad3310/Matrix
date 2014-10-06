package com.letv.portal.clouddb.controller;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.DbModel;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.letv.portal.service.IMclusterService;

public class DbControllerTest extends AbstractTest{
	@Resource
	private IDbService dbService;
	@Resource
	private IContainerService containerService;
	@Resource
	private IMclusterService mclusterService;
	@Resource
	private IDbUserService dbUserService;
	
    @Test
	public void toList(){
    	dbService.hashCode();
    	System.out.println(dbService.hashCode());
	}
    @Test
	public void list(){
		Page page = new Page();
		page.setCurrentPage(1);
		page.setRecordsPerPage(11);
	
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dbName", "%");
		params.put("createUser", "yaokuo@letv.com");
		
		ResultObject obj = new ResultObject();
		obj.setData(this.dbService.findPagebyParams(params, page));
		System.out.println(obj);
	}
    /*@Test
	public void saveInsert(){
    	DbApplyStandardModel dbApplyStandardModel = new DbApplyStandardModel();
//		dbApplyStandardModel.setCreateUser(123);
		dbApplyStandardModel.setStatus("1");
		this.dbApplyStandardService.insert(dbApplyStandardModel);
	}
    @Test
   	public void saveUpdate(){
    	DbApplyStandardModel dbApplyStandardModel1 = new DbApplyStandardModel();
//    	dbApplyStandardModel1.setCreateUser("letvTest1");
    	dbApplyStandardModel1.setStatus("2");
		this.dbApplyStandardService.updateBySelective(dbApplyStandardModel1);
    }*/
    @Test
    public void detail(){
    	Long dbId=1L;
		DbModel dbModel = this.dbService.selectById(dbId);
		ModelAndView mav = new ModelAndView();
		mav.addObject("containers", this.containerService.selectByClusterId(dbModel.getMclusterId()));
		mav.addObject("dbUsers", this.dbUserService.selectByDbId(dbId));
		mav.addObject("db", dbModel);
		mav.setViewName("/clouddb/user_db_detail");
		System.out.println(mav);
    }
}
