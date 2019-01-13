package com.mogujie.trade.hander;

import java.util.Set;

import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.utils.ReplaceTableName;
import com.mogujie.trade.utils.TShardingLog;

public class DefaultSQLEnhancerHander implements SQLEnhancerHander {
	protected Class<?> mappedClass;
	private DataSourceRouting routing;

	public DefaultSQLEnhancerHander(Class<?> mappedClass) {
		this.mappedClass = mappedClass;
		this.routing = mappedClass.getAnnotation(DataSourceRouting.class);
	}

	public String getTable(long value) {
		if (routing.tables() > 1) {
			String table = routing.table();
			String tableSuffix = RouteRuleFactory.getRouteRule(mappedClass).fillBit(value);
			return table + tableSuffix;
		} else {
			return routing.table();
		}
	}

	@Override
	public boolean hasReplace(String sql) {
		Set<Class<?>> mappers = MapperFactory.getEnhanceMappers();
		for (Class<?> mapper : mappers) {
			DataSourceRouting routing = mapper.getAnnotation(DataSourceRouting.class);
			if (routing.tables() > 1) {
				String table = routing.table();
				if (ReplaceTableName.getInstance().matches(sql, table)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String format(String sql, long value) {
		TShardingLog.getLogger().debug("raw.SQL:" + sql);
		sql = sql.replaceAll("\n", " ");
		sql = sql.replaceAll("\t", " ");
		sql = sql.replaceAll("  ", " ");
		Set<Class<?>> mappers = MapperFactory.getEnhanceMappers();
		for (Class<?> mapper : mappers) {
			DataSourceRouting routing = mapper.getAnnotation(DataSourceRouting.class);
			if (routing.tables() > 1) {
				String table = routing.table();
				String tableSuffix = RouteRuleFactory.getRouteRule(mapper).fillBit(value);
				String newTable = table + tableSuffix;
				sql = ReplaceTableName.getInstance().replace(sql, table, newTable);
			}
		}
		TShardingLog.getLogger().debug("SQL:" + sql);
		return sql;
	}

}
