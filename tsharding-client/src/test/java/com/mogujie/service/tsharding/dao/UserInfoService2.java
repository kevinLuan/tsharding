package com.mogujie.service.tsharding.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import com.mogujie.distributed.transction.ChainedTransaction;
import com.mogujie.service.tsharding.bean.UserInfo;
import com.mogujie.service.tsharding.mapper.UserInfoMapper;

@Component
public class UserInfoService2 {
	@Autowired
	private UserInfoMapper userInfoMapper;
	@Autowired
	private UserInfoService userService;

	/**
	 * 测试当前事物必须在一个已经存在的事物中执行
	 * 
	 * @param succ
	 * @param name
	 * @return
	 */
	@ChainedTransaction(mapper = { UserInfoMapper.class }, propagation = Propagation.REQUIRES_NEW)
	public UserInfo test_Transaction_REQUIRES_NEW(boolean succ, String name) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(name);
		userInfo.setAge(100);
		userInfo.setNickName("hello kitty");
		userInfo.setSex(1);
		userInfoMapper.insert(userInfo);
		if (userInfo.getId() > 0 && succ) {
			userService.test_Transaction_MANDATORY(name + "-MANDATORY");
			return userInfo;
		}
		throw new RuntimeException();
	}

	/**
	 * 测试当前事物必须在一个已经存在的事物中执行
	 * 
	 * @param succ
	 * @param name
	 * @return
	 */
	@ChainedTransaction(mapper = { UserInfoMapper.class }, propagation = Propagation.REQUIRES_NEW)
	public UserInfo test_Transaction_REQUIRES_NEW_error(String name) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(name);
		userInfo.setAge(100);
		userInfo.setNickName("hello kitty");
		userInfo.setSex(1);
		userInfoMapper.insert(userInfo);
		if (userInfo.getId() > 0) {
			userService.test_Transaction_MANDATORY(name + "-MANDATORY");
		}
		throw new RuntimeException();
	}
}
