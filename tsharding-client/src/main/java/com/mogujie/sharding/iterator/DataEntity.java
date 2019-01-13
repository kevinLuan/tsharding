package com.mogujie.sharding.iterator;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.trade.db.DataSourceRouting;

public interface DataEntity {
	/**
	 * 获取sharding dataSource名称
	 * @return
	 */
	public String getDataSource();

	/**
	 * 获取master sharding dataSource名称
	 * @return
	 */
	public String getMasterDataSource();

	/**
	 * 获取slave sharding dataSource名称
	 * @return
	 */
	public String getSlaveDataSource();

	/**
	 * 获取sharding dataSource名称
	 * @return
	 */
	public String getDataSourceSuffix();

	/**
	 * 获取数据源路由配置
	 * @return
	 */
	public DataSourceRouting getDateSourceRouting();

	/**
	 * 获取路由规则
	 * @return
	 */
	public RouteRule<?> getRouteRule();

	/**
	 * 获取单个数据源对应的事物管理器名称
	 * @param shardingSuffix
	 * @return
	 */
	public String getTransactionManager();

	/**
	 * 获取多数据源链接事物管理器名称
	 * @param shardingSuffix
	 * @return
	 */
	public String getChaintTransactionManager();

}