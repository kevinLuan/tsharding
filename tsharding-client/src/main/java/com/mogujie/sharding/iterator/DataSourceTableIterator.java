package com.mogujie.sharding.iterator;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.trade.db.DataSourceRouting;

/**
 * 数据库+表.迭代器
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月16日
 */
public class DataSourceTableIterator implements RouteIterator<DataSourceTableEntity> {

	protected final Class<?> mapper;
	protected final RouteRule<?> routeRule;
	protected final DataSourceRouting dataSourceRouting;
	private int dataSourcePos = -1;
	private int tablePos = -1;
	private int pos = -1;

	public DataSourceTableIterator(Class<?> mapper) {
		this.mapper = mapper;
		this.routeRule = RouteRuleFactory.getRouteRule(this.mapper);
		dataSourceRouting = this.mapper.getAnnotation(DataSourceRouting.class);
	}

	public void reset() {
		this.dataSourcePos = -1;
		this.tablePos = -1;
	}

	public DataSourceTableEntity next() {
		if (hasNext()) {
			pos++;
			if (tablePos == -1 || dataSourcePos == -1) {
				dataSourcePos = 0;
				tablePos = 0;
				return new DataSourceTableEntity(dataSourcePos, tablePos, routeRule, dataSourceRouting);
			} else {
				tablePos++;
				if (dataSourceRouting.tables() > tablePos) {
					return new DataSourceTableEntity(dataSourcePos, tablePos, routeRule, dataSourceRouting);
				} else {
					dataSourcePos++;
					tablePos = 0;
					if (dataSourceRouting.databases() > dataSourcePos) {
						return new DataSourceTableEntity(dataSourcePos, tablePos, routeRule, dataSourceRouting);
					}
				}
			}
		}
		throw new IllegalArgumentException("数据访问越界了.");
	}

	@Override
	public boolean hasNext() {
		if (dataSourceRouting.tables() * dataSourceRouting.databases() > pos + 1) {
			return true;
		}
		return false;
	}

}
