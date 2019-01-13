package com.mogujie.trade.tsharding.route.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.hander.SQLEnhancerHander;
import com.mogujie.trade.utils.TShardingLog;

/**
 * Mappper sql增强
 * <p>
 * 扩展MapperResourceEnhancer不支持嵌套多标签的增强SQL处理
 * </p>
 */
@SuppressWarnings("unchecked")
public class MapperResourceEnhancerNew extends MapperEnhancer {

  public MapperResourceEnhancerNew(Class<?> mapperClass) {
    super(mapperClass);
  }

  private SQLEnhancerHander getShardingTableHander(MappedStatement ms) {
    Class<?> clazzClass = getMapperClass(ms.getId());
    DataSourceRouting sharding = clazzClass.getAnnotation(DataSourceRouting.class);
    Class<?> enhancerHanderClass = sharding.sqlEnhancerHander();
    try {
      SQLEnhancerHander hander = (SQLEnhancerHander) enhancerHanderClass.getConstructor(Class.class)
          .newInstance(getMapperClass(ms.getId()));
      return hander;
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isStaticTextSqlNode(SqlNode node) {
    return node instanceof StaticTextSqlNode;
  }

  private Field getField(Class<?> clazz, String fieldName, Exception ex) throws Exception {
    try {
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field;
    } catch (Exception e) {
      if (clazz.getSuperclass() != null) {
        return getField(clazz.getSuperclass(), fieldName, e);
      }
      throw new RuntimeException("没有找到`" + fieldName + "`字段定义", e);
    }
  }

  private void processDynamicSqlSource(SQLEnhancerHander hander, DynamicSqlSource sqlSource,
      Long shardingPara) throws Exception {
    try {
      Class<?> sqlSourceClass = sqlSource.getClass();
      Field sqlNodeField = sqlSourceClass.getDeclaredField("rootSqlNode");
      sqlNodeField.setAccessible(true);
      MixedSqlNode rootSqlNode = (MixedSqlNode) sqlNodeField.get(sqlSource);
      processSqlNode(hander, rootSqlNode, shardingPara);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void processSqlNode(SQLEnhancerHander hander, SqlNode sqlNode, Long shardingPara)
      throws Exception {
    try {
      Field contentsField = getField(sqlNode.getClass(), "contents", null);
      List<SqlNode> textSqlNodes = (List<SqlNode>) contentsField.get(sqlNode);
      for (int i = 0; i < textSqlNodes.size(); i++) {
        SqlNode node = textSqlNodes.get(i);
        if (isStaticTextSqlNode(node)) {
          processTextSqlNode(hander, (StaticTextSqlNode) node, shardingPara);
        } else if (isDeclaredField(node.getClass(), "contents")) {
          Field contents_field = getField(node.getClass(), "contents", null);
          Object value = contents_field.get(node);
          if (value != null) {
            if (List.class.isAssignableFrom(value.getClass())) {
              List<SqlNode> sqlNodes = (List<SqlNode>) contents_field.get(node);
              for (int n = 0; n < sqlNodes.size(); n++) {
                processSqlNode(hander, sqlNodes.get(n), shardingPara);
              }
            } else if (SqlNode.class.isAssignableFrom(value.getClass())) {
              processSqlNode(hander, (SqlNode) value, shardingPara);
            } else {
              TShardingLog.getLogger().warn("MapperResourceEnhancer--->>>>未知的类型:" + value);
            }
          }
        } else {
          TShardingLog.getLogger().warn("MapperResourceEnhancer--->>>>skip:" + node.getClass());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean isDeclaredField(Class<?> clazz, String fieldName) {
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (field.getName().equals(fieldName)) {
        return true;
      }
    }
    // 从父类查询
    if (clazz.getSuperclass() != null) {
      return isDeclaredField(clazz.getSuperclass(), fieldName);
    }
    return false;
  }

  /**
   * 处理SQL语句
   * 
   * @param hander
   * @param textSqlNode
   * @param shardingPara
   * @throws Exception
   */
  private void processTextSqlNode(SQLEnhancerHander hander, StaticTextSqlNode textSqlNode,
      Long shardingPara) throws Exception {
    Field textField = getField(textSqlNode.getClass(), "text", null);
    String text = (String) textField.get(textSqlNode);
    if (hander.hasReplace(text)) {
      String sql = hander.format(text, shardingPara);
      textField.set(textSqlNode, sql); // 修改SQL语句
    }
  }

  public SqlSource enhancedShardingSQL(MappedStatement ms, Configuration configuration,
      Long shardingPara) {
    SQLEnhancerHander hander = getShardingTableHander(ms);
    TShardingLog.getLogger().debug(ms.getId());
    try {
      if (ms.getSqlSource() instanceof DynamicSqlSource) {
        processDynamicSqlSource(hander, (DynamicSqlSource) ms.getSqlSource(), shardingPara);
        return ms.getSqlSource();
      } else if (ms.getSqlSource() instanceof RawSqlSource) {
        RawSqlSource sqlSource = (RawSqlSource) ms.getSqlSource();
        Class<?> sqlSourceClass = sqlSource.getClass();
        Field sqlSourceField = sqlSourceClass.getDeclaredField("sqlSource");
        sqlSourceField.setAccessible(true);
        StaticSqlSource staticSqlSource = (StaticSqlSource) sqlSourceField.get(sqlSource);
        Field sqlField = staticSqlSource.getClass().getDeclaredField("sql");
        Field parameterMappingsField =
            staticSqlSource.getClass().getDeclaredField("parameterMappings");
        sqlField.setAccessible(true);
        parameterMappingsField.setAccessible(true);
        String sql = (String) sqlField.get(staticSqlSource);
        if (hander.hasReplace(sql)) {
          sql = hander.format(sql, shardingPara);
          SqlSource result = new RawSqlSource(configuration, sql, null);
          // 为sqlSource对象设置mappering参数
          StaticSqlSource newStaticSqlSource = (StaticSqlSource) sqlSourceField.get(result);
          List<ParameterMapping> parameterMappings =
              (List<ParameterMapping>) parameterMappingsField.get(staticSqlSource);
          parameterMappingsField.set(newStaticSqlSource, parameterMappings);
          return result;
        } else {
          return sqlSource;
        }
      } else {
        throw new RuntimeException("wrong sqlSource type!" + ms.getResource());
      }
    } catch (Exception e) {
      TShardingLog.error("reflect error!, ms resources:" + ms.getResource(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
