package com.mogujie.trade.tsharding.route.orm.base;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.mogujie.route.exception.NoSupportOperatorException;
import com.mogujie.trade.db.DataSourceRouting;

/**
 * 简单Invocation处理类，不支持Sharding的处理类
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月15日
 */
class SimpleInvocation implements Invocation {
	private final Class<?> clazz;
	private final Method method;
	private final Object[] args;
	private final MapperBasicConfig config;

	public SimpleInvocation(Class<?> clazz, Method method, Object[] args)
			throws IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
		this.clazz = clazz;
		this.method = method;
		this.args = args;
		String dataSource = getDataSourceRouting().dataSource();
		config = new MapperBasicConfig(getMapperClass(), dataSource);
	}

	@Override
	public Method getMethod() {
		return this.method;
	}

	@Override
	public Object[] getArgs() {
		return this.args;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleInvocation [");
		if (method != null) {
			builder.append("method=").append(method).append(", ");
		}
		if (args != null) {
			builder.append("args=").append(Arrays.toString(args));
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public Method getRouteMethod() {
		throw new NoSupportOperatorException("不支持的操作");
	}

	@Override
	public ShardingMeta getShardingMeta() {
		throw new NoSupportOperatorException("不支持的操作");
	}

	@Override
	public Class<?> getMapperClass() {
		return clazz;
	}

	@Override
	public Class<?> getShardingMappingClass() {
		throw new NoSupportOperatorException("不支持的操作");
	}

	@Override
	public DataSourceRouting getDataSourceRouting() {
		DataSourceRouting routing = clazz.getAnnotation(DataSourceRouting.class);
		return routing;
	}

	@Override
	public MapperBasicConfig getMapperConfig() {
		return config;
	}

	public boolean isSharding() {
		return false;
	}

}
