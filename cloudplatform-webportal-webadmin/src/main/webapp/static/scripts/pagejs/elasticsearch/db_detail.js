$(function(){
	//隐藏搜索框
	$('#nav-search').addClass("hidden");
	queryContainer();
})
function queryContainer(){
	$("#tby tr").remove();
	getLoading();

	$.ajax({ 
		cache:false,
		type : "get",
		url:"/es/"+$("#id").val(),
		dataType : "json", 
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var tempObj = data.data;
					
			var tby = $("#tby");
			
 			var recordsArray=[];

 			var dbName = "<td>"+tempObj.esName+"</td>";
 			var memorySize = "<td>"+TransUnit(tempObj.memorySize)+"</td>";
 			var storageSize = "<td>"+TransUnit(tempObj.storageSize)+"</td>";
 			var nodeCount = "<td>"+tempObj.nodeCount+"</td>";
 			var createTime ="<td>"+date('Y-m-d H:i:s',tempObj.createTime)+"</td>";
 			var status="<td>"+esStateTransform(tempObj.status)+"</td>";			
			
			recordsArray.push("<tr>",dbName,memorySize,storageSize,nodeCount,createTime,status,"</tr>");
			tby.append(recordsArray.join(''));
			
			/*初始化tooltip*/
			$('[data-toggle = "tooltip"]').tooltip();
		}
	});
}

