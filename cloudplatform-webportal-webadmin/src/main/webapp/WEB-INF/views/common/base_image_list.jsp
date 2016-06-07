<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}
	});
</script>
<div class="page-content-area">
  <div class="row">
    <div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
      <div class="widget-header hidden-md hidden-lg">
	<h5 class="widget-title">基础镜像查询条件</h5>
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
	      <div class="form-group  col-sm-6 col-xs-12 col-md-2">
		<input type="text" class="form-control" id="seImageName"
		       placeholder="镜像名称">
	      </div>
	      <div class="form-group  col-sm-6 col-xs-12 col-md-2">
		<select class="form-control" id="seImageType">
		<option value="">镜像类型</option>
		</select>
	      </div>
              <div class="form-group  col-sm-6 col-xs-12 col-md-2">
		<input type="text" class="form-control" id="seImageUsedTo"
		       placeholder="镜像用途">
	      </div>
           <div class="form-group  col-sm-6 col-xs-12 col-md-2">
		<select class="form-control" id="seImageStatus">
			<option value="">使用状态</option>
			<option value="1">默认使用</option>
			<option value="0">默认不使用</option>
		</select>
	</div>
	      <div class="form-group  col-sm-6 col-xs-12 col-md-3">
		<button class="btn btn-sm btn-primary btn-search" id="imgSearch" type="button">
		  <i class="ace-icon fa fa-search"></i>搜索
		</button>
		<button class="btn btn-sm" type="button" id="imgClearSearch">清空</button>
	      </div>
	    </form>
	  </div>
	</div>
      </div>
    </div>
    <div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
      <div class="widget-header">
	<h5 class="widget-title">基础镜像列表</h5>
	<div class="widget-toolbar no-border">
	  <button id="create_job_stream" class="btn btn-white btn-primary btn-xs" data-toggle="modal" data-target="#create-job-unit-modal">
	    <i class="ace-icont fa fa-plus"></i>
	    添加镜像
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
		<th>镜像名称</th>
		<th class="hidden-480">业务类型</th>
		<th>镜像用途</th>
		<th class="hidden-480">下载地址</th>
		<th class="hidden-480">版本</th>
		<th>默认使用</th>
		<th class="hidden-480">描述</th>
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
            <h4 class="modal-title">添加镜像 </h4>
          </div>
	  <form id="add-gce-image-form" name="add-gce-image-form" class="form-horizontal" role="form">
	    <div class="modal-body image-size-adapt">
              <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskUnitName">镜像名称</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="name" id="name" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="镜像名称应与下载镜像名相同" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="type">业务类型</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <select class="form-control" name="imageType" id="imageType" data-placeholder="请选择类型...">
		    <!-- <option value="">  </option> -->
		    <!-- <option value="xx">消息中间件</option> -->
		  </select>
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="所属业务类型" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskUnitName">镜像用途</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="purpose" id="purpose" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="标记镜像的用途，用于程序调用" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">镜像版本</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="tag" id="tag" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="上传镜像的版本号tag" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="url">下载地址</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="url" id="url" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="下载此镜像的地址" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">默认使用</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <select class="form-control" name="isUsed" id="isUsed" data-placeholder="该镜像是否可用">
		    <option value="1">默认使用</option>
		    <option value="0">默认不使用</option>
		  </select>
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="在创建改业务时，默认使用此镜像" style="cursor:pointer; text-decoration:none;">
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
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="本镜像的功能描述，最多100字符!" style="cursor:pointer; text-decoration:none;">
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
    <div class="modal fade" id="modify-image-modal" tabindex="-1" aria-labelledby="myModalLabel" style="margin-top:157px">
       <div class="modal-dialog">
	<div class="modal-content">
	  <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
              <span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
              <span class="sr-only">关闭</span>
            </button>
            <h4 class="modal-title">修改镜像 </h4>
          </div>
	  <form id="modify-gce-image-form" name="add-gce-image-form" class="form-horizontal" role="form">
	   <input id="modify-imageId" type="hidden" value="">
	    <div class="modal-body">
              <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskUnitName">镜像名称</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="modify-name" id="modify-name" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="镜像名称应与下载镜像名相同" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="type">业务类型</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <select class="form-control" name="modify-imageType" id="modify-imageType" data-placeholder="请选择类型...">
		    <!-- <option value="">  </option> -->
		    <!-- <option value="xx">消息中间件</option> -->
		  </select>
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="所属业务类型" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="taskUnitName">镜像用途</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="modify-purpose" id="modify-purpose" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="标记镜像的用途，用于程序调用" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">镜像版本</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="modify-tag" id="modify-tag" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="上传镜像的版本号tag" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="url">下载地址</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <input class="form-control" name="modify-url" id="modify-url" type="text" />
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="下载此镜像的地址" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="beanName">默认使用</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <select class="form-control" name="modify-isUsed" id="modify-isUsed" data-placeholder="该镜像是否可用">
		    <option value="1">默认使用</option>
		    <option value="0">默认不使用</option>
		  </select>
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="在创建改业务时，默认使用该镜像" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
	      <div class="form-group">
		<label class="col-sm-12 col-xs-12 col-md-4 control-label" for="descn">描述</label>
		<div class="col-sm-10 col-xs-10 col-md-6">
		  <textarea id="modify-descn" name="modify-descn" class="form-control" rows="4"></textarea>
		</div>
		<label class="control-label">
		  <a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="本镜像的功能描述，可输入字母、数字或_,最多100字符!" style="cursor:pointer; text-decoration:none;">
		    <i class="ace-icon fa fa-question-circle blue bigger-125"></i>
		  </a>
		</label>
	      </div>
            </div>
	    <div class="modal-footer">
	      <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">关闭</button>
	      <button type="submit" class="btn btn-sm btn-primary">修改</button>
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
<script src="${ctx}/static/scripts/pagejs/common/base_image_list.js"></script>
