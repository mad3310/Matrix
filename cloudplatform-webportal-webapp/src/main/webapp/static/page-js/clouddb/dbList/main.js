/**
 * Created by yaokuo on 2014/12/12.
 * dblist page
 */
define(function(require){
	var iFresh;
    var common = require('../../common');
    var cn = new common();

    cn.Tooltip();
     /* 初始化navbar-header-menu */
	cn.initNavbarMenu([{
				name : "RDS关系型数据库",
				herf : "/list/db"
			}]);


    /*加载数据*/
    var dataHandler = require('./dataHandler');
    var dbListHandler = new dataHandler();
    /*
     * 初始化数据
     */
	asyncData();

	$("#search").click(function() {
		cn.currentPage = 1;
		asyncData();
	});
	$("#refresh").click(function() {
		asyncData();
	});
	$("#dbName").keydown(function(e){
		if(e.keyCode==13){
			e.preventDefault();
			cn.currentPage = 1;
			asyncData();
		}
	});

	/*初始化按钮*/
	$(".btn-region-display").click(function(){
		$(".btn-region-display").removeClass("btn-success").addClass("btn-default");
		$(this).removeClass("btn-default").addClass("btn-success");
		$("#dbName").val("");
		asyncData();
	})

	/*
	 * 可封装公共方法 begin
	 */
	//初始化分页组件
	$('#paginator').bootstrapPaginator({
		size:"small",
    	alignment:'right',
		bootstrapMajorVersion:3,
		numberOfPages: 3,
		onPageClicked: function(e,originalEvent,type,page){
			cn.currentPage = page;
        	asyncData(page);
        }
	});
	//初始化checkbox
	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	$(document).on('click', 'tfoot input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox,th input:checkbox ')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	/*
	 * 可封装公共方法 end
	 */

	//加载列表数据
	function asyncData(page) {
		var dbName = $("#dbName").val(),location = $("#location").val();
		if(!page) page = cn.currentPage;
		var url = "/db?currentPage=" + page +"&&recordsPerPage=" + cn.recordsPerPage + "&&dbName=" + dbName + "&&location=" + location;
		cn.GetData(url,refreshCtl);

	}
	function refreshCtl(data) {
		dbListHandler.DbListHandler(data);
		clearInterval(iFresh);
		if ($(".progress").length == 0){
			iFresh = setInterval(asyncData,cn.dbListRefreshTime);
		}else{
			 /*进度条数据刷新*/
			var asyncProgressIntervalList=[]
			$("input[name = progress_db_id]").each(function(){
				var dbId = $(this).val(),
				url = "/build/db/" + dbId,
				intervalId = setInterval(function(){
					cn.GetLocalData(url,function(data){
						dbListHandler.progress(dbId,data,onProgressEnd);
					});
				},5000);
				
				function onProgressEnd(){
					var intervalIndex= asyncProgressIntervalList.indexOf(intervalId);
					asyncProgressIntervalList.splice(intervalIndex,1);
					clearInterval(intervalId);
					if(asyncProgressIntervalList.length===0){
						asyncData();	
					}
				}
				
				asyncProgressIntervalList.push(intervalId);
			});
		}
	}
});
