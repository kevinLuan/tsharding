package com.mogujie.sharding.iterator;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.service.tsharding.mapper.ShopOrderMapper;
import com.mogujie.service.tsharding.mapper.UserMapper;
import com.mogujie.sharding.iterator.DataSourceEntity;
import com.mogujie.sharding.iterator.DataSourceIterator;
import com.mogujie.sharding.iterator.DataSourceIteratorX;
import com.mogujie.sharding.iterator.DataSourceSuffixIterator;
import com.mogujie.sharding.iterator.DataSourceTableEntity;
import com.mogujie.sharding.iterator.DataSourceTableIterator;
import com.mogujie.sharding.iterator.RouteIterator;
import com.mogujie.sharding.iterator.TableIterator;
import com.mogujie.sharding.iterator.TableSuffixIterator;

public class RouteIteratorTest {
	static {
		RouteRuleFactory.register(UserMapper.class);
		RouteRuleFactory.register(ShopOrderMapper.class);
	}

	@Test
	public void test_table() {
		RouteIterator<String> routeIterator = new TableIterator(UserMapper.class);
		int num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals("user_info" + num, routeIterator.next());
			num++;
		}
		routeIterator = new TableIterator(ShopOrderMapper.class);
		num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals("tradeorder000" + num, routeIterator.next());
			num++;
		}
	}

	@Test
	public void test_table_suffix() {
		RouteIterator<String> routeIterator = new TableSuffixIterator(UserMapper.class);
		int num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals(String.valueOf(num), routeIterator.next());
			num++;
		}
		routeIterator = new TableSuffixIterator(ShopOrderMapper.class);
		num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals(String.valueOf("000" + num), routeIterator.next());
			num++;
		}
	}

	@Test
	public void test_dataSource() {
		int num = 0;
		DataSourceIterator routeIterator = new DataSourceIterator(UserMapper.class);
		while (routeIterator.hasNext()) {
			Assert.assertEquals(String.valueOf("user" + num), routeIterator.next());
			Assert.assertEquals("user" + num + "MasterDataSource", routeIterator.getMasterDataSource(num));
			Assert.assertEquals("user" + num + "SlaveDataSource", routeIterator.getSlaveDataSource(num));
			num++;
		}
		Assert.assertEquals("userTransactionManager", routeIterator.getChaintTransactionManager());
		routeIterator = new DataSourceIterator(ShopOrderMapper.class);
		num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals(String.valueOf("trade000" + num), routeIterator.next());
			Assert.assertEquals("trade000" + num + "MasterDataSource", routeIterator.getMasterDataSource(num));
			Assert.assertEquals("trade000" + num + "SlaveDataSource", routeIterator.getSlaveDataSource(num));
			num++;
		}

		Assert.assertEquals("tradeTransactionManager", routeIterator.getChaintTransactionManager());

	}

	@Test
	public void test_dataSource_suffix() {
		RouteIterator<String> routeIterator = new DataSourceSuffixIterator(UserMapper.class);
		int num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals(String.valueOf(num), routeIterator.next());
			num++;
		}
		routeIterator = new DataSourceSuffixIterator(ShopOrderMapper.class);
		num = 0;
		while (routeIterator.hasNext()) {
			Assert.assertEquals(String.valueOf("000" + num), routeIterator.next());
			num++;
		}
	}

	@Test
	public void test_iterator() {
		RouteIterator<DataSourceTableEntity> routeIterator = new DataSourceTableIterator(UserMapper.class);
		int table_index = 0;
		int db_index = 0;
		while (routeIterator.hasNext()) {
			DataSourceTableEntity entity = routeIterator.next();
			Assert.assertEquals("user" + db_index, entity.getDataSource());
			Assert.assertEquals("user_info" + table_index, entity.getTable());
			System.out.println("数据库：" + entity.getDataSource() + "->表:" + entity.getTable());
			table_index++;
			if (table_index == 3) {
				table_index = 0;
				db_index++;
			}
		}
	}

	@Test
	public void test_dataSource_x() {
		RouteIterator<DataSourceEntity> iterator = new DataSourceIteratorX(UserMapper.class);
		int counter = 0;
		while (iterator.hasNext()) {
			DataSourceEntity entity = iterator.next();
			Assert.assertEquals("user" + counter, entity.getDataSource());
			counter++;
		}

	}
}
