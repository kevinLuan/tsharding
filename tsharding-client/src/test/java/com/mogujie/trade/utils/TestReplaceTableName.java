package com.mogujie.trade.utils;

import org.junit.Assert;
import org.junit.Test;

public class TestReplaceTableName {
	static String sql = "Order ,ORDER ,order_detail ,user_order ORDER BY ID order";
	static char[] chats = sql.toCharArray();

	@Test
	public void test() {
		Assert.assertTrue(ReplaceTableName.getInstance().matches(sql, "Order"));
		Assert.assertTrue(ReplaceTableName.getInstance().matches(sql, "order_detail"));
		Assert.assertFalse(ReplaceTableName.getInstance().matches(sql, "order_det"));
	}

	@Test
	public void test_replace() {
		String expected = "order0000 ,order0000 ,order_detail ,user_order order0000 BY ID order0000";
		Assert.assertEquals(expected, ReplaceTableName.getInstance().replace(sql, "Order", "order0000"));
	}
}
