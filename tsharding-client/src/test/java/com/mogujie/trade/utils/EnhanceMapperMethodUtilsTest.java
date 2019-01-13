package com.mogujie.trade.utils;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.trade.utils.EnhanceMapperMethodUtils;
import com.mogujie.trade.utils.EnhanceMapperMethodUtils.Entity;
import com.mogujie.trade.utils.EnhanceMapperMethodUtils.MethodSegemation;

public class EnhanceMapperMethodUtilsTest {
	public static void main(String[] args) {
		Entity entity = EnhanceMapperMethodUtils.segmentation(10000);
		while (entity.hasSegemation()) {
			MethodSegemation segemation = entity.nextSegemation();
			System.out
			.println(segemation.getStart() + "-" + segemation.getEnd() + "--" + segemation.isCurrentSegemation(488));
			if (segemation.isCurrentSegemation(488)) {
				break;
			}
		}
		System.out.println("---");
		System.out.println("==>>"+EnhanceMapperMethodUtils.getSegemation(10000, 488));
	}

	@Test
	public void test_getSegemation() {
		Assert.assertEquals(0, EnhanceMapperMethodUtils.getSegemation(10000, 0));
		Assert.assertEquals(0, EnhanceMapperMethodUtils.getSegemation(10000, 511));
		Assert.assertEquals(1, EnhanceMapperMethodUtils.getSegemation(10000, 512));
		Assert.assertEquals(1, EnhanceMapperMethodUtils.getSegemation(10000, 1023));
		Assert.assertEquals(2, EnhanceMapperMethodUtils.getSegemation(10000, 1024));
		Assert.assertEquals(2, EnhanceMapperMethodUtils.getSegemation(10000, 1025));
	}

	@Test
	public void test_markMapperStatement() {
		String msId = "com.mogujie.service.tsharding.mapper.UserMapper.insertBatch";
		String res = EnhanceMapperMethodUtils.getMappedStatement(msId, "0000", 10000);
		Assert.assertEquals("com.mogujie.service.tsharding.mapper.UserMapper$Sharding$insertBatch$0.insertBatch0000", res);
		res = EnhanceMapperMethodUtils.getMappedStatement(msId, "0888", 2048);
		Assert.assertEquals("com.mogujie.service.tsharding.mapper.UserMapper$Sharding$insertBatch$1.insertBatch0888", res);

		res = EnhanceMapperMethodUtils.getMappedStatement(msId, "0511", 2048);
		Assert.assertEquals("com.mogujie.service.tsharding.mapper.UserMapper$Sharding$insertBatch$0.insertBatch0511", res);

		res = EnhanceMapperMethodUtils.getMappedStatement(msId, "0512", 2048);
		Assert.assertEquals("com.mogujie.service.tsharding.mapper.UserMapper$Sharding$insertBatch$1.insertBatch0512", res);

	}

	@Test
	public void test_isSegementation() {
		Assert.assertTrue(EnhanceMapperMethodUtils.isSegmentation(513));
		Assert.assertFalse(EnhanceMapperMethodUtils.isSegmentation(512));
		Assert.assertFalse(EnhanceMapperMethodUtils.isSegmentation(0));
		Assert.assertFalse(EnhanceMapperMethodUtils.isSegmentation(100));
	}

	@Test
	public void test() {
		Assert.assertEquals(0, EnhanceMapperMethodUtils.getSegemation(10000, 0));
		Assert.assertEquals(0, EnhanceMapperMethodUtils.getSegemation(10000, 511));
		Assert.assertEquals(1, EnhanceMapperMethodUtils.getSegemation(10000, 512));
		Assert.assertEquals(8, EnhanceMapperMethodUtils.getSegemation(10000, 4096));
		Assert.assertEquals(8, EnhanceMapperMethodUtils.getSegemation(10000, 4607));
		Assert.assertEquals(11, EnhanceMapperMethodUtils.getSegemation(10000, 6000));
	}
}
