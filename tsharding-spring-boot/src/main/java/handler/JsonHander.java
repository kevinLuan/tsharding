package handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import com.google.protobuf.GeneratedMessageV3;
import com.look.tsharding.utils.JsonSerialize;

/**
 * 通用Proto Message转换处理
 * 
 * @author SHOUSHEN LUAN
 *
 */
@MappedJdbcTypes(value = {JdbcType.TINYINT})
@MappedTypes({GeneratedMessageV3.class})
public class JsonHander extends BaseTypeHandler<GeneratedMessageV3> {
  public Class<?> type;

  public JsonHander(Class<?> type) throws NoSuchMethodException, SecurityException {
    this.type = type;
  }

  public JsonHander() {}

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, GeneratedMessageV3 parameter,
      JdbcType jdbcType) throws SQLException {
    if (parameter != null) {
      String json = JsonSerialize.encode(parameter);
      ps.setString(i, json);
    } else {
      ps.setString(i, null);
    }
  }

  private GeneratedMessageV3 jsonAsBean(String value) {
    if (value != null && value.length() > 0) {
      return (GeneratedMessageV3) JsonSerialize.decode(value, type);
    }
    return null;
  }

  @Override
  public GeneratedMessageV3 getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String value = rs.getString(columnName);
    return jsonAsBean(value);
  }

  @Override
  public GeneratedMessageV3 getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String value = rs.getString(columnIndex);
    return jsonAsBean(value);
  }

  @Override
  public GeneratedMessageV3 getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    String value = cs.getString(columnIndex);
    return jsonAsBean(value);
  }

}
