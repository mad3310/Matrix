/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.model.elasticcalc.gce;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.letv.common.model.BaseModel;

/**
 * GCE应用包容器
 * @author linzhanbo .
 * @since 2016年6月27日, 下午3:33:19 .
 * @version 1.0 .
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EcGceContainer extends BaseModel {

	private static final long serialVersionUID = -5070819205246981080L;
	/**
	 * 节点名称
	 */
	private String containerName;

	/**
	 * 挂载路径
	 */
	private String mountDir;

	/**
	 * ZOOKEEPER_ID
	 */
	private String zookeeperId;

	/**
	 * 节点ip
	 */
	private String ipAddr;

	/**
	 * 网关
	 */
	private String gateAddr;

	/**
	 * 子网掩码
	 */
	private String ipMask;

	/**
	 * TYPE VIP or normal
	 */
	private String type;

	/**
	 * 磁盘大小
	 */
	private Integer diskSize;

	/**
	 * cpu内核数
	 */
	private Integer coresNumber;

	/**
	 * cpu速度
	 */
	private Integer cpuSpeed;

	/**
	 * 内存大小
	 */
	private Integer memorySize;

	/**
	 * 所属host
	 */
	private Long hostId;

	/**
	 * HOST_IP
	 */
	private String hostIp;

	/**
	 * 容器内部服务端口
	 */
	private String bindContainerPort;

	/**
	 * 物理机映射端口
	 */
	private String bindHostPort;

	/**
	 * 物理机映射IP
	 */
	private String bindHostIp;

	/**
	 * BIND_PROTOCOL
	 */
	private String bindProtocol;

	/**
	 * 物理机映射端口号
	 */
	private String mgrBindHostPort;

	/**
	 * 调用container用的UUID
	 */
	private String containerUuid;

	/**
	 * GCE主键
	 */
	private Long gceId;

	/**
	 * GCE应用版本ID
	 */
	private Long gcePackageId;

	/**
	 * GCE集群ID
	 */
	private Long gceClusterId;

	/**
	 * 对应zabbix删除container时候需要的标识
	 */
	private String zabbixhosts;

	/**
	 * SERVICE_TYPE
	 */
	private Byte serviceType;

	/**
	 * LOG_BIND_HOST_PORT
	 */
	private String logBindHostPort;

	/**
	 * STATUS
	 */
	private Integer status;
	
	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getMountDir() {
		return mountDir;
	}

	public void setMountDir(String mountDir) {
		this.mountDir = mountDir;
	}

	public String getZookeeperId() {
		return zookeeperId;
	}

	public void setZookeeperId(String zookeeperId) {
		this.zookeeperId = zookeeperId;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getGateAddr() {
		return gateAddr;
	}

	public void setGateAddr(String gateAddr) {
		this.gateAddr = gateAddr;
	}

	public String getIpMask() {
		return ipMask;
	}

	public void setIpMask(String ipMask) {
		this.ipMask = ipMask;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(Integer diskSize) {
		this.diskSize = diskSize;
	}

	public Integer getCoresNumber() {
		return coresNumber;
	}

	public void setCoresNumber(Integer coresNumber) {
		this.coresNumber = coresNumber;
	}

	public Integer getCpuSpeed() {
		return cpuSpeed;
	}

	public void setCpuSpeed(Integer cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
	}

	public Integer getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(Integer memorySize) {
		this.memorySize = memorySize;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getBindContainerPort() {
		return bindContainerPort;
	}

	public void setBindContainerPort(String bindContainerPort) {
		this.bindContainerPort = bindContainerPort;
	}

	public String getBindHostPort() {
		return bindHostPort;
	}

	public void setBindHostPort(String bindHostPort) {
		this.bindHostPort = bindHostPort;
	}

	public String getBindHostIp() {
		return bindHostIp;
	}

	public void setBindHostIp(String bindHostIp) {
		this.bindHostIp = bindHostIp;
	}

	public String getBindProtocol() {
		return bindProtocol;
	}

	public void setBindProtocol(String bindProtocol) {
		this.bindProtocol = bindProtocol;
	}

	public String getMgrBindHostPort() {
		return mgrBindHostPort;
	}

	public void setMgrBindHostPort(String mgrBindHostPort) {
		this.mgrBindHostPort = mgrBindHostPort;
	}

	public String getContainerUuid() {
		return containerUuid;
	}

	public void setContainerUuid(String containerUuid) {
		this.containerUuid = containerUuid;
	}

	public Long getGceId() {
		return gceId;
	}

	public void setGceId(Long gceId) {
		this.gceId = gceId;
	}

	public Long getGcePackageId() {
		return gcePackageId;
	}

	public void setGcePackageId(Long gcePackageId) {
		this.gcePackageId = gcePackageId;
	}
	
	public Long getGceClusterId() {
		return gceClusterId;
	}

	public void setGceClusterId(Long gceClusterId) {
		this.gceClusterId = gceClusterId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getZabbixhosts() {
		return zabbixhosts;
	}

	public void setZabbixhosts(String zabbixhosts) {
		this.zabbixhosts = zabbixhosts;
	}

	public Byte getServiceType() {
		return serviceType;
	}

	public void setServiceType(Byte serviceType) {
		this.serviceType = serviceType;
	}

	public String getLogBindHostPort() {
		return logBindHostPort;
	}

	public void setLogBindHostPort(String logBindHostPort) {
		this.logBindHostPort = logBindHostPort;
	}
	
}
