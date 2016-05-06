package com.letv.portal.task.rds.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.JsonUtils;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.UserModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IPythonService;
import com.letv.portal.service.IBuildService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IMonitorService;
import com.letv.portal.service.IUserService;
import com.mysql.jdbc.StringUtils;

@Service("taskDbCreateService")
public class TaskDbCreateServiceImpl extends BaseTask4RDSServiceImpl implements IBaseTaskService{
	
	
	@Autowired
	private IPythonService pythonService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IBuildService buildService;
	@Autowired
	private IUserService userService;
	
	@Autowired 
	private IMonitorService monitorService;
	@Value("${service.notice.email.to}")
	private String SERVICE_NOTICE_MAIL_ADDRESS;	
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskDbCreateServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		//执行业务
		Long dbId = getLongFromObject(params.get("dbId"));
		if(dbId == null)
			throw new ValidateException("params's dbId is null");
		Map<String,Object> createParams = this.dbService.selectCreateParams(dbId,false);
		if(createParams.isEmpty())
			throw new ValidateException("create params's is null by dbId");
		
		ApiResultObject result = this.pythonService.createDb((String)createParams.get("nodeIp"), (String)createParams.get("dbName"), (String)createParams.get("dbName"), null, (String)createParams.get("username"), (String)createParams.get("password"));
		tr = analyzeRestServiceResult(result);
		
		if(tr.isSuccess()) {
			Map<String,Object> emailParams = this.getDbConfig(createParams);
			this.email4User(emailParams,((BigInteger)createParams.get("createUser")).longValue(),"createDb.ftl");
			DbModel dbModel = new DbModel();
			dbModel.setId(dbId);
			dbModel.setStatus(DbStatus.NORMAL.getValue());
			this.dbService.updateBySelective(dbModel);
		}
		
		//set params
		tr.setParams(params);
		return tr;
	}
	
	private Map<String,Object> getDbConfig(Map<String,Object> createParams) throws Exception {
		List<ContainerModel> containers = this.containerService.selectByMclusterId(((BigInteger)createParams.get("mclusterId")).longValue());
		Map<String,Object> glbParams = new HashMap<String,Object>();
		List<String> urlPorts = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();
		for (ContainerModel container : containers) {
			if("mclusternode".equals(container.getType()) || "mclusteraddnode".equals(container.getType())) {
				buffer.setLength(0);
                urlPorts.add(buffer.append(container.getIpAddr()).append(":3306").toString());
			}
		}
		glbParams.put("User", "monitor");
		glbParams.put("Pass", createParams.get("sstPwd"));
		glbParams.put("Addr", "127.0.0.1");
		glbParams.put("Port", "3306");
		glbParams.put("Backend", urlPorts);
		
		Map<String,Object> emailParams = new HashMap<String,Object>();
		emailParams.put("dbName", createParams.get("dbName"));
		emailParams.put("glbStr", JsonUtils.writeObject(glbParams));
		
		return emailParams;
	}
	
	public void email4User(Map<String,Object> params,Long to,String ftlName){
		UserModel user = this.userService.selectById(to);
		if(null != user) {
			MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统",user.getEmail(),"乐视云平台web-portal系统通知",ftlName,params);
			mailMessage.setHtml(true);
			try {
				defaultEmailSender.sendMessage(mailMessage);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			
		}
	}
	public void buildResultToMgr(String buildType,String result,String detail,String to){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buildType", buildType);
		map.put("buildResult", result);
		map.put("errorDetail", detail);
		MailMessage mailMessage = new MailMessage("乐视云平台web-portal系统", StringUtils.isNullOrEmpty(to)?SERVICE_NOTICE_MAIL_ADDRESS:to,"乐视云平台web-portal系统通知","buildForMgr.ftl",map);
		try {
			defaultEmailSender.sendMessage(mailMessage);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
}
