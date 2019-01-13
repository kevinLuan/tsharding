package com.mogujie.route.rule;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.route.rule.ShardingUtils;

public class ShardingUtilsTest {
	@Test
	public void test() {
		Assert.assertEquals(ShardingUtils.fillBit(123, 5), "00123");
	}

	@Test
	public void test_1() {
		Assert.assertEquals(ShardingUtils.fillBit(0, 5), "00000");

	}

	@Test(expected = IllegalArgumentException.class)
	public void test_error() {
		try {
			Assert.assertEquals(ShardingUtils.fillBit(-1, 5), "00000");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(e.getMessage(), "shardingTableSuffix:`-1` 必须大于等于零");
			throw e;
		}
		Assert.assertTrue(false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_error_1() {
		try {
			ShardingUtils.fillBit(0, 0);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(e.getMessage(), "bit:`0` 必须大于零");
			throw e;
		}
		Assert.assertTrue(false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_error_2() {
		try {
			ShardingUtils.fillBit(12345, 3);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(e.getMessage(), "shardingTableSuffix:`12345`位数超过生成总长度");
			throw e;
		}
		Assert.assertTrue(false);
	}
}
