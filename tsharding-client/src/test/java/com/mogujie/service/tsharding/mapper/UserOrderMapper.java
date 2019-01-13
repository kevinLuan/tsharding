package com.mogujie.service.tsharding.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mogujie.route.rule.CRC32RouteRule;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.annotation.ShardingExtensionMethod;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;
import com.mogujie.trade.tsharding.route.orm.MapperResourceEnhancerNew;

@DataSourceRouting(dataSource = "user", isReadWriteSplitting = false, table = "user_order", routeRule = CRC32RouteRule.class, tables = 3, databases = 2)
public interface UserOrderMapper {
	/**
	 * 测试分库分表中进行链表查询
	 * 
	 * @param test
	 * @param id
	 * @param ids
	 * @return
	 */
	@ShardingExtensionMethod(type = MapperResourceEnhancerNew.class)
	public int join_test(@Param("test") @ShardingParam long test// 路由参数
			, @Param("id") long id, // 用户ID
			@Param("ids") List<?> ids);
}
