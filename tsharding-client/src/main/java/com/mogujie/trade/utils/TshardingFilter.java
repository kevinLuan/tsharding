package com.mogujie.trade.utils;

import com.mogujie.trade.tsharding.route.orm.base.Invocation;

public interface TshardingFilter {
	public void filter(Invocation invocation, long startTime, long doneTime);
}
