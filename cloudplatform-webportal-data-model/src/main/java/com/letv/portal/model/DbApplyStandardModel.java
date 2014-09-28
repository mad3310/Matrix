package com.letv.portal.model;


/**Program Name: DbApplyStandardModel <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月12日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class DbApplyStandardModel extends BaseModel {
	
	
	private static final long serialVersionUID = 3933353876563424673L;
	
	private String applyCode;   //编码
	private String applyName;   //名称
	private String version;   //版本
	private String backupCycle;   //备份周期
	private String isEmailNotice;   //邮件通知:0:否 1 :是
	private String descn;   //描述
	private String maxConcurrency;   //最大并发量
	private String engineType;   //存储引擎类型   0:INNOB   1:ISMIY
	private String fromDbIp;   //原数据库ip
	private String fromDbPort;   //原数据库port
	private String fromDbName;   //原数据库name
	private String dataLimitIpList;   //数据访问限制ip列表 :   xxx.xxx.xxx.xxx~xxx.xxx.xxx.xxx
	private String mgrLimitIpList;   //管理访问限制ip列表:xxx.xxx.xxx.xxx~xxx.xxx.xxx.xxx
	private String readWriterRate;   //读写比例
	private String developLanguage;   //开发语言
	private String linkType;   //链接类型:    0:长链接   1:短链接
	private String maxQueriesPerHour;   //每小时最大查询数(用户可填,系统自动填充,管理员审核修改)
	private String maxUpdatesPerHour;   //每小时最大更新数(用户可填,系统自动填充,管理员审核修改)
	private String maxConnectionsPerHour;   //每小时最大连接数(用户可填,系统自动填充,管理员审核修改)
	private String maxUserConnections;   //用户最大链接数(用户可填,系统自动填充,管理员审核修改)
	private Long belongDb;   //所属db
	private String auditTime;   //审核时间
	private String auditUser;   //审核人
	private String auditInfo;   //审核信息
	private String clusterId;   //所属cluster

	private String status; //审核状态:    0:未审核1:审核通过2:审核不通过
	
	public DbApplyStandardModel(){}
	public DbApplyStandardModel(String status){
		this.status = status;
	}
	public DbApplyStandardModel(String status,String auditInfo){
		this.status = status;
		this.auditInfo = auditInfo;
	}
	
	public String getApplyCode() {
		return applyCode;
	}
	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBackupCycle() {
		return backupCycle;
	}
	public void setBackupCycle(String backupCycle) {
		this.backupCycle = backupCycle;
	}
	public String getIsEmailNotice() {
		return isEmailNotice;
	}
	public void setIsEmailNotice(String isEmailNotice) {
		this.isEmailNotice = isEmailNotice;
	}
	
	public String getDescn() {
		return descn;
	}
	public void setDescn(String descn) {
		this.descn = descn;
	}
	public String getMaxConcurrency() {
		return maxConcurrency;
	}
	public void setMaxConcurrency(String maxConcurrency) {
		this.maxConcurrency = maxConcurrency;
	}
	public String getEngineType() {
		return engineType;
	}
	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}
	public String getFromDbIp() {
		return fromDbIp;
	}
	public void setFromDbIp(String fromDbIp) {
		this.fromDbIp = fromDbIp;
	}
	public String getFromDbPort() {
		return fromDbPort;
	}
	public void setFromDbPort(String fromDbPort) {
		this.fromDbPort = fromDbPort;
	}
	public String getFromDbName() {
		return fromDbName;
	}
	public void setFromDbName(String fromDbName) {
		this.fromDbName = fromDbName;
	}
	public String getDataLimitIpList() {
		return dataLimitIpList;
	}
	public void setDataLimitIpList(String dataLimitIpList) {
		this.dataLimitIpList = dataLimitIpList;
	}
	public String getMgrLimitIpList() {
		return mgrLimitIpList;
	}
	public void setMgrLimitIpList(String mgrLimitIpList) {
		this.mgrLimitIpList = mgrLimitIpList;
	}
	public String getReadWriterRate() {
		return readWriterRate;
	}
	public void setReadWriterRate(String readWriterRate) {
		this.readWriterRate = readWriterRate;
	}
	public String getDevelopLanguage() {
		return developLanguage;
	}
	public void setDevelopLanguage(String developLanguage) {
		this.developLanguage = developLanguage;
	}
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	public String getMaxQueriesPerHour() {
		return maxQueriesPerHour;
	}
	public void setMaxQueriesPerHour(String maxQueriesPerHour) {
		this.maxQueriesPerHour = maxQueriesPerHour;
	}
	public String getMaxUpdatesPerHour() {
		return maxUpdatesPerHour;
	}
	public void setMaxUpdatesPerHour(String maxUpdatesPerHour) {
		this.maxUpdatesPerHour = maxUpdatesPerHour;
	}
	public String getMaxConnectionsPerHour() {
		return maxConnectionsPerHour;
	}
	public void setMaxConnectionsPerHour(String maxConnectionsPerHour) {
		this.maxConnectionsPerHour = maxConnectionsPerHour;
	}
	public String getMaxUserConnections() {
		return maxUserConnections;
	}
	public void setMaxUserConnections(String maxUserConnections) {
		this.maxUserConnections = maxUserConnections;
	}
	public Long getBelongDb() {
		return belongDb;
	}
	public void setBelongDb(Long belongDb) {
		this.belongDb = belongDb;
	}
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}
	public String getAuditUser() {
		return auditUser;
	}
	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}
	public String getAuditInfo() {
		return auditInfo;
	}
	public void setAuditInfo(String auditInfo) {
		this.auditInfo = auditInfo;
	}
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "DbApplyStandardModel [applyCode=" + applyCode
				+ ", applyName=" + applyName + ", version=" + version
				+ ", backupCycle=" + backupCycle + ", isEmailNotice="
				+ isEmailNotice + ", descn=" + descn + ", maxConcurrency="
				+ maxConcurrency + ", engineType=" + engineType + ", fromDbIp="
				+ fromDbIp + ", fromDbPort=" + fromDbPort + ", fromDbName="
				+ fromDbName + ", dataLimitIpList=" + dataLimitIpList
				+ ", mgrLimitIpList=" + mgrLimitIpList + ", readWriterRate="
				+ readWriterRate + ", developLanguage=" + developLanguage
				+ ", linkType=" + linkType + ", maxQueriesPerHour="
				+ maxQueriesPerHour + ", maxUpdatesPerHour="
				+ maxUpdatesPerHour + ", maxConnectionsPerHour="
				+ maxConnectionsPerHour + ", maxUserConnections="
				+ maxUserConnections + ", belongDb=" + belongDb
				+ ", auditTime=" + auditTime + ", auditUser=" + auditUser
				+ ", auditInfo=" + auditInfo + ", clusterId=" + clusterId
				+ "]";
	}
	
	
}
