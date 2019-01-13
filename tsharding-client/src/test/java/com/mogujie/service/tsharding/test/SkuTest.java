package com.mogujie.service.tsharding.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mogujie.service.tsharding.bean.Sku;
import com.mogujie.service.tsharding.bean.Vedio;
import com.mogujie.service.tsharding.mapper.SplitTableSkuMapper;
import com.mogujie.service.tsharding.mapper.SplitDBVedioMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
public class SkuTest {
	@Autowired
	private SplitTableSkuMapper splitTableSkuMapper;
	@Autowired
	private SplitDBVedioMapper splitDBVedioMapper;

	@Test
	public void test_split_table() {
		for (int i = 1; i < 1000; i++) {
			Sku sku = new Sku();
			sku.setProduct_id(i);
			sku.setName("test->" + i);
			splitTableSkuMapper.insertData(sku);
		}
	}

	@Test
	public void test_split_db() {
		for (int i = 1; i < 1000; i++) {
			Vedio sku = new Vedio();
			sku.setProduct_id(i);
			sku.setName("test->" + i);
			splitDBVedioMapper.insertData(sku);
		}
	}
}
