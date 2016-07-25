<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<%@include file='main.jsp' %>
<body>
<style>
.dirPath{color:#428bca;}.dirPath:hover{cursor: pointer;}a:hover{cursor: pointer;}
</style>

<input class="hidden" value="${swiftId}" name="swiftId" id="swiftId" type="text" />
<input class="hidden" value="${directory}" id="dirName" type="text" />
<input class="hidden" value="" id="baseLocation" type="text" />
<div class="se-heading m-pr10" id="headingOne">
	<div class="pull-left">
		<h5>文件管理</h5>
	</div>
	<div class="clearfix"></div>
	<div class="">
	<div class="col-xs-12 col-sm-12 col-md-12 clearfix">
		<!-- <div class="pull-left">
			<form class="form-inline" role="form">
				<div class="form-group">
					<input onkeyup="value=value.replace(/[\W]/g,'') " 
					onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" 
					id="fileName" type="text" class="form-control" size="48" placeholder="请输入文件名称进行搜索">
				</div>
				<button id="search" type="button" class="btn btn-default">搜索</button>
			</form>
		</div> -->
		<div class="pull-left" style="padding:15px 0 0 10px;">
			<span name='dirName' style="color:#428bca">当前位置：根目录 /</span>
		</div>
		<div class="pull-right" style="padding-bottom:10px;">
		<!-- 文件上传-->
		<form enctype="multipart/form-data" id='form-upload' method="post" action="/oss/${swiftId}/file">
			<div class="btn btn-success btn-file"> 
				<i class="fa fa-cloud-upload"></i> &nbsp;上传文件
				<input id="upload" type="file" name="file" class="file" autocomplete="off">
				<input id="dir" type="hidden" name="directory" >
			</div>
			<button type="button" class="btn btn-primary" data-backdrop="false" data-toggle="modal" data-target="#addDirModal" id="test"><i class="fa fa-plus"></i> 新建文件夹</button>
			<button class="btn-default btn btn-md " id="refresh"><span class="glyphicon glyphicon-refresh"></span> <span class="hidden-xs">刷新</span></button>
		</form>
		</div>
		<div class="clearfix"></div>
	</div>
	</div>
</div>
<div class="container-fluid">
	<div class="row"><!-- main-content-center-begin -->
		<div class="col-xs-12 col-sm-12 col-md-12">
			<table class="table table-hover table-se">
				<thead>
					<tr>
						<th width="10">
							<input type="checkbox">
						</th>
						<th class="padding-left-32">文件名称</th>
						<th>类型</th>
						<th class="hidden-xs">上传日期</th>
						<th class="hidden-xs">大小</th>
						<th class="hidden-xs">下载地址</th>
						<!-- <th>缓存过期</th>-->
						<th class="text-right">操作</th> 
					</tr>
				</thead>
				<tbody id="tby">
				</tbody>
				<!-- <tfoot id="paginatorBlock">
					<tr class="tfoot">
						<td width="10">
							<input type="checkbox">
						</td>
						<td colspan="10">
							<div class="pull-left">
									<div class="clearfix" pagination-info="paginationInfo">
										<div class="pull-left">
											<button class="btn btn-default" disabled="disabled" style="height:30px;font-size:12px;">批量删除</button>
										</div>
									</div>
							</div>
							<div class="pull-right">
								<div class="pagination-info">
									<span class="ng-binding">共有<span id="totalRecords">1</span>条</span>， 
									<span class="ng-binding">每页显示：<span id="recordsPerPage">10</span>条</span>&nbsp;
								    <ul id="paginator" class="pagination pagination-sm"></ul>
								</div>
							</div>
							<div class="clearfix"></div>
						</td>
					</tr>
				</tfoot> -->
			</table>
		    <div class="help-block hidden" id="noData">没有记录</div>
		</div>
	</div><!-- main-content-center-end -->
</div>
<div class="modal fade in" id="addDirModal" role="dialog" aria-labelledby="addDirModal" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="modal-title">新建文件夹<a class="anchorjs-link" href="#modal-title"><span class="anchorjs-icon"></span></a></h4>
      </div>
      <form class="form-horizontal" name="form" id="createDirform" method="post" >
      <div class="modal-body">
        	<div class="form-group">
        		<label class="col-xs-12 col-sm-3 control-label">文件夹名：</label>
        		<div class="col-xs-12 col-sm-9">
        			<div class="row">
        				<div class="col-xs-10 col-sm-7">
        					<input type="text" class="form-control" name="folderName" id="floderName">
        				</div>
        			<!-- <div class="col-xs-5 col-sm-5 text-danger">
        				<div class="help-tip">
        					<i class='fa fa-exclamation-circle'></i>&nbsp;<span class="">文件夹名不能为空</span>
        				</div>
        			</div> -->
        		</div>
	        	<div class="row">
	        		<div class="col-xs-12 col-sm-12">
	        			<p class="help-tip">文件夹命名规范：<br>» 1. 只能包含字母，数字，中文，下划线（_）和短横线（-）,小数点（.）<br>» 2. 只能以字母、数字或者中文开头<br>» 3. 文件夹的长度限制在1-254之间<br>» 4. Object总长度必须在1-1023之间</p></div>
	        	</div>
        	</div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="submit" class="btn btn-primary" id="add-dir" >新建</button>
      </div>
    </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div>
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script>
//Set configuration
seajs.config({
	base: "${ctx}/static/modules/",
	alias: {
		"jquery": "jquery/2.0.3/jquery.min.js",
		"jquery.form": "jquery/form/jquery.form.js",
		"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js",
		"bootstrapValidator": "bootstrap/bootstrapValidator/0.5.3/bootstrapValidator.js"
	}
});
seajs.use("${ctx}/static/page-js/cloudswift/fileManage/main");
/*self define*/
</script>
</body>
</html>