/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.common.util.jacksonext;

import java.lang.reflect.Method;

/**
 * 过滤属性处理器
 * 
 * @author linzhanbo .
 * @since 2016年7月7日, 下午5:34:38 .
 * @version 1.0 .
 */
public interface FilterPropertyHandler {
	/**
	 * 传入调用方法和返回值过滤属性
	 * 
	 * @param method
	 * @param object
	 * @return
	 * @author linzhanbo .
	 * @since 2016年7月7日, 下午5:34:00 .
	 * @version 1.0 .
	 */
	public Object filterProperties(Method method, Object object);
}
