<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div class="row">
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">zookeeper列表</h5>
				<div class="widget-toolbar no-border">
					<button id="create_job_stream" class="btn btn-white btn-primary btn-xs" data-toggle="modal" data-target="#create-job-unit-modal">
						<i class="ace-icont fa fa-plus"></i>
						 添加zookeeper
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
								<th class="hidden-480">名称</th>
								<th>ip</th>
								<th>端口</th>
								<th>挂载集群数</th>
								<th>所属物理机集群</th>
								<th  class="hidden-480">状态</th>
								<!-- <th class="hidden-480">描述</th> -->
								<th>操作</th>
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
		<div class="modal fade" id="create-job-unit-modal" tabindex="-1" aria-labelledby="myModalLabel" style="margin-top:157px">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
           				<button type="button" class="close" data-dismiss="modal">
           					<span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
           					<span class="sr-only">关闭</span>
           				</button>
           				<h4 class="modal-title">添加zookeeper </h4>
            		</div>
					<form id="add-zk-form" name="add-zk-form" class="form-horizontal" role="form">
						<div class="modal-body">            				
            				<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskUnitName">zookeeper名称</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="name" id="name" type="text" />
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="zookeeper名称应与下载zookeeper名相同" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">zookeeper地址</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<textarea class="form-control" name="ip" id="ip"></textarea>
								</div>								
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="上传zookeeper的版本号tag,逗号分隔" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="url">端口</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<input class="form-control" name="port" id="port" type="text" value="2181" readOnly="true" />
								</div>								
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="zookeeper端口，默认填2181" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="url">所属物理机集群</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<select class="form-control" name="hclusterId" id="hcluster">
									</select>
								</div>								
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="所属物理机集群" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<!-- <div class="form-group">
								<label class="col-md-3 control-label">zookeeper地址</label>

								<div class="col-md-6">
									<input class="col-md-6" name="ip" id="ip" type="text">
									<label class="col-md-3 control-label">端口</label>
									<input class="col-md-3" name="port" id="port" type="text">
									<div class="clearfix"></div>
								</div>

								<div class="col-md-2" style="padding-left:0">
									<button type="button" id="addzkitem" class="btn btn-sm btn-white btn-default" style="border-radius:50%;width:30px;height:30px;text-align:center;padding:0;"><i class="ace-icon fa fa-plus" style="font-size:20px;margin:0;line-height:30px;"></i></button>
								</div>
								<div class="clearfix"></div>
								<div style="margin-top:5px;" class="zkitem">
									<div class="col-md-offset-3 col-md-6">
										<input class="col-md-6" name="ip" id="ip1" type="text">
										<label class="col-md-3 control-label">端口</label>
										<input class="col-md-3" name="port" id="port1" type="text">
										<div class="clearfix"></div>
									</div>
									<div class="col-md-2" style="padding-left:0">
										<button type="button" id="addzkitem" class="btn btn-sm btn-white btn-default hide" style="border-radius:50%;width:30px;height:30px;text-align:center;padding:0;"><i class="ace-icon fa fa-minus" style="font-size:20px;margin:0;line-height:30px;"></i></button>
									</div>
									<div class="clearfix"></div>
								</div>
							</div> -->
							
							
							<div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">可用性</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<select class="form-control" name="status" id="status" data-placeholder="该zookeeper是否可用">
										<option value="AVAILABLE">AVAILABLE</option>
										<!-- <option value="NOTAVAILABLE">NOTAVAILABLE</option> -->
									</select>
								</div>							
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="暂时禁用zookeeper" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<!-- <div class="form-group">
								<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="descn">描述</label>
								<div class="col-sm-10 col-xs-10 col-md-6">
									<textarea id="descn" name="descn" class="form-control" rows="4"></textarea>
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="本zookeeper的功能描述，可输入字母、数字或_,最多100字符!" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div> -->
            			</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">关闭</button>
						<button type="submit" class="btn btn-sm btn-primary">添加</button>
					</div>
				</form>
				</div>
			</div>
		</div>
	</div>
	<div id="dialog-confirm" class="hide">
		<div id="dialog-confirm-content" class="alert alert-info bigger-110">
			删除container集群将不能恢复！
		</div>
		<div class="space-6"></div>
		<p id="dialog-confirm-question" class="bigger-110 bolder center grey">
			您确定要删除?
		</p>
	</div>
</div>
<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>
<script src="${ctx}/static/scripts/pagejs/zk_list.js"></script>