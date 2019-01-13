package com.mogujie.sharding.iterator;

public class TableIterator extends TableSuffixIterator {

	public TableIterator(Class<?> mapper) {
		super(mapper);
	}

	/**
	 * 获取sharding后的表名称
	 */
	@Override
	public String fillBit(int shardingSuffix) {
		String table = dataSourceRouting.table();
		return table + super.fillBit(shardingSuffix);
	}

}
