package com.mogujie.sharding.iterator;

/**
 * 路由迭代器
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月16日
 */
public interface RouteIterator<T> {
	/**
	 * 有下一个元素
	 * @return
	 */
	public boolean hasNext();

	/**
	 * 获取下一个元素
	 * @return
	 */
	public T next();
}
