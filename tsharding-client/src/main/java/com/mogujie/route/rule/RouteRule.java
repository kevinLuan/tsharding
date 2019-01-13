package com.mogujie.route.rule;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * 路由规则
 * 
 * @author SHOUSHEN LUAN create date: 2017年1月6日
 */
public interface RouteRule<T> {
	/**
	 * 计算分库名称
	 * 
	 * @param routing
	 * @param shardingKey
	 * @return 返回分库的完成名称
	 */
	public String calculateSchemaName(DataSourceRouting routing, T shardingParam);

	/**
	 * 根据分片参数值计算分表后缀
	 * 
	 * @param shardingPara
	 * @return 返回分表的后缀
	 */
	public String calculateTableNameSuffix(DataSourceRouting routing, T shardingParam);

	/**
	 * 根据分片参数值计算分表名
	 * 
	 * @param shardingPara
	 * @return 返回分表的完整名称
	 */
	public String calculateTableName(DataSourceRouting routing, T shardingPara);

	/**
	 * 根据shardingTableSuffix填充完整表后缀
	 * 
	 * @param shardingTableSuffix
	 * @return
	 */
	String fillBit(long shardingTableSuffix);

	/**
	 * 根据shardingTableSuffix填充完整数据源后缀
	 * 
	 * @param shardingTableSuffix
	 * @return
	 */
	String fillDataSourceBit(long shardingDataSourceSuffix);

	/**
	 * 根据分库策略和sharding参数获取数据源后缀
	 * 
	 * @param routing
	 * @param shardingVal
	 * @return
	 */
	public int getDataSourceSuffix(DataSourceRouting routing, T shardingVal);

	/**
	 * 根据分表策略和sharding参数获取分表后缀
	 * 
	 * @param routing
	 * @param shardingVal
	 * @return
	 */
	public int getTableSuffix(DataSourceRouting routing, T shardingVal);

	/**
	 * 根据路由参数计算路由段落
	 * 
	 * @param routing
	 * @param routeParam
	 * @return
	 */
	public int getSegemation(DataSourceRouting routing, T routeParam);
}
