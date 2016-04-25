<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="${ctx}/static/scripts/jquery.bootstrap-duallistbox.min.js"></script>
<div class="page-content-area">
	<div id="page-header-id" class="page-header">
		<%-- <h3> 
			<a href="${ctx}/list/db">数据库列表<i class="ace-icon fa fa-reply icon-only"></i> </a>
		</h3> --%>
	</div>
	<!-- /.page-header -->
	<input type="text" class="form-control hide" id="clusterId" name="clusterId"/>
	<div class="row clearfix">
		<div class="col-xs-12 col-sm-12 col-md-5 column">
			<div class="col-sm-12">
				<table class="table table-bordered table-striped table-hover" id="db_detail_table">	
					<caption>用户申请单</caption>
					<tr>
						<th>数据库名</th>
						<td id="dbTableDbName"></td>
					</tr>
					<tr>
						<th>所属用户</th>
						<td id="dbTableBelongUser"></td>
					</tr>
					<tr>
						<th>链接类型</th>
						<td id="dbTableType"></td>
					</tr>
					<tr>
						<th>数据库引擎</th>
						<td id="dbTableEngine"></td>
					</tr>
					<tr>
						<th>申请时间</th>
						<td id="dbTableApplyTime"></td>
					</tr>
				</table>
			</div>
		</div>
		<div class="col-xs-12 col-sm-12 col-md-7 column">
			<ul id="myTab" class="nav nav-tabs">
			   <li class="active">
			      <a class="hidden-480" href="#create_on_cluster" data-toggle="tab">在已有RDS集群创建</a>
			      <a class="hidden-md hidden-lg" href="#create_on_cluster" data-toggle="tab">已有集群</a>
			   </li>
			   <li>
			   	  <a class="hidden-480" href="#create_on_new_cluster" data-toggle="tab">在新RDS集群创建</a>
			   	  <a class="hidden-md hidden-lg" href="#create_on_new_cluster" data-toggle="tab">新集群</a>
			   </li>
			   <li>
			   	  <a href="#disagree" data-toggle="tab">不同意</a>
			   </li>
			</ul>					
			<div id="myTabContent" class="tab-content" >
				<div class="tab-pane fade in active" id="create_on_cluster" style="margin-top: 45px;margin-bottom: 45px;">
					<form id="create_on_old_cluster_form" class="form-horizontal" role="form">
						<input type="text" class="form-control hide" id="dbId" name="dbId" value="${dbId}"/>
						<input type="text" class="form-control hide" value="2" id="auditType" name="auditType"/>
						<div class="form-group">
							<label class="col-xs-12 col-sm-12 col-md-4 control-label" for="mclusterOption">选择RDS集群</label>
							<div class="col-xs-12 col-sm-12 col-md-6">
								<select class="form-control" id="mclusterOption" name="mclusterId" data-placeholder="请选择集群...">
									<option value="">  </option>
								</select>
							</div>
							<div class="col-xs-12 col-sm-12 col-md-2">
								<button id="create_on_old_mcluster_botton" type="submit" class="btn btn-sm btn-info">创建</button>
							</div>
						</div>
					</form>
			   </div>
				<div class="tab-pane fade" id="create_on_new_cluster" style="margin-top: 45px;margin-bottom: 45px;">
					<form class="form-horizontal" id="create_on_new_cluster_form">
						<input type="text" class="form-control hide" id="dbId" name="dbId" value="${dbId}"/>
						<input type="text" class="form-control hide" value="2" id="auditType" name="auditType"/>
						<div class="form-group">
							<label for="text" class="control-label col-xs-12 col-sm-12 col-md-3 ">RDS集群名称</label>
							<div class="col-xs-12 col-sm-12 col-md-6 ">
								<input class="form-control" name="mclusterName" id="mclusterName" type="text"/>
							</div>
							<div class="col-xs-12 col-sm-12 col-md-3 ">
								<button id="create-mcluster-botton" type="submit" class="btn btn-sm btn-info">创建</button>
							</div>
						</div>
					</form>
				</div>
				<div class="tab-pane fade" id="disagree">
					<div>
				      <form class="form-horizontal" id="refuse_create_mcluster" role="form">
							<input type="text" class="form-control hide" id="dbId" name="dbId" value="${dbId}"/>
							<input type="text" class="form-control hide" value="4" id="auditType" name="auditType"/>
						  	<div class="form-group">
						  		<div class="col-sm-12">
						      	<textarea  id="auditInfo" name="auditInfo" class="form-control" rows="3" placeholder="请输入未通过原因"></textarea>
						  		</div>
						  		<div class="col-sm-12">
						  			<button type="button" class="btn btn-sm btn-info pull-right" onclick="refuseCreateMcluster()" style="margin-top:10px">确定</button>
						  		</div>
						  	</div>
					  </form>
				  </div>
			   </div>
			</div>
		</div>
	</div>
</div>
<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />

<!-- <link rel="stylesheet" href="../assets/css/ace.min.css" id="main-ace-style" />  -->
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>
<script src="${ctx}/static/scripts/pagejs/db_audit.js"></script>

