<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1" />
<meta name="viewport" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no" />
<!-- bootstrap css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css" />
<!-- fontawesome css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css" />
<!-- ui-css -->
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css" />
<title>SLB管理控制台</title>
</head>
<body>
<style>
/*table{table-layout: fixed;}*/
td{word-break: break-all; word-wrap:break-word;}</style>
	<!-- 全局参数 start -->
	<input class="hidden" value="${gceId}" name="gceId" id="gceId" type="text" />
		<div class="panel-group pd10" id="accordion" role="tablist" aria-multiselectable="true">
			<div class="panel panel-default panel-table ">
				<div class="panel-heading bdl-list overHidden panel-heading-mcluster" role="tab" id="headingOne" >
					<span class="panel-title">
							基本信息
					</span>
					<div class="pull-right table-viewer-topbar-content">
					</div>
					<a data-toggle="collapse" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
					<span class="toggle-drop-down-icon" toggle-show="toggleShow">
						<span class="glyphicon glyphicon-chevron-down table-viewer-dropdown"></span>
					</span>
					</a>
				</div>
				<div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
				  <div class="panel-body pd0">
					<table class="table table-bordered table-bi">
						<tbody>
						<tr>
							<td width="50%"><span class="text-muted pd-r8">应用名称:</span><span id="gce_info_gce_name">webportal-img</span></td>
							<td width="50%"><span class="text-muted" style="padding-right:8px">状态:</span><span>运行中</span></td>
						</tr>
						<tr>
							<td width="50%">
								<span class="text-muted pd-r8">应用类型:</span><span text-length="26" id="gce_type"></span>
							</td>
							<td width="50%">
								<span class="text-muted pd-r8">地域:</span>北京
							</td>
						</tr>
						<tr>
							<td width="50%">
								<span class="text-muted pd-r8">访问地址:</span><span text-length="26" id="gce_server_addr"></span>
								<a href="/helpCenter/helpCenter.jsp?container=help-gceAccess"  target="_black" data-toggle="tooltip" data-placement="top" title="点击查看详细介绍">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a>
							</td>
							<td width="50%">
								<span class="text-muted pd-r8">可用区:</span><span text-length="26" id="gce_info_available_region"></span>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<span class="text-muted pd-r8">应用描述:</span><span text-length="26" id="gce_descn"></span>
							</td>
						</tr>
						</tbody>
					</table>
				  </div>
				</div>
			</div>
			<div class="panel panel-default panel-table">
				<div class="panel-heading bdl-list panel-heading-mcluster" role="tab" id="headingTwo">
					<span class="panel-title">
							付费信息
					</span>
					<a data-toggle="collapse" href="#collapseTwo"  aria-expanded="true" aria-controls="collapseTwo">
					<span class="toggle-drop-down-icon" toggle-show="toggleShow">
						<span class="glyphicon glyphicon-chevron-down table-viewer-dropdown "></span>
					</span>
					</a>
				</div>
				<div id="collapseTwo" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingTwo">
				  <div class="panel-body pd0">
					<table class="table table-bordered table-bi">
						<tbody>
						<tr>
							<td width="50%"><span class="text-muted pd-r8">付费方式:</span><span class="text-success">包年</span></td>
							<td width="50%"><span class="text-muted" style="padding-right:8px">创建时间:</span><span id="gce_create_time"></span></td>
						</tr>
						<!-- <tr>
							<td width="50%">
								<span class="text-muted pd-r8">服务地址：</span><span id="slbServerIp" text-length="26">---</span>
							</td>
							<td width="50%">
								<span class="text-muted pd-r8">自动释放时间:</span><span>无</span>
							</td>
						</tr> -->
						</tbody>
					</table>
				  </div>
				</div>
			</div>
			
			<div class="panel panel-default panel-table">
				<div class="panel-heading bdl-list panel-heading-mcluster" role="tab" id="headingThree">
					<span class="panel-title">
							关联服务
					</span>
					<a data-toggle="collapse" href="#collapseThree"  aria-expanded="true" aria-controls="collapseThree">
					<span class="toggle-drop-down-icon" toggle-show="toggleShow">
						<span class="glyphicon glyphicon-chevron-down table-viewer-dropdown "></span>
					</span>
					</a>
				</div>
				<div id="collapseThree" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingThree">
				  <div class="panel-body pd0">
					<table class="table table-bordered table-bi">
						<tbody>
						<tr>
							<td width="50%"><span class="text-muted pd-r8">RDS:</span><span id="gce_rds_name"></span></td>
							<td width="50%"><span class="text-muted" style="padding-right:8px">OCS:</span><span id="gce_ocs_name"></span></td>
						</tr>
						</tbody>
					</table>
				  </div>
				</div>
			</div>
			
			
		</div>
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

seajs.use("${ctx}/static/page-js/elasticcalc/gce/basicInfo/main");

</script>
</html>