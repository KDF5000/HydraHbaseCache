package com.hydra.hydracache.client;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.HostAndPort;

import com.hydra.hydracache.client.service.HydraCacheClient;
import com.sun.jersey.api.client.Client;

import junit.framework.TestCase;

public class HydraCacheClientImplTest extends TestCase {
	
	private static HydraCacheClientImpl client = null;
	
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		//创建表输入数据
		client = new HydraCacheClientImpl();
		client.createTable("test", "cf");
		client.put("test", "abc", "cf", "hello", "world");
	}

	/**
	 * 
	 */
	private void readProcess(){
		//第一次读取没有命中，放到缓存里
		long startTime = System.currentTimeMillis();
		String valString = client.get("test", "abc", "cf", "hello", 20);
		long endTime = System.currentTimeMillis();
		System.out.println("First Read Time:"+(endTime-startTime));
		System.out.println(valString);
		
		startTime = System.currentTimeMillis();
		valString = client.get("test", "abc", "cf", "hello", 20);
		endTime = System.currentTimeMillis();
		System.out.println("Second Read Time:"+(endTime-startTime));
		System.out.println(valString);
	}
	
/*	public void testWiteAndRead(){
		long startTime = System.currentTimeMillis();
		String valString = client.get("test", "abc", "cf", "hello", 20);
		long endTime = System.currentTimeMillis();
		System.out.println("First Read Time:"+(endTime-startTime));
		System.out.println(valString);
	}
	
	public void testRedis() {
		List<HostAndPort> list = new ArrayList<HostAndPort>();
		list.add(new HostAndPort("127.0.0.1", 7000));
		list.add(new HostAndPort("127.0.0.1", 7001));
		client = new HydraCacheClientImpl(true, list);
	
		readProcess();
		
	}
	*/
	public void testLocalCache(){
		client = new HydraCacheClientImpl(false, null, true);
		
		readProcess();
	}
}
