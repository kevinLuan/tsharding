package com.mogujie.route.rule;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * <b>根据CRC32算法实现路由</b>
 * <p>
 * 路由规则：
 * </p>
 * <ul>
 * <li>路由dataSource:
 * (CRC32(value)%(DataSourceRouting.tables*DataSourceRouting.database))/DataSourceRouting.tables</li>
 * <li>获得DataSource:datasource0~datasourceN-1</li>
 * <li>路由Table：CRC32(value)%DataSourceRouting.tables</li>
 * <li>获得Table:table0~tableN-1</li>
 * </ul>
 *
 * @author SHOUSHEN LUAN
 */
public class CRC32RouteRuleOpenOrderInfo extends AbstractRouteRule<String> implements RouteRule<String> {

	protected String caculateSchemaNameSuffix(DataSourceRouting routing, String shardingKey) {
		if (routing.databases() > 1) {
			long crc32Val = calcCRC32(shardingKey);
			long res = (crc32Val % (routing.tables() * routing.databases())) / 2048;

			return String.valueOf(res);
		}
		throw new IllegalArgumentException("注解实例的分库数量必须大于1");
	}

	@Override
	public String calculateSchemaName(DataSourceRouting routing, String shardingKey) {
		String schemaSuffix = this.caculateSchemaNameSuffix(routing, shardingKey);
		return routing.dataSource() + schemaSuffix;
	}

	@Override
	public String calculateTableNameSuffix(DataSourceRouting routing, String shardingPara) {
		if (routing.tables() > 1) {
			long crc32Val = calcCRC32(shardingPara);
			long res = crc32Val % routing.tables();
			return String.valueOf(res);
		}
		throw new IllegalArgumentException("注解实例的分表数量必须大于1");
	}

	@Override
	public String calculateTableName(DataSourceRouting routing, String shardingPara) {
		String tableSuffix = calculateTableNameSuffix(routing, shardingPara);
		return routing.table() + tableSuffix;
	}

	@Override
	public String fillBit(long shardingTableSuffix) {
		return String.valueOf(shardingTableSuffix);
	}

	@Override
	public int getDataSourceSuffix(DataSourceRouting routing, String shardingVal) {
		if (routing.databases() > 1) {
			long crc32Val = calcCRC32(shardingVal);
			long res = (crc32Val % (routing.tables() * routing.databases())) / 2048;

			return (int) res;
		}
		throw new IllegalArgumentException("注解实例的分库数量必须大于1");
	}

	@Override
	public int getTableSuffix(DataSourceRouting routing, String shardingVal) {
		if (routing.tables() > 1) {
			long crc32Val = calcCRC32(shardingVal);
			long res = crc32Val % routing.tables();
			return (int) res;
		}else{
			return 0;
		}
	}

}
