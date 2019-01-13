package com.mogujie.distributed.transction;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.mogujie.trade.utils.TransactionResult;

public class DefaultUnfinishedCallback {
	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultUnfinishedCallback.class);
	private static Gson gson = new Gson();

	public static void unfinishedCallback(ProxyMethodMeta proxyMethod) {
		Map<String, Object> transctionData = new HashMap<>();
		String name = proxyMethod.getTarget().getClass().getName();
		String proxyMethodName = proxyMethod.getProxyMethod().getName();
		Object[] args = proxyMethod.getArgs();
		TransactionResult result = proxyMethod.getTransactionResult();
		transctionData.put("name", name);
		transctionData.put("method", proxyMethodName);
		transctionData.put("args", args);
		transctionData.put("type", result.getType());
		transctionData.put("message", result.getCause().getMessage());
		transctionData.put("cause", result.getCause());
		LOGGER.warn("异常事物数据:{}", gson.toJson(transctionData));
	}

}
