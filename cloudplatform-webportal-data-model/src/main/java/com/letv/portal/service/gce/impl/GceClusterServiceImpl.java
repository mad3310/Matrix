package com.letv.portal.service.gce.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.service.gce.IGceServerService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.MatrixException;
import com.letv.portal.dao.gce.IGceClusterDao;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.enumeration.MclusterType;
import com.letv.portal.model.HclusterModel;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceContainerExt;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceContainerExtService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("gceClusterService")
public class GceClusterServiceImpl extends BaseServiceImpl<GceCluster> implements IGceClusterService{
	
	private final static Logger logger = LoggerFactory.getLogger(GceClusterServiceImpl.class);
	
	@Resource
	private IGceClusterDao gceClusterDao;
    @Resource
    private IGceServerService gceServerService;
	@Resource
	private IGceContainerService gceContainerService;
	@Resource
	private IHostService hostService;
	@Resource
	private IGceContainerExtService gceContainerExtService;
    @Autowired
    private IFixedPushService fixedPushService;


    public GceClusterServiceImpl() {
		super(GceCluster.class);
	}

	@Override
	public IBaseDao<GceCluster> getDao() {
		return this.gceClusterDao;
	}

	@Override
	public Boolean isExistByName(String clusterName) {
		List<GceCluster> mclusters = this.gceClusterDao.selectByName(clusterName);
		return mclusters.size() == 0?true:false;
	}

	@Override
	public List<GceCluster> selectByName(String clusterName) {
		return this.gceClusterDao.selectByName(clusterName);
	}
	
	@Override
	public void asyncClusterCount(Map<String,Object> mm,HclusterModel hcluster) {
		String clusterName = (String) mm.get("clusterName");
		if(StringUtils.isEmpty(clusterName))
			return;
		List<GceCluster> list = this.selectByName(clusterName);
		if(null == list || list.isEmpty()) {
			this.addHandMcluster(mm,hcluster.getId());
		} else {
			GceCluster cluster = list.get(0);
			if(MclusterStatus.BUILDDING.getValue() == cluster.getStatus())
				return;
			addOrUpdateContainer(mm,cluster);
		}
	}

	@Override
	public Integer selectValidClusterCount() {
		return this.gceClusterDao.selectValidClusterCount();
	}

	private void addOrUpdateContainer(Map<String,Object> mm,GceCluster cluster) {
		List<Map<String,Object>> cms = (List<Map<String,Object>>) mm.get("nodeInfo");
		for (Map<String,Object> cm : cms) {
			GceContainer container  = this.gceContainerService.selectByName((String) cm.get("containerName"));
			if(null == container) {
				this.addHandContainer(cm, cluster.getId());
				continue;
			} 
		}
	}
	private void addHandMcluster(Map mm,Long hclusterId) {
		GceCluster cluster = new GceCluster();
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
		
		GceContainer container = new GceContainer();
		try {
			BeanUtils.populate(container, cm);
		} catch (Exception e) {
			throw new MatrixException("container转换失败", e);
		}
		container.setGceClusterId(mclusterId);
		container.setIpMask((String) cm.get("netMask"));
		container.setStatus(MclusterStatus.RUNNING.getValue());
		//物理机集群维护完成后，修改此处，需要关联物理机id
		HostModel hostModel = this.hostService.selectByIp((String) cm.get("hostIp"));
		if(null != hostModel) {
			container.setHostId(hostModel.getId());
		}
		List<Map> portBindings = (List<Map>) cm.get("port_bindings");
		StringBuffer hostPort = new StringBuffer();
		StringBuffer containerPort = new StringBuffer();
		StringBuffer protocol = new StringBuffer();
		if(null != portBindings) {
			for (Map portBinding : portBindings) {
				if("manager".equals(portBinding.get("type"))) {
					container.setMgrBindHostPort((String)portBinding.get("hostPort"));
					continue;
				}
				if("9999".equals(portBinding.get("containerPort"))) {
					container.setLogBindHostPort((String)portBinding.get("hostPort"));
					continue;
				}
				hostPort.append((String)portBinding.get("hostPort")).append(",");
				containerPort.append((String)portBinding.get("containerPort")).append(",");
				protocol.append((String)portBinding.get("protocol")).append(",");
			}
		}
		container.setBingHostPort(hostPort.length()>0?hostPort.substring(0, hostPort.length()-1):hostPort.toString());
		container.setBindContainerPort(containerPort.length()>0?containerPort.substring(0, containerPort.length()-1):containerPort.toString());
		container.setBingProtocol(protocol.length()>0?protocol.substring(0, protocol.length()-1):protocol.toString());
		
		this.gceContainerService.insert(container);
		
		if("jetty".equals(cm.get("type"))) {
			GceContainerExt ext = new GceContainerExt();
			ext.setContainerId(container.getId());
			if(null != portBindings) {
				for (Map portBinding : portBindings) {
					//保存gceContainer扩展表，记录映射端口
					if("9888".equals(portBinding.get("containerPort"))) {//gbalance端口
						ext.setBindPort((String)portBinding.get("hostPort"));
						ext.setInnerPort((String)portBinding.get("containerPort"));
						ext.setType("glb");
						ext.setDescn("gbalancer映射内外端口");
						this.gceContainerExtService.insert(ext);
					} else if("7777".equals(portBinding.get("containerPort"))) {//moxi端口
						ext.setBindPort((String)portBinding.get("hostPort"));
						ext.setInnerPort((String)portBinding.get("containerPort"));
						ext.setType("moxi");
						ext.setDescn("moxi映射内外端口");
						this.gceContainerExtService.insert(ext);
					}
				}
			}
		}
		
	}

    @Override
    public void delete(GceCluster gceCluster) {
		List<GceContainer> gceContainers = this.gceContainerService.selectByGceClusterId(gceCluster.getId());
		for (GceContainer container:gceContainers) {
            if(container.getIpAddr().startsWith("10.")) {
                this.fixedPushService.sendFixedInfo(container.getHostIp(),container.getContainerName(),container.getIpAddr(),"delete");
            }
		}
		this.gceContainerExtService.deleteByClusterId(gceCluster.getId());
        this.gceContainerService.deleteByClusterId(gceCluster.getId());
        this.gceServerService.deleteByClusterId(gceCluster.getId());
        this.gceClusterDao.delete(gceCluster);
    }

	@Override
	public List<GceCluster> selectValidCluster() {
		return this.gceClusterDao.selectValidCluster();
	}
}
