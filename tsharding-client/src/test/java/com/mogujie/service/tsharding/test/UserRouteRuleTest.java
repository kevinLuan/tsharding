package com.mogujie.service.tsharding.test;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mogujie.service.tsharding.bean.User;
import com.mogujie.service.tsharding.dao.UserDao;
import com.mogujie.service.tsharding.mapper.UserMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRouteRuleTest {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserDao userDao;

	@Test
	public void test1_insert() {
		for (int i = 1; i < 100; i++) {
			long start = System.currentTimeMillis();
			User user = new User();
			user.setId(i);
			user.setName("zhang san");
			int res = userMapper.insertData(user);
			System.out.println("insert use time:" + (System.currentTimeMillis() - start));
			Assert.assertEquals(res, 1);
		}
	}

	/**
	 * 测试批量插入失败回滚
	 */
	@Test
	public void test2_err_rollback() {
		List<User> list = new ArrayList<>();
		for (int i = 200; i < 230; i++) {
			User user = new User();
			user.setId(i);
			user.setName("test data");
			list.add(user);
		}
		userMapper.delete(list.get(3).getId());
		// 先单独插入一次数据，后面再批量插入时出现主键冲突，批量回滚.
		userMapper.insertData(list.get(3));
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis();
			try {
				int res = userDao.insertBatch(list);
				System.out.println(res);
			} catch (Throwable th) {
// th.printStackTrace();
			} finally {
				System.out.println("use time:" + (System.currentTimeMillis() - start));
			}
		}

	}

	@Test
	public void test3_get() {
		for (int i = 1; i < 100; i++) {
			long start = System.currentTimeMillis();
			User user = userMapper.getUser(i);
			System.out.println("get id use time:" + (System.currentTimeMillis() - start));
			Assert.assertTrue(user != null);
		}
	}

	@Test
	public void test4_delete() {
		for (int i = 1; i < 100; i++) {
			long start = System.currentTimeMillis();
			int res = userMapper.delete(i);
			System.out.println("delete id use time:" + (System.currentTimeMillis() - start));
			Assert.assertTrue(res > 0);
		}
	}

	@Test
	public void test5_sharding_delete_old_data() {
		long start = System.currentTimeMillis();
		for (int i = 1000; i < 1100; i++) {
			userMapper.delete(i);
		}
		System.out.println("test_sharding_delete_old_data use time:" + (System.currentTimeMillis() - start));
	}

	@Test
	public void test6_sharding() {
		for (int i = 1000; i < 1100; i++) {
			User user = new User(i);
			user.setName("name-" + i + "->db-" + (crc32Calc(i) % 2) + "--table-" + (crc32Calc(i) % 3));
			int res = userMapper.insertData(user);
			Assert.assertTrue(res == 1);
		}
	}

	private long crc32Calc(long num) {
		CRC32 crc32 = new CRC32();
		crc32.update(String.valueOf(num).getBytes());
		return crc32.getValue();
	}

	@Test
	public void testDistributedTransction() {
		try {
			User user1 = new User();
			user1.setId(1100);
			user1.setName("test 1000 data");

			User user2 = new User();
			user2.setId(1101);
			user2.setName("test 1001 data");

			User user3 = new User();
			user3.setId(1102);
			user3.setName("test 1002 data");
			try {
				int res = userDao.test_distributed_transaction(user1, user2, user3);
				Assert.assertTrue(res == 3);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		} finally {
			Assert.assertEquals(0, userMapper.delete(1100));
			Assert.assertEquals(0, userMapper.delete(1101));
			Assert.assertEquals(0, userMapper.delete(1102));
		}
	}

	@Test
	public void testUserDefinedDistributedTransactionManager() {
		int counter = 0;
		do {
			long start = System.currentTimeMillis();
			start = System.currentTimeMillis();
			User user1 = new User();
			user1.setId(1100);
			user1.setName("test 1000 data");

			User user2 = new User();
			user2.setId(1101);
			user2.setName("test 1001 data");
			boolean res = userDao.testUserDefinedDistributedTransctionManager(user1, user2);
			System.out.println(
					"test user defined distributed transction manager  use time:" + (System.currentTimeMillis() - start));
			if (res) {// 数据插入成功并完成提交事物
				long delStart = System.currentTimeMillis();
				Assert.assertEquals(1, userMapper.delete(1100));
				Assert.assertEquals(1, userMapper.delete(1101));
				Assert.assertEquals(0, userMapper.delete(1102));
				System.out.println("delete use time:" + (System.currentTimeMillis() - delStart));
			} else {// 异常回滚了
				continue;
			}
			counter++;
		} while (counter < 500);
		Assert.assertEquals(500, counter);
	}
}
