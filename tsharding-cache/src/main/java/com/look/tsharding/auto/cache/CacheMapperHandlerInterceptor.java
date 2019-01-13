package com.look.tsharding.auto.cache;

import org.springframework.beans.factory.annotation.Autowired;
import com.mogujie.trade.utils.TShardingLog;
import com.mogujie.tsharding.filter.InvocationProxy;
import com.mogujie.tsharding.filter.MapperHandlerInterceptor;

/**
 * 支持缓存处理的拦截器
 * 
 * @author SHOUSHEN LUAN
 *
 */
public class CacheMapperHandlerInterceptor implements MapperHandlerInterceptor {
  @Autowired
  private ProxyCacheHandler proxyCacheHandler;

  @Override
  public Object invoker(InvocationProxy invocation) throws Throwable {
    return proxyCacheHandler.invoke(invocation, this);
  }

  /**
   * 执行DB操作
   * 
   * @param invocation
   * @param mapperHander
   * @return
   * @throws Throwable
   */
  public Object doInvoker(InvocationProxy invocation, MapperHander mapperHander) throws Throwable {
    long start = System.currentTimeMillis();
    try {
      return invocation.doInvoker();
    } finally {
      long useTime = System.currentTimeMillis() - start;
      // 每次DB操作均记录日志，日后可以根据开关控制
      TShardingLog.getLogger().info(mapperHander.getLogInfo(useTime));
    }
  }
}
