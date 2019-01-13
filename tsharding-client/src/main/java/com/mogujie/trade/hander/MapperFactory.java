package com.mogujie.trade.hander;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;
import com.mogujie.trade.utils.TShardingLog;

/**
 * Mapper factory
 * 
 * @author SHOUSHEN LUAN
 * @date 2016年12月19日
 */
public class MapperFactory {
	private static final Set<Class<?>> MAPPERS = new HashSet<>();
	/**
	 * 分库分表Mapper
	 */
	private static final Set<Class<?>> ENHANCE_MAPPERS = new HashSet<>();
	private static Map<String, ShardingHanderEntry> CACHE = Collections
			.synchronizedMap(new HashMap<String, ShardingHanderEntry>());

	/**
	 * 根据Mapper class 和调用method 获取{@link ShardingHanderEntry}
	 * 
	 * @param mappendClass
	 * @param method
	 * @return
	 * @throws IllegalAccessException
	 */
	public static ShardingHanderEntry getShardingHanderEntry(Class<?> mappendClass, Method method)
			throws IllegalAccessException {
		String name = mappendClass.getName() + "." + method.getName();
		if (CACHE.containsKey(name)) {
			return CACHE.get(name);
		}
		throw new IllegalArgumentException("没有找到该Mapper:" + name);
	}

	public static class ShardingHanderEntry {
		private Class<?> mappedClass;
		private Method method;
		private DataSourceRouting routing;
		private ShardingParam shardingParam;
		/**
		 * 路由参数索引
		 */
		private int routeParamIndex = -1;

		public ShardingHanderEntry(Class<?> mappendClass, Method method) throws IllegalAccessException {
			this.mappedClass = mappendClass;
			this.method = method;
			routing = this.mappedClass.getAnnotation(DataSourceRouting.class);
			parserShardingParam();
		}

		private void parserShardingParam() throws IllegalAccessException {
			if (isSharding()) {
				Annotation[][] annotations = method.getParameterAnnotations();
				for (int i = 0; i < annotations.length; i++) {
					for (int j = 0; j < annotations[i].length; j++) {
						if (annotations[i][j] instanceof ShardingParam) {
							if (this.routeParamIndex != -1) {
								throw new IllegalArgumentException(
										this.mappedClass.getSimpleName() + "." + this.method.getName() + " 方法参数存在多个@ShardingParam注解");
							}
							this.routeParamIndex = i;
							shardingParam = (ShardingParam) annotations[i][j];
						}
					}
				}
				if (routeParamIndex == -1) {
					throw new IllegalAccessException(
							this.mappedClass.getSimpleName() + "." + this.method.getName() + " 方法参数缺少@ShardingParam注解");
				}
			}
		}

		public Class<?> getMappedClass() {
			return mappedClass;
		}

		public Method getMethod() {
			return method;
		}

		public DataSourceRouting getRouting() {
			return routing;
		}

		public ShardingParam getShardingParam() {
			return shardingParam;
		}

		public int getRouteParamIndex() {
			return routeParamIndex;
		}

		/**
		 * 获取路由参数
		 * 
		 * @param args
		 * @return
		 */
		public Object getRouteParam(Object[] args) {
			return args[getRouteParamIndex()];
		}

		public boolean isSharding() {
			return routing.tables() > 1 || routing.databases() > 1;
		}
	}

	/**
	 * register Mapper interface
	 * 
	 * @param clazz MapperClass
	 */
	public static void registerMapper(Class<?> clazz) {
		DataSourceRouting routing = clazz.getAnnotation(DataSourceRouting.class);
		if (routing != null) {
			TShardingLog.getLogger().debug("注册Mapper:" + clazz);
			MAPPERS.add(clazz);
			if (routing.tables() > 1 || routing.databases() > 1) {
				ENHANCE_MAPPERS.add(clazz);
				TShardingLog.getLogger().debug("注册分库分表Mapper:" + clazz);
			}
		} else {
			throw new IllegalArgumentException("无效的Mapper:`" + clazz + "`");
		}
		registerMethod(clazz);
	}

	private static void registerMethod(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			try {
				register(clazz, methods[i]);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * register Mapper interface method
	 * 
	 * @param clazz MapperClass
	 * @param method MapperClass.method
	 * @throws IllegalAccessException
	 */
	private static void register(Class<?> clazz, Method method) throws IllegalAccessException {
		String name = clazz.getName() + "." + method.getName();
		CACHE.put(name, new ShardingHanderEntry(clazz, method));
	}

	/**
	 * 获取所有增强Mapper
	 * 
	 * @return
	 */
	public static Set<Class<?>> getEnhanceMappers() {
		return ENHANCE_MAPPERS;
	}

	public static Set<Class<?>> getMappers() {
		return MAPPERS;
	}

}
