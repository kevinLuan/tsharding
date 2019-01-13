package com.mogujie.sharding.merge;

import java.lang.reflect.Method;

public class MergeLong implements MergeApi {
	private Long longValue;

	@Override
	public void merge(Object value) {
		if (value != null) {
			if (this.longValue == null) {
				this.longValue = (Long) value;
			} else {
				this.longValue += (Long) value;
			}
		}
	}

	@Override
	public Object getValue() {
		return longValue;
	}

	@Override
	public boolean isSupport(Method method) {
		Class<?> clazz = method.getReturnType();
		return isSupport(clazz);
	}

	@Override
	public MergeApi newInstance() {
		return new MergeLong();
	}

	@Override
	public boolean isSupport(Class<?> clazz) {
		if (clazz == long.class) {
			return true;
		} else if (Long.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

}
