package com.letv.portal.service.es;

import java.io.IOException;
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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.util.DataFormat;
import com.letv.common.util.ESUtil;
import com.letv.portal.constant.Constant;
import com.letv.portal.junitBase.AbstractTest;
 
public class DeleteESTest extends AbstractTest{

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private static final String dbName = "WEBPORTAL_MONITOR_DB_ROWOPERS_PS";
	private static final String types = "num_updates_sec,num_deletes_sec,num_reads_sec,num_inserts_sec";
	
	private final static Logger logger = LoggerFactory.getLogger(
			DeleteESTest.class);
	
	@Test
	//@Ignore
	public void testDeleteEs2() {
		for(int j=0; j<100; j++) {
			Calendar cal = Calendar.getInstance();
			cal.set(2016, 4, 1);
        	cal.add(Calendar.DATE, j);
        	System.out.println(DataFormat.compactDate(cal.getTime()));
        	DeleteIndexResponse indexRes = ESUtil.getClient().admin().indices().prepareDelete(Constant.ES_RDS_MONITOR_INDEX + dbName.toLowerCase() + "_" + DataFormat.compactDate(cal.getTime())).execute().actionGet();
			System.out.println(indexRes.isAcknowledged());
		}
		//DeleteIndexResponse indexRes = ESUtil.getClient().admin().indices().prepareDelete("matrix_rds_monitor_webportal_monitor_db_rowopers_ps_20160514").execute().actionGet();
		//DeleteResponse res = ESUtil.getClient().delete(new DeleteRequest().index("matrix_rds_monitor_webportal_monitor_db_rowopers_ps_20160609").type("num_inserts_sec").id("AVSUzKUCUY2lqU33INMG")).actionGet();
		//System.out.println(res.isFound());
		//System.out.println(indexRes.isAcknowledged());
	}
	
	@Test
	@Ignore
	public void testDeleteEs() {
		DeleteByQueryResponse res = ESUtil.getClient().prepareDeleteByQuery().setQuery(QueryBuilders.termsQuery("index", "matrix_rds_monitor_webportal_monitor_db_rowopers_ps_20160609")).execute().actionGet();
    	System.out.println(res.status());
		SearchResponse sr = ESUtil.getClient().prepareSearch("matrix_rds_monitor_webportal_monitor_db_rowopers_ps_20160609").execute().actionGet();
		SearchHits hits = sr.getHits();
		for (SearchHit hit : hits.getHits()) {
			Map<String, Object> params = hit.getSource();
			System.out.println(params.toString());
		}
		
//		for(int j=0; j<25; j++) {//模拟30天数据
//    		final Calendar cal = Calendar.getInstance();
//        	cal.add(Calendar.DATE, j+5);
//        	ESUtil.getClient().prepareDeleteByQuery().setQuery(QueryBuilders.termsQuery("index", "")).execute().actionGet();
//        	logger.info("delete-add-{}", Constant.ES_RDS_MONITOR_INDEX + dbName.toLowerCase() + "_" + DataFormat.compactDate(cal.getTime()));
//		}
		
	}
	
    
}
