<%@ page language="java" pageEncoding="UTF-8"%>
<!-- /section:settings.box -->
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');$('#containeruser').removeClass('chosen-select')}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');$('#containeruser').removeClass('chosen-select')}
	});
</script>
<div class="page-content-area">
<div class="row">
<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
		<div class="widget-header hidden-md hidden-lg">
			<h5 class="widget-title">RDS集群查询条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
		<div class="page-header col-sm-12 col-xs-12 col-md-12">
				<!-- <h3>RDS集群列表</h3> -->
				<div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
					<form class="form-inline">
						<!-- <div class="form-group">
							<input type="text" class="form-control" id="containerType"
								placeholder="类型">
						</div> -->
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="containerName" placeholder="RDS集群名称">
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<!-- <input type="text" class="form-control" id="Physicalcluster" placeholder="所属物理机集群"> -->
							<select  class="chosen-select" id="Physicalcluster" data-placeholder="所属物理机集群" style="width:100%">
								<option></option>
							</select>
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<!-- <input type="text" class="form-control" id="containeruser" placeholder="所属用户"> -->
							<select  class="chosen-select" id="containeruser" data-placeholder="所属用户" style="width:100%">
								<option></option>
							</select>
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<select class="form-control" id="containerStatus">
								<option value="">请选择状态</option>
							</select>
						</div>
						<!-- <div class="form-group">
							<input type="date" class="form-control" placeholder="yyyy-MM-dd">
						</div> -->
						<div class="form-group col-sm-6 col-xs-12 col-md-3">
						<button class="btn btn-sm btn-primary btn-search" id="mclusterSearch" type="button">
							<i class="ace-icon fa fa-search"></i>搜索
						</button>
						<button class="btn btn-sm" type="button" id="mclusterClearSearch">清空</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">RDS集群列表</h5>
				<div class="widget-toolbar no-border">
					<!-- <button class="btn btn-white btn-primary btn-xs" data-toggle="modal" onclick="queryHcluster()" data-target="#create-mcluster-modal">
						<i class="ace-icont fa fa-plus"></i>
						 创建RDS集群
					</button> -->
				</div>
			</div>
		
			<div class="widget-body">
				<div class="widget-main no-padding">
					<table id="mcluster_list" class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th class="center">
									<label class="position-relative">
										<input type="checkbox" class="ace" />
										<span class="lbl"></span>
									</label>
								</th>
								<!-- <th width="13%">RDS集群名称</th>
								<th width="16%">所属物理机集群</th>
								<th width="13%">类型</th>
								<th width="13%">所属用户</th>
								<th width="13%">创建时间 </th>
								<th width="13%" class="hidden-480">当前状态</th>
								<th width="13%">操作</th> -->
								<th>RDS集群名称</th>
								<th class="hidden-480">所属物理机集群</th>
								<!-- <th class="hidden-480">类型</th> -->
								<th>所属用户</th>
								<th class="hidden-480">创建时间 </th>
								<th>当前状态</th>
								<th>备份开关</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody id="tby">
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-xs-12 col-sm-12">
			<small><font color="gray"></font></small>
		</div>
		<div id="pageControlBar" class="col-xs-12 col-sm-12">
			<input type="hidden" id="totalPage_input" />
			<ul class="pager">
				<li><a href="javascript:void(0);" id="firstPage">&laquo&nbsp;首页</a></li>
				<li><a href="javascript:void(0);" id="prevPage" >上一页</a></li>
				<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
				<li><a href="javascript:void(0);" id="lastPage">末页&nbsp;&raquo</a></li>
				<li class="hidden-480"><a>共<lable id="totalPage"></lable>页</a></li>
				<li class="hidden-480"><a>第<lable id="currentPage"></lable>页</a></li>
				<li class="hidden-480"><a>共<lable id="totalRows"></lable>条记录</a></li>
			</ul>
		</div>
		
		<div class="modal fade" id="create-mcluster-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="margin-top:157px">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
            				<button type="button" class="close" data-dismiss="modal">
            					<span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
            					<span class="sr-only">关闭</span>
            				</button>
            				<h4 class="modal-title">创建RDS集群</h4>
            		</div>
					<form id="create-mcluster-form" name="create-mcluster-form" class="form-horizontal" role="form">
					<!-- <div class="col-xs-12">
						<h4 class="lighter">
							<a href="#modal-wizard" data-toggle="modal" class="blue"> 创建RDS集群 </a>
						</h4>
						<div class="widget-box">
							<div class="widget-body">
								<div class="widget-main">
									<div class="form-group">
										<label class="col-sm-4 control-label" for="mcluster_name">RDS集群名称</label>
										<div class="col-sm-6">
											<input class="form-control" name="mclusterName" id="mclusterName" type="text" />
										</div>
										<label class="control-label">
											<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="请输入字母数字或'_'." style="cursor:pointer; text-decoration:none;">
												<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
											</a>
										</label>
									</div>
									<div class="form-group">
										<label class="col-sm-4 control-label" for="hcluster">物理机集群</label>
										<div class="col-sm-6">
											<select class="form-control" name="hclusterId" id="hcluster_select">
											</select>
										</div>
										<label class="control-label" for="hcluster">
											<a id="hclusterHelp" name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="请保证您的应用与数据库在同一地域,以保证连接速度." style="cursor:pointer; text-decoration:none;">
												<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
											</a>
										</label>
									</div>
								</div>
							</div>
						</div>
					</div> -->
					<div class="modal-body">            				
            					<div class="form-group">
										<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="mcluster_name">RDS集群名称</label>
										<div class="col-sm-10 col-xs-10 col-md-6">
											<input class="form-control" name="mclusterName" id="mclusterName" type="text" />
										</div>
										<label class="control-label">
											<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="请输入字母数字或'_'." style="cursor:pointer; text-decoration:none;">
												<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
											</a>
										</label>
									</div>
									<div class="form-group">
										<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="hcluster">物理机集群</label>
										<div class="col-sm-10 col-xs-10 col-md-6">
											<select class="form-control" name="hclusterId" id="hcluster_select">
											</select>
										</div>
										<label class="control-label" for="hcluster">
											<a id="hclusterHelp" name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="请保证您的应用与数据库在同一地域,以保证连接速度." style="cursor:pointer; text-decoration:none;">
												<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
											</a>
										</label>
									</div>
            			</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">关闭</button>
						<button id="create-mcluster-botton" type="button" class="btn btn-sm btn-primary disabled" onclick="createMcluster()">创建</button>
					</div>
				</form>
				</div>
			</div>
		</div>
		<div class="modal fade bs-example-modal-lg"  id="create-mcluster-status-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="buildStatusHeader">
		        	<i class="ace-icon fa fa-spinner fa-spin green bigger-125"></i>
		        	创建中...
		        </h4>
		      </div>
		      <div class="modal-body">
		        <table id="mcluster_list" class="table">
						<thead>
							<tr class="info">
								<th width="3%">#</th>
								<th width="18%">操作</th>
								<th width="15%">开始时间</th>
								<th width="15%">结束时间</th>
								<th>信息</th>
								<th width="10%">结果  </th>
							</tr>
						</thead>
						<tbody id="build_status_tby">
						</tbody>
					</table>
		      </div>
		    </div><!-- /.modal-content -->
		  </div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
		<div id="dialog-confirm" class="hide">
			<div id="dialog-confirm-content" class="alert alert-info bigger-110">
				删除RDS集群将不能恢复！
			</div>
			<div class="space-6"></div>
			<p id="dialog-confirm-question" class="bigger-110 bolder center grey">
				您确定要删除?
			</p>
		</div>
	</div>
</div>
<!-- /.page-content-area -->

<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>

<script src="${ctx}/static/ace/js/jquery.dataTables.min.js"></script>
<script src="${ctx}/static/ace/js/jquery.dataTables.bootstrap.js"></script>

<script src="${ctx}/static/scripts/pagejs/mcluster_list.js"></script>