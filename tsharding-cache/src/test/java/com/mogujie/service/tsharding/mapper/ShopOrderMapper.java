package com.mogujie.service.tsharding.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mogujie.service.tsharding.bean.ShopOrder;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.annotation.ShardingExtensionMethod;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;

@DataSourceRouting(dataSource = "trade", table = "tradeorder", tables = 3, databases = 2, isReadWriteSplitting = true)
public interface ShopOrderMapper {
	@ShardingExtensionMethod()
	public ShopOrder getShopOrderByShopOrderId(@ShardingParam Long shopOrderId);

	@ShardingExtensionMethod()
	public List<ShopOrder> getShopOrderByShopOrderIds(@ShardingParam List<Long> shopOrderIds);

	@ShardingExtensionMethod()
	int batchUpdateShopOrderByShopOrderIds(@ShardingParam @Param("shopOrderIds") List<Long> shopOrderIds,
			@Param("shopOrder") ShopOrder shopOrder);

	@ShardingExtensionMethod()
	public ShopOrder getShopOrder(@ShardingParam @Param("id") Long id);

	@ShardingExtensionMethod
	public int insertOrder(@ShardingParam("orderId") @Param("order") ShopOrder order);

	@ShardingExtensionMethod
	public int deleteByOrderId(@ShardingParam @Param("orderId") Long orderId);

}
