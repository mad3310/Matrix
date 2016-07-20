<%@ page language="java" pageEncoding="UTF-8"%>
<!-- /section:settings.box -->
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#hclusterName').removeClass('chosen-select');}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#hclusterName').removeClass('chosen-select');}
	});
</script>
<link rel="stylesheet" href="${ctx}/static/ace/css/select2.css" />
<div class="page-content-area">
<div class="row">
	<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
		<div class="widget-header hidden-md hidden-lg">
			<h5 class="widget-title">物理机集群查询条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
			<div class="page-header col-sm-12 col-xs-12 col-md-12">
			<!-- <h3> 物理机集群列表 </h3> -->
			<div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
				<form class="form-inline">
					<div class="form-group col-sm-6 col-xs-12 col-md-2">
						<!-- <input type="text" class="form-control" id="hclusterName" placeholder="物理机集群名称"> -->
						<select  class="chosen-select" id="hclusterName" data-placeholder="所属物理机集群" style="width:100%">
							<option></option>
						</select>
					</div>
					<!-- <div class="form-group col-sm-6 col-xs-12 col-md-3">
						<input type="text" class="form-control" id="hclusterIndex"
							placeholder="编号">
					</div> -->
					<!-- <div class="form-group">
						<input type="date" class="form-control" id="PhyMechineDate"
							placeholder="创建时间">
					</div> -->
					<div class="form-group col-sm-6 col-xs-12 col-md-2">
						<select class="form-control" id="hclusterStatus">
						    <option value="">请选择状态</option>
						</select>
					</div>
					<div class="form-group col-sm-6 col-xs-12 col-md-3">
					<button class="btn btn-sm btn-primary btn-search" id="hclusterSearch" type="button"><i class="ace-icon fa fa-search"></i>搜索</button>
					<button class="btn btn-sm " type="button" id="hclusterSearchClear">清空</button>
					</div>
				</form>
			</div>
		</div>
		</div>
	</div>
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">物理机集群列表</h5>
				<div class="widget-toolbar no-border">
					<button class="btn btn-white btn-primary btn-xs" data-toggle="modal" onclick="openModalHclusterCreate()">
						<i class="ace-icont fa fa-plus"></i>
						 创建物理机集群
					</button>
				</div>
			</div>
			<div class="widget-body">
				<div class="widget-main no-padding">
					<table id="hcluster_list" class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th class="center">
									<label class="position-relative">
										<input type="checkbox" class="ace" />
										<span class="lbl"></span>
									</label>
								</th>
								<!-- <th width="19%">物理机集群名称</th>
								<th width="19%">编号</th>
								<th width="19%" class="hidden-480">应用业务类型 </th>
								<th width="19%" class="hidden-480">创建时间 </th>
								<th width="19%">当前状态</th>
								<th width="19%">操作</th> -->
								<th>物理机集群名称</th>
								<th>编号</th>
								<th class="hidden-480">应用业务类型 </th>
								<th class="hidden-480">创建时间 </th>
								<th class="hidden-480">集群ip池 </th>
								<th>当前状态</th>
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
			<small><font color="gray">*注：点击编号可查看详情.</font></small>
		</div>
		<div id="pageControlBar" class="col-xs-12 col-sm-12">
			<input type="hidden" id="totalPage_input" />
			<ul class="pager">
				<li><a href="javascript:void(0);" id="firstPage">&laquo首页</a></li>
				<li><a href="javascript:void(0);" id="prevPage">上一页</a></li>
				<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
				<li><a href="javascript:void(0);" id="lastPage">末页&raquo</a></li>
	
				<li class="hidden-480"><a>共<lable id="totalPage"></lable>页</a>
				</li>
				<li class="hidden-480"><a>第<lable id="currentPage"></lable>页</a>
				</li>
				<li class="hidden-480"><a>共<lable id="totalRows"></lable>条记录</a>
				</li>
			</ul>
		</div>
		
		<div class="modal fade" id="modal-hcluster" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="margin-top:157px">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
            				<button type="button" class="close" data-dismiss="modal">
            					<span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
            					<span class="sr-only">关闭</span>
            				</button>
            				<!-- <h4 class="modal-title">创建物理机集群 </h4> -->
            		</div>
            		<input type="hidden" id="inputCurrentHclusterId">
					<form id="form-hcluster" name="form_hcluster" class="form-horizontal" role="form">
						<div class="modal-body">            				
            				<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="hclusterNameAlias">物理机集群名称</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="hclusterNameAlias" id="hclusterNameAlias" type="text" />	
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="集群名称应能概括此集群的信息，可用汉字!" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="hcluster_name">编号</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="hclusterName" id="hclusterName" type="text" />
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="请输入字母数字或'_'." style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>	
							
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="containerIps">集群ip池</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="containerIps" id="containerIps" type="text" />
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="多个ip请用,分隔" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>							
							<div class="form-group">
								<label class="control-label col-sm-12 col-xs-12 col-md-4  no-padding-right"> 是否启用</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<select id="isHclusterEnable"  name="status">
										<option value="1" selected="selected">启用</option>
										<option value="0">不启用</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-sm-12 col-xs-12 col-md-4  no-padding-right"> 应用业务类型</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<select multiple=""  class="select2"  name="type" data-placeholder="请选择应用业务类型...">
										<option>RDS</option>
										<option>SLB</option>
										<option>GCE</option>
										<option>CBASE</option>
										<option>OSS</option>
										<option>ES</option>
									</select>
								</div>
								<label class="control-label hidden-sm hidden-xs">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="请选择应用业务类型..." style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
            			</div>
					
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">关闭</button>
						<button id="botton-hcluster-submit" type="button" class="btn btn-sm disabled btn-primary" onclick="submitHcluster()">提交</button>
					</div>
				</form>
				</div>
			</div>
		</div>
		
		<div id="dialog-confirm" class="hide">
			<div id="dialog-confirm-content" class="alert alert-info bigger-110"></div>
			<div class="space-6"></div>
			<p id="dialog-confirm-question" class="bigger-110 bolder center grey"></p>
		</div>
	</div>
</div>
<!-- /.page-content-area -->

<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>

<script src="${ctx}/static/ace/js/jquery.dataTables.min.js"></script>
<script src="${ctx}/static/ace/js/jquery.dataTables.bootstrap.js"></script>
<script src="${ctx}/static/ace/js/select2.min.js"></script>
<script src="${ctx}/static/scripts/pagejs/hcluster_list.js"></script>

<script type="text/javascript">
	$('.select2').css('width','100%').select2({allowClear:true});
</script>
