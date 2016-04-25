<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div id="page-header-id" class="page-header">
		<h3> 
			<a href="${ctx}/list/mcluster/monitor">RDS集群监控列表</a>
			<small id="header_mcluster_name"> 
				<i class="ace-icon fa fa-angle-double-right"></i> 
			</small>
		</h3>
	</div>
	<!-- /.page-header -->
	<input class="hidden" value="${ip}" name="ip" id="ip" type="text" />
	<div class="row">
		<div class="widget-box transparent ui-sortable-handle">
			<div class="widget-header">
				<div class="widget-toolbar no-border pull-left">
					<ul id="db-detail-tabs" class="nav nav-tabs" id="myTab2">
						<li class="active">
							<a data-toggle="tab" href="#cluster-info" onclick="queryClusterMonitorDetail()">cluster信息</a>
						</li>
						<li class="">
							<a data-toggle="tab" href="#node-info" onclick="queryNodeMonitorDetail()">node信息</a>
						</li>
						<li class="">
							<a data-toggle="tab" href="#db-info" onclick="queryNodeMonitorDetail()">db信息</a>
						</li>
					</ul>
				</div>
			</div>
			<div class="widget-body">
				<div class="widget-main padding-12 no-padding-left no-padding-right">
					<div class="tab-content padding-4">
						<div id="cluster-info" class="tab-pane  active">
							<div id="db-detail-table" class="col-xs-5">
								<table class="table table-bordered" id="db_detail_table">
									<thead>
										<tr>
											<th></th>
											<th width="70%">message</th>
											<th width="30%">alarm</th>
										</tr>
									</thead>
									<tbody id="tby-cluster-info"></tbody>
								</table>
							</div>
						</div>
						<div id="node-info" class="tab-pane">
							<div id="db-detail-table" class="col-xs-10">
								<table class="table table-bordered" id="db_detail_table">
									<thead>
										<tr>
											<th></th>
											<th width="30%">message</th>
											<th width="40%">error_record</th>
											<th width="20%">ctime</th>
											<th width="10%">alarm</th>
										</tr>
									</thead>
									<tbody id="tby-node-info"></tbody>
								</table>
							</div>
						</div>
						<div id="db-info" class="tab-pane">
							<div id="db-detail-table" class="col-xs-10">
								<table class="table table-bordered" id="db_detail_table">
									<thead>
										<tr>
											<th></th>
											<th width="30%">message</th>
											<th width="40%">error_record</th>
											<th width="20%">ctime</th>
											<th width="10%">alarm</th>
										</tr>
									</thead>
									<tbody id="tby-db-info"></tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script src="${ctx}/static/scripts/pagejs/mcluster_monitor_detail.js"></script>
