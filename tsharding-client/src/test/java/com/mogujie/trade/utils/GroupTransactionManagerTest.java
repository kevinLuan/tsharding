package com.mogujie.trade.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.trade.utils.GroupTransactionManager.TmGroup;
import com.mogujie.trade.utils.GroupTransactionManager.TmGroup.Entry;

public class GroupTransactionManagerTest {
	@Test
	public void test() {
		GroupTransactionManager.register("productTransactionManager");
		GroupTransactionManager.register("simpleDataBaseTransactionManager");
		GroupTransactionManager.register("user0TransactionManager");
		GroupTransactionManager.register("trade0001TransactionManager");
		GroupTransactionManager.register("trade0000TransactionManager");
		GroupTransactionManager.register("user1TransactionManager");

		List<String> list = GroupTransactionManager.group("productTransactionManager");
		Assert.assertEquals("[productTransactionManager]", Arrays.toString(list.toArray()));

		list = GroupTransactionManager.group("userTransactionManager");
		Assert.assertEquals("[user1TransactionManager, user0TransactionManager]", Arrays.toString(list.toArray()));

		list = GroupTransactionManager.group("tradeTransactionManager");
		Assert.assertEquals("[trade0000TransactionManager, trade0001TransactionManager]", Arrays.toString(list.toArray()));

		list = GroupTransactionManager.group("simpleDataBaseTransactionManager");
		Assert.assertEquals("[simpleDataBaseTransactionManager]", Arrays.toString(list.toArray()));

		TmGroup tmGroup = GroupTransactionManager.group();
		while (tmGroup.hasNext()) {
			Entry entry = tmGroup.next();
			System.out
					.println(entry.getName() + "-" + entry.isManyDataSource() + "->" + Arrays.toString(entry.getTransction().toArray()));
		}
	}
}
