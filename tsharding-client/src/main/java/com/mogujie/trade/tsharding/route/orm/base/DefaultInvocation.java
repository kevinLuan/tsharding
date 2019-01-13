package com.mogujie.trade.tsharding.route.orm.base;

import java.lang.reflect.Method;

import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.hander.MapperFactory;
import com.mogujie.trade.hander.MapperFactory.ShardingHanderEntry;

public final class DefaultInvocation implements Invocation {
	private final Invocation delegation;

	public DefaultInvocation(Class<?> clazz, Method method, Object[] args) throws IllegalAccessException,
			IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, SecurityException {
		ShardingHanderEntry shardingHanderEntry = MapperFactory.getShardingHanderEntry(clazz, method);
		if (shardingHanderEntry.isSharding()) {
			this.delegation = new ShardingInvocation(clazz, method, args);
		} else {
			this.delegation = new SimpleInvocation(clazz, method, args);
		}
	}

	@Override
	public Method getMethod() {
		return delegation.getMethod();
	}

	@Override
	public Object[] getArgs() {
		return delegation.getArgs();
	}

	@Override
	public String toString() {
		return delegation.toString();
	}

	@Override
	public Method getRouteMethod() {
		return delegation.getRouteMethod();
	}

	@Override
	public ShardingMeta getShardingMeta() {
		return delegation.getShardingMeta();
	}

	@Override
	public Class<?> getMapperClass() {
		return delegation.getMapperClass();
	}

	@Override
	public Class<?> getShardingMappingClass() {
		return delegation.getShardingMappingClass();
	}

	@Override
	public DataSourceRouting getDataSourceRouting() {
		return delegation.getDataSourceRouting();
	}

	@Override
	public MapperBasicConfig getMapperConfig() {
		return delegation.getMapperConfig();
	}

	public boolean isSharding() {
		return this.delegation.isSharding();
	}

}
