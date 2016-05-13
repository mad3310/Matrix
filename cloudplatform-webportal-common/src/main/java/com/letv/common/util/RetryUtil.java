package com.letv.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.model.ErrorMailMessageModel;
import com.letv.common.util.function.IRetry;

@Service("retryUtil")
public class RetryUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(RetryUtil.class);
	
	@Autowired
	ExceptionEmailServiceUtil exceptionEmailServiceUtil;

    /**
      * @Title: retryByTimes
      * @Description: 按次数重试
      * @param process 执行方法
      * @param times 执行次数
      * @param mailMessageModel 当执行相应次数后未成功发送邮件   
      * @throws 
      * @author lisuxiao
      */
    public void retryByTimes(IRetry<Object, Boolean> process, int times, ErrorMailMessageModel mailMessageModel) {
    	Object result = null;
        for (int i = 0; i < times; i++) {
        	try {
				result = process.execute();
				if (process.judgeAnalyzeResult(process.analyzeResult(result))) {
				    return;
				}
			} catch (Exception e1) {
				if(null != mailMessageModel) {
		        	exceptionEmailServiceUtil.sendErrorEmail(mailMessageModel.getExceptionMessage(), 
							mailMessageModel.getExceptionContent()+"返回结果:"+result, mailMessageModel.getRequestUrl());
		        }
			}
            try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				logger.error("重试方法中线程sleep出错", e);
			}
        }
        if(null != mailMessageModel) {
        	exceptionEmailServiceUtil.sendErrorEmail(mailMessageModel.getExceptionMessage(), 
					mailMessageModel.getExceptionContent()+"返回结果:"+result, mailMessageModel.getRequestUrl());
        }
    }
    
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
				break;
			}
			
			try {
    			Thread.sleep(intervalTime);
    		} catch (InterruptedException e) {
    			logger.error("重试方法中线程sleep出错", e);
    			ret.put("analyzeResult", false);
    			break;
    		}
			if(new Date().getTime()-start > totalTime) {
				ret.put("analyzeResult", false);
				break;
			}
		}
		return ret;
    }
}
