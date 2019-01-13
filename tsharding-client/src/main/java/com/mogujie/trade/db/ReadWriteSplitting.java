package com.mogujie.trade.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadWriteSplitting {
    /**
     * <p>
     * 如果方法上配置了@ReadWriteSplitting，者优先使用该注解指定的数据源。
     * <p>
     * <p>
     * 否则以@DataSourceRouting(isReadWriteSplitting=?)为准
     * </p>
     */
    DataSourceType value() default DataSourceType.master;
}