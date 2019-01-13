package com.mogujie.tsharding.filter;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;

import com.alibaba.druid.support.json.JSONWriter;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.route.orm.base.Invocation;

public class TshardingHander {
	private Object[] args;
	private Class<?> mapper;
	private Method method;
	private boolean isSharding;
	private DataSourceRouting dataSourceRouting;
	private long useTime;

	public TshardingHander(Invocation invocation, long useTime) {
		args = invocation.getArgs();
		mapper = invocation.getMapperClass();
		method = invocation.getMethod();
		isSharding = invocation.isSharding();
		dataSourceRouting = invocation.getDataSourceRouting();
		this.useTime = useTime;
	}

	public String getDataSource() {
		if (dataSourceRouting == null) {
			return null;
		}
		return dataSourceRouting.dataSource();
	}

	public String getTable() {
		if (dataSourceRouting != null) {
			return dataSourceRouting.table();

		}
		return null;
	}

	public String getParams() {
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

	public Object[] getArgs() {
		return args;
	}

	public Class<?> getMapper() {
		return mapper;
	}

	public Method getMethod() {
		return method;
	}

	public boolean isSharding() {
		return isSharding;
	}

	public DataSourceRouting getDataSourceRouting() {
		return dataSourceRouting;
	}

	public long getUseTime() {
		return useTime;
	}

	@Override
	public String toString() {
		return "prefix:tsharding|mapper:" + mapper.getName() + "." + method.getName() + "|dataSource:" + getDataSource()
				+ "|args:" + getParams() + "|useTime:" + useTime;

	}
}
