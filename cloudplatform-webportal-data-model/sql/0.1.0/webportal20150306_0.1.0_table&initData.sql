DROP TABLE WEBPORTAL_BUILD;
CREATE TABLE WEBPORTAL_BUILD ( ID bigint NOT NULL AUTO_INCREMENT, MCLUSTER_ID bigint unsigned, DB_ID bigint unsigned, STATUS tinyint, CODE varchar(50) COLLATE utf8_unicode_ci, MSG varchar(200) COLLATE utf8_unicode_ci, START_TIME varchar(19) COLLATE utf8_unicode_ci, STEP_MSG varchar(100), STEP int(3), END_TIME varchar(19), DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint, UPDATE_USER bigint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_CONTAINER;
CREATE TABLE WEBPORTAL_CONTAINER ( ID bigint unsigned NOT NULL AUTO_INCREMENT, CONTAINER_NAME varchar(50) COLLATE utf8_unicode_ci, MOUNT_DIR varchar(500), ZOOKEEPER_ID varchar(3) COLLATE utf8_unicode_ci, IP_ADDR varchar(15), GATE_ADDR varchar(15), IP_MASK varchar(15), type varchar(15), DISK_SIZE int, CORES_NUMBER int, CPU_SPEED int, MEMORY_SIZE int, HOST_ID bigint unsigned, HOST_IP varchar(15), MCLUSTER_ID bigint unsigned, STATUS tinyint, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, ZABBIXHOSTS varchar(20), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_DB;
CREATE TABLE WEBPORTAL_DB ( ID bigint unsigned NOT NULL AUTO_INCREMENT, MCLUSTER_ID bigint unsigned, HCLUSTER_ID bigint unsigned, DB_NAME varchar(20) COLLATE utf8_unicode_ci, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, STATUS tinyint, BACKUP_CYCLE varchar(36) COLLATE utf8_unicode_ci COMMENT '?????????:??????????��?,????????????????', NOTICE_TYPE tinyint, DESCN varchar(200) COLLATE utf8_unicode_ci, ENGINE_TYPE tinyint, FROM_DB_IP varchar(36) COLLATE utf8_unicode_ci COMMENT '????????IP', FROM_DB_PORT varchar(10) COLLATE utf8_unicode_ci COMMENT '????????PORT', FROM_DB_NAME varchar(100) COLLATE utf8_unicode_ci COMMENT '????????NAME', LINK_TYPE tinyint, AUDIT_INFO varchar(200) COLLATE utf8_unicode_ci, AUDIT_TIME datetime, AUDIT_USER bigint unsigned, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_DB_APPLY_STANDARD;
CREATE TABLE WEBPORTAL_DB_APPLY_STANDARD ( ID bigint NOT NULL AUTO_INCREMENT, APPLY_CODE varchar(100) COLLATE utf8_unicode_ci, APPLY_NAME varchar(100) COLLATE utf8_unicode_ci, VERSION varchar(30) COLLATE utf8_unicode_ci COMMENT '??????', BACKUP_CYCLE varchar(36) COLLATE utf8_unicode_ci COMMENT '?????????:??????????��?,????????????????', IS_EMAIL_NOTICE varchar(10) COLLATE utf8_unicode_ci, DESCN varchar(100) COLLATE utf8_unicode_ci, ENGINE_TYPE varchar(50) COLLATE utf8_unicode_ci, FROM_DB_IP varchar(36) COLLATE utf8_unicode_ci COMMENT '????????IP', FROM_DB_PORT varchar(10) COLLATE utf8_unicode_ci COMMENT '????????PORT', FROM_DB_NAME varchar(100) COLLATE utf8_unicode_ci COMMENT '????????NAME', DEVELOP_LANGUAGE varchar(50) COLLATE utf8_unicode_ci, LINK_TYPE varchar(50) COLLATE utf8_unicode_ci, BELONG_DB varchar(36) COLLATE utf8_unicode_ci, AUDIT_TIME varchar(19) COLLATE utf8_unicode_ci, AUDIT_USER varchar(36) COLLATE utf8_unicode_ci, STATUS varchar(2) COLLATE utf8_unicode_ci, AUDIT_INFO varchar(1024) COLLATE utf8_unicode_ci, CLUSTER_ID varchar(36) COLLATE utf8_unicode_ci, IS_DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, MAX_USER_CONNECTIONS int(10), MAX_CONNECTIONS_PER_HOUR int(10), MAX_UPDATES_PER_HOUR int(10), MAX_QUERIES_PER_HOUR int(10), READ_WRITER_RATE varchar(10) COLLATE utf8_unicode_ci, DATA_LIMIT_IP_LIST varchar(1024) COLLATE utf8_unicode_ci, MAX_CONCURRENCY int(10), MGR_LIMIT_IP_LIST varchar(1024) COLLATE utf8_unicode_ci, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_DB_USER;
CREATE TABLE WEBPORTAL_DB_USER ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DB_ID bigint unsigned, USERNAME varchar(20) COLLATE utf8_unicode_ci, PASSWORD varchar(20) COLLATE utf8_unicode_ci, TYPE tinyint, ACCEPT_IP varchar(15) COLLATE utf8_unicode_ci COMMENT '????IP', MAX_CONCURRENCY int COMMENT '????��???????', READ_WRITER_RATE varchar(10) COLLATE utf8_unicode_ci COMMENT '?????????', MAX_QUERIES_PER_HOUR int COMMENT '???????????��??��?????(????��????,?????????????,???��?????��???????)', MAX_UPDATES_PER_HOUR int COMMENT '???????????��??��?????(????��????,?????????????,???��?????��???????)', MAX_CONNECTIONS_PER_HOUR int COMMENT '???????????��???????(????��????,?????????????,???��?????��???????)', MAX_USER_CONNECTIONS int COMMENT '????��????��???????(????��????,?????????????,???��?????��???????)', STATUS tinyint, SALT varchar(20) COLLATE utf8_unicode_ci, CREATE_TIME datetime, DELETED tinyint, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, DESCN varchar(200), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_HCLUSTER;
CREATE TABLE WEBPORTAL_HCLUSTER ( ID bigint unsigned NOT NULL AUTO_INCREMENT, HCLUSTER_NAME varchar(20) COLLATE utf8_unicode_ci, STATUS tinyint, DELETED tinyint, UPDATE_USER bigint unsigned, UPDATE_TIME datetime, CREATE_USER bigint unsigned, CREATE_TIME datetime, DESCN varchar(200), HCLUSTER_NAME_ALIAS varchar(30), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_HCLUSTER (ID, HCLUSTER_NAME, STATUS, DELETED, UPDATE_USER, UPDATE_TIME, CREATE_USER, CREATE_TIME, DESCN, HCLUSTER_NAME_ALIAS) values (1, 'TEST_15X', 1, 0, null, null, null, '2014-11-28 14:18:34', null, 'TEST_15X');
DROP TABLE WEBPORTAL_HOST;
CREATE TABLE WEBPORTAL_HOST ( ID bigint unsigned NOT NULL AUTO_INCREMENT, HOST_NAME varchar(20) COLLATE utf8_unicode_ci, HOST_IP varchar(15), NODES_NUMBER int, CPU_MODEL varchar(50), CORES_NUMBER int, MEMORY_SIZE int, DISK_SIZE int, DISK_USED int, HOST_MODEL varchar(50), TYPE varchar(10), HCLUSTER_ID bigint unsigned, STATUS tinyint, DELETED tinyint, UPDATE_USER bigint unsigned, UPDATE_TIME datetime, CREATE_USER bigint unsigned, CREATE_TIME datetime, DESCN varchar(200), NAME varchar(30), PASSWORD varchar(30), HOST_NAME_ALIAS varchar(30), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_HOST (ID, HOST_NAME, HOST_IP, NODES_NUMBER, CPU_MODEL, CORES_NUMBER, MEMORY_SIZE, DISK_SIZE, DISK_USED, HOST_MODEL, TYPE, HCLUSTER_ID, STATUS, DELETED, UPDATE_USER, UPDATE_TIME, CREATE_USER, CREATE_TIME, DESCN, NAME, PASSWORD, HOST_NAME_ALIAS) values (1, 'test_host_15X', '10.154.156.150', null, null, null, null, null, null, null, '0', 1, null, 1, null, '2014-11-28 14:21:07', null, '2014-11-28 14:21:07', '??????', 'root', 'root', 'test_host_15X');
insert into WEBPORTAL_HOST (ID, HOST_NAME, HOST_IP, NODES_NUMBER, CPU_MODEL, CORES_NUMBER, MEMORY_SIZE, DISK_SIZE, DISK_USED, HOST_MODEL, TYPE, HCLUSTER_ID, STATUS, DELETED, UPDATE_USER, UPDATE_TIME, CREATE_USER, CREATE_TIME, DESCN, NAME, PASSWORD, HOST_NAME_ALIAS) values (2, 'test_host_151', '10.154.156.151', null, null, null, null, null, null, null, '1', 1, null, 1, null, '2014-11-28 14:22:16', null, '2014-11-28 14:22:16', '', 'root', 'root', 'test_host_15X');
insert into WEBPORTAL_HOST (ID, HOST_NAME, HOST_IP, NODES_NUMBER, CPU_MODEL, CORES_NUMBER, MEMORY_SIZE, DISK_SIZE, DISK_USED, HOST_MODEL, TYPE, HCLUSTER_ID, STATUS, DELETED, UPDATE_USER, UPDATE_TIME, CREATE_USER, CREATE_TIME, DESCN, NAME, PASSWORD, HOST_NAME_ALIAS) values (3, 'test_host_152', '10.154.156.152', null, null, null, null, null, null, null, '1', 1, null, 1, null, '2014-11-28 14:22:54', null, '2014-11-28 14:22:54', '', 'root', 'root', 'test_host_15X');
DROP TABLE WEBPORTAL_INDEX_MONITOR;
CREATE TABLE WEBPORTAL_INDEX_MONITOR ( ID bigint unsigned NOT NULL AUTO_INCREMENT, TITLE_TEXT varchar(50), SUB_TITLE_TEXT varchar(50), Y_AXIS_TEXT varchar(50), Y_AXIS_TEXT_1 varchar(50), Y_AXIS_TEXT_2 varchar(50), Y_AXIS_TEXT_3 varchar(50), TOOLTIP_SUFFIX varchar(50), FLUSH_TIME int, DETAIL_TABLE varchar(50), STATUS tinyint, DATA_FROM_API varchar(50) NOT NULL, MONITOR_POINT varchar(100), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (16, 'db.innodb_buffer.memallco', null, '??', null, null, null, 'B', null, 'WEBPORTAL_MONITOR_DB_INNODB_BUFFER_MEMALLCO', 1, '/db/all/stat/innobuffer/memallco', 'total_mem_alloc,add_pool_alloc');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (17, 'db.innodb_buffer.page', null, '???', null, null, null, '', null, 'WEBPORTAL_MONITOR_DB_INNODB_BUFFER_PAGE', 1, '/db/all/stat/innobuffer/page', 'pages_modified,pages_total');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (18, 'db.innodb_buffer.pool', null, '???', null, null, null, '', null, 'WEBPORTAL_MONITOR_DB_INNODB_BUFFER_POOL', 1, '/db/all/stat/innobuffer/pool', 'buf_pool_hit_rate,buf_free,buf_pool_size');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (19, 'db.variable_status.ps', null, '?????', null, null, null, 'n/s', null, 'WEBPORTAL_MONITOR_DB_VARIABLESTATUS_PS', 1, '/db/all/stat/variablestatus/ps', 'Commit_PS,QPS,Opens_PS,Threads_PS');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (20, 'db.variable_status.ration', null, '??', null, null, null, '', null, 'WEBPORTAL_MONITOR_DB_VARIABLESTATUS_RATION', 1, '/db/all/stat/variablestatus/ration', 'R_W_Ratio,Write_Commit,Rollback_Commit');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (21, 'db.variable_status.used', null, '???', null, null, null, '', null, 'WEBPORTAL_MONITOR_DB_VARIABLESTATUS_USED', 1, '/db/all/stat/variablestatus/used', 'Table_Cache_Used,Thread_Cache_Used,CXN_Used_Now,CXN_Used_Ever');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (22, 'db.row_opers.ps', null, '???', null, null, null, '/s', null, 'WEBPORTAL_MONITOR_DB_ROWOPERS_PS', 1, '/db/all/stat/rowsoper/ps', 'num_updates_sec,num_deletes_sec,num_reads_sec,num_inserts_sec');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (23, 'db.row_opers.total', null, '???', null, null, null, '', null, 'WEBPORTAL_MONITOR_DB_ROWOPERS_TOTAL', 1, '/db/all/stat/rowsoper/total', 'num_inserts,num_deletes,num_updates,num_reads');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (24, 'db.wsrep_status.flow_control_paused', null, '??', null, null, null, '', null, 'WEBPORTAL_MONITOR_WSREPSTATUS_FLOWCONTROL_PAUSED', 1, '/db/all/stat/wsrepstatus/flow_control_paused', 'wsrep_flow_control_paused');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (25, 'db.wsrep_status.slowest_node_param', null, '??', null, null, null, '', null, 'WEBPORTAL_MONITOR_WSREPSTATUS_SLOWESTNODE_PARAM', 1, '/db/all/stat/wsrepstatus/slowest_node_param', 'wsrep_flow_control_sent,wsrep_local_recv_queue_avg');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (26, 'db.wsrep_status.slowest_network_param', null, '??', null, null, null, '', null, 'WEBPORTAL_MONITOR_WSREPSTATUS_SLOWESTNETWORK_PARAM', 1, '/db/all/stat/wsrepstatus/slowest_network_param', 'wsrep_local_send_queue_avg');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (27, 'node.memory.size', null, '????', null, null, null, 'GB', null, 'WEBPORTAL_MONITOR_NODE_MEMORYSIZE', 1, '/node/stat/memory/size', 'node_mem_free_size,node_mem_used_size');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (28, 'node.datadir.size', null, '??????', null, null, null, '%', null, 'WEBPORTAL_MONITOR_NODE_DATADIRSIZE', 1, '/node/stat/datadir/size', '/,/srv/mcluster');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (29, 'node.mysql_cpu.partion', null, 'cpu??', null, null, null, '%', null, 'WEBPORTAL_MONITOR_MYSQLCPUPARTION', 1, '/node/stat/mysqlcpu/partion', 'mysql_cpu_partion');
insert into WEBPORTAL_INDEX_MONITOR (ID, TITLE_TEXT, SUB_TITLE_TEXT, Y_AXIS_TEXT, Y_AXIS_TEXT_1, Y_AXIS_TEXT_2, Y_AXIS_TEXT_3, TOOLTIP_SUFFIX, FLUSH_TIME, DETAIL_TABLE, STATUS, DATA_FROM_API, MONITOR_POINT) values (30, 'node.mysql_memory.partion', null, '????', null, null, null, '%', null, 'WEBPORTAL_MONITOR_MYSQLMEMORYPARTION', 1, '/node/stat/mysqlmemory/partion', 'mysql_mem_partion');
DROP TABLE WEBPORTAL_IP_RESOURCE;
CREATE TABLE WEBPORTAL_IP_RESOURCE ( ID bigint unsigned NOT NULL AUTO_INCREMENT, IP varchar(15) COLLATE utf8_unicode_ci, GATE_WAY varchar(15) COLLATE utf8_unicode_ci, MASK varchar(15) COLLATE utf8_unicode_ci, STATUS tinyint, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MCLUSTER_INFO;
CREATE TABLE WEBPORTAL_MCLUSTER_INFO ( ID bigint unsigned NOT NULL AUTO_INCREMENT, MCLUSTER_NAME varchar(200), STATUS tinyint, SST_PASSWORD varchar(20), HCLUSTER_ID bigint unsigned, DELETED tinyint, CREATE_TIME datetime COMMENT '????��?????', CREATE_USER bigint unsigned, UPDATE_TIME datetime, UPDATE_USER bigint unsigned, ADMIN_USER varchar(200), ADMIN_PASSWORD varchar(200), type int(10), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_INNODB_BUFFER_MEMALLCO;
CREATE TABLE WEBPORTAL_MONITOR_DB_INNODB_BUFFER_MEMALLCO ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_INNODB_BUFFER_PAGE;
CREATE TABLE WEBPORTAL_MONITOR_DB_INNODB_BUFFER_PAGE ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_INNODB_BUFFER_POOL;
CREATE TABLE WEBPORTAL_MONITOR_DB_INNODB_BUFFER_POOL ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_ROWOPERS_PS;
CREATE TABLE WEBPORTAL_MONITOR_DB_ROWOPERS_PS ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_ROWOPERS_TOTAL;
CREATE TABLE WEBPORTAL_MONITOR_DB_ROWOPERS_TOTAL ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_VARIABLESTATUS_PS;
CREATE TABLE WEBPORTAL_MONITOR_DB_VARIABLESTATUS_PS ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_VARIABLESTATUS_RATION;
CREATE TABLE WEBPORTAL_MONITOR_DB_VARIABLESTATUS_RATION ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_DB_VARIABLESTATUS_USED;
CREATE TABLE WEBPORTAL_MONITOR_DB_VARIABLESTATUS_USED ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_MYSQLCPUPARTION;
CREATE TABLE WEBPORTAL_MONITOR_MYSQLCPUPARTION ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_MYSQLMEMORYPARTION;
CREATE TABLE WEBPORTAL_MONITOR_MYSQLMEMORYPARTION ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_NODE_DATADIRSIZE;
CREATE TABLE WEBPORTAL_MONITOR_NODE_DATADIRSIZE ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_NODE_MEMORYSIZE;
CREATE TABLE WEBPORTAL_MONITOR_NODE_MEMORYSIZE ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_WSREPSTATUS_FLOWCONTROL_PAUSED;
CREATE TABLE WEBPORTAL_MONITOR_WSREPSTATUS_FLOWCONTROL_PAUSED ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_WSREPSTATUS_SLOWESTNETWORK_PARAM;
CREATE TABLE WEBPORTAL_MONITOR_WSREPSTATUS_SLOWESTNETWORK_PARAM ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_MONITOR_WSREPSTATUS_SLOWESTNODE_PARAM;
CREATE TABLE WEBPORTAL_MONITOR_WSREPSTATUS_SLOWESTNODE_PARAM ( ID bigint unsigned NOT NULL AUTO_INCREMENT, DETAIL_NAME varchar(50), DETAIL_VALUE float(30,3), MONITOR_DATE datetime, IP varchar(16), PRIMARY KEY (ID), INDEX NAME_IP_DATE (DETAIL_NAME(10), IP(13), MONITOR_DATE) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_USER;
CREATE TABLE WEBPORTAL_USER ( ID bigint NOT NULL AUTO_INCREMENT, USERNAME varchar(30), EMAIL varchar(45), PASSWORD varchar(50), PASSPORT_ID varchar(36), LAST_LOGIN_TIME datetime, LASTLOGIN_IP varchar(20), CURRENT_LOGIN_TIME datetime, CURRENT_LOGIN_IP varchar(20), REGISTER_DATE datetime, IS_ADMIN tinyint, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, UPDATE_USER bigint unsigned, CREATE_USER bigint unsigned, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (1, 'sysadmin', 'sysadmin@letv.com', null, null, null, null, '2014-12-29 18:17:16', '0:0:0:0:0:0:0:1', '2014-11-28 14:24:57', null, null, '2014-11-28 14:24:57', '2014-11-28 14:24:57', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (2, 'yaokuo', 'yaokuo@letv.com', null, null, null, null, '2015-01-05 10:02:10', '10.154.238.73', '2014-11-28 14:34:38', null, null, '2014-11-28 14:34:38', '2014-11-28 14:34:38', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (3, 'yaofaliang', 'yaofaliang@letv.com', null, null, null, null, '2015-01-04 17:56:39', '10.154.238.73', '2014-11-28 14:43:28', null, null, '2014-11-28 14:43:28', '2014-11-28 14:43:28', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (4, 'liuhao1', 'liuhao1@letv.com', null, null, null, null, '2015-01-04 10:53:06', '10.154.238.73', '2014-11-28 14:49:01', null, null, '2014-11-28 14:49:01', '2014-11-28 14:49:01', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (5, 'zhangxiang', 'zhangxiang@letv.com', null, null, null, null, '2014-12-29 20:07:09', '10.154.238.73', '2014-11-28 14:50:12', null, null, '2014-11-28 14:50:12', '2014-11-28 14:50:12', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (6, 'wujun', 'wujun@letv.com', null, null, null, null, '2014-11-28 17:37:24', '10.58.164.26', '2014-11-28 16:40:56', null, null, '2014-11-28 16:40:56', '2014-11-28 16:40:56', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (7, 'zhoubingzheng', 'zhoubingzheng@letv.com', null, null, null, null, '2015-01-06 09:46:19', '10.154.238.73', '2014-12-03 10:03:36', null, null, '2014-12-03 10:03:36', '2014-12-03 10:03:36', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (9, 'jinglinlin', 'jinglinlin@letv.com', null, null, null, null, '2014-12-22 11:04:35', '127.0.0.1', '2014-12-08 11:57:28', null, null, '2014-12-08 11:57:28', '2014-12-08 11:57:28', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (10, 'zhangzeng', 'zhangzeng@letv.com', null, null, null, null, '2015-01-06 09:51:54', '10.154.238.73', '2014-12-09 11:35:55', null, null, '2014-12-09 11:35:55', '2014-12-09 11:35:55', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (11, 'gaomin', 'gaomin@letv.com', null, null, null, null, '2014-12-29 15:20:30', '0:0:0:0:0:0:0:1', '2014-12-24 09:04:19', null, null, '2014-12-24 09:04:20', '2014-12-24 09:04:20', 0, 0);
insert into WEBPORTAL_USER (ID, USERNAME, EMAIL, PASSWORD, PASSPORT_ID, LAST_LOGIN_TIME, LASTLOGIN_IP, CURRENT_LOGIN_TIME, CURRENT_LOGIN_IP, REGISTER_DATE, IS_ADMIN, DELETED, CREATE_TIME, UPDATE_TIME, UPDATE_USER, CREATE_USER) values (12, 'duanwei1', 'duanwei1@letv.com', null, null, null, null, '2015-01-05 15:24:56', '10.154.238.73', '2015-01-04 17:56:55', null, null, '2015-01-04 17:56:55', '2015-01-04 17:56:55', 0, 0);
DROP TABLE WEBPORTAL_BACKUP_RESULT;
CREATE TABLE
    WEBPORTAL_BACKUP_RESULT
    (
        ID bigint NOT NULL AUTO_INCREMENT,
        MCLUSTER_ID bigint unsigned,
        DB_ID bigint unsigned,
        BACKUP_IP VARCHAR(50) COLLATE utf8_unicode_ci,
        START_TIME DATETIME,
        END_TIME DATETIME,
        STATUS VARCHAR(10),
        RESULT_DETAIL VARCHAR(100) COLLATE utf8_unicode_ci,
        DELETED TINYINT,
        CREATE_TIME DATETIME,
        UPDATE_TIME DATETIME,
        CREATE_USER bigint,
        UPDATE_USER bigint,
        PRIMARY KEY (ID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
DROP TABLE WEBPORTAL_SLB;
CREATE TABLE WEBPORTAL_SLB ( ID bigint unsigned NOT NULL AUTO_INCREMENT, SLB_NAME varchar(20) COLLATE utf8_unicode_ci, SLBCLUSTER_ID bigint unsigned, HCLUSTER_ID bigint unsigned, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, STATUS tinyint, DESCN varchar(200) COLLATE utf8_unicode_ci, IP varchar(20), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_SLBCLUSTER;
CREATE TABLE WEBPORTAL_SLBCLUSTER ( ID bigint unsigned NOT NULL AUTO_INCREMENT, CLUSTER_NAME varchar(200), STATUS tinyint, SST_PASSWORD varchar(20), HCLUSTER_ID bigint unsigned, DELETED tinyint, CREATE_TIME datetime COMMENT '???????????', CREATE_USER bigint unsigned, UPDATE_TIME datetime, UPDATE_USER bigint unsigned, ADMIN_USER varchar(200), ADMIN_PASSWORD varchar(200), TYPE int(10), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_SLBCLUSTER (ID, CLUSTER_NAME, STATUS, SST_PASSWORD, HCLUSTER_ID, DELETED, CREATE_TIME, CREATE_USER, UPDATE_TIME, UPDATE_USER, ADMIN_USER, ADMIN_PASSWORD, TYPE) values (1, 'TestSlbCluster', null, null, null, null, null, null, null, null, null, null, null);
DROP TABLE WEBPORTAL_SLBCONTAINER;
CREATE TABLE WEBPORTAL_SLBCONTAINER ( ID bigint unsigned NOT NULL AUTO_INCREMENT, CONTAINER_NAME varchar(50) COLLATE utf8_unicode_ci, MOUNT_DIR varchar(500), ZOOKEEPER_ID varchar(3) COLLATE utf8_unicode_ci, IP_ADDR varchar(15), GATE_ADDR varchar(15), IP_MASK varchar(15), TYPE varchar(15), DISK_SIZE int, CORES_NUMBER int, CPU_SPEED int, MEMORY_SIZE int, HOST_ID bigint unsigned, HOST_IP varchar(15), MCLUSTER_ID bigint unsigned, STATUS tinyint, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, ZABBIXHOSTS varchar(20), SERVICE_TYPE tinyint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_SLB_BACKEND_SERVER;
CREATE TABLE WEBPORTAL_SLB_BACKEND_SERVER ( ID bigint unsigned NOT NULL AUTO_INCREMENT, CONTAINER_ID bigint unsigned,SLB_CONFIG_ID bigint unsigned, TYPE varchar(10), SERVER_NAME varchar(20), SERVER_IP varchar(15), DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, SLB_ID bigint NOT NULL, PORT varchar(10), PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_SLB_CONFIG;
CREATE TABLE WEBPORTAL_SLB_CONFIG ( ID bigint unsigned NOT NULL AUTO_INCREMENT, AGENT_TYPE varchar(10), FRONT_PORT varchar(15), SLB_ID bigint unsigned, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint unsigned, UPDATE_USER bigint unsigned, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='????container?????';
DROP TABLE WEBPORTAL_TASK_CHAIN;
CREATE TABLE WEBPORTAL_TASK_CHAIN ( ID bigint NOT NULL AUTO_INCREMENT, TASK_ID bigint unsigned, TASK_DETAIL_ID bigint unsigned, CHAIN_INDEX_ID bigint unsigned, EXECUTE_ORDER tinyint, START_TIME datetime, END_TIME datetime, STATUS varchar(10), PARAMS varchar(1000), RESULT varchar(1000), DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint, UPDATE_USER bigint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_TASK_CHAIN_INDEX;
CREATE TABLE WEBPORTAL_TASK_CHAIN_INDEX ( ID bigint NOT NULL AUTO_INCREMENT, TASK_ID bigint unsigned, START_TIME datetime, END_TIME datetime, STATUS varchar(10), DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint, UPDATE_USER bigint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE WEBPORTAL_TEMPLATE_TASK;
CREATE TABLE WEBPORTAL_TEMPLATE_TASK ( ID bigint NOT NULL AUTO_INCREMENT, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint, UPDATE_USER bigint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_TEMPLATE_TASK (ID, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (1, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK (ID, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (2, null, null, null, null, null);
DROP TABLE WEBPORTAL_TEMPLATE_TASK_CHAIN;
CREATE TABLE WEBPORTAL_TEMPLATE_TASK_CHAIN ( ID bigint NOT NULL AUTO_INCREMENT, TASK_ID bigint unsigned, TASK_DETAIL_ID bigint unsigned, EXECUTE_ORDER tinyint, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint, UPDATE_USER bigint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_TEMPLATE_TASK_CHAIN (ID, TASK_ID, TASK_DETAIL_ID, EXECUTE_ORDER, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (3, 1, 1, 1, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_CHAIN (ID, TASK_ID, TASK_DETAIL_ID, EXECUTE_ORDER, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (4, 1, 2, 2, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_CHAIN (ID, TASK_ID, TASK_DETAIL_ID, EXECUTE_ORDER, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (5, 1, 3, 3, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_CHAIN (ID, TASK_ID, TASK_DETAIL_ID, EXECUTE_ORDER, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (6, 1, 4, 4, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_CHAIN (ID, TASK_ID, TASK_DETAIL_ID, EXECUTE_ORDER, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (7, 1, 5, 5, null, null, null, null, null);
DROP TABLE WEBPORTAL_TEMPLATE_TASK_DETAIL;
CREATE TABLE WEBPORTAL_TEMPLATE_TASK_DETAIL ( ID bigint NOT NULL AUTO_INCREMENT, NAME varchar(200), DESCN varchar(500), BEANNAME varchar(50), PARAMS varchar(500), RETRY tinyint, VERSION tinyint, DELETED tinyint, CREATE_TIME datetime, UPDATE_TIME datetime, CREATE_USER bigint, UPDATE_USER bigint, PRIMARY KEY (ID) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (1, '????', null, 'testTask1Service', null, 5, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (2, '????', null, 'testTask2Service', null, 5, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (3, '????', null, 'testTask3Service', null, 5, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (4, '????', null, 'testTask4Service', null, 5, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (5, '????', null, 'testTask5Service', null, 5, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (6, 'taskMclusterCreateService', null, 'taskMclusterCreateService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (7, 'taskMclusterCheckStatusService', null, 'taskMclusterCheckStatusService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (8, 'taskMclusterInitZookeeperService', null, 'taskMclusterInitZookeeperService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (9, 'taskMclusterInitAdminUserAndPwdService', null, 'taskMclusterInitAdminUserAndPwdService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (10, 'taskMclusterPostInfoService', null, 'taskMclusterPostInfoService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (11, 'taskMclusterInitService', null, 'taskMclusterInitService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (12, 'taskDbCreateService', null, 'taskDbCreateService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (13, 'taskDbUserCreateService', null, 'taskDbUserCreateService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (14, 'taskContainerSyncService', null, 'taskContainerSyncService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (15, 'taskContainerPostInfoService', null, 'taskContainerPostInfoService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (16, 'taskContainerSync2Service', null, 'taskContainerSync2Service', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (17, 'taskContainerPostInfo2Service', null, 'taskContainerPostInfo2Service', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (18, 'taskMclusterStartService', null, 'taskMclusterStartService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (19, 'taskContainerCheckStatusService', null, 'taskContainerCheckStatusService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (20, 'taskMclusterStartGlb3306Service', null, 'taskMclusterStartGlb3306Service', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (21, 'taskMclusterStartGlb8888Service', null, 'taskMclusterStartGlb8888Service', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (22, 'taskFixedPushService', null, 'taskFixedPushService', null, 1, null, null, null, null, null, null);
insert into WEBPORTAL_TEMPLATE_TASK_DETAIL (ID, NAME, DESCN, BEANNAME, PARAMS, RETRY, VERSION, DELETED, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) values (23, 'taskZabbixPushService', null, 'taskZabbixPushService', null, 1, null, null, null, null, null, null);
