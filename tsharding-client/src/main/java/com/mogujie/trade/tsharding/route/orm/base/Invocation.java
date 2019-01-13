package com.mogujie.trade.tsharding.route.orm.base;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * @author qigong
 */
public interface Invocation {
	Logger LOGGER = LoggerFactory.getLogger(Invocation.class);

	Class<?> getMapperClass();

	Method getMethod();

	Object[] getArgs();

	/**
	 * 获取路由方法
	 * @throws ClassNotFoundException
	 */
	public Method getRouteMethod();

	ShardingMeta getShardingMeta();

	/**
	 * 获取分库分表 sharding Mapping class
	 */
	Class<?> getShardingMappingClass();

	DataSourceRouting getDataSourceRouting();

	MapperBasicConfig getMapperConfig();

	boolean isSharding();
}
