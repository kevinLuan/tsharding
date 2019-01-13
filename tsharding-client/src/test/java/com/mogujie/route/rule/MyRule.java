package com.mogujie.route.rule;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * 实现记录的路由规则
 * 
 * @author SHOUSHEN LUAN
 *
 */
public class MyRule extends BaseRouteRule<Long> {
	@Override
	public int getDataSourceSuffix(DataSourceRouting routing, Long shardingVal) {
		long val = shardingVal % routing.databases();
		return (int) val;
	}

	@Override
	public int getTableSuffix(DataSourceRouting routing, Long shardingVal) {
		if (routing.tables() > 1) {
			long val = shardingVal % routing.tables();
			return (int) val;
		} else {
			return 0;
		}
	}
}
