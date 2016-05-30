package com.hydra.hydracache.cache;

public interface IStrategy<E> {
	/**
	 * 插入一条数据
	 * @param node
	 */
	public void insert(E node);
	
	/**
	 * 更新数据
	 * @param node
	 */
	public void update(E node);
	
	/**
	 * 根据淘汰策略删除节点
	 * @param node
	 */
	public E delete();
	
	/**
	 * 删除指定节点
	 * @param node
	 */
	public void delete(E node);
	
	/**
	 * 访问到了某个节点
	 * @param node
	 */
	public void hitNode(E node);
	
}
