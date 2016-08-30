/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.task.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.task.TaskChain;
import com.letv.portal.model.task.service.ITaskChainService;
import com.letv.portal.model.task.service.ITaskEngine;

/**
 * 工作流引擎测试<br/>
 * 1.命名规范	2.时间复杂度
 * @author linzhanbo .
 * @since 2016年7月26日, 下午2:41:47 .
 * @version 1.0 .
 */
public class TaskEngineTest extends AbstractTest {
	
	private final static Logger logger = LoggerFactory.getLogger(TaskEngineTest.class);
	@Autowired
	private ITaskEngine taskEngine;
	@Test
	public void testProcess(){
		Map<String,Object> params = new HashMap<String,Object>();
    	params.put("mclusterId", 7L);
    	params.put("dbId", 6L);
		taskEngine.run("ES_BUY", params);
	}
	
	/**
	 * 失败的重新执行，使用旧参数
	 * @author linzhanbo .
	 * @since 2016年7月26日, 下午3:17:05 .
	 * @version 1.0 .
	 */
    @Test
    public void testRun6() {
    }
}
