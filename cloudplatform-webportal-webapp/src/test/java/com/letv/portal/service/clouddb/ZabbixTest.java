package com.letv.portal.service.clouddb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.service.IContainerService;
import com.letv.portal.zabbixPush.IZabbixPushService;

public class ZabbixTest extends AbstractTest {
	private final static Logger logger = LoggerFactory
			.getLogger(UserLoginTest.class);
	@Resource
	public IZabbixPushService zabbixPushService;
	
	@Resource
	public IContainerService containerService;

   /**
    * Methods Name: createMultiContainerPushZabbixInfo <br>
    * Description: 创建多个container，containerName不能重复<br>
    * @author name: wujun
    */
	@Test
	public void createMultiContainerPushZabbixInfo() {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", 17);
		zabbixPushService.createMultiContainerPushZabbixInfo(this.containerService.selectWithHClusterNameByMap(map));
	}

	@Test
	public void createSingleContainer(){
		Boolean flag  =false;
		try {
			List<ContainerModel> list  = new ArrayList<ContainerModel>();
			ContainerModel containerModel1 = new ContainerModel();
			containerModel1.setContainerName("abc");
			containerModel1.setIpAddr("10.154.238.147");
			containerModel1.setId(3L);
			containerModel1.setType("mclustervip");
			list.add(containerModel1);
		
			ApiResultObject apiResult = zabbixPushService.createMultiContainerPushZabbixInfo(list);
			System.out.println(apiResult.getAnalyzeResult());
		} catch (Exception e) {
			logger.debug("zabbix创建成功");
		}
		
	}
	@Test
	public void createMutilContainer(){
		Boolean flag  =false;
		try {
			List<ContainerModel> list  = new ArrayList<ContainerModel>();
			ContainerModel containerModel1 = new ContainerModel();
			containerModel1.setContainerName("daocaorgege");
			containerModel1.setIpAddr("10.58.166.21");
			containerModel1.setId(3L);
			containerModel1.setType("mclustervip");
			list.add(containerModel1);
		
			ApiResultObject apiResult = zabbixPushService.createMultiContainerPushZabbixInfo(list);
			System.out.println(apiResult.getAnalyzeResult());
		} catch (Exception e) {
			logger.debug("zabbix创建成功");
		}
		
	}
   @Test
   public void deleContainer(){
	   Map<String, Object> map = new HashMap<String, Object>();
	   map.put("mclusterId", 17);
		
	   Boolean flag  =false;

		try {
			
		 ApiResultObject apiResult =	zabbixPushService.deleteMutilContainerPushZabbixInfo(this.containerService.selectByMap(map));
	     System.out.println(apiResult.getAnalyzeResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   } 
   
	@Test
    public void login(){
	  try {
		  List<ContainerModel> list = this.containerService.selectContainerByMclusterId(227L);
		  System.out.println("xx");
		//  zabbixPushService.loginZabbix();
	} catch (Exception e) {
		logger.debug("zabbix"+e.getMessage());
	}
	}
}
