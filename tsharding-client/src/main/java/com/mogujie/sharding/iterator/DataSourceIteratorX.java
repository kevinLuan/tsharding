package com.mogujie.sharding.iterator;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.trade.db.DataSourceRouting;

/**
 * 数据源迭代器扩展
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月17日
 */
public class DataSourceIteratorX implements RouteIterator<DataSourceEntity> {
	protected final Class<?> mapper;
	protected final RouteRule<?> routeRule;
	protected final DataSourceRouting dataSourceRouting;
	private int dataSourcePos = -1;

	public DataSourceIteratorX(Class<?> mapper) {
		this.mapper = mapper;
		this.routeRule = RouteRuleFactory.getRouteRule(this.mapper);
		dataSourceRouting = this.mapper.getAnnotation(DataSourceRouting.class);
	}

	public void reset() {
		this.dataSourcePos = -1;
	}

	public DataSourceEntity next() {
		if (hasNext()) {
			dataSourcePos++;
			return new DataSourceEntity(dataSourcePos, routeRule, dataSourceRouting);
		}
		throw new IllegalArgumentException("数据访问越界了.");
	}

	@Override
	public boolean hasNext() {
		if (dataSourceRouting.databases() > dataSourcePos + 1) {
			return true;
		}
		return false;
	}

}
