package com.look.tsharding.ext;

import com.mogujie.trade.db.DataSourceRouting;

@DataSourceRouting(dataSource = "a", table = "a_table", tables = 1, databases = 1, isReadWriteSplitting = false)
public interface AMapper {

}
