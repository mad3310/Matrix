/*
 * backup_recover page js. 2015/01/23
 */

var currentPage = 1; //第几页 
var recordsPerPage = 5; //每页显示条数
var currentSelectedLineDbName = 1;
alert(startTime);
alert(endTime);


//页面查询功能
$("#bksearch").click(function() {
	queryByPage(currentPage, recordsPerPage);
});

$(function(){
	//初始化
	page_init();
});	

function queryByPage(currentPage, recordsPerPage) {
	$(".data-tr").remove();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();
	$.ajax({ 
		type : "get",
		url : "/backup?" + "&&startTime=" + startTime + "&&endTime=" + endTime + "&&currentPage=" + currentPage + "&&recordsPerPage=" + recordsPerPage,	
	    dataType : "json", /*这句可用可不用，没有影响*/
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			error(data);
			var array = data.data.data;
			var $backupTbody = $("#backupTbody");
			var totalPages = data.data.totalPages;
	        for(var i= 0, len= array.length;i<len;i++){
	                var td1 = $("<td>"
	                        + date('Y-m-d H:i:s',array[i].startTime) 
	                        + "/"
	                        + date('Y-m-d H:i:s',array[i].endTime)
	                        + "</td>");
	                var td2 = $("<td class=\"padding-left-32\">"
	                        /*+ array[i].strategy*/
	                		+ "实例备份"
	                        +"</td>");
	                var td3 = $("<td>"
	                        /*+ array[i].size*/
	                		+ "0.39M"
	                        +"</td>");
	                var td4 = $("<td>"
	                        /*+ array[i].method*/
	                		+ "物理备份"
	                        + "</td>");
	                var td5 = $("<td>"
	                		/*+ array[i].backupType*/
	                		+ "全量"
	                		+"</td>");
	                var td6 = $("<td>"
	                		/*+ array[i].pattern*/
	                		+ "常规任务"
	                		+ "</td>");
	                var td7 = $("<td><span>"
	                		/*+ array[i].status*/
	                		+ "完成备份"
	                		+ "</span></td>");
	                var td8 = $("<td class=\"text-right\"> <div>"
	                        + "<a class=\"text-explode font-disabled\" href=\"javascript:void(0);\">下载</a><span class=\"text-explode\">"
	                        + "|</span><a class=\"text-explode font-disabled\"  href=\"javascript:void(0);\">创建临时实例</a><span class=\"text-explode\">"
	                        + "|</span><a class=\"text-explode font-disabled\"  href=\"javascript:void(0);\">恢复</a><span class=\"text-explode\">"
	                        + "</div></td>");
	                var tr = $("<tr class='data-tr'></tr>");
	                tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td7).append(td8);
	                tr.appendTo($backupTbody);
				   //$('[name = "dbRefuseStatus"]').popover();
			}//循环json中的数据 
			
			if (totalPages <= 1) {
				$("#pageControlBar").hide();
			} else {
				$("#pageControlBar").show();
				$("#totalPage_input").val(totalPages);
				$("#currentPage").html(currentPage);
				$("#totalRows").html(data.data.totalRecords);
				$("#totalPage").html(totalPages);
			}
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			$.gritter.add({
				title: '警告',
				text: errorThrown,
				sticky: false,
				time: '5',
				class_name: 'gritter-warning'
			});
	
			return false;
		}
	});
   }

function pageControl() {
	// 首页
	$("#firstPage").bind("click", function() {
		currentPage = 1;
		queryByPage(currentPage,recordsPerPage);
	});

	// 上一页
	$("#prevPage").click(function() {
		if (currentPage == 1) {
			$.gritter.add({
				title: '警告',
				text: '已到达首页',
				sticky: false,
				time: '5',
				class_name: 'gritter-warning'
			});
	
			return false;
			
		} else {
			currentPage--;
			queryByPage(currentPage,recordsPerPage);
		}
	});

	// 下一页
	$("#nextPage").click(function() {
		if (currentPage == $("#totalPage_input").val()) {
			$.gritter.add({
				title: '警告',
				text: '已到达末页',
				sticky: false,
				time: '5',
				class_name: 'gritter-warning'
			});
	
			return false;
			
		} else {
			currentPage++;
			queryByPage(currentPage,recordsPerPage);
		}
	});

	// 末页
	$("#lastPage").bind("click", function() {
		currentPage = $("#totalPage_input").val();
		queryByPage(currentPage,recordsPerPage);
	});
}

function searchAction(){
	$('#nav-search-input').bind('keypress',function(event){
        if(event.keyCode == "13")    
        {
        	queryByPage(currentPage, recordsPerPage);
        }
    });
}
function page_init(){
	queryByPage(currentPage, recordsPerPage);
	searchAction();
	pageControl();
}
