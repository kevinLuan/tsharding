package com.mogujie.route.rule;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.trade.db.DataSourceRouting;

public class SimpleRouteRuleTest {
	@Test
	public void test_calculateSchemaName() {
		DataSourceRouting routing = SplitDB.class.getAnnotation(DataSourceRouting.class);
		for (long i = 0; i < 5; i++) {
			RouteRule routeRule = new SimpleRouteRule();
			String expecteds = routeRule.calculateSchemaName(routing, i);
			routeRule = new MyRule();
			String actuals = routeRule.calculateSchemaName(routing, i);
			Assert.assertEquals(expecteds, actuals);
		}
		/////////////
		routing = SplitDBTable.class.getAnnotation(DataSourceRouting.class);
		for (long i = 0; i < 5; i++) {
			RouteRule routeRule = new SimpleRouteRule();
			String expecteds = routeRule.calculateSchemaName(routing, i);
			routeRule = new MyRule();
			String actuals = routeRule.calculateSchemaName(routing, i);
			Assert.assertEquals(expecteds, actuals);
		}
	}

	@Test
	public void test_calculateTableName() {
		DataSourceRouting routing = SplitTable.class.getAnnotation(DataSourceRouting.class);
		for (long i = 0; i < 5; i++) {
			RouteRule routeRule = new SimpleRouteRule();
			String expecteds = routeRule.calculateTableName(routing, i);
			routeRule = new MyRule();
			String actuals = routeRule.calculateTableName(routing, i);
			System.out.println(expecteds + "-->>" + actuals);
		}
		////////////////
		routing = SplitDBTable.class.getAnnotation(DataSourceRouting.class);
		
		for (long i = 0; i < 5; i++) {
			RouteRule routeRule = new SimpleRouteRule();
			String expecteds = routeRule.calculateTableName(routing, i);
			routeRule = new MyRule();
			String actuals = routeRule.calculateTableName(routing, i);
			System.out.println(expecteds + "-->>" + actuals);
		}
	}

	@DataSourceRouting(dataSource = "account", table = "user", isReadWriteSplitting = true)
	public static class Simple {
	}

	@DataSourceRouting(dataSource = "account", databases = 5, table = "user", isReadWriteSplitting = true)
	public static class SplitDB {
	}

	@DataSourceRouting(dataSource = "account", tables = 5, table = "user", isReadWriteSplitting = true)
	public static class SplitTable {
	}

	@DataSourceRouting(dataSource = "account", databases = 2, tables = 5, table = "user", isReadWriteSplitting = true)
	public static class SplitDBTable {
	}
}
