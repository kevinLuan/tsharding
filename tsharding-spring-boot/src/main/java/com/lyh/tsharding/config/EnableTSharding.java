package com.lyh.tsharding.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.look.tsharding.auto.cache.CacheMapperHandlerInterceptor;
import com.mogujie.tsharding.filter.MapperHandlerInterceptor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(EnableConfigRegistry.class)
public @interface EnableTSharding {
  /**
   * 配置Mappper包路径
   * 
   * @return
   */
  public String[] mapperPackage();

  /**
   * 配置分库分表的Mapper Class 定义增强Mapper
   * <p>
   * 增加自动发现功能,根据：this.mapperPackage()配置的包路径下的Mapper注解自动发现是分库或分表Mapper进行增强处理
   * </p>
   * 
   * @return
   */
  @Deprecated
  public Class<?>[] enhancedMappers() default {};

  /**
   * 定义mapper.xml配置路径
   * 
   * @return
   */
  public String mapperLocations() default "classpath*:sqlmap/*/*Mapper.xml";

  /**
   * Mybatis配置文件（选填） classpath:mybatis-config.xml
   * 
   * @return
   */
  public String configLocation() default "";

  /**
   * 定义数据库连接池类型
   * 
   * @return
   */
  public DataSourceType dataSourceType() default DataSourceType.Hikari;

  /**
   * TSharding 拦截器
   * 
   * @return
   */
  public Class<? extends MapperHandlerInterceptor> interceptor() default CacheMapperHandlerInterceptor.class;
}
