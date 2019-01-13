package com.mogujie.trade.tsharding.route.orm.base;

/**
 * @CreateTime 2016年8月12日 上午10:14:01
 * @author SHOUSHEN LUAN
 */
public class ShardingMeta {
	private Object shardingParam;

	private String schemaName;

	private String tableSuffix;

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableSuffix() {
		return tableSuffix;
	}

	public void setTableSuffix(String tableSuffix) {
		this.tableSuffix = tableSuffix;
	}

	public Object getShardingParam() {
		return shardingParam;
	}

	public void setShardingParam(Object shardingParam) {
		this.shardingParam = shardingParam;
	}
}