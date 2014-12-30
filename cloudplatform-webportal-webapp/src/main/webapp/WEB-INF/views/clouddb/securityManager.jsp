<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1" />
<meta name="viewpoint" content="width=device-width,initial-scale=1" />
<!-- bootstrap css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css" />
<!-- ui-css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css" />
<title>RDS安全控制</title>
</head>
<body>
	<!-- 全局参数 start -->
	<input class="hidden" value="${dbId}" name="dbId" id="dbId" type="text" />
	<!-- 全局参数 end -->
	<div class="panel-group pd10" role="tablist" aria-multiselectable="true">
		<div class="se-heading" id="headingOne">
			<div class="pull-left">
				<h5>安全控制</h5>
			</div>
			<div class="pull-right">
				<button id="refresh" class="btn btn-default">
					<span class="glyphicon glyphicon-refresh"></span> 刷新
				</button>
			</div>
		</div>
		<ul class="nav nav-tabs" role="tablist" id="setab">
			<li id="whitelist-tab" role="presentation" class="active"><a data-toggle="tab" href="#whitelist">白名单设置</a></li>
			<li id="sqlInject-tab" role="presentation"><a data-toggle="tab" href="#sqlInject">SQL注入警告</a></li>
		</ul>
		<!-- <div class="panel-body pd0" id="whitelist"> -->
		<div class="tab-content">
			<div id="whitelist" role="tabpanel" class="tab-pane fade active in" aria-labelledby="whitelist-tab">
				<div id="ipList">
					<table class="table table-hover">
						<thead>
							<tr>
								<th colspan="4">允许访问IP名单</th>
							</tr>
						</thead>
						<tbody id="ipList-tby">

						</tbody>
					</table>
					<div class="" style="margin-bottom: 40px">
						<button id="modifyIpList" class="btn btn-primary">手动修改</button>
					</div>
				</div>
				<form id="ipForm" class="form-horizontal ng-pristine ng-valid ng-scope hide" role="form" novalidate="" name="security_form" security-list-man="">
					<div class="form-group">
						<label class="col-sm-2">允许访问IP名单：</label>
					</div>
					<div ng-hide="!loadingState" aliyun-loading="" size="48" style="margin-top: 10px" class="ng-hide"></div>
					<div class="form-group">
						<div class="col-sm-4">
							<textarea id="iplist-textarea" name="iplist-textarea" class="form-control" rows="4"></textarea>
						</div>
						<div class="help-block ng-hide" style="padding-top: 30px"></div>
					</div>
					<div class="form-group">
						<div class="col-sm-10">
							<span class="help-block">请以逗号隔开，不可重复，最多100个。</span> <span class="help-block">支持格式如：10.23.12.24,10.23.34.%</span>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-4">
							<input name="submitIpForm" type="button" value="确定" class="btn btn-primary ipFromBotton" /> <input name="cancleIpForm" type="button" value="取消" class="btn btn-default ipFromBotton" />
						</div>
					</div>
				</form>
			</div>
			<div id="sqlInject" role="tabpanel" class="tab-pane fade" aria-labelledby="sqlInject-tab">
				<div class="time-range-unit-header">
					<span class="time-range-title">选择时间范围：</span>
					<div class="date-unit">
						<input type="date" class="form-control date-picker-unit" value="2014/12/15">
					</div>
					<span class="date-step-span">至</span>
					<div class="date-unit">
						<input type="date" class="form-control date-picker-unit" value="2014/12/15">
					</div>
					<button class="btn btn-primary btn-search">查询</button>
				</div>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>时间</th>
							<th>执行帐号</th>
							<th>sql语句</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>
</body>
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">
	// Set configuration
	seajs.config({
		base : "${ctx}/static/modules/",
		alias : {
			"jquery" : "jquery/2.0.3/jquery.min.js",
			"bootstrap" : "bootstrap/bootstrap/3.3.0/bootstrap.js"
		}
	});

	seajs.use("${ctx}/static/page-js/securityManager/main");
</script>
</html>