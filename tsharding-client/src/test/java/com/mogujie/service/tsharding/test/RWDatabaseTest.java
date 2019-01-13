package com.mogujie.service.tsharding.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.mogujie.service.tsharding.bean.Product;
import com.mogujie.service.tsharding.mapper.ProductMapper;
import com.mogujie.trade.db.DataSourceRouting;

/**
 * 读写分离
 * 
 * @CreateTime 2016年8月3日 下午2:59:47
 * @author SHOUSHEN LUAN
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
public class RWDatabaseTest {
	@Autowired
	private ProductMapper productMapper;

	@Test
	public void insert() {
		for (int i = 0; i < 1000; i++) {
			long start = System.currentTimeMillis();
			Product product = new Product();
			product.setName("Mac book");
			product.setPrice(19999.12);
			int res = productMapper.insert(product);
			Assert.isTrue(res == 1);
			System.out.println("\tinsert use time:" + (System.currentTimeMillis() - start));
		}
		delete();
	}

	@Test
	public void get() {
		for (int i = 0; i < 1000; i++) {
			long start = System.currentTimeMillis();
			Product product = productMapper.testFormMasterLoader("Mac book 123");
			Assert.isNull(product);
			product = productMapper.testFormMasterLoader("Iphone5");
			product = productMapper.get(product.getId());
			product = productMapper.testFormMasterLoader("` ' \"\t \n \r and or & | ");
			Assert.isNull(product);
			System.out.println("\tget use time:" + (System.currentTimeMillis() - start));
		}

	}

	public void delete() {
		for (int i = 0; i < 1000; i++) {
			long start = System.currentTimeMillis();
			// 注意查询走的是Slave
			Product product = productMapper.testFormMasterLoader("Mac book");
			Assert.isTrue(product != null);
			// 删除走的是Master
			int res = productMapper.delete(0);
			Assert.isTrue(res == 0);
			System.out.println("\tdelete use time:" + (System.currentTimeMillis() - start));
		}

	}

	@Test
	public void test_rw() {
		Product product = productMapper.testFormMasterLoader("from_trader_r");
		if (ProductMapper.class.getAnnotation(DataSourceRouting.class).isReadWriteSplitting()) {
			Assert.notNull(product);
		} else {
			Assert.isNull(product);
		}
		System.out.println(product);
	}
}
