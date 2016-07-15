package com.letv.portal.service.es;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.util.DataFormat;
import com.letv.common.util.ESUtil;
import com.letv.portal.constant.Constant;
import com.letv.portal.junitBase.AbstractTest;
 
public class QueryESTest extends AbstractTest{

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private static final String dbName = "WEBPORTAL_MONITOR_DB_ROWOPERS_PS";
	private static final String types = "num_updates_sec,num_deletes_sec,num_reads_sec,num_inserts_sec";
	
	private final static Logger logger = LoggerFactory.getLogger(
			QueryESTest.class);
	
	@Test
	@Ignore
	public void testQueryEsWithOneMillion() {
		String[] indexs = new String[]{"一百万单"};
		/*********1小时测试************/
		long t1 = System.currentTimeMillis();
		Date now = new Date();
		long start = now.getTime() - 3600*1000;
		SearchResponse response = ESUtil.getClient().prepareSearch(indexs)
                .setPostFilter(FilterBuilders.andFilter(
                		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113"),
                		FilterBuilders.rangeFilter("monitorDate").from(new Date(start)).to(now)))
                //.setQuery(QueryBuilders.rangeQuery("monitorDate").from(new Date(start)).to(now))
                .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
		SearchHits hits = response.getHits();
		logger.info("1 hour query total time:{}", System.currentTimeMillis()-t1);
		logger.info("1 hour totalHits:{}", hits.totalHits());
		
		/*********3小时测试************/
		t1 = System.currentTimeMillis();
		now = new Date();
		start = now.getTime() - 3600*1000*3;
		response = ESUtil.getClient().prepareSearch(indexs)
                .setPostFilter(FilterBuilders.andFilter(
                		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113"),
                		FilterBuilders.rangeFilter("monitorDate").from(new Date(start)).to(now)))
                .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
		hits = response.getHits();
		logger.info("3 hours query total time:{}", System.currentTimeMillis()-t1);
		logger.info("3 hours totalHits:{}", hits.totalHits());
		
		/*********12小时测试************/
		t1 = System.currentTimeMillis();
		now = new Date();
		start = now.getTime() - 3600*1000*12;
		response = ESUtil.getClient().prepareSearch(indexs)
                .setPostFilter(FilterBuilders.andFilter(
                		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113"),
                		FilterBuilders.rangeFilter("monitorDate").from(new Date(start)).to(now)))
                .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
		hits = response.getHits();
		logger.info("12 hours query total time:{}", System.currentTimeMillis()-t1);
		logger.info("12 hours totalHits:{}", hits.totalHits());
		
		
//		for (SearchHit hit : hits.getHits()) {
//			Map<String, Object> params = hit.getSource();
//			logger.info(params.toString());
//		}
	}
	
	@Test
	@Ignore
	public void testQueryEsWithOnePoint() {
		String[] indexs = new String[]{"一百万多"};
		String[] dots = types.split(",");
		/*********1小时测试************/
		long t1 = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 4, 12);;
		Date now = cal.getTime();
		long start = now.getTime() - 3600*1000;
		SearchResponse response = ESUtil.getClient().prepareSearch(indexs)
                .setPostFilter(FilterBuilders.andFilter(
                		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113"),
                		FilterBuilders.rangeFilter("monitorDate").from(new Date(start)).to(now)))
                .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
		SearchHits hits = response.getHits();
		logger.info("1 hour query total time:{}", System.currentTimeMillis()-t1);
		logger.info("1 hour totalHits:{}", hits.totalHits());
		
		/*********3小时测试************/
		t1 = System.currentTimeMillis();
		now = cal.getTime();
		start = now.getTime() - 3600*1000*3;
		response = ESUtil.getClient().prepareSearch(indexs)
                .setPostFilter(FilterBuilders.andFilter(
                		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113"),
                		FilterBuilders.rangeFilter("monitorDate").from(new Date(start)).to(now)))
                .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
		hits = response.getHits();
		logger.info("3 hours query total time:{}", System.currentTimeMillis()-t1);
		logger.info("3 hours totalHits:{}", hits.totalHits());
		
		/*********12小时测试************/
		t1 = System.currentTimeMillis();
		now = cal.getTime();
		start = now.getTime() - 3600*1000*12;
		response = ESUtil.getClient().prepareSearch(indexs)
                .setPostFilter(FilterBuilders.andFilter(
                		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113"),
                		FilterBuilders.rangeFilter("monitorDate").from(new Date(start)).to(now)))
                .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
		hits = response.getHits();
		logger.info("12 hours query total time:{}", System.currentTimeMillis()-t1);
		logger.info("12 hours totalHits:{}", hits.totalHits());
		
	}
	
	@Test
	//@Ignore
	public void testQueryEs1() {
		System.out.println(ESUtil.getClient().prepareCount("一百万多").setQuery(
				QueryBuilders.termsQuery("ip", "10.154.238.111","10.154.238.112","10.154.238.113")).execute().actionGet().getCount());
			
		SearchResponse sr = ESUtil.getClient().prepareSearch("一百万多").setFetchSource("num_updates_sec", null).setPostFilter(FilterBuilders.andFilter(
        		FilterBuilders.inFilter("ip", "10.154.238.111","10.154.238.112","10.154.238.113")))
        .addSort(SortBuilders.fieldSort("monitorDate").order(SortOrder.ASC))
        .setSize(100000)
        .execute().actionGet();
		
		SearchHits hits = sr.getHits();
		System.out.println(hits.getHits().length);
		for (SearchHit hit : hits.getHits()) {
			Map<String, Object> params = hit.getSource();
			//System.out.println(params.toString());
		}

	}
	
    
}
