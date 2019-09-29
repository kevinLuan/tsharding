package com.mogujie.trade.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mogujie.trade.hander.DefaultShardingHandler;
import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.SimpleRouteRule;
import com.mogujie.trade.hander.DefaultSQLEnhancerHandler;
import com.mogujie.trade.hander.ShardingHandler;
import com.mogujie.trade.hander.SQLEnhancerHandler;

/**
 * <strong>DataSource路由</strong>
 * <ul>
 * <li>支持分库分表</li>
 * <li>支持分库不分表</li>
 * <li>支持分表部分库</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRouting {
	/**
	 * 静态绑定该Mapper对应的数据源
	 *
	 * @return 绑定的数据源的名称
	 */
	String dataSource();

	/**
	 * sharding表 mybatis中SQL语句中的表名称，最终会将该表名称替换成Sharding后真实的表名称
	 * <p>
	 * 如果没有分表的话，这个表名称可以不用设置
	 * </p>
	 */
	String table();

	/**
	 * 单库中分表的数量
	 */
	int tables() default 1;// 512

	/**
	 * 水平分库数量
	 */
	int databases() default 1;// 7

	/**
	 * 是否读写分离
	 */
	boolean isReadWriteSplitting();

	/**
	 * Sharding 处理
	 */
	Class<? extends ShardingHandler> shardingHandler() default DefaultShardingHandler.class;

	/**
	 * SQL增强处理器
	 */
	Class<? extends SQLEnhancerHandler> sqlEnhancerHandler() default DefaultSQLEnhancerHandler.class;

	/**
	 * 路由规则
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends RouteRule> routeRule() default SimpleRouteRule.class;
}
