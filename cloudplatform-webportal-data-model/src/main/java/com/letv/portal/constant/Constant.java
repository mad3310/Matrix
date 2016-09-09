package com.letv.portal.constant;

public class Constant {

	public static final String KAPTCHA_COOKIE_NAME = "captcha_cache_id_";
	public static int RESULT_SUCCESS = 1;
	public static int RESULT_ERROR = 0;

	public static int PAGE_SIZE = 10;

	public static String ZK_PORT = "2181";

	// container node节点类型
	public static String MCLUSTER_NODE_TYPE_VIP_SUFFIX = "_vip";
	public static String MCLUSTER_NODE_TYPE_DATA_SUFFIX = "_data";

	// db审批状态
	public static String DB_AUDIT_STATUS_FALSE = "-1";

	public static String IS_DELETE_FALSE = "0";
	public static String IS_DELETE_TRUE = "1";

	public static Integer STATUS_DEFAULT = 0;
	public static Integer STATUS_OK = 1;
	public static Integer STATUS_BUILDDING = 2;
	public static Integer STATUS_BUILD_FAIL = 3;
	public static Integer STATUS_AUDIT_FAIL = 4;

	public static String DB_AUDIT_STATUS_TRUE_BUILD_NEW_MCLUSTER = "1";
	public static String DB_AUDIT_STATUS_TRUE_BUILD_OLD_MCLUSTER = "2";

	public static int MCLUSTER_CONTAINERS_COUNT = 4;

	public static Integer IPRESOURCE_STATUS_USERD = 1;

	public static String PYTHON_API_RESPONSE_SUCCESS = "200";
	public static String REQUEST_ERROR = "400";
	//根据业务判断是否异常
	public static String PYTHON_API_RESPONSE_JUDGE  = "417";

	public static String CREATE_BUCKET_API_RESPONSE_SUCCESS = "202";

	public static String CREATE_REBALANCE_STATUS_RESPONSE_SUCCESS = "none";

	public static String PYTHON_API_RESULT_SUCCESS = "000000";

	public static String PYTHON_API_CHECK_CONTAINER_RUNNING = "<running>";

	public static String DB_USER_TYPE_MANAGER = "manager";

	public static String MCLUSTER_INIT_STATUS_RUNNING = "running";

	public static final String  MONITOR_TOPBY_12H_PREFIX = "12h";
	public static final String  MONITOR_TOPBY_24H_PREFIX = "24h";
	public static final String  MONITOR_TOPBY_3D_PREFIX = "3d";
	public static final String  MONITOR_TOPBY_1W_PREFIX = "1w";
    public static final long  MONITOR_TOPBY_12H = 43200000; //12*60*60*1000
    public static final long  MONITOR_TOPBY_24H = 86400000;
    public static final long  MONITOR_TOPBY_3D = 259200000;
    public static final long  MONITOR_TOPBY_1W = 604800000;
	public static final int  MONITOR_TOP_MAX = 10;

	public static final String ES_RDS_MONITOR_INDEX = "matrix_rds_monitor_";

}
