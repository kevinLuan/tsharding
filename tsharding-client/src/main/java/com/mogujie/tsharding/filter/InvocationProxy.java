package com.mogujie.tsharding.filter;

import com.mogujie.trade.tsharding.route.orm.base.Invocation;

public interface InvocationProxy {
	public Invocation getInvocation();

	public Object doInvoker() throws Throwable;
}
