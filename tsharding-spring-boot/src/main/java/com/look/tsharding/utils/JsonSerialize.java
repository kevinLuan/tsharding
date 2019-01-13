package com.look.tsharding.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonSerialize {
  public static final ObjectMapper mapper = new ObjectMapper();
  static {
    // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    // // mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    // // mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    // SimpleModule module = new SimpleModule();
    // module.addDeserializer(Date.class, new DateDeserializer());
    // mapper.registerModule(module);
    // // 解决jackson序列化时默认的时区是UTC导致Date类型慢8小时问题
    // mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
  }

  @SneakyThrows
  public static String encode(Object obj) {
    return mapper.writeValueAsString(obj);
  }

  @SneakyThrows
  public static <T> T decode(String json, Class<T> type) {
    return mapper.readValue(json, type);
  }
}
