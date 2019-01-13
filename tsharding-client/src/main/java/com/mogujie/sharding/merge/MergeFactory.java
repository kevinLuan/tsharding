package com.mogujie.sharding.merge;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * merge API工厂类
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月17日
 */
public class MergeFactory {
	private static Set<MergeApi> mergeApis;
	static {
		mergeApis = new HashSet<>();
		register(new MergeList());
		register(new MergeLong());
		register(new MergeInt());
		register(new MergeVoid());
	}

	/**
	 * 注册支持的merge API
	 * @param api
	 */
	public static void register(MergeApi api) {
		mergeApis.add(api);
	}

	/**
	 * 根据返回类型创建mergeAPI实例
	 * @param method
	 * @return
	 */
	public static MergeApi newMerge(Method method) {
		Class<?> returnType = method.getReturnType();
		return newMerge(returnType);
	}

	/**
	 * 根据clazz类型创建mergeAPI实例
	 * @param method
	 * @return
	 */
	public static MergeApi newMerge(Class<?> clazz) {
		Iterator<MergeApi> iterator = mergeApis.iterator();
		while (iterator.hasNext()) {
			MergeApi mergeApi = iterator.next();
			if (mergeApi.isSupport(clazz)) {
				return mergeApi.newInstance();
			}
		}
		throw new IllegalArgumentException("不支持的merge:" + clazz.getName() + "类型");
	}

	/**
	 * 是否支持merge类型
	 * @param clazz
	 * @return
	 */
	public static boolean isSupportMerge(Class<?> clazz) {
		Iterator<MergeApi> iterator = mergeApis.iterator();
		while (iterator.hasNext()) {
			MergeApi mergeApi = iterator.next();
			if (mergeApi.isSupport(clazz)) {
				return true;
			}
		}
		return false;
	}
}
