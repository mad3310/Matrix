/*
 * backup_recover page js. 2015/01/23
 */

var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var currentSelectedLineDbName = 1;
var timer = null;
var backup_list = [];

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
		var startTime = '';
		var endTime = '';
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
		url : "/backup?" + "&&startTime=" + startTime + "&&endTime=" + endTime + "&&currentPage=" + currentPage + "&&recordsPerPage=" + recordsPerPage + "&&dbName=" + dbName +"&&mclusterName=" + mclusterName +'&&status=' + backupStatus,
		dataType : "json", /*这句可用可不用，没有影响*/
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			removeLoading();
			error(data);
			var $backupTbody = $("#backupTbody");
			var totalPages = data.data.totalPages;
			var array = data.data.data;
			backup_list = array;
			
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
		                var td1 = $("<td class='hidden-480'><a>"
		                		+ "<a class=\"link\" href=\"/detail/mcluster/" + array[i].mclusterId +"\">"+FilterNull(mclusterName)+"</a>"
		                		+"</a></td>");
		                var td2 = $("<td>"
		                		+ "<a class=\"link\" class=\"danger\" href=\"/audit/db/"+array[i].dbId+"\">"+FilterNull(dbName)+"</a>"
		                		+"</td>");
		                if(array[i].status == 'FAILD'){
		                	var td5 = $("<td class='status'> <a>"
								+ translateStatus(array[i].status)
								+ "</a></td>");
						}else if(array[i].status == 'BUILDING'){
							var td5 = $("<td class='status'>"
									+ "<a name=\"buildStatusBoxLink\" data-toggle=\"modal\" data-target=\"#create-mcluster-status-modal\" style=\"cursor:pointer; text-decoration:none;\">"
									+ "<i class=\"ace-icon fa fa-spinner fa-spin dark bigger-125\" />"
									+ translateStatus(array[i].status)
									+ "</a>"
									+ "</td>");
						}else{
							var td5 = $("<td class='status'> <a>"
									+ translateStatus(array[i].status)
									+ "</a></td>");
						}
		                
//		                var allBackUpHtml='',addBackUpHtml=''
//		                	
//						var allBackUpOs=containerClusterOs(array[i].status,"rds","backup");
//						var addBackUpOs=containerClusterOs(array[i].status,"rds","backup");
//						allBackUpHtml=allBackUpOs==0?allBackUpHtml:containerOsHtml("rds","start");
//		                addBackUpHtml=addBackUpOs==0?addBackUpHtml:containerOsHtml("rds","start");
		                
//		                var td6 = "<td data-status='"+array[i].status+"'>"
//						+"<div class='hidden-sm hidden-xs  action-buttons'>"
//						+allBackUpHtml+addBackUpHtml
//						+"</div>"
//						+'<div class="hidden-md hidden-lg">'
//						+'<div class="inline pos-rel">'
//						+'<button class="btn btn-minier btn-yellow dropdown-toggle" data-toggle="dropdown" data-position="auto">'
//							+'<i class="ace-icon fa fa-caret-down icon-only bigger-120"></i>'
//						+'</button>'
//						+'<ul class="dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">'
//							+'<li>'+allBackUpHtml+'</li>'
//							+'<li>'+addBackUpHtml+'</li>'
//						+'</ul></div></div>'
//					+ "</td>";
		                var td6 = $("<td> " +
		                		"<a href='javascript:void(0);' class='backup-add'>增量备份</a>&nbsp;&nbsp"
								+ "<a href='javascript:void(0);' class='backup-all'>全量备份</a>"
								+ "</td>");
		                
		                
		                if(array[i].status == 'FAILD'){
							var tr = $("<tr class=\"data-tr default-danger\"></tr>");
						}else if(array[i].status == 'SUCCESS'){
							var tr = $("<tr class=\"data-tr success\"></tr>");
						}else{
							var tr = $("<tr class='data-tr'></tr>");
						}
		                tr.attr("mclusterId",array[i].mclusterId);
		                tr.attr("id",array[i].id);
		                tr.append(td1).append(td2).append(td5).append(td6);
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


function backupInit(){
	var timer = setInterval (function(){
		if(backup_list.length>0){
			UpdateBackupState();
		}
	},15000);
	
	$("#backupTbody").delegate('.backup-add,.backup-all', 'click', function(){
		var mclusterId = $(this).parents("tr").attr("mclusterId");
		var id = $(this).parents("tr").attr("id");
		var url = "";	
		getState(id, mclusterId,function(state){
			if(state=="BUILDING"){
				warn("备份中",2000);
			}else{
				BackupFunc(id, mclusterId);
			}
		});		

	});
}


function BackupFunc(id,mclusterId){
	if($(this).hasClass(".backup-add")){
		url = "/backup/incr?id="+id+"&mclusterId="+mclusterId;
	}else{
		url = "/backup/full?id="+id+"&mclusterId="+mclusterId;
	}
	$.ajax({ 
		cache:false,
		type : "get",
		url :url,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			if(!data.result){
				warn("获取数据失败",2000);
			}else{
				UpdateBackupStateById(id, "BUILDING");
			}
		}
	});
}


function UpdateBackupState(){	
	backup_list.forEach(function(item,index){
		if(item.status=="BUILDING"){
			var mclusterId = item["mclusterId"];
			var id = item["id"];
			getState(id,mclusterId, function(state){
				UpdateBackupStateById(id, state);
			});
		}
	});
}


function UpdateBackupStateById(id,state){
			
	var objTr = $("#backupTbody").find("tr[id='"+id+"']");
    if(state == 'FAILD'){
    	objTr.attr("class","data-tr default-danger");
	}else if(state == 'SUCCESS'){
		objTr.attr("class","data-tr success");
	}else{
		objTr.attr("class","data-tr");
	}
    	
	var obj = objTr.find(".status a");
	obj.text(translateStatus(state));
	backup_list.forEach(function(item){
		if(item.id==id){
			item.status = state;
		}
	});
}


function getState(id, mclusterId, callback){
	var state = "";
	$.ajax({ 
		cache:false,
		type : "get",
		url :"/backup/check?id="+id+"&mclusterId="+mclusterId,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			if(data.result==0){
				state = "ABNORMAL";
			}else{
				state = data.data.status;
			}
			callback(state);
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
	backupInit();
}
