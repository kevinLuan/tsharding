package com.mogujie.route.rule;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.utils.TShardingLog;

/**
 * 数据路由规则工厂类
 * 
 * @author SHOUSHEN LUAN
 */
public class RouteRuleFactory {
	/**
	 * Mapper数据路由规则
	 */
	private static Map<Class<?>, RouteRule<Object>> dataRouteMap = new ConcurrentHashMap<>();

	/**
	 * 根据Mapper类获取路由规则
	 * 
	 * @param mappedClass
	 * @return
	 */
	public static RouteRule<Object> getRouteRule(Class<?> mappedClass) {
		if (dataRouteMap.containsKey(mappedClass)) {
			return dataRouteMap.get(mappedClass);
		} else {
			throw new IllegalArgumentException("Not found mapper route rule by mapperClass:`" + mappedClass.getName() + "`");
		}
	}

	/**
	 * 注册Mapper API
	 * 
	 * @param clazz
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void register(Class<?> clazz) {
		DataSourceRouting dataSourceRouting = clazz.getAnnotation(DataSourceRouting.class);
		if (dataSourceRouting != null && clazz.isInterface()) {
			if (isShardingMapper(dataSourceRouting)) {
				Class<? extends RouteRule> ruleClazz = dataSourceRouting.routeRule();
				try {
					RouteRule routeRule = ruleClazz.newInstance();
					if (dataRouteMap.containsKey(clazz)) {
						throw new RuntimeException("请不要重复注册Mapper:" + clazz.getName());
					}
					dataRouteMap.put(clazz, routeRule);
					TShardingLog.warn("注册Mapper路由规则: mapper={}->routeRule={}", clazz.getName(), ruleClazz.getName());
					return;
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException("初始化路由规则实例出错:" + clazz.getName());
				}
			} else {
				// skip no sharding mapper class
				return;
			}
		}
		throw new RuntimeException("不支持的Mapper:" + clazz.getName());
	}

	private static boolean isShardingMapper(DataSourceRouting dataSourceRouting) {
		if (dataSourceRouting.databases() > 1 || dataSourceRouting.tables() > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 获取所有增强Mapper类
	 * 
	 * @return
	 */
	public static Set<Class<?>> getEnhancedMappers() {
		return dataRouteMap.keySet();
	}
}
