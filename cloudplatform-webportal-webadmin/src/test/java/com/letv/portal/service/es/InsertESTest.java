package com.letv.portal.service.es;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.util.DataFormat;
import com.letv.common.util.ESUtil;
import com.letv.portal.constant.Constant;
import com.letv.portal.junitBase.AbstractTest;

public class InsertESTest extends AbstractTest {

	private static ExecutorService executorService = Executors
			.newCachedThreadPool();

	private static final String dbName = "WEBPORTAL_MONITOR_DB_ROWOPERS_PS";
	private static final String types = "num_updates_sec,num_deletes_sec,num_reads_sec,num_inserts_sec";

	private final static Logger logger = LoggerFactory
			.getLogger(InsertESTest.class);
	
	public static void main(String[] args) {
		InsertESTest test = new InsertESTest();
		test.testInsertEsWithCountDownMultiThread();
	}

	@Test
	@Ignore
	public void testInsertEsWithCountDownMultiThread() {
		int size = 10;
		int bulkSize = 100;
		int needData = 100000;
		List<FutureTask<Integer>> list = new ArrayList<FutureTask<Integer>>();
		// 协调线程之间
		CountDownLatch countDownLatch = new CountDownLatch(size);

		for (int i = 1; i <= size; i++) {
			System.out.println("---------run index is : " + i);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, i);

			String[] dots = types.split(",");
			FutureTask<Integer> futureTask = new FutureTask<Integer>(new RunnerCallable(
					i, countDownLatch, dots, cal, bulkSize, needData));
			executorService.execute(futureTask);
			list.add(futureTask);
		}

		try {
			countDownLatch.await();
			int total = 0;
			for (FutureTask<Integer> ft : list) {
				total += ft.get();
			}
			logger.info("total message : {}", total);
			logger.info("{}/s", total/10);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			executorService.awaitTermination(3, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	private static Date randomDate(String beginDate,String  endDate ){  
		try {  
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
			Date start = format.parse(beginDate);//构造开始日期  
			Date end = format.parse(endDate);//构造结束日期  

			//getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。  
			if(start.getTime() >= end.getTime()){  
				return null;  
			}  
			long date = random(start.getTime(),end.getTime());  
			return new Date(date);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return null;  
	}  

	private static long random(long begin,long end){  
		long rtn = begin + (long)(Math.random() * (end - begin));  
		//如果返回的是开始时间和结束时间，则递归调用本函数查找随机值  
		if(rtn == begin || rtn == end){  
			return random(begin,end);  
		}  
		return rtn;  
	}

	class RunnerCallable implements Callable<Integer> {
		private int sumto = 0;
		private CountDownLatch countDownLatch;
		private String[] dots;
		private Calendar cal;
		private AtomicInteger sum = new AtomicInteger();
		private final int bulkSize;
		private final int needData;
		
		Random r = new Random();

		public Integer call() throws Exception {
			long start = System.currentTimeMillis();
			while(true) {
				//for (int i = 0; i < dots.length; i++) {
					//final String dot = dots[i];
					BulkRequestBuilder bulkRequestBuilder = ESUtil.getClient()
							.prepareBulk();
					for (int z = 0; z < bulkSize; z++) {
						try {
							bulkRequestBuilder
									.add(ESUtil
											.getClient()
											.prepareIndex(
													(Constant.ES_RDS_MONITOR_INDEX
															+ dbName.toLowerCase()
															+ "_" + DataFormat
															.compactDate(cal
																	.getTime()))
															.toLowerCase(), "type")
//													"一百万多", "type")
											.setSource(
													XContentFactory
															.jsonBuilder()
															.startObject()
															.field(dots[0],r.nextInt(100))
															.field(dots[1],r.nextInt(100))
															.field(dots[2],r.nextInt(100))
															.field(dots[3],r.nextInt(100))
															.field("ip", "10.154.238."+r.nextInt(255))
															.field("monitorDate", randomDate("2016-05-12","2016-05-13"))
															.endObject()));
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
					BulkResponse bulkResponse = null;
					try {
						bulkResponse = bulkRequestBuilder.execute().actionGet();
					} catch (ElasticsearchException e) {
						logger.error(e.getMessage());
					}
					if (bulkResponse.hasFailures()) {
						logger.error(bulkResponse.buildFailureMessage());
					}
				//}
				sum.addAndGet(bulkSize);
				if(System.currentTimeMillis()-start>=10000) {
					break;
				}
//				if(sum.get()>=needData) {
//					break;
//				}
			}
			System.out.println(Thread.currentThread().getId()
					+ "_index is : " + sumto);
			// 减一
			countDownLatch.countDown();
			return sum.get();
		}

		public RunnerCallable(int sumto, CountDownLatch countDownLatch, String[] dots, Calendar cal, int bulkSize, int needData) {
			super();
			this.sumto = sumto;
			this.countDownLatch = countDownLatch;
			this.dots = dots;
			this.cal = cal;
			this.bulkSize = bulkSize;
			this.needData = needData;
		}
	}

	class Worker implements Runnable {
		private CyclicBarrier barrier;
		private String[] dots;
		private Calendar cal;
		private int id;

		public Worker(int id, Calendar cal, String[] dots, CyclicBarrier barrier) {
			this.barrier = barrier;
			this.dots = dots;
			this.cal = cal;
			this.id = id;
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < dots.length; i++) {
					final String dot = dots[i];
					BulkRequestBuilder bulkRequestBuilder = ESUtil.getClient()
							.prepareBulk();
					for (int z = 0; z < 10000; z++) {
						try {
							bulkRequestBuilder
									.add(ESUtil
											.getClient()
											.prepareIndex(
													(Constant.ES_RDS_MONITOR_INDEX
															+ dbName.toLowerCase()
															+ "_" + DataFormat
															.compactDate(cal
																	.getTime()))
															.toLowerCase(), dot)
											.setSource(
													XContentFactory
															.jsonBuilder()
															.startObject()
															.field("detailName",
																	dot)
															.field("detailValue",
																	"10")
															.field("ip",
																	"10.154.238.188")
															.field("monitorDate",
																	new Date())
															.endObject()));
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
					BulkResponse bulkResponse = null;
					try {
						bulkResponse = bulkRequestBuilder.execute().actionGet();
					} catch (ElasticsearchException e) {
						logger.error(e.getMessage());
					}
					if (bulkResponse.hasFailures()) {
						logger.error(bulkResponse.buildFailureMessage());
					}
				}
				logger.info("CyclicBarrier id :{}", id);
				barrier.await();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	@Ignore
	public void testInsertEsWithCyclicBarrierMultiThread() {
		final long start = System.currentTimeMillis();
		logger.info("start:{}", start);

		final int threadNum = 3;

		CyclicBarrier barrier = new CyclicBarrier(10, new Runnable() {

			@Override
			public void run() {
				long end = System.currentTimeMillis();
				logger.info("end : {}", end);
				logger.info("total time(s) : {}", (end - start) / 1000);
				logger.info("{}/s", threadNum * 20000 * 4
						/ ((end - start) / 1000));
			}
		});

		for (int j = 0; j < threadNum; j++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, j);

			String[] dots = types.split(",");

			Worker w = new Worker(j, cal, dots, barrier);
			System.out.println(j);

			executorService.execute(w);

		}
		try {
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	@Ignore
	public void testInsertEsWithOneThread() {
		long start = System.currentTimeMillis();
		logger.info("start:{}", start);
		for (int j = 0; j < 3; j++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, j);

			String[] dots = types.split(",");

			for (String dot : dots) {
				BulkRequestBuilder bulkRequestBuilder = ESUtil.getClient()
						.prepareBulk();
				for (int i = 0; i < 20000; i++) {
					try {
						bulkRequestBuilder
								.add(ESUtil
										.getClient()
										.prepareIndex(
												(Constant.ES_RDS_MONITOR_INDEX
														+ dbName.toLowerCase()
														+ "_" + DataFormat
														.compactDate(cal
																.getTime()))
														.toLowerCase(), dot)
										.setSource(
												XContentFactory
														.jsonBuilder()
														.startObject()
														.field("detailName",
																dot)
														.field("detailValue",
																"10")
														.field("ip",
																"10.154.238.178")
														.field("monitorDate",
																new Date())
														.endObject()));
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}

				BulkResponse bulkResponse = null;
				try {
					bulkResponse = bulkRequestBuilder.execute().actionGet();
					logger.info("写入数据{}", DataFormat.compactDate(cal.getTime()));
				} catch (ElasticsearchException e) {
					logger.error(e.getMessage());
				}
				if (bulkResponse.hasFailures()) {
					logger.error(bulkResponse.buildFailureMessage());
				}
			}

		}
		long end = System.currentTimeMillis();
		logger.info("end : {}", end);
		logger.info("total time(s) : {}", (end - start) / 1000);
		logger.info("{}/s", 3 * 4 * 20000 / ((end - start) / 1000));
	}

}
