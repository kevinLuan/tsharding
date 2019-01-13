package com.mogujie.distributed.transction;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.trade.db.DataSourceRouting;

/**
 * 编程式事物管理器工厂
 * 
 * @author SHOUSHEN LUAN
 *
 */
public class DynamicTransctionManagerFactory implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 创建事物管理器
	 * 
	 * @return
	 */
	public DynamicTransctionManager create() {
		return new DynamicTransctionManager(applicationContext);
	}

	/**
	 * 创建事物管理器
	 * 
	 * @param mapper
	 * @param shardings
	 *        0~N 个路由参数
	 * @return
	 */
	public DynamicTransctionManager create(Class<?> mapper, Object... shardings) {
		DynamicTransctionManager transctionManager = create();
		if (shardings == null || shardings.length == 0) {
			transctionManager.addTransManager(mapper);
		} else {
			for (Object sharding : shardings) {
				transctionManager.addTransManager(mapper, sharding);
			}
		}
		return transctionManager;
	}

	/**
	 * 获取事物管理器
	 */
	public Set<String> getTransManagerBeanName(Class<?> mapper, Object... shardingParams) {
		Set<String> set = new HashSet<>();
		DataSourceRouting routing = mapper.getAnnotation(DataSourceRouting.class);
		String dataSource = routing.dataSource();
		if (routing.tables() > 1 || routing.databases() > 1) {
			RouteRule<Object> routeRule = RouteRuleFactory.getRouteRule(mapper);
			for (Object param : shardingParams) {
				set.add(routeRule.calculateSchemaName(routing, param) + "TransactionManager");
			}
		} else {
			set.add(dataSource + "TransactionManager");
		}
		return set;
	}
}
