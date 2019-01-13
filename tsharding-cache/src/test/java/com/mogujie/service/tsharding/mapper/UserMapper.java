package com.mogujie.service.tsharding.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mogujie.route.rule.CRC32RouteRule;
import com.mogujie.service.tsharding.bean.User;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.tsharding.annotation.ShardingExtensionMethod;
import com.mogujie.trade.tsharding.annotation.parameter.ShardingParam;

@DataSourceRouting(dataSource = "user", isReadWriteSplitting = false, table = "user_info", routeRule = CRC32RouteRule.class, tables = 3, databases = 2)
public interface UserMapper {
	@ShardingExtensionMethod
	public int insertData(@Param("user") @ShardingParam("id") User user);

	@ShardingExtensionMethod
	public User getUser(@Param("id") @ShardingParam long id);

	@ShardingExtensionMethod
	public int delete(@Param("id") @ShardingParam long id);

	/**
	 * 批量插入
	 * 
	 * @param list
	 * @return
	 */
	@ShardingExtensionMethod
	public int insertBatch(@Param("list") @ShardingParam("id") List<User> list);
}
