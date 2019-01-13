package com.lyh.tsharding.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

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
}
