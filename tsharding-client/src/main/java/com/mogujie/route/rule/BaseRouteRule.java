package com.mogujie.route.rule;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * 基础路由规则实现，支持只分库或只分表实现，该该默认值支持Long类型Sharding参数，<Long>
 * 如果需要其他类型TSharding参数执行实现扩展。 <code>
 * <pre>
 * public class MyRule extends BaseRouteRule<String> {
 * 
 * public int getDataSourceSuffix(DataSourceRouting routing, String shardingVal) {
 * 		long val = calcCRC32(shardingVal) % routing.databases();
 *		return (int) val;
 *	}
 *
 *	public int getTableSuffix(DataSourceRouting routing, String shardingVal) {
 *		if (routing.tables() > 1) {
 *			long val = calcCRC32(shardingVal) % routing.tables();
 *			return (int) val;
 *		} else {
 *			return 0;
 *		}
 *	}
 *}
 * </code>
 * </pre>
 * 
 * @author SHOUSHEN LUAN
 *
 * @param <Long>
 */
public class BaseRouteRule<T> extends AbstractRouteRule<T> implements RouteRule<T> {
	/**
	 * 根据shardingSuffix填充完整库后缀
	 * 
	 * @param shardingTableSuffix
	 * @return
	 */
	public String fillDataSource(int shardingSuffix) {
		return ShardingUtils.fillBit(shardingSuffix, 4);
	}

	@Override
	public String fillBit(long shardingTableSuffix) {
		return String.valueOf(shardingTableSuffix);
	}

	/**
	 * 获取数据源后缀
	 * 
	 */
	@Override
	public int getDataSourceSuffix(DataSourceRouting routing, T shardingVal) {
		long crc32Val = calcCRC32(shardingVal);
		long res = crc32Val % routing.databases();
		return (int) res;
	}

	/**
	 * 计算数据源后缀
	 * 
	 * @param routing
	 * @param shardingVal
	 * @return
	 */
	protected final String calcSchemaNameSuffix(DataSourceRouting routing, T shardingVal) {
		if (routing.databases() > 1) {
			int shardingSuffix = getDataSourceSuffix(routing, shardingVal);
			return fillDataSource(shardingSuffix);
		} else {
			return "";
		}
	}

	/**
	 * 计算数据源名称
	 */
	public final String calculateSchemaName(DataSourceRouting routing, T shardingVal) {
		String schemaSuffix = this.calcSchemaNameSuffix(routing, shardingVal);
		return routing.dataSource() + schemaSuffix;
	}

	/**
	 * 计算表名称后缀
	 */
	@Override
	public String calculateTableNameSuffix(DataSourceRouting routing, T shardingVal) {
		return String.valueOf(getTableSuffix(routing, shardingVal));
	}

	@Override
	public String calculateTableName(DataSourceRouting routing, T shardingPara) {
		String tableSuffix = calculateTableNameSuffix(routing, shardingPara);
		return routing.table() + tableSuffix;
	}

	/**
	 * 如果没有分表则
	 */
	@Override
	public int getTableSuffix(DataSourceRouting routing, T shardingVal) {
		if (routing.tables() > 1) {
			long crc32Val = calcCRC32(shardingVal);
			long res = crc32Val % routing.tables();
			return (int) res;
		} else {
			return 0;// 没有分表返回0
		}

	}

}
