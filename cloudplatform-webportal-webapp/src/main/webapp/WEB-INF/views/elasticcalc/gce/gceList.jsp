<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
	<meta charset="utf-8"/>
	<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1"/>
	<meta name="viewport" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no"/>
	<!-- bootstrap css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css"/>
	<!-- fontawesome css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css"/>
	<!-- ui-css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css"/>
	<title>gce管理控制台</title>
</head>
<body>
<%@ include file="../../../layouts/header.jsp"%>
<!-- main-content begin-->
<div class="container-fluid">
	<div class="row main-header overHidden"> <!-- main-content-header begin -->
		<div class="col-xs-12 col-sm-12 col-md-6">
			<div class="pull-left">
				<h5>
				<span>云引擎管理</span>
				<button class="btn btn-success btn-md btn-region-display hidden-xs">全部</button>
				<button class="btn btn-default btn-md btn-region-display hidden-xs">北京</button>
				<button class="btn btn-success btn-sm btn-region-display hidden-sm hidden-md hidden-lg">全部</button>
				<button class="btn btn-default btn-sm btn-region-display hidden-sm hidden-md hidden-lg">北京</button>
				</h5> 
			</div>
		</div>
		<div class="col-xs-12 col-sm-12 col-md-6 hidden-xs">
			<div class="pull-right">
				<h5 class="bdl-0">
				<button class="btn-default btn btn-md" id="refresh"><span class="glyphicon glyphicon-refresh"></span>刷新</button>
				<button class="btn-primary btn btn-md"  onclick="window.open('${ctx}/detail/ecgceCreate')">购买云引擎</button>
				</h5>
			</div>
		</div>
		<div>
			<div class="pull-left">
				<form class="form-inline" role="form">
					<div class="form-group col-xs-9 col-sm-10">
						<input  id="gceName" type="text" class="form-control" size="48" placeholder="请输入实例名称进行搜索">
					</div>
					<div class="col-xs-3 col-sm-2">
						<button id="search" type="button" class="btn btn-default"><span class="hidden-xs">搜索</span><span class="glyphicon glyphicon-search hidden-sm hidden-md hidden-lg"></span></button>
					</div>
				</form>
			</div>
		</div>
	</div><!-- main-content-header end-->

	<div class="row"><!-- main-content-center-begin -->
		<div class="col-xs-12 col-sm-12 col-md-12">
			<table class="table table-hover table-se">
				<thead>
					<tr>
						<th width="10">
							<input type="checkbox">
						</th>
						<th class="padding-left-32">应用名称</th>
						<th>应用描述</th>
						<th class="hidden-xs">服务类型</th>
						<th class="hidden-xs">所在可用区</th>
						<th class="hidden-xs">付费方式</th>
						<th class="hidden-xs">操作</th>
					</tr>
				</thead>
				<tbody id="tby">
				</tbody>
				<tfoot id="paginatorBlock">
					<tr class="tfoot" >
						<td colspan=" 8">
							<div class="col-xs-11 col-sm-11" style="margin:2px 0;padding:0;">
								<div class="pull-right">
									<div class="pagination-info hidden-xs">
										<span class="ng-binding">共有<span id="totalRecords"></span>条</span>， 
										<span class="ng-binding">每页显示<span id="recordsPerPage"></span>条</span>&nbsp;
									</div>
									<ul id='paginator'></ul>
								</div>
							</div>
						</td>
					</tr>
				</tfoot>
			</table>
		    <!-- <div class="help-block hidden" id="noData">没有记录</div> -->
		</div>
	</div><!-- main-content-center-end -->
</div>
<!--确认对话框-->
	<div id="dialog-box" class="modal">
		<div class="modal-dialog modal-sm modal-top">
			<div class="modal-content">
				<div class="modal-header">
			<!--	<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">关闭</span>
					</button>-->
					<h5 id="dialog-box-title" class="modal-title"></h5ashboard>
				</div>
				<div class="modal-body clearfix">
					<div class="col-sm-1 col-md-1 warning-sign">
						<span class="glyphicon glyphicon-exclamation-sign"></span>
					</div>
					<div id="dialog-box-text" class="col-sm-10 table-responsive"></div>
				</div>
				<div class="modal-footer">
					<button id="dialogBoxSubmit" type="button" class="btn btn-primary" data-dismiss="modal">确定</button>
					<button id="dialogBoxCancel" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
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
		"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js",
		"paginator": "bootstrap/paginator/bootstrap-paginator.js"
	}
})
seajs.use("${ctx}/static/page-js/elasticcalc/gce/gceList/main");
</script>
</html>
