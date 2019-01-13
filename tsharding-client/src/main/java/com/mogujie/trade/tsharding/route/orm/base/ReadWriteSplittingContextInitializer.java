package com.mogujie.trade.tsharding.route.orm.base;

import com.mogujie.trade.db.DataSourceType;
import com.mogujie.trade.db.ReadWriteSplitting;
import com.mogujie.trade.db.ReadWriteSplittingContext;
import com.mogujie.trade.utils.TShardingLog;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读写分离的上下文初始化和清空
 */
public class ReadWriteSplittingContextInitializer {

	/**
	 * write method name matches
	 */
	private final static Set<String> DEFAULT_WRITE_METHOD_NAMES;
	static {
		DEFAULT_WRITE_METHOD_NAMES = Collections.synchronizedSet(new HashSet<String>());
		register("update", "save", "insert", "delete", "add", "batchInsert", "batchUpdate", "batchSave", "batchAdd");
	}

	/**
	 * register route master datasource method
	 * @param methods
	 */
	public static void register(String... methods) {
		for (String method : methods) {
			DEFAULT_WRITE_METHOD_NAMES.add(method);
		}
		TShardingLog.info("Register route rule master DataSource method ->{}", Arrays.toString(methods));
	}

	private final static ConcurrentHashMap<Method, DataSourceType> METHOD_DATASOURCE_CACHE = new ConcurrentHashMap<Method, DataSourceType>();

	public static void clearReadWriteSplittingContext() {
		ReadWriteSplittingContext.clear();
	}

	/**
	 * 根据方法名称确认DataSourceType
	 */
	private static DataSourceType determineDataSourceType(Method method) {
		DataSourceType dataSourceType = DataSourceType.slave;
		for (String writeMethodName : DEFAULT_WRITE_METHOD_NAMES) {
			if (method.getName().startsWith(writeMethodName)) {
				dataSourceType = DataSourceType.master;
				break;
			}
		}
		return dataSourceType;
	}

	/**
	 * 初始化读写分离上下文
	 */
	public static void initReadWriteSplittingContext(Invocation invocation)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		Method method = invocation.getMethod();
		if (METHOD_DATASOURCE_CACHE.containsKey(method) == false) {
			DataSourceType dataSourceType = getDataSourceType(invocation);
			METHOD_DATASOURCE_CACHE.putIfAbsent(method, dataSourceType);
		}
		DataSourceType dataSourceType = METHOD_DATASOURCE_CACHE.get(method);
		TShardingLog.debug("ReadWriteSplitting:{} using dataSourceType:{}", method, dataSourceType);
		ReadWriteSplittingContext.set(dataSourceType);
	}

	/**
	 * 根据当前执行的Mapper.method获得DataSourceType
	 */
	private static DataSourceType getDataSourceType(Invocation invocation)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		ReadWriteSplitting rws = invocation.getMethod().getAnnotation(ReadWriteSplitting.class);
		if (rws != null) {// 优先使用方法注解
			if (rws.value() == null) {
				throw new IllegalArgumentException("ReadWriteSplitting.value must not null");
			}
			return rws.value();
		} else {
			MapperBasicConfig config = invocation.getMapperConfig();
			if (config.isReadWriteSplitting()) {
				Method method = invocation.getMethod();
				if (method.getDeclaringClass() == Object.class) {
					throw new IllegalArgumentException("NoSupport Object." + method.getName() + " method");
				}
				return determineDataSourceType(method);
			} else {// 不支持读写分离
				return DataSourceType.master;
			}
		}
	}
}
