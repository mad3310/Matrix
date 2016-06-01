package com.letv.portal.zabbixPush;

import java.util.List;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.zabbix.ZabbixPushModel;

 
public interface IZabbixPushService{
	
	/**
	 * Methods Name: createMultiContainerPushZabbixInfo <br>
	 * Description:创建多个container<br>
	 * @author name: wujun
	 * @param containerModels
	 */
	public ApiResultObject createMultiContainerPushZabbixInfo(List<ContainerModel> containerModels);
	
	/**
	 * Methods Name: MultiContainerPushZabbixInfo <br>
	 * Description:  删除单个container<br>
	 * @author name: wujun
	 * @param containerModels
	 */
	public ApiResultObject deleteSingleContainerPushZabbixInfo(ContainerModel containerModel, String auth);
	/**
	 * Methods Name: deleteMutilContainerPushZabbixInfo <br>
	 * Description: 删除多个container信息<br>
	 * @author name: wujun
	 * @param containerModel
	 * @return
	 */
	public ApiResultObject deleteMutilContainerPushZabbixInfo(List<ContainerModel> list);
	/**
	 * Methods Name: loginZabbix <br>
	 * Description:登陆zabbix系统<br>
	 * @author name: wujun
	 * @param zabbixPushModel
	 * @return
	 */
	public String loginZabbix(); 
	/**
	 * Methods Name: createContainerPushZabbixInfo <br>
	 * Description: 创建container时向zabbix系统推送信息<br>
	 * @author name: wujun
	 * @param zabbixPushModel
	 * @return
	 */
	public ApiResultObject pushZabbixInfo(ZabbixPushModel zabbixPushModel,Long containerId); 
	
	/**
	 * Methods Name: sendFixedInfo <br>
	 * Description: 向zabbix系统发送固资信息<br>
	 * @author name: wujun
	 * @throws Exception 
	 */
	public String sendZabbixInfo(Object object) throws Exception;
	/**
	 * Methods Name: receviceFixedInfo <br>
	 * Description: 接受zabbix系统的固资信息<br>
	 * @author name: wujun
	 */
	public String receviceZabbixInfo(ZabbixPushModel zabbixPushModel)throws Exception;
}
