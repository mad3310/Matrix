ALTER TABLE `WEBPORTAL_TEMPLATE_TASK_DETAIL` ADD COLUMN ASYNC_RETRY TINYINT(4) COMMENT "异步重试";

CREATE TABLE `WEBPORTAL_TASK_ASYNC_EXECUTE` (
  `ID` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '异步推送信息表主键',
  `TASK_CHAIN_ID` BIGINT(20) UNSIGNED DEFAULT NULL COMMENT '工作流执行id',
  `CLUSTER_NAME` VARCHAR(200) DEFAULT NULL COMMENT '集群名称',
  `COUNT` INT(4) DEFAULT '0' COMMENT '执行次数',
  `DESCN` VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` TINYINT(4) DEFAULT NULL,
  `CREATE_TIME` DATETIME DEFAULT NULL,
  `UPDATE_TIME` DATETIME DEFAULT NULL,
  `CREATE_USER` BIGINT(20) UNSIGNED DEFAULT NULL,
  `UPDATE_USER` BIGINT(20) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='异步推送信息表';

INSERT INTO `WEBPORTAL_TEMPLATE_TASK_DETAIL`(`NAME`,`DESCN`,`BEANNAME`,`TASK_TYPE`,`PARAMS`,`RETRY`,`ASYNC_RETRY`,`VERSION`,`DELETED`,`CREATE_TIME`,`UPDATE_TIME`,`CREATE_USER`,`UPDATE_USER`) VALUES
('taskEcGceClusterDeleteService','删除GCE：删除GCE集群','taskEcGceClusterDeleteService','GCE',NULL,1,NULL,0,0,'2016-09-09 11:46:45',NULL,NULL,NULL),
('taskEcGceClusterDeleteCheckStatusService','删除GCE：检查集群删除状态','taskEcGceClusterDeleteCheckStatusService','GCE',NULL,1,NULL,0,0,'2016-09-09 11:47:02',NULL,NULL,NULL),
('taskEcGceClusterDelFixedPushService','删除GCE：同步固资系统','taskEcGceClusterDelFixedPushService','GCE',NULL,1,NULL,0,0,'2016-09-09 11:47:54',NULL,NULL,NULL),
('taskDelZabbixPushService','RDS集群删除-zabbix删除','taskDelZabbixPushService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:35:17',NULL,NULL,NULL),
('taskDelFixedService','RDS集群删除-固资删除','taskDelFixedService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:36:10',NULL,NULL,NULL),
('taskDelMclusterVipService','RDS集群删除-vip节点删除','taskDelMclusterVipService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:40:27',NULL,NULL,NULL),
('taskDelMclusterVipCheckStatusService','RDS集群删除-vip节点删除后检查','taskDelMclusterVipCheckStatusService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:41:07',NULL,NULL,NULL),
('taskDelMclusterService','RDS集群删除-数据节点删除','taskDelMclusterService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:41:40',NULL,NULL,NULL),
('taskDelMclusterCheckStatusService','RDS集群删除-数据节点删除后检查','taskDelMclusterCheckStatusService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:42:18',NULL,NULL,NULL),
('taskDelDbDataService','RDS集群删除-数据库记录删除','taskDelDbDataService','RDS',NULL,1,NULL,0,0,'2016-09-11 19:42:50',NULL,NULL,NULL),
('taskDelZkService','RDS集群删除-集群zk信息删除','taskDelZkService','RDS',NULL,1,0,0,0,'2016-09-18 10:57:58',NULL,NULL,NULL);

