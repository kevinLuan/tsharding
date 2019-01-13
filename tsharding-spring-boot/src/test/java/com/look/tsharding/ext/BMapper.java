package com.look.tsharding.ext;

import com.mogujie.trade.db.DataSourceRouting;

@DataSourceRouting(dataSource = "b", table = "b_table", tables = 10, databases = 256, isReadWriteSplitting = false)
public interface BMapper {

}
