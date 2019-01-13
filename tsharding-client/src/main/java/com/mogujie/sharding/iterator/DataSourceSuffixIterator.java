package com.mogujie.sharding.iterator;

public class DataSourceSuffixIterator extends BaseIterator {

	public DataSourceSuffixIterator(Class<?> mapper) {
		super(mapper);
	}

	@Override
	public int getShardingValue() {
		return dataSourceRouting.databases();
	}

	/**
	 * 获取数据源sharding后缀
	 */
	@Override
	public String fillBit(int shardingSuffix) {
		return routeRule.fillDataSourceBit(shardingSuffix);
	}

}
