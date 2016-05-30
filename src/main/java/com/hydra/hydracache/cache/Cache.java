package com.hydra.hydracache.cache;

import java.util.HashMap;

public class Cache {
	
	private IStrategy<Node> cacheStrategy = null;
	private HashMap<String, Node> kvMap = new HashMap<String, Node>();
	/**
	 * 默认使用LRU
	 */
	public Cache(){
		this.cacheStrategy = new LRUStrategy<Node>();
	}
	/**
	 * 指定使用的缓存淘汰算法
	 * @param alg
	 */
	public Cache(String alg){
		if(alg.toLowerCase().equals("lru")){
			this.cacheStrategy = new LRUStrategy< Node>();
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
	 * @param expire ms
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
			//
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
