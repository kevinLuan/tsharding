package com.mogujie.trade.hander;

import java.lang.reflect.Method;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.route.rule.ShardingUtils;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.hander.MapperFactory.ShardingHanderEntry;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;

/**
 * @CreateTime 2016年8月6日 上午9:15:19
 * @author SHOUSHEN LUAN
 */
public class DefaultShardingHandler implements ShardingHandler {
	private final DataSourceRouting routing;
	private final ShardingParam shardingParam;
	// sharding原始参数
	private final Object rawShardingValue;
	private final Object value;// sharding value
	private final Class<?> mappendClass;
	private final ShardingHanderEntry entry;

	public DefaultShardingHandler(Class<?> mappendClass, Method method, Object[] args) throws IllegalAccessException {
		entry = MapperFactory.getShardingHanderEntry(mappendClass, method);
		this.routing = entry.getRouting();
		this.mappendClass = mappendClass;
		this.shardingParam = entry.getShardingParam();
		this.rawShardingValue = entry.getRouteParam(args);
		RouteRule<Object> rule = RouteRuleFactory.getRouteRule(mappendClass);
		value = ShardingUtils.parserShardingValue(rawShardingValue, shardingParam.value(), rule);
	}

	@Override
	public String schemaName() {
		try {
			if (routing.databases() > 1) {
				Object value = getShardingValue();
				RouteRule<Object> sharding = RouteRuleFactory.getRouteRule(mappendClass);
				return sharding.calculateSchemaName(routing, value);
			} else {
				return routing.dataSource();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTableNameSuffix() {
		try {
			if (routing.tables() > 1) {
				Object value = getShardingValue();
				RouteRule<Object> sharding = RouteRuleFactory.getRouteRule(mappendClass);
				return sharding.calculateTableNameSuffix(routing, value);
			} else {
				return "0";
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getShardingValue() throws IllegalArgumentException, IllegalAccessException {
		return this.value;
	}

}
