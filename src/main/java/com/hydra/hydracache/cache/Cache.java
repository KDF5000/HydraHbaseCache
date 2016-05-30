package com.hydra.hydracache.cache;

import java.util.HashMap;

public class Cache {
	
	private IStrategy<Node> cacheStrategy = null;
	private HashMap<String, Node> kvMap = new HashMap<String, Node>();
	private int cacheSize; //默认存储两千对kv
	private static int DEFAULT_CACHE_SIZE = 2000;
	
	/**
	 * 
	 * @param size
	 */
	public Cache(int size){
		this.cacheStrategy = new LRUStrategy<Node>();
		this.cacheSize = size;
	}
	
	/**
	 * 默认使用LRU
	 */
	public Cache(){
		this(Cache.DEFAULT_CACHE_SIZE);
	}
	/**
	 * 
	 * @param alg
	 */
	public Cache(String alg){
		this(alg, Cache.DEFAULT_CACHE_SIZE);
	}
	/**
	 * 指定使用的缓存淘汰算法
	 * @param alg
	 * @param size 所含节点大小，限制内存
	 */
	public Cache(String alg, int size){
		this.cacheSize = size;
		if(alg.toLowerCase().equals("lru")){
			this.cacheStrategy = new LRUStrategy<Node>();
		}else if(alg.toLowerCase().equals("lfu")){
			this.cacheStrategy = new LFUStrategy<Node>();
		}else{
			//..
			System.out.println("waiting");
			System.exit(0);
		}
	}
	/**
	 * 
	 * @param key
	 * @param val
	 * @param expire
	 */
	public void set(String key,String val, long expire){
		if(this.kvMap.containsKey(key)){
			//更新缓存
			Node node = this.kvMap.get(key);
			node.setVal(val);
			node.setExpeireTime(System.currentTimeMillis()+expire);
			this.kvMap.put(key, node);
			this.cacheStrategy.update(node);
		}else{
			Node node = new Node();
			node.setKey(key);
			node.setVal(val);
			node.setExpeireTime(System.currentTimeMillis()+expire);
			//如果达到了缓存大小，删除
			if(this.kvMap.keySet().size() >= this.cacheSize){
				Node delNode = this.cacheStrategy.delete();
				this.kvMap.remove(delNode.getKey());
			}
			this.kvMap.put(key, node);
			this.cacheStrategy.insert(node);
		}
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key){
		if(this.kvMap.containsKey(key)){
			if(this.kvMap.get(key).getExpeireTime() > System.currentTimeMillis()){
				this.cacheStrategy.hitNode(this.kvMap.get(key));
				return this.kvMap.get(key).getVal();
			}else{
				//说明过期，删除
				Node node = this.kvMap.get(key);
				this.kvMap.remove(key);
				this.cacheStrategy.delete(node);
				return null;
			}
		}else{
			return null;
		}
	}
	/**
	 * 
	 * @param key
	 */
	public void expire(String key){
		//
		if(this.kvMap.containsKey(key)){
			Node node = this.kvMap.get(key);
			this.kvMap.remove(key);
			this.cacheStrategy.delete(node);
		}
	}
}
