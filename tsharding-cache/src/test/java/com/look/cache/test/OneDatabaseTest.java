package com.look.cache.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.look.cache.utils.CacheConfig;
import com.mogujie.service.tsharding.bean.UserInfo;
import com.mogujie.service.tsharding.mapper.UserInfoMapper;

/**
 * 单数据库
 * 
 * @author SHOUSHEN LUAN
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-tsharding.xml"})
public class OneDatabaseTest {
  @Autowired
  private UserInfoMapper userInfoMapper;
  static {
    CacheConfig.slow_times = 0;
  }

  @Test
  public void insert() {
    for (int i = 0; i < 1000; i++) {
      System.out.println("======================" + i);
      long start = System.currentTimeMillis();
      userInfoMapper.getByName("张三");
      userInfoMapper.getByName("李四");
      userInfoMapper.getByName("张三");
      userInfoMapper.getByName("李四");
      UserInfo userInfo = new UserInfo();
      userInfo.setName("张三");
      userInfo.setAge(10);
      userInfo.setSex(1);
      userInfo.setNickName("李四");
      int res = userInfoMapper.insert(userInfo);
      Assert.assertTrue(res == 1);
      System.out.println("\tinsert use time:" + (System.currentTimeMillis() - start));
    }
  }

  @Test
  public void test_many_annotation() {
    for (int i = 0; i < 1000; i++) {
      System.out.println("------------------------" + i);
      userInfoMapper.get(0);
      System.out.println();
    }
  }

}
