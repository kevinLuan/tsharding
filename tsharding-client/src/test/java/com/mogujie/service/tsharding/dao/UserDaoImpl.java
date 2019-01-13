package com.mogujie.service.tsharding.dao;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mogujie.service.tsharding.bean.User;
import com.mogujie.service.tsharding.mapper.UserMapper;
import com.mogujie.trade.utils.TransactionManagerUtils;
import com.mogujie.trade.utils.TransactionManagerUtils.TransactionProxy;

@Service("userDao")
public class UserDaoImpl implements UserDao {
	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional(value = "userTransactionManager", rollbackFor = Throwable.class)
	public int insert(List<User> list) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			count += userMapper.insertData(list.get(i));
		}
		return count;
	}

	@Transactional(value = "userTransactionManager", rollbackFor = Throwable.class)
	public int insertBatch(List<User> list) {
		int res = userMapper.insertBatch(list);
		System.out.println("---->>>" + res);
		return res;
	}

	@Transactional(value = "userTransactionManager", rollbackFor = Throwable.class)
	public int test_distributed_transaction(User user1, User user2, User user3) {
		int res = userMapper.insertData(user1);
		System.out.println("insert 1->" + res);
		res += userMapper.insertData(user2);
		System.out.println("insert 2->" + res);
		res += userMapper.insertData(user3);
		System.out.println("insert 3->" + res);
		int num = 1 / 0;// 异常回滚
		return res;
	}

	@Autowired
	private ChainedTransactionManager userTransactionManager;

	public boolean testUserDefinedDistributedTransctionManager(User... users) {
		TransactionProxy transactionProxy = TransactionManagerUtils.createTransaction(userTransactionManager);
		int res = 0;
		for (User user : users) {
			res += userMapper.insertData(user);
			System.out.println("insert data " + user.getId() + "--->true");
		}
		if (new Random().nextInt(100) == 0) {
			System.out.println("触发失败回滚机制");
			res = 0;// 准备触发回滚操作
		}
		if (res == users.length) {
			System.out.println("commit transction");
			transactionProxy.commit();
			return true;
		} else {
			System.out.println("rollback transction");
			transactionProxy.rollback();
			return false;
		}
	}
}
