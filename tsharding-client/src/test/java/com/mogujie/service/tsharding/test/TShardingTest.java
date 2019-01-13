package com.mogujie.service.tsharding.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mogujie.service.tsharding.bean.ShopOrder;
import com.mogujie.service.tsharding.dao.ShopOrderDao;
import com.mogujie.service.tsharding.mapper.ShopOrderMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
public class TShardingTest {

	@Autowired
	private ShopOrderDao shopOrderDao;

	@Autowired
	private ShopOrderMapper shopOrderMapper;

	@Test
	public void test_insert() {
		for (int i = 0; i < 100; i++) {
			ShopOrder order = new ShopOrder();
			order.setOrderId(Long.valueOf(i));
			order.setBuyerUserId(1000L);
			order.setSellerUserId(8888L);
			order.setShipTime(6666L);
			int insertRes = shopOrderMapper.insertOrder(order);
			Assert.assertTrue(insertRes > 0);
			int res = shopOrderMapper.deleteByOrderId(Long.valueOf(i));
			Assert.assertTrue(res > 0);
			res = shopOrderMapper.deleteByOrderId(Long.valueOf(0));
			Assert.assertTrue(res == 0);
		}
	}

	@Test
	public void testGetShopOrderByShopOrderIdsDao() {
		List<Long> orderIds = new ArrayList<>();
		orderIds.add(6L);
		orderIds.add(7L);
		orderIds.add(8L);
		orderIds.add(2L);
		{
			ShopOrder order6 = new ShopOrder();
			order6.setOrderId(6L);
			order6.setBuyerUserId(1006L);
			order6.setSellerUserId(10086L);
			order6.setShipTime(System.currentTimeMillis());
			boolean res = shopOrderDao.insert(order6);
			Assert.assertTrue(res);
		}
		{
			ShopOrder order7 = new ShopOrder();
			order7.setOrderId(7L);
			order7.setBuyerUserId(1007L);
			order7.setSellerUserId(10087L);
			order7.setShipTime(System.currentTimeMillis());
			boolean res = shopOrderDao.insert(order7);
			Assert.assertTrue(res);
		}
		{
			ShopOrder order8 = new ShopOrder();
			order8.setOrderId(8L);
			order8.setBuyerUserId(1008L);
			order8.setSellerUserId(10088L);
			order8.setShipTime(System.currentTimeMillis());
			boolean res = shopOrderDao.insert(order8);
			Assert.assertTrue(res);
		}
		{
			ShopOrder order2 = new ShopOrder();
			order2.setOrderId(2L);
			order2.setBuyerUserId(1002L);
			order2.setSellerUserId(10082L);
			order2.setShipTime(System.currentTimeMillis());
			boolean res = shopOrderDao.insert(order2);
			Assert.assertTrue(res);
		}
		try {
			long start = System.currentTimeMillis();
			// TODO 测试路由参数为List<Long>时,需要多次查询并合并结果及在返回
			List<ShopOrder> orders = shopOrderDao.getShopOrderByShopOrderIds(orderIds);
			System.out.println("批量查询 use time:" + (System.currentTimeMillis() - start));
			for (ShopOrder order : orders) {
				System.out.println(order.toString());
			}
//			Assert.assertEquals(4, orders.size());
		} finally {
			int res = shopOrderMapper.deleteByOrderId(7L);
			Assert.assertTrue(res > 0);
			res = shopOrderMapper.deleteByOrderId(8L);
			Assert.assertTrue(res > 0);
			res = shopOrderMapper.deleteByOrderId(6L);
			Assert.assertTrue(res > 0);
			res = shopOrderMapper.deleteByOrderId(2L);
			Assert.assertTrue(res > 0);
		}
	}

	@Test
	public void testGetShopOrderByShopOrderIds() {
		int param[] = new int[] { 12, 18 };
		List<Long> orderIds = new ArrayList<>();
		for (int i = 0; i < param.length; i++) {
			ShopOrder order = new ShopOrder();
			order.setOrderId(Long.valueOf(param[i]));
			order.setBuyerUserId(1000L);
			order.setSellerUserId(8888L);
			order.setShipTime(6666L);
			int insertRes = shopOrderMapper.insertOrder(order);
			Assert.assertTrue(insertRes > 0);
			orderIds.add(Long.valueOf(param[i]));
		}
		// 由于这里使用了读写分离
		List<ShopOrder> orders = shopOrderMapper.getShopOrderByShopOrderIds(orderIds);
		System.out.println(Arrays.toString(orders.toArray()));
		System.out.println();
		Assert.assertTrue(orders.get(0).getBuyerUserId() == 1000L);
	}

	@Test
	public void testUpdateShopOrder() {
		ShopOrder order = new ShopOrder();
		order.setOrderId(1L);
		order.setBuyerUserId(1000L);
		order.setSellerUserId(10000L);
		order.setShipTime(System.currentTimeMillis());
		int res = shopOrderMapper.insertOrder(order);
		Assert.assertEquals(1, res);
		List<Long> orderIds = new ArrayList<>();
		orderIds.add(1L);
		orderIds.add(2L);
		orderIds.add(3L);
		orderIds.add(4L);
		orderIds.add(5L);
		orderIds.add(6L);
		orderIds.add(7L);
		ShopOrder shopOrder = new ShopOrder();
		shopOrder.setShipTime(System.currentTimeMillis());
		int rows = shopOrderMapper.batchUpdateShopOrderByShopOrderIds(orderIds, shopOrder);
		System.out.println(rows);
		Assert.assertTrue(rows > 0);
	}

	@Test
	public void test_get_slave() {
		long start = System.currentTimeMillis();
		for (int i = 10; i < 1000; i++) {
			ShopOrder order = new ShopOrder();
			order.setOrderId(100L + i);
			order.setBuyerUserId(1000L);
			order.setSellerUserId(10000L + i);
			order.setShipTime(System.currentTimeMillis());
			int res = shopOrderMapper.insertOrder(order);
			Assert.assertEquals(1, res);
			ShopOrder shopOrder = shopOrderMapper.getShopOrder(Long.valueOf(100L + i));
			Assert.assertEquals(100 + i, shopOrder.getOrderId().intValue());
		}
		// 13036 13160 13163
		// 12994 13116 13169
		System.out.println("test_get_slave useTime:" + (System.currentTimeMillis() - start));
	}

	@Test
	public void test_get_slave_simple() {
		ShopOrder order = shopOrderMapper.getShopOrder(Long.valueOf(1));
		Assert.assertTrue(order != null);
	}

	@Test
	public void test_delete() {
		for (int i = 0; i < 1000; i++) {
			ShopOrder order = new ShopOrder();
			order.setOrderId(Long.valueOf(i));
			order.setBuyerUserId(1000L);
			order.setSellerUserId(8888L);
			order.setShipTime(6666L);
			int insertRes = shopOrderMapper.insertOrder(order);
			Assert.assertEquals(1, insertRes);
		}
		for (int i = 0; i < 1000; i++) {
			long start = System.currentTimeMillis();
			int result = shopOrderMapper.deleteByOrderId(Long.valueOf(i));
			Assert.assertTrue(result > 0);
			System.out.println("删除orderId:" + i + "->使用时间：" + (System.currentTimeMillis() - start));
		}

	}

	@Test
	public void test_trasction() {
		ShopOrder order = new ShopOrder();
		order.setBuyerUserId(86L);
		order.setOrderId(87L);
		order.setSellerUserId(88L);
		order.setShipTime(System.currentTimeMillis());
		boolean res = shopOrderDao.insert(order);
		Assert.assertTrue(res);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_insert_rollback() {
		ShopOrder order = new ShopOrder();
		order.setBuyerUserId(87L);
		order.setOrderId(88L);
		order.setSellerUserId(89L);
		order.setShipTime(System.currentTimeMillis());
		shopOrderDao.insert_rollback(order);
	}

	@Test(expected = RuntimeException.class)
	public void test_insert_err_no_rollback() {
		ShopOrder order = new ShopOrder();
		order.setBuyerUserId(88L);
		order.setOrderId(89L);
		order.setSellerUserId(90L);
		order.setShipTime(System.currentTimeMillis());
		shopOrderDao.insert_err_no_rollback(order);
	}

	// 串行测试结果
	// druid
	// total time:19636
	// total time:21407
	// total time:20868
	// Hikari
	// total time:16461
	// total time:19114
	// total time:17783
	@Test
	public void load_test() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			ShopOrder order = new ShopOrder();
			order.setOrderId(Long.valueOf(i));
			order.setBuyerUserId(1000L);
			order.setSellerUserId(8888L);
			order.setShipTime(6666L);
			int insertRes = shopOrderMapper.insertOrder(order);
			Assert.assertTrue(insertRes > 0);
			int res = shopOrderMapper.deleteByOrderId(Long.valueOf(i));
			Assert.assertTrue(res > 0);
			res = shopOrderMapper.deleteByOrderId(Long.valueOf(0));
			Assert.assertTrue(res == 0);
		}
		System.out.println("total time:" + (System.currentTimeMillis() - startTime));
	}
}
