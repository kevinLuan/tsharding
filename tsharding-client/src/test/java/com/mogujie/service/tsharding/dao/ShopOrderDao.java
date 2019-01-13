package com.mogujie.service.tsharding.dao;

import java.util.List;

import com.mogujie.service.tsharding.bean.ShopOrder;

/**
 * @auther qigong on 6/5/15 8:50 PM.
 */
public interface ShopOrderDao {

	/**
	 * 根据店铺级订单ID获取订单信息（同一个买家）
	 *
	 * @param listShopOrderIds
	 *            店铺级订单ID集合
	 * @return List<XdShopOrder>
	 */
	List<ShopOrder> getShopOrderByShopOrderIds(List<Long> listShopOrderIds);

	public boolean insert(ShopOrder order);

	/**
	 * 插入数据出错不会滚
	 */
	public boolean insert_err_no_rollback(ShopOrder order);

	/**
	 * 插入数据出错回滚
	 */
	public boolean insert_rollback(ShopOrder order);

	/**
	 * 测试编程式分布式事物
	 * 
	 * @param order
	 * @param isCommit
	 * @return
	 */
	public boolean programmeTransaction(ShopOrder order, boolean isCommit);

	/**
	 * 测试运行时异常回滚事物
	 * 
	 * @param order
	 * @param isCommit
	 * @return
	 */
	public boolean chainedTransactionTestRollback(ShopOrder order, boolean isCommit);

	/**
	 * 测试运行时异常不回滚事物
	 * 
	 * @param order
	 * @return
	 */
	public boolean chainedTransactionTestNoRollback(ShopOrder order, long orderId);

	public boolean test_NoShardingParamErr();

	public boolean test_NoShardingParam();

	public boolean testShardingPojoList(List<ShopOrder> list);

	public boolean testErrorShardingParam(String str, List<ShopOrder> list);

	public boolean testShardingNumList(int num, List<Long> list);
}
