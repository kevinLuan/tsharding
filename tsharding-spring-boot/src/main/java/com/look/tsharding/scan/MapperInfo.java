package com.look.tsharding.scan;

import java.util.Iterator;
import java.util.Set;

public class MapperInfo {
	/**
	 * Mapper包路径
	 */
	private Set<String> packages;
	/**
	 * 增强Mapper类
	 */
	private Set<Class<?>> enhancedMapper;

	public MapperInfo(Set<String> packages, Set<Class<?>> enhancedMapper) {
		this.packages = packages;
		this.enhancedMapper = enhancedMapper;
	}

	public Set<String> getPackages() {
		return packages;
	}

	public void setPackages(Set<String> packages) {
		this.packages = packages;
	}

	public Set<Class<?>> getEnhancedMapper() {
		return enhancedMapper;
	}

	public void setEnhancedMapper(Set<Class<?>> enhancedMapper) {
		this.enhancedMapper = enhancedMapper;
	}

	/**
	 * 生成增强类字符串形式 生成增强类Mapper
	 * <p>
	 * 例如：
	 * com.mogujie.service.tsharding.mapper.ShopOrderMapper,com.mogujie.service.tsharding.mapper.UserMapper
	 * </p>
	 * 
	 * @return
	 */
	public String buildEnhancedMapper() {
		StringBuilder builder = new StringBuilder(1024);
		Iterator<Class<?>> iterator = enhancedMapper.iterator();
		while (iterator.hasNext()) {
			Class<?> clazz = iterator.next();
			builder.append(clazz.getName());
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	public String buildMappers() {
		StringBuilder builder = new StringBuilder(1024);
		Iterator<String> iterator = packages.iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		return builder.toString();
	}
}
