package com.hydra.hydracache.client;

import java.io.IOException;
import java.util.List;
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

import com.hydra.hydracache.cache.Cache;
import com.hydra.hydracache.client.service.HydraCacheClient;
import com.hydra.hydracache.redis.RedisCluster;

public class HydraCacheClientImpl implements HydraCacheClient{
	
	private static Configuration conf = null;
	private RedisCluster redisCluster = null;
	private boolean cacheOn = false; //是否使用缓存
	private boolean localCacheOn = false;//本地缓存
	private Cache localCache = null; //本地in memory cache
	private static int DEFAULT_EXPIRE_TIME = 3600;// 1 hour
	
	static{
		conf = HBaseConfiguration.create();
//		conf.set("hbase.zookeeper.quorum", "localhost");
	}
	
	/**
	 * 添加配置
	 * @param name
	 * @param value
	 */
	public void setConf(String name, String value){
		if(HydraCacheClientImpl.conf!=null){
			HydraCacheClientImpl.conf.set(name, value);
		}
	}
	
	public HydraCacheClientImpl(){
	}
	
	/**
	 * 
	 * @param cacheOn boolean 是否使用缓存
	 * @param list　如果使用缓存请提供redis节点的地址和端口，否则置null即可
	 */
	public HydraCacheClientImpl(boolean cacheOn, List<HostAndPort> list){
		this.cacheOn = cacheOn;
		if(this.cacheOn){
			this.initRedisCluster(list);
		}
	}
	/**
	 * 
	 * @param cacheOn
	 * @param list
	 * @param secondaryCacheOn
	 */
	public HydraCacheClientImpl(boolean cacheOn, List<HostAndPort> list, boolean localCacheOn){
		this.cacheOn = cacheOn;
		this.localCacheOn = localCacheOn;
		
		//redis分布式缓存
		if(this.cacheOn){
			this.initRedisCluster(list);
		}
		//二级缓存
		if(this.localCacheOn){
			this.localCache = new Cache();
		}
	}
	
	/**
	 * init redis cluster 
	 */
	private void initRedisCluster(List<HostAndPort> list){
		if(list == null){
			System.err.println("请制定redis节点的地址和端口");
			System.exit(0);
		}
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
		if(this.localCacheOn && this.localCache != null){
			val = this.localCache.get(key);
			if(val !=null){
				System.out.println(">>Load data from localCache!");
				return val;
			}
		}
		if(this.cacheOn == true && this.redisCluster != null){
			val = this.redisCluster.get(key);
			System.out.println(">>Load data from Redis!");
		}
		return val;
	}
	/**
	 * 
	 * @param key
	 * @param val
	 * @param expire int seconds
	 */
	private void setData2Cache(String key, String val, int expire){
		if(this.localCacheOn == true){
			this.localCache.set(key, val, expire);
		}
		if(this.cacheOn == true && this.redisCluster != null){
			System.out.println(">>set data to redis!");
			this.redisCluster.set(key, val, expire);
		}
	}
	
	/**
	 * 使用默认过期时间
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param columnName
	 * @return
	 */
	public String get(String tableName, String rowKey, String family,
			String columnName){
		return this.get(tableName, rowKey, family, columnName, HydraCacheClientImpl.DEFAULT_EXPIRE_TIME);
	}
	/**
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param columnName
	 * @param expireTime
	 * @return
	 */
	public String get(String tableName, String rowKey, String family,
			String columnName, int expireTime) {
		// TODO Auto-generated method stub
		//先判断缓存里有没有
		String key = tableName+"_"+rowKey+"_"+family+"_"+columnName;
		String valString = getDataFromCache(key);
		if(valString != null){
			return valString;
		}
		
		System.out.println(">>Load data from hbase!");
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
			if(valueStr != null){
				this.setData2Cache(key, valueStr, expireTime);
			}
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
