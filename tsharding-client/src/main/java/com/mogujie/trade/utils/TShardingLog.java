package com.mogujie.trade.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中间件日志
 * 
 * @author SHOUSHEN LUAN create date: 2017年1月7日
 */
public class TShardingLog {
	private final static Logger LOGGER = LoggerFactory.getLogger(TShardingLog.class);
	public static final String SYSTEM_INFO = "[TSharding]";

	public static void warn(String msg, Object... param) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn(SYSTEM_INFO + msg, param);
		}
	}

	public static void info(String msg, Object... param) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(SYSTEM_INFO + msg, param);
		}
	}

	public static void error(String msg, Object... param) {
		if (LOGGER.isErrorEnabled()) {
			LOGGER.error(SYSTEM_INFO + msg, param);
		}
	}

	public static void error(String msg, Throwable th) {
		if (LOGGER.isErrorEnabled()) {
			LOGGER.error(SYSTEM_INFO + msg, th);
		}
	}

	public static void debug(String msg, Object... param) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SYSTEM_INFO + msg, param);
		}
	}

	public static Logger getLogger() {
		return LOGGER;
	}

}
