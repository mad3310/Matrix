<%@ page language="java" pageEncoding="UTF-8"%>
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>991){//md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#dbPhyMcluster').removeClass('chosen-select');$('#containeruser').removeClass('chosen-select')}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>991){//md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#dbPhyMcluster').removeClass('chosen-select');$('#containeruser').removeClass('chosen-select')}
	});
</script>
<div class="page-content-area">
  <div class="row">
    <div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
		<div class="widget-header hidden-md hidden-lg">
			<h5 class="widget-title">GCE查询条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
			<div class="page-header col-sm-12 col-xs-12 col-md-12 col-lg-12">
				<!-- <h3>数据库列表	</h3> -->
			    <div class="input-group pull-right col-sm-12 col-xs-12 col-md-12 col-lg-12">
					<form class="form-inline">
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="dbName" placeholder="GCE名称">
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="dbMcluster" placeholder="所属cluster">
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<!-- <input type="text" class="form-control" id="dbPhyMcluster" placeholder="所属物理机集群"> -->
							<select  class="chosen-select" id="dbPhyMcluster" data-placeholder="所属物理机集群" style="width:100%">
								<option></option>
							</select>
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<!-- <input type="text" class="form-control" id="dbuser" placeholder="所属用户"> -->
							<select  class="chosen-select" id="containeruser" data-placeholder="所属用户" style="width:100%">
								<option></option>
							</select>
						</div>
						<!-- <div class="form-group">
							<input type="date" class="form-control" id="PhyMechineDate"
								placeholder="创建时间">
						</div> -->
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<select class="form-control" id="dbStatus">
								<option value="">请选择状态</option>
							</select>
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2" style="padding-right:0;">
							<button class="btn btn-sm btn-primary btn-search" type="button" id="dbSearch"><i class="ace-icon fa fa-search"></i>搜索
							</button>
							<button class="btn btn-sm" type="button" id="dbSearchClear">清空</button>
						</div>
					</form>
				</div>	
			</div>
		</div>
	</div>
    <!-- /.page-header -->

    <div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
      <div class="widget-header">
	<h5 class="widget-title">GCE列表</h5>
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
		<th>GCE名称</th>
		<th  class="hidden-480">所属gceCluster</th>
		<th  class="hidden-480">所属物理机集群</th>
		<th>所属用户</th>
		<th  class="hidden-480">
		  创建时间
		</th>
		<th>当前状态</th>
	      </tr>
	    </thead>
	    <tbody id="tby">
	    </tbody>
	  </table>
	</div>
      </div>
    </div>
    <div id="pageControlBar">
      <input type="hidden" id="totalPage_input" />
      <ul class="pager">
	<li><a href="javascript:void(0);" id="firstPage">&laquo首页</a></li>
	<li><a href="javascript:void(0);" id="prevPage">上一页</a></li>
	<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
	<li><a href="javascript:void(0);" id="lastPage">末页&raquo</a></li>

	<li class='hidden-480'><a>共<lable id="totalPage"></lable>页</a>
	</li>
	<li class='hidden-480'><a>第<lable id="currentPage"></lable>页</a>
	</li>
	<li class='hidden-480'><a>共<lable id="totalRows"></lable>条记录</a>
	</li>
      </ul>
    </div>
  </div>
</div>
<!-- /.page-content-area -->

<script src="${ctx}/static/scripts/pagejs/gce/gce_server_list.js"></script>
