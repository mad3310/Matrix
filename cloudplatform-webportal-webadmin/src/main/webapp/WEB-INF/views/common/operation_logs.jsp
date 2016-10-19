<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div class="row">
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">操作日志</h5>
			</div>
			<div class="widget-body">
				<div class="page-header col-sm-12 col-xs-12 col-md-12">
					<!-- <h3>数据库列表	</h3> -->
				    <div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
						<form class="form-inline">
							<div class="form-group col-sm-6 col-xs-12 col-md-2">
								<input type="text" class="form-control" id="inputUserName" placeholder="用户名称">
							</div>
							<div class="form-group col-sm-6 col-xs-12 col-md-2" style="padding-right:0;">
								<button class="btn btn-sm btn-primary btn-search" type="button" id="btnSearch"><i class="ace-icon fa fa-search"></i>搜索
								</button>
								<button class="btn btn-sm" type="button" id="btnSearchClear">清空</button>
							</div>
						</form>
					</div>	
				</div>
				<div class="widget-main no-padding">
					<table class="table table-bordered" id="db_detail_table" >
						<thead>
							<tr>
								<th class="hidden-480">操作名称</th>
								<th>操作信息</th>
								<th>用户</th>
								<th>操作时间</th>
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
	
				<li class="hidden-480"><a>共<lable id="totalPage"></lable>页</a>
				</li>
				<li class="hidden-480"><a>第<lable id="currentPage"></lable>页</a>
				</li>
				<li class="hidden-480"><a>共<lable id="totalRows"></lable>条记录</a>
				</li>
			</ul>
		</div>
	</div>
</div>
<script src="${ctx}/static/scripts/pagejs/common/operation_logs.js"></script>