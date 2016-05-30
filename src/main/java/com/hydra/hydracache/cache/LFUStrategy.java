package com.hydra.hydracache.cache;

import java.util.LinkedList;

class LfuNode<E>{
	public E node;
	public int count; //访问次数
}

public class LFUStrategy<E> implements IStrategy<E> {
	
	
	private LinkedList<LfuNode<E>> lfuList = new LinkedList<LfuNode<E>>();
	
	public void insert(E node) {
		// TODO Auto-generated method stub
		LfuNode<E> lfuNode = new LfuNode<E>();
		lfuNode.count = 1;
		lfuNode.node = node;
		this.lfuList.add(lfuNode);
	}

	public void update(E node) {
		// TODO Auto-generated method stub
		for(int index =0; index < this.lfuList.size(); index++){
			LfuNode<E> lfuNode = this.lfuList.get(index);
			if(lfuNode.node == node){
				lfuNode.count++;
				this.lfuList.set(index, lfuNode);
				return;
			}
		}
	}

	public E delete() {
		// TODO Auto-generated method stub
		int minCount = Integer.MAX_VALUE;
		int minCountIndex = 0;
		E minNode = null;
		for(int index =0; index < this.lfuList.size(); index++){
			LfuNode<E> lfuNode = this.lfuList.get(index);
			if(lfuNode.count < minCount){
				minCount = lfuNode.count;
				minCountIndex = index;
				minNode = lfuNode.node;
			}
		}
		this.lfuList.remove(minCountIndex);
		return minNode;
	}

	public void delete(E node) {
		// TODO Auto-generated method stub
		for(int index =0; index < this.lfuList.size(); index++){
			LfuNode<E> lfuNode = this.lfuList.get(index);
			if(lfuNode.node == node){
				this.lfuList.remove(index);
				return;
			}
		}
	}

	public void hitNode(E node) {
		// TODO Auto-generated method stub
		for(int index =0; index < this.lfuList.size(); index++){
			LfuNode<E> lfuNode = this.lfuList.get(index);
			if(lfuNode.node == node){
				lfuNode.count++;
				this.lfuList.set(index, lfuNode);
				return;
			}
		}
	}

}
