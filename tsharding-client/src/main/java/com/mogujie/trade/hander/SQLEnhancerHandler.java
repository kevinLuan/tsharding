package com.mogujie.trade.hander;

/**
 * SQL增强处理器
 * @CreateTime 2016年8月6日 上午9:45:06
 * @author SHOUSHEN LUAN
 */
public interface SQLEnhancerHandler {

	/**
	 * 是否存在需要替换的表名称
	 */
	public boolean hasReplace(String sql);

	/**
	 * 修改SQL中的表名称
	 */
	public String format(String text, long value);
}
