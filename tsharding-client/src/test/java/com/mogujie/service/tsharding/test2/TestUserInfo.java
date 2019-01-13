package com.mogujie.service.tsharding.test2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.CannotCreateTransactionException;

import com.mogujie.distributed.transction.DynamicTransctionManagerFactory;
import com.mogujie.service.tsharding.bean.UserInfo;
import com.mogujie.service.tsharding.dao.UserInfoService;
import com.mogujie.service.tsharding.dao.UserInfoService2;
import com.mogujie.service.tsharding.mapper.UserInfoMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
public class TestUserInfo {
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	UserInfoService2 userInfoService2;
	@Autowired
	private UserInfoMapper userInfoMapper;
	@Autowired
	private DynamicTransctionManagerFactory dtmFactory;

	@Test
	public void test_a() {
		String name = "test-A";
		UserInfo userInfo = userInfoService.test_ChainedTransaction(true, name);
		Assert.assertTrue(userInfo.getId() > 0);
		{// 测试删除失败
			try {
				userInfoService.test_del(false, name);
			} catch (Exception e) {
			}
			Assert.assertNotNull(userInfoMapper.get(userInfo.getId()));
		}

		{// 测试删除成功
			userInfoService.test_del(true, name);
			Assert.assertNull(userInfoMapper.get(userInfo.getId()));
		}
	}

	@Test(expected = CannotCreateTransactionException.class)
	public void test_b() {
		userInfoService.test_Transaction_MANDATORY(null);
		Assert.fail("没有出现预期错误");
	}

	@Test
	public void test_c() {
		String name = "test-C";
		UserInfo userInfo = userInfoService2.test_Transaction_REQUIRES_NEW(true, name);
		Assert.assertTrue(userInfo.getId() > 0);
		userInfo = userInfoMapper.get(userInfo.getId());
		Assert.assertEquals(name, userInfo.getName());
		{
			userInfo = userInfoMapper.getByName(name + "-MANDATORY");
			Assert.assertNotNull(userInfo);
			int res = userInfoMapper.delete(userInfo.getId());
			Assert.assertTrue(res > 0);
		}
		{
			userInfo = userInfoMapper.getByName(name);
			int res = userInfoMapper.delete(userInfo.getId());
			Assert.assertTrue(res > 0);
		}
	}

	@Test
	public void test_d() {
		String name = "test-D";
		try {
			userInfoService2.test_Transaction_REQUIRES_NEW_error(name);
			Assert.fail("没有出现预期错误");
		} catch (Exception e) {
		}
		{
			UserInfo userInfo = userInfoMapper.getByName(name);
			Assert.assertNull(userInfo);
		}
	}

	@Test
	public void test_e() {
		Assert.assertTrue(userInfoService.useTransactionApi());
	}
}
