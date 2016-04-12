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
		url : "/container/"+$("#mclusterId").val(),
		dataType : "json", 
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var array = data.data;
			var array_len=array.length;
			var tby = $("#tby");
			if($('#mclusterTitle').html().indexOf(array[0].mcluster.mclusterName) < 0){
 				$("#mclusterTitle").prepend(array[0].mcluster.mclusterName);
			}
 			$("#headerContainerName").append(array[0].mcluster.mclusterName);
 			var recordsArray=[];
			for (var i = 0, len =array_len; i < len; i++) {
				var tempObj=array[i];
				var td0="<input name='container_id' value='"+tempObj.id+"' type='hidden' />";
				var td1="<td>"+tempObj.containerName+"</td>";
				var td2="<td class='hidden-480'>"+tempObj.type+"</td>";
				var td3="<td class='hidden-480'>"+tempObj.hostIp+"</td>";
				var td4="<td>"+tempObj.ipAddr+"</td>";
				if(tempObj.mountDir != null){
					jsonStr = tempObj.mountDir.substring(1,tempObj.mountDir.length-1);
					jsonArr = jsonStr.split(",");
					var mountDir = "";
					var jsonArrLen=jsonArr.length;
					for (var j = 0; j < jsonArrLen; j++){						
						mountDir += jsonArr[j]+"<br/>";					
					}
					var td5="<td class='hidden-480'>"+mountDir+"</td>";
				}else{
					var td5="<td class='hidden-480'>-</td>";
				}
				if(tempObj.zookeeperIp != null){
					var td6="<td class='hidden-480'>"+tempObj.zookeeperIp+"</td>";
				}else{
					var td6="<td class='hidden-480'>-</td>";
				}

				var td7="<td>"+stateTransform(tempObj.status,"rdsMcluster")+"</td>";
				var startHtml=containerOsHtml("rdsContainer","start");
				var stopHtml=containerOsHtml("rdsContainer","stop");
				var compressHtml=containerOsHtml("rdsContainer","compress");
				if(array_len<=4||tempObj.type=="mclustervip"){
					var td8="<td data-status='"+tempObj.status+"'>"
							+"<div class='hidden-sm hidden-xs action-buttons'>"
							+startHtml+stopHtml
							+"</div>"
							+"<div class='hidden-md hidden-lg'>"
								+"<div class='inline pos-rel'>"
									+"<button class='btn btn-minier btn-yellow dropdown-toggle' data-toggle='dropdown' data-position='auto'>"
										+"<i class='ace-icon fa fa-caret-down icon-only bigger-120'></i>"
									+"</button>"
								+"</div>"
								+"<ul class='dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close'>"
									+"<li>"+startHtml+"</li>"
									+"<li>"+stopHtml+"</li>"
								+"</ul>"
							+"</div>"
							+"</td>";
				}else{
					var td8="<td data-status='"+tempObj.status+"'>"
							+"<div class='hidden-sm hidden-xs action-buttons'>"
							+startHtml+stopHtml+compressHtml
							+"</div>"
							+"<div class='hidden-md hidden-lg'>"
								+"<div class='inline pos-rel'>"
									+"<button class='btn btn-minier btn-yellow dropdown-toggle' data-toggle='dropdown' data-position='auto'>"
										+"<i class='ace-icon fa fa-caret-down icon-only bigger-120'></i>"
									+"</button>"
								+"</div>"
								+"<ul class='dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close'>"
									+"<li>"+startHtml+"</li>"
									+"<li>"+stopHtml+"</li>"
									+"<li>"+compressHtml+"</li>"
								+"</ul>"
							+"</div>"
							+"</td>";
				}
				
				recordsArray.push("<tr>",td0,td1,td2,td3,td4,td5,td6,td7,td8,"</tr>");
			}
			tby.append(recordsArray.join(''));
			/*初始化tooltip*/
			$('[data-toggle = "tooltip"]').tooltip();
		}
	});
}

function startContainer(obj){
	var _target=$(obj);
	var status=_target.parents("td").attr('data-status');
	if(!containerClusterOs(status,"rdsContainer","start")){
		warn("该集群所处状态不可被启动",3000);
		return 0;
	}
	function startCmd(){
		var containerId =_target.parents("tr").find('[name="container_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/container/start',
			type:'post',
			data:{containerId : containerId},
			success:function(data){
				removeLoading();
				if(error(data)) return;
				queryContainer();
			}
		});
	}
	confirmframe("启动container","启动container大概需要几分钟时间!","请耐心等待...",startCmd);
}
function stopContainer(obj){
	var _target=$(obj);
	var status=_target.parents("td").attr('data-status');
	if(!containerClusterOs(status,"rdsContainer","stop")){
		warn("该集群所处状态不可被停止",3000);
		return 0;
	}
	function stopCmd(){
		var containerId =_target.parents("tr").find('[name="container_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/container/stop',
			type:'post',
			data:{containerId : containerId},
			success:function(data){
				removeLoading();
				if(error(data)) return;
				queryContainer();
			}
		});
	}
	confirmframe("关闭container","关闭container将不能提供服务,再次启动需要十几分钟!","您确定要关闭?",stopCmd);
}
function compressContainer(obj){
	var _target=$(obj);
	var status=_target.parents("td").attr('data-status');
	if(!containerClusterOs(status,"rdsContainer","compress")){
		warn("该集群所处状态不可被缩容",3000);
		return 0;
	}
	function compressCmd(){
		var containerId =_target.parents("tr").find('[name="container_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/container/'+containerId,
			type:'delete',
			success:function(data){
				removeLoading();
				if(typeof(data) == 'string'){
					data = JSON.parse(data)
				};
				if(error(data)) return;
				queryContainer();
			}
		});
	}
	confirmframe("删除container","删除container,缩容container集群!","您确定要删除?",compressCmd);
}
