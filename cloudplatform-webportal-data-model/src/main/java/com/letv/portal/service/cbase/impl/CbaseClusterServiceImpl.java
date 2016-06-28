package com.letv.portal.service.cbase.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.letv.portal.service.cbase.ICbaseBucketService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.MatrixException;
import com.letv.portal.dao.cbase.ICbaseClusterDao;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.enumeration.MclusterType;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.cbase.CbaseClusterModel;
import com.letv.portal.model.cbase.CbaseContainerModel;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.cbase.ICbaseClusterService;
import com.letv.portal.service.cbase.ICbaseContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("cbaseClusterService")
public class CbaseClusterServiceImpl extends BaseServiceImpl<CbaseClusterModel>
		implements ICbaseClusterService {

	private final static Logger logger = LoggerFactory
			.getLogger(CbaseClusterServiceImpl.class);

	@Resource
	private ICbaseClusterDao cbaseClusterDao;
	@Resource
	private ICbaseContainerService cbaseContainerService;
    @Resource
    private ICbaseBucketService cbaseBucketService;
	@Resource
	private IHostService hostService;

	public CbaseClusterServiceImpl() {
		super(CbaseClusterModel.class);
	}

	@Override
	public IBaseDao<CbaseClusterModel> getDao() {
		return this.cbaseClusterDao;
	}

	@Override
	public List<CbaseClusterModel> selectByName(String clusterName) {
		return this.cbaseClusterDao.selectByName(clusterName);
	}

	@Override
	public Integer selectValidClusterCount() {
		return this.cbaseClusterDao.selectValidClusterCount();
	}

	@Override
	public void asyncClusterCount(Map<String,Object> mm,HclusterModel hcluster) {
		String clusterName = (String) mm.get("clusterName");
		if(StringUtils.isEmpty(clusterName))
			return;
		List<CbaseClusterModel> list = this.selectByName(clusterName);
		if(null == list || list.isEmpty()) {
			this.addHandMcluster(mm,hcluster.getId());
		} else {
			CbaseClusterModel cluster = list.get(0);
			if(MclusterStatus.BUILDDING.getValue() == cluster.getStatus())
				return;
			addOrUpdateContainer(mm,cluster);
		}
	}

	private void addOrUpdateContainer(Map<String,Object> mm,CbaseClusterModel cluster) {
		List<Map<String,Object>> cms = (List<Map<String,Object>>) mm.get("nodeInfo");
		for (Map<String,Object> cm : cms) {
			CbaseContainerModel container  = this.cbaseContainerService.selectByName((String) cm.get("containerName"));
			if(null == container) {
				this.addHandContainer(cm, cluster.getId());
				continue;
			}
		}
	}

	private void addHandMcluster(Map mm,Long hclusterId) {
		CbaseClusterModel cluster = new CbaseClusterModel();
		cluster.setCbaseClusterName((String) mm.get("clusterName"));
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
			CbaseContainerModel container = new CbaseContainerModel();
			try {
				BeanUtils.populate(container, cm);
			} catch (Exception e) {
				throw new MatrixException("container转换失败", e);
			}
			container.setCbaseClusterId(mclusterId);
			container.setIpMask((String) cm.get("netMask"));
			container.setContainerName((String) cm.get("containerName"));
			container.setStatus(MclusterStatus.RUNNING.getValue());
			container.setHostIp((String) cm.get("hostIp"));
			HostModel hostModel = this.hostService.selectByIp((String) cm
					.get("hostIp"));
			if (null != hostModel) {
				container.setHostId(hostModel.getId());
			}
			this.cbaseContainerService.insert(container);
	}

	@Override
	public void delete(CbaseClusterModel cbaseClusterModel) {
		this.cbaseContainerService.deleteByClusterId(cbaseClusterModel.getId());
		this.cbaseBucketService.deleteByClusterId(cbaseClusterModel.getId());
		super.delete(cbaseClusterModel);
	}
}
