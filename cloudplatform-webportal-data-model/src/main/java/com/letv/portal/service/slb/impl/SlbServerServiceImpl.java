package com.letv.portal.service.slb.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.portal.dao.slb.ISlbServerDao;
import com.letv.portal.enumeration.GceStatus;
import com.letv.portal.enumeration.SlbStatus;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbConfig;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbConfigService;
import com.letv.portal.service.slb.ISlbServerService;

@Service("slbServerService")
public class SlbServerServiceImpl extends BaseServiceImpl<SlbServer> implements ISlbServerService{
	
	private final static Logger logger = LoggerFactory.getLogger(SlbServerServiceImpl.class);
	
	@Resource
	private ISlbServerDao slbServerDao;
	@Autowired
	private ISlbClusterService slbClusterService;
	@Resource
	private ISlbConfigService slbConfigService;

	public SlbServerServiceImpl() {
		super(SlbServer.class);
	}

	@Override
	public IBaseDao<SlbServer> getDao() {
		return this.slbServerDao;
	}
	
	@Override
	public void insert(SlbServer t) {
		if(t == null)
			throw new ValidateException("参数不合法");
		t.setStatus(SlbStatus.NORMAL.getValue());
		super.insert(t);
	}
	@Override
	public Map<String,Object> save(SlbServer slbServer) {
		
		slbServer.setStatus(GceStatus.BUILDDING.getValue());
		
		StringBuffer clusterName = new StringBuffer();
		clusterName.append(slbServer.getCreateUser()).append("_").append(slbServer.getSlbName());
		
		/*function 验证mclusterName是否存在*/
		
		SlbCluster slbCluster = new SlbCluster();
		slbCluster.setHclusterId(slbServer.getHclusterId());
		slbCluster.setClusterName(clusterName.toString());
		slbCluster.setStatus(SlbStatus.BUILDDING.getValue());
		slbCluster.setCreateUser(slbServer.getCreateUser());
		slbCluster.setAdminUser("root");
		slbCluster.setAdminPassword(clusterName.toString());
		
		this.slbClusterService.insert(slbCluster);
		
		slbServer.setSlbClusterId(slbServer.getId());
		this.slbServerDao.insert(slbServer);
		
		Map<String,Object> params = new HashMap<String,Object>();
    	params.put("SlbClusterId", slbCluster.getId());
    	params.put("SlbId", slbServer.getId());
		return params;
	}
	@Override
	public <K, V> Page selectPageByParams(Page page, Map<K, V> params) {
		page = super.selectPageByParams(page, params);
		List<SlbServer> data = (List<SlbServer>) page.getData();
		
		for (SlbServer slbServer : data) {
			List<SlbConfig> slbConfigs = this.slbConfigService.selectBySlbServerId(slbServer.getId());
			slbServer.setSlbConfigs(slbConfigs);
		}
		page.setData(data);
		return page;
	}

}
