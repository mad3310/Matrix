<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<link rel="stylesheet" href="${ctx}/static/ace/css/select2.css" />
<div class="page-content-area">
	<div class="row">
		<div id="monitor-option" class="col-sm-12">
			<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
				<script>
					$(window).load(function() {
						var iw=document.body.clientWidth;
						if(iw>767){//md&&lg
							$('.queryOption').removeClass('collapsed');
						}
					});
				</script>
				<div class="widget-header hidden-md hidden-lg">
					<h5 class="widget-title">查询条件</h5>
					<div class="widget-toolbar">
						<a href="#" data-action="collapse">
							<i class="ace-icon fa fa-chevron-down"></i>
						</a>
					</div>
				</div>
				<div class="widget-body">
					<div class="chosen-field"
						style="height: 50px; margin: 10px 0 10px 0;">
						<div class="row">
							<div class="col-sm-12 col-xs-12 ">
								<form>
									<div class="col-md-1"></div>
									<div class="form-group col-sm-12 col-xs-12 col-md-2 hidden-xs hidden-sm">
										<!-- <select class="chosen-select" data-placeholder="请选择物理机集群..."
											id="monitorHclusterOption">
											<option></option>
										</select> -->
										<select class="chosen-select monitorHclusterOption" data-placeholder="请选择物理机集群...">
											<option></option>
										</select>
										
									</div>
									<div class="form-group col-sm-12 col-xs-12 col-md-2 hidden-md hidden-lg">
										<select class="monitorHclusterOption col-sm-12 col-xs-12">
											<option>请选择物理机集群...</option>
										</select>
									</div>
									<div class="form-group col-sm-12 col-xs-12 col-md-2 hidden-xs hidden-sm">
										<!-- <select class="chosen-select" single id="mclusterOption"
											name="mclusterId" data-placeholder="请选择集群...">
											<option></option>
										</select> -->
										<select class="chosen-select mclusterOption" single 
											name="mclusterId" data-placeholder="请选择集群...">
											<option></option>
										</select>
									</div>
									<div class="form-group col-sm-12 col-xs-12 col-md-2 hidden-md hidden-lg">
										<select class="mclusterOption col-sm-12 col-xs-12">
											<option>请选择集群...</option>
										</select>
									</div>
									<div class="form-group col-sm-12 col-xs-12 col-md-2">
										<select id="queryTime" name="queryTime" class="form-control">
											<option value="">请选择时段...</option>
											<option value="1">一小时</option>
											<option value="2">三小时</option>
											<option value="3">一天</option>
											<option value="4">一周</option>
										</select>
									</div>
									<!-- <div class="col-sm-12 col-xs-12 col-md-2">
										<select class="chosen-select" multiple="multiple"
											name="monitorPoint" id="monitorPointOption"
											data-placeholder="请选择监控点...">
											<option></option>
										</select>
									</div> -->
									<div class="form-group col-sm-12 col-xs-12 col-md-2">
										<select class="select2" multiple="multiple"
											name="monitorPoint" id="monitorPointOption"
											data-placeholder="请选择监控点...">
											<option></option>
										</select>
									</div>
									<div class="form-group col-sm-12 col-xs-12 col-md-2">
										<button class="pull-right btn btn-sm btn-primary btn-search"
											id="refresh-data" type="button"
											onclick="refreshChartForSelect()">
											<i class="ace-icon fa fa-refresh"></i>刷新
										</button>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- /.page-header -->
			<div id="monitor-view" class="row"></div>
			<div class="space-24"></div>
			<div id="monitor-view-demo" name="monitor-view"
				class="col-xs-12 col-sm-12 col-md-12 widget-container-col ui-sortable hide ui-sortable-disabled">
				<div class="widget-box">
					<div class="widget-header widget-header-flat widget-header-small">
						<!-- <h4 class="widget-title lighter">监控图</h4> -->
						<div class="widget-toolbar">
							<a href="#" data-action="fullscreen" class="orange2"
								onclick="updateChartSize($(this))"> <i
								class="ace-icon fa fa-expand"></i>
							</a> <a href="#" class="orange2" data-action="settings"
								onclick="changeDraggable($(this))"> <input type="text"
								name="draggable" value="0" class="hidden" /> <i
								class="ace-icon fa fa-thumb-tack"
								style="-webkit-transform: rotate(45deg); -moz-transform: rotate(45deg); -o-transform: rotate(45deg);"></i>
							</a>
						</div>
					</div>
					<div class="widget-body">
						<div class="widget-main">
							<div name="data-chart"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<script src="${ctx}/static/scripts/highcharts/highcharts.js"></script>
<%-- <script src="${ctx}/static/scripts/highcharts/themes/grid.js"></script> --%>
	<%-- <script src="${ctx}/static/scripts/highcharts/themes/grid-light.js"> --%>
<!-- </script> -->

<%-- <script src="${ctx}/static/scripts/highcharts/themes/dark-blue.js"></script> --%>
<script src="${ctx}/static/scripts/pagejs/mcluster_monitor_view_res.js"></script>
<script src="${ctx}/static/ace/js/select2.min.js"></script>

<script type="text/javascript">
	$('.select2').css('width','100%').select2({allowClear:true})
</script>

