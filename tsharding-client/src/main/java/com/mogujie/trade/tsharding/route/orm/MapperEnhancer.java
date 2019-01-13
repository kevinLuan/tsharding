package com.mogujie.trade.tsharding.route.orm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.utils.EnhanceMapperMethodUtils;
import com.mogujie.trade.utils.EnhanceMapperMethodUtils.Entity;
import com.mogujie.trade.utils.EnhanceMapperMethodUtils.MethodSegemation;
import com.mogujie.trade.utils.TShardingLog;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

/**
 * 通用Mapper增强基类，扩展Mapper sql时需要继承该类
 * 
 * @author qigong on 5/1/15
 */
public abstract class MapperEnhancer {
	private static ClassPool pool = ClassPool.getDefault();
	/* Map<methodName,enhancedShardingSQL> */
	private Map<String, Method> methodMap = new HashMap<String, Method>();
	private Class<?> mapperClass;

	public MapperEnhancer(Class<?> mapperClass) {
		this.mapperClass = mapperClass;
	}

	/**
	 * 代码增加方法标记
	 * 
	 * @param record
	 */
	public String enhancedShardingSQL(Object record) {
		return "enhancedShardingSQL";
	}

	public MapperEnhancer() {
		super();
	}

	private static AtomicBoolean isInit = new AtomicBoolean(false);

	private static void init() {
		if (isInit.compareAndSet(false, true)) {
			pool.insertClassPath(new LoaderClassPath(getClassLoader()));
			// 下面方式也可以达到同样效果
			// Set<String> set = ScanClass.getAppClassPath();
			// Iterator<String> iterator = set.iterator();
			// while (iterator.hasNext()) {
			// String path = iterator.next();
			// try {
			// LOGGER.warn("[init.set.classPool.path]:" + path);
			// pool.insertClassPath(path);
			// } catch (NotFoundException e) {
			// e.printStackTrace();
			// }
			// }
		}
	}

	private static ClassLoader classLoader;

	public static ClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		return classLoader;
	}

	/**
	 * 对mapper进行增强，生成新的mapper，并主动加载新mapper类到classloader
	 * 
	 * @param mapperClassName
	 */
	public static void enhanceMapperClass(String mapperClassName) throws Exception {
		init();
		TShardingLog.warn("ShardingMapper:" + mapperClassName);
		Class<?> originClass = Class.forName(mapperClassName);
		Method[] originMethods = originClass.getDeclaredMethods();
		CtClass cc = pool.get(mapperClassName);
		DataSourceRouting routing = originClass.getAnnotation(DataSourceRouting.class);
		int tables = routing.tables();
		boolean isSplitTable = tables > 1;
		TShardingLog.warn("增强接口:{} - [0,{}]", mapperClassName, tables - 1);
		String methodName[] = new String[cc.getDeclaredMethods().length];
		for (int m = 0; m < cc.getDeclaredMethods().length; m++) {
			CtMethod ctMethod = cc.getDeclaredMethods()[m];
			methodName[m] = ctMethod.getName();
			Entity entity = EnhanceMapperMethodUtils.segmentation(tables);
			while (entity.hasSegemation()) {
				MethodSegemation methodSegemation = entity.nextSegemation();
				String shardingClass = EnhanceMapperMethodUtils.getShardingClass(mapperClassName, ctMethod.getName(),
						methodSegemation);
				enhanceClass(shardingClass, originClass, originMethods, ctMethod, methodSegemation, isSplitTable);
			}
		}
		TShardingLog.warn("Methods:{}", Arrays.toString(methodName));
	}

	/**
	 * 增加mapper class
	 * 
	 * @param mapperClassName
	 * @param originClass
	 * @param originMethods
	 * @param ctMethod
	 * @param tables
	 * @param isSplitTable 是否分表
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	private static void enhanceClass(String mapperClassName, Class<?> originClass, Method[] originMethods,
			CtMethod ctMethod, MethodSegemation segemation, boolean isSplitTable)
			throws NotFoundException, CannotCompileException {
		CtClass enhanceClass = pool.makeInterface(mapperClassName);
		for (long i = segemation.getStart(); i <= segemation.getEnd(); i++) {
			String tableSuffix;
			if (isSplitTable) {// 计算分表后缀
				tableSuffix = RouteRuleFactory.getRouteRule(originClass).fillBit(i);
			} else {
				tableSuffix = String.valueOf(i);
			}
			CtMethod newMethod = new CtMethod(ctMethod.getReturnType(), ctMethod.getName() + tableSuffix,
					ctMethod.getParameterTypes(), enhanceClass);
			Method method = getOriginMethod(newMethod, originMethods);
			if (method.getParameterAnnotations()[0].length > 0) {
				ClassFile ccFile = enhanceClass.getClassFile();
				ConstPool constPool = ccFile.getConstPool();
				// 拷贝注解信息和注解内容，以支持mybatis mapper类的动态绑定
				newMethod.getMethodInfo()
						.addAttribute(MapperAnnotationEnhancer.duplicateParameterAnnotationsAttribute(constPool, method));
			}
			enhanceClass.addMethod(newMethod);
		}
		Class<?> loadThisClass = enhanceClass.toClass();
		// 2015.09.22后不再输出类到本地
		// enhanceClass.writeFile(".");
	}

	private static Method getOriginMethod(CtMethod ctMethod, Method[] originMethods) {
		for (Method method : originMethods) {
			if (isEq(ctMethod.getName(), method.getName())) {
				return method;
			}
		}
		throw new RuntimeException("enhanceMapperClass find method error!");
	}

	/**
	 * 验证方法名称是否为增强方法
	 * 
	 * @param routeMethod 路由方法
	 * @param rawMethod 原始方法
	 * @return
	 */
	protected static boolean isEq(String routeMethod, String rawMethod) {
		if (routeMethod.startsWith(rawMethod)) {
			String suffix = routeMethod.substring(rawMethod.length());
			return suffix.matches("\\d+");
		}
		return false;
	}

	/**
	 * 添加映射方法
	 * 
	 * @param methodName
	 * @param method
	 */
	public void addMethodMap(String methodName, Method method) {
		methodMap.put(methodName, method);
	}

	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	private static final DefaultReflectorFactory DEFAULT_REFLECTOR_FACTORY=new DefaultReflectorFactory();
	/**
	 * 反射对象，增加对低版本Mybatis的支持
	 * 
	 * @param object 反射对象
	 * @return
	 */
	public static MetaObject forObject(Object object) {
		return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY,DEFAULT_REFLECTOR_FACTORY);
	}

	/**
	 * 是否支持该通用方法
	 * 
	 * @param msId
	 * @return
	 */
	public boolean supportMethod(String msId) {
		Class<?> mapperClass = getMapperClass(msId);
		if (this.mapperClass.isAssignableFrom(mapperClass)) {
			String methodName = getMethodName(msId);
			return methodMap.get(methodName) != null;
		}
		return false;
	}

	/**
	 * 重新设置SqlSource
	 * 
	 * @param ms
	 * @param sqlSource
	 */
	protected void setSqlSource(MappedStatement ms, SqlSource sqlSource) {
		MetaObject msObject = forObject(ms);
		msObject.setValue("sqlSource", sqlSource);
	}

	/**
	 * 重新设置SqlSource
	 * 
	 * @param ms
	 * @throws java.lang.reflect.InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void setSqlSource(MappedStatement ms, Configuration configuration) throws Exception {
		Method method = methodMap.get(getMethodName(ms));
		try {
			if (method.getReturnType() == Void.TYPE) {
				method.invoke(this, ms);
			} else if (SqlSource.class.isAssignableFrom(method.getReturnType())) {
				// 增加DAO方法
				Method exMethod = getMethod(getMethodName(ms));
				DataSourceRouting routing = getMapperClass(ms.getId()).getAnnotation(DataSourceRouting.class);
				TShardingLog.warn("setSqlSource|method:{}|databases:{}|tables:{}", exMethod.getName(), routing.databases(),
						routing.tables());
				StringBuilder builder = new StringBuilder();
				// 代码增强 扩充为sem.table()
				for (long i = 0; i < routing.tables(); i++) {
					// 获取新的sharding后的sql
					SqlSource sqlSource = (SqlSource) method.invoke(this, ms, configuration, i);
					String tableSuffix;
					if (routing.tables() > 1) {
						tableSuffix = RouteRuleFactory.getRouteRule(getMapperClass(ms.getId())).fillBit(i);
					} else {
						tableSuffix = String.valueOf(i);
					}
					String newMsId = EnhanceMapperMethodUtils.getMappedStatement(ms.getId(), tableSuffix, routing.tables());
					if (i == 0) {
						builder.append(newMsId);
					} else if (i == routing.tables() - 1) {
						builder.append(" ~ " + newMsId);
					}
					// 添加到ms库中
					MappedStatement newMs = copyFromMappedStatement(ms, sqlSource, newMsId);
					configuration.addMappedStatement(newMs);
					setSqlSource(newMs, sqlSource);
				}
				TShardingLog.info("sharding.api:[" + builder.toString() + "]");
			} else {
				throw new RuntimeException("自定义Mapper方法返回类型错误,可选的返回类型为void和SqlNode!");
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException() != null ? e.getTargetException() : e);
		}
	}

	private Method getMethod(String name) {
		Method[] methods = this.mapperClass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(name)) {
				return methods[i];
			}
		}
		throw new RuntimeException("Not found method:" + name);
	}

	protected MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource, String newMsId) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId, newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		// setStatementTimeout()
		builder.timeout(ms.getTimeout());
		// setParameterMap()
		builder.parameterMap(ms.getParameterMap());
		// setStatementResultMap()
		List<ResultMap> resultMaps = ms.getResultMaps();
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		// setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
		return builder.build();
	}

	/**
	 * 根据msId获取接口类
	 * 
	 * @param msId
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getMapperClass(String msId) {
		String mapperClassStr = msId.substring(0, msId.lastIndexOf("."));
		try {
			return Class.forName(mapperClassStr);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("无法获取Mapper接口信息:" + msId);
		}
	}

	/**
	 * 获取执行的方法名
	 * 
	 * @param ms
	 * @return
	 */
	public static String getMethodName(MappedStatement ms) {
		return getMethodName(ms.getId());
	}

	/**
	 * 获取执行的方法名
	 * 
	 * @param msId
	 * @return
	 */
	public static String getMethodName(String msId) {
		return msId.substring(msId.lastIndexOf(".") + 1);
	}
}
