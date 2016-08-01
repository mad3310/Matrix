package com.letv.portal.task.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.task.TaskChain;
import com.letv.portal.model.task.TaskChainIndex;
import com.letv.portal.model.task.service.ITaskChainIndexService;
import com.letv.portal.model.task.service.ITaskChainService;
import com.letv.portal.model.task.service.ITaskEngine;
 
public class TaskEngineRDS extends AbstractTest{

	@Autowired
	private ITaskEngine taskEngine;
	
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	@Autowired
	private ITaskChainService taskChainService;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEngineRDS.class);
	
    /**Methods Name: testRun <br>
     * Description: 创建流程并执行,带参数<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun5() {
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("mclusterId", 7L);
    	params.put("dbId", 6L);
    	this.taskEngine.run(5L,params);
    }
    
    /**Methods Name: testRun <br>
     * Description: 失败的重新执行，使用旧参数<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun6() {
    	TaskChain tc = this.taskChainService.selectFailedChainByIndex(2L);
    	//this.taskEngine.run(tc);
    }
    
}
