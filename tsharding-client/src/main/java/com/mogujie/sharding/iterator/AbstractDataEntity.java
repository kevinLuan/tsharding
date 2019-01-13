package com.mogujie.sharding.iterator;

public abstract class AbstractDataEntity implements DataEntity {
	@Override
	public final String getMasterDataSource() {
		return this.getDataSource() + "MasterDataSource";
	}

	@Override
	public final String getSlaveDataSource() {
		return this.getDataSource() + "SlaveDataSource";
	}

	/**
	 * 获取单个数据源对应的事物管理器名称
	 * @param shardingSuffix
	 * @return
	 */
	public final String getTransactionManager() {
		return this.getDataSource() + "TransactionManager";
	}

	/**
	 * 获取多数据源链接事物管理器名称
	 * @param shardingSuffix
	 * @return
	 */
	public final String getChaintTransactionManager() {
		return this.getDateSourceRouting().dataSource() + "TransactionManager";
	}
}
