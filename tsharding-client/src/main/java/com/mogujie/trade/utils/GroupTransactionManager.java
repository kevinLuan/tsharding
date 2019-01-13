package com.mogujie.trade.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分组事物管理器处理
 * 主要用于多个同一数据库的合并分组处理
 * @author SHOUSHEN LUAN
 *         create date: 2017年1月7日
 */
public class GroupTransactionManager {
	/**
	 * 事物管理器
	 */
	private static final Set<String> TRANSACTION_MANAGER_NAME;
	static {
		TRANSACTION_MANAGER_NAME = new HashSet<>();
	}

	/**
	 * 注册事物管理器Name
	 * @param transactionManagerName
	 */
	public static void register(String transactionManagerName) {
		if (TRANSACTION_MANAGER_NAME.contains(transactionManagerName)) {
			throw new IllegalArgumentException("请不要重复注册事物管理器:`" + transactionManagerName + "`");
		}
		TShardingLog.info("addTransactionManager:{}", transactionManagerName);
		TRANSACTION_MANAGER_NAME.add(transactionManagerName);
	}

	/**
	 * 事物管理器分组
	 * @return
	 */
	public static TmGroup group() {
		return new TmGroup(parser());
	}

	/**
	 * 事物管理器分组
	 * @return
	 */
	public static List<String> group(String name) {
		Map<String, List<String>> map = parser();
		return map.get(name);
	}

	private static Map<String, List<String>> parser() {
		Iterator<String> iterator = TRANSACTION_MANAGER_NAME.iterator();
		Map<String, List<String>> map = new HashMap<>();
		while (iterator.hasNext()) {
			String transactionManager = iterator.next();
			String tmKey = trimNumber(transactionManager);
			if (!map.containsKey(tmKey)) {
				map.put(tmKey, new ArrayList<String>());
			}
			List<String> list = map.get(tmKey);
			list.add(transactionManager);
			map.put(tmKey, list);
		}
		return map;
	}

	public static class TmGroup {
		private Map<String, List<String>> group;
		private Iterator<String> iterator;

		public TmGroup(Map<String, List<String>> group) {
			this.group = group;
			this.iterator = group.keySet().iterator();
		}

		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		public Entry next() {
			String key = this.iterator.next();
			List<String> list = this.group.get(key);
			return new Entry(key, list);
		}

		public static class Entry {
			private String name;
			private List<String> list;

			public Entry(String key, List<String> list) {
				this.name = key;
				this.list = list;
			}

			public boolean isManyDataSource() {
				return this.list.size() > 1;
			}

			public String getName() {
				return this.name;
			}

			public List<String> getTransction() {
				return list;
			}
		}
	}

	static String trimNumber(String tm) {
		StringBuilder builder = new StringBuilder(tm.length());
		char[] chars = tm.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (!isNumber(chars[i])) {
				builder.append(chars[i]);
			}
		}
		return builder.toString();
	}

	static boolean isNumber(char chat) {
		return chat >= '0' && chat <= '9';
	}

}
