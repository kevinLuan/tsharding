package com.mogujie.trade.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import org.junit.Assert;
import org.junit.Test;
import com.mogujie.route.rule.RouteRuleFactory;
import com.mogujie.service.tsharding.bean.ShopOrder;
import com.mogujie.service.tsharding.bean.User;
import com.mogujie.service.tsharding.mapper.ShopOrderMapper;
import com.mogujie.service.tsharding.mapper.UserMapper;
import com.mogujie.trade.hander.MapperFactory;
import com.mogujie.trade.hander.MapperFactory.ShardingHanderEntry;

public class ShardingGroupTest {
  @Test
  public void test() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException,
      SecurityException {
    init();
    process();
    System.out.println("-------------");
    processCRC32Rule();
    Assert.assertTrue(crc32Calc(7) % 2 == 0);
    Assert.assertTrue(crc32Calc(4) % 2 == 0);
    Assert.assertTrue(crc32Calc(1) % 2 == 1);
    Assert.assertTrue(crc32Calc(2) % 2 == 1);
    Assert.assertTrue(crc32Calc(7) % 3 == 0);
    Assert.assertTrue(crc32Calc(6) % 3 == 1);
    Assert.assertTrue(crc32Calc(1) % 3 == 2);
  }

  private static long crc32Calc(long num) {
    CRC32 crc32 = new CRC32();
    crc32.update(String.valueOf(num).getBytes());
    return crc32.getValue();
  }

  private static void process() throws IllegalAccessException, IllegalArgumentException,
      NoSuchMethodException, SecurityException {
    Method method = null;
    Method[] methods = ShopOrderMapper.class.getMethods();
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].getName().equals("batchUpdateShopOrderByShopOrderIds")) {
        method = methods[i];
      }
    }
    MapperFactory.registerMapper(ShopOrderMapper.class);
    List<Long> list = new ArrayList<>();
    list.add(6L);
    list.add(7L);
    list.add(8L);
    list.add(1L);
    list.add(2L);
    list.add(3L);
    list.add(4L);
    list.add(5L);
    ShardingHanderEntry entry = MapperFactory.getShardingHanderEntry(ShopOrderMapper.class, method);
    Object[] params = new Object[] {list, null};
    Object shardingVal = entry.getRouteParam(params);
    List<Long> shardingParam = (List<Long>) shardingVal;
    GroupDBTable dbTable = new GroupDBTable(entry.getRouting(), shardingParam, create1());
    for (int i = 0; i < dbTable.db_table.length; i++) {
      for (int j = 0; j < dbTable.db_table[i].length; j++) {
        if (dbTable.db_table[i][j] != null) {
          System.out.println(i + "-" + j + "-" + Arrays.toString(dbTable.db_table[i][j].toArray()));
        }
      }
    }

    while (dbTable.hasNext()) {
      System.out.println(Arrays.toString(dbTable.next().toArray()));
    }
  }

  private static void processCRC32Rule() throws IllegalAccessException, IllegalArgumentException,
      NoSuchMethodException, SecurityException {
    Method method = null;
    Method[] methods = UserMapper.class.getMethods();
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].getName().equals("insertBatch")) {
        method = methods[i];
      }
    }
    MapperFactory.registerMapper(UserMapper.class);
    List<Object> list = new ArrayList<>();
    list.add(new User(1));
    list.add(new User(2));
    list.add(new User(3));
    list.add(new User(4));
    list.add(new User(5));
    list.add(new User(6));
    list.add(new User(7));

    ShardingHanderEntry entry = MapperFactory.getShardingHanderEntry(UserMapper.class, method);
    Object[] params = new Object[] {list};
    Object shardingVal = entry.getRouteParam(params);
    List<Object> shardingParam = (List<Object>) shardingVal;
    GroupDBTable dbTable = new GroupDBTable(entry.getRouting(), shardingParam, create());
    for (int i = 0; i < dbTable.db_table.length; i++) {
      for (int j = 0; j < dbTable.db_table[i].length; j++) {
        if (dbTable.db_table[i][j] != null) {
          System.out.println(i + "-" + j + "-" + Arrays.toString(dbTable.db_table[i][j].toArray()));
        }
      }
    }

    while (dbTable.hasNext()) {
      System.out.println(Arrays.toString(dbTable.next().toArray()));
    }
  }

  public static void init() {
    RouteRuleFactory.register(UserMapper.class);
    RouteRuleFactory.register(ShopOrderMapper.class);
  }

  public static ShardingHanderEntry create()
      throws NoSuchMethodException, SecurityException, IllegalAccessException {
    Method method = UserMapper.class.getMethod("insertBatch", List.class);
    ShardingHanderEntry shardingHanderEntry = new ShardingHanderEntry(UserMapper.class, method);
    return shardingHanderEntry;
  }

  public static ShardingHanderEntry create1()
      throws NoSuchMethodException, SecurityException, IllegalAccessException {
    Method method = ShopOrderMapper.class.getMethod("batchUpdateShopOrderByShopOrderIds",
        List.class, ShopOrder.class);
    ShardingHanderEntry shardingHanderEntry =
        new ShardingHanderEntry(ShopOrderMapper.class, method);
    return shardingHanderEntry;
  }
}
