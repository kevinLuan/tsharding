package com.mogujie.distributed.transction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.Interceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mogujie.trade.db.DataSourceRouting;
import com.mogujie.trade.utils.TransactionManagerUtils.TransactionProxy;
import com.mogujie.trade.utils.TransactionResult;

/**
 * 链式事物管理拦截器
 * 
 * @author SHOUSHEN LUAN
 */
@Aspect
@Component
public class ChainedTransactionInteceptor implements Interceptor {
	@Autowired
	private DynamicTransctionManagerFactory dtmFactory;
	private final Map<String, Entity> cacheMap = new ConcurrentHashMap<>();

	@Around("@annotation(com.mogujie.distributed.transction.ChainedTransaction)")
	public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
		if (!cacheMap.containsKey(pjp.toLongString())) {
			cacheMap.put(pjp.toLongString(), new Entity(pjp));
		}
		Entity entity = cacheMap.get(pjp.toLongString());
		Class<?>[] mappers = entity.getMapper();
		DynamicTransctionManager transctionManager = dtmFactory.create();
		for (Class<?> mapper : mappers) {
			DataSourceRouting routing = mapper.getAnnotation(DataSourceRouting.class);
			if (routing.databases() > 1) {
				List<Object> params = FieldUtils.parserParam(mapper, entity, pjp.getArgs());
				if (params == null || params.size() == 0) {
					throw new IllegalArgumentException("mapper:`" + mapper
							+ "` ShardingParam must not empty. use @DataSourceRouting(" + mapper.getSimpleName() + "...)");
				}
				transctionManager.addTransManager(mapper, params);
			} else {
				transctionManager.addTransManager(mapper);
			}
		}
		TransactionProxy transactionProxy = transctionManager.build(entity.getTransactionAttribute());
		Throwable throwable = null;
		Object result = null;
		try {
			result = pjp.proceed(pjp.getArgs());
		} catch (Throwable e) {
			throwable = e;// DB操作异常
		}
		if (throwable != null && entity.isRollback(throwable)) {
			TransactionResult res = transactionProxy.rollback();
			if (res.isCompleted()) {
				// 完成回退操作，将异常传递到调用者
				throw throwable;
			} else {
				entity.doInvokeUnfinishedCallback(pjp, res);
				throw res.getCause();// 将异常传递到调用者
			}
		} else {
			TransactionResult res = transactionProxy.commit();
			if (res.isCompleted()) {
				if (throwable != null) {
					// 事物提交成功，将异常传递到调用者
					throw throwable;
				}
				return result;// 完成提交事物
			} else {
				entity.doInvokeUnfinishedCallback(pjp, res);
				throw res.getCause();// 将异常传递到调用者
			}
		}
	}
}
