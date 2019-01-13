package com.mogujie.sharding.iterator;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.trade.db.DataSourceRouting;

public class DataSourceTableEntity extends AbstractDataEntity {
	/***
	 * 表后缀
	 */
	private final int tableSuffix;
	/**
	 * 数据源后缀
	 */
	private final int dataSourceSuffix;
	/**
	 * 当前路由规则
	 */
	private final RouteRule<?> routeRule;
	/**
	 * 数据源配置
	 */
	private final DataSourceRouting dataSourceRouting;

	public DataSourceTableEntity(int dataSourceSuffix, int tableSuffix, RouteRule<?> routeRule,
			DataSourceRouting dataSourceRouting) {
		this.dataSourceSuffix = dataSourceSuffix;
		this.tableSuffix = tableSuffix;
		this.routeRule = routeRule;
		this.dataSourceRouting = dataSourceRouting;
	}

	/**
	 * 获取sharding dataSource名称
	 * @return
	 */
	public String getDataSource() {
		String dataSource = dataSourceRouting.dataSource();
		String suffix = routeRule.fillDataSourceBit(dataSourceSuffix);
		return dataSource + suffix;
	}

	/**
	 * 获取sharding dataSource名称
	 * @return
	 */
	public String getDataSourceSuffix() {
		return routeRule.fillDataSourceBit(dataSourceSuffix);
	}

	/**
	 * 获取sharding表名称
	 * @return
	 */
	public String getTable() {
		String table = dataSourceRouting.table();
		String suffix = routeRule.fillBit(tableSuffix);
		return table + suffix;
	}

	/**
	 * 获取表后缀
	 * @return
	 */
	public String getTableSuffix() {
		return routeRule.fillBit(tableSuffix);
	}

	/**
	 * 获取数据源路由配置
	 * @return
	 */
	public DataSourceRouting getDateSourceRouting() {
		return dataSourceRouting;
	}

	/**
	 * 获取路由规则
	 * @return
	 */
	public RouteRule<?> getRouteRule() {
		return routeRule;
	}
}