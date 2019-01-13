package com.mogujie.tsharding.filter;

public interface MapperHandlerInterceptor {
	public Object invoker(InvocationProxy invocation) throws Throwable;
}
