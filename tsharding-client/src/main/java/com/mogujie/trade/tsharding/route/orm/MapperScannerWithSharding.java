package com.mogujie.trade.tsharding.route.orm;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;

import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.sharding.merge.MergeApi;
import com.mogujie.sharding.merge.MergeFactory;
import com.mogujie.trade.db.DataSourceLookup;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.db.ReadWriteSplittingDataSource;
import com.mogujie.trade.hander.MapperFactory;
import com.mogujie.trade.hander.MapperFactory.ShardingHanderEntry;
import com.mogujie.trade.tsharding.route.orm.base.ClassPathScanHandler;
import com.mogujie.trade.tsharding.route.orm.base.DefaultInvocation;
import com.mogujie.trade.tsharding.route.orm.base.Invoker;
import com.mogujie.trade.tsharding.route.orm.base.SqlSessionFactoryLookup;
import com.mogujie.trade.tsharding.route.orm.base.TShardingRoutingInvokeFactory;
import com.mogujie.trade.utils.GroupDBTable;
import com.mogujie.trade.utils.TShardingLog;

/**
 * Tsharding MybatisMapper的扫描类，负责将Mapper接口与对应的xml配置文件整合，绑定设定的数据源，注入到Spring
 * Context中。
 * 
 * @author qigong
 */
public class MapperScannerWithSharding implements BeanFactoryPostProcessor, InitializingBean {
	private static DataSourceLookup dataSourceLookup;

	private String packageName;

	private Resource[] mapperLocations;
	/**
	 * Mybatis配置文件
	 */
	private Resource configLocation;
	private String[] mapperPacakages;
	private SqlSessionFactoryLookup sqlSessionFactoryLookup;

	public static DataSourceLookup getDataSourceLookup() {
		return dataSourceLookup;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initMapperPackage();
	}

	private void initMapperPackage() throws IOException {
		this.mapperPacakages = packageName.split(",");
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		MapperScannerWithSharding.dataSourceLookup = beanFactory.getBean(DataSourceLookup.class);
		try {
			this.initSqlSessionFactories(beanFactory);
		} catch (Exception e) {
			throw new RuntimeException("initSqlSessionFactories ERROR:", e);
		}
		ClassPathScanHandler scanner = new ClassPathScanHandler();
		Set<Class<?>> mapperClasses = new HashSet<>();
		for (String mapperPackage : this.mapperPacakages) {
			Set<Class<?>> classes = scanner.getPackageAllClasses(mapperPackage.trim(), false);
			mapperClasses.addAll(classes);
		}
		for (Class<?> clazz : mapperClasses) {
			if (isMapper(clazz)) {
				Object mapper = this.newMapper(clazz);
				MapperFactory.registerMapper(clazz);
				RouteRuleFactory.register(clazz);
				beanFactory.registerSingleton(
						Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1), mapper);
			}
		}

	}

	private void initSqlSessionFactories(ConfigurableListableBeanFactory beanFactory) throws Exception {
		Map<String, SqlSessionFactory> sqlSessionFactories = new HashMap<>(this.dataSourceLookup.getMapping().size());

		ReadWriteSplittingDataSource defaultDataSource = null;
		SqlSessionFactory defaultSqlSessionFactory = null;
		for (ReadWriteSplittingDataSource dataSource : this.dataSourceLookup.getMapping().values()) {

			SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
			sessionFactoryBean.setMapperLocations(mapperLocations);
			sessionFactoryBean.setDataSource(dataSource);
			sessionFactoryBean.setTypeAliasesPackage(this.packageName + ".domain.entity");
			sessionFactoryBean.setConfigLocation(configLocation);
			// init 初始化所有sql对应的元数据、资源（sqlNode, sqlSource, mappedStatement）等
			sessionFactoryBean.afterPropertiesSet();

			if (defaultDataSource == null) {
				// 第一个
				defaultDataSource = dataSource;
				defaultSqlSessionFactory = sessionFactoryBean.getObject();
			} else {
				SqlSessionFactory newSqlSessionFactory = sessionFactoryBean.getObject();
				Field conf = newSqlSessionFactory.getClass().getDeclaredField("configuration");
				conf.setAccessible(true);
				Configuration newConfiguration = (Configuration) conf.get(newSqlSessionFactory);
				Field mappedStatementField = newConfiguration.getClass().getDeclaredField("mappedStatements");

				// 去掉final修饰符
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(mappedStatementField, mappedStatementField.getModifiers() & ~Modifier.FINAL);
				mappedStatementField.setAccessible(true);

				// 后续的元数据复用
				Configuration defaultConfiguration = defaultSqlSessionFactory.getConfiguration();
				Map<String, MappedStatement> reUsedMappedStatement = (Map) mappedStatementField.get(defaultConfiguration);
				mappedStatementField.set(newConfiguration, reUsedMappedStatement);
			}
			beanFactory.registerSingleton(dataSource.getName() + "SqlSessionFactory", sessionFactoryBean);
			sqlSessionFactories.put(dataSource.getName(), sessionFactoryBean.getObject());
			defaultSqlSessionFactory = sessionFactoryBean.getObject();
		}

		this.sqlSessionFactoryLookup = new SqlSessionFactoryLookup(sqlSessionFactories);
	}

	private static boolean isMapper(Class<?> clazz) {
		if (clazz.isInterface()) {
			return true;
		}
		return false;
	}

	private Object newMapper(final Class<?> clazz) {

		final Invoker invoker = new TShardingRoutingInvokeFactory(sqlSessionFactoryLookup).newInvoker(clazz);
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
				ShardingHanderEntry entry = MapperFactory.getShardingHanderEntry(clazz, method);
				if (entry.isSharding()) {
					Object shardingVal = entry.getRouteParam(args);
					// 路由参数为List时，需要根据集合中的参数根据分库分表策略进行分组，然后在批量执行
					if (List.class.isAssignableFrom(shardingVal.getClass())) {
						List<?> rawShardingVal = (List<?>) shardingVal;
						TShardingLog.info("sharding raw param:{}", Arrays.toString(rawShardingVal.toArray()));
						GroupDBTable groupDBTable = new GroupDBTable(entry.getRouting(), rawShardingVal, entry);
						MergeApi merge = MergeFactory.newMerge(method);
						TShardingLog.info("sharding group process...");
						groupDBTable.reset();
						while (groupDBTable.hasNext()) {
							List<Object> shardingList = groupDBTable.next();
							TShardingLog.info("group database->table:{}", Arrays.toString(shardingList.toArray()));
							// 修改参数
							args[entry.getRouteParamIndex()] = shardingList;
							Object result = invoker.invoke(new DefaultInvocation(clazz, method, args));
							merge.merge(result);
						}
						return merge.getValue();
					}
				}
				return invoker.invoke(new DefaultInvocation(clazz, method, args));
			}
		});
	}

	/**
	 * 注入packageName配置
	 * 
	 * @param packageName
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * 注入mapperLocations配置
	 * 
	 * @param mapperLocations
	 */
	public void setMapperLocations(Resource[] mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	public Resource getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}
	/**
	 * 扫描Mapper类
	 * @param mapperPacakage
	 * @return
	 */
	public static Set<Class<?>> scanMapper(String... mapperPackage) {
		ClassPathScanHandler scanner = new ClassPathScanHandler();
		Set<Class<?>> mapperClasses = new HashSet<>();
		for (String pack : mapperPackage) {
			Set<Class<?>> classes = scanner.getPackageAllClasses(pack.trim(), false);
			mapperClasses.addAll(classes);
		}
		for (Class<?> clazz : mapperClasses) {
			if (isMapper(clazz)) {
				if(clazz.getAnnotation(DataSourceRouting.class)!=null){
					mapperClasses.add(clazz);	
				}
			}
		}
		return mapperClasses;
	}
}
