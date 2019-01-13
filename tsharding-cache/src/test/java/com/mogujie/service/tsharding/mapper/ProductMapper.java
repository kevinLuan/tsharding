package com.mogujie.service.tsharding.mapper;

import com.mogujie.service.tsharding.bean.Product;
import com.mogujie.trade.db.DataSourceRouting;

@DataSourceRouting(dataSource = "product", table = "product", isReadWriteSplitting = true)
public interface ProductMapper {
  public int insert(Product product);

  public int delete(int id);

  public Product get(int id);

  public Product getByName(String name);
}
