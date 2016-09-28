/*
 * @Title: MemcachedCacheImpl.java
 * @Package com.letv.mms.cache.client.memcached
 * @Description: TODO
 * @author chenguang 
 * @date 2012-12-18 下午6:22:59
 * @version V1.0
 *
 * Modification History:  
 * Date         Author      Version     Description  
 * -------------------------------------------------------------- 
 * 2012-12-18                          
 */
package com.letv.mms.cache.impl;


import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cache.memcached.CacheCommand;
import cache.memcached.MemCachedClientHelper;
import cache.memcached.MemcacheStats;
import cache.memcached.MemcacheStatsSlab;
import cache.memcached.MemcachedException;
import cache.memcached.MemcachedResponse;
import cache.memcached.main.MemCachedClient;

import com.letv.mms.cache.ICache;
import com.letv.mms.cache.IMemcachedCache;

@SuppressWarnings("all")
public class MemcachedCacheImpl implements IMemcachedCache
{
	private static final Log Logger = LogFactory.getLog(MemcachedCacheImpl.class);
	private MemCachedClientHelper helper;
	
	private ICache<String,Object> localCache;
	
	private StatisticsTask task;
	
	private long statisticsInterval = 5 * 60;//单位秒
	
	static final String CACHE_STATUS_RESPONSE = "cacheStatusResponse";

	/**
	 * 数据队列
	 */
	private LinkedBlockingQueue<Object[]> dataQueue;	
	
	public MemcachedCacheImpl(MemCachedClientHelper helper,int statisticsInterval)
	{
		this.helper = helper;
		
		dataQueue = new LinkedBlockingQueue<Object[]>();
		localCache = new DefaultCacheImpl();
		
		if (statisticsInterval > 0)
		{
			this.statisticsInterval = statisticsInterval;
			task = new StatisticsTask();
			task.setDaemon(true);
			task.start();
		}
	}
	
	public boolean clear()
	{
		boolean result = false;
		
		return helper.getInnerCacheClient().flushAll( null );
	}

	public Map<String, Object> getMulti(String[] keys)
	{
		if (keys == null || keys.length <= 0)
			return null;
			
			
		Map<String, Object> result = getCacheClient(keys[0]).getMulti(keys);
		
		return result;
	}

	public Object[] getMultiArray(String[] keys)
	{
		if (keys == null || keys.length <= 0)
			return null;
		
		Object[] result = getCacheClient(keys[0]).getMultiArray(keys);
		
		return result;
	}

	public Object put(String key, Object value, Date expiry)
	{

		boolean result = getCacheClient(key).set(key,value,expiry);
		
		//移除本地缓存的内容
		if (result)
			localCache.remove(key);
		
		if (!result)
			throw new RuntimeException
				(new StringBuilder().append("put key :").append(key).append(" error!").toString());
		
		return value;
	}
	
	public Object put(String key, Object value, int TTL)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, TTL);
		
		put(key,value,calendar.getTime());
		
		return value;
	}

	public boolean containsKey(String key)
	{	
		boolean result = false;
		boolean isError = false;
		
		try
		{
			result = getCacheClient(key).keyExists(key);
		}
		catch(MemcachedException ex)
		{
			Logger.error(new StringBuilder(helper.getCacheName())
				.append(" cluster containsKey error"),ex);
			isError = true;
		}
		return result;
	}

	public Object get(String key)
	{
		Object result = null;
		boolean isError = false;
		
		try
		{
			result = getCacheClient(key).get(key);	
		}
		catch(MemcachedException ex)
		{
			Logger.error(new StringBuilder(helper.getCacheName())
				.append(" cluster get error"),ex);
			
			isError = true;
		}

		return result;
	}

	public Object put(String key, Object value)
	{
		boolean result = getCacheClient(key).set(key, value);
		
		//移除本地缓存的内容
		if (result)
			localCache.remove(key);
		
		if (!result)
			throw new RuntimeException
				(new StringBuilder().append("put key :").append(key).append(" error!").toString());

		return value;
	}
	
	public void storeCounter(String key, long count) {
		boolean result = getCacheClient(key).storeCounter(key, count);
		if (!result)
			throw new RuntimeException(new StringBuilder()
					.append("storeCounter key :").append(key).append(" error!")
					.toString());

	}
	
	public long getCounter(String key)
	{
		long result = -1;
		boolean isError = false;
		
		try
		{
			result = getCacheClient(key).getCounter(key);
		}
		catch(MemcachedException ex)
		{
			Logger.error(new StringBuilder(helper.getCacheName())
				.append(" cluster getCounter error"),ex);
			
			isError = true;
		}
		
		return result;
	}
	
	public long addOrDecr(String key, long decr)
	{
		long result = getCacheClient(key).addOrDecr(key,decr);
		return result;
	}

	public long addOrIncr(String key, long inc)
	{
		long result = getCacheClient(key).addOrIncr(key,inc);
		return result;
	}
	
	public long decr(String key, long decr)
	{
		long result = getCacheClient(key).decr(key,decr);
		return result;
	}

	public long incr(String key, long inc)
	{
		long result = getCacheClient(key).incr(key,inc);
		return result;
	}	

	public Object remove(String key)
	{
		Object result = getCacheClient(key).delete(key);
		return result;
	}

	@Deprecated
	public int size()
	{
		throw new UnsupportedOperationException("Memcached not support size method!");
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> values()
	{
		Set<Object> values = new HashSet<Object>();
		Map<String,Integer> dumps = new HashMap<String,Integer>();
			 
		Map slabs = helper.getInnerCacheClient().statsItems();
		
		if (slabs != null && slabs.keySet() != null)
		{
			Iterator itemsItr = slabs.keySet().iterator();
			
			while(itemsItr.hasNext())
			{
				String server = itemsItr.next().toString();
				Map itemNames = (Map) slabs.get(server);
				Iterator itemNameItr = itemNames.keySet().iterator();
				
				while(itemNameItr.hasNext())
				{
					String itemName = itemNameItr.next().toString();
			        String[] itemAtt = itemName.split(":");
			        
			        if (itemAtt[2].startsWith("number")) 
			        	dumps.put(itemAtt[1], Integer.parseInt(itemAtt[1]));
				}
			}
			
			if (!dumps.values().isEmpty())
			{
				Iterator<Integer> dumpIter = dumps.values().iterator();
				
				while(dumpIter.hasNext())
				{
					int dump = dumpIter.next();
					
					Map cacheDump = helper.getInnerCacheClient().statsCacheDump(dump,50000);
					
					Iterator entryIter = cacheDump.values().iterator();
					
					while (entryIter.hasNext()) 
		            {
		            	Map items = (Map)entryIter.next();
		            	
		            	Iterator ks = items.keySet().iterator();

		            	while(ks.hasNext())
		            	{
		            		String k = (String)ks.next();
		            		
		            		try
		            		{
		            			k = URLDecoder.decode(k,"UTF-8");
		            		}
		            		catch(Exception ex)
		            		{
		            			Logger.error(ex);
		            		}

		            		if (k != null && !k.trim().equals(""))
		            		{
		            			Object value = get(k);
		            			
		            			if (value != null)
		            				values.add(value);
		            		}
		            	}
		            }
					
				}
			}
		}
		
		return values;		
	}
	
	
	@SuppressWarnings("unchecked")
	public Set<String> keySet(boolean fast)
	{
		Set<String> keys = new HashSet<String>();
		Map<String,Integer> dumps = new HashMap<String,Integer>();
			 
		Map slabs = helper.getInnerCacheClient().statsItems();
		
		if (slabs != null && slabs.keySet() != null)
		{
			Iterator itemsItr = slabs.keySet().iterator();
			
			while(itemsItr.hasNext())
			{
				String server = itemsItr.next().toString();
				Map itemNames = (Map) slabs.get(server);
				Iterator itemNameItr = itemNames.keySet().iterator();
				
				while(itemNameItr.hasNext())
				{
					String itemName = itemNameItr.next().toString();
			        String[] itemAtt = itemName.split(":");
			        
			        if (itemAtt[2].startsWith("number")) 
			        	dumps.put(itemAtt[1], Integer.parseInt(itemAtt[1]));
				}
			}
			
			if (!dumps.values().isEmpty())
			{
				Iterator<Integer> dumpIter = dumps.values().iterator();
				
				while(dumpIter.hasNext())
				{
					int dump = dumpIter.next();
					
					Map cacheDump = helper.getInnerCacheClient().statsCacheDump(dump,0);
					
					Iterator entryIter = cacheDump.values().iterator();
					
					while (entryIter.hasNext()) 
		            {
		            	Map items = (Map)entryIter.next();
		            	
		            	Iterator ks = items.keySet().iterator();
		            	

		            	while(ks.hasNext())
		            	{
		            		String k = (String)ks.next();
		            		
		            		try
		            		{
		            			k = URLDecoder.decode(k,"UTF-8");
		            		}
		            		catch(Exception ex)
		            		{
		            			Logger.error(ex);
		            		}

		            		if (k != null && !k.trim().equals(""))
		            		{
		            			if (fast)
		            				keys.add(k);
		            			else
		            				if (containsKey(k))
		            					keys.add(k);
		            		}
		            	}
		            }
					
				}
			}
		}
		
		return keys;

	}	
	
	public MemCachedClient getCacheClient(String key)
	{
		if (helper == null)
		{
			Logger.error("MemcachedCacheImpl helper is null!");
			throw new RuntimeException("MemcachedCacheImpl helper is null!");
		}
		
		return helper.getCacheClient(key);
	}

	public MemCachedClientHelper getHelper()
	{
		return helper;
	}

	public void setHelper(MemCachedClientHelper helper)
	{
		this.helper = helper;
	}

	public Set<String> keySet()
	{
		return keySet(false);
	}

	public Object get(String key, int localTTL)
	{
		Object result = null;
		
		result = localCache.get(key);
		
		if (result == null)
		{
			result = get(key);
			
			if (result != null)
			{
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, localTTL);
				localCache.put(key, result,calendar.getTime());
			}
		}
		
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public MemcacheStats[] stats()
	{
		MemcacheStats[] result = null;
		
		Map<String,Map<String,String>> statMap = helper.getInnerCacheClient().stats();
		
		if (statMap != null && !statMap.isEmpty())
		{
			result = new MemcacheStats[statMap.size()];
			
			Iterator<String> iter = statMap.keySet().iterator();
			
			int i = 0;
			
			while(iter.hasNext())
			{
				result[i] = new MemcacheStats();
				result[i].setServerHost(iter.next());
				result[i].setStatInfo(statMap.get(result[i].getServerHost()).toString());
				i += 1;
			}
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public MemcacheStatsSlab[] statsSlabs()
	{
		MemcacheStatsSlab[] result = null;
		
		Map<String,Map<String,Object>> statMap = helper.getInnerCacheClient().statsSlabs();
		
		if (statMap != null && !statMap.isEmpty())
		{
			result = new MemcacheStatsSlab[statMap.size()];
			
			Iterator<String> iter = statMap.keySet().iterator();
			
			int i = 0;
			
			while(iter.hasNext())
			{
				result[i] = new MemcacheStatsSlab();
				result[i].setServerHost(iter.next());
				
				Map<String,Object> node = statMap.get(result[i].getServerHost());
				
				Iterator<String> nodeIter = node.keySet().iterator();
				
				while(nodeIter.hasNext())
				{
					String key = nodeIter.next();
					result[i].addSlab(key,node.get(key).toString());
				}

				i += 1;
			}
		}	
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map statsItems()
	{
		Map items = helper.getInnerCacheClient().statsItems();
		return items;
	}
	
	/**
	 * 将需要异步处理的内容放到Queue中
	 * @param command
	 */
	public void addCommandToQueue(Object[] command)
	{
		dataQueue.add(command);
	}

	public void destroy() 
	{
		try
		{
			if (localCache != null)
				localCache.destroy();
			
			if (task != null)
			{
				task.stopTask();
			}
				
		}
		catch(Exception ex)
		{
			Logger.error(ex);
		}
	}
	

	public MemcachedResponse statCacheResponse()
	{
		if (localCache.get(CACHE_STATUS_RESPONSE)== null)
		{
			MemcachedResponse response = new MemcachedResponse();
			response.setCacheName(helper.getCacheName());
			localCache.put(CACHE_STATUS_RESPONSE, response);
		}
		
		return (MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE);
	}
	
	public long getStatisticsInterval()
	{
		return statisticsInterval;
	}

	public void setStatisticsInterval(long statisticsInterval)
	{
		this.statisticsInterval = statisticsInterval;
	}

	public boolean add(String key, Object value)
	{
		boolean result = getCacheClient(key).add(key,value);
		return result;
	}

	public boolean add(String key, Object value, Date expiry)
	{
		boolean result = getCacheClient(key).add(key,value,expiry);
		return result;
	}
	
	public boolean delete(String key) {
		boolean result = getCacheClient(key).delete(key);
		return result;
	}


	public boolean replace(String key, Object value)
	{
		boolean result = getCacheClient(key).replace(key,value);
		return result;
	}

	public boolean replace(String key, Object value, Date expiry)
	{
		boolean result = getCacheClient(key).replace(key,value,expiry);
		return result;
	}

	
	/**
	 * 统计响应时间等信息的后台线程
	 * @author wenchu.cenwc
	 *
	 */
	class StatisticsTask extends Thread
	{
		private boolean flag = true;

		
		@Override
		public void run()
		{
			while(flag)
			{
				long consume = 0;
				
				try
				{
					Thread.sleep(statisticsInterval * 1000);
					
					consume = checkResponse();		
				}
				catch(InterruptedException e)
				{
					Logger.warn("StatisticsTask stoped!");
				}
				catch(Exception ex)
				{
					Logger.error("StatisticsTask execute error",ex);
					consume = -1;
				}
				
				if (localCache != null)
				{
					MemcachedResponse response = (MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE);
					
					if (response != null && response.getResponses() != null)
						response.getResponses().add(consume);
				}
			}
			
		}
		
		/**
		 * 发送请求
		 * @return
		 */
		private long checkResponse()
		{
			if (localCache.get(CACHE_STATUS_RESPONSE)== null)
			{
				MemcachedResponse response = new MemcachedResponse();
				response.setCacheName(helper.getCacheName());
				localCache.put(CACHE_STATUS_RESPONSE, response);
			}
			else if (((MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE))
						.getEndTime().before(new Date()))
			{
				((MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE)).ini();
			}
			
			long consume = System.currentTimeMillis();
			
			put(CACHE_STATUS_RESPONSE,CACHE_STATUS_RESPONSE);
			get(CACHE_STATUS_RESPONSE);
			
			consume = System.currentTimeMillis() - consume;
			
			return consume;
		}
		
		public void stopTask()
		{
			flag = false;
			interrupt();
		}


		public boolean isFlag()
		{
			return flag;
		}


		public void setFlag(boolean flag)
		{
			this.flag = flag;
		}
		
	}


	public void asynPut(String key, Object value)
	{
		Object[] commands = new Object[]{CacheCommand.ANSYPUT,key,value};
		
		addCommandToQueue(commands);
	}

	public void asynAddOrDecr(String key, long decr)
	{
		Object[] commands = new Object[]{CacheCommand.ANSYADDORDECR,key,decr};
		
		addCommandToQueue(commands);
	}

	public void asynAddOrIncr(String key, long incr)
	{
		Object[] commands = new Object[]{CacheCommand.ANSYADDORINCR,key,incr};
		
		addCommandToQueue(commands);
	}

	public void asynDecr(String key, long decr)
	{
		Object[] commands = new Object[]{CacheCommand.ANSYDECR,key,decr};
		
		addCommandToQueue(commands);
		
	}

	public void asynIncr(String key, long incr)
	{
		Object[] commands = new Object[]{CacheCommand.ANSYINCR,key,incr};
		
		addCommandToQueue(commands);
	}

	public void asynStoreCounter(String key, long count)
	{
		Object[] commands = new Object[]{CacheCommand.ANSYSTORECOUNTER,key,count};
		
		addCommandToQueue(commands);
	}

}
