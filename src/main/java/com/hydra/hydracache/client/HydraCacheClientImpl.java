package com.hydra.hydracache.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

import redis.clients.jedis.HostAndPort;

import com.hydra.hydracache.client.service.HydraCacheClient;
import com.hydra.hydracache.redis.RedisCluster;

public class HydraCacheClientImpl implements HydraCacheClient{
	
	private static Configuration conf = null;
	private RedisCluster redisCluster = null;
	
	static{
		conf = HBaseConfiguration.create();
//		conf.set("hbase.zookeeper.quorum", "localhost");
	}
	
	public HydraCacheClientImpl(){
		initRedisCluster();
	}
	/**
	 * init redis cluster 
	 */
	private void initRedisCluster(){
		List<HostAndPort> list = new ArrayList<HostAndPort>();
		list.add(new HostAndPort("127.0.0.1", 7000));
		list.add(new HostAndPort("127.0.0.1", 7001));
		this.redisCluster = new RedisCluster(list);
	}
	
	/**
	 * create table
	 * @param tableName string
	 * @param families string
	 */
	public void createTable(String tableName, String... families) {
		// TODO Auto-generated method stub
		HydraCacheHbaseAdmin admin = null;
	    try {
	        admin = new HydraCacheHbaseAdmin(conf);
	        HTableDescriptor tableDescriptor = new HydraHTableDescriptor(TableName.valueOf(tableName));
	        
	        if (admin.tableExists(tableName)) {
	        	// delete the table if exists
	        	System.out.println(tableName + "already exists, it will be deleted！");
	        	admin.disableTable(tableName);
	        	admin.deleteTable(tableName);
	        } 
            for(String family :families){
                tableDescriptor.addFamily(new HColumnDescriptor(family));
            }
            admin.createTable(tableDescriptor);
            System.out.println("Create successfully!");
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 
	}

	/**
	 * delete a table
	 * @param tableName
	 */
	public void deleteTable(String tableName) {
		// TODO Auto-generated method stub
		HydraCacheHbaseAdmin admin = null;
	    try {
	        admin = new HydraCacheHbaseAdmin(conf);
	        
	        if (admin.tableExists(tableName)) {
	        	// delete the table if exists
	        	admin.disableTable(tableName);
	        	admin.deleteTable(tableName);
	        	System.out.println("delete successfully!");
	        } 
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 
	}

	/**
	 * put key-value
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void put(String tableName, String rowKey, String family,
			String columnName, String value) {
		// TODO Auto-generated method stub
		Table table = null;
		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(HydraCacheClientImpl.conf);
			table = connection.getTable(TableName.valueOf(tableName));
			Put p = new Put(rowKey.getBytes());
			p.addColumn(family.getBytes(), columnName.getBytes(), value.getBytes());
			table.put(p);
			System.out.println("Put value: "+ value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(table!=null){
				try {
					table.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(connection!=null){
				try {
					connection.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
		
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getDataFromCache(String key){
     	//tableName+"_"+rowKey+"_"+family+"_"+columnName
		String val = null;
		if(this.redisCluster == null){
			System.out.println("Didn`t init redis cluster!");
		}else{
			val = this.redisCluster.get(key);
		}
		return val;
	}
	/**
	 * 
	 * @param key
	 * @param val
	 */
	private void setData2Cache(String key, String val){
		if(this.redisCluster == null){
			System.out.println("Didn`t init redis cluster!");
		}else{
			this.redisCluster.set(key, val);
		}
	}
	/**
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param columnName
	 * @return
	 */
	public String get(String tableName, String rowKey, String family,
			String columnName) {
		// TODO Auto-generated method stub
		//先判断缓存里有没有
		String key = tableName+"_"+rowKey+"_"+family+"_"+columnName;
		String valString = getDataFromCache(key);
		if(valString != null){
			System.out.println("load data from cache!");
			return valString;
		}
		System.out.println("load data from hbase!");
		Table table = null;
		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(HydraCacheClientImpl.conf);
			table = connection.getTable(TableName.valueOf(tableName));
			Get g = new Get(rowKey.getBytes());
			g.addColumn(family.getBytes(), columnName.getBytes());
			Result result = table.get(g);
			byte[] bytes = result.getValue(family.getBytes(), columnName.getBytes());
			String valueStr = new String(bytes);
			System.out.println("get value: "+valueStr);
			//set cache
			this.setData2Cache(key, valueStr);
			return valueStr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(table!=null){
				try {
					table.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(connection !=null){
				try {
					connection.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
