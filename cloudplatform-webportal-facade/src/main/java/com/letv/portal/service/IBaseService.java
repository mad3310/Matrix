package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.letv.common.paging.impl.Page;

@Path("hclster")
//标注可接受请求的MIME媒体类型
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
//标注返回的MIME媒体类型
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
public interface IBaseService<T> {
	
	/**
	 * 创建对象
	 * @return
	 */
//	T create();
	
	/**
	 * 修改记录
	 * @return
	 * @throws Exception
	 */
	 void update(T t);

	/**
	 * 插入记录
	 * @return
	 * @throws Exception
	 */
	void insert(T t);

	/**
	 * 修改记录，至修改不为空的字段
	 * @param t
	 * @throws Exception
	 */
	void updateBySelective(T t) ;
	
	/**
	 * 根据id删除记录
	 * @param ids
	 * @throws Exception
	 */
	void delete(T t);
	/**
	 * 根据id查找记录记录，返回单条记录
	 * @param Id
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("{id : \\d+}")
	T selectById(Long id);
	
	/**
	 * 根据model查询总数
	 * @param Id
	 * @return
	 * @throws Exception
	 */
	Integer selectByModelCount(T t);
	
	/**
	 * 根据map查询总数
	 * @param Id
	 * @return
	 * @throws Exception
	 */
	<K,V> Integer selectByMapCount(Map<K,V>  map);
	
	/**
	 * 根据map查询list记录，分页,默认10条
	 * @param Id
	 * @return
	 * @throws Exception
	 */
	List<T> selectByModel(T t);
	
	/**
	 * 根据map查询list记录，不分页。
	 * @param Id
	 * @return
	 * @throws Exception
	 */
	<K,V> List<T> selectByMap(Map<K,V>  map);
	
	public boolean hasSoftDelete();

	/**Methods Name: selectPageByParams <br>
	 * Description: <br>
	 * @author name: liuhao1
	 * @param page
	 * @param params
	 * @param orderBy
	 * @param isAsc
	 * @return
	 */
	<K, V> Page selectPageByParams(Page page, Map<K,V> params,String orderBy,Boolean isAsc);
	<K, V> Page selectPageByParams(Page page, Map<K,V> params);
	
	/**
	  * @Title: queryByPagination
	  * @Description: 根据参数查询数据（带分页）
	  * @param page
	  * @param params
	  * @return Page   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月27日 上午11:38:37
	  */
	<K, V> Page queryByPagination(Page page, Map<K,V> params);
	
}
