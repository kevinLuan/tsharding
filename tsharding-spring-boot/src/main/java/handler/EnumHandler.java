package handler;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.ProtocolMessageEnum;

/**
 * 通用枚举转换处理
 * 
 * @author KEVIN LUAN
 *
 */
@MappedJdbcTypes(value = {JdbcType.TINYINT})
@MappedTypes({ProtocolMessageEnum.class})
public class EnumHandler extends BaseTypeHandler<ProtocolMessageEnum> {
  public Class<?> type;
  private Method method;
  private static final Logger LOGGER = LoggerFactory.getLogger(EnumHandler.class);

  public EnumHandler(Class<?> type) throws NoSuchMethodException, SecurityException {
    this.type = type;
    try {
      this.method = type.getMethod("forNumber", int.class);
    } catch (Exception e) {
      LOGGER.error("type:`" + type + "` getMethod:`forNumber` ERROR", e);
    }
  }

  public EnumHandler() {}

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, ProtocolMessageEnum parameter,
      JdbcType jdbcType) throws SQLException {
    ps.setInt(i, parameter.getNumber());
  }

  private ProtocolMessageEnum forNumber(int value) {
    try {
      return (ProtocolMessageEnum) method.invoke(null, value);
    } catch (Exception e) {
      LOGGER.error("type:`" + type + "`.forNumber() ERROR", e);
      throw new RuntimeException("type:`" + type + "`.forNumber() ERROR", e);
    }
  }

  @Override
  public ProtocolMessageEnum getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    int value = rs.getInt(columnName);
    return forNumber(value);
  }

  @Override
  public ProtocolMessageEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    int value = rs.getInt(columnIndex);
    return forNumber(value);
  }

  @Override
  public ProtocolMessageEnum getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    int value = cs.getInt(columnIndex);
    return forNumber(value);
  }

}
