package com.mogujie.sharding.merge;

import java.lang.reflect.Method;

public class MergeInt implements MergeApi {
	private Integer intValue;

	@Override
	public void merge(Object value) {
		if (value != null) {
			if (this.intValue == null) {
				this.intValue = (Integer) value;
			} else {
				this.intValue += (Integer) value;
			}
		}
	}

	@Override
	public Object getValue() {
		return intValue;
	}

	@Override
	public boolean isSupport(Method method) {
		Class<?> clazz = method.getReturnType();
		return this.isSupport(clazz);
	}

	@Override
	public MergeApi newInstance() {
		return new MergeInt();
	}

	@Override
	public boolean isSupport(Class<?> clazz) {
		if (clazz == int.class) {
			return true;
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

}
