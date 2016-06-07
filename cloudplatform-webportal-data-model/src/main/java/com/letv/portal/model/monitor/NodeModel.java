package com.letv.portal.model.monitor;

import java.io.Serializable;

public class NodeModel extends BaseMonitor {
	
	private static final long serialVersionUID = 6043376043424618205L;

	private Meta meta;
	
	private Response response;
	
	public NodeModel(){
		super();
	}
	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public static class Meta implements Serializable {
		private static final long serialVersionUID = -6637659355252428782L;
		
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

	public static class Response implements Serializable{
		private static final long serialVersionUID = 42187492989168080L;
		
		private Node node;
		private Db db;
		public Node getNode() {
			return node;
		}
		public void setNode(Node node) {
			this.node = node;
		}
		public Db getDb() {
			return db;
		}
		public void setDb(Db db) {
			this.db = db;
		}
		
	}
	

	public static class Node implements Serializable{
		private static final long serialVersionUID = 2706770940539382989L;
		private DetailModel log_warning;
		private DetailModel log_health;
		private DetailModel log_error;
		private DetailModel started;
		
		public DetailModel getLog_warning() {
			return log_warning;
		}
		public void setLog_warning(DetailModel log_warning) {
			this.log_warning = log_warning;
		}
		public DetailModel getLog_health() {
			return log_health;
		}
		public void setLog_health(DetailModel log_health) {
			this.log_health = log_health;
		}
		public DetailModel getLog_error() {
			return log_error;
		}
		public void setLog_error(DetailModel log_error) {
			this.log_error = log_error;
		}
		public DetailModel getStarted() {
			return started;
		}
		public void setStarted(DetailModel started) {
			this.started = started;
		}
		
	}
	
	public static class Db implements Serializable{
		private static final long serialVersionUID = -4759722577027187402L;
		private DetailModel existed_db_anti_item;
		private DetailModel cur_conns;
		private DetailModel wsrep_status;
		private DetailModel write_read_avaliable;
		private DetailModel dbuser;
		private DetailModel backup;
		private DetailModel cur_user_conns;
		
		public DetailModel getExisted_db_anti_item() {
			return existed_db_anti_item;
		}
		public void setExisted_db_anti_item(DetailModel existed_db_anti_item) {
			this.existed_db_anti_item = existed_db_anti_item;
		}
		public DetailModel getCur_conns() {
			return cur_conns;
		}
		public void setCur_conns(DetailModel cur_conns) {
			this.cur_conns = cur_conns;
		}
		public DetailModel getWsrep_status() {
			return wsrep_status;
		}
		public void setWsrep_status(DetailModel wsrep_status) {
			this.wsrep_status = wsrep_status;
		}
		public DetailModel getWrite_read_avaliable() {
			return write_read_avaliable;
		}
		public void setWrite_read_avaliable(DetailModel write_read_avaliable) {
			this.write_read_avaliable = write_read_avaliable;
		}
		public DetailModel getDbuser() {
			return dbuser;
		}
		public void setDbuser(DetailModel dbuser) {
			this.dbuser = dbuser;
		}
		public DetailModel getBackup() {
			return backup;
		}
		public void setBackup(DetailModel backup) {
			this.backup = backup;
		}
		public DetailModel getCur_user_conns() {
			return cur_user_conns;
		}
		public void setCur_user_conns(DetailModel cur_user_conns) {
			this.cur_user_conns = cur_user_conns;
		}
		
	}
	
	public static class DetailModel implements Serializable {
		private static final long serialVersionUID = -6595878731277685775L;
		private String alarm;
		private String message;
		private Object error_record;
		private String ctime;
		private int timeout_num;
		
		public String getAlarm() {
			return alarm;
		}
		public void setAlarm(String alarm) {
			this.alarm = alarm;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getCtime() {
			return ctime;
		}
		public void setCtime(String ctime) {
			this.ctime = ctime;
		}
		public Object getError_record() {
			return error_record;
		}
		public void setError_record(Object error_record) {
			this.error_record = error_record;
		}
		public int getTimeout_num() {
			return timeout_num;
		}
		public void setTimeout_num(int timeout_num) {
			this.timeout_num = timeout_num;
		}
		
	}
	
	
}
