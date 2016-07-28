package com.letv.portal.service.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.letv.common.exception.ValidateException;
import com.letv.portal.enumeration.ZookeeperStatus;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.service.IHclusterService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.common.IZookeeperInfoDao;
import com.letv.portal.model.common.ZookeeperInfo;
import com.letv.portal.service.common.IZookeeperInfoService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("zookeeperInfoService")
public class ZookeeperInfoServiceImpl extends BaseServiceImpl<ZookeeperInfo> implements IZookeeperInfoService{
	
	private final static Logger logger = LoggerFactory.getLogger(ZookeeperInfoServiceImpl.class);
	
	@Resource
	private IZookeeperInfoDao zookeeperInfoDao;
	@Resource
	private IHclusterService hclusterService;

	public ZookeeperInfoServiceImpl() {
		super(ZookeeperInfo.class);
	}

	@Override
	public IBaseDao<ZookeeperInfo> getDao() {
		return this.zookeeperInfoDao;
	}

	@Override
	public ZookeeperInfo selectMinusedZk() {
		return this.zookeeperInfoDao.selectMinusedZk();
	}

	@Override
	public List<ZookeeperInfo> selectMinusedZkByHclusterId(Long hclusterId,int number) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("hclusterId", hclusterId);
		params.put("number", number);
		return this.zookeeperInfoDao.selectMinusedZkByHclusterId(params);
	}

	@Override
	public void batchInsert(String name, String ip, Long hclusterId) {
		if(StringUtils.isEmpty(name) || StringUtils.isEmpty(ip) || null == hclusterId)
			throw new ValidateException("参数不合法");
		String[] ips = ip.split(",");
		if(null == ips || 0 == ips.length)
			throw new ValidateException("参数不合法");
		HclusterModel hclusterModel = this.hclusterService.selectById(hclusterId);
		if(null == hclusterModel)
			throw new ValidateException("参数不合法");

		for (String ipStr:ips) {
			ZookeeperInfo zk = new ZookeeperInfo();
			zk.setStatus(ZookeeperStatus.AVAILABLE);
			zk.setHclusterId(hclusterId);
			zk.setIp(ipStr);
			zk.setName(name);
			zk.setPort("2181");
			zk.setUsed(0);
			this.insert(zk);
		}
	}

	@Override
	public void plusOneUsedByZookeeperId(Long zookeeperId) {
		this.zookeeperInfoDao.plusOneUsedByZookeeperId(zookeeperId);
	}
}
