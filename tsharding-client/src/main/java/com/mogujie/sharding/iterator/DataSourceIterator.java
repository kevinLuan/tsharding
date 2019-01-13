package com.mogujie.sharding.iterator;

/**
 * dataSource迭代器
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月16日
 */
public class DataSourceIterator extends DataSourceSuffixIterator {

	public DataSourceIterator(Class<?> mapper) {
		super(mapper);
	}

	/**
	 * 获取dataSource名称
	 */
	@Override
	public String fillBit(int shardingSuffix) {
		return dataSourceRouting.dataSource() + super.fillBit(shardingSuffix);
	}

	/**
	 * 获取Master dataSource名称
	 * @param shardingSuffix
	 * @return
	 */
	public String getMasterDataSource(int shardingSuffix) {
		return this.fillBit(shardingSuffix) + "MasterDataSource";
	}

	/**
	 * 获取slave dataSource名称
	 * @param shardingSuffix
	 * @return
	 */
	public String getSlaveDataSource(int shardingSuffix) {
		return this.fillBit(shardingSuffix) + "SlaveDataSource";
	}

	/**
	 * 获取单个数据源对应的事物管理器名称
	 * @param shardingSuffix
	 * @return
	 */
	public String getTransactionManager(int shardingSuffix) {
		return this.fillBit(shardingSuffix) + "TransactionManager";
	}

	/**
	 * 获取多数据源链接事物管理器名称
	 * @param shardingSuffix
	 * @return
	 */
	public String getChaintTransactionManager() {
		return dataSourceRouting.dataSource() + "TransactionManager";
	}

}
