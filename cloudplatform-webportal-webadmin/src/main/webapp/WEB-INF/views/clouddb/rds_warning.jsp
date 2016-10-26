<%@ page language="java" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${ctx}/static/css/gridstack.min.css" />
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');}
	});
</script>
<div class="page-content-area">
	<div class="grid-stack">
	    <div class="grid-stack-item"
	        data-gs-x="0" data-gs-y="0"
	        data-gs-width="6" data-gs-height="3">
	    <div class="grid-stack-item-content" style="backgroud-color:black;"><div id="chart1" class="container-chart" style="height:100%;width:100%;"></div></div>
	    </div>
	    <div class="grid-stack-item"
	        data-gs-x="6" data-gs-y="0"
	        data-gs-width="6" data-gs-height="3">
	    <div class="grid-stack-item-content" style="backgroud-color:black;"><div id="chart2" class="container-chart" style="height:100%;width:100%;"></div></div>
	    </div>
	    <div class="grid-stack-item"
	        data-gs-x="0" data-gs-y="3"
	        data-gs-width="6" data-gs-height="3">
	    <div class="grid-stack-item-content" style="backgroud-color:black;"><div id="chart3" class="container-chart" style="height:100%;width:100%;"></div></div>
	    </div>
	    <div class="grid-stack-item"
	        data-gs-x="6" data-gs-y="3"
	        data-gs-width="6" data-gs-height="3">
	    <div class="grid-stack-item-content" style="backgroud-color:black;"><div id="chart4" class="container-chart" style="height:100%;width:100%;"></div></div>
	    </div>
	</div>
</div>
<!-- /.page-content-area -->
<script src="${ctx}/static/scripts/lodash.min.js"></script>
<script src="${ctx}/static/scripts/gridstack.min.js"></script>
<script src="${ctx}/static/scripts/highcharts/highcharts.js"></script>
<script type="text/javascript">
$(function () {
	var cellHeight = ($(window).height()-120)/6;
	var cellWidth = ($(window).width()-200)/12;
    var options = {
        cell_height: cellHeight,
        cell_width: cellWidth,
        vertical_margin: 0
    };
    $('.grid-stack').gridstack(options);
    $('.grid-stack').on('resizestop', function(event, ui) {
    	buildChart($(event.target).find('.container-chart'));
    });
    
    ['chart1','chart2','chart3','chart4'].forEach(function(id){
    	buildChart(id);
    });
    
    function buildChart(container){
    	var param =  typeof container ==='string'? '#'+container:container;
        $(param).highcharts({
            title: {
                text: 'Monthly Average Temperature',
            },
            series: [{
                name: 'Tokyo',
                data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
            }, {
                name: 'New York',
                data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
            }]
        });	
    }
});
</script>