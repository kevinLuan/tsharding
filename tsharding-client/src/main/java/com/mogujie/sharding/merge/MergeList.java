package com.mogujie.sharding.merge;

import java.lang.reflect.Method;
import java.util.List;

public class MergeList implements MergeApi {
	private List<Object> merge = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void merge(Object value) {
		if (value != null) {
			if (merge == null) {
				this.merge = (List) value;
			} else {
				if (this.merge == value) {
					// 当前merge实例和传入的实例为同一个对象时直接返回
					return;
				}
				List<?> list = (List<?>) value;
				for (int i = 0; i < list.size(); i++) {
					merge.add(list.get(i));
				}
			}
		}
	}

	@Override
	public Object getValue() {
		return merge;
	}

	@Override
	public boolean isSupport(Method method) {
		Class<?> clazz = method.getReturnType();
		return isSupport(clazz);
	}

	@Override
	public MergeApi newInstance() {
		return new MergeList();
	}

	@Override
	public boolean isSupport(Class<?> clazz) {
		if (List.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

}
