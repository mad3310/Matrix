<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/common/common.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>管理员数据库审核</title>

</head>
<body>
	<div class="container">
		<div id="wrap">
			<div class="row">
				<div class="col-md-12">
					<h3 class="text-left">DB审核</h3>
				</div>
				<hr style="FILTER: alpha(opacity = 0, finishopacity = 100, style = 1)" width="100%" color=#987cb9 SIZE=3></hr>
			</div>
			<input type="text" class="form-control hide" id="clusterId" name="clusterId"/>

			<div class="row clearfix">
				<div class="col-md-5 column">
					<div class="col-sm-12">
					<table class="table table-bordered" id="db_detail_table">	
						<caption>用户申请单</caption>
					</table>
					</div>
				</div>
				<div class="col-md-7 column">
					<ul id="myTab" class="nav nav-tabs">
					   <li class="active">
					      <a href="#create_on_cluster" data-toggle="tab">在已有集群创建</a>
					   </li>
					   <li>
					   	  <a href="#create_on_new_cluster" data-toggle="tab">在新集群创建</a>
					   </li>
					   <li>
					   	  <a href="#disagree" data-toggle="tab">不同意</a>
					   </li>
					</ul>					
					<div id="myTabContent" class="tab-content" >
						<div class="tab-pane fade in active" id="create_on_cluster" style="margin-top: 50px;">
						<form class="form-horizontal" role="form" action="${ctx}/db/toMgrAudit/save">
							<input type="text" class="form-control hide" id="applyCode" name="applyCode"/>
							<input type="text" class="form-control hide" id="dbId" name="dbId"/>
							<input type="text" class="form-control hide" value="1" id="auditType" name="auditType"/>
							<input type="text" class="form-control hide" id="dbApplyStandardId" name="dbApplyStandardId"/>
						  <div class="form-group">
						    <label for="text" class="col-sm-2 control-label">选择集群</label>
						    <div class="col-sm-8">
						      <select id="mclusterOption" name="mclusterId" class="form-control">
									<option value=""></option>
						        <c:forEach var="mcluster" items="${mclusterList}">
									<option value="${mcluster.id}">${mcluster.mclusterName}</option>
								</c:forEach>
						      </select>
						    </div>
						    <div class="col-sm-2">
						      <button type="submit" class="btn btn-primary">创建</button>
						    </div>
						  </div>
						</form>
					   </div>
					   <div class="tab-pane fade" id="create_on_new_cluster">
							<form id="demoform" method="post" action="${ctx}/db/toMgrAudit/save">
								<input type="text" class="form-control hide" id="applyCode" name="applyCode"/>
								<input type="text" class="form-control hide" id="dbId" name="dbId"/>
								<input type="text" class="form-control hide" id="dbApplyStandardId" name="dbApplyStandardId"/>
								<input type="text" class="form-control hide" value="2" id="auditType" name="auditType"/>
					            <select multiple="multiple" size="10" name="hostIds" id="duallistbox" style="height: 256px;">
								<c:forEach var="host" items="${hostList}">
									<option value="${host.id}">${host.hostName}(${host.hostIp})</option>
								</c:forEach>
					            </select>
				          <button type="submit" class="btn btn-primary pull-right" style="margin-top: 10px;">创建</button>				     
				          </form>
					   </div>
					   <div class="tab-pane fade" id="disagree">

					      <form role="form" method="post" action="${ctx}/db/toMgrAudit/save">
							  <input type="text" class="form-control hide" value="-1" id="auditType" name="auditType"/>
								<input type="text" class="form-control hide" id="applyCode" name="applyCode"/>
								<input type="text" class="form-control hide" id="dbId" name="dbId"/>
								<input type="text" class="form-control hide" id="dbApplyStandardId" name="dbApplyStandardId"/>
							  <div class="form-group form-group-lg">
							    <label class="control-label" for="formGroupInputLarge">原因</label>
							    <div>
							      <textarea id="disagreeForm" name="disagreeForm" class="form-control" rows="12" placeholder="请输入未通过原因"></textarea>
							    </div>
							  </div>
							  <button type="submit" class="btn btn-primary pull-right">确定</button>
						  </form>
					   </div>
					   </div>
				</div>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
$(function(){
	var dbId = request("dbId");
	queryByDbId(dbId);
	hostDualListBox();
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
			$("input[name='clusterId']").val(value.clusterId);
			$("input[name='dbId']").val(value.belongDb);
			$("input[name='applyCode']").val(value.applyCode);
			$("input[name='dbApplyStandardId']").val(value.id);
			apply_table.append("<tr><td>项目名称</td><td>"+value.applyName+"</td></tr>");
			apply_table.append("<tr><td>业务描述</td><td>"+value.descn+"</td></tr>");
			apply_table.append("<tr><td>链接类型</td><td>"+value.linkType+"</td></tr>");
			apply_table.append("<tr><td>最大访问量</td><td>"+value.maxConcurrency+"/s</td></tr>");
			apply_table.append("<tr><td>读写比例</td><td>"+value.readWriterRate+"</td></tr>");
			apply_table.append("<tr><td>开发语言</td><td>"+value.developLanguage+"</td></tr>");
			apply_table.append("<tr><td>IP访问列表</td><td>"+value.dataLimitIpList+"</td></tr>");
			apply_table.append("<tr><td>管理IP访问列表</td><td>"+value.mgrLimitIpList+"</td></tr>");
			apply_table.append("<tr><td>数据库引擎</td><td>"+value.engineType+"</td></tr>");
			apply_table.append("<tr><td>邮件通知</td><td>"+translateStatus(value.isEmailNotice)+"</td></tr>");
			apply_table.append("<tr><td>申请时间</td><td>"+value.createTime+"</td></tr>");
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			$('#pageMessage').html("<p class=\"bg-warning\" style=\"color:red;font-size:16px;\"><strong>警告!</strong>"+errorThrown+"</p>").show().fadeOut(3000);
		}
	});
}

function translateStatus(status){
	if(status = 1)
	{
		return "是";
	}else
	{
		return "否";
	}	
}

function request(paras)
{ 
    var url = location.href; 
    var paraString = url.substring(url.indexOf("?")+1,url.length).split("&"); 
    var paraObj = {} 
    for (i=0; j=paraString[i]; i++){ 
    paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length); 
    } 
    var returnValue = paraObj[paras.toLowerCase()]; 
    if(typeof(returnValue)=="undefined"){ 
    return ""; 
    }else{ 
    return returnValue; 
    } 
}

function hostDualListBox()
{
	 var demo1 = $('select[name="hostIds"]').bootstrapDualListbox();
     $("#demoform").submit(function() {
		alert($('select[name="hostIds"]').val()));  
     	return false;
     });		
}
</script>
</html>
