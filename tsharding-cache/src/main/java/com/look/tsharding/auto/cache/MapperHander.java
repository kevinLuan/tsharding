package com.look.tsharding.auto.cache;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import com.alibaba.druid.support.json.JSONWriter;
import com.look.tsharding.utils.MapperUtils;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.tsharding.filter.InvocationProxy;

public final class MapperHander {
  private final Method method;
  private final DataSourceRouting routing;
  private final Object[] args;
  private final Class<?> mapper;

  public MapperHander(InvocationProxy invocation) {
    this.method = invocation.getInvocation().getMethod();
    this.routing = MapperUtils.getDataSourceRouting(invocation);
    this.args = invocation.getInvocation().getArgs();
    this.mapper = invocation.getInvocation().getMapperClass();
  }

  public boolean hasAnnotation(Class<? extends Annotation> annotation) {
    return method.getAnnotation(annotation) != null;
  }

  public String getLogInfo(long useTime) {
    StringBuilder builder = new StringBuilder(500);
    builder.append("prefix:tsharding|");
    builder.append("mapper:" + mapper.getSimpleName() + "." + method.getName()).append("|");
    builder.append("dataSource:" + routing.dataSource()).append("|");
    builder.append("args:" + getParams()).append("|");
    builder.append("useTime:" + useTime).append("|");
    return builder.toString();

  }

  private String getParams() {
    if (args == null) {
      return "[]";
    } else {
      JSONWriter out = new JSONWriter();
      out.writeArrayStart();
      for (int i = 0; i < args.length; i++) {
        if (i != 0) {
          out.writeComma();
        }
        Object value = args[i];
        if (value == null) {
          out.writeNull();
        } else {
          if (value instanceof String) {
            String text = (String) value;
            if (text.length() > 100) {
              out.writeString(text.substring(0, 97) + "...");
            } else {
              out.writeString(text);
            }
          } else if (value instanceof Number) {
            out.writeObject(value);
          } else if (value instanceof java.util.Date) {
            out.writeObject(value);
          } else if (value instanceof Boolean) {
            out.writeObject(value);
          } else if (value instanceof InputStream) {
            out.writeString("<InputStream>");
          } else if (value instanceof NClob) {
            out.writeString("<NClob>");
          } else if (value instanceof Clob) {
            out.writeString("<Clob>");
          } else if (value instanceof Blob) {
            out.writeString("<Blob>");
          } else {
            out.writeString('<' + value.getClass().getName() + '>');
          }
        }
      }
      out.writeArrayEnd();
      return out.toString();
    }
  }

}
