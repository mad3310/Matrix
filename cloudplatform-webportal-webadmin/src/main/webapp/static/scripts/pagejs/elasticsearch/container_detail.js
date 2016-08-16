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
		url:"/es/container/"+$("#containerId").val(),
		dataType : "json", 
		success : function(data) {

			removeLoading();
			if(error(data)) return;
			var tempObj = data.data;
					
			var tby = $("#tby");
			
 			var recordsArray=[];

			var containerId="<input name='container_id' value='"+tempObj.id+"' type='hidden' />";
			var containerName="<td>"+tempObj.containerName+"</td>";
			var hostIp="<td class='hidden-480'>"+tempObj.hostIp+"</td>";
			var ipAddr="<td>"+tempObj.ipAddr+"</td>";
			if(tempObj.mountDir != null){
				jsonStr = tempObj.mountDir.substring(1,tempObj.mountDir.length-1);
				jsonArr = jsonStr.split(",");
				var mountDirStr = "";
				var jsonArrLen=jsonArr.length;
				for (var j = 0; j < jsonArrLen; j++){						
					mountDirStr += jsonArr[j]+"<br/>";					
				}
				var mountDir="<td class='hidden-480'>"+mountDirStr+"</td>";
			}else{
				var mountDir="<td class='hidden-480'>-</td>";
			}
			if(tempObj.zookeeperIp != null){
				var zookeeperIp="<td class='hidden-480'>"+tempObj.zookeeperIp+"</td>";
			}else{
				var zookeeperIp="<td class='hidden-480'>-</td>";
			}

			var status="<td>"+esStateTransform(tempObj.status)+"</td>";
]	
			
			recordsArray.push("<tr>",containerId,containerName,hostIp,ipAddr,mountDir,zookeeperIp,status,"</tr>");
			tby.append(recordsArray.join(''));
			
			/*初始化tooltip*/
			$('[data-toggle = "tooltip"]').tooltip();
		}
	});
}

