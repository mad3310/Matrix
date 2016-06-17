<%@ page language="java" pageEncoding="UTF-8"%>
<!-- /section:settings.box -->
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}
	});
</script>
<div class="page-content-area">
<div class="row">
<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
		<div class="widget-header hidden-md hidden-lg">
			<h5 class="widget-title">Container查询条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
			<div class="page-header col-sm-12 col-xs-12 col-md-12">
				<!-- <h3>Container集群列表</h3> -->
				<div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
					<form class="form-inline">
						<!-- <div class="form-group">
							<input type="text" class="form-control" id="containerType"
								placeholder="类型">
						</div> -->
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="containerName"
								placeholder="container名称">
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="ipAddr"
								placeholder="IP地址">
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
	<!-- /.page-header -->
	
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">Container列表</h5>
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
								<!-- <th width="16%">Container名称</th>
								<th width="16%">所属container集群</th>
								<th width="15%">所属物理机集群</th>
								<th width="15%">ip</th>
								<th width="15%">宿主机ip</th>
								<th width="15%">创建时间 </th>
								<th width="15%" class="hidden-480">当前状态</th> -->
								<th>Container名称</th>
								<th class='hidden-480'>所属container集群</th>
								<th class="hidden-480">所属物理机集群</th>
								<th>ip</th>
								<th>宿主机ip</th>
								<th class="hidden-480">创建时间 </th>
								<th>当前状态</th>
							</tr>
						</thead>
						<tbody id="tby">
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div id="pageControlBar" class="col-xs-12">
			<input type="hidden" id="totalPage_input" />
			<ul class="pager">
				<li><a href="javascript:void(0);" id="firstPage">&laquo&nbsp;首页</a></li>
				<li><a href="javascript:void(0);" id="prevPage" >上一页</a></li>
				<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
				<li><a href="javascript:void(0);" id="lastPage">末页&nbsp;&raquo</a></li>
				<li class='hidden-480'><a>共<lable id="totalPage"></lable>页</a></li>
				<li class='hidden-480'><a>第<lable id="currentPage"></lable>页</a></li>
				<li class='hidden-480'><a>共<lable id="totalRows"></lable>条记录</a></li>
			</ul>
		</div>
	</div>
</div>
<!-- /.page-content-area -->

<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>

<script src="${ctx}/static/ace/js/jquery.dataTables.min.js"></script>
<script src="${ctx}/static/ace/js/jquery.dataTables.bootstrap.js"></script>

<script src="${ctx}/static/scripts/pagejs/ocs/ocs_container_list.js"></script>