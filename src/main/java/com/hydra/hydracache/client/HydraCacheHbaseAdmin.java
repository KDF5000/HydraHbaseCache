package com.hydra.hydracache.client;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HydraCacheHbaseAdmin extends HBaseAdmin{

	public HydraCacheHbaseAdmin(Configuration c)
			throws MasterNotRunningException, ZooKeeperConnectionException,
			IOException {
		super(c);
		// TODO Auto-generated constructor stub
	}
	
	

}
