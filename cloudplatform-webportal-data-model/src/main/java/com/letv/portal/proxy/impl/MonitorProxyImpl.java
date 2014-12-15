package com.letv.portal.proxy.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MonitorIndexModel;
import com.letv.portal.model.MonitorViewYModel;
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
		Date data = new Date();
		logger.info("collectMclusterServiceData start");
		for (MonitorIndexModel index : indexs) {
			for (ContainerModel container : contianers) {
				this.buildTaskService.getContainerServiceData(container, index,data);
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

}
