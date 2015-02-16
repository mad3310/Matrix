package com.letv.portal.proxy.impl;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MonitorIndexModel;
import com.letv.portal.model.monitor.MonitorViewYModel;
import com.letv.portal.proxy.IMonitorProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IMonitorIndexService;
import com.letv.portal.service.IMonitorService;

@Component("monitorProxy")
public class MonitorProxyImpl implements IMonitorProxy{
	private final static Logger logger = LoggerFactory.getLogger(MonitorProxyImpl.class);
	@Autowired
	private IMonitorService monitorService;
	
	@Autowired
	private IBuildTaskService buildTaskService;
	
	@Autowired
	private IContainerService containerService;
	
	@Autowired
	private IMonitorIndexService monitorIndexService;
	
	@Override
	public void collectMclusterServiceData() {
		Map<String,String> map = new  HashMap<String,String>();
		map.put("type", "mclusternode");
		List<ContainerModel> contianers = this.containerService.selectByMap(map);
		
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 1);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		Date date = new Date();
		logger.info("collectMclusterServiceData start" + date);
		for (MonitorIndexModel index : indexs) {
			for (ContainerModel container : contianers) {
				this.buildTaskService.getContainerServiceData(container, index,date);
			}
		}
		logger.info("collectMclusterServiceData end");
	}

	@Override
	public List<MonitorViewYModel> getMonitorViewData(Long mclusterId,Long chartId, Integer strategy) {
		return this.monitorService.getMonitorViewData(mclusterId, chartId, strategy);
	}
	
	@Override
	public List<MonitorViewYModel> getDbConnMonitor(Long mclusterId,Long chartId, Integer strategy) {
		Date start = new Date();
		logger.debug("get data start" + start);
		logger.debug("get data prepare" + start);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusterId);
		map.put("type", "mclusternode");
		List<ContainerModel> containers = this.containerService.selectByMap(map);	
		Date prepare = new Date();
		logger.debug("get data prepare" + (prepare.getTime()-start.getTime())/1000);
		List<MonitorViewYModel> data = this.monitorService.getDbConnMonitor(containers.get(0).getIpAddr(), chartId, strategy);
		Date end = new Date();
		
		logger.debug("get data end" + (end.getTime()-prepare.getTime())/1000);
		return data;
	}

	@Override
//	@Async
	public void deleteOutData() {
		Map<String,Object> indexParams = new  HashMap<String,Object>();
		indexParams.put("status", 1);
		List<MonitorIndexModel> indexs = this.monitorIndexService.selectByMap(indexParams);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);    //得到前一个月
		long date = cal.getTimeInMillis();
		Date monthAgo = new Date(date);
		
		Map<String, Object> map = new HashMap<String, Object>();
		for (MonitorIndexModel monitorIndexModel : indexs) {
			
			//get max id and min id from table where monitor_date<monthAgo
			//for in  min and max, delete every 5000 by id.
			
			map.put("dbName", monitorIndexModel.getDetailTable());
			map.put("monitorDate", monthAgo);
			List<Map<String,Object>> ids = this.monitorService.selectExtremeIdByMonitorDate(map);
			if(ids.isEmpty() || ids.get(0) == null || ids.get(0).isEmpty()) {
				continue;
			}
			Map<String, Object> extremeIds = ids.get(0);
			Long max = ((BigInteger)extremeIds.get("maxId")).longValue();
			Long min = ((BigInteger)extremeIds.get("minId")).longValue();
			if(max == null || max == 0 || max == min)
				return;
			for (Long i = min; i <= max; i+=5000) {
				map.put("id", i);
				this.monitorService.deleteOutDataByIndex(map); 
			}
		}
	}

}
