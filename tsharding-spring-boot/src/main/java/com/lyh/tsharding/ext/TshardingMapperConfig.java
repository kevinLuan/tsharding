package com.lyh.tsharding.ext;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.env.Environment;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.utils.TShardingLog;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Tsharding配置文件处理器
 * 
 * @author SHOUSHEN LUAN
 */
@Slf4j
@Data(staticConstructor = "of")
public final class TshardingMapperConfig {
  List<EnvironmentConfig> environments = new ArrayList<>();
  private final Environment environment;

  public TshardingMapperConfig parse(InputStream inputStream) throws DocumentException {
    SAXReader saxReader = new SAXReader();
    Document root = saxReader.read(inputStream);
    if ("environments".equalsIgnoreCase(root.getRootElement().getName())) {
      Element nodeElement = root.getRootElement();
      @SuppressWarnings("unchecked")
      List<Element> elements = nodeElement.elements();
      for (int i = 0; i < elements.size(); i++) {
        Element element = elements.get(i);
        EnvironmentConfig environment = EnvironmentConfig.parser(element);
        environments.add(environment);
      }
      return this;
    }
    throw new IllegalArgumentException("无效的配置文件");
  }

  /**
   * Tsharding 环境配置
   * 
   * @author SHOUSHEN LUAN
   */
  public static class EnvironmentConfig {
    String name;

    public EnvironmentConfig(String name) {
      this.name = name;
    }

    private List<Mapper> mappers = new ArrayList<>();

    public static EnvironmentConfig parser(Element element) {
      if ("environment".equalsIgnoreCase(element.getName())) {
        EnvironmentConfig environment = new EnvironmentConfig(element.attributeValue("name"));
        Element mappers = element.element("Mappers");
        environment.loadMappers(mappers);
        return environment;
      }
      return null;
    }

    public void loadMappers(Element element) {
      if (element == null) {
        return;
      }
      @SuppressWarnings("unchecked")
      List<Element> mapperList = element.elements();
      for (int i = 0; i < mapperList.size(); i++) {
        Element mapper = mapperList.get(i);
        if (mapper.getName().equalsIgnoreCase("Mapper")) {
          mappers.add(Mapper.parser(mapper));
        }
      }
    }

    public Mapper getMapper(Class<?> clazz) {
      for (Mapper mapper : mappers) {
        if (mapper.matches(clazz)) {
          return mapper;
        }
      }
      return null;
    }

    public boolean exists(Class<?> clazz) {
      for (Mapper mapper : mappers) {
        if (mapper.matches(clazz)) {
          return true;
        }
      }
      return false;
    }

  }

  /**
   * TSharding Mapper配置
   * 
   * @author SHOUSHEN LUAN
   */
  public static class Mapper {
    Class<?> mapperClazz;
    String dataSource;
    Integer databases;
    String table;
    Integer tables;
    Boolean isReadWriteSplitting;

    void parserMapperClass(Element element) {
      String value = element.attributeValue("class");
      if (value == null || value.trim().length() == 0) {
        throw new IllegalArgumentException("<Mapper class='必须的'/>");
      }
      try {
        this.mapperClazz = Class.forName(value.trim());
      } catch (Exception ex) {
        ex.printStackTrace();
        throw new IllegalArgumentException("<Mapper class='" + value + "'... 无效的Class");
      }
      if (!this.mapperClazz.isInterface()) {
        throw new IllegalArgumentException("<Mapper class='" + value + "'... 不合法的Class");
      }
    }

    public boolean matches(Class<?> clazz) {
      if (clazz == null) {
        return false;
      }
      return this.mapperClazz.getName().equals(clazz.getName());
    }

    public static Mapper parser(Element element) {
      Mapper mapper = new Mapper();
      mapper.parserMapperClass(element);
      String val = null;
      mapper.dataSource = element.attributeValue("dataSource");
      try {
        val = element.attributeValue("databases");
        if (val != null) {
          mapper.databases = Integer.parseInt(val);
        }
      } catch (Exception ex) {
        throw new IllegalArgumentException("databases=`" + val + "`");
      }
      mapper.table = element.attributeValue("table");
      try {
        val = element.attributeValue("tables");
        if (val != null) {
          mapper.tables = Integer.parseInt(element.attributeValue("tables"));
        }
      } catch (Exception ex) {
        throw new IllegalArgumentException("tables=`" + val + "`");
      }
      try {
        val = element.attributeValue("isReadWriteSplitting");
        if (val != null) {
          mapper.isReadWriteSplitting = Boolean.parseBoolean(val);
        }
      } catch (Exception ex) {
        throw new IllegalArgumentException("isReadWriteSplitting=`" + val + "`");
      }

      return mapper;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void modifyAnnotation(DataSourceRouting routing)
        throws NoSuchFieldException, IllegalAccessException {
      InvocationHandler h = Proxy.getInvocationHandler(routing);
      Field hField = h.getClass().getDeclaredField("memberValues");
      hField.setAccessible(true);
      Map memberValues = (Map) hField.get(h);
      if (this.dataSource != null) {
        memberValues.put("dataSource", this.dataSource);
      }
      if (table != null) {
        memberValues.put("table", table);
      }
      if (tables != null) {
        memberValues.put("tables", tables);
      }
      if (databases != null) {
        memberValues.put("databases", databases);
      }
      if (this.isReadWriteSplitting != null) {
        memberValues.put("isReadWriteSplitting", isReadWriteSplitting);
      }
    }
  }

  /**
   * 获取环境配置
   * 
   * @param env
   * @return
   */
  public EnvironmentConfig getEnvironment(String env) {
    for (EnvironmentConfig environment : environments) {
      if (environment.name.equalsIgnoreCase(env)) {
        return environment;
      }
    }
    // null环境
    return new EnvironmentConfig(env);
  }

  public void modifyAnnotation(Set<Class<?>> mappers)
      throws NoSuchFieldException, IllegalAccessException {
    String env = getSystemEnv();
    EnvironmentConfig environment = getEnvironment(env);
    for (Class<?> clazz : mappers) {
      if (environment.exists(clazz)) {
        Mapper mapper = environment.getMapper(clazz);
        DataSourceRouting routing = clazz.getAnnotation(DataSourceRouting.class);
        mapper.modifyAnnotation(routing);
        TShardingLog.getLogger().warn("当前系统环境：" + env + "|修改Mapper:" + clazz);
      }
    }
  }

  /**
   * 获取系统环境变量
   *
   * @return
   */
  private String getSystemEnv() {
    String env = null;
    if (environment != null) {
      String[] activeProfiles = environment.getActiveProfiles();
      if (activeProfiles != null && activeProfiles.length > 0) {
        env = activeProfiles[0];
      } else {
        env = environment.getProperty("spring.profiles.active");
      }
    }
    if (StringUtils.isBlank(env)) {
      log.error("没有获取到有效的环境->spring.profiles.active");
    }
    return env;
  }
}
