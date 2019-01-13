package com.mogujie.tsharding.filter;

/**
 * 定义了实现DB cache 接口
 * 
 * @author kevin
 *
 */
public interface MapperCacheApi {
	Object get(String key);

	void put(String key, Object value);
}
