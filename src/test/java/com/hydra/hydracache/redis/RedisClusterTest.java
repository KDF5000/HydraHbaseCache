package com.hydra.hydracache.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.HostAndPort;
import junit.framework.TestCase;

public class RedisClusterTest extends TestCase {
	
	public void testPut(){
		List<HostAndPort> list = new ArrayList<HostAndPort>();
		list.add(new HostAndPort("127.0.0.1", 7000));
		list.add(new HostAndPort("127.0.0.1", 7001));
		RedisCluster redisCluster = new RedisCluster(list);
		redisCluster.set("hello1", "world", 5);
		System.out.println(redisCluster.get("hello1"));
	}
}
