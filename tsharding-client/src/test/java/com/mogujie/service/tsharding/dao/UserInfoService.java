package com.mogujie.service.tsharding.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import com.mogujie.distributed.transction.ChainedTransaction;
import com.mogujie.distributed.transction.DynamicTransctionManagerFactory;
import com.mogujie.service.tsharding.bean.UserInfo;
import com.mogujie.service.tsharding.mapper.UserInfoMapper;
import com.mogujie.trade.utils.TransactionManagerUtils.TransactionProxy;

@Component
public class UserInfoService {
	@Autowired
	private UserInfoMapper userInfoMapper;

	@ChainedTransaction(mapper = { UserInfoMapper.class }, timeout = 2, propagation = Propagation.REQUIRES_NEW)
	public UserInfo test_ChainedTransaction(boolean succ, String name) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(name);
		userInfo.setAge(100);
		userInfo.setNickName("hello kitty");
		userInfo.setSex(1);
		userInfoMapper.insert(userInfo);
		if (succ && userInfo.getId() > 0) {
			return userInfo;
		}
		throw new RuntimeException("数据回滚");
	}

	@ChainedTransaction(mapper = { UserInfoMapper.class })
	public UserInfo test_del(boolean succ, String name) {
		UserInfo userInfo = userInfoMapper.getByName(name);
		int res = userInfoMapper.delete(userInfo.getId());
		if (succ && res > 0) {
			return userInfo;
		}
		throw new RuntimeException("数据回滚");
	}

	/**
	 * 测试当前事物必须在一个已经存在的事物中执行
	 * 
	 * @param succ
	 * @param name
	 * @return
	 */
	@ChainedTransaction(mapper = { UserInfoMapper.class }, propagation = Propagation.MANDATORY)
	public UserInfo test_Transaction_MANDATORY(String name) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(name);
		userInfo.setAge(100);
		userInfo.setNickName("hello kitty");
		userInfo.setSex(1);
		userInfoMapper.insert(userInfo);
		return userInfo;
	}

	@Autowired
	private DynamicTransctionManagerFactory dtmFactory;

	public boolean useTransactionApi() {
		{
			String name = "test-hive";
			TransactionProxy transactionProxy = dtmFactory.create().addTransManager(UserInfoMapper.class).build(1);
			UserInfo userInfo = new UserInfo();
			userInfo.setName(name);
			userInfo.setAge(100);
			userInfo.setNickName("hello kitty");
			userInfo.setSex(1);
			int res = userInfoMapper.insert(userInfo);
			System.out.println("插入数据:" + res);
			transactionProxy.commit();
			userInfo = userInfoMapper.getByName(name);
			if (userInfo == null) {
				return false;// 失败
			} else {
				if (userInfoMapper.delete(userInfo.getId()) < 1) {
					System.out.println("删除用户ID：" + userInfo.getId() + "失败");
					return false;
				} else {
					System.out.println("删除用户ID：" + userInfo.getId() + "成功");
				}
			}
		}
		{
			String name = "test-hive";
			TransactionProxy transactionProxy = dtmFactory.create().addTransManager(UserInfoMapper.class).build(1);
			UserInfo userInfo = new UserInfo();
			userInfo.setName(name);
			userInfo.setAge(100);
			userInfo.setNickName("hello kitty");
			userInfo.setSex(1);
			userInfoMapper.insert(userInfo);
			transactionProxy.rollback();
			userInfo = userInfoMapper.getByName(name);
			if (userInfo == null) {
				return true;
			}
		}
		return false;
	}
}
