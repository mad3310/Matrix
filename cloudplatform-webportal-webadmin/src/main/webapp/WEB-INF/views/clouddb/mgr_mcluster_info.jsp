<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<div class="col-md-12">
		<h3 class="text-left">Mcluster详情</h3>
	</div>
	<hr
		style="FILTER: alpha(opacity = 0, finishopacity = 100, style = 1)"
		width="100%" color=#987cb9 SIZE=3></hr>
</div>

<div class="row clearfix">
	<div class="col-md-2 column">
		<h2>提示：</h2>
		<p>管理员在修改Mcluster信息后，请联系我们及时更新。</p>
<!-- 		<p>
			<a class="btn" href="#">查看详细使用教程 »</a>
		</p> -->
	</div>
	<div class="col-md-10 column">
		<div class="col-md-12 column">
		<button id="goBack" name="goBack" type="button" class="btn btn-default">返回</button>
		<table class="table table-bordered" id="Mcluster_detail_table" name="Mcluster_detail_table" style="margin-top: 10px;">
			<thead>
		      <tr>
		         <th>container名称</th>
		         <th>类型</th>
		         <th>宿主机</th>
		         <th>ip</th>
		         <th>挂载路径</th>
		         <th>zookeepId</th>
		         <th>状态</th>
		      </tr>
		   </thead>
		   <c:forEach var="container" items="${containers}">
			 <tr>
			 	<td>${container.containerName}</td>
			 	<td>${container.type}</td>
			 	<td>${container.hostId}</td>
			 	<td>${container.ipAddr}</td>
<%-- 			 	<td>${container.gateAddr}</td>
			 	<td>${container.ipMask}</td> --%>
			 	<td>${container.mountDir}</td>
			 	<td>${container.zookeeperId}</td>
			 	<td>正常</td>
<%-- 			 	<c:set var="status" value="${container.status}"/>
			 	<c:if test="${status == 0 || status == null}">
			 		<td>正常</td>
			 	</c:if> --%>
			 	
			 </tr>
		   </c:forEach>
		</table>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	var dbId = request("belongDb");
	initPage();
});

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

function translateStatus(status){
	if(status = 1)
	{
		return "是";
	}else{
		return "否";
	}
}

function initPage(){
	$("#goBack").click(function() {
		location.href = "${ctx}/mcluster/list";
	});
}
</script>
