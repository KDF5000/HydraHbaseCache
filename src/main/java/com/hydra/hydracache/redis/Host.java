package com.hydra.hydracache.redis;

public class Host {
	
	public Host(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}
	
	private String hostName;
	private int port;
	
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	

}
