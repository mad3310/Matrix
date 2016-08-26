<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">

<head>
	<meta charset="utf-8"/>
	<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1"/>
	<meta name="viewpoint" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no"/>
	<!-- bootstrap css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css"/>
	<!-- ui-css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css"/>
	<title>CPU使用率</title>
</head>

<body>
	<!-- 全局参数 start -->
	<input class="hidden" value="${gceId}" name="dbId" id="dbId" type="text" />
	<!-- 全局参数 end -->
	<div class="se-heading  m-pr10">
		<div class="pull-left">
			<h5 class="">cpu使用率</h5>
		</div>
		<div class="pull-right">
			<input class="hidden" value="1" name="strategy" id="strategy" type="text" />
			<span class="monitor-date-block">
				<ul class="nav nav-pills">
					<li class="time-block active" value="1">
						<a>1小时</a>
					</li>
					<li class="time-block" value="2">
						<a>3小时</a>
					</li>
					<li class="time-block" value="3">
						<a>1天</a>
					</li>
					<li class="time-block" value="4">
						<a>7天</a>
					</li>
					<li class="time-block" value="5">
						<a>1个月</a>
					</li>
				</ul>
			</span>
		</div>
	</div>
	<div class="monitor-charts">
		<div class="chart-content">
			<div id="chart-container" class="chart-container chart-container chart-container-xs">							
			</div>
		</div>
		<div class="footer" style="width:100%;height:200px;">						
		</div>
	</div>
	<!-- main-content-center-end -->			
</body>
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">

// Set configuration
seajs.config({
	base: "${ctx}/static/modules/",
	alias: {
		"jquery": "jquery/2.0.3/jquery.min.js",
		"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js",
		"highcharts": "highcharts/highcharts.src.js"
	}
});

seajs.use("${ctx}/static/page-js/elasticcalc/gce/monitor/cpu/main");

</script>
</html>