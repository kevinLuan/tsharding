package com.mogujie.trade.utils;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 事物管理工具
 * 
 * @author SHOUSHEN LUAN create date: 2017年1月14日
 */
public class TransactionManagerUtils {

	/**
	 * 创建默认事物定义
	 * 
	 * @return
	 */
	private static DefaultTransactionDefinition getDefaultTransactionDefinition(int timeout) {
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		// 定义如隔离级别、传播行为等，即在本示例中隔离级别为ISOLATION_READ_COMMITTED（提交读），
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		// 传播行为为
		// PROPAGATION_REQUIRED（必须有事务支持，即如果当前没有事务，就新建一个事务，如果已经存在一个事务中，就加入到这个事务中）。
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		if (timeout > 0) {
			definition.setTimeout(timeout);
		}
		return definition;
	}

	/**
	 * 创建事务管理器
	 * 
	 * @param tm
	 * @return
	 */
	public static TransactionProxy createTransaction(PlatformTransactionManager tm, TransactionAttribute attribute) {
		TransactionStatus status = tm.getTransaction(attribute);
		return new TransactionProxy(tm, status);
	}

	/**
	 * 创建事务管理器
	 * 
	 * @param tm
	 * @return
	 */
	public static TransactionProxy createTransaction(PlatformTransactionManager tm, int timeout) {
		DefaultTransactionDefinition definition = getDefaultTransactionDefinition(timeout);
		// 事务状态类，通过PlatformTransactionManager的getTransaction方法根据事务定义获取；获取事务状态后，Spring根据传播行为来决定如何开启事务；
		TransactionStatus status = tm.getTransaction(definition);
		return new TransactionProxy(tm, status);
	}

	public static TransactionProxy createTransaction(PlatformTransactionManager tm) {
		DefaultTransactionDefinition definition = getDefaultTransactionDefinition(-1);
		// 事务状态类，通过PlatformTransactionManager的getTransaction方法根据事务定义获取；获取事务状态后，Spring根据传播行为来决定如何开启事务；
		TransactionStatus status = tm.getTransaction(definition);
		return new TransactionProxy(tm, status);
	}

	/**
	 * 事物管理器Proxy类
	 * 
	 * @author SHOUSHEN LUAN create date: 2017年1月14日
	 */
	public static class TransactionProxy {
		private PlatformTransactionManager delegation;
		private TransactionStatus ts;

		public TransactionProxy(PlatformTransactionManager tm, TransactionStatus txStatus) {
			this.delegation = tm;
			this.ts = txStatus;
		}

		/**
		 * 提交事物
		 */
		public TransactionResult commit() {
			try {
				delegation.commit(ts);
				return TransactionResult.commit();
			} catch (Throwable t) {
				return TransactionResult.commit().initCause(t);
			}
		}

		/**
		 * 事物回滚
		 */
		public TransactionResult rollback() {
			try {
				this.delegation.rollback(ts);
				return TransactionResult.rollback();
			} catch (Throwable t) {
				return TransactionResult.rollback().initCause(t);
			}
		}
	}
}
