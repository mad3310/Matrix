var xAxis_min,xAxis_max;
function refreshChartForSelect(){
	var iw=document.body.clientWidth;
	if(iw>767){//md&&lgchart
	}else{
		
		$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
		$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
		var qryStr='';
		var qryHcluster=$('.monitorHclusterOption:last').val();var qryMcluster=$('.mclusterOption:last').val();var qryTime=$('#queryTime').val();var qryPoint=$('#monitorPointOption').val();
		if(qryHcluster){
			var temp=$('.monitorHclusterOption:last option[value="'+qryHcluster+'"]').text();
			qryStr+='<span class="label label-success arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryMcluster){
			var temp=$('.mclusterOption:last option[value="'+qryMcluster+'"]').text();
			qryStr+='<span class="label label-warning arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryTime){
			var temp=$('#queryTime').find("option:selected").text();
			qryStr+='<span class="label label-purple arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryPoint){
			var obj=$('#monitorPointOption').val()
			for(i in obj){
				var index=obj[i];
				var temp=$("#monitorPointOption option[value='"+index+"']").text();
				qryStr+='<span class="label label-yellow arrowed">'+temp+'<span class="queryBadge" data-rely-index="'+index+'"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
		}
		if(qryStr){
			$('.queryOption').find('.widget-title').html(qryStr);
			$('.queryBadge').click(function(event) {
				var index=$(this).attr('data-rely-index');
				$("#monitorPointOption option[value='"+index+"']").removeAttr('selected');
				$(this).parent().remove();
				//查询
				var monitorPoint = $('#monitorPointOption').val();
				$('#monitor-view [name="monitor-view"]').each(function(){
					if (monitorPoint != null){
						for (var i = 0,len = monitorPoint.length; i < len ; i++){
							if($(this).attr('id') == (monitorPoint[i]+"-monitor-view")){
								$(this).removeClass('hide');
								var chart = $("#"+monitorPoint[i]).highcharts();
								setChartData(monitorPoint[i],chart);
								break
							}
							$(this).addClass('hide');
						}
					}else{
						$(this).addClass('hide');
					}
				});//end 查询
				if($('.queryBadge').length<=0){
					$('.queryOption').find('.widget-title').html('查询条件');
				}
				return;
			});
		}else{
			$('.queryOption').find('.widget-title').html('查询条件');
		}
	}
	var monitorPoint = $('#monitorPointOption').val();
	$('#monitor-view [name="monitor-view"]').each(function(){
		if (monitorPoint != null){
			for (var i = 0,len = monitorPoint.length; i < len ; i++){
				if($(this).attr('id') == (monitorPoint[i]+"-monitor-view")){
					$(this).removeClass('hide');
					var chart = $("#"+monitorPoint[i]).highcharts();
					setChartData(monitorPoint[i],chart);
					break
				}
				$(this).addClass('hide');
			}
		}else{
			$(this).addClass('hide');
		}
	});
}

function queryHcluster(){
	$.ajax({
		cache:false,
		type:"get",		
		url:"/hcluster/byType/RDS",
		dataType:"json",
		success:function(data){
			//removeLoading();
			if(error(data)) return;
			var hclusterInfo = data.data;
			for(var i=0,len=hclusterInfo.length;i<len;i++){
				var option = $("<option value=\""+hclusterInfo[i].id+"\">"+hclusterInfo[i].hclusterNameAlias+"</option>");
				$(".monitorHclusterOption").append(option);
			}
			initChosen();
			queryMonitorPoint();
		}
	});	
}
function queryMcluster(){
	//getLoading();
	var hclusterId = $('.monitorHclusterOption:visible').val();
	var mclusterOptionObj = $(".mclusterOption");
	mclusterOptionObj.empty();
	mclusterOptionObj.append("<option></option>");
	$.ajax({
		cache:false,
		type:"get",		
		url:"/hcluster/" + hclusterId,
		dataType:"json",
		success:function(data){
			//removeLoading();
			if(error(data)) return;
			var mclustersInfo = data.data.hclusterDetail;
			for(var i=0,len=mclustersInfo.length;i<len;i++){
				var option = $("<option value=\""+mclustersInfo[i].hostId+"\">"+mclustersInfo[i].hostIp+"</option>");
				$(".mclusterOption").append(option);
			}
			initChosen();
		}
	});	
}

function queryMonitorPoint(){
	//getLoading();
	$.ajax({
		cache:false,
		type:"get",		
		url : "/monitor/index/6",
		dataType:"json",
		success:function(data){
			//removeLoading();
			if(error(data)) return;
			var monitorPoint = data.data;
			for(var i=0,len=monitorPoint.length;i<len;i++){
				var option = $("<option value=\""+monitorPoint[i].id+"\">"+monitorPoint[i].titleText+"</option>");
				$("#monitorPointOption").append(option);
				//init all charts
				initCharts(monitorPoint[i]);
			}
			initChosen();
		}
	});	
}

function initCharts(data){
	var viewDemo = $('#monitor-view-demo').clone().removeClass('hide').attr("id",data.id+"-monitor-view").appendTo($('#monitor-view'));
	var div = $(viewDemo).find('[name="data-chart"]');
	$(div).attr("id",data.id);
	//init div to chart
	initChart(div,data.titleText,data.yAxisText,data.tooltipSuffix);
	
	/*隐藏图表*/
	$('div[name="monitor-view"]').each(function(){
		$(this).addClass("hide");
	});
	var chart = $(div).highcharts();
	setChartData(data.id,chart);
	draggable(viewDemo);
}


function setChartData(indexId,chart){
	var mclusterId= $('.mclusterOption').val();
	var queryTime= $('#queryTime').val();
	if(queryTime == ''){
		var queryTime = 1;
	}
	if(mclusterId != ''){
		chart.showLoading();
		$.ajax({
			cache:false,
			type : "get",
			url : "/monitor/host/"+mclusterId+"/"+indexId+"/"+queryTime,
			dataType : "json", 
			contentType : "application/json; charset=utf-8",
			success:function(data){	
				chart.zoom();
				chart.hideLoading();
		 		if(error(data)) return;
		 		var ydata = data.data;
		 		
		 		
		 		for(var i=chart.series.length-1;i>=0;i--){
		 			chart.series[i].remove(false);
	 			}
		 		for(var i=0;i<ydata.length;i++){	 			
		 			getLocalTime(ydata[i]);
		 			chart.addSeries(ydata[i],false);
	 			}
		 		chart.redraw();
			}
		});
	}	
}

function getLocalTime(timeArray){
	for(var i=0;i<timeArray.length;++i){
		var date = new Date(timeArray[i][0]);
		timeArray[i][0] = date;
	}
}


function draggable(obj){
	 $(obj).sortable({
	        connectWith: '.widget-container-col',
			items:'> .widget-box',
			handle: ace.vars['touch'] ? '.widget-header' : false,
			cancel: '.fullscreen',
			opacity:0.8,
			revert:true,
			forceHelperSize:true,
			placeholder: 'widget-placeholder',
			forcePlaceholderSize:true,
			tolerance:'pointer',
			disabled:true,
			start: function(event, ui) {
				ui.item.parent().css({'min-height':ui.item.height()})
			},
			update: function(event, ui) {
				ui.item.parent({'min-height':''})
			}
	    });
}

function changeDraggable(obj){
	var dgable = $(obj).find('input').val();
	if(dgable == '1'){
		$(obj).closest('[name="monitor-view"]').sortable('disable');
		$(obj).find('input').val(0);
		$(obj).find('i').attr("style","-webkit-transform:rotate(45deg);-moz-transform:rotate(45deg);-o-transform:rotate(45deg);");
	}else{
		$(obj).closest('[name="monitor-view"]').sortable('enable');
		$(obj).find('input').val(1);
		$(obj).find('i').attr("style","-webkit-transform:rotate(90deg);-moz-transform:rotate(90deg);-o-transform:rotate(90deg);");
	}
}

function updateChartSize(obj){
	 setTimeout(function () { 
		 $(obj).closest('.widget-box').find('[name="data-chart"]').highcharts().reflow();
	    }, 1);
}

$(function(){
	$('#nav-search').addClass("hidden");
	queryHcluster();
	$(".monitorHclusterOption").change(function() {
		queryMcluster();
	})
});
var xAxis_min,xAxis_max;
function refreshChartForSelect(){
	var iw=document.body.clientWidth;
	if(iw>767){//md&&lgchart
	}else{
		
		$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
		$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
		var qryStr='';
		var qryHcluster=$('.monitorHclusterOption:last').val();var qryMcluster=$('.mclusterOption:last').val();var qryTime=$('#queryTime').val();var qryPoint=$('#monitorPointOption').val();
		if(qryHcluster){
			var temp=$('.monitorHclusterOption:last option[value="'+qryHcluster+'"]').text();
			qryStr+='<span class="label label-success arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryMcluster){
			var temp=$('.mclusterOption:last option[value="'+qryMcluster+'"]').text();
			qryStr+='<span class="label label-warning arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryTime){
			var temp=$('#queryTime').find("option:selected").text();
			qryStr+='<span class="label label-purple arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryPoint){
			var obj=$('#monitorPointOption').val()
			for(i in obj){
				var index=obj[i];
				var temp=$("#monitorPointOption option[value='"+index+"']").text();
				qryStr+='<span class="label label-yellow arrowed">'+temp+'<span class="queryBadge" data-rely-index="'+index+'"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
		}
		if(qryStr){
			$('.queryOption').find('.widget-title').html(qryStr);
			$('.queryBadge').click(function(event) {
				var index=$(this).attr('data-rely-index');
				$("#monitorPointOption option[value='"+index+"']").removeAttr('selected');
				$(this).parent().remove();
				//查询
				var monitorPoint = $('#monitorPointOption').val();
				$('#monitor-view [name="monitor-view"]').each(function(){
					if (monitorPoint != null){
						for (var i = 0,len = monitorPoint.length; i < len ; i++){
							if($(this).attr('id') == (monitorPoint[i]+"-monitor-view")){
								$(this).removeClass('hide');
								var chart = $("#"+monitorPoint[i]).highcharts();
								setChartData(monitorPoint[i],chart);
								break
							}
							$(this).addClass('hide');
						}
					}else{
						$(this).addClass('hide');
					}
				});//end 查询
				if($('.queryBadge').length<=0){
					$('.queryOption').find('.widget-title').html('查询条件');
				}
				return;
			});
		}else{
			$('.queryOption').find('.widget-title').html('查询条件');
		}
	}
	var monitorPoint = $('#monitorPointOption').val();
	$('#monitor-view [name="monitor-view"]').each(function(){
		if (monitorPoint != null){
			for (var i = 0,len = monitorPoint.length; i < len ; i++){
				if($(this).attr('id') == (monitorPoint[i]+"-monitor-view")){
					$(this).removeClass('hide');
					var chart = $("#"+monitorPoint[i]).highcharts();
					setChartData(monitorPoint[i],chart);
					break
				}
				$(this).addClass('hide');
			}
		}else{
			$(this).addClass('hide');
		}
	});
}

function queryHcluster(){
	$.ajax({
		cache:false,
		type:"get",		
		url:"/hcluster/byType/RDS",
		dataType:"json",
		success:function(data){
			//removeLoading();
			if(error(data)) return;
			var hclusterInfo = data.data;
			for(var i=0,len=hclusterInfo.length;i<len;i++){
				var option = $("<option value=\""+hclusterInfo[i].id+"\">"+hclusterInfo[i].hclusterNameAlias+"</option>");
				$(".monitorHclusterOption").append(option);
			}
			initChosen();
			queryMonitorPoint();
		}
	});	
}
function queryMcluster(){
	//getLoading();
	var hclusterId = $('.monitorHclusterOption:visible').val();
	var mclusterOptionObj = $(".mclusterOption");
	mclusterOptionObj.empty();
	mclusterOptionObj.append("<option></option>");
	$.ajax({
		cache:false,
		type:"get",		
		url:"/hcluster/" + hclusterId,
		dataType:"json",
		success:function(data){
			//removeLoading();
			if(error(data)) return;
			var mclustersInfo = data.data.hclusterDetail;
			for(var i=0,len=mclustersInfo.length;i<len;i++){
				var option = $("<option value=\""+mclustersInfo[i].hostId+"\">"+mclustersInfo[i].hostIp+"</option>");
				$(".mclusterOption").append(option);
			}
			initChosen();
		}
	});	
}

function queryMonitorPoint(){
	//getLoading();
	$.ajax({
		cache:false,
		type:"get",		
		url : "/monitor/index/6",
		dataType:"json",
		success:function(data){
			//removeLoading();
			if(error(data)) return;
			var monitorPoint = data.data;
			for(var i=0,len=monitorPoint.length;i<len;i++){
				var option = $("<option value=\""+monitorPoint[i].id+"\">"+monitorPoint[i].titleText+"</option>");
				$("#monitorPointOption").append(option);
				//init all charts
				initCharts(monitorPoint[i]);
			}
			initChosen();
		}
	});	
}

function initCharts(data){
	var viewDemo = $('#monitor-view-demo').clone().removeClass('hide').attr("id",data.id+"-monitor-view").appendTo($('#monitor-view'));
	var div = $(viewDemo).find('[name="data-chart"]');
	$(div).attr("id",data.id);
	//init div to chart
	initChart(div,data.titleText,data.yAxisText,data.tooltipSuffix);
	
	/*隐藏图表*/
	$('div[name="monitor-view"]').each(function(){
		$(this).addClass("hide");
	});
	var chart = $(div).highcharts();
	setChartData(data.id,chart);
	draggable(viewDemo);
}


function setChartData(indexId,chart){
	var mclusterId= $('.mclusterOption').val();
	var queryTime= $('#queryTime').val();
	if(queryTime == ''){
		var queryTime = 1;
	}
	if(mclusterId != ''){
		chart.showLoading();
		$.ajax({
			cache:false,
			type : "get",
			url : "/monitor/host/"+mclusterId+"/"+indexId+"/"+queryTime,
			dataType : "json", 
			contentType : "application/json; charset=utf-8",
			success:function(data){	
				chart.zoom();
				chart.hideLoading();
		 		if(error(data)) return;
		 		var ydata = data.data;
		 		
		 		
		 		for(var i=chart.series.length-1;i>=0;i--){
		 			chart.series[i].remove(false);
	 			}
		 		for(var i=0;i<ydata.length;i++){	 			
		 			getLocalTime(ydata[i]);
		 			chart.addSeries(ydata[i],false);
	 			}
		 		chart.redraw();
			}
		});
	}	
}

function getLocalTime(timeArray){
	for(var i=0;i<timeArray.length;++i){
		var date = new Date(timeArray[i][0]);
		timeArray[i][0] = date;
	}
}


function draggable(obj){
	 $(obj).sortable({
	        connectWith: '.widget-container-col',
			items:'> .widget-box',
			handle: ace.vars['touch'] ? '.widget-header' : false,
			cancel: '.fullscreen',
			opacity:0.8,
			revert:true,
			forceHelperSize:true,
			placeholder: 'widget-placeholder',
			forcePlaceholderSize:true,
			tolerance:'pointer',
			disabled:true,
			start: function(event, ui) {
				ui.item.parent().css({'min-height':ui.item.height()})
			},
			update: function(event, ui) {
				ui.item.parent({'min-height':''})
			}
	    });
}

function changeDraggable(obj){
	var dgable = $(obj).find('input').val();
	if(dgable == '1'){
		$(obj).closest('[name="monitor-view"]').sortable('disable');
		$(obj).find('input').val(0);
		$(obj).find('i').attr("style","-webkit-transform:rotate(45deg);-moz-transform:rotate(45deg);-o-transform:rotate(45deg);");
	}else{
		$(obj).closest('[name="monitor-view"]').sortable('enable');
		$(obj).find('input').val(1);
		$(obj).find('i').attr("style","-webkit-transform:rotate(90deg);-moz-transform:rotate(90deg);-o-transform:rotate(90deg);");
	}
}

function updateChartSize(obj){
	 setTimeout(function () { 
		 $(obj).closest('.widget-box').find('[name="data-chart"]').highcharts().reflow();
	    }, 1);
}

$(function(){
	$('#nav-search').addClass("hidden");
	queryHcluster();
	$(".monitorHclusterOption").change(function() {
		queryMcluster();
	})
});
