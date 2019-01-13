package com.mogujie.distributed.transction;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.mogujie.trade.utils.TransactionResult;

public class ProxyMethodMeta {
	private final Method proxyMethod;
	private final Object[] args;
	private final Object target;
	private final TransactionResult transactionResult;

	public ProxyMethodMeta(ProceedingJoinPoint pjp, TransactionResult transactionResult) {
		args = pjp.getArgs();
		target = pjp.getTarget();
		this.proxyMethod = ((MethodSignature) pjp.getSignature()).getMethod();
		this.transactionResult = transactionResult;
	}

	public Method getProxyMethod() {
		return proxyMethod;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object getTarget() {
		return target;
	}

	public TransactionResult getTransactionResult() {
		return transactionResult;
	}
}
