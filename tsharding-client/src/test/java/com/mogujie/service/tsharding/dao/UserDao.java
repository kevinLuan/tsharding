package com.mogujie.service.tsharding.dao;

import java.util.List;

import com.mogujie.service.tsharding.bean.User;

public interface UserDao {
	public int insert(List<User> users);

	public int insertBatch(List<User> list);

	/**
	 * 测试分布式事物
	 * @param user1
	 * @param user2
	 * @param user3
	 */
	public int test_distributed_transaction(User user1, User user2, User user3);

	/**
	 * 用户自定义行为控制事务管理器操作
	 * @param users
	 */
	public boolean testUserDefinedDistributedTransctionManager(User... users);

}
