package com.mogujie.service.tsharding.mapper;

import org.apache.ibatis.annotations.Param;

import com.mogujie.route.rule.BaseRouteRule;
import com.mogujie.service.tsharding.bean.Sku;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.annotation.ShardingExtensionMethod;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;

/**
 * 测试分表不分库
 * 
 * @author SHOUSHEN LUAN
 *
 */
@DataSourceRouting(dataSource = "product", table = "sku", routeRule = BaseRouteRule.class, tables = 3, isReadWriteSplitting = false)
public interface SplitTableSkuMapper {
	@ShardingExtensionMethod
	public int insertData(@Param("sku") @ShardingParam("product_id") Sku sku);

	@ShardingExtensionMethod
	public Sku get(@Param("product_id") @ShardingParam int product_id);

	@ShardingExtensionMethod
	public int delete(@Param("product_id") @ShardingParam int product_id);
}
