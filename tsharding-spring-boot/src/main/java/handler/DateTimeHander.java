package handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * 数据类型转换从：代码中Long类型转到到Mysql中的datetime类型 pojo.Long->mysql.DateTime
 * 
 * @author SHOUSHEN LUAN
 *
 */
@MappedJdbcTypes(value = {JdbcType.TIMESTAMP})
@MappedTypes({Long.class, long.class})
public class DateTimeHander extends BaseTypeHandler<Long> {
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setTimestamp(i, new Timestamp(parameter));
  }

  @Override
  public Long getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(columnName);
    if (timestamp != null) {
      return timestamp.getTime();
    } else {
      return 0L;
    }
  }

  @Override
  public Long getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(columnIndex);
    if (timestamp != null) {
      return timestamp.getTime();
    } else {
      return 0L;
    }
  }

  @Override
  public Long getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Timestamp timestamp = cs.getTimestamp(columnIndex);
    if (timestamp != null) {
      return timestamp.getTime();
    } else {
      return 0L;
    }
  }

}
