package com.mogujie.trade.utils;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.route.rule.ShardingUtils;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.hander.MapperFactory.ShardingHanderEntry;

public class GroupDBTable {
	/**
	 * 按照db和table分组
	 */
	protected List<Object>[][] db_table;
	/**
	 * 最大分组（tables.size()*databases.size()）
	 */
	private int maxGroupSize;
	/**
	 * 计算分组后的values
	 */
	private List<List<Object>> list;
	private int position = -1;

	@SuppressWarnings("unchecked")
	public GroupDBTable(DataSourceRouting dataSourceRouting, List<?> values, ShardingHanderEntry entry)
			throws IllegalArgumentException, IllegalAccessException {
		maxGroupSize = dataSourceRouting.databases() * dataSourceRouting.tables();
		list = new ArrayList<>(maxGroupSize);
		db_table = new ArrayList[dataSourceRouting.databases()][dataSourceRouting.tables()];
		@SuppressWarnings("rawtypes")
		RouteRule rule = RouteRuleFactory.getRouteRule(entry.getMappedClass());
		for (Object val : values) {
			Object shardingVal = ShardingUtils.parserShardingValue(val, entry.getShardingParam().value(),rule);
			int db_index = rule.getDataSourceSuffix(entry.getRouting(), shardingVal);
			int table_index = rule.getTableSuffix(entry.getRouting(), shardingVal);
			if (db_table[db_index][table_index] == null) {
				db_table[db_index][table_index] = new ArrayList<>();
			}
			List<Object> list = db_table[db_index][table_index];
			list.add(val);
		}
		group();
	}

	/**
	 * 是否还有下一个元素
	 * @return
	 */
	public boolean hasNext() {
		return list.size() > position + 1;
	}

	/**
	 * 获取下一个元素
	 * @return
	 */
	public List<Object> next() {
		if (hasNext()) {
			return list.get(++position);
		}
		return null;
	}

	/**
	 * 重置偏移量
	 */
	public void reset() {
		position = -1;
	}

	/**
	 * 按照分库分表进行分组
	 */
	private void group() {
		for (int i = 0; i < db_table.length; i++) {
			for (int j = 0; j < db_table[i].length; j++) {
				if (db_table[i][j] != null) {
					list.add(db_table[i][j]);
				}
			}
		}
	}

	/**
	 * 获取分组后的Size
	 * @return
	 */
	public int getGroupSize() {
		return list.size();
	}

}