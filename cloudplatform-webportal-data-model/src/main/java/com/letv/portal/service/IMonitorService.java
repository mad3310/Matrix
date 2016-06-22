package com.letv.portal.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.MonitorDetailModel;
import com.letv.portal.model.MonitorIndexModel;
import com.letv.portal.model.monitor.MonitorErrorModel;
import com.letv.portal.model.monitor.MonitorViewModel;
import com.letv.portal.model.monitor.MonitorViewYModel;

public interface IMonitorService extends IBaseService<MonitorDetailModel>{
	
	List<MonitorViewYModel> getMonitorViewData(Long MclusterId,Long chartId,Integer strategy);
	List<MonitorViewYModel> getHostDiskMonitorData(Long MclusterId,Long chartId,Integer strategy);
	List<MonitorViewYModel>  getMonitorTopNViewData(Long hclusterId, Long chartId,String monitorName, Integer strategy,Integer topN);
	void syncMonitorFromDbToEs(String dbName,int strategy);
    void syncMonitorFromDbToEs(String dbName,Date start,Date end);

	List<MonitorViewYModel> getMonitorData(String ip,Long chartId,Integer strategy,boolean isTimeAveraging,int format);

	Float selectDbStorage(Long mclusterId);

	List<Map<String,Object>> selectDbConnect(Long mclusterId);
	void deleteOutDataByIndex(Map<String, Object> map);

	List<Map<String, Object>> selectExtremeIdByMonitorDate(
			Map<String, Object> map);
	/**
	  * @Title: addMonitorPartition
	  * @Description: 添加分区
	  * @param map void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月17日 上午9:29:14
	  */
	void addMonitorPartition(Map<String, Object> map, Date d);

	/**
	  * @Title: deleteMonitorPartitionThirtyDaysAgo
	  * @Description: 删除30天以前的分区
	  * @param map void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月17日 上午10:42:28
	  */
	void deleteMonitorPartitionThirtyDaysAgo(Map<String, Object> map);
	/**
	  * @Title: insertMysqlMonitorData
	  * @Description: 保存数据到mysql监控表(当该次收集数据为空时，把相应字段置为-1，表示数据错误)
	  * @param map void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月28日 下午2:04:23
	  */
	void insertMysqlMonitorData(ContainerModel container, Map<String, Object> map, Date d);

	/**
	  * @Title: insertMysqlMonitorSpaceData
	  * @Description: 保存数据到mysql 表空间监控表
	  * @param container
	  * @param map
	  * @param d void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月31日 上午10:08:02
	  */
	void insertMysqlMonitorSpaceData(String dbName, ContainerModel container, Map<String, Object> map, Date d);

	/**
	  * @Title: getLatestDataFromMonitorTable
	  * @Description: 获取监控表数据库中最新记录值
	  * @param containerIp container的ip
	  * @param titles WEBPORTAL_INDEX_MONITOR表的title值
	  * @param d
	  * @return Map<String,Object>   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月30日 下午2:04:21
	  */
	Map<String, Object> getLatestDataFromMonitorTables(String containerIp, String[] titles, Date d);

	/**
	  * @Title: saveMonitorErrorInfo
	  * @Description: 保存监控错误数据
	  * @param error void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月10日 下午3:49:01
	  */
	void saveMonitorErrorInfo(MonitorErrorModel error);
	List<Map<String, Object>> getMonitorErrorModelsByMap(Map<String, Object> map);

	void updateTopN(MonitorDetailModel monitorDetail,Long hclusterId);
	List<MonitorDetailModel> getTopN( MonitorIndexModel monitorIndex,Long hclusterId,String monitorName, Integer strategy, Integer topN);

	/**
	  * @Title: deleteMonitorErrorDataByMap
	  * @Description: 删除几天前监控错误数据
	  * @param map void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月10日 下午3:48:33
	  */
}
