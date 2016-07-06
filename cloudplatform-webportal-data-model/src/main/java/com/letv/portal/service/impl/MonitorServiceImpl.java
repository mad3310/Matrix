package com.letv.portal.service.impl;


import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.util.DataFormat;
import com.letv.common.util.ESUtil;
import com.letv.mms.cache.ICacheService;
import com.letv.mms.cache.factory.CacheFactory;
import com.letv.portal.constant.Constant;
import com.letv.portal.dao.IMonitorDao;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MonitorDetailModel;
import com.letv.portal.model.MonitorIndexModel;
import com.letv.portal.model.monitor.MonitorErrorModel;
import com.letv.portal.model.monitor.MonitorViewYModel;
import com.letv.portal.model.monitor.mysql.MysqlDbSpaceMonitor;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.IMonitorIndexService;
import com.letv.portal.service.IMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlDbSpaceMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlGaleraMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlHealthMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlInnoDBMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlKeyBufferMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlResourceMonitorService;
import com.letv.portal.service.monitor.mysql.IMysqlTableSpaceMonitorService;

@Service("monitorService")
public class MonitorServiceImpl extends BaseServiceImpl<MonitorDetailModel> implements IMonitorService {

	private final static Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
	
	@Autowired
	private IMonitorDao monitorDao;
	
	@Autowired
	private IMonitorIndexService monitorIndexService;
	
	@Autowired
	private IContainerService containerService;
	
	@Autowired
	private IMysqlHealthMonitorService mysqlHealthMonitorService;
	@Autowired
	private IMysqlResourceMonitorService mysqlResourceMonitorService;
	@Autowired
	private IMysqlKeyBufferMonitorService mysqlKeyBufferMonitorService;
	@Autowired
	private IMysqlInnoDBMonitorService mysqlInnoDBMonitorService;
	@Autowired
	private IMysqlDbSpaceMonitorService mysqlDbSpaceMonitorService;
	@Autowired
	private IMysqlTableSpaceMonitorService mysqlTableSpaceMonitorService;
	@Autowired
	private IMysqlGaleraMonitorService mysqlGaleraMonitorService;
	@Value("${jdbc.url}")
	private String jdbcUrl;
	@Value("${monitor.statistics.cycle}")
	private int cycleTime;
	
	@Autowired
	private IHostService hostService;

    private ICacheService<?> cacheService = CacheFactory.getCache();
	
	public MonitorServiceImpl() {
		super(MonitorDetailModel.class);
	}

	@Override
	public IBaseDao<MonitorDetailModel> getDao() {
		return this.monitorDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MonitorViewYModel> getHostDiskMonitorData(Long hostId, Long chartId, Integer strategy) {
		List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
		HostModel hostModel = this.hostService.selectById(hostId);
	    
	    MonitorIndexModel monitorIndexModel  = this.monitorIndexService.selectById(chartId);	   
	    Date end = new Date();
	    Date start = getStartDate(end, strategy);
	    
		String[] indexs = getIndexs(monitorIndexModel.getDetailTable().toLowerCase(), start, end);
		AndFilterBuilder filterBuilder = FilterBuilders.andFilter(
				FilterBuilders.termFilter("ip", hostModel.getHostIp()),
				FilterBuilders.rangeFilter("timestamp").from(start).to(end));
		FieldSortBuilder sortBuilder = SortBuilders.fieldSort("timestamp").order(SortOrder.ASC);
		
		SearchHits searchHits = ESUtil.getFilterResult(indexs, filterBuilder, sortBuilder, 100000);

		String[] detailNames =  monitorIndexModel.getMonitorPoint().split(",");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		for (String s : detailNames) {
			MonitorViewYModel ydata = new MonitorViewYModel();
			List<List<Object>> datas = new ArrayList<List<Object>>();
			String[] layer = s.split("\\.");
			try {
				for (SearchHit hit : searchHits.getHits()) {
					Map<String, Object> data = hit.getSource();
					Date d = sdf.parse((String)data.get("timestamp"));
					int j = layer.length-1;
					for(int i=0; i<j; i++) {//适配“user.system”这类map嵌套取值
						data = (Map<String, Object>) data.get(layer[i]);
					}
					if(data.containsKey(layer[j])) {
						List<Object> point = new ArrayList<Object>();
						point.add(d);
						point.add(data.get(layer[j]));
						datas.add(point);
					}
				}
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}

			ydata.setName(hostModel.getHostIp() +":"+s);
			ydata.setData(datas);
			ydatas.add(ydata);
		}
		return ydatas;
	}
	@Override
	public List<MonitorViewYModel> getMonitorViewData(Long mclusterId,Long chartId,Integer strategy) {
		List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusterId);
		List<ContainerModel> containers = this.containerService.selectNodeContainersByMap(map);	  
		
		MonitorIndexModel monitorIndexModel  = this.monitorIndexService.selectById(chartId);	   
		Date end = new Date();
		String[] detailNames =  monitorIndexModel.getMonitorPoint().split(",");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("dbName", monitorIndexModel.getDetailTable());
		params.put("start", getStartDate(end,strategy));
		params.put("end", end);
		
		for (ContainerModel c : containers) {
			for (String s : detailNames) {
				MonitorViewYModel ydata = new MonitorViewYModel();
				params.put("ip", c.getIpAddr());
				params.put("detailName", s);
				
				List<MonitorDetailModel> list = this.monitorDao.selectDateTime(params);
				List<List<Object>> datas = new ArrayList<List<Object>>();
				for (MonitorDetailModel monitorDetail : list) {
					List<Object> point = new ArrayList<Object>();
					point.add(monitorDetail.getMonitorDate());
					point.add(monitorDetail.getDetailValue());
					datas.add(point);
				}
				ydata.setName(c.getIpAddr() +":"+s);
				ydata.setData(datas);
				ydatas.add(ydata);
			}
		}
		return ydatas;
	}
  /* @Override
    public List<MonitorViewYModel> getMonitorViewData(Long mclusterId,Long chartId,Integer strategy) {
        List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mclusterId", mclusterId);
        map.put("type", "mclusternode");
        List<ContainerModel> containers = this.containerService.selectByMap(map);

       MonitorIndexModel monitorIndexModel  = this.monitorIndexService.selectById(chartId);
       Date end = new Date();
       String[] detailNames =  monitorIndexModel.getMonitorPoint().split(",");

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
       long tbefore = System.currentTimeMillis();
       for (ContainerModel c : containers) {
            for (String s : detailNames) {
                MonitorViewYModel ydata = new MonitorViewYModel();

                *//*AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter(
                        FilterBuilders.termFilter("ip", c.getIpAddr().toLowerCase()),
                        FilterBuilders.termFilter("detailName", s.toLowerCase().toLowerCase()),
                      FilterBuilders.rangeFilter("monitorDate").from(getStartDate(end, strategy).getTime()).to(end.getTime())
                );*//*
                long before = System.currentTimeMillis();
                BoolFilterBuilder must = FilterBuilders.boolFilter().must(FilterBuilders.termFilter("ip", c.getIpAddr().toLowerCase()))
                        .must(FilterBuilders.termFilter("detailName", s.toLowerCase().toLowerCase()))
                        .must(FilterBuilders.rangeFilter("monitorDate").from(getStartDate(end, strategy).getTime()).to(end.getTime()));
                SearchHits searchHits = ESUtil.getFilterSortResult(getIndexs(Constant.ES_RDS_MONITOR_INDEX + monitorIndexModel.getDetailTable().toLowerCase(), getStartDate(end, strategy), end), must, "monitorDate");
                List<List<Object>> datas = new ArrayList<List<Object>>();
                for (SearchHit hit : searchHits) {
                    List<Object> point = new ArrayList<Object>();
                    Map<String, Object> source = hit.getSource();
                    try {
                        point.add(sdf.parse((String) source.get("monitorDate")).getTime());
                    } catch (ParseException e) {
                        throw new CommonException("监控日期格式化出错");
                    }
                    point.add(source.get("detailValue"));
                    datas.add(point);
                }
                ydata.setName(c.getIpAddr() +":"+s);
                ydata.setData(datas);
                ydatas.add(ydata);
                logger.info("searchHits time:{}",System.currentTimeMillis()-before);
            }
       }
       logger.info("all searchHits time:{}",System.currentTimeMillis()-tbefore);
       return ydatas;
}*/
    private  String[] getIndexs(String indexPrefix,Date start,Date end) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(start);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);
        List<String> list = new ArrayList<String>();
        while(true) {
        	String indexName = indexPrefix +"_"+ DataFormat.compactDate(startTime.getTime());
        	if(isExistsIndex(indexName)) {
        		list.add(indexName);
        	}
            startTime.add(Calendar.DATE, 1);
            if (startTime.compareTo(endTime) > 0) {
                break;
            }
        }
        return list.toArray(new String[list.size()]);
    }
    
    //判断索引是否存在
    private boolean isExistsIndex(String indexName){
        IndicesExistsResponse response = ESUtil.getClient().admin().indices().exists( 
                        new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
        return response.isExists();
    }

    @Override
    public void syncMonitorFromDbToEs(String dbName,int strategy) {
        Date end = new Date();

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("dbName", dbName);
        params.put("start", getStartDate(end,strategy));
        params.put("end", end);

        List<MonitorDetailModel> list = this.monitorDao.selectDateTime(params);

        for (MonitorDetailModel monitorDetail:list) {
            Map<String,Object> monitorMap = new HashMap<String,Object>();
            monitorMap.put("detailName",monitorDetail.getDetailName());
            monitorMap.put("detailValue",monitorDetail.getDetailValue());
            monitorMap.put("ip",monitorDetail.getIp());
            monitorMap.put("monitorDate",monitorDetail.getMonitorDate());
            String dataStr = DataFormat.compactDate(monitorDetail.getMonitorDate());
            String add = ESUtil.add(Constant.ES_RDS_MONITOR_INDEX + dbName.toLowerCase() + "_" + dataStr, monitorDetail.getDetailName().toLowerCase(), monitorMap);
            logger.info("current Thread:{},insert id:{}",Thread.currentThread().getName(),add);
        }

    }
    @Override
    public void syncMonitorFromDbToEs(String dbName,Date start,Date end) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("dbName", dbName);
        params.put("start", start);
        params.put("end", end);

        List<MonitorDetailModel> list = this.monitorDao.selectDateTime(params);

        for (MonitorDetailModel monitorDetail:list) {
            Map<String,Object> monitorMap = new HashMap<String,Object>();
            monitorMap.put("detailName",monitorDetail.getDetailName());
            monitorMap.put("detailValue",monitorDetail.getDetailValue());
            monitorMap.put("ip",monitorDetail.getIp());
            monitorMap.put("monitorDate",monitorDetail.getMonitorDate());
            String dataStr = DataFormat.compactDate(monitorDetail.getMonitorDate());
            String add = ESUtil.add(Constant.ES_RDS_MONITOR_INDEX + dbName.toLowerCase() + "_" + dataStr, monitorDetail.getDetailName().toLowerCase(), monitorMap);
            logger.info("current Thread:{},insert id:{}",Thread.currentThread().getName(),add);
        }

    }



    @Override
	public List<MonitorViewYModel> getMonitorTopNViewData(Long hclusterId, Long chartId,String monitorName, Integer strategy,Integer topN) {

        MonitorIndexModel monitorIndexModel  = this.monitorIndexService.selectById(chartId);
        Date end = new Date();

        List<MonitorDetailModel> topNMonitors = getTopN( monitorIndexModel,hclusterId,monitorName,strategy,topN);//get topN from cbase.
        List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
        if(null == topNMonitors || topNMonitors.isEmpty()) {
            return ydatas;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dbName", monitorIndexModel.getDetailTable());
        params.put("start", getStartDate(end,strategy));
        params.put("end", end);
        params.put("detailName", topNMonitors.get(0).getDetailName());

        for (MonitorDetailModel monitor : topNMonitors) {
            MonitorViewYModel ydata = new MonitorViewYModel();
            params.put("ip", monitor.getIp());
            List<MonitorDetailModel> list = this.monitorDao.selectDateTime(params);
            List<List<Object>> datas = new ArrayList<List<Object>>();
            for (MonitorDetailModel monitorDetail : list) {
                List<Object> point = new ArrayList<Object>();
                point.add(monitorDetail.getMonitorDate());
                point.add(monitorDetail.getDetailValue());
                datas.add(point);
            }
            ydata.setName(MessageFormat.format("{0}:{1}",monitor.getIp(),monitor.getDetailName()));
            ydata.setData(datas);
            ydatas.add(ydata);
        }
        return ydatas;
	}

    @Override
	public List<MonitorViewYModel> getMonitorData(String ip,Long chartId,Integer strategy,boolean isTimeAveraging,int format) {
		List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
		
		MonitorIndexModel monitorIndexModel  = this.monitorIndexService.selectById(chartId);	   
		Date end = new Date();
		String[] detailNames =  monitorIndexModel.getMonitorPoint().split(",");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("dbName", monitorIndexModel.getDetailTable());
		params.put("start", getStartDate(end,strategy));
		params.put("end", end);
		
		/*
		 * 1、按照detailNames进行查询，将contianer数据获取到。
		 * 2、存储到两个list，进行减法计算，除以频次。
		 */
		List<MonitorDetailModel> beforData = new ArrayList<MonitorDetailModel>();
		for (String s : detailNames) {
			MonitorViewYModel ydata = new MonitorViewYModel();
			params.put("ip", ip);
			params.put("detailName", s);
			
			beforData = this.monitorDao.selectDateTime(params); 
			
			List<List<Object>> datas = new ArrayList<List<Object>>();
			if(isTimeAveraging) {
				for (int i = 0; i < beforData.size()-1; i++) {
					List<Object> point = new ArrayList<Object>();
					point.add(beforData.get(i+1).getMonitorDate());
					float diff = beforData.get(i+1).getDetailValue()-beforData.get(i).getDetailValue();
					float time = (beforData.get(i+1).getMonitorDate().getTime()-beforData.get(i).getMonitorDate().getTime())/1000;
					float value = diff>0&&time>0 ? diff/time : 0;
					value = monitorDataAdapter(value,format);
					point.add(value);
					datas.add(point);
				}
			} else {
				for (int i = 0; i <= beforData.size()-1; i++) {
					List<Object> point = new ArrayList<Object>();
					point.add(beforData.get(i).getMonitorDate());
					float value = beforData.get(i).getDetailValue()>=0?beforData.get(i).getDetailValue():0;
					value = monitorDataAdapter(value,format);
					point.add(value);
					datas.add(point);
				}
			}
			
			ydata.setName(s);
			ydata.setData(datas);
			ydatas.add(ydata);
		}
		return ydatas;
	}
	
	private float monitorDataAdapter(float value, int format) {
		DecimalFormat df=new DecimalFormat("##########.00");
		switch(format) {
			case 1: //B 转   M
				value=value/1024/1024;
				break;
			case 2: //B 转  G
				value = value/1024/1024/1024;
				break;
			default:
				break;
		}
		return Float.valueOf(df.format(value));
		
	}


	private Date getStartDate(Date end, Integer strategy) {
		Calendar now = Calendar.getInstance();
		now.setTime(end);
		switch (strategy) {
		case 1:
			now.add(Calendar.HOUR, -1); //one hour ago
			break;
		case 2:
			now.add(Calendar.HOUR, -3); //three hour ago
			break;
		case 3:
			now.add(Calendar.HOUR, -24);  // one day ago
			break;
		case 4:
//			now.add(Calendar.HOUR, -168); // one week ago
			now.add(Calendar.HOUR, -120); // one week ago
			break;
		case 5:
			now.add(Calendar.MONTH, -1); // one month ago
			break;
        case 6:
            now.add(Calendar.HOUR, -12);  // 12 hour ago
            break;
        case 7:
            now.add(Calendar.HOUR, -72);  // three day ago
            break;
		default:
			now.add(Calendar.HOUR, -1);
			break;
		}
		return now.getTime();
	}

	@Override
	public Float selectDbStorage(Long mclusterId) {
		List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("mclusterId", mclusterId);
	    List<ContainerModel> containers = this.containerService.selectNodeContainersByMap(map);
	    if(containers.size()<0) {
	    	return 0F;
	    }
		return this.monitorDao.selectDbStorage(containers.get(0).getIpAddr());
	}


	@Override
	public List<Map<String,Object>> selectDbConnect(Long mclusterId) {
		List<MonitorViewYModel> ydatas = new ArrayList<MonitorViewYModel>();
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("mclusterId", mclusterId);
	    List<ContainerModel> containers = this.containerService.selectNodeContainersByMap(map);
	    if(containers.size()<0) {
	    	return null;
	    }
		return this.monitorDao.selectDbConnect(containers.get(0).getIpAddr());
	}

	@Override
	public void deleteOutDataByIndex(Map<String, Object> map) {
		this.monitorDao.deleteOutDataByIndex(map);
	}

	@Override
	public List<Map<String, Object>> selectExtremeIdByMonitorDate(
			Map<String, Object> map) {
		return this.monitorDao.selectExtremeIdByMonitorDate(map);
	}


	@Override
	public void addMonitorPartition(Map<String, Object> map, Date d) {
		String tableSchema = jdbcUrl.substring(jdbcUrl.lastIndexOf("/")+1, jdbcUrl.indexOf("?"));
		map.put("tableSchema", tableSchema);
		int i = 0;
		//从往前第八天开始查询是否存在分区，不存在，查询第七天...；存在后，跳出循环；当分区超过38个后，跳出循环；
		while(true) {
			getPartitionInfos(map, d, 8-i);
			//根据分区名称获取分区排序号
			String order = this.monitorDao.getPartitionOrder(map);
			if(order==null) {
				i++;
				if(i==38) {
					break;
				}
			} else {
				i--;
				break;
			}
		}
		//从时间最小的分区开始创建，一直到往前的第八天
		for(int j=i; j>=0; j--) {
			getPartitionInfos(map, d, 8-j);
			this.monitorDao.addMonitorPartition(map);
		}
	}
	
	/**
	  * @Title: getPartitionInfos
	  * @Description: 计算分区名称和分区时间（一天2个分区，名称以pa+yyyyMMdd、pb+yyyyMMdd分开，时间以每天12点、24点分开）
	  * @param map 往map中放值
	  * @param d 计算起始时间
	  * @param day 几天后时间   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月6日 上午10:04:46
	  */
	private void getPartitionInfos(Map<String, Object> map, Date d, int day) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());
		c.add(Calendar.DATE, day);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		//分区名称
		String partitionName1 = "pa"+formatter.format(c.getTime());
		String partitionName2 = "pb"+formatter.format(c.getTime());
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 12, 0, 0);
		//分区时间
		long partitionTime1 = c.getTimeInMillis();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 24, 0, 0);
		long partitionTime2 = c.getTimeInMillis();
		map.put("partitionName1", partitionName1);
		map.put("partitionTime1", new Date(partitionTime1));
		map.put("partitionName2", partitionName2);
		map.put("partitionTime2", new Date(partitionTime2));
	}


	@Override
	public void deleteMonitorPartitionThirtyDaysAgo(Map<String, Object> map) {
		String tableSchema = jdbcUrl.substring(jdbcUrl.lastIndexOf("/")+1, jdbcUrl.indexOf("?"));
		map.put("tableSchema", tableSchema);
		//根据分区名称获取分区排序号
		String order = this.monitorDao.getPartitionOrder(map);
		if(order!=null) {
			map.put("order", order);
			//获取小于等于排序号的分区信息
			List<String> names = this.monitorDao.getPartitionInfo(map);
			map.put("names", names);
			this.monitorDao.deleteMonitorPartitionThirtyDaysAgo(map);
		} else {
			logger.info("delete partition is not exist. partition name is "
					+ "("+(String)map.get("partitionName1")+","+(String)map.get("partitionName2")+")");
		}
	}
	
	
	@Override
	public void insertMysqlMonitorData(ContainerModel container, Map<String, Object> map, Date d) {
		this.mysqlHealthMonitorService.collectMysqlHealthMonitorData(container, map, d);
		this.mysqlResourceMonitorService.collectMysqlResourceMonitorData(container, map, d);
		this.mysqlKeyBufferMonitorService.collectMysqlKeyBufferMonitorData(container, map, d);
		this.mysqlInnoDBMonitorService.collectMysqlInnoDBMonitorData(container, map, d);
		this.mysqlGaleraMonitorService.collectMysqlGaleraMonitorData(container, map, d);
	}


	@Override
	public Map<String, Object> getLatestDataFromMonitorTables(String containerIp, String[] titles, Date d) {
		Map<String, String> param = new HashMap<String, String>();
		List<MonitorIndexModel> indexs = null;
		Map<String, Object> results = new HashMap<String, Object>();
		for (String title : titles) {
			param.put("titleText", title);
			indexs = this.monitorIndexService.selectByMap(param);
			if(indexs!=null && indexs.size()==1) {
				Map<String, Object> result = getLatestDataFromMonitorTable(containerIp, indexs.get(0).getDetailTable(), indexs.get(0).getMonitorPoint(), d);
				results.putAll(result);
			} else if(indexs.size()>1){
				logger.info("have many MonitorIndexModels with titleText is : "+title);
			} else {
				logger.info("have no MonitorIndexModel with titleText is : "+title);
			}
		}
		return results;
	}
	
	/**
	  * @Title: getLatestDataFromMonitorTable
	  * @Description: 获取单个监控表的最新数据
	  * @param containerIp
	  * @param tableName
	  * @param colNames
	  * @param d
	  * @return Map<String,Object>   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年7月30日 下午2:07:27
	  */
	private Map<String, Object> getLatestDataFromMonitorTable(String containerIp, String tableName, String colNames, Date d) {
		Map<String, Object> result = new HashMap<String, Object>();
		String[] cols = colNames.split(",");
		Map<String, Object> params = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, -cycleTime);
		Date start = new Date(cal.getTimeInMillis());
		params.put("dbName", tableName);
		params.put("ip", containerIp);
		params.put("start", start);
		params.put("end", d);
		params.put("count", cols.length * 3);//防止监控时，该批数据未全部保存
		List<MonitorDetailModel> models = this.monitorDao.selectLastestData(params);
		if("WEBPORTAL_MONITOR_MYSQL_BASE_QUERY".equals(tableName)) {
			Map<String, Object> computer = new HashMap<String, Object>();
			for (MonitorDetailModel monitorDetailModel : models) {
				//拿到2次值和对应时间，相减后除以时间
				if("stat_QPS_command".equals(monitorDetailModel.getDetailName()) || "stat_Com_rollback".equals(monitorDetailModel.getDetailName()) ||
						"stat_Com_commit_command".equals(monitorDetailModel.getDetailName())) {
					if(computer.get(monitorDetailModel.getDetailName()+"_value")==null) {
						computer.put(monitorDetailModel.getDetailName()+"_value", monitorDetailModel.getDetailValue());
						computer.put(monitorDetailModel.getDetailName()+"_time", monitorDetailModel.getMonitorDate());
					} else {
						long time = (((Date)computer.get(monitorDetailModel.getDetailName()+"_time")).getTime()-monitorDetailModel.getMonitorDate().getTime())/1000;
						if(time!=0) {
							float ret = ((Float)computer.get(monitorDetailModel.getDetailName()+"_value")-monitorDetailModel.getDetailValue())/time;
							result.put(monitorDetailModel.getDetailName(), ret);
						}
					}
				} else {
					result.put(monitorDetailModel.getDetailName(),monitorDetailModel.getDetailValue());
				}
				if(result.size()==cols.length) {//采用最新2次数据，满足采集条数后，退出
					break;
				}
			}
		} else if("WEBPORTAL_MONITOR_MYSQL_BASE_WSREP_REP_REC".equals(tableName)) {
			Map<String, Object> computer = new HashMap<String, Object>();
			for (MonitorDetailModel monitorDetailModel : models) {
				//拿到2次值和对应时间，相减后除以时间
				if(computer.get(monitorDetailModel.getDetailName()+"_value")==null) {
					computer.put(monitorDetailModel.getDetailName()+"_value", monitorDetailModel.getDetailValue());
					computer.put(monitorDetailModel.getDetailName()+"_time", monitorDetailModel.getMonitorDate());
				} else {
					long time = (((Date)computer.get(monitorDetailModel.getDetailName()+"_time")).getTime()-monitorDetailModel.getMonitorDate().getTime())/1000;
					if(time!=0) {
						float ret = ((Float)computer.get(monitorDetailModel.getDetailName()+"_value")-monitorDetailModel.getDetailValue())/time;
						result.put(monitorDetailModel.getDetailName(), ret);
					}
				}
				if(result.size()==cols.length) {//采用最新2次数据，满足采集条数后，退出
					break;
				}
			}
		} else if("WEBPORTAL_MONITOR_MYSQL_BASE_NET".equals(tableName)) {
			Map<String, Object> computer = new HashMap<String, Object>();
			for (MonitorDetailModel monitorDetailModel : models) {
				if(computer.get(monitorDetailModel.getDetailName()+"_value")==null) {
					computer.put(monitorDetailModel.getDetailName()+"_value", monitorDetailModel.getDetailValue());
				} else {
					float ret = ((Float)computer.get(monitorDetailModel.getDetailName()+"_value")-monitorDetailModel.getDetailValue());
					result.put(monitorDetailModel.getDetailName(), Math.abs(ret));
				}
				if(result.size()==cols.length) {//采用最新2次数据，满足采集条数后，退出
					break;
				}
			}
		} else {
			for (MonitorDetailModel monitorDetailModel : models) {
				result.put(monitorDetailModel.getDetailName(),monitorDetailModel.getDetailValue());
				if(result.size()==cols.length) {//采用最新2次数据，满足采集条数后，退出
					break;
				}
			}
		}
		
		return result;
	}


	@Override
	public void insertMysqlMonitorSpaceData(String dbName, ContainerModel container,
			Map<String, Object> map, Date d) {
		int count = 0;
		MysqlDbSpaceMonitor dbSpace = null;
		//由于保存tableSpace表时需要有dbSpace的id，所以第一个for循环先保存dbSpace表，第二个for循环保存tableSpace表
		for(Iterator it =  map.keySet().iterator();it.hasNext();){
			String key = (String) it.next();
			if(map.get(key)!=null && map.get(key) instanceof Map) {
				continue;
			} else {
				//当值不存在时，表明本次调用数据有误，定义返回-1，用于提示数据有误
				String size = map.get(key)==null?"-1":(String)map.get(key);
				dbSpace = this.mysqlDbSpaceMonitorService.collectMysqlDbSpaceMonitorData(dbName, container, size, d);
				count++;
				break;
			}
		}
		//确认保存了dbSpace表
		if(count==1) {
			for(Iterator it =  map.keySet().iterator();it.hasNext();){
				String key = (String) it.next();
				if(map.get(key)!=null && map.get(key) instanceof Map) {
					Map<String,Object> sizeAndComment = (Map<String, Object>) map.get(key);
					this.mysqlTableSpaceMonitorService.collectMysqlTableSpaceMonitorData(dbSpace.getId(), key, container, sizeAndComment, d);
				}
			}
		}
		
	}

	@Override
	public void saveMonitorErrorInfo(MonitorErrorModel error) {
		this.monitorDao.saveMonitorErrorInfo(error);
	}

	@Override
	public List<Map<String, Object>> getMonitorErrorModelsByMap(
			Map<String, Object> map) {
		return this.monitorDao.getMonitorErrorModelsByMap(map);
	}

    @Override
    public void updateTopN(MonitorDetailModel monitorDetail,Long hclusterId) {
        if(null == monitorDetail)
            return;
        Map<String,Long> params = new HashMap<String,Long>();
        String split = ":";
        String key;
        StringBuffer keySuffix = new StringBuffer().append(split).append(hclusterId).append(split).append(monitorDetail.getDbName()).append(split).append(monitorDetail.getDetailName());
        key = new StringBuffer().append(Constant.MONITOR_TOPBY_12H_PREFIX).append(keySuffix).toString();
        params.put(key,Constant.MONITOR_TOPBY_12H);
        key = new StringBuffer().append(Constant.MONITOR_TOPBY_24H_PREFIX).append(keySuffix).toString();
        params.put(key,Constant.MONITOR_TOPBY_24H);
        key = new StringBuffer().append(Constant.MONITOR_TOPBY_3D_PREFIX).append(keySuffix).toString();
        params.put(key,Constant.MONITOR_TOPBY_3D);
        key = new StringBuffer().append(Constant.MONITOR_TOPBY_1W_PREFIX).append(keySuffix).toString();
        params.put(key,Constant.MONITOR_TOPBY_1W);

        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> param : params.entrySet()) {
            List<MonitorDetailModel> dataOld = (List<MonitorDetailModel>) this.cacheService.get(param.getKey(),null);
            List<MonitorDetailModel> dataNow = new ArrayList<MonitorDetailModel>();
            if(dataOld ==null)
                dataOld = new ArrayList<MonitorDetailModel>();
            boolean flag = true;
            for (MonitorDetailModel monitor:dataOld) {
                if(now-monitor.getMonitorDate().getTime()<param.getValue()) {
                    if(flag && monitor.getIp().equals(monitorDetail.getIp())) {
                        dataNow.add(monitor.getDetailValue()>=monitorDetail.getDetailValue()?monitor:monitorDetail);
						flag = false;
                    } else {
                        dataNow.add(monitor);
                    }
                }

            }
			if(flag) {
                dataNow.add(monitorDetail);
                Collections.sort(dataNow);
                if(dataNow.size() > Constant.MONITOR_TOP_MAX)
                    dataNow.remove(0);
            }
            this.cacheService.set(param.getKey(), dataNow);
            logger.info("set key:{}-------------",param.getKey());
        }
    }

    @Override
    public List<MonitorDetailModel> getTopN(MonitorIndexModel monitorIndex,Long hclusterId,String monitorName, Integer strategy, Integer topN) {
        if(null == hclusterId) {
            return null;
        }
        String topNKey = getTopNKey(monitorIndex,hclusterId,monitorName,strategy);
        List<MonitorDetailModel> monitors = (List<MonitorDetailModel>) this.cacheService.get(topNKey,null);
        if(monitors !=null && monitors.size()>topN)
            monitors = monitors.subList(monitors.size()-topN, monitors.size());
        return monitors;
    }

    private String getTopNKey(MonitorIndexModel monitorIndex,Long hclusterId, String monitorName,Integer strategy) {
        StringBuffer key = new StringBuffer();
        switch (strategy) {
            case 3:
                key.append(Constant.MONITOR_TOPBY_24H_PREFIX);
                break;
            case 6:
                key.append(Constant.MONITOR_TOPBY_12H_PREFIX);
                break;
            case 7:
                key.append(Constant.MONITOR_TOPBY_3D_PREFIX);
                break;
            case 4:
                key.append(Constant.MONITOR_TOPBY_1W_PREFIX);
                break;
            default:
                key.append(Constant.MONITOR_TOPBY_12H_PREFIX);
                break;
        }
        key.append(":").append(hclusterId).append(":").append(monitorIndex.getDetailTable()).append(":").append(monitorName);
        return key.toString();
    }
}
