package com.letv.portal.service.slb.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.service.slb.ISlbServerService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.MatrixException;
import com.letv.portal.dao.slb.ISlbClusterDao;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.enumeration.MclusterType;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.gce.GceContainerExt;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.slb.ISlbClusterService;
import com.letv.portal.service.slb.ISlbContainerService;

@Service("slbClusterService")
public class SlbClusterServiceImpl extends BaseServiceImpl<SlbCluster> implements ISlbClusterService{
	
	private final static Logger logger = LoggerFactory.getLogger(SlbClusterServiceImpl.class);
	
	@Resource
	private ISlbClusterDao slbClusterDao;
	@Resource
	private ISlbContainerService slbContainerService;
	@Resource
	private ISlbServerService slbServerService;
	@Resource
	private IHostService hostService;
	@Autowired
	private IFixedPushService fixedPushService;
	
	public SlbClusterServiceImpl() {
		super(SlbCluster.class);
	}

	@Override
	public IBaseDao<SlbCluster> getDao() {
		return this.slbClusterDao;
	}

	@Override
	public Boolean isExistByName(String clusterName) {
		List<SlbCluster> mclusters = this.slbClusterDao.selectByName(clusterName);
		return mclusters.size() == 0?true:false;
	}
	
	@Override
	public List<SlbCluster> selectByName(String clusterName) {
		return this.slbClusterDao.selectByName(clusterName);
	}

	
	@Override
	public void asyncClusterCount(Map<String,Object> mm,HclusterModel hcluster) {
		String clusterName = (String) mm.get("clusterName");
		if(StringUtils.isEmpty(clusterName))
			return;
		List<SlbCluster> list = this.selectByName(clusterName);
		if(null == list || list.isEmpty()) {
			this.addHandMcluster(mm,hcluster.getId());
		} else {
			SlbCluster cluster = list.get(0);
			if(MclusterStatus.BUILDDING.getValue() == cluster.getStatus())
				return;
			addOrUpdateContainer(mm,cluster);
		}
	}

	@Override
	public Integer selectValidClusterCount() {
		return this.slbClusterDao.selectValidClusterCount();
	}

	private void addOrUpdateContainer(Map<String,Object> mm,SlbCluster cluster) {
		List<Map<String,Object>> cms = (List<Map<String,Object>>) mm.get("nodeInfo");
		for (Map<String,Object> cm : cms) {
			SlbContainer container  = this.slbContainerService.selectByName((String) cm.get("containerName"));
			if(null == container) {
				this.addHandContainer(cm, cluster.getId());
				continue;
			} 
		}
	}
	
	private void addHandMcluster(Map mm,Long hclusterId) {
		SlbCluster cluster = new SlbCluster();
		cluster.setClusterName((String) mm.get("clusterName"));
		cluster.setStatus(MclusterStatus.RUNNING.getValue());	
		cluster.setAdminUser("root");
		cluster.setAdminPassword((String) mm.get("clusterName"));
		cluster.setType(MclusterType.HAND.getValue());
		cluster.setHclusterId(hclusterId);
		cluster.setDeleted(true);
		this.insert(cluster);
		List<Map<String,Object>> cms = (List<Map<String,Object>>) mm.get("nodeInfo");
		for (Map<String,Object> cm : cms) {
			this.addHandContainer(cm,cluster.getId());
		}
	}
	
	private void addHandContainer(Map cm,Long mclusterId) {
		
		SlbContainer container = new SlbContainer();
		try {
			BeanUtils.populate(container, cm);
		} catch (Exception e) {
			throw new MatrixException("container转换失败", e);
		}
		container.setSlbClusterId(mclusterId);
		container.setIpMask((String) cm.get("netMask"));
		container.setStatus(MclusterStatus.RUNNING.getValue());
		//物理机集群维护完成后，修改此处，需要关联物理机id
		HostModel hostModel = this.hostService.selectByIp((String) cm.get("hostIp"));
		if(null != hostModel) {
			container.setHostId(hostModel.getId());
		}
		this.slbContainerService.insert(container);
	}

	@Override
	public void delete(SlbCluster slbCluster) {
		List<SlbContainer> slbContainers = this.slbContainerService.selectBySlbClusterId(slbCluster.getId());
		for (SlbContainer container:slbContainers) {
			if(container.getIpAddr().startsWith("10.")) {
				this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"delete");
			}
		}
		this.slbContainerService.deleteByClusterId(slbCluster.getId());
		this.slbServerService.deleteByClusterId(slbCluster.getId());
		this.slbClusterDao.delete(slbCluster);
	}

	@Override
	public List<SlbCluster> selectValidCluster() {
		return this.slbClusterDao.selectValidCluster();
	}
}
