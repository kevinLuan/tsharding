package com.mogujie.route.rule;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * 根据sharding参数取余分库，分表数量获取到分库分表后缀，根据设置bit位长度不足补零
 * <p>
 * 路由规则：
 * </p>
 * <ul>
 * <li>路由dataSource: value%DataSourceRouting.database</li>
 * <li>获得DataSource:datasource0000~datasourceN-1</li>
 * <li>路由Table：value%DataSourceRouting.tables</li>
 * <li>获得Table table0000~tableN-1</li>
 * </ul>
 * @author SHOUSHEN LUAN
 */
public class SimpleRouteRule extends AbstractRouteRule<Long> implements RouteRule<Long> {
	/**
	 * 分表名称后缀固定长度，长度不足补零
	 */
	private final int tableBit = 4;
	/**
	 * 分库名称后缀固定长度，长度不足补零
	 */
	private final int schemaBit = 4;

	private String calcSchemaNameSuffix(DataSourceRouting routing, Long shardingVal) {
		int index = getDataSourceSuffix(routing, shardingVal);
		return ShardingUtils.fillBit(index, getSchemaBit());
	}

	@Override
	public String calculateSchemaName(DataSourceRouting routing, Long shardingVal) {
		String schemaSuffix = calcSchemaNameSuffix(routing, shardingVal);
		return routing.dataSource() + schemaSuffix;
	}

	/**
	 * 根据分片参数值计算分表名
	 * @param shardingPara
	 * @return 分表名0xxx
	 */
	@Override
	public String calculateTableNameSuffix(DataSourceRouting routing, Long shardingVal) {
		int index = getTableSuffix(routing, shardingVal);
		return ShardingUtils.fillBit(index, getTableBit());
	}

	/**
	 * 根据分片参数值计算分表名
	 * @param shardingPara
	 * @return 分表名0xxx
	 */
	@Override
	public String calculateTableName(DataSourceRouting routing, Long shardingPara) {
		String tableSuffix = calculateTableNameSuffix(routing, shardingPara);
		return routing.table() + tableSuffix;
	}

	public int getTableBit() {
		return tableBit;
	}

	public int getSchemaBit() {
		return schemaBit;
	}

	@Override
	public String fillBit(long shardingTableSuffix) {
		return ShardingUtils.fillBit(shardingTableSuffix, getTableBit());
	}

	@Override
	public int getDataSourceSuffix(DataSourceRouting routing, Long shardingVal) {
		if (routing.databases() > 1) {
			long val = shardingVal % routing.databases();
			return (int) val;
		}
		throw new IllegalArgumentException("注解实例的分库数量必须大于1");
	}

	@Override
	public int getTableSuffix(DataSourceRouting routing, Long shardingVal) {
		if (routing.tables() > 1) {
			long val = shardingVal % routing.tables();
			return (int) val;
		}else{
			return 0;
		}
	}

	public String fillDataSourceBit(long shardingDataSourceSuffix) {
		return ShardingUtils.fillBit(shardingDataSourceSuffix, getSchemaBit());
	}
}
