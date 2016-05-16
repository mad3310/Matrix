package com.letv.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.util.function.IRetry;

public class RetryUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(RetryUtil.class);
	
    
    /**
     * @Title: retryByTime
     * @Description: 按时间重试
     * @param process 执行方法
     * @param totalTime 执行总时间
     * @param intervalTime 执行间隔时间
     * @throws 
     * @author lisuxiao
     */
    public static Map<String, Object> retryByTime(IRetry<Object, Boolean> process, long totalTime, long intervalTime) {
    	Map<String, Object> ret = new HashMap<String, Object>();
    	Long start = new Date().getTime();
		while(true) {
			//执行结果
			Object executeResult = process.execute();
			ret.put("executeResult", executeResult);
			//分析结果
			Object analyzeResult = process.analyzeResult(executeResult);
			
			if (process.judgeAnalyzeResult(analyzeResult)) {
				ret.put("analyzeResult", analyzeResult);
				ret.put("judgeAnalyzeResult", true);
				break;
			}
			
			try {
    			Thread.sleep(intervalTime);
    		} catch (InterruptedException e) {
    			logger.error("重试方法中线程sleep出错", e);
    			ret.put("judgeAnalyzeResult", false);
    			break;
    		}
			if(new Date().getTime()-start > totalTime) {
				ret.put("judgeAnalyzeResult", false);
				break;
			}
		}
		return ret;
    }
}
