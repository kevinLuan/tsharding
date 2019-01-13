package com.mogujie.distributed.transction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.interceptor.TransactionAttribute;

import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.utils.TransactionResult;

class Entity {
	private static final Logger LOGGER = LoggerFactory.getLogger(Entity.class);
	private final String staticPart;
	final Method method;
	final ChainedTransaction transaction;
	final Map<Class<?>, LinkedList<Integer>> mapper_paramIndex = new ConcurrentHashMap<>();
	private Map<Class<?>, Boolean> shardingMappersFlag = new ConcurrentHashMap<>();
	private Method unfinishedCallback;
	private final Object target;
	private final TransactionAttribute transactionAttribute;

	public Entity(ProceedingJoinPoint pjp) {
		this.target = pjp.getTarget();
		staticPart = pjp.toLongString();
		this.method = ((MethodSignature) pjp.getSignature()).getMethod();
		this.transaction = method.getAnnotation(ChainedTransaction.class);
		check();
		parserUnfinishedCallback();
		transactionAttribute = TransactionHelper.parseTransactionAttribute(transaction);
	}

	public TransactionAttribute getTransactionAttribute() {
		return this.transactionAttribute;
	}

	public ChainedTransaction getChainedTransaction() {
		return transaction;
	}

	public void doInvokeUnfinishedCallback(ProceedingJoinPoint pjp, TransactionResult res) {
		try {
			ProxyMethodMeta pmm = new ProxyMethodMeta(pjp, res);
			if (unfinishedCallback == null) {
				DefaultUnfinishedCallback.unfinishedCallback(pmm);
			} else {
				unfinishedCallback.invoke(target, pmm);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("doInvokeUnfinishedCallback", e);
		}
	}

	private void parserUnfinishedCallback() {
		String methodName = transaction.unfinishedCallback();

		if (methodName == null || methodName.length() == 0) {
			methodName = this.method.getName() + "_Callback";
		}
		try {
			this.unfinishedCallback = target.getClass().getDeclaredMethod(methodName, ProxyMethodMeta.class);
			if (!this.unfinishedCallback.isAccessible()) {
				this.unfinishedCallback.setAccessible(true);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error("缺少未完成回调方法 {}.{}(ProxyMethod pm)", target.getClass().getName(), methodName);
			}
		}
	}

	// public Entity(Method method, String staticPart) {
	// this.staticPart = staticPart;
	// this.method = method;
	// this.transaction = method.getAnnotation(ChainedTransaction.class);
	// check();
	// parserUnfinishedCallback();
	// this.target = null;
	// }
	/**
	 * 获取该Mapper 对应Method 参数上所有的对应
	 * 
	 * @param mapper
	 * @return
	 */
	public List<RouteParam> getRouteParam(Class<?> mapper) {
		List<Integer> list = this.getParamIndex(mapper);
		List<RouteParam> annotations = new LinkedList<>();
		for (int i = 0; i < list.size(); i++) {
			int index = list.get(i);
			Parameter parameter = method.getParameters()[index];
			RouteParam routeParam = parameter.getAnnotation(RouteParam.class);
			if (routeParam == null) {
				throw new IllegalArgumentException(staticPart + "注解mapper:`" + mapper + "`没有找到映射@RouteParam");
			}
			annotations.add(routeParam);
		}
		return annotations;
	}

	public List<Integer> getParamIndex(Class<?> mapper) {
		if (mapper_paramIndex.containsKey(mapper)) {
			return mapper_paramIndex.get(mapper);
		} else {
			throw new IllegalArgumentException(staticPart + "没有找到Mapper:`" + mapper + "`");
		}
	}

	/**
	 * 验证当前异常是否需要回滚
	 * 
	 * @param throwable
	 * @return
	 */
	public boolean isRollback(Throwable throwable) {
		Class<?> clazzs[] = transaction.rollbackFor();
		for (int i = 0; i < clazzs.length; i++) {
			if (AnyException.class.getName().equals(clazzs[i].getName())) {
				return true;
			}
		}
		// if (clazzs[i].getName().equals(throwable.getClass().getName())) {
		// return true;
		// }
		return getTransactionAttribute().rollbackOn(throwable);
	}

	public Class<?>[] getMapper() {
		return transaction.mapper();
	}

	public int getTimeout() {
		return transaction.timeout();
	}

	private synchronized void check() {
		Class<?>[] mappers = transaction.mapper();
		if (mappers == null || mappers.length == 0) {
			throw new IllegalArgumentException(staticPart + ".mappers must not empty");
		}
		for (Class<?> mapper : mappers) {
			DataSourceRouting routing = mapper.getAnnotation(DataSourceRouting.class);
			if (routing == null) {
				throw new IllegalArgumentException(staticPart + ".无效的Mapper:" + mapper);
			}
			if (routing.databases() > 1) {// 增加需要标记Mapper
				shardingMappersFlag.put(mapper, false);
			}
		}
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			RouteParam routeParam = parameters[i].getAnnotation(RouteParam.class);
			if (routeParam != null) {
				Class<?> mapper = loadShardingMapperAndTag(routeParam.value());
				if (!mapper_paramIndex.containsKey(mapper)) {
					mapper_paramIndex.put(mapper, new LinkedList<Integer>());
				}
				mapper_paramIndex.get(mapper).add(i);
			}
		}
		assertFlag();
	}

	/**
	 * 验证是否还存在未标记的Mapper(检测是否所有的分库分表Mapper至少有一个@RouteParam 参数定义)
	 */
	void assertFlag() {
		for (Map.Entry<Class<?>, Boolean> entry : shardingMappersFlag.entrySet()) {
			if (!entry.getValue()) {
				throw new IllegalArgumentException(staticPart + ".Mapper:`" + entry.getKey() + "` missing @RouteParam(...)");
			}
		}
	}

	private Class<?> loadShardingMapperAndTag(String expression) {
		String name = getMapperName(expression);
		for (Map.Entry<Class<?>, Boolean> entry : shardingMappersFlag.entrySet()) {
			Class<?> clazz = entry.getKey();
			if (clazz.getSimpleName().equals(name)) {
				entry.setValue(true);// 标记
				return clazz;
			}
		}
		throw new IllegalArgumentException(staticPart + ".invalid @RouteParam(value='" + name + "')");
	}

	private String getMapperName(String str) {
		int index = str.indexOf(".");
		if (index == -1) {
			return str;
		} else {
			return str.substring(0, index);
		}
	}

	public Method getUnfinishedCallback() {
		return unfinishedCallback;
	}
}
