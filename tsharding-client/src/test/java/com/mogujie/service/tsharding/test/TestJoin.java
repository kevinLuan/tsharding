package com.mogujie.service.tsharding.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mogujie.service.tsharding.mapper.UserOrderMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
public class TestJoin {
	@Autowired
	private UserOrderMapper userOrderMapper;

	/**
	 * 测试分库分表join查询
	 */
	@Test
	public void test_join() {
		try {
			long start = System.currentTimeMillis();
			for (int i =0; i < 10; i++) {
				List<Object>list=new ArrayList<>();
				list.add(1);
				list.add(2);
				int value = userOrderMapper.join_test(5,1,list);
				System.out.println("返回结果:"+value);
			}
			System.out.println("use time:" + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
