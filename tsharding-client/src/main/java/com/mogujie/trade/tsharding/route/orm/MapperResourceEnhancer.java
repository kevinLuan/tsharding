package com.mogujie.trade.tsharding.route.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.hander.SQLEnhancerHander;
import com.mogujie.trade.utils.TShardingLog;

/**
 * Mappper sql增强
 *
 * @author qigong on 5/1/15
 */
@SuppressWarnings("unchecked")
public class MapperResourceEnhancer extends MapperEnhancer {

	public MapperResourceEnhancer(Class<?> mapperClass) {
		super(mapperClass);
	}

	private SQLEnhancerHander getShardingTableHander(MappedStatement ms) {
		Class<?> clazzClass = getMapperClass(ms.getId());
		DataSourceRouting sharding = clazzClass.getAnnotation(DataSourceRouting.class);
		Class<?> enhancerHanderClass = sharding.sqlEnhancerHander();
		try {
			SQLEnhancerHander hander = (SQLEnhancerHander) enhancerHanderClass.getConstructor(Class.class)
					.newInstance(getMapperClass(ms.getId()));
			return hander;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public SqlSource enhancedShardingSQL(MappedStatement ms, Configuration configuration, Long shardingPara) {
		SQLEnhancerHander hander = getShardingTableHander(ms);
		TShardingLog.getLogger().debug(ms.getId());
		try {
			if (ms.getSqlSource() instanceof DynamicSqlSource) {
				DynamicSqlSource sqlSource = (DynamicSqlSource) ms.getSqlSource();
				Class<?> sqlSourceClass = sqlSource.getClass();
				Field sqlNodeField = sqlSourceClass.getDeclaredField("rootSqlNode");
				sqlNodeField.setAccessible(true);
				MixedSqlNode rootSqlNode = (MixedSqlNode) sqlNodeField.get(sqlSource);
				Class<?> mixedSqlNodeClass = rootSqlNode.getClass();
				Field contentsField = mixedSqlNodeClass.getDeclaredField("contents");
				contentsField.setAccessible(true);
				List<SqlNode> textSqlNodes = (List<SqlNode>) contentsField.get(rootSqlNode);
				List<SqlNode> newSqlNodesList = new ArrayList<SqlNode>();
				Class<?> textSqlNodeClass = textSqlNodes.get(0).getClass();
				Field textField = textSqlNodeClass.getDeclaredField("text");
				textField.setAccessible(true);
				for (SqlNode node : textSqlNodes) {
					if (node instanceof StaticTextSqlNode) {
						StaticTextSqlNode textSqlNode = (StaticTextSqlNode) node;
						String text = (String) textField.get(textSqlNode);
						if (hander.hasReplace(text)) {
							String sql = hander.format(text, shardingPara);
							newSqlNodesList.add(new StaticTextSqlNode(sql));
							continue;
						}
					}
					newSqlNodesList.add(node);
				}

				MixedSqlNode newrootSqlNode = new MixedSqlNode(newSqlNodesList);
				return new DynamicSqlSource(configuration, newrootSqlNode);
			} else if (ms.getSqlSource() instanceof RawSqlSource) {
				RawSqlSource sqlSource = (RawSqlSource) ms.getSqlSource();
				Class<?> sqlSourceClass = sqlSource.getClass();
				Field sqlSourceField = sqlSourceClass.getDeclaredField("sqlSource");
				sqlSourceField.setAccessible(true);
				StaticSqlSource staticSqlSource = (StaticSqlSource) sqlSourceField.get(sqlSource);
				Field sqlField = staticSqlSource.getClass().getDeclaredField("sql");
				Field parameterMappingsField = staticSqlSource.getClass().getDeclaredField("parameterMappings");
				sqlField.setAccessible(true);
				parameterMappingsField.setAccessible(true);
				String sql = (String) sqlField.get(staticSqlSource);
				if (hander.hasReplace(sql)) {
					sql = hander.format(sql, shardingPara);
					SqlSource result = new RawSqlSource(configuration, sql, null);
					// 为sqlSource对象设置mappering参数
					StaticSqlSource newStaticSqlSource = (StaticSqlSource) sqlSourceField.get(result);
					List<ParameterMapping> parameterMappings = (List<ParameterMapping>) parameterMappingsField
							.get(staticSqlSource);
					parameterMappingsField.set(newStaticSqlSource, parameterMappings);
					return result;
				} else {
					return sqlSource;
				}
			} else {
				throw new RuntimeException("wrong sqlSource type!" + ms.getResource());
			}
		} catch (Exception e) {
			TShardingLog.error("reflect error!, ms resources:" + ms.getResource(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}