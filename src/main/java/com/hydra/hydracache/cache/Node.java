package com.hydra.hydracache.cache;

public class Node {
	
	private String key; //key
	private String val; //val
	private long expeireTime; //过期时间
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public long getExpeireTime() {
		return expeireTime;
	}
	public void setExpeireTime(long expeireTime) {
		this.expeireTime = expeireTime;
	}
}
