package com.mogujie.sharding.merge;

import java.lang.reflect.Method;

/**
 * 无返回值类型merge
 * @author SHOUSHEN LUAN
 * @date 2016年12月20日
 */
public class MergeVoid implements MergeApi {
	private Object value;

	@Override
	public void merge(Object value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean isSupport(Method method) {
		Class<?> clazz = method.getReturnType();
		return isSupport(clazz);
	}

	@Override
	public MergeApi newInstance() {
		return new MergeVoid();
	}

	@Override
	public boolean isSupport(Class<?> clazz) {
		if (Void.class == clazz) {
			return true;
		} else if (clazz == void.class) {
			return true;
		}
		return false;
	}
}
