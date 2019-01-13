package com.lyh.tsharding.utils;

import java.util.HashSet;
import java.util.Set;

import com.mogujie.trade.db.DataSourceRouting;

public class ClassNameHelper {
	private static final char COMMA = ',';
	private Set<Class<?>> mapperSet;
	private String[] mappers;

	public static ClassNameHelper create() {
		return new ClassNameHelper();
	}

	public static String build(String[] mappers, Set<Class<?>> mapperSet) {
		return create().setMapper(mappers).setMapper(mapperSet).build();
	}

	public static String build(String[] mappers) {
		return create().setMapper(mappers).build();
	}

	public static String build(Set<Class<?>> mapperSet) {
		return create().setMapper(mapperSet).build();
	}

	public ClassNameHelper setMapper(Set<Class<?>> mapperSet) {
		this.mapperSet = mapperSet;
		return this;
	}

	public ClassNameHelper setMapper(String[] mappers) {
		this.mappers = mappers;
		return this;
	}

	public String build() {
		String value1 = buildPackage();
		String value2 = buildEnhancedMappers();
		String value = null;
		if (value1 != null && value2 != null) {
			value = value1 + value2;
		} else if (value1 != null) {
			value = value1;
		} else if (value2 != null) {
			value = value2;
		} else {
			return null;
		}
		value = deleteLastComma(value);
		return deleteRepeat(value);
	}

	String buildEnhancedMappers() {
		if (mapperSet != null && mapperSet.size() > 0) {
			String value = "";
			Object[] mapper = mapperSet.toArray();
			for (int i = 0; i < mapper.length; i++) {
				Class<?> mapperClass = (Class<?>) mapper[i];
				DataSourceRouting routing = mapperClass.getAnnotation(DataSourceRouting.class);
				if (routing != null && (routing.tables() > 1 || routing.databases() > 1)) {
					value += mapperClass.getName() + COMMA;
				}
			}
			return value;
		}
		return null;
	}

	/**
	 * 如果最后一个字符时逗号“,”则删除该字符
	 * 
	 * @param builder
	 */
	String deleteLastComma(String value) {
		if (value != null && value.length() > 0) {
			int lastIndex = value.length() - 1;
			if (value.charAt(lastIndex) == COMMA) {
				return value.substring(0, lastIndex);
			}
		}
		return value;
	}

	String deleteRepeat(String value) {
		if (value != null) {
			String[] array = value.split(String.valueOf(COMMA));
			Set<String> set = new HashSet<>(array.length);
			for (int i = 0; i < array.length; i++) {
				set.add(array[i]);
			}
			Object vals[] = set.toArray();
			value = "";
			for (int i = 0; i < vals.length; i++) {
				value += vals[i];
				if (vals.length > i + 1) {
					value += COMMA;
				}
			}
		}
		return value;
	}

	String buildPackage() {
		if (mappers != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < mappers.length; i++) {
				builder.append(mappers[i]);
				builder.append(",");
			}
			return builder.toString();
		} else {
			return null;
		}
	}
}
