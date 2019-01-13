package com.look.tsharding.utils;

import java.lang.reflect.Field;

import org.springframework.util.StringUtils;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * 生成CACHE key工具
 */
public class GeneratedCacheKeyUtils {

	public static String generatedKey(DataSourceRouting routing, String key, String param, Object... args)
			throws Throwable {
		StringBuilder builder = new StringBuilder(20);
		builder.append(routing.dataSource());
		builder.append("_");
		builder.append(routing.table());
		builder.append("_");
		builder.append(key);
		if (!StringUtils.isEmpty(param)) {
			appendParameter(builder, param, args);
		}
		return builder.toString();
	}

	private static void appendParameter(StringBuilder builder, String params, Object[] args) throws Throwable {
		String[] strs = params.replaceAll("\\s*", "").split(",");
		for (int i = 0; i < strs.length; i++) {
			if (!StringUtils.isEmpty(strs[i])) {
				if (strs[i].indexOf(".") == -1) {
					if (args[Integer.parseInt(strs[i])] == null) {
						builder.append("_NULL");
					} else {
						builder.append("_" + args[Integer.parseInt(strs[i])].toString());
					}
				} else {
					String[] el = strs[i].split("\\.");
					if (el.length == 2) {
						int index = Integer.parseInt(el[0]);
						Object obj = args[index];
						builder.append("_" + parserParameter(obj, el[1]));
					} else {
						throw new IllegalArgumentException("parser “" + strs[i] + "” error...");
					}
				}
			}
		}
	}

	private static Object parserParameter(Object obj, String fieldStr) throws Throwable {
		if (!StringUtils.isEmpty(fieldStr)) {
			Field filField = obj.getClass().getDeclaredField(fieldStr);
			if (filField != null) {
				filField.setAccessible(true);
				return filField.get(obj);
			}
		}
		throw new IllegalArgumentException("fieldStr is empty |fieldStr:" + fieldStr);
	}

}
