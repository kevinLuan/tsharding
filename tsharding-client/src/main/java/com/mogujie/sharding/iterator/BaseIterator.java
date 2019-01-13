package com.mogujie.sharding.iterator;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.trade.db.DataSourceRouting;

public abstract class BaseIterator implements RouteIterator<String> {
	protected final Class<?> mapper;
	protected final RouteRule<?> routeRule;
	protected final DataSourceRouting dataSourceRouting;
	protected int pos = -1;

	public BaseIterator(Class<?> mapper) {
		this.mapper = mapper;
		this.routeRule = RouteRuleFactory.getRouteRule(this.mapper);
		dataSourceRouting = this.mapper.getAnnotation(DataSourceRouting.class);
	}

	public void reset() {
		this.pos = -1;
	}

	/**
	 * 获取Sharding value
	 * @return
	 */
	public abstract int getShardingValue();

	/**
	 * 填充规则数据
	 * @param shardingSuffix
	 * @return
	 */
	public abstract String fillBit(int shardingSuffix);

	@Override
	public boolean hasNext() {
		if (getShardingValue() > pos + 1) {
			return true;
		}
		return false;
	}

	@Override
	public String next() {
		if (hasNext()) {
			pos++;
			return this.fillBit(pos);
		}
		return null;
	}

}
