package com.hydra.hydracache.client;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.HostAndPort;

import com.hydra.hydracache.client.service.HydraCacheClient;

import junit.framework.TestCase;

public class HydraCacheClientImplTest extends TestCase {
	
	
	
//	public void testRedis() {
//		List<HostAndPort> list = new ArrayList<HostAndPort>();
//		list.add(new HostAndPort("127.0.0.1", 7000));
//		list.add(new HostAndPort("127.0.0.1", 7001));
//		HydraCacheClient client = new HydraCacheClientImpl(true, list);
////		client.createTable("test", "cf");
////		client.put("test", "abc", "cf", "hello1", "world1");
//		
//		long startTime = System.currentTimeMillis();
//		String valString = client.get("test", "abc", "cf", "hello1");
//		long endTime = System.currentTimeMillis();
//		System.out.println("Time:"+(endTime-startTime));
//		System.out.println(valString);
//	}
	
	public void testLocalCache(){
		HydraCacheClient client = new HydraCacheClientImpl(false, null,true);
		client.createTable("test", "cf");
		client.put("test", "abc", "cf", "hello2", "world2");
		
		long startTime = System.currentTimeMillis();
		String valString = client.get("test", "abc", "cf", "hello2");
		long endTime = System.currentTimeMillis();
		System.out.println("First Time:"+(endTime-startTime));
		
		startTime = System.currentTimeMillis();
		valString = client.get("test", "abc", "cf", "hello2");
		endTime = System.currentTimeMillis();
		System.out.println("Second Time:"+(endTime-startTime));
		
		System.out.println(valString);
	}

}
