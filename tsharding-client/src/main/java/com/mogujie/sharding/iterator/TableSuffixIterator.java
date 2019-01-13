package com.mogujie.sharding.iterator;

public class TableSuffixIterator extends BaseIterator {

	public TableSuffixIterator(Class<?> mapper) {
		super(mapper);
	}

	@Override
	public int getShardingValue() {
		return super.dataSourceRouting.tables();
	}

	/**
	 * 获取sharding后的表后缀
	 */
	@Override
	public String fillBit(int shardingSuffix) {
		return routeRule.fillBit(shardingSuffix);
	}
}
