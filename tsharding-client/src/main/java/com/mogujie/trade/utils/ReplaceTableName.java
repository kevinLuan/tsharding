package com.mogujie.trade.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceTableName {
	static ReplaceTableName replaceTableName = new ReplaceTableName();

	public static ReplaceTableName getInstance() {
		return replaceTableName;
	}

	/**
	 * 替换表名称
	 * 
	 * @param sql
	 * @param table
	 * @param newTable
	 * @return
	 */
	public String replace(String sql, String table, String newTable) {
		Pattern pattern = Pattern.compile(getRegex(table));
		Matcher matcher = pattern.matcher(sql);
		char[] data = sql.toCharArray();
		StringBuffer buffer = new StringBuffer();
		boolean matche = matcher.find();
		while (matche) {
			int start = matcher.start();
			int end = matcher.end();
			if (checkStart(start, data) && checkEnd(end, data)) {
				matcher.appendReplacement(buffer, newTable);
			}
			matche = matcher.find();
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private boolean checkStart(int startIndex, char[] chats) {
		if (startIndex == 0) {
			return true;
		} else {
			return isValidTableStart(chats[startIndex - 1]);
		}
	}

	private boolean checkEnd(int endIndex, char[] chats) {
		if (chats.length == endIndex) {
			return true;
		} else {
			if (isValidTableStart(chats[endIndex])) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidTableStart(char chat) {
		if (chat >= 'A' && chat <= 'Z') {
			return false;
		} else if (chat >= 'a' && chat <= 'z') {
			return false;
		} else if (chat >= '0' && chat <= '9') {
			return false;
		} else if (chat == '_' || chat == '-') {
			return false;
		}
		return true;
	}

	private String getRegex(String table) {
		return "(?i)(" + table + ")";
	}

	/**
	 * 验证是否存在需要替换的表名称
	 * 
	 * @param sql
	 * @param table
	 * @return
	 */
	public boolean matches(String sql, String table) {
		Pattern pattern = Pattern.compile(getRegex(table));
		Matcher matcher = pattern.matcher(sql);
		char[] data = sql.toCharArray();
		boolean matche = matcher.find();
		while (matche) {
			int start = matcher.start();
			int end = matcher.end();
			if (checkStart(start, data) && checkEnd(end, data)) {
				return true;
			}
			matche = matcher.find();
		}
		return false;
	}

}
