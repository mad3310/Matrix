/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require){
	var $ = require('jquery');
	require('highcharts')($);
    var common = require('../../../../common');
    var cn = new common();

	var dataHandler = require('./dataHandler');
	var monitor = new dataHandler();

	$(".monitor-date-block").find("li").click(function () {
		var $brothers = $(".monitor-date-block").find("li");
		$brothers.removeClass("active");
		$(this).addClass("active");
		$("#strategy").val($(this).val());

		UpdateChartData();
	})
	/*初始化加载数据*/
	InitChart();
	UpdateChartData();

	function InitChart(){
		cn.GetData("/monitor/index/33",monitor.InitCharts);
	}
	function UpdateChartData(){
		var url = "/monitor/gce/"+$("#dbId").val()+"/33/"+$("#strategy").val()+"/false/0";
		cn.GetData(url,monitor.SetChartData); 
	}
});
