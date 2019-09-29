package com.mogujie.route.rule;

import java.lang.annotation.Annotation;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.route.rule.CRC32RouteRule;
import com.mogujie.route.rule.RouteRule;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.hander.SQLEnhancerHandler;
import com.mogujie.trade.hander.ShardingHandler;

public class CRC32RouteRuleTest {
	public CRC32RouteRule crc32RouteRule = new CRC32RouteRule();

	@Test
	public void test_fill() {
		Assert.assertEquals(String.valueOf(100), crc32RouteRule.fillBit(100));
	}

	@Test
	public void test_ruleDataSource() {
		Assert.assertEquals("testDB0", crc32RouteRule.calculateSchemaName(new MyDataSourceRouting(), "7"));
		Assert.assertEquals("testDB1", crc32RouteRule.calculateSchemaName(new MyDataSourceRouting(), "30"));
	}

	@Test
	public void test_ruleTable() {
		Assert.assertEquals("test0", crc32RouteRule.calculateTableName(new MyDataSourceRouting(), "7"));
		Assert.assertEquals("test1", crc32RouteRule.calculateTableName(new MyDataSourceRouting(), "30"));
	}

	@Test
	public void test_ruleTableSuffix() {
		Assert.assertEquals("0", crc32RouteRule.calculateTableNameSuffix(new MyDataSourceRouting(), "20"));
		Assert.assertEquals("1", crc32RouteRule.calculateTableNameSuffix(new MyDataSourceRouting(), "23"));
	}

	public static class MyDataSourceRouting implements DataSourceRouting {
		@Override
		public Class<? extends Annotation> annotationType() {
			return null;
		}

		@Override
		public int tables() {
			return 3;
		}

		@Override
		public String table() {
			return "test";
		}

		@Override
		public Class<? extends SQLEnhancerHandler> sqlEnhancerHandler() {
			return null;
		}

		@Override
		public Class<? extends ShardingHandler> shardingHandler() {
			return null;
		}

		@Override
		public Class<? extends RouteRule> routeRule() {
			return null;
		}

		@Override
		public boolean isReadWriteSplitting() {
			return false;
		}

		@Override
		public int databases() {
			return 2;
		}

		@Override
		public String dataSource() {
			return "testDB";
		}
	}
}
