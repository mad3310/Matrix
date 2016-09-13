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
	<!-- bootstrapValidator-->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrapValidator.css"/>

	<title>GCE版本管理</title>
</head>

<body>
<!-- 全局参数 start -->
<input class="hidden" value="${ecGce.id}" name="gceId" id="gceId" type="text" />
<!-- 全局参数 end -->
<!-- 账号管理主界面div -->
<div id="accountList" class="m-pr10" role="tablist" aria-multiselectable="true">
	<div class="se-heading" id="headingOne">
		<div class="pull-left">
			<h5>版本管理</h5>
		</div>
		<div class="pull-right hidden-xs">
			<button id="refresh" class="btn btn-default">
				<span class="glyphicon glyphicon-refresh"></span>
				刷新
			</button>
			<button class="btn btn-primary" id="uploadImage">上传镜像</button>
		</div>
	</div>
	<div class="table-responsive">
		<table class="table table-hover table-se">
			<thead>
			<tr>
				<th width="10%">版本号</th>
				<th width="10%">状态</th>
				<th width="20%">创建时间</th>
				<th width="20%">备注</th>
				<th width="20%">地址</th>
				<th width="20%">操作</th>
			</tr>
			</thead>
			<tbody id="tby">
			</tbody>
			<tfoot id="paginatorBlock">
					<tr class="tfoot" >
						<td colspan=" 8">
							<div class="col-xs-10 col-sm-10" style="margin:2px 0;padding:0;">
								<div class="pull-right">
									<div class="pagination-info hidden-xs">
										<span class="ng-binding">共有<span id="totalRecords"></span>条</span>， 
										<span class="ng-binding">每页显示：<span id="recordsPerPage"></span>条</span>&nbsp;
									</div>
									<ul id='paginator'></ul>
								</div>
							</div>
						</td>
					</tr>
			</tfoot>
		</table>
	</div>
</div>

<!--上传镜像-->
<div id="upload-image-box" class="modal">
	<div class="modal-dialog modal-md" style="left: -120px; display: block;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
				</button>
				<h5 id="reset-password-box-title" class="modal-title">上传镜像</h5>
			</div>
			<form id="uploadImageForm" role="form" class="form-horizontal" method="post" action="/ecgce/packages/uploadPackage" enctype="multipart/form-data" autocomplete="off">
				<div class="modal-body">
					<div class="form-group">
						<label class="col-sm-4 control-label">版本号： </label>
						<div class="col-sm-8 row">
							<div class="col-sm-12">
								<input name="version" class="form-control input-radius-2" type="text" autocomplete="off" placeholder="输入格式形如 10.12.12.1"/>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">上传本地镜像： </label>
						<div class="col-sm-8 row">
							<div class="col-sm-12">
								<input type="file" name="file" autocomplete="off" placeholder="上传文件必须是小于500M的zip文件"/>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">备注： </label>
						<div class="col-sm-8 row">
							<div class="col-sm-12">
								<textarea name="descn" class="form-control input-radius-2" autocomplete="off" placeholder="备注描述不能超过100位"></textarea>
							</div>
						</div>
					</div>
					<div class="form-group hidden">
						<label class="col-sm-4 control-label">名字： </label>
						<div class="col-sm-8 row">
							<div class="col-sm-12">
								<input type="text" value="${ecGce.gceName}" name="gceName" autocomplete="off"/>
							</div>
						</div>
					</div>
					<div class="form-group hidden" >
						<label class="col-sm-4 control-label">ID： </label>
						<div class="col-sm-8 row">
							<div class="col-sm-12">
								<input type="text" value="${ecGce.id}" name="gceId" autocomplete="off"/>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="uploadImageSubmit" type="submit" class="btn btn-primary">确定</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				</div>
			</form>
		</div>
	</div>

</div>
<div id="zard"></div>
</body>
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">

	// Set configuration
	seajs.config({
		base: "${ctx}/static/modules/",
		alias: {
			"jquery": "jquery/2.0.3/jquery.min.js",
			"jquery.form": "jquery/form/jquery.form.js",
			"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js",
			"bootstrapValidator": "bootstrap/bootstrapValidator/0.5.3/bootstrapValidator.js",
			"paginator": "bootstrap/paginator/bootstrap-paginator.js"
		}
	});
	seajs.use("${ctx}/static/page-js/elasticcalc/gce/versionManger/main");
</script>

</html>
