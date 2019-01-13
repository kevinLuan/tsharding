package com.mogujie.trade.db;

/**
 * @author by jiuru on 16/7/14.
 */
public enum DataSourceType {
	master, slave;

	public boolean isSlave() {
		return this == slave;
	}

	public boolean isMaster() {
		return this == master;
	}
}
