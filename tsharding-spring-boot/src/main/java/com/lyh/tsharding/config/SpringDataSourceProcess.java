package com.lyh.tsharding.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;

public class SpringDataSourceProcess {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringDataSourceProcess.class);

  public static void process() {
    LOGGER.warn("Tsharding.SpringBoot.process start.............");
    if (DataSourceBuilder.findType(DataSourceBuilder.class.getClassLoader()) != null) {
      try {
        Field nameField = DataSourceBuilder.class.getDeclaredField("DATA_SOURCE_TYPE_NAMES");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
        nameField.setAccessible(true);
        nameField.set(null, new String[0]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
