package com.letv.portal.model.monitor;

import java.io.Serializable;

public class ClusterModel extends BaseMonitor {
	
	private static final long serialVersionUID = -2759916694114126871L;

	private Meta meta;
	
	private Response response;
	
	public ClusterModel(){
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

	public static class Meta implements Serializable{
		private static final long serialVersionUID = -19989822112406773L;
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

	public static class Response implements Serializable{
		private static final long serialVersionUID = 8672169006272159381L;
		private Node node;
		private Cluster cluster;
		
		public Node getNode() {
			return node;
		}
		
		public void setNode(Node node) {
			this.node = node;
		}
		
		public Cluster getCluster() {
			return cluster;
		}
		
		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}
		
	}
	

	public static class Node implements Serializable{
		private static final long serialVersionUID = -7136064209257357126L;
		private NodeSize node_size;

		public NodeSize getNode_size() {
			return node_size;
		}

		public void setNode_size(NodeSize node_size) {
			this.node_size = node_size;
		}
	}
	public static class NodeSize implements Serializable{
		private static final long serialVersionUID = -8729738640152826300L;
		private String[] lost_ip;
		private String message;
		private String alarm;
		
		public String[] getLost_ip() {
			return lost_ip;
		}
		public void setLost_ip(String[] lost_ip) {
			this.lost_ip = lost_ip;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getAlarm() {
			return alarm;
		}
		public void setAlarm(String alarm) {
			this.alarm = alarm;
		}
		
	}
	public static class Cluster implements Serializable{
		private static final long serialVersionUID = -5434624806375187603L;
		private  Available cluster_available;

		public Available getCluster_available() {
			return cluster_available;
		}

		public void setCluster_available(Available cluster_available) {
			this.cluster_available = cluster_available;
		}
	}
	
	public static class Available implements Serializable{
		private static final long serialVersionUID = -8234192749703817491L;
		private String message; 
		private String alarm;
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getAlarm() {
			return alarm;
		}
		public void setAlarm(String alarm) {
			this.alarm = alarm;
		} 
	}
	
}
