package com.mogujie.trade.utils;

import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.mogujie.route.rule.RouteRuleFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-tsharding.xml"})
public class RouteRuleFactoryTest {
  @Test
  public void test_getEnhancedMappers() {
    try {
      System.out.println(Arrays.toString(RouteRuleFactory.getEnhancedMappers().toArray()));
      Assert.assertEquals(5, RouteRuleFactory.getEnhancedMappers().size());
      Set<Class<?>> set = RouteRuleFactory.getEnhancedMappers();
      System.out.println(set);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
