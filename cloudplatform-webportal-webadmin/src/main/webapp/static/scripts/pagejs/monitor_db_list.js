 $(function(){
	//初始化
	page_init();
	/*查询功能*/
	$("#monitorDbSearch").click(function(){
		var iw=document.body.clientWidth;
		if(iw>767){//md&&lg
		}else{
			$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
			$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
			var qryStr='';
			var qryStr1=$('#monitorContainer').val();var qryStr2=$('#VipAddress').val();var qryStr3=$("#Physicalcluster").find('option:selected').text();
			if(qryStr1){
				qryStr+='<span class="label label-success arrowed">'+qryStr1+'<span class="queryBadge" data-rely-id="monitorContainer"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr2){
				qryStr+='<span class="label label-warning arrowed">'+qryStr2+'<span class="queryBadge" data-rely-id="VipAddress"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr3){
				qryStr+='<span class="label label-purple arrowed">'+qryStr3+'<span class="queryBadge" data-rely-id="Physicalcluster"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr){
				$('.queryOption').find('.widget-title').html(qryStr);
				$('.queryBadge').click(function(event) {
					var id=$(this).attr('data-rely-id');
					$('#'+id).val('');
					$(this).parent().remove();
					queryMclusterMonitor();
					if($('.queryBadge').length<=0){
						$('.queryOption').find('.widget-title').html('数据库预警列表');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('数据库预警列表');
			}

		}
		queryMclusterMonitor();
	});
	$("#monitorDbClear").click(function(){
		var clearList = ["monitorContainer","Physicalcluster","VipAddress"]
		clearSearch(clearList);
	});
	enterKeydown($(".page-header > .input-group input"),queryMclusterMonitor);
});	
 function queryMclusterMonitor() {
	var mclusterName = $("#monitorContainer").val()?$("#monitorContainer").val():'';
	var hclusterName = $("#Physicalcluster").find('option:selected').attr('data-hclsName')?$("#Physicalcluster").find('option:selected').attr('data-hclsName'):'';
	var vip = $("#VipAddress").val()?$("#VipAddress").val():'';
	var queryCondition={};
	
	$("#tby tr").remove();
	var updateflag = false;
	if(hclusterName) {
		queryCondition = {
				'mclusterName':mclusterName,
				'hclusterName':hclusterName,
				'ipAddr':vip.replace(/\%/g,"%25")
		}
		queryMcluster(queryCondition,true);
		
	} else {
		var hclusters = $("#hclusters").val();
		var hclusterArray = hclusters.split(",");
		for(var i=0;i<hclusterArray.length-1;i++){
			if(!hclusterArray[i]) continue;
			queryCondition = {
					'mclusterName':mclusterName,
					'hclusterName':hclusterArray[i],
					'ipAddr':vip.replace(/\%/g,"%25")
			}
			if(i == (hclusterArray.length-2)) {
				updateflag = true;
			}
			queryMcluster(queryCondition,updateflag);
			
		}
	}
	

}

function queryMcluster(queryCondition,updateflag) {
	$.ajax({ 
		cache:false,
		type : "POST",
		//url : "/monitor/mcluster/list",
		url : queryUrlBuilder("/monitor/mcluster/list/",queryCondition),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		async:false,
		success : function(data) {
			if(error(data)) return;
			var array = data.data;
			var tby = $("#tby");
			
			for (var i = 0, len = array.length; i < len; i++) {
				if(array[i] == null  && array[i].status==0) continue;
				var mclusterName = '';
        		if(array[i].mcluster != undefined && array[i].mcluster != null) {
        			mclusterName = array[i].mcluster.mclusterName;
        		}
        		var tdh1 = "<input name=\"mclusterId\" type=\"hidden\" value=\""+array[i].mclusterId+"\"/>"
        		var tdh2 = "<input name=\"onRestart\" type=\"hidden\" value=\"0\"/>"
				var td1 = $("<td>"
						+ "<a class=\"link\" target=\"_blank\" href=\"/detail/mcluster/" + array[i].mclusterId+"\">"+ mclusterName +"</a>"
						+ "</td>");
				var td2 = $("<td class='hidden-480' name=\"vip\">"
						+ array[i].ipAddr
						+ "</td>");

			    if(array[i].hcluster.hclusterNameAlias){
					var td3 = $("<td class='hidden-480'>"
							+ "<a class=\"link\" href=\"/detail/hcluster/" + array[i].host.hclusterId+"\">"+array[i].hcluster.hclusterNameAlias+"</a>"
							+ "</td>");
				}else{
					var td3 = $("<td class='hidden-480'>"
 							+ "-"
							+ "</td>");
				}
				var td4 = $("<td name=\"mclusterStatus\ status='"+array[i].status+"'>"
							+"<a><i class=\"ace-icon fa fa-spinner fa-spin  bigger-120\"/>数据抓取中...</a>"
							+ "</td>");
				var td5 = $("<td name=\"mclusterControl\">"
						//+ "<a href=\"/monitor/"+array[i].ipAddr+"/db/status\" target=\"_blank\">查看详情</a>"
						+ "<a class=\"link\" href=\"/detail/mcluster/monitor/list/" + array[i].ipAddr + "/3\">查看详情</a>"
						+ "</td>");
				if(array[i].status == 0 ||array[i].status == 5||array[i].status == 13){
					var tr = $("<tr class=\"warning\"></tr>");
				}else if(array[i].status == 3 ||array[i].status == 4||array[i].status == 14){
					var tr = $("<tr class=\"default-danger\"></tr>");
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(tdh1).append(tdh2).append(td1).append(td2).append(td3).append(td4).append(td5).hide();
				tr.appendTo(tby);
			}
			if(updateflag) {
				var tr1 = $("<tr class=\"default-danger normalTag\"></tr>");
				var tr2 = $("<tr class=\"default-danger lightDangerTag\"></tr>");
				var tr3 = $("<tr class=\"default-danger seriousTag\"></tr>");
				var tr4 = $("<tr class=\"default-danger disableClusterTag\"></tr>");
				var tr5 = $("<tr class=\"default-danger timeoutClusterTag\"></tr>");
				var tr6 = $("<tr class=\"default-danger exceptionClusterTag\"></tr>");
				tby.prepend(tr1).prepend(tr2).prepend(tr3).prepend(tr4).prepend(tr5).prepend(tr6);
				updateMclusterStatus();//查询集群状态
			}
		}
	});
}
function getMclusterStatus(ip,obj) {
	var restartButton = $("<span class=\"text-explode\">|</span>" 
							+ "<a data-toggle=\"tooltip\" data-placement=\"top\" title=\"拉起\" style=\"cursor:pointer\" onclick=\"restartMclusterServer(this)\">"
								+"<i class=\"ace-icon fa fa-repeat bigger-120\"></i>"
							+"</a>")
							
	function addNormalButton(){
		$(obj).find('[name="mclusterControl"]').html($(obj).find('[name="mclusterControl"]').find('a').first());
	}
	function addControlButton(){
		$(obj).find('[name="mclusterControl"]').html($(obj).find('[name="mclusterControl"]').find('a').first());
		$(obj).find('[name="mclusterControl"]').append(restartButton);
	}
	
	if($(obj).find('[name = "onRestart"]').val() != 0) return; //如果正在拉起，不更新
	getLoading();
	$.ajax({ 
		cache:false,
		type : "get",
		url : "/monitor/"+ip+"/db/status",
		dataType : "json", 
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var result = data.data.result;
			$(obj).show();
			$(obj).find('[name="mclusterStatus"]').attr("status",result);
			if(result == "0"){
				$(obj).removeClass();
				$(obj).find('[name="mclusterStatus"]').html("<a>正常</a>");
				$(obj).remove();
				//$(obj).parent().find(".normalTag").after($(obj));
				addNormalButton();
			}else if(result == "1"){
				$(obj).removeClass();
				$(obj).find('[name="mclusterStatus"]').html("<a>单节点故障</a>");
				$(obj).addClass("default-danger lightDanger");
				$(obj).parent().find(".lightDangerTag").after($(obj));	
				addControlButton();
			}else if(result == "2"){
				$(obj).removeClass();
				$(obj).find('[name="mclusterStatus"]').html("<a>危险</a>");
				$(obj).addClass("default-danger serious");
				$(obj).parent().find(".seriousTag").after($(obj));
				addControlButton();
			}else if(result == "3"){
				$(obj).removeClass();
				$(obj).find('[name="mclusterStatus"]').html("<a>集群不可用</a>");
				$(obj).addClass("default-danger disableCluster");
				$(obj).parent().find(".disableClusterTag").after($(obj));
				addControlButton();
			}else if(result == "4"){
				$(obj).removeClass();
				$(obj).find('[name="mclusterStatus"]').html("<a>获取数据超时</a>");
				$(obj).addClass("default-danger timeoutCluster");
				$(obj).parent().find(".timeoutClusterTag").after($(obj));
				addControlButton();
			}else if(result == "5"){
				$(obj).removeClass();
				$(obj).find('[name="mclusterStatus"]').html("<a>解析出错，请联系管理员</a>");
				$(obj).addClass("default-danger timeoutCluster");
				$(obj).parent().find(".exceptionClusterTag").after($(obj));
				addControlButton();
			}
			$('[data-toggle = "tooltip"]').tooltip();
		}	
	});
}
function updateMclusterStatus(){
	$("#tby tr").each(function(){
		var ip = $(this).find('[name="vip"]').html();
		if(ip != null){
			var status = getMclusterStatus(ip,$(this));
		}
	});
}

function restartMclusterServer(obj){
	if($(obj).closest('tr').find('[name = "onRestart"]').val() != 0){	//如果正在拉起，不在发送命令。
		warn($(obj).closest("tr").find('td').first().html()+"拉起命令已发出，请等待...",3000);
		return
	}else{
		$(obj).closest('tr').find('[name = "onRestart"]').val(1);
	}
	$(obj).closest('tr').find('[name="mclusterControl"] a:eq(1)').attr('title',"正在拉起，请稍等...").find('i').attr('class',"ace-icon fa fa-spinner fa-spin  bigger-120");//改为刷新状态，并提示正在拉起
	getLoading();
	$.ajax({ 
		cache:false,
		type : "post",
		url : "/mcluster/restart",
		dataType : "json", 
		data : {
			mclusterId:$(obj).closest("tr").find('[name = "mclusterId"]').val()
		},
		success : function(data) {
			removeLoading();
			$(obj).closest('tr').find('[name = "onRestart"]').val(0); //恢复可刷新状态
			if(error(data)){
				$(obj).closest('tr').find('[name="mclusterControl"] a:eq(1)').attr('title',"拉起").find('i').attr('class',"ace-icon fa fa-repeat bigger-120");//改为刷新状态，并提示正在拉起
				return;
			}
			var tr = $(obj).closest('tr')
			getMclusterStatus(tr.find('[name="vip"]').html(),tr); //返回成功刷新本行
		}
	});
}
function queryHcluster(){
	var options=$('#Physicalcluster');
	var hclusters="";
	getLoading();
	$.ajax({
		cache:false,
		url:'/hcluster/byType/RDS',
		type:'get',
		dataType:'json',
		success:function(data){
			removeLoading();
			var array = data.data;
			for(var i = 0, len = array.length; i < len; i++){
				
				var option = $("<option value=\""+array[i].id+"\" data-hclsName='"+array[i].hclusterName+"'>"
								+array[i].hclusterNameAlias
								+"</option>");
				options.append(option);
				hclusters+=array[i].hclusterName+",";
			}
			$("#hclusters").val(hclusters);
			initChosen();
			queryMclusterMonitor();
		}
	});
}
function page_init(){
	queryHcluster();
	$('#nav-search').addClass("hidden");
	setInterval(function() {
		updateMclusterStatus();
		},60000);
}
