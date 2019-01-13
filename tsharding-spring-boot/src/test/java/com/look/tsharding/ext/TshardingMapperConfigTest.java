package com.look.tsharding.ext;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import com.look.tsharding.ext.TshardingMapperConfig;
import com.look.tsharding.ext.TshardingMapperConfig.EnvironmentConfig;
import com.look.tsharding.ext.TshardingMapperConfig.Mapper;
import com.mogujie.trade.db.DataSourceRouting;
import lombok.SneakyThrows;

public class TshardingMapperConfigTest {
  @Test
  public void test_get_resource_path() throws DocumentException, IOException {
    DefaultResourceLoader classPathResource = new DefaultResourceLoader();
    Resource resource = classPathResource.getResource("classpath:tsharding_mapper_config.xml");
    String path =
        "/tsharding/tsharding-spring-boot/target/test-classes/tsharding_mapper_config.xml";
    Assert.assertTrue(resource.getFile().getAbsolutePath().endsWith(path));;
  }

  @Test
  public void test_parse_xmlAsPojo() throws DocumentException, IOException {
    DefaultResourceLoader classPathResource = new DefaultResourceLoader();
    Resource resource = classPathResource.getResource("classpath:tsharding_mapper_config.xml");
    TshardingMapperConfig config = TshardingMapperConfig.of(null).parse(resource.getInputStream());
    EnvironmentConfig environment = config.getEnvironment("dev");
    Assert.assertFalse(environment.exists(this.getClass()));
    Assert.assertNull(environment.getMapper(this.getClass()));
    Assert.assertTrue(environment.exists(AMapper.class));
    Assert.assertNotNull(environment.getMapper(AMapper.class));
    Assert.assertTrue(environment.exists(BMapper.class));
    Assert.assertNotNull(environment.getMapper(BMapper.class));
    {
      environment = config.getEnvironment("t");
      Assert.assertFalse(environment.exists(this.getClass()));
      Assert.assertFalse(environment.exists(AMapper.class));
      Assert.assertFalse(environment.exists(BMapper.class));
    }
  }

  @Test
  @SneakyThrows
  public void test_annotation() {
    try {
      DataSourceRouting routing = AMapper.class.getAnnotation(DataSourceRouting.class);
      Assert.assertEquals("a", routing.dataSource());
      DefaultResourceLoader classPathResource = new DefaultResourceLoader();
      Resource resource = classPathResource.getResource("classpath:tsharding_mapper_config.xml");
      TshardingMapperConfig config =
          TshardingMapperConfig.of(null).parse(resource.getInputStream());
      EnvironmentConfig environment = config.getEnvironment("dev");
      Assert.assertTrue(environment.exists(AMapper.class));
      Mapper mapper = environment.getMapper(AMapper.class);
      mapper.modifyAnnotation(routing);
      Assert.assertEquals("db_01", routing.dataSource());
      Assert.assertEquals("tab_0001", routing.table());
      Assert.assertEquals(666, routing.tables());
      Assert.assertEquals(true, routing.isReadWriteSplitting());
    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = FileNotFoundException.class)
  public void test_err() throws IOException {
    DefaultResourceLoader classPathResource = new DefaultResourceLoader();
    Resource resource = classPathResource.getResource("classpath:xxxx.xml");
    System.out.println(resource.getFile().getAbsolutePath());
  }

}
