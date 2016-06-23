var xAxis_min,xAxis_max;
function refreshChartForSelect(){
	var iw=document.body.clientWidth;
	if(iw>767){//md&&lg
	}else{
		$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
		$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
		var qryStr='';
		var qryStr1=$('.monitorHclusterOption:last').val();var qryStr2=$('.mclusterOption:last').val();var qryStr3=$('#queryTime').val();var qryStr4=$('#monitorPointOption').val();
		if(qryStr1){
			var temp=$('.monitorHclusterOption:last option[value="'+qryStr1+'"]').text();
			qryStr+='<span class="label label-success arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryStr2){
			var temp=$('.mclusterOption:last option[value="'+qryStr2+'"]').text();
			qryStr+='<span class="label label-warning arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryStr3){
			var temp=$('#queryTime').find("option:selected").text();
			qryStr+='<span class="label label-purple arrowed">'+temp+'</span>&nbsp;'
		}
		if(qryStr4){
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
	$(".mclusterOption").empty();
	$(".mclusterOption").append("<option></option>");
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

function initChart(obj,title,ytitle,unit){
    $(obj).highcharts({
        chart: {
            // type: 'areaspline',
            type:'line',
            zoomType: 'x',
            spacingRight: 20
        },
        colors:['#ff66cc','#66ff66','#66ffff','#FFBB33','#C9C','#090','#330000','#CCCC00','#66cc99','#ccff66','#996666','#66cc33'],
        title: {
            text: title
        },
        legend :{
            borderColor: '#000000',
            backgroundColor: '#f9f9f9',
            symbolRadius: '2px',
            borderRadius: '5px',
            itemHoverStyle: {
                Color: '#000000'
            }
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval:150,
            labels:{
                rotation:0,
                align:'right'
            },
            dateTimeLabelFormats:{
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%e. %b',
                week: '%e. %b',
                month: '%b \'%y',
                year: '%Y'
            }
        },
        scrollbar:{
	        enabled:true
	    },
        plotOptions: {
        	lineWidth: 0.1,  
            fillOpacity: 0.1,
            // areaspline: {
            //     marker: {
            //         enabled: false,
            //         symbol: 'circle',
            //         radius: 2,
            //         states: {
            //             hover: {
            //                 enabled: true
            //             }
            //         }
            //     }
            // },
            line: {
                marker: {
                    enabled: false,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            },
            series:{
            	// lineWidth: 0.5,
            	lineWidth:2,  
                fillOpacity: 0.5,
                states:{
                    hover:{
                        lineWidthPlus:0
                    }
            	}
        	}
        },
        credits:{
            enabled: false
        },
        yAxis: {
            title: {
                text: ytitle
            }
        },
        tooltip: {
            valueSuffix: unit,
            shared: true,
            pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}B/S</b><br/>'
        }
    });

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
				chart.hideLoading();
		 		if(error(data)) return;
		 		var ydata = data.data;
		 		for(var i=chart.series.length-1;i>=0;i--){
		 			chart.series[i].remove(false);
	 			}
		 		for(var i=0;i<ydata.length;i++){		
		 			ydata[i].name = MapIopsName(ydata[i].name);
		 			chart.addSeries(ydata[i],false);
	 			}
		 		chart.redraw();
			}
		});
	}	
}

function MapIopsName(str){
	var ar = str.split(":");
	if(ar.length!=2)return str;
	
	if(ar[1]=="read_iops"){
		return ar[0]+":每秒读操作次数";
	}else if(ar[1]=="write_iops"){
		return ar[0]+":每秒写操作次数";
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
