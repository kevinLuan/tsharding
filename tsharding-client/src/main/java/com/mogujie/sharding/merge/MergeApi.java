package com.mogujie.sharding.merge;

import java.lang.reflect.Method;

/**
 * 合并API
 * @author SHOUSHEN LUAN
 * @date 2016年12月19日
 */
public interface MergeApi {
	/**
	 * 合并数据
	 * @param value
	 */
	public void merge(Object value);

	/**
	 * 获取合并后的value
	 * @return
	 */
	public Object getValue();

	/**
	 * 是否支持该方法的返回类型
	 * @param method
	 * @return
	 */
	public boolean isSupport(Method method);

	/**
	 * 创建MergeApi实例
	 * @return
	 */
	public MergeApi newInstance();

	/**
	 * 是否支持类型
	 * @param method
	 * @return
	 */
	public boolean isSupport(Class<?> clazz);
}
