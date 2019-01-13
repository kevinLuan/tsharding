package com.mogujie.trade.utils;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.route.rule.CRC32RouteRule;
import com.mogujie.route.rule.RouteRule;
import com.mogujie.route.rule.ShardingUtils;
import com.mogujie.route.rule.SimpleRouteRule;

public class ShardingUtilsTest {
	@Test
	public void test_convertLong() {
		Long num = ShardingUtils.convert("1", Long.class);
		Assert.assertEquals(1L, num.longValue());

		long num2 = ShardingUtils.convert("2", long.class);
		Assert.assertEquals(2L, num2);
	}

	@Test
	public void test_convertInt() {
		Integer num = ShardingUtils.convert("1", Integer.class);
		Assert.assertEquals(1, num.intValue());

		int num2 = ShardingUtils.convert("2", int.class);
		Assert.assertEquals(2, num2);
	}

	@Test(expected = NumberFormatException.class)
	public void test_convertLongError() {
		ShardingUtils.convert("1xxx", Long.class);
	}

	@Test
	public void test_convertString() {
		String str = ShardingUtils.convert("xxx", String.class);
		Assert.assertEquals("xxx", str);
	}

	@Test
	public void testGenericityType() {
		RouteRule<?> rule = new SimpleRouteRule();
		Assert.assertEquals(Long.class, ShardingUtils.getGenericityType(rule));
	}

	@Test
	public void testGenericityTypeString() {
		RouteRule<?> rule = new CRC32RouteRule();
		Assert.assertEquals(String.class, ShardingUtils.getGenericityType(rule));
	}

}
