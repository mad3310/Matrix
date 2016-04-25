<%@ page language="java" pageEncoding="UTF-8"%>
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');}
	});
</script>
<div class="page-content-area">
	<div class="row">
	<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
		<div class="widget-header hidden-md hidden-lg">
			<h5 class="widget-title">节点预警查询条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
			<div class="page-header col-sm-12 col-xs-12 col-md-12">
				<div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
					<form class="form-inline">
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="monitorContainer"
								placeholder="RDS集群名称">
						</div>

						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="VipAddress"
								placeholder="Vip节点地址">
						</div>

						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<!-- <input type="text" class="form-control" id="monitorPhyM" placeholder="所属物理机集群"> -->
							<select  class="chosen-select" id="Physicalcluster" data-placeholder="所属物理机集群" style="width:100%">
								<option></option>
							</select>
							<input type="hidden" id="hclusters"></input>
						</div>
						<!-- <div class="form-group">
							<select class="form-control" id="nodeStatus">
								<option value="">请选择状态</option>
							</select>
						</div> -->
						<!-- <div class="form-group">
							<input type="date" class="form-control" placeholder="yyyy-MM-dd">
						</div> -->
						<div class="form-group col-sm-6 col-xs-12 col-md-3">
						<button class="btn btn-sm btn-primary btn-search" id="monitorNodeSearch" type="button">
							<i class="ace-icon fa fa-search"></i>搜索
						</button>
						<button class="btn btn-sm" type="button"
							id="monitorNodeClear">清空</button>
						</div>
					</form>

				</div>
			</div>
		</div>
	</div>
	
	<!-- /.page-header -->
            <!-- <div class="modal fade" id="dbuseradvancedSearch">
            	<div class="modal-dialog">
            		<div class="modal-content">
            			<div class="modal-header">
            				<button type="button" class="close" data-dismiss="modal">
            					<span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
            					<span class="sr-only">关闭</span>
            				</button>
            				<h4 class="modal-title">高级搜索</h4>
            			</div>
            			<div class="modal-body">
            				<form class="form-horizontal" role="form">
            					<div class="form-group">
            						<lable class="col-sm-4 control-label" for="monitorContainer"><b>container集群名称</b></lable>
            						<div class="col-sm-7">
            							<input type="text" class="form-control" id="monitorContainer" placeholder="container集群名称">
            						</div>
            						<label class="control-label"><i class="ace-icon fa fa-info-circle blue bigger-125"></i></label>
            					</div>
            					
            					<div class="form-group">
            						<lable class="col-sm-4 control-label" for="VipAddress"><b>Vip节点地址</b></lable>
            						<div class="col-sm-7">
            							<input type="text" class="form-control" id="VipAddress" placeholder="Vip节点地址">
            						</div>
            						<label class="control-label"><i class="ace-icon fa fa-info-circle blue bigger-125"></i></label>
            					</div>
            					<div class="form-group">
            						<lable class="col-sm-4 control-label" for="monitorPhyM"><b>所属物理机集群</b></lable>
            						<div class="col-sm-7">
            							<input type="text" class="form-control" id="monitorPhyM" placeholder="所属物理机集群">
            						</div>
            						<label class="control-label"><i class="ace-icon fa fa-question-circle blue bigger-125"></i></label>
            					</div>
            					
            					
            					<div class="form-group">
            						<lable class="col-sm-4 control-label" for="PhyMechineRunState"><b>当前状态</b></lable>
            						<div class="col-sm-7">
            							<select class="form-control" id="PhyMechineRunState">
            								<option value="">创建失败</option>
            								<option value="">未审核</option>
            								<option value="">。。。</option>
            							</select>
            						</div>
            						<label class="control-label"><i class="ace-icon fa fa-cogs blue bigger-125"></i></label>
            					</div>
            				</form>
            			</div>
            			<div class="modal-footer">
            			<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">取 消 </button>
            			<button type="button" class="btn btn-sm btn-info">搜索</button>
            			</div>
            		</div>
            	</div>
            </div> -->

		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">节点预警列表</h5>
			</div>
			<div class="widget-body">
				<div class="widget-main no-padding">
					<table id="mcluster_list" class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<!-- <th width="20%">container集群名称</th>
							<th width="20%">VIP节点地址</th>
							<th width="20%">所属物理机集群</th>
							<th width="20%" class="hidden-480">当前状态</th>
							<th width="20%">操作</th> -->
							<th>RDS集群名称</th>
							<th>VIP节点地址</th>
							<th class="hidden-480">所属物理机集群</th>
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
			<small><font color="gray">*注：预警数据每分钟刷新一次.</font></small>
		</div>

	</div>
</div>
<!-- /.page-content-area -->

<script src="${ctx}/static/scripts/pagejs/monitor_node_list.js"></script>