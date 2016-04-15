<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 全局参数 start -->
<input class="hidden" value="${dbId}" name="dbId" id="dbId" type="text" />

<div class="page-content-area">
<div class="row">
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
			<h5 class="widget-title">备份查询条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
			<div class="input-group col-sm-12 col-xs-12 col-md-12">
				<div class="form-group time-range-unit-header col-xs-12 col-sm-12 col-md-12">
					<span class="time-range-title hidden-480">选择时间范围：</span>
					<span class="time-range-title hidden-md hidden-lg">开始时间：</span>
					<div class="date-unit">
						<input type='text' class="form-control datetimepicker" id='startTime' />
					</div>
					<br class="hidden-md hidden-lg">
					<span class="date-step-span hidden-480">至</span>
					<span class="time-range-title hidden-md hidden-lg">结束时间：</span>
					<div class="date-unit">
						<input type='text' class="form-control datetimepicker" id='endTime' />
					</div>
					
				</div>
				<script type="text/javascript">
					$(function () {
		                $('#startTime').datetimepicker({
		            		viewMode: 'days',
		            		format: 'L',
		            		locale: 'zh-cn'
		            	});
		                $('#endTime').datetimepicker({
		            		viewMode: 'days',
		            		format: 'L',
		            		locale: 'zh-cn'
		            	});
		            });
				</script>
				<div class="form-group col-xs-12 col-sm-6 col-md-3">
					<input id="dbName" type="input" class="form-control"
						placeholder="请输入数据库名称">
				</div>
				<div class="form-group col-xs-12 col-sm-6 col-md-3">
					<input id="mclusterName" type="input"
						class="form-control" placeholder="请输入集群名称">
				</div>
				<div class="form-group col-xs-12 col-sm-6 col-md-3">
					<select id="backupStatus" class="form-control">
					    <option value=""  selected="selected">全部</option>
						<option value="SUCCESS">备份成功</option>
						<option value="FAILD">备份失败</option>
						<option value="BUILDING">备份中...</option>
						<option value="ABNORMAL">备份异常</option>
					</select>
				</div>
				<div class="form-group col-xs-12 col-sm-6 col-md-3">
					<button id="bksearch" class="btn btn-primary btn-sm btn-search"><i class="ace-icon fa fa-search"></i>搜索</button>
					<button class="btn btn-sm" type="button" id="btnSearchClear">清空</button>
				</div>
			</div>
		</div>
	</div>
	<!-- </div> -->
			<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
				<div class="widget-header">
					<h5 class="widget-title">备份列表</h5>
				</div>
				<div class="widget-body">
					<div class="widget-main no-padding">
						<table class="table table-hover table-striped">
							<thead>
								<tr class="text-muted">
									<!-- <td style="width: 15%">container集群名称</td>
									<td style="width: 15%">数据库名称</td>
									<td style="width: 20%">开始时间</td>
									<td style="width: 20%">结束时间</td>
									<td style="width: 10%">状态</td>
									<td style="width: 20%">详情</td> -->
									<td class="hidden-480">container集群名称</td>
									<td>数据库名称</td>
									<td class="hidden-480">开始时间</td>
									<td class="hidden-480">结束时间</td>
									<td>状态</td>
									<td>详情</td>
								</tr> 
							</thead>
							<tbody id="backupTbody">
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div id="noData" class="col-xs-12 col-md-12 hidden">
			      <small><font color="gray">没有数据记录</font></small>
		    </div>
			<div id="pageControlBar">
				<input type="hidden" id="totalPage_input" ></input>
				<ul class="pager">
					<li><a href="javascript:void(0);" id="firstPage">&laquo首页</a></li>
					<li><a href="javascript:void(0);" id="prevPage">上一页</a></li>
					<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
					<li><a href="javascript:void(0);" id="lastPage">末页&raquo</a></li>
                    <li class="hidden-480"><a>共<lable id="totalPage"></lable>页
					</a></li>
					<li class="hidden-480"><a>第<lable id="currentPage"></lable>页
					</a></li>
					<li class="hidden-480"><a>共<lable id="totalRows"></lable>条记录
					</a></li>
				</ul>
	        </div>
		</div>
</div>

<!-- js -->
<script src="${ctx}/static/scripts/moment/2.9.0/moment-with-locales.min.js"></script>
<script src="${ctx}/static/scripts/bootstrap/datetimepicker/bootstrap-datetimepicker.js"></script>
<script src="${ctx}/static/scripts/pagejs/backup_recover.js"></script>
