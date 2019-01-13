package com.mogujie.trade.hander;


/**
 * Sharding 处理器
 * 
 * @CreateTime 2016年8月6日 上午9:12:33
 * @author SHOUSHEN LUAN
 */
public interface ShardingHander {
	/**
	 * 获取目标datasource
	 */
	public String schemaName();

	/**
	 * 获取目标表名称后缀
	 */
	public String getTableNameSuffix();

	/**
	 * 获取分表分库value
	 */
	public Object getShardingValue() throws IllegalArgumentException, IllegalAccessException;

}
