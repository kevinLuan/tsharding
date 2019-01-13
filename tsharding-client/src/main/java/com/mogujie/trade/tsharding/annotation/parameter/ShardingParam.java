package com.mogujie.trade.tsharding.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在Mapper接口中的方法参数上使用
 * 
 * @CreateTime 2016年8月3日 下午10:16:51
 * @author SHOUSHEN LUAN
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardingParam {
	/**
	 * 在传入为对象类型时，会根据传入的field解析Sharding参数
	 * <p>
	 * 例如：使用参数order.orderId 作为Shardig 参数是，设置value=orderId即可
	 * </p>
	 */
	String value() default "";
}
