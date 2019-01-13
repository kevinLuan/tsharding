package com.mogujie.trade.tsharding.route.orm.base;

import com.mogujie.trade.db.DataSourceRouting;

/**
 * Mapper管控基础 类-数据源
 *
 * @author qigong
 *
 */
public class MapperBasicConfig {

	private final Class<?> mapperClass;
	private final Class<?> shardingMapper;
	private final String dataSourceName;

	public MapperBasicConfig(Class<?> mapperClass, Class<?> shardingMapper, String dataSourceName) {
		this.mapperClass = mapperClass;
		this.dataSourceName = dataSourceName;
		this.shardingMapper = shardingMapper;
	}

	public MapperBasicConfig(Class<?> mapperClass, String dataSourceName) {
		this.mapperClass = mapperClass;
		this.dataSourceName = dataSourceName;
		this.shardingMapper = mapperClass;
	}

	public Class<?> getShardingMapper() {
		return shardingMapper;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public boolean isReadWriteSplitting() {
		DataSourceRouting sharding = mapperClass.getAnnotation(DataSourceRouting.class);
		return sharding.isReadWriteSplitting();
	}

}
