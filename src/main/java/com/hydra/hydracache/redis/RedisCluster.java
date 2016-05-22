package com.hydra.hydracache.redis;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisCluster {
	
	private JedisCluster jcCluster = null;
	private List<HostAndPort> hostList = null;
	private Set<HostAndPort> jedisClusterNodes = null;
	
	public RedisCluster(List<HostAndPort> hosts){
		this.hostList = hosts;
		this.jedisClusterNodes = new HashSet<HostAndPort>();
		for(HostAndPort host: this.hostList){
			this.jedisClusterNodes.add(host);
		}
		this.jcCluster = new JedisCluster(this.jedisClusterNodes);
	}
	
	/**
	 * get kv
	 * @param key
	 * @return
	 */
	public String get(String key){
		if(this.jcCluster == null){
			System.out.println("please init cluster");
			return null;
		}
		return this.jcCluster.get(key);
	}
	
	/**
	 * set kv
	 * @param key
	 * @param value
	 */
	public void set(String key, String value){
		if(this.jcCluster == null){
			System.out.println("please init cluster");
			return ;
		}
		this.jcCluster.set(key, value);
	}
	
}
