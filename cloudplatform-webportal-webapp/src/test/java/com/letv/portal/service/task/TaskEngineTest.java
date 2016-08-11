package com.letv.portal.service.task;

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
 
public class TaskEngineTest extends AbstractTest{

	@Autowired
	private ITaskEngine taskEngine;
	
	@Autowired
	private ITaskChainIndexService taskChainIndexService;
	@Autowired
	private ITaskChainService taskChainService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEngineTest.class);
	
    /**Methods Name: testRun <br>
     * Description: 创建流程并执行<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun() {
    	this.taskEngine.run(1L);
    }
    
    /**Methods Name: testRun2 <br>
     * Description: 执行：从失败节点开始<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun3() {
    	TaskChain tc = this.taskChainService.selectFailedChainByIndex(31L);
    	this.taskEngine.run(tc);
    }
    
    //-----------------------------------------------------------------------------------------------------
    
    /**Methods Name: testRun <br>
     * Description: 创建流程并执行,带参数<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun4() {
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("id", 1);
    	params.put("name", "test");
    	this.taskEngine.run(1L,params);
    }
    
    /**Methods Name: testRun <br>
     * Description: 创建流程并执行,带参数，第二步骤失败<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun5() {
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("id", 0);
    	params.put("name", "test");
    	this.taskEngine.run(1L,params);
    }
    
    /**Methods Name: testRun <br>
     * Description: 失败的重新执行，使用旧参数<br>
     * @author name: liuhao1
     */
    @Test
    public void testRun6() {
    	TaskChain tc = this.taskChainService.selectFailedChainByIndex(42L);
    	this.taskEngine.run(tc);
    }
    
}
