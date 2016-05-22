package com.hydra.hydracache.client;

import com.hydra.hydracache.client.service.HydraCacheClient;

import junit.framework.TestCase;

public class HydraCacheClientImplTest extends TestCase {
	
	public void testPut() {
		HydraCacheClient client = new HydraCacheClientImpl();
//		client.createTable("test", "cf");
//		client.put("test", "abc", "cf", "hello1", "world");
		long startTime = System.currentTimeMillis();
		String valString = client.get("test", "abc", "cf", "hello1");
		long endTime = System.currentTimeMillis();
		System.out.println("Time:"+(endTime-startTime));
		System.out.println(valString);
	}

}
