
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1" />
<meta name="viewport" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no"/>
<!-- bootstrap css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css" />
<!-- fontawesome css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css" />
<!-- ui-css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css" />
<title>GCE管理控制台</title>
</head>
<body>
	<!-- 全局参数 start -->
	<input class="hidden" value="${ecGce.id}" name="gceId" id="gceId" type="text" />
	<!-- 全局参数 end -->
	<%@ include file="../../../layouts/header.jsp"%>

	<!-- main-content begin-->
	<div class="container-fluid">
		<div class="row main-header">
			<!-- main-content-header begin -->
			<div class="col-xs-12 col-sm-6 col-md-6">
				<div class="pull-left">
					<h3>
						<span class="fa  fa-cubes"></span>
						<span id="gceName"></span>
						<span style="display: inline-block;vertical-align:super;">
							<small id="gceStatus" class="text-success text-xs"></small>
						</span>
							<a class="btn btn-default btn-xs" href="${ctx}/list/ecgce">
						<span class="glyphicon glyphicon-step-backward"></span>
							返回实例列表
						</a>
					</h3>
				</div>
			</div>
			<div class="col-sm-6 col-md-6 hidden-xs" style="margin-top: 35px;">
				<div class="pull-right">
					<a href="/helpCenter/helpCenter.jsp?container=product-GCEIntro"  target="_black"><span class="glyphicon glyphicon-paperclip "></span><span>GCE帮助中心</span></a>
				</div>
			</div>
		</div>
		<!-- main-content-header end-->

		<div class="row">
			<!-- main-content-center-begin -->
			<nav id="sidebar" class="col-sm-2 col-md-2 nav-sidebar-div">
				<div class="sidebar sidebar-line sidebar-selector">
					<ul class="nav nav-sidebar li-underline">
						<li class="active"><a class="text-sm" src="${ctx}/detail/ecgceBaseInfo/${ecGce.id}" href="javascript:void(0)">应用详情</a></li>
						<li><a class="text-sm" src="${ctx}/detail/versionManger/${ecGce.id}" href="javascript:void(0)">版本管理</a></li>
						<%--<li><a class="text-sm" id = "logUrl" src="" href="javascript:void(0)">服务日志<p class="pull-right home-orange" id="logUrlInst"></p></a></li>--%>
						 <li><a  class="text-sm" href="javascript:void(0)"><span class="glyphicon glyphicon glyphicon-chevron-right"></span>系统资源监控</a>
	                        <ul class="nav hide">
	                            <li><a  class="text-sm" src="${ctx}/monitor/ecgce/cpu/${ecGce.id}" href="javascript:void(0)">cpu使用率</a></li>
	                            <li><a  class="text-sm" src="${ctx}/monitor/ecgce/disk/${ecGce.id}" href="javascript:void(0)">磁盘</a></li>
	                            <li><a  class="text-sm" src="${ctx}/monitor/ecgce/memory/${ecGce.id}" href="javascript:void(0)">内存</a></li>
	                            <li><a  class="text-sm" src="${ctx}/monitor/ecgce/network/${ecGce.id}" href="javascript:void(0)">网络</a></li>
	                        </ul>
	                    </li>
					</ul>
		</div>
			</nav>
			<div class="embed-responsive embed-responsive-16by9 col-sm-10 col-xm-12" id="frame-content-div">
				<iframe class="embed-responsive-item" id="frame-content" src="${ctx}/detail/ecgceBaseInfo/${ecGce.id}" frameBorder=0 scrolling="no"></iframe>
			</div>
		</div>
	</div>
	<%@ include file="../../../layouts/rToolbar.jsp"%>
</body>	
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">
// Set configuration
seajs.config({
base: "${ctx}/static/modules/",
alias: {
    "jquery": "jquery/2.0.3/jquery.min.js",
    "bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js"
}
});
seajs.use("${ctx}/static/page-js/elasticcalc/gce/layout/main");
</script>

</html>