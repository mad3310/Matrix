package com.letv.portal.model;


/**Program Name: ContainerModel <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月12日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class ContainerModel extends BaseModel {
	
	private static final long serialVersionUID = 4029730587083735122L;
	
	private String containerName; //节点名称  
	private String mountDir; //挂载路径
	private String zookeeperId;
	private String ipAddr; //节点ip  
	
	private String gateAddr; //网关
	private String ipMask; //子网掩码
	private String clusterNodeName; //cluster名称
	private String assignName; 
	private String originName;
	private String type; // VIP or normal
	
	
	private Integer diskSize; //磁盘大小
	private Integer coresNumber; //cpu内核数
	private Integer cpuSpeed; //cpu速度 
	private Integer memorySize; //内存大小
	
	private String hostId;  //所属host
	private String clusterId; //所属cluster
	
	private String status; //状态:0:停止  1:正常
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
	public String getClusterNodeName() {
		return clusterNodeName;
	}
	public void setClusterNodeName(String clusterNodeName) {
		this.clusterNodeName = clusterNodeName;
	}
	public String getAssignName() {
		return assignName;
	}
	public void setAssignName(String assignName) {
		this.assignName = assignName;
	}
	public String getOriginName() {
		return originName;
	}
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "ContainerModel [containerName=" + containerName
				+ ", mountDir=" + mountDir + ", zookeeperId=" + zookeeperId
				+ ", ipAddr=" + ipAddr + ", gateAddr=" + gateAddr + ", ipMask="
				+ ipMask + ", clusterNodeName=" + clusterNodeName
				+ ", assignName=" + assignName + ", originName=" + originName
				+ ", type=" + type + ", diskSize=" + diskSize
				+ ", coresNumber=" + coresNumber + ", cpuSpeed=" + cpuSpeed
				+ ", memorySize=" + memorySize + ", hostId=" + hostId
				+ ", clusterId=" + clusterId + ", status=" + status
				+ "]";
	}
	
}
