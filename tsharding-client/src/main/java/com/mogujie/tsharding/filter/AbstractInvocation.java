package com.mogujie.tsharding.filter;

import com.mogujie.trade.tsharding.route.orm.base.Invocation;

public abstract class AbstractInvocation implements InvocationProxy {
	private Invocation invocation;

	public AbstractInvocation(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Invocation getInvocation() {
		return invocation;
	}
}
