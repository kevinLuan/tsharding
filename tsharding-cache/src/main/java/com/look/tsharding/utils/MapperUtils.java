package com.look.tsharding.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.look.tsharding.auto.cache.MapperHander;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.tsharding.filter.InvocationProxy;

public class MapperUtils {
	private static Map<String, DataSourceRouting> dataSourceRoutingCache = new ConcurrentHashMap<>();

	public static DataSourceRouting getDataSourceRouting(InvocationProxy invocation) {
		Class<?> mapper = invocation.getInvocation().getMapperClass();
		String name = mapper.getName();
		if (dataSourceRoutingCache.containsKey(name)) {
			return dataSourceRoutingCache.get(name);
		}
		DataSourceRouting routing = mapper.getAnnotation(DataSourceRouting.class);
		if (routing == null) {
			throw new IllegalArgumentException("Mybatis.Mapper:`" + name + "`缺少@DataSourceRouting");
		}
		dataSourceRoutingCache.put(name, routing);
		return routing;
	}

	public static MapperHander getMapperHander(InvocationProxy invocation) {
		return new MapperHander(invocation);
	}

}
