/**   
*    
* Project：hydracache   
* Class Name: HydraCacheClient   
* Description：   
* Author：KDF5000
* Created Time：May 20, 2016 10:34:59 PM   
* @version        
*/
package com.hydra.hydracache.client.service;


public interface HydraCacheClient {
	
	/**
	 * create table 
	 * @param tableName
	 * @param families
	 */
	public void createTable(String tableName, String ...families);
	
	/**
	 * delete a table
	 * @param tableName
	 */
	public void deleteTable(String tableName);
	
	/**
	 * put key-value
	 * @param key
	 * @param value
	 */
	public void put(String tableName,String rowKey, String family, String columnName, String value);
	
	/**
	 * get value by key
	 * @param key
	 * @return <T> T
	 */
	public String get(String tableName, String rowKey, String family,String columnName);
	

}
