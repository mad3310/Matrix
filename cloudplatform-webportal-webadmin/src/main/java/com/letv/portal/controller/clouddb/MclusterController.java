package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.adminoplog.AoLogType;
import com.letv.portal.proxy.IMclusterProxy;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.adminoplog.AoLog;
import com.letv.portal.service.adminoplog.ClassAoLog;
import com.letv.portal.zabbixPush.IZabbixPushService;

@ClassAoLog(module="RDS管理/集群管理")
@Controller
@RequestMapping("/mcluster")
public class MclusterController {
	
	@Autowired
	private IMclusterProxy mclusterProxy;
	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	public IZabbixPushService zabbixPushService;
	@Autowired
	public IFixedPushService fixedPushService;

	private final static Logger logger = LoggerFactory.getLogger(MclusterController.class);
	
	@RequestMapping(value="/list",method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		obj.setData(this.mclusterService.selectPageByParams(page, params));
		return obj;
	}
	
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(ResultObject result) {
		result.setData(this.mclusterService.select4Run());
		return result;
	}	
	
	@RequestMapping(value="/valid",method=RequestMethod.GET)   
	public @ResponseBody ResultObject validList(ResultObject result) {
		result.setData(this.mclusterService.selectValidMclusters());
		return result;
	}
	
	/**
	 * 打开/关闭备份
	 * @param result
	 * @return
	 * @author linzhanbo .
	 * @since 2016年10月21日, 下午3:17:02 .
	 * @version 1.0 .
	 */
	@RequestMapping(value="/openOrcloseBackupSwitch/{mclusterId}",method=RequestMethod.PUT)   
	public @ResponseBody ResultObject openOrcloseBackupSwitch(@PathVariable Long mclusterId,ResultObject result) {
		MclusterModel mclusterModel = mclusterService.selectById(mclusterId);
		if(null == mclusterModel){
			result.setResult(0);
			result.addMsg("集群不存在");
			return result;
		}
		//打开/关闭备份
		mclusterModel.setCanBackup(!mclusterModel.getCanBackup());
		mclusterService.update(mclusterModel);
		return result;
	}
	
	@RequestMapping(value="/valid/{hclusterId}",method=RequestMethod.GET)   
	public @ResponseBody ResultObject validList(@PathVariable Long hclusterId,ResultObject result) {
		if(hclusterId == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("hclusterId", hclusterId);
		result.setData(this.mclusterService.selectValidMclustersByMap(map));
		return result;
	}	

	/**Methods Name: save <br>
	 * Description:  保存并创建mcluster<br>
	 * @author name: liuhao1
	 * @param mclusterModel
	 */
	@AoLog(desc="保存并创建mcluster",type=AoLogType.INSERT)
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(MclusterModel mclusterModel,ResultObject result) {
		this.mclusterProxy.insertAndBuild(mclusterModel);
		return result;
	}
	
	/**Methods Name: validate <br>
	 * Description: 根据mclusterName验证重复性<br>
	 * @author name: liuhao1
	 * @param mclusterName
	 * @return
	 */
	@AoLog(desc="根据mclusterName验证重复性",type=AoLogType.VALIDATE,ignore = true)
	@RequestMapping(value="/validate",method=RequestMethod.POST)
	public @ResponseBody Map<String,Object> validate(String mclusterName) {
		Map<String,Object> map = new HashMap<String,Object>();
		Boolean isExist= this.mclusterService.isExistByName(mclusterName);
		map.put("valid", isExist);
		return map;
	}
	
	/**Methods Name: delete <br>
	 * Description: contianer集群删除<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	@AoLog(desc="contianer集群删除",type=AoLogType.DELETE)
	@RequestMapping(value = "/{mclusterId}", method=RequestMethod.DELETE) 
	public @ResponseBody ResultObject delete(@PathVariable Long mclusterId,ResultObject result) {
		if(mclusterId == null)
			throw new ValidateException("参数不合法");
		MclusterModel mcluster = this.mclusterService.selectById(mclusterId);
		if(mcluster == null || MclusterStatus.BUILDFAIL.getValue() != mcluster.getStatus())
			throw new ValidateException("参数不合法");
		this.mclusterProxy.deleteAndRemove(mclusterId);
		return result;
	}
	@AoLog(desc="增加集群节点",type=AoLogType.UPDATE)
	@RequestMapping(value = "/dilatation", method=RequestMethod.POST)
	public @ResponseBody ResultObject dilatation(Long mclusterId,int count,ResultObject result) {
		if(mclusterId == null || count <=0)
			throw new ValidateException("参数不合法");
		this.mclusterProxy.addContainerOnMcluster(mclusterId,count);
		return result;
	}
	/**Methods Name: start <br>
	 * Description: 启动container集群<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	@AoLog(desc="启动container集群",type=AoLogType.START)
	 @RequestMapping(value = "/start", method=RequestMethod.POST)
	 public @ResponseBody ResultObject start(Long mclusterId,ResultObject result) {
		this.mclusterProxy.start(mclusterId);
		return result;
	}
	/**Methods Name: stop <br>
	 * Description: 关闭container集群<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	@AoLog(desc="停止container集群",type=AoLogType.STOP)
	@RequestMapping(value = "/stop", method=RequestMethod.POST) 
	public @ResponseBody ResultObject stop(Long mclusterId,ResultObject result) {
		this.mclusterProxy.stop(mclusterId);
		return result;
	}

	/**Methods Name: restartDb <br>
	 * Description: <br>
	 * @author name: liuhao1
	 * @return
	 */
	@AoLog(desc="重启container集群",type=AoLogType.RESTART)
	@RequestMapping(value="/restart",method=RequestMethod.POST)
	public @ResponseBody ResultObject restartDb(Long mclusterId,ResultObject obj){
		if(mclusterId == null) 
			throw new ValidateException("参数不合法");
		this.mclusterProxy.restartDb(mclusterId);
		return obj;
	}
	
	@AoLog(desc="修改container集群所属用户",type=AoLogType.UPDATE)
	@RequestMapping(value="/updateUser",method=RequestMethod.POST)
	public @ResponseBody ResultObject updateUser(Long mclusterId, Long userId, ResultObject obj){
		if(mclusterId == null) 
			throw new ValidateException("参数不合法");
		this.mclusterService.updateUser(mclusterId, userId);
		return obj;
	}
	
}
