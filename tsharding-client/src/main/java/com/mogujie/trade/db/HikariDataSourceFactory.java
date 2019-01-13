package com.mogujie.trade.db;

import java.sql.SQLException;

import com.mogujie.trade.utils.TShardingLog;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 使用HikariCP连接池
 * <p>
 * https://github.com/brettwooldridge/HikariCP
 * </p>
 * 
 * @CreateTime 2016年10月26日 下午6:01:24
 * @author SHOUSHEN LUAN
 */
public class HikariDataSourceFactory implements DataSourceFactory<HikariDataSource> {

	@Override
	public HikariDataSource getDataSource(DataSourceConfig config) throws SQLException {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getUrl());
		hikariConfig.setUsername(config.getUsername());
		hikariConfig.setPassword(config.getPassword());
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.setIdleTimeout(30000);
		hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
		hikariConfig.setMinimumIdle(config.getMinPoolSize());
		hikariConfig.setConnectionTimeout(3000);
		hikariConfig.setMaxLifetime(150000);
		if (config.getDriver() != null) {
			hikariConfig.setDriverClassName(config.getDriver());
		}
		TShardingLog.getLogger().warn(
				"dataSource.init|url:{},maxPoolSize:{},minPoolSize:{},"
						+ "connectionTimeout:3000ms,idleTimeout:30000ms,maxLifetime:150000",
				config.getUrl(), config.getMaxPoolSize(), config.getMinPoolSize());
		HikariDataSource ds = new HikariDataSource(hikariConfig);
		return ds;
	}

}
