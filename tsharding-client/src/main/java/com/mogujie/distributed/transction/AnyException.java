package com.mogujie.distributed.transction;

/**
 * 设置该异常处理器将会对任何异常都进行回滚操作
 * 
 * @author SHOUSHEN LUAN
 *
 */
public class AnyException extends RuntimeException {

	private static final long serialVersionUID = -9030574851839374479L;

	public AnyException() {
	}
}
