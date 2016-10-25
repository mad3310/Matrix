<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>991){//md&&lg
			$('.queryOption').removeClass('collapsed');
		}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>991){//md&&lg
			$('.queryOption').removeClass('collapsed');
		}
	});
</script>
<div class="page-content-area">
	<div class="row">
		<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
			<div class="widget-header hidden-md hidden-lg">
				<h5 class="widget-title">数据库用户查询条件</h5>
				<div class="widget-toolbar">
					<a href="#" data-action="collapse">
						<i class="ace-icon fa fa-chevron-down"></i>
					</a>
				</div>
			</div>
			<div class="widget-body">
				<div class="page-header col-sm-12 col-xs-12 col-md-12">
					<!-- <h3>数据库用户列表</h3> -->
				    <div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
						<form class="form-inline">
							<div class="form-group col-sm-6 col-xs-12 col-md-2">
								<input type="text" class="form-control" id="userName" placeholder="用户名">
							</div>
							<div class="form-group col-sm-6 col-xs-12 col-md-2">
								<input type="text" class="form-control" id="userDb" placeholder="所属数据库">
							</div>
							<div class="form-group col-sm-6 col-xs-12 col-md-2">
								<!-- <input type="text" class="form-control" id="userAuthority" placeholder="所属Mcluster"> -->
								<select class="form-control" id="userAuthority">
									<option value="">请选择用户权限</option>
									<option value="1">管理员</option>
									<option value="2">只读用户</option>
									<option value="3">读写用户</option>
								</select>
							</div>
							<!-- <div class="form-group col-sm-6 col-xs-12 col-md-2">
								<input type="text" class="form-control" id="userIp" placeholder="ip地址">
							</div> -->
							<!-- <div class="form-group">
								<select class="form-control" id="userLimit">
									<option value="">频次限制</option>
									<option value="">50</option>
									<option value="">100</option>
									<option value="">。。。</option>
								</select>
							</div> -->
							<div class="form-group col-sm-6 col-xs-12 col-md-2">
								<select class="form-control" id="dbuserStatus">
									<option value="">请选择状态</option>
								</select>
							</div>
							<div class="form-group  col-sm-6 col-xs-12 col-md-3" style="padding-right:0;">
							<button class="btn btn-sm btn-primary btn-search" id="dbuserSearch" type="button">
								<i class="ace-icon fa fa-search"></i>搜索
							</button>
							<button class="btn btn-sm " type="button" id="dbuseClearSearch">清空</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div id='accountList' class=''>
			<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
				<div class="widget-header">
					<h5 class="widget-title">数据库用户列表</h5>
					<div class="widget-toolbar no-border">
						<!-- <button id="create_db_user" class="btn btn-white btn-primary btn-xs toCreateAccount hidden-xs" type="button">  onclick="buildUser()"
							<i class="ace-icont fa fa-plus"></i>
							 创建用户
						</button>
						<button class="btn btn-info btn-xs toCreateAccount hidden-sm hidden-md hidden-lg"><i class="fa fa-plus-square"></i> </button> -->
					</div>
				</div>
				<div class="widget-body">
					<div class="widget-main no-padding">
						<table class="table table-bordered" id="db_detail_table" >
							<thead>
								<tr>
									<th class="center">
										<label class="position-relative">
											<input type="checkbox" id="titleCheckbox" class="ace" /><span class="lbl"></span>
										</label>
									</th>
									<th>用户名</th>
									<th>用户类型</th>
									<!-- <th>所属数据库</th><th>用户权限</th>
									<th>ip地址</th> -->
									<th class="hidden-xs">最大并发量</th>
									<th>当前状态</th>
									<th class="hidden-xs">所属数据库</th>
									<th class="hidden-xs">备注</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody id="tby"></tbody>
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
</div>
<!-- 点击“创建账号”后加载的div 去掉hide既可以显示此div-->
<div id="newAccountTab" class="hide" role="tablist" aria-multiselectable="true">
	<!-- heading部分 -->
	<div class="se-heading">
		<div class="pull-left">
			<h5>创建新账号</h5>
			<a class="toAccountList">返回数据库用户列表</a>
		</div>
	</div>
	<!-- 内容部分，由一个form承载 -->
	<div class="">
		<form id="db_user_create_form" role="form" class="form-horizontal" name="account_modify_form">
			<!-- 数据库账号模块 -->
			<div class="form-group has-feedback">
				<label class="col-xs-12 col-sm-2 control-label"><span class="text-danger">*</span>数据库帐号：</label>
				<div class="">						
					<div class="col-xs-12 col-sm-4">							
						<input name="username" class="form-control input-radius-2" type="text"></div>
					<div class="col-xs-12 col-sm-offset-2 col-sm-10 notice-block">
						<p class="text-correct">由小写字母，数字、下划线组成、字母开头，字母或数字结尾，最长16个字符</p>
					</div>
				</div>
			</div>
			<!-- 数据库账号模块end -->
			<!-- 授权数据库模块 -->
			<div class="form-group multi-select">
				<label class="col-xs-12 col-sm-2 control-label"><span class="text-danger">*</span>授权IP：</label>
				<div class="inline-block mcluster-select col-xs-10 col-sm-2">
					<div class="select-head clearfix">
						<p class="inline-block">未授权IP</p>
						<p class="inline-block pull-right">
							<a id="manager-ip-list">管理IP名单</a>
						</p>
					</div>
					<div class="select">
						<ul class="select-list select-list-left"></ul>
						<!-- 没有数据记录时显示暂无数据 -->
						<div class="select-msg hide"><span class="text-muted">暂无数据</span></div>
					</div>
				</div>
				<div class="inline-block col-xs-2 col-sm-1 m-authorize">
					<div style="margin-bottom:5px"><a class="btn_db_add">授权&nbsp;&gt;</a></div>
					<div><a class="btn_db_remove">&lt;&nbsp;移除</a></div>
				</div>
				<div class="inline-block mcluster-select col-xs-12 col-sm-5 col-md-4">
					<div class="select-head clearfix">
						<p class="inline-block">已授权IP</p>
						<p class="inline-block pull-right">
							<span style="padding-right: 5px;color:#bbb">权限</span>
							<a class="select-all-rw">全部设读写</a>
						</p>
					</div>
					<div class="select">
						<div class="select-wrap">
							<ul class="select-list select-list-right"></ul>
							<!--无数据时显示暂无数据 -->
							<div class="select-msg hide"><span class="text-muted">暂无数据</span></div>
						</div>
					</div>
				</div>
			</div>
			<!-- 授权数据库模块end -->
			<!-- 密码输入模块 -->
			<div class="hide form-group">
				<label class="col-sm-2 control-label"><span class="text-danger">*</span>读写比例：</label>
				<div class="col-sm-8 row">
					<!-- 密码输入框 -->
					<div class="col-sm-4">
						<input name="readWriterRate" class="form-control input-radius-2"/>
					</div>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"><span class="text-danger">*</span>最大并发量：</label>
				<div class="">
					<!-- 密码输入框 -->
					<div class="col-xs-12 col-sm-4">
						<input name="maxConcurrency" class="form-control input-radius-2"/>
					</div>
					<div class="col-sm-1 hidden-xs" style="padding-top : 8px">
						<a data-container="body" data-toggle="popover" data-placement="right" data-html="true"
							data-content="
							<table border='1' style='width:100%;font-size:12px;color:gray;border:1px solid gray;'>
								<tr>
								<th>并发量</th>
								<td>maxConcurrency</td>
							</tr>
							<tr>
								<th>MAX_QUERIES_PER_HOUR</th>
								<td>maxConcurrency*7200</td>
							</tr>
							<tr>
								<th>MAX_UPDATES_PER_HOUR</th>
								<td>maxConcurrency*3600</td>
							</tr>
							<tr>
								<th>MAX_CONNECTIONS_PER_HOUR</th>
								<td>maxConcurrency*7200</td>
							</tr>
							<tr>
								<th>MAX_USER_CONNECTIONS</th>
								<td>maxConcurrency</td>
							</tr>
							</table>
							">
							<span>查看计算公式</span>
						</a>	
					</div>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"><span class="text-danger">*</span>密码：</label>
				<div class="">
					<!-- 密码输入框 -->
					<div class="col-xs-12 col-sm-4">
						<input name="newPwd1" class="form-control input-radius-2" type="password"/>
					</div>
					<!-- 密码规则提示 -->
					<div class="col-xs-12 notice-block col-sm-10 col-sm-offset-2">
						<p class="">由字母、数字或特殊字符如：@#$%^&*!~_- 组成，长度6~32位</p>
					</div>
				</div>
			</div>
			<!-- 密码输入模块end -->
			<!-- 确认密码模块 -->
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"><span class="text-danger">*</span>确认密码：</label>
				<div class="">
					<div class="col-xs-12 col-sm-4">
						<input name="newPwd2" class="form-control input-radius-2" type="password"/>
					</div>
				</div>
			</div>
			<!-- 密码确认模块end -->
			<!-- 备注说明模块 -->
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label">备注说明：</label>
				<div class="">
					<div class="col-xs-12 col-sm-4">
						<textarea name="descn" class="form-control input-radius-2" style="width:100%;height:90px"></textarea>
					</div>
					<div class="col-sm-8 help-info hide">
						<small class="text-danger" >
							<span class="glyphicon glyphicon-remove-sign"></span>
							备注说明最多256个字符
						</small>
					</div>
					<div class="col-sm-10 col-sm-offset-2 col-xs-12 notice-block">
						<p class="text-correct">请输入备注说明，输入长度不超过100个字符!</p>
					</div>
				</div>
			</div>
			<!-- 备注说明模块end -->
			<!-- 按钮模块 -->
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"></label>
				<div class="col-xs-12 col-sm-4">
					<button type="submit" id="submitCreateUserForm" class="btn btn-primary">提交</button>
					<button type="button" class="btn btn-default toAccountList">返回</button>
				</div>
			</div>
			<!-- 按钮模块end -->
		</form>
	</div>
</div>
<div id="modifyAccountTab" class="hide" role="tablist" aria-multiselectable="true">
	<!-- heading部分 -->
	<div class="se-heading">
		<div class="pull-left">
			<h5>编辑账号</h5>
			<a class="toAccountList">返回数据库用户列表</a>
		</div>
	</div>
	<!-- 内容部分，由一个form承载 -->
	<div>
		<form id="db_user_modify_form" role="form" class="form-horizontal" name="account_modify_form">
			<input class="hidden" id="modifydbUserReadWriterRate" type="text" />

			<!-- 数据库账号模块 -->
			<div class="form-group">
				<label class="col-xs-6 col-sm-2 control-label">
					数据库帐号：
				</label>
				<label id="modifyFormDbUsername" class="col-xs-6 col-sm-4 control-label"></label>
				<div class="">
					<div class="col-xs-6 col-sm-4">
						<label id="modifyFormDbUsername" style="padding-top:7px;"></label>
					</div>
				</div>
			</div>
			<!-- 数据库账号模块end -->
			<!-- 授权数据库模块 -->
			<div class="form-group modify-multi-select">
				<label class="col-xs-12 col-sm-2 control-label">
					<span class="text-danger">*</span>
					授权IP：
				</label>
				<div class="inline-block mcluster-select col-xs-10 col-sm-2">
					<div class="select-head clearfix">
						<p class="pull-left">未授权IP</p>
					</div>
					<div class="select">
						<ul class="select-list select-list-left">
						</ul>
						<!-- 没有数据记录时显示暂无数据 -->
						<div class="select-msg mc-hide">
							<span class="text-muted">暂无数据</span>
						</div>
					</div>
				</div>
				<div class="inline-block col-xs-2 col-sm-1 m-authorize">
					<div style="margin-bottom:5px">
						<a class="btn_db_add">授权&nbsp;&gt;</a>
					</div>
					<div>
						<a class="btn_db_remove">&lt;&nbsp;移除</a>
					</div>
				</div>
				<div class="inline-block mcluster-select col-xs-12 col-sm-5 col-md-4">
					<div class="select-head clearfix">
						<p class="pull-left">已授权IP</p>
						<p class="pull-right">
							<span style="padding-right: 5px;color:#bbb">权限</span>
							<a class="select-all-rw">全部设读写</a>
						</p>
					</div>
					<div class="select">
						<div class="select-wrap">
							<ul class="select-list select-list-right">
							</ul>
							<!--无数据时显示暂无数据 -->
							<div class="select-msg mc-hide">
								<span class="text-muted">暂无数据</span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"> <span class="text-danger">*</span> 最大并发量：
				</label>
				<div class="">
					<!-- 密码输入框 -->
					<div class="col-xs-12 col-sm-4">
						<input id="modifydbUserMaxConcurrency" name="modifydbUserMaxConcurrency" class="form-control input-radius-2" />
					</div>
					<div class="col-sm-1 hidden-xs" style="padding-top : 8px">
						<a data-container="body" data-toggle="popover" data-placement="right" data-html="true"
							data-content="
							<table border='1' style='width:100%;font-size:12px;color:gray;border:1px solid gray;'>
								<tr>
								<th>并发量</th>
								<td>maxConcurrency</td>
							</tr>
							<tr>
								<th>MAX_QUERIES_PER_HOUR</th>
								<td>maxConcurrency*7200</td>
							</tr>
							<tr>
								<th>MAX_UPDATES_PER_HOUR</th>
								<td>maxConcurrency*3600</td>
							</tr>
							<tr>
								<th>MAX_CONNECTIONS_PER_HOUR</th>
								<td>maxConcurrency*7200</td>
							</tr>
							<tr>
								<th>MAX_USER_CONNECTIONS</th>
								<td>maxConcurrency</td>
							</tr>
							</table>
							">
							<span>查看计算公式</span>
						</a>	
					</div>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"> <span class="text-danger">*</span> 密码：
				</label>
				<div class="">
					<!-- 密码输入框 -->
					<div class="col-xs-12 col-sm-4">
						<input name="modifyFormNewPwd1" id="modifyFormNewPwd1" class="form-control input-radius-2" type="password" />
					</div>
					<!-- 密码规则提示 -->
					<div class="col-xs-12 notice-block col-sm-10 col-sm-offset-2">
						<p class="">由字母、数字或特殊字符如：@#$%^&*!~_- 组成，长度6~32位</p>
					</div>
				</div>
			</div>
			<!-- 密码输入模块end -->
			<!-- 确认密码模块 -->
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"> <span class="text-danger">*</span> 确认密码：
				</label>
				<div class="">
					<!-- 确认密码输入框 -->
					<div class="col-xs-12 col-sm-4">
						<input name="modifyFormNewPwd2" id="modifyFormNewPwd2" class="form-control input-radius-2" type="password" />
					</div>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label">备注说明：</label>
				<div class="">
					<div class="col-xs-12 col-sm-4">
						<label id="modifyFormDbDesc" style="padding-top:7px"></label>
					</div>
				</div>
			</div>
			<!-- 按钮模块 -->
			<div class="form-group">
				<label class="col-xs-12 col-sm-2 control-label"></label>
				<div class="col-xs-12 col-sm-4">
					<button type="submit" id="submitModifyUserForm" class="btn btn-primary">提交</button>
					<button type="button" class="btn btn-default toAccountList">返回</button>
				</div>
			</div>
			<!-- 按钮模块end -->
		</form>
	</div>
</div>
<!-- 新添创建用户，编辑权限end -->
<!-- new add by gm 2015-7-1 -->
<div id="showDbuserIpPrivilege" class="modal fade in">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
				</button>
				<h5 id="showDbuserIpPrivilegeTitle" class="modal-title"></h5>
			</div>
			<div class="modal-body">
				<div class="table-responsive">
					<table id="dbuser-list-ip-privilege-table" class="table table-bordered table-striped">
						<thead>
							<tr>
								<th>IP
								</td>
								<th>权限
								</td>
							</tr>
						</thead>
						<tbody id="ip-privilege-tby">
						</tbody>
					</table>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>

<div id="reset-password-box" class="modal">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
				</button>
				<h5 id="reset-password-box-title" class="modal-title"></h5>
			</div>
			<form id="reset-password-form" role="form" class="form-horizontal">
				<input id="reset-password-username" class="hidden" name="usename" type="text"/>
				<div class="modal-body">
					<div id="reset-password-box-text">
						<div class="form-group">
							<label class="col-sm-4 control-label">密码： </label>
							<div class="col-sm-8 row">
								<div class="col-sm-12">
									<input name="reset-password" class="form-control input-radius-2" type="password" />
								</div>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label">确认密码： </label>
							<div class="col-sm-8 row">
								<div class="col-sm-12">
									<input name="reset-password-repeat" class="form-control input-radius-2" type="password" />
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="resetPasswordBoxSubmit" type="submit" class="btn btn-primary">确定</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!--确认对话框-->
<div id="dialog-box" class="modal">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
		<!--	<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span><span class="sr-only">关闭</span>
				</button>-->
				<h5 id="dialog-box-title" class="modal-title"></h5>
			</div>
			<div class="modal-body clearfix">
				<div class="col-xs-1 col-sm-1 col-md-1" style="font-size:20px;color:orange;padding-top:5px;">
					<span class="glyphicon glyphicon-exclamation-sign"></span>
				</div>
				<div id="dialog-box-text" class="col-xs-11 col-sm-10 "></div>
			</div>
			<div class="modal-footer">
				<button id="dialogBoxSubmit" type="button" class="btn btn-primary" data-dismiss="modal">确定</button>
				<button id="dialogBoxCancel" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
			</div>
		</div>
	</div>
</div>
<link rel="stylesheet" href="${ctx}/static/styles/bootstrap/bootstrapValidator.min.css" />
<script src="${ctx}/static/scripts/bootstrap/bootstrapValidator.min.js"></script>
<script src="${ctx}/static/scripts/pagejs/db_user_list.js"></script>