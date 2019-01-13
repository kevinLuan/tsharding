package com.mogujie.trade.utils;

/**
 * 增强Mapper工具
 * 
 * @author SHOUSHEN LUAN create date: 2017年1月14日
 */
public class EnhanceMapperMethodUtils {
	/**
	 * 分表数量默认最大为512，超过512时，需要对Mapper$method 进行分段扩展，以避免生成的Mapper实例超过65525字节问题
	 */
	public static final int MAX_SEGMENTATION = 512;

	/**
	 * 分段处理
	 * 
	 * @param tables
	 * @return
	 */
	public static Entity segmentation(int tables) {
		if (isSegmentation(tables)) {
			// 分段数(总页码)
			int segemations = tables / MAX_SEGMENTATION;
			if (tables % MAX_SEGMENTATION != 0) {
				segemations++;
			}
			return new Entity(segemations, tables);
		} else {
			return new Entity(1, tables);
		}

	}

	/**
	 * 是否进行分段扩展
	 * 
	 * @param tables
	 * @return
	 */
	static boolean isSegmentation(int tables) {
		return tables > MAX_SEGMENTATION;
	}

	public static class Entity {
		/**
		 * 分段数量
		 */
		private int segemations;
		/**
		 * 段落片（分段的页码）
		 */
		private int section = -1;
		private int tables;

		public Entity(int segemations, int tables) {
			this.segemations = segemations;
			this.tables = tables;
		}

		public void reset() {
			this.section = -1;
		}

		public boolean hasSegemation() {
			return segemations > section + 1;
		}

		public MethodSegemation nextSegemation() {
			if (hasSegemation()) {
				section++;
				return new MethodSegemation(section, tables);
			}
			throw new IndexOutOfBoundsException("已超过最大段落");
		}
	}

	public static class MethodSegemation {
		/**
		 * 段落片（分段的页码）
		 */
		private int section;
		/**
		 * 分表数量
		 */
		private int tables;

		public MethodSegemation(int section, int tables) {
			this.section = section;
			this.tables = tables;
		}

		/**
		 * 获取增强方法片段的起始点
		 * <p/>
		 * 其实数据为0开始
		 * 
		 * @return
		 */
		public int getStart() {
			return section * MAX_SEGMENTATION;
		}

		/**
		 * 获取增强方法片段的结束点
		 * 
		 * @return
		 */
		public int getEnd() {
			if (getStart() + MAX_SEGMENTATION >= tables) {
				return tables - 1;
			} else {
				return getStart() + MAX_SEGMENTATION - 1;
			}
		}

		public int getSection() {
			return this.section;
		}

		/**
		 * 验证表后缀是否在当前片段中
		 * 
		 * @param tableSuffix
		 *            表后缀
		 * @return
		 */
		public boolean isCurrentSegemation(int tableSuffix) {
			if (getEnd() < tableSuffix) {
				return false;
			} else if (getStart() > tableSuffix) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 获取 Sharding method class
	 * 
	 * @param mapperClassName
	 * @param methodName
	 * @param segemation
	 * @return
	 */
	public static String getShardingClass(String mapperClassName, String methodName, int segemation) {
		return mapperClassName + "$Sharding$" + methodName + "$" + segemation;
	}

	/**
	 * Sharding method class
	 * 
	 * @param mapperClassName
	 * @param methodName
	 * @param segemation
	 * @return
	 */
	public static String getShardingClass(String mapperClassName, String methodName) {
		return mapperClassName + "$Sharding$" + methodName;
	}

	/**
	 * mark Sharding method class
	 * 
	 * @param mapperClassName
	 * @param methodName
	 * @param segemation
	 * @return
	 */
	public static String getShardingClass(String mapperClassName, String methodName, MethodSegemation segemation) {
		return getShardingClass(mapperClassName, methodName, segemation.getSection());
	}

	/**
	 * 根据分表后缀获取分片段落
	 * 
	 * @param tableSuffix
	 *            分表后缀
	 */
	public static int getSegemation(int tables, int tableSuffix) {
		Entity entity = EnhanceMapperMethodUtils.segmentation(tables);
		while (entity.hasSegemation()) {
			MethodSegemation methodSegemation = entity.nextSegemation();
			if (methodSegemation.isCurrentSegemation(tableSuffix)) {
				return methodSegemation.getSection();
			}
		}
		throw new IllegalArgumentException("参数错误 tables:`" + tables + "` tableSuffix:`" + tableSuffix + "`");
	}

	/**
	 * generated mapper statement
	 * 
	 * @param ms
	 * @param tableSuffix
	 * @param tables
	 * @return
	 */
	public static String getMappedStatement(String msId, String tableSuffix, int tables) {
		String mapper = parserMapper(msId);
		String method = getMethodName(msId);
		int table_suffix = Integer.parseInt(tableSuffix.trim());
		int segemation = EnhanceMapperMethodUtils.getSegemation(tables, table_suffix);
		String newMsId = getShardingClass(mapper, method, segemation);
		return newMsId + "." + method + tableSuffix;
	}

	private static String parserMapper(String msId) {
		int lastIndex = msId.lastIndexOf(".");
		return msId.substring(0, lastIndex);
	}

	/**
	 * 获取执行的方法名
	 * 
	 * @param msId
	 * @return
	 */
	public static String getMethodName(String msId) {
		return msId.substring(msId.lastIndexOf(".") + 1);
	}
}
