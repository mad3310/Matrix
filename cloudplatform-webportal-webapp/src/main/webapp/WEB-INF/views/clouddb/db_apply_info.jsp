<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/common/common.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>数据库申请详情</title>

</head>
<body>
	<div class="container">
		<%@include file="/common/header.jsp"%>
		<div id="wrap">
			<div class="row">
				<div class="col-md-12">
					<h3 class="text-left">DB申请内容</h3>
				</div>
				<hr
					style="FILTER: alpha(opacity = 0, finishopacity = 100, style = 1)"
					width="100%" color=#987cb9 SIZE=3></hr>
			</div>

			<div class="row clearfix">
				<div class="col-md-3 column">
					<h2>通告：</h2>
					<p>关于数据库使用的通知、帮助和注意事项。</p>
					<p>
						<a class="btn" href="#">查看详细使用教程 »</a>
					</p>
				</div>
				<div class="col-md-9 column">
					<div class="col-sm-10">
					<table class="table table-bordered" id="db_detail_table" name="db_detail_table">
						
					</table>
					</div>
<!-- 					<div class="col-sm-10">
						<button id="db_apply_modify" type="submit" class="btn btn-default">修改</button>
					</div> -->
				</div>
			</div>
		</div>
		<%@include file="/common/footer.jsp"%>
	</div>
</body>
<script type="text/javascript">
$(function(){
	queryByDbId("fb7241cc-5438-403b-a815-08c5c3ed67aa");
});
function queryByDbId(dbId) {
//	$("#db_detail_table tr").remove();
	$.ajax({ 
		type : "post",
		url : "${ctx}/db/list/dbApplyInfo?belongDb="
				+dbId,
				/* + "&dbName="
				+ $("#dbName").val() */
		dataType : "json", /*这句可用可不用，没有影响*/
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			
			var value = data.data;
			var apply_table = $("#db_detail_table");

			apply_table.append("<tr><td>项目名称</td><td>"+value.applyName+"</td></tr>");
			apply_table.append("<tr><td>链接类型</td><td>"+value.linkType+"</td></tr>");
			apply_table.append("<tr><td>最大访问量</td><td>"+value.maxConcurrency+"</td></tr>");
			apply_table.append("<tr><td>开发语言</td><td>"+value.developLanguage+"</td></tr>");
			apply_table.append("<tr><td>IP访问列表</td><td>"+value.dataLimitIpList+"</td></tr>");
			apply_table.append("<tr><td>管理IP访问列表</td><td>"+value.mgrLimitIpList+"</td></tr>");
			apply_table.append("<tr><td>数据库引擎</td><td>"+value.engineType+"</td></tr>");
			apply_table.append("<tr><td>原数据库名</td><td>"+value.fromDbName+"</td></tr>");
			apply_table.append("<tr><td>原始数据库IP</td><td>"+value.fromDbIp+"</td></tr>");
			apply_table.append("<tr><td>原始数据库port</td><td>"+value.fromDbPort+"</td></tr>");
			apply_table.append("<tr><td>邮件通知</td><td>"+value.isEmailNotice+"</td></tr>");
			apply_table.append("<tr><td>申请时间</td><td>"+value.createTime+"</td></tr>");
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			$('#pageMessage').html("<p class=\"bg-warning\" style=\"color:red;font-size:16px;\"><strong>警告!</strong>"+errorThrown+"</p>").show().fadeOut(3000);
		}
	});
}
</script>
</html>
