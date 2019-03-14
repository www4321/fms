CREATE TABLE `tb_bill` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `consume_type` varchar(20) NOT NULL DEFAULT '0' COMMENT '消费类型',
  `money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '消费金额',
  `consume_info` varchar(1024) NOT NULL DEFAULT '' COMMENT '消费信息',
  `consume_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `consume_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `year` int(10) NOT NULL DEFAULT '0' COMMENT '年份',
  `month` int(10) NOT NULL DEFAULT '0' COMMENT '月份',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账单表'

