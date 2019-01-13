/*Master/slave数据库建库建表脚本-master*/
CREATE DATABASE `product_master` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9001 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Master/slave数据库建库建表脚本-slave*/
CREATE DATABASE `product_slave` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*单数据库建库建表脚本*/
CREATE DATABASE `simpleDataBase` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `age` int(11) NOT NULL,
  `sex` int(11) NOT NULL,
  `nick_name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52317 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*数据库分库分表建库建表脚本*/
CREATE DATABASE `trade0000` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `tradeorder0000` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `tradeorder0001` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `tradeorder0002` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `tradeorder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE DATABASE `trade0001` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `tradeorder0000` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `tradeorder0001` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `tradeorder0002` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `tradeorder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `buyerUserId` bigint(20) NOT NULL,
  `sellerUserId` bigint(20) NOT NULL,
  `shipTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/* user 库 分库分表数据路由规则CRC32方式的支持*/
CREATE DATABASE `user0` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `user_info0` (
  `id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `user_info1` (
  `id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `user_info2` (
  `id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE DATABASE `user1` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE TABLE `user_info0` (
  `id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `user_info1` (
  `id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `user_info2` (
  `id` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
