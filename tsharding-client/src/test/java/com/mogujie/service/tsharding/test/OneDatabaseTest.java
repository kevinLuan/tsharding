package com.mogujie.service.tsharding.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mogujie.service.tsharding.bean.UserInfo;
import com.mogujie.service.tsharding.mapper.UserInfoMapper;
import com.mogujie.trade.db.ReadWriteSplittingDataSource.NotFoundDataSource;

/**
 * 单数据库
 * 
 * @CreateTime 2016年8月3日 下午12:01:01
 * @author SHOUSHEN LUAN
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-tsharding.xml" })
public class OneDatabaseTest {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void insert() {
        for (int i = 0; i < 1000; i++) {
            long start = System.currentTimeMillis();
            UserInfo userInfo = new UserInfo();
            userInfo.setName("kevin");
            userInfo.setAge(10);
            userInfo.setSex(1);
            int res = userInfoMapper.insert(userInfo);
            Assert.assertTrue(res == 1);
            System.out.println("\tinsert use time:" + (System.currentTimeMillis() - start));
        }
    }

    @Test
    public void get() {
    	UserInfo userInfo = userInfoMapper.getByName("zhangsan");
    	if(userInfo==null){
    		userInfo=new UserInfo();
    		userInfo.setName("zhangsan");
    		userInfo.setNickName("Kevin");
    		userInfo.setAge(1);
    		userInfo.setSex(1);
    		int res= userInfoMapper.insert(userInfo);
    		Assert.assertEquals(1, res);
    	}
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            userInfo = userInfoMapper.getByName("zhangsan");
            System.out.println(userInfo);
            Assert.assertTrue(userInfo != null);
            userInfo = userInfoMapper.get(userInfo.getId());
            System.out.println(userInfo);
            Assert.assertTrue(userInfo != null);
            Assert.assertEquals("Kevin", userInfo.getNickName());
            System.out.println("\tget use time:" + (System.currentTimeMillis() - start));
        }

    }

    @Test
    public void delete() {
        for (int i = 0; i < 1000; i++) {
            long start = System.currentTimeMillis();
            UserInfo userInfo = userInfoMapper.getByName("kevin");
            System.out.println(userInfo);
            Assert.assertTrue(userInfo != null);
            int res = userInfoMapper.delete(userInfo.getId());
            Assert.assertTrue(res > 0);
            System.out.println("\tdelete use time:" + (System.currentTimeMillis() - start));
        }

    }

    @Test(expected = NotFoundDataSource.class)
    public void test_no_slave_datasouce() throws Throwable {
        try {
            userInfoMapper.test_no_slave_datasouce();
        } catch (Exception ex) {
            Throwable th = ex.getCause();
            th = th.getCause();
            th = th.getCause();
            th = th.getCause();
            System.out.println(th.getMessage());
            Assert.assertEquals("Not found slave datasource:`simpleDataBase`", th.getMessage());
            throw th;
        }
    }

    // 串行测试结果
    // druid
    // total time:13580
    // total time:14634
    // total time:14324
    // Hikari
    // total time:12435
    // total time:11991
    // total time:11630

    @Test(timeout = 20000)
    public void load_db_test() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            long start = System.currentTimeMillis();
            UserInfo userInfo = new UserInfo();
            userInfo.setName("kevin");
            userInfo.setAge(10);
            userInfo.setSex(1);
            int res = userInfoMapper.insert(userInfo);
            Assert.assertTrue(res == 1);
            res = userInfoMapper.delete(userInfo.getId());
            Assert.assertTrue(res == 1);
            res = userInfoMapper.delete(userInfo.getId());
            Assert.assertTrue(res == 0);
            // System.out.println("use time:" + (System.currentTimeMillis() - start));
        }
        System.out.println("total time:" + (System.currentTimeMillis() - startTime));
    }
}
