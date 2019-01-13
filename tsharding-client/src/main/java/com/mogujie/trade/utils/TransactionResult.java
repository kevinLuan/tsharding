package com.mogujie.trade.utils;

import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.UnexpectedRollbackException;

public class TransactionResult {
	private Throwable throwable;
	private Type type;

	public enum Type {
		COMMIT, ROLLBACK
	}

	public static TransactionResult commit() {
		TransactionResult result = new TransactionResult();
		result.type = Type.COMMIT;
		return result;
	}

	public static TransactionResult rollback() {
		TransactionResult result = new TransactionResult();
		result.type = Type.ROLLBACK;
		return result;
	}

	public TransactionResult initCause(Throwable t) {
		this.throwable = t;
		return this;
	}

	/**
	 * 是否完成事物`提交`或`回滚`操作
	 * 
	 * @return
	 */
	public boolean isCompleted() {
		return throwable == null;
	}

	// org.springframework.transaction.UnexpectedRollbackException: Rollback
	// exception, originated at
	// (com.mogujie.trade.db.RoutingDataSourceTransactionManager@3a5a5f8b)
	// Transaction is already completed - do not call commit or rollback more
	// than once per transaction; nested exception is
	// org.springframework.transaction.IllegalTransactionStateException:
	// Transaction is already completed - do not call commit or rollback more
	// than once per transaction at
	// org.springframework.data.transaction.ChainedTransactionManager.rollback(ChainedTransactionManager.java:206)
	public boolean isUnexpectedRollbackException() {
		return UnexpectedRollbackException.class == throwable.getClass();
	}

	public boolean isHeuristicCompletionException() {
		return HeuristicCompletionException.class == throwable.getClass();
	}

	public boolean isCommitOper() {
		return type == Type.COMMIT;
	}

	public boolean isRollbackOper() {
		return type == Type.ROLLBACK;
	}

	public Throwable getCause() {
		return throwable;
	}

	public Type getType() {
		return type;
	}
}
