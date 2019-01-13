package com.mogujie.route.rule;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.mogujie.trade.tsharding.route.orm.base.ReflectUtil;

/**
 * 工具类
 * 
 * @author SHOUSHEN LUAN
 */
public class ShardingUtils {
	/**
	 * 根据shardingTableSuffix生成bit位表后缀，长度不够补0
	 * 
	 * @param shardingTableSuffix
	 *            数据散列表后缀
	 * @param bit
	 *            生成的位数
	 * @return
	 */
	final static String fillBit(long shardingTableSuffix, int bit) {
		if (shardingTableSuffix < 0) {
			throw new IllegalArgumentException("shardingTableSuffix:`" + shardingTableSuffix + "` 必须大于等于零");
		} else if (bit < 1) {
			throw new IllegalArgumentException("bit:`" + bit + "` 必须大于零");
		}
		char[] chat = String.valueOf(shardingTableSuffix).toCharArray();
		if (chat.length > bit) {// 数值长度必须小于生成的长度
			throw new IllegalArgumentException("shardingTableSuffix:`" + shardingTableSuffix + "`位数超过生成总长度");
		}
		StringBuilder builder = new StringBuilder(bit);
		for (short s = 0; s < bit; s++) {
			if (s < chat.length) {
				builder.append(chat[s]);
			} else {
				builder.insert(0, '0');
			}
		}
		return builder.toString();
	}

	/**
	 * 解析sharding参数Value
	 * 
	 * @param shardingValue
	 * @param fieldName
	 * @return 返回原始类型
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static Object parserShardingValue(Object shardingValue, String fieldName)
			throws IllegalArgumentException, IllegalAccessException {
		Object param = shardingValue;
		if (List.class.isAssignableFrom(param.getClass())) {
			List<?> list = (List<?>) param;
			param = list.get(0);// 如果Sharding参数是List，则获取第一个元素作为sharding参数
		}
		if (StringUtils.hasLength(fieldName)) {// 如果设置了field.name，则从POJO属性中获取
			param = getFieldValue(param, fieldName);
		}
		return param;
	}

	/**
	 * 解析sharding参数Value
	 * 
	 * @param <T>
	 * @param shardingValue
	 * @param fieldName
	 * @return RouteRule支持泛型类型
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parserShardingValue(Object shardingValue, String fieldName, RouteRule<?> rule)
			throws IllegalArgumentException, IllegalAccessException {
		Object param = parserShardingValue(shardingValue, fieldName);
		Type type = getGenericityType(rule);
		return convert(param, (Class<T>) type);
	}

	private static Object getFieldValue(Object shardingParam, String fieldName)
			throws IllegalArgumentException, IllegalAccessException {
		if (shardingParam == null) {
			throw new IllegalArgumentException("sharding 参数不允许为空");
		}
		if (shardingParam.getClass().isPrimitive()) {
			return shardingParam;
		} else {
			if(Map.class.isAssignableFrom(shardingParam.getClass())){
				Map<String, Object>map=(Map<String, Object>)shardingParam;
				return map.get(fieldName);
			}
			Field field = ReflectUtil.getDeclaredField(shardingParam, fieldName);
			if (field != null) {
				field.setAccessible(true);
				Object val = field.get(shardingParam);
				return val;
			} else {
				throw new RuntimeException("sharding参数" + shardingParam + " 不存属性:`" + fieldName + "`");
			}
		}
	}

	public static Type getGenericityType(RouteRule<?> rule) {
		Type[] types = rule.getClass().getGenericInterfaces();
		Type shardingType=Long.TYPE;
		if (types.length == 0) {
			// 从继承的父类泛型中获取类型 form super class<Long>
			ParameterizedType parameterizedType = (ParameterizedType) rule.getClass().getGenericSuperclass();
			shardingType= parameterizedType.getActualTypeArguments()[0];
		}
		if (types.length > 0) {
			ParameterizedType parameterizedType = (ParameterizedType) types[0];
			shardingType = parameterizedType.getActualTypeArguments()[0];
		}
		if (!"T".equals(shardingType.getTypeName())) {
			return shardingType;
		}
		// 默认使用Long
		return Long.TYPE;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, Class<T> t) {
		if (isLong(t)) {
			return (T) asLong(value);
		} else if (isString(t)) {
			return (T) asString(value);
		} else if (isInt(t)) {
			return (T) asInt(value);
		}
		return (T) value;
	}

	private static <T> boolean isLong(Class<T> t) {
		if (Long.class.isAssignableFrom(t)) {
			return true;
		} else if (long.class == t) {
			return true;
		}
		return false;
	}

	private static <T> boolean isInt(Class<T> t) {
		if (Integer.class.isAssignableFrom(t)) {
			return true;
		} else if (int.class == t) {
			return true;
		}
		return false;
	}

	private static <T> boolean isString(Class<T> t) {
		if (String.class == t) {
			return true;
		}
		return false;

	}

	private static String asString(Object value) {
		if (value != null) {
			return value.toString();
		}
		return null;
	}

	private static Long asLong(Object value) {
		if (value != null) {
			return Long.parseLong(value.toString().trim());
		}
		return null;
	}

	private static Integer asInt(Object value) {
		if (value != null) {
			return Integer.parseInt(value.toString().trim());
		}
		return null;
	}
}
