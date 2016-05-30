package com.hydra.hydracache.cache;

import java.util.LinkedList;

public class LRUStrategy<E> implements IStrategy<E> {
	
	private LinkedList<E> lruList = new LinkedList<E>();
	
	public void insert(E node) {
		// TODO Auto-generated method stub
		this.lruList.addFirst(node);
	}
	public void update(E node) {
		// TODO Auto-generated method stub
		//刚放问过，放到链表头部
		this.lruList.remove(node);
		this.lruList.addFirst(node);
	}
	
	public E delete() {
		// TODO Auto-generated method stub
		E node = this.lruList.getLast();
		this.lruList.removeLast();
		return node;
	}
	
	public void delete(E node) {
		// TODO Auto-generated method stub
		this.lruList.remove(node);
	}
	
	public void hitNode(E node) {
		// TODO Auto-generated method stub
	}
	
}
