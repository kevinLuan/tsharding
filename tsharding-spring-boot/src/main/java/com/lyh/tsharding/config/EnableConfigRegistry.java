package com.lyh.tsharding.config;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import com.lyh.tsharding.ext.TshardingMapperConfig;
import com.lyh.tsharding.utils.ClassNameHelper;
import com.mogujie.distributed.transction.ChainedTransactionInteceptor;
import com.mogujie.distributed.transction.DynamicTransctionManagerFactory;
import com.mogujie.trade.db.DataSourceScanner;
import com.mogujie.trade.db.DruidDataSourceFactory;
import com.mogujie.trade.db.HikariDataSourceFactory;
import com.mogujie.trade.db.ReadWriteSplittingAdvice;
import com.mogujie.trade.tsharding.route.orm.MapperScannerWithSharding;
import com.mogujie.trade.tsharding.route.orm.MapperShardingInitializer;
import com.mogujie.trade.utils.TShardingLog;

class EnableConfigRegistry implements ImportBeanDefinitionRegistrar, ResourceLoaderAware,
    BeanClassLoaderAware, EnvironmentAware {
  private Environment environment;

  public EnableConfigRegistry() {}

  @Override
  public void registerBeanDefinitions(AnnotationMetadata metadata,
      BeanDefinitionRegistry registry) {
    Map<String, Object> defaultAttrs =
        metadata.getAnnotationAttributes(EnableTSharding.class.getName(), true);
    String[] mapperPackage = (String[]) defaultAttrs.get("mapperPackage");
    Set<Class<?>> mappers = MapperScannerWithSharding.scanMapper(mapperPackage);
    mapperConfigProcess(mappers);
    registerMapper(defaultAttrs, registry);
    registerEnhancedMappers(defaultAttrs, registry, mappers);
    registerDataSourceFactory(registry, defaultAttrs);
    registerDataSourceScanner(registry);
    registerReadWriteSplittingAdvice(registry);
    // registerMapperHandlerInterceptor(registry, defaultAttrs);
    registerDynamicTransctionManagerFactory(registry);
    registerChainedTransactionInteceptor(registry);
  }

  /**
   * Mapper配置处理
   * 
   * @param mappers
   */
  private void mapperConfigProcess(Set<Class<?>> mappers) {
    try {
      if (TShardingLog.getLogger().isDebugEnabled()) {
        dump();
      }
      InputStream inputStream = getResource();
      if (inputStream != null) {
        TshardingMapperConfig config = TshardingMapperConfig.of(environment).parse(inputStream);
        config.modifyAnnotation(mappers);
      }
    } catch (Exception e) {
      TShardingLog.error("mapperConfigProcess error:", e);
    }
  }

  private void dump() {
    try {
      InputStream inputStream = getResource();
      if (inputStream != null) {
        int length = inputStream.available();
        byte[] data = new byte[length];
        inputStream.read(data);
        TShardingLog.getLogger().info("tsharding_mapper_config.xml data:" + new String(data));
        inputStream.close();
      }
    } catch (Exception e) {
      TShardingLog.error("dump error:", e);
    }
  }

  private InputStream getResource() {
    String value =
        System.getProperty("mapper.config.path", "classpath:tsharding_mapper_config.xml");
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource resource = resolver.getResource(value);
      return resource.getInputStream();
    } catch (java.io.FileNotFoundException e) {
      TShardingLog.getLogger().info("no config:" + value);
      return null;
    } catch (Exception e) {
      TShardingLog.error("mapperConfigProcess error:", e);
      return null;
    }
  }

  /**
   * 注册链式事物管理器
   * 
   * @param registry
   */
  private void registerChainedTransactionInteceptor(BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder definitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(ChainedTransactionInteceptor.class);
    String name = ChainedTransactionInteceptor.class.getSimpleName();
    registry.registerBeanDefinition(name, definitionBuilder.getBeanDefinition());
  }

  /**
   * 注册动态事物管理器工厂
   * 
   * @param registry
   */
  private void registerDynamicTransctionManagerFactory(BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder definitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(DynamicTransctionManagerFactory.class);
    String name = DynamicTransctionManagerFactory.class.getSimpleName();
    registry.registerBeanDefinition(name, definitionBuilder.getBeanDefinition());
  }

  // /**
  // * 注册Mapper拦截器
  // *
  // * @param registry
  // */
  // @SuppressWarnings("unchecked")
  // private void registerMapperHandlerInterceptor(BeanDefinitionRegistry
  // registry, Map<String, Object> defaultAttrs) {
  //// String interceptorName = (String) defaultAttrs.get("interceptor");
  //// if (interceptorName == null) {
  //// return;// 禁用拦截器
  //// }
  //// Class<? extends MapperHandlerInterceptor> interceptor = null;
  //// try {
  //// interceptor = (Class<? extends MapperHandlerInterceptor>)
  // Class.forName(interceptorName);
  ////
  //// BeanDefinitionBuilder interceptorBuilder =
  // BeanDefinitionBuilder.genericBeanDefinition(interceptor);
  //// String name = interceptor.getClass().getSimpleName();
  //// registry.registerBeanDefinition(name,
  // interceptorBuilder.getBeanDefinition());
  ////
  //// BeanDefinitionBuilder adapterBuilder = BeanDefinitionBuilder
  //// .genericBeanDefinition(HandlerInterceptorAdapterFactory.class);
  //// adapterBuilder.addConstructorArgReference(name);
  //// String handleName =
  // HandlerInterceptorAdapterFactory.class.getSimpleName();
  //// registry.registerBeanDefinition(handleName,
  // adapterBuilder.getBeanDefinition());
  ////
  //// } catch (ClassNotFoundException e) {
  //// e.printStackTrace();
  //// }
  //
  // }

  /**
   * 注册Mapper
   * 
   * @param defaultAttrs
   * @param registry
   */
  private void registerMapper(Map<String, Object> defaultAttrs, BeanDefinitionRegistry registry) {
    String[] mapperPackage = (String[]) defaultAttrs.get("mapperPackage");
    BeanDefinitionBuilder definitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(MapperScannerWithSharding.class);
    String value = ClassNameHelper.build(mapperPackage);
    definitionBuilder.addPropertyValue("packageName", value);
    definitionBuilder.addPropertyValue("mapperLocations", defaultAttrs.get("mapperLocations"));
    String myBatisConfig = (String) defaultAttrs.get("configLocation");
    if (StringUtils.isNoneBlank(myBatisConfig)) {
      definitionBuilder.addPropertyValue("configLocation", defaultAttrs.get("configLocation"));
    }
    registry.registerBeanDefinition("MapperScannerWithSharding",
        definitionBuilder.getBeanDefinition());
  }

  /**
   * 注册增强Mapper
   * 
   * @param defaultAttrs
   * @param registry
   */
  private void registerEnhancedMappers(Map<String, Object> defaultAttrs,
      BeanDefinitionRegistry registry, Set<Class<?>> mappers) {
    String[] enhancedMappers = (String[]) defaultAttrs.get("enhancedMappers");
    String value = ClassNameHelper.build(enhancedMappers, mappers);
    System.out.println("注册增强Mapper:" + value);
    if (StringUtils.isNotBlank(value)) {
      BeanDefinitionBuilder definitionBuilder =
          BeanDefinitionBuilder.genericBeanDefinition(MapperShardingInitializer.class);

      definitionBuilder.addPropertyValue("needEnhancedClasses", value);
      registry.registerBeanDefinition("MapperShardingInitializer",
          definitionBuilder.getBeanDefinition());
    }
  }

  /**
   * 注册DataSource工厂
   * 
   * @param registry
   */
  private void registerDataSourceFactory(BeanDefinitionRegistry registry,
      Map<String, Object> defaultAttrs) {
    DataSourceType dataSourceType = (DataSourceType) defaultAttrs.get("dataSourceType");
    if (DataSourceType.Druid.equals(dataSourceType)) {// 阿里连接池
      BeanDefinitionBuilder definitionBuilder =
          BeanDefinitionBuilder.genericBeanDefinition(DruidDataSourceFactory.class);
      registry.registerBeanDefinition("dataSourceFactory", definitionBuilder.getBeanDefinition());
    } else {
      BeanDefinitionBuilder definitionBuilder =
          BeanDefinitionBuilder.genericBeanDefinition(HikariDataSourceFactory.class);
      registry.registerBeanDefinition("dataSourceFactory", definitionBuilder.getBeanDefinition());
    }
  }

  /**
   * 注册DataSource
   * 
   * @param registry
   */
  private void registerDataSourceScanner(BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder definitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(DataSourceScanner.class);
    definitionBuilder.addPropertyReference("dataSourceFactory", "dataSourceFactory");
    registry.registerBeanDefinition("dataSourceScanner", definitionBuilder.getBeanDefinition());
  }

  private void registerReadWriteSplittingAdvice(BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder definitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(ReadWriteSplittingAdvice.class);
    registry.registerBeanDefinition("ReadWriteSplittingAdvice",
        definitionBuilder.getBeanDefinition());
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {}

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {}
}
