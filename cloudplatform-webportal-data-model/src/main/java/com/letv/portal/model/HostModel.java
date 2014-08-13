package com.letv.portal.model;


/**Program Name: HostModel <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月12日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class HostModel extends BaseModel {
	
	private static final long serialVersionUID = 3497965985607790962L;
	
	private String id;   //主键ID
	private String hostName; //主机名称
	private String hostIp; //主机ip
	private Integer nodesNumber; //节点个数
	private String hostModel; //主机型号
	private String cpu_model; //cpu型号
	private Integer coresNumber; //cpu核数
	private Integer memorySize; //内存大小
	private Integer diskSize; //磁盘大小
	private Integer diskUsed; //磁盘使用量

	private String status; //状态:0:关闭   1:正常
	private String isDeleted; //是否删除   0:无效 1:有效
	private String createTime;
	private String createUser;
	private String updateTime;
	private String updateUser;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public Integer getNodesNumber() {
		return nodesNumber;
	}
	public void setNodesNumber(Integer nodesNumber) {
		this.nodesNumber = nodesNumber;
	}
	public String getHostModel() {
		return hostModel;
	}
	public void setHostModel(String hostModel) {
		this.hostModel = hostModel;
	}
	public String getCpu_model() {
		return cpu_model;
	}
	public void setCpu_model(String cpu_model) {
		this.cpu_model = cpu_model;
	}
	public Integer getCoresNumber() {
		return coresNumber;
	}
	public void setCoresNumber(Integer coresNumber) {
		this.coresNumber = coresNumber;
	}
	public Integer getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(Integer memorySize) {
		this.memorySize = memorySize;
	}
	public Integer getDiskSize() {
		return diskSize;
	}
	public void setDiskSize(Integer diskSize) {
		this.diskSize = diskSize;
	}
	public Integer getDiskUsed() {
		return diskUsed;
	}
	public void setDiskUsed(Integer diskUsed) {
		this.diskUsed = diskUsed;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	@Override
	public String toString() {
		return "HostModel [id=" + id + ", hostName=" + hostName + ", hostIp="
				+ hostIp + ", nodesNumber=" + nodesNumber + ", hostModel="
				+ hostModel + ", cpu_model=" + cpu_model + ", coresNumber="
				+ coresNumber + ", memorySize=" + memorySize + ", diskSize="
				+ diskSize + ", diskUsed=" + diskUsed + ", status=" + status
				+ ", isDeleted=" + isDeleted + ", createTime=" + createTime
				+ ", createUser=" + createUser + ", updateTime=" + updateTime
				+ ", updateUser=" + updateUser + "]";
	}
	
	
	
	
	
}
