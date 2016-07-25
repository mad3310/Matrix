<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div class="row">
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">任务单元列表</h5>
				<div class="widget-toolbar no-border">
					<button id="create_job_stream" class="btn btn-white btn-primary btn-xs" data-toggle="modal" data-target="#create-job-unit-modal">
						<i class="ace-icont fa fa-plus"></i>
						 创建单元
					</button>
				</div>
			</div>
			<div class="widget-body">
				<div class="widget-main no-padding">
					<table class="table table-bordered" id="db_detail_table" >
						<thead>
							<tr>
								<th class="center">
									<label class="position-relative">
										<input type="checkbox" id="titleCheckbox" class="ace" />
										<span class="lbl"></span>
									</label>
								</th>
								<th>任务单元名称</th>
								<th class="hidden-480">业务类型</th>
								<th class="hidden-480">功能描述</th>
								<th>实现接口</th>
								<th class="hidden-480">重试次数</th>
								<!-- <th>操作</th> -->
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
		<div class="modal fade" id="create-job-unit-modal" tabindex="-1" aria-labelledby="myModalLabel" style="margin-top:157px">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
           				<button type="button" class="close" data-dismiss="modal">
           					<span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
           					<span class="sr-only">关闭</span>
           				</button>
           				<h4 class="modal-title">创建任务单元 </h4>
            		</div>
					<form id="create-task-unit-form" name="create-task-unit-form" class="form-horizontal" role="form">
						<div class="modal-body">            				
            				<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskUnitName">任务单元名称</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="taskUnitName" id="taskUnitName" type="text" />
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="任务单元名称应能概括此任务的信息，可用汉字" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskType">业务类型</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<select class="form-control" name="taskType" id="taskType" data-placeholder="请选择类型...">
										<!-- <option value="">  </option> -->
										<!-- <option value="xx">消息中间件</option> -->
										<option value="RDS">RDS</option>
										<option value="SLB">SLB</option>
										<option value="GCE">GCE</option>
										<option value="CBASE">CBASE</option>								
										<option value="OSS">OSS</option>								
										<option value="LOG">LOG</option>								
										<option value="ES">ES</option>								
									</select>
								</div>								
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="所属业务类型" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">实现接口</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="beanName" id="beanName" type="text" />
								</div>								
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="执行本任务时所调用的接口" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="retry">失败重试次数</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<select class="form-control" name="retry" id="retry" data-placeholder="请选择重试次数">
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
									</select>
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="失败后自动重试的次数" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="descn">描述</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<textarea id="descn" name="descn" class="form-control" rows="4"></textarea>
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="本单元的功能描述，可输入字母、数字或_,最多100字符!" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
            			</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">关闭</button>
						<button type="submit" class="btn btn-sm btn-primary">创建</button>
					</div>
				</form>
				</div>
			</div>
		</div>
	</div>
</div>
<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>
<script src="${ctx}/static/scripts/pagejs/job_unit_list.js"></script>