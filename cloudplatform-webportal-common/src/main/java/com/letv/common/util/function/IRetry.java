package com.letv.common.util.function;

@SuppressWarnings("hiding")
public interface IRetry<R, Boolean> {
    /**
      * @Title: execute
      * @Description: 方法执行
      * @return R   
      * @throws 
      * @author lisuxiao
      */
    R execute();
    /**
      * @Title: judgeAnalyzeResult
      * @Description: 分析执行结果
      * @param o
      * @return T   
      * @throws 
      * @author lisuxiao
      */
    Boolean judgeAnalyzeResult(Object o);
    /**
     * @Title: analyzeResult
     * @Description: 获取执行结果
     * @param r
     * @return A   
     * @throws 
     * @author lisuxiao
     */
    Object analyzeResult(R r);
}
