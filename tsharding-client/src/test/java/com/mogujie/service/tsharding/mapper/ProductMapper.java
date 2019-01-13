package com.mogujie.service.tsharding.mapper;

import com.mogujie.service.tsharding.bean.Product;
import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.db.DataSourceType;
import com.mogujie.trade.db.ReadWriteSplitting;

@DataSourceRouting(dataSource = "product", table = "product", isReadWriteSplitting = true)
public interface ProductMapper {
	public int insert(Product product);

	public int delete(int id);

	public Product get(int id);

	@ReadWriteSplitting(DataSourceType.master)
	public Product testFormMasterLoader(String name);
}
