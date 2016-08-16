package com.letv.common.util;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liuhao1 on 2016/1/4.
 * @param <T>
 */
public class JsonUtil {
	@Deprecated
    public static String transToString(Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
	/**
	 * json串转对象
	 * @param paramsJsonStr
	 * @param destClazz	转换后目标数据类型
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @author linzhanbo .
	 * @since 2016年8月10日, 下午6:33:16 .
	 * @version 1.0 .
	 * @param <T>
	 */
	public static Object fromJson(String paramsJsonStr,Object destClazz) throws JsonParseException, JsonMappingException, IOException{
		if(StringUtils.isEmpty(paramsJsonStr))
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		Object jsonResult = (Object) resultMapper.readValue(paramsJsonStr,destClazz.getClass());
		return jsonResult;
	}
	
	/**
	 * 对象转为json串
	 * @param params
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @author linzhanbo .
	 * @since 2016年8月10日, 下午6:30:57 .
	 * @version 1.0 .
	 */
	public static String toJson(Object params) throws JsonGenerationException, JsonMappingException, IOException{
		if(params == null)
			return null;
		ObjectMapper resultMapper = new ObjectMapper();
		String jsonResult =  resultMapper.writeValueAsString(params);
		return jsonResult;
	}
}
