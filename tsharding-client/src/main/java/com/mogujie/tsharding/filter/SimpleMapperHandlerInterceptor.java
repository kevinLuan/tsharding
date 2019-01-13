package com.mogujie.tsharding.filter;

import com.mogujie.trade.utils.TShardingLog;

/**
 * 实现简单的Mapper拦截器，这里只实现了对所有db操作进行记录日志。
 * 
 * @author kevin
 *
 */
public class SimpleMapperHandlerInterceptor implements MapperHandlerInterceptor {

	@Override
	public Object invoker(InvocationProxy invocation) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return invocation.doInvoker();
		} finally {
			long useTime = System.currentTimeMillis() - start;
			TshardingHander hander = new TshardingHander(invocation.getInvocation(), useTime);
			TShardingLog.getLogger().info(hander.toString());
		}
	}

}
