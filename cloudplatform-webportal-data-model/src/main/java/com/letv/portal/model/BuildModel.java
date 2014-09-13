package com.letv.portal.model;


/**Program Name: BuildModel <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月12日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class BuildModel extends BaseModel {
	
	private static final long serialVersionUID = 3290439855942720161L;
	
	private String id;   //主键ID
	private String mclusterId;
	private String dbId;
	private String step;
	private String status;
	private String code;
	private String msg;
	

	private String startTime;
	private String endTime;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getMclusterId() {
		return mclusterId;
	}


	public void setMclusterId(String mclusterId) {
		this.mclusterId = mclusterId;
	}


	public String getDbId() {
		return dbId;
	}


	public void setDbId(String dbId) {
		this.dbId = dbId;
	}


	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "BuildModel [id=" + id + ", mclusterId=" + mclusterId
				+ ", dbId=" + dbId + ", step=" + step + ", status=" + status
				+ ", code=" + code + ", msg=" + msg + ", startTime="
				+ startTime + ", endTime=" + endTime + "]";
	}
}
