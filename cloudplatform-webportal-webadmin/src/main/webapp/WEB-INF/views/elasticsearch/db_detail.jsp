<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div id="page-header-id" class="page-header">
		<%-- <h1>
			<a href="${ctx}/list/mcluster">返回Container集群列表<i class="ace-icon fa fa-reply icon-only"></i></a>	
		</h1> --%>
	</div>
	<input class="hidden" value="${id}" name="id"
		id="id" type="text" />
	<!-- /.page-header -->
	<div class="row">
		<div style="margin-top: 10px;">
			<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
				<div class="widget-header">
					<h5 id="mclusterTitle" class="widget-title">数据库详情</h5>
				</div>

				<table class="table table-bordered table-striped table-hover"
					id="Mcluster_detail_table">
					<thead>
						<tr
							style="background-image: none; background-color: #307ECC; color: #FFFFFF;">
							<th>数据库名称</th>
							<th class="hidden-480">存储</th>
							<th>内存</th>
							<th class="hidden-480">节点个数</th>
							<th>创建时间</th>
							<th>状态</th>
						</tr>
					</thead>
					<tbody id="tby"></tbody>
				</table>
			</div>
		</div>
	</div>
	<div id="dialog-confirm" class="hide">
		<div id="dialog-confirm-content" class="alert alert-info bigger-110"></div>
		<div class="space-6"></div>
		<p id="dialog-confirm-question" class="bigger-110 bolder center grey"></p>
	</div>
</div>

<script src="${ctx}/static/scripts/pagejs/elasticsearch/db_detail.js"></script>