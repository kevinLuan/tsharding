package com.look.tsharding.auto.cache;

import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.mogujie.tsharding.filter.HandlerInterceptorAdapterFactory;

@Component
public class RegisterTshardingCacheInterceptor implements ApplicationContextAware {
  private ApplicationContext context;

  @PostConstruct
  private void register() {
    try {
      CacheMapperHandlerInterceptor interceptor =
          context.getAutowireCapableBeanFactory().createBean(CacheMapperHandlerInterceptor.class);
      HandlerInterceptorAdapterFactory.registerInterceptor(interceptor);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;

  }
}
