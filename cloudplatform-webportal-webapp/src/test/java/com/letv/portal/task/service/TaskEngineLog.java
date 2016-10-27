package com.letv.portal.task.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.log.LogCluster;
import com.letv.portal.model.log.LogServer;
import com.letv.portal.model.task.TaskChain;
import com.letv.portal.model.task.service.ITaskChainIndexService;
import com.letv.portal.model.task.service.ITaskChainService;
import com.letv.portal.model.task.service.ITaskEngine;
import com.letv.portal.service.log.ILogClusterService;
import com.letv.portal.service.log.ILogServerService;
 
public class TaskEngineLog extends AbstractTest{

	@Autowired
	private ITaskEngine taskEngine;
	
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	@Autowired
	private ITaskChainService taskChainService;
	@Autowired
	private ILogClusterService logClusterService;
	@Autowired
	private ILogServerService logServerService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEngineLog.class);
	
    /**Methods Name: testRun <br>
     * Description: 创建流程并执行,带参数<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun5() {
    	LogCluster cluster = new LogCluster();
    	cluster.setClusterName("testLogCluster");
    	cluster.setAdminUser(cluster.getClusterName());
    	cluster.setAdminPassword(cluster.getClusterName());
    	cluster.setStatus(0);
    	cluster.setHclusterId(1L);
    	cluster.setCreateUser(4L);
    	this.logClusterService.insert(cluster);
    	
    	LogServer log = new LogServer();
    	log.setLogName("testLog");
    	log.setLogClusterId(cluster.getId());
    	this.logServerService.insert(log);
    	
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("logClusterId", cluster.getId());
    	params.put("logId", log.getId());
    	this.taskEngine.run("LOG_BUY",params);
    }
    
    /**Methods Name: testRun <br>
     * Description: 失败的重新执行，使用旧参数<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun6() {
    	TaskChain tc = this.taskChainService.selectFailedChainByIndex(2L);
//    	this.taskEngine.run(tc);
    }
    
}
