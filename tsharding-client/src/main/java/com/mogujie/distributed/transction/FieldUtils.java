package com.mogujie.distributed.transction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mogujie.trade.tsharding.route.orm.base.ReflectUtil;

public class FieldUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldUtils.class);

	/**
	 * 解析参数
	 * 
	 * @param mapper
	 * @param entity
	 * @param args
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static List<Object> parserParam(Class<?> mapper, Entity entity, Object[] args)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		List<RouteParam> routeParams = entity.getRouteParam(mapper);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("routeParams->{}", Arrays.toString(routeParams.toArray()));
		}
		List<Integer> paramIndex = entity.getParamIndex(mapper);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("paramIndex->{}", Arrays.toString(paramIndex.toArray()));
		}
		if (paramIndex.size() != routeParams.size()) {
			throw new IllegalArgumentException("mapper:" + mapper + "解析的routeParams.size!=list.size()");
		}
		List<Object> results = new LinkedList<>();
		for (int i = 0; i < routeParams.size(); i++) {
			RouteParam routeParam = routeParams.get(i);
			int index = paramIndex.get(i);
			Object param = args[index];
			results.addAll(parserParamList(param, routeParam.value()));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("sharding.list->" + Arrays.toString(results.toArray()));
		}
		return results;
	}

	private static List<Object> parserParamList(Object param, String expression)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("parserValue:{}->{}", expression, param);
		}
		List<Object> list = new ArrayList<>();
		if (expression.indexOf(".") == -1) {
			append(list, param);
			return list;
		} else {
			String[] expressions = expression.split("\\.");
			return parserParamList(param, expressions, list);
		}
	}

	@SuppressWarnings("unchecked")
	private static void append(List<Object> list, Object param) {
		if (param != null) {
			if (List.class.isAssignableFrom(param.getClass())) {
				list.addAll((List<Object>) param);
			} else {
				list.add(param);
			}
		}
	}

	private static List<Object> parserParamList(Object param, String[] expressions, List<Object> list)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Object value = param;
		for (int i = 1; i < expressions.length; i++) {
			String fieldName = expressions[i];
			if (isList(value)) {// List 类型必须为表达式的最后节点
				LIST: {
					if (expressions.length == i + 1) {// 叶子节点
						List<Object> nodes = (List<Object>) value;
						for (Object node : nodes) {
							node = getValue(node, fieldName);
							append(list, node);
						}
						return list;
					} else {
						throw new IllegalArgumentException(
								"不支持的数据类型:`" + param + "` expressions:`" + expressions + "`");
					}
				}
			} else {
				value = getValue(value, fieldName);
				if (value != null && expressions.length == i + 1) {
					append(list, value);
					return list;
				}
			}
		}
		return list;
	}

	private static boolean isList(Object obj) {
		if (obj != null && List.class.isAssignableFrom(obj.getClass())) {
			return true;
		}
		return false;
	}

	private static Object getValue(Object bean, String name)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		if (bean != null) {
			Field field = ReflectUtil.getDeclaredField(bean, name);
			if (field != null) {
				field.setAccessible(true);
				return field.get(bean);
			} else {
				throw new NoSuchFieldError(bean + " not find field:" + name);
			}
		}
		return null;
	}

}
