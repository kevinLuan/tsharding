package com.lyh.tsharding.scan;
//package com.hivescm.tsharding.scan;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.mogujie.trade.db.DataSourceRouting;
//import com.mogujie.trade.tsharding.route.orm.base.ClassPathScanHandler;
//
//public class ScanMapperUtils {
//	private static final Logger LOGGER = LoggerFactory.getLogger(ScanMapperUtils.class);
//	private static MapperInfo mapperInfo = null;
//
//	public static MapperInfo scanMapper() {
//		if (mapperInfo != null) {
//			return mapperInfo;
//		}
//		synchronized (ScanMapperUtils.class) {
//			if (mapperInfo != null) {
//				return mapperInfo;
//			}
//			Set<Class<?>> classSet = new ClassPathScanHandler().getPackageAllClasses("com.hivescm", true);
//			Set<Class<?>>mappperClass=new HashSet<>();
//			for (Class<?> clazz : classSet) {
//				if (clazz.getAnnotation(DataSourceRouting.class) != null) {
//					LOGGER.warn("Scan.Mapper-->"+clazz.getName());
//					mappperClass.add(clazz);
//				}
//			}
//			Set<String> mapperPackage = new HashSet<>();
//			Set<Class<?>> enhancedMapper = new HashSet<>();
//			for (Class<?> clazz : mappperClass) {
//				mapperPackage.add(clazz.getPackage().getName());
//				DataSourceRouting routing = clazz.getAnnotation(DataSourceRouting.class);
//				if (routing.tables() > 1 || routing.databases() > 1) {
//					enhancedMapper.add(clazz);
//				}
//			}
//			mapperInfo = new MapperInfo(mapperPackage, enhancedMapper);
//			LOGGER.warn("Tsharding.EnhancedMapper:{}", mapperInfo.buildEnhancedMapper());
//			LOGGER.warn("Tsharding.Mappers:{}", mapperInfo.buildMappers());
//		}
//		return mapperInfo;
//	}
//}
