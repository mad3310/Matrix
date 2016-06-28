/*
 * backup_recover page js. 2015/01/23
 */

var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var currentSelectedLineDbName = 1;
flag = false;
$(function(){
	//初始化
	page_init();
});	
//页面查询功能
$("#bksearch").click(function() {
	flag = true;
	currentPage = 1;
	var iw=document.body.clientWidth;
	if(iw>767){//md&&lg
	}else{
		$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
		$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
		var qryStr='';
		var qryStr1=$('#startTime').val();var qryStr2=$('#endTime').val();var qryStr3=$('#dbName').val();var qryStr4=$('#mclusterName').val();var qryStr5;
		if($('#backupStatus').val()){
			var status={0:'SUCCESS',1:'FAILD',2:'BUILDING'};
			var temp=$('#backupStatus').val();
			if(status[temp]){
				qryStr5=translateStatus(status[temp]);
			}else{}
		}
		if(qryStr1){
			qryStr+='<span class="label label-success arrowed">开始时间：'+qryStr1+'<span class="queryBadge" data-rely-id="startTime"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
		}
		if(qryStr2){
			qryStr+='<span class="label label-warning arrowed">结束时间：'+qryStr2+'<span class="queryBadge" data-rely-id="endTime"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
		}
		if(qryStr3){
			qryStr+='<span class="label label-purple arrowed">'+qryStr3+'<span class="queryBadge" data-rely-id="dbName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
		}
		if(qryStr4){
			qryStr+='<span class="label label-yellow arrowed">'+qryStr4+'<span class="queryBadge" data-rely-id="mclusterName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
		}
		if(qryStr5){
			qryStr+='<span class="label label-pink arrowed">'+qryStr5+'<span class="queryBadge" data-rely-id="backupStatus"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
		}
		if(qryStr){
			$('.queryOption').find('.widget-title').html(qryStr);
			$('.queryBadge').click(function(event) {
				var id=$(this).attr('data-rely-id');
				$('#'+id).val('');
				$(this).parent().remove();
				queryByPage(currentPage, recordsPerPage);
				if($('.queryBadge').length<=0){
					$('.queryOption').find('.widget-title').html('备份恢复查询条件');
				}
				return;
			});
		}else{
			$('.queryOption').find('.widget-title').html('备份恢复查询条件');
		}
	}
	queryByPage(currentPage, recordsPerPage);
});
/*绑定enter事件*/
$(".input-group input").each(function(){
	flag = true;
	$(this).bind('keypress',function(event){
        if(event.keyCode == "13")    
        {
        	queryByPage(currentPage, recordsPerPage);
        }
    });
});
function queryByPage(currentPage, recordsPerPage) {
	$("#backupTbody tr").remove();
	if(flag == true){
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var mclusterName = $("#mclusterName").val();
		var dbName = $("#dbName").val();
		
		var backupStatus = $("#backupStatus").val();
		
	}else{
		var startTime = '';
		var endTime = '';
		var mclusterName = '';
		var dbName = '';
		var backupStatus = '';
	}
	
	getLoading();
	$.ajax({ 
		type : "get",
		url : "/backup/latestLog/list?" + "&&startTime=" + startTime + "&&endTime=" + endTime + "&&currentPage=" + currentPage + "&&recordsPerPage=" + recordsPerPage + "&&dbName=" + dbName +"&&mclusterName=" + mclusterName +'&&status=' + backupStatus,
		dataType : "json", /*这句可用可不用，没有影响*/
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			removeLoading();
			error(data);
			var $backupTbody = $("#backupTbody");
			var totalPages = data.data.totalPages;
			var array = data.data.data;
			if(array.length == 0){
				$("#noData").removeClass("hidden");
			}else{
				$("#noData").addClass("hidden");
				 for(var i= 0, len= array.length;i<len;i++){
		        		var mclusterName = '';
		        		if(array[i].mcluster) {
		        			mclusterName = array[i].mcluster.mclusterName;
		        		}
		        		var dbName = '';
		        		if(array[i].db) {
		        			dbName = array[i].db.dbName;
		        		}
		                var td1 = "<td class='hidden-480'><a>"
		                		+ "<a class=\"link\" href=\"/detail/mcluster/" + array[i].mclusterId +"\">"+FilterNull(mclusterName)+"</a>"
		                		+"</a></td>";
		                var td2 = "<td>"
		                		+ "<a class=\"link\" class=\"danger\" href=\"/detail/db/"+array[i].dbId+"\">"+FilterNull(dbName)+"</a>"
		                		+"</td>"
		                var td3 = "<td class='hidden-480'>"
		                        + date('Y-m-d H:i:s',array[i].startTime)
		                        + "</td>";
		                var td4 = "<td class='hidden-480'>"
	                            + date('Y-m-d H:i:s',array[i].endTime)
		                        + "</td>";
		                if(array[i].status == 'FAILD'){
		                	var td5 = "<td> <a>"
								+ translateStatus(array[i].status)
								+ "</a></td>";
						}else if(array[i].status == 'BUILDING'){
							var td5 = "<td>"
									+ "<a name=\"buildStatusBoxLink\" data-toggle=\"modal\" data-target=\"#create-mcluster-status-modal\" style=\"cursor:pointer; text-decoration:none;\">"
									+ "<i class=\"ace-icon fa fa-spinner fa-spin dark bigger-125\" />"
									+ translateStatus(array[i].status)
									+ "</a>"
									+ "</td>";
						}else{
							var td5 = "<td> <a>"
									+ translateStatus(array[i].status)
									+ "</a></td>";
						}
		                
		                var td6 = "<td><a> " +
		                		translateStatus(array[i].backupType)
								+ "</a></td>";
		                
		                var td7 = "<td>"
		                        + array[i].resultDetail
		                        + "</td>";
		                if(array[i].status == 'FAILD'){
							var tr = $("<tr class=\"data-tr default-danger\"></tr>");
						}else if(array[i].status == 'SUCCESS'){
							var tr = $("<tr class=\"data-tr success\"></tr>");
						}else{
							var tr = $("<tr class='data-tr'></tr>");
						}
		                tr.append($(td1 + td2 + td3 + td4 + td5 + td6 + td7));
		                tr.appendTo($backupTbody);
					   //$('[name = "dbRefuseStatus"]').popover();
				}//循环json中的数据 
			}
	       			
			if (totalPages < 1) {
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

function InitSearchClearButton(){
	var inputDbNameEl=$("#dbName"),
	inputmclusterNameEl=$("#mclusterName"),
	selectBackupStatusEl=$('#backupStatus'),
	startTimeEl=$('#startTime').data("DateTimePicker"),
	endTimeEl=$('#endTime').data("DateTimePicker");
	$("#btnSearchClear").on('click',function(e){
		inputDbNameEl.val("");
		inputmclusterNameEl.val("");
		selectBackupStatusEl.val("");
		startTimeEl.date(null);
		endTimeEl.date(null);
		queryByPage(currentPage, recordsPerPage);
	});
}

function page_init(){
	$('#nav-search').addClass("hidden");
	queryByPage(currentPage, recordsPerPage);
	searchAction();
	pageControl();
	InitSearchClearButton();
}
