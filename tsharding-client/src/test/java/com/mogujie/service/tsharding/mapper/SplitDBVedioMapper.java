package com.mogujie.service.tsharding.mapper;

import org.apache.ibatis.annotations.Param;

import com.mogujie.service.tsharding.bean.Vedio;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.annotation.ShardingExtensionMethod;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;

/**
 * 演示分库不分表
 * 
 * @author SHOUSHEN LUAN
 *
 */
@DataSourceRouting(dataSource = "trade", table = "vedio", databases = 2, isReadWriteSplitting = false)
public interface SplitDBVedioMapper {
	@ShardingExtensionMethod
	public int insertData(@Param("sku") @ShardingParam("product_id") Vedio vedio);

	@ShardingExtensionMethod
	public Vedio get(@Param("product_id") @ShardingParam int product_id);

	@ShardingExtensionMethod
	public int delete(@Param("product_id") @ShardingParam int product_id);
}
