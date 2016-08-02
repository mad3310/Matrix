package com.letv.portal.proxy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.letv.portal.enumeration.*;
import com.letv.portal.model.swift.SwiftServer;
import com.letv.portal.service.cbase.ICbaseClusterService;
import com.letv.portal.service.elasticcalc.gce.IEcGceService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.slb.ISlbClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.model.monitor.BaseMonitor;
import com.letv.portal.proxy.IContainerProxy;
import com.letv.portal.proxy.IDashBoardProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.letv.portal.service.IHclusterService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMclusterService;
import com.letv.portal.service.IMonitorService;
import com.letv.portal.service.cbase.ICbaseBucketService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.slb.ISlbServerService;
import com.letv.portal.service.swift.ISwiftServerService;

@Component
public class DashBoardProxyImpl implements IDashBoardProxy {

	private final static Logger logger = LoggerFactory
			.getLogger(DashBoardProxyImpl.class);

	@Autowired
	private IMclusterService mclusterService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IDbUserService dbUserService;
	@Autowired
	private IHclusterService hclusterService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IBuildTaskService buildTaskService;
	@Autowired
	private IMonitorService monitorService;
	@Autowired
	private ISlbServerService slbServerService;
	@Autowired
	private IGceServerService gceServerService;
	@Autowired
	private IEcGceService gceService;
	@Autowired
	private ICbaseBucketService cbaseBucketService;
	@Autowired
	private ISwiftServerService swiftServerService;
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Autowired(required = false)
	private SessionServiceImpl sessionService;

	@Value("${db.auto.build.count}")
	private int DB_AUTO_BUILD_COUNT;

    @Autowired
    private IGceClusterService gceClusterService;
    @Autowired
    private ISlbClusterService slbClusterService;
    @Autowired
    private ICbaseClusterService cbaseClusterService;

    @Override
	public Map<String, Integer> selectManagerResource() {
		Map<String, Integer> statistics = new HashMap<String, Integer>();
		statistics.put("db_hclusterSum", this.hclusterService.selectCountByType("RDS"));
        statistics.put("gce_hclusterSum", this.hclusterService.selectCountByType("GCE"));
        statistics.put("slb_hclusterSum", this.hclusterService.selectCountByType("SLB"));
        statistics.put("ocs_hclusterSum", this.hclusterService.selectCountByType("CBASE"));
        statistics.put("oss_hclusterSum", this.hclusterService.selectCountByType("OSS"));

        statistics.put("db_hostSum", this.hostService.selectCountByHclusterType("RDS"));
        statistics.put("gce_hostSum", this.hostService.selectCountByHclusterType("GCE"));
        statistics.put("slb_hostSum", this.hostService.selectCountByHclusterType("SLB"));
        statistics.put("ocs_hostSum", this.hostService.selectCountByHclusterType("CBASE"));
        statistics.put("oss_hostSum", this.hostService.selectCountByHclusterType("OSS"));

        statistics.put("db_clusterSum", this.mclusterService.selectValidMclusterCount());
        statistics.put("gce_clusterSum", this.gceClusterService.selectValidClusterCount());
        statistics.put("slb_clusterSum", this.slbClusterService.selectValidClusterCount());
        statistics.put("ocs_clusterSum", this.cbaseClusterService.selectValidClusterCount());

        statistics.put("db_dbSum", this.dbService.selectCountByStatus(DbStatus.NORMAL.getValue()));
        statistics.put("db_unauditeDbSum", this.dbService.selectCountByStatus(DbStatus.DEFAULT.getValue()));

        statistics.put("gce_gceSum", this.gceServerService.selectCountByStatus(GceStatus.NORMAL.getValue()));
        statistics.put("gce_unauditeGceSum", this.gceServerService.selectCountByStatus(GceStatus.DEFAULT.getValue()));

        statistics.put("slb_slbSum", this.slbServerService.selectCountByStatus(SlbStatus.NORMAL.getValue()));
        statistics.put("slb_unauditeSlbSum", this.slbServerService.selectCountByStatus(SlbStatus.DEFAULT.getValue()));

        statistics.put("ocs_ocsSum", this.cbaseBucketService.selectCountByStatus(CbaseBucketStatus.NORMAL.getValue()));
        statistics.put("ocs_unauditeOcsSum", this.cbaseBucketService.selectCountByStatus(CbaseBucketStatus.DEFAULT.getValue()));

        statistics.put("oss_clusterSum", this.swiftServerService.selectCountByStatus(DbStatus.NORMAL.getValue()));
        statistics.put("oss_ossSum", this.swiftServerService.selectCountByStatus(DbStatus.NORMAL.getValue()));
		statistics.put("oss_unauditeOssSum", this.swiftServerService.selectCountByStatus(DbStatus.DEFAULT.getValue()));

		return statistics;
	}
	
	private class MonitorStatusThread extends Thread {
		private Long monitorType;
		private Long mclusterId;
		AtomicInteger nothing;
		AtomicInteger general;
		AtomicInteger serious;
		AtomicInteger crash;
		AtomicInteger timeout;
		AtomicInteger except;
		CountDownLatch latch;
		
		public MonitorStatusThread(long mclusterId, long monitorType, AtomicInteger nothing, AtomicInteger general, AtomicInteger serious, 
				AtomicInteger crash, AtomicInteger timeout, AtomicInteger except, CountDownLatch latch) {
			this.mclusterId = mclusterId;
			this.monitorType = monitorType;
			this.nothing = nothing;
			this.general = general;
			this.serious = serious;
			this.crash = crash;
			this.timeout = timeout;
			this.except = except;
			this.latch = latch;
		}
		
		@Override
		public void run() {
			try {
				ContainerModel container = selectValidVipContianer(mclusterId, "mclustervip");
				if (container == null) {
					except.incrementAndGet();
				} else {
					BaseMonitor monitor = buildTaskService.getMonitorData(
							container.getIpAddr(), monitorType);
					if (MonitorStatus.NORMAL.getValue() == monitor.getResult()) {
						nothing.incrementAndGet();
					}
					if (MonitorStatus.GENERAL.getValue() == monitor.getResult()) {
						general.incrementAndGet();
					}
					if (MonitorStatus.SERIOUS.getValue() == monitor.getResult()) {
						serious.incrementAndGet();
					}
					if (MonitorStatus.CRASH.getValue() == monitor.getResult()) {
						crash.incrementAndGet();
					}
					if (MonitorStatus.TIMEOUT.getValue() == monitor.getResult()) {
						timeout.incrementAndGet();
					}
					if (MonitorStatus.EXCEPTION.getValue() == monitor.getResult()) {
						except.incrementAndGet();
					}
				}
			} catch (Exception e) {
				except.incrementAndGet();
			} finally {
				latch.countDown();
			}
		}
		
	}
	
	public Map<String, Integer> selectMonitorAlertWithMultiThread(Long monitorType) {
		List<MclusterModel> mclusters = this.mclusterService
				.selectValidMclusters();
		AtomicInteger nothing = new AtomicInteger();
		AtomicInteger general = new AtomicInteger();
		AtomicInteger serious = new AtomicInteger();
		AtomicInteger crash = new AtomicInteger();
		AtomicInteger timeout = new AtomicInteger();
		AtomicInteger except = new AtomicInteger();
		CountDownLatch latch = new CountDownLatch(mclusters==null?0:mclusters.size());

		for (MclusterModel mcluster : mclusters) {
			MonitorStatusThread status = new MonitorStatusThread(mcluster.getId(), monitorType, nothing, general, serious, crash, timeout, 
					except, latch);
			this.threadPoolTaskExecutor.execute(status);
		}
		try {
			latch.await(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.error("CountDownLatch.wait had error : ", e);
		}
		Map<String, Integer> data = new HashMap<String, Integer>();
		data.put("nothing", nothing.get());
		data.put("general", general.get());
		data.put("serious", serious.get());
		data.put("crash", crash.get());
		data.put("timeout", timeout.get());
		data.put("except", except.get());
		return data;
	}

	@Override
	public Map<String, Integer> selectMonitorAlert(Long monitorType) {
		List<MclusterModel> mclusters = this.mclusterService
				.selectValidMclusters();

		int nothing = 0;
		int general = 0;
		int serious = 0;
		int crash = 0;
		int timeout = 0;
		int except = 0;
		for (MclusterModel mcluster : mclusters) {
			ContainerModel container = this.selectValidVipContianer(
					mcluster.getId(), "mclustervip");
			if (container == null) {
				except++;
				continue;
			}
			BaseMonitor monitor = this.buildTaskService.getMonitorData(
					container.getIpAddr(), monitorType);
			if (MonitorStatus.NORMAL.getValue() == monitor.getResult()) {
				nothing++;
			}
			if (MonitorStatus.GENERAL.getValue() == monitor.getResult()) {
				general++;
			}
			if (MonitorStatus.SERIOUS.getValue() == monitor.getResult()) {
				serious++;
			}
			if (MonitorStatus.CRASH.getValue() == monitor.getResult()) {
				crash++;
			}
			if (MonitorStatus.TIMEOUT.getValue() == monitor.getResult()) {
				timeout++;
			}
			if (MonitorStatus.EXCEPTION.getValue() == monitor.getResult()) {
				except++;
			}
		}
		Map<String, Integer> data = new HashMap<String, Integer>();
		/*
		 * nothing tel:sms:email sms:email
		 */
		data.put("nothing", nothing);
		data.put("general", general);
		data.put("serious", serious);
		data.put("crash", crash);
		data.put("timeout", timeout);
		data.put("except", except);
		return data;
	}

	private ContainerModel selectValidVipContianer(Long mclusterId, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusterId);
		map.put("type", type);
		List<ContainerModel> containers = this.containerService
				.selectAllByMap(map);
		if (containers.isEmpty()) {
			return null;
		}
		return containers.get(0);
	}

	@Override
	public Map<String, Integer> selectAppResource() {
		Map<String, Integer> statistics = new HashMap<String, Integer>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("createUser", sessionService.getSession().getUserId());

		Integer db = this.dbService.selectByMapCount(map);
		Integer dbFree = DB_AUTO_BUILD_COUNT - db;
		statistics.put("db", db);
		statistics.put("dbFree", dbFree > 0 ? dbFree : 0);
		statistics.put("dbUser", this.dbUserService.selectByMapCount(map));
		statistics.put("slb", this.slbServerService.selectByMapCount(map));
		statistics.put("gce", this.gceService.selectByMapCount(map));
		statistics.put("cache", this.cbaseBucketService.selectByMapCount(map));
		statistics.put("oss", this.swiftServerService.selectByMapCount(map));
		return statistics;
	}

	@Override
	public Map<String, Float> selectDbStorage() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("createUser", sessionService.getSession().getUserId());
		map.put("status", DbStatus.NORMAL.getValue());
		List<DbModel> dbs = this.dbService.selectByMap(map);
		Map<String, Float> storage = new HashMap<String, Float>();
		for (DbModel db : dbs) {
			// 依次查询使用量。
			storage.put(db.getDbName(),
					this.monitorService.selectDbStorage(db.getMclusterId()));
		}
		return storage;
	}

	@Override
	public List<Map<String, Object>> selectDbConnect() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("createUser", sessionService.getSession().getUserId());
		map.put("status", DbStatus.NORMAL.getValue());
		List<DbModel> dbs = this.dbService.selectByMap(map);
		List<Map<String, Object>> connect = new ArrayList<Map<String, Object>>();
		for (DbModel db : dbs) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("dbName", db.getDbName());
			List<Map<String, Object>> selectDbConnect = this.monitorService
					.selectDbConnect(db.getMclusterId());
			float total = 0F;
			for (Map<String, Object> value : selectDbConnect) {
				total += (Float) value.get("detailValue");
			}
			data.put("value", selectDbConnect);
			data.put("total", total);
			connect.add(data);
		}
		return connect;
	}

}
