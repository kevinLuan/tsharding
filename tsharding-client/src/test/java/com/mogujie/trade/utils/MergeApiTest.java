package com.mogujie.trade.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.mogujie.sharding.merge.MergeApi;
import com.mogujie.sharding.merge.MergeFactory;

public class MergeApiTest {
  public List<Object> list(List<Object> list) {
    return list;
  }

  public Integer intNum(List<Object> list) {
    return 1;
  };

  public Long longNum(List<Object> list) {
    return 1L;
  };

  public void voidNum(List<Object> list) {

  };

  public String stringMethod(List<Object> list) {
    return "Error";
  };

  public Double doubleMethod(List<Object> list) {
    return 0.1d;
  }

  static class MergeDouble implements MergeApi {
    private Double value;

    @Override
    public void merge(Object value) {
      if (value != null) {
        if (this.value == null) {
          this.value = (Double) value;
        } else {
          this.value += (Double) value;
        }
      }
    }

    @Override
    public Object getValue() {
      return value;
    }

    @Override
    public boolean isSupport(Method method) {
      Class<?> clazz = method.getReturnType();
      return isSupport(clazz);
    }

    @Override
    public MergeApi newInstance() {
      return new MergeDouble();
    }

    @Override
    public boolean isSupport(Class<?> clazz) {
      if (clazz == double.class) {
        return true;
      } else if (Double.class.isAssignableFrom(clazz)) {
        return true;
      }
      return false;
    }
  }

  @Test
  public void test_many_merge_ref_double() throws NoSuchMethodException, SecurityException {
    MergeFactory.register(new MergeDouble());
    Method method = MergeApiTest.class.getMethod("doubleMethod", List.class);
    MergeApi mergeApi = MergeFactory.newMerge(method);
    mergeApi.merge(1.02D);
    mergeApi.merge(2.13D);
    Assert.assertEquals(3.15D, mergeApi.getValue());
  }

  /**
   * 测试不支持的返回类型
   * 
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  @Test(expected = IllegalArgumentException.class)
  public void test_many_merge_ref_string() throws NoSuchMethodException, SecurityException {
    Method method = MergeApiTest.class.getMethod("stringMethod", List.class);
    try {
      MergeFactory.newMerge(method);
    } catch (IllegalArgumentException e) {
      Assert.assertEquals("不支持的merge:java.lang.String类型", e.getMessage());
      System.out.println(e.getMessage());
      throw e;
    }
    Assert.assertTrue(false);
  }

  @Test
  public void test_many_merge_ref_void() throws NoSuchMethodException, SecurityException {
    Method method = MergeApiTest.class.getMethod("voidNum", List.class);
    MergeApi mergeApi = MergeFactory.newMerge(method);
    mergeApi.merge(null);
    mergeApi.merge(null);
    Assert.assertEquals(null, mergeApi.getValue());
  }

  @Test
  public void test_many_merge_ref_int() throws NoSuchMethodException, SecurityException {
    Method method = MergeApiTest.class.getMethod("intNum", List.class);
    MergeApi mergeApi = MergeFactory.newMerge(method);
    mergeApi.merge(1);
    mergeApi.merge(2);
    Assert.assertEquals(3, mergeApi.getValue());
  }

  @Test
  public void test_many_merge_ref_Long() throws NoSuchMethodException, SecurityException {
    Method method = MergeApiTest.class.getMethod("longNum", List.class);
    MergeApi mergeApi = MergeFactory.newMerge(method);
    mergeApi.merge(1L);
    mergeApi.merge(2L);
    Assert.assertEquals(3L, mergeApi.getValue());
  }

  @Test
  public void test_many_merge_ref() throws NoSuchMethodException, SecurityException {
    Method method = MergeApiTest.class.getMethod("list", List.class);
    MergeApi mergeApi = MergeFactory.newMerge(method);
    List<Object> data = new ArrayList<>();
    data.add(1);
    mergeApi.merge(data);
    data.remove(0);
    data.add(2);
    mergeApi.merge(data);
    Assert.assertEquals("[2]", mergeApi.getValue().toString());
  }

  @Test
  public void test_many_merge_ref_2() throws NoSuchMethodException, SecurityException {
    Method method = MergeApiTest.class.getMethod("list", List.class);
    MergeApi mergeApi = MergeFactory.newMerge(method);
    List<Object> data = new ArrayList<>();
    data.add(1);
    mergeApi.merge(data);
    data.add(2);
    mergeApi.merge(data);
    Assert.assertEquals("[1, 2]", mergeApi.getValue().toString());
  }

  @Test
  public void test() {
    Assert.assertTrue(MergeFactory.isSupportMerge(Integer.class));
    Assert.assertTrue(MergeFactory.isSupportMerge(int.class));
    Assert.assertTrue(MergeFactory.isSupportMerge(Long.class));
    Assert.assertTrue(MergeFactory.isSupportMerge(long.class));
    Assert.assertTrue(MergeFactory.isSupportMerge(List.class));
    Assert.assertTrue(MergeFactory.isSupportMerge(ArrayList.class));
    Assert.assertFalse(MergeFactory.isSupportMerge(String.class));
  }
}
