var currentPage = 1; //第几页 
var recordsPerPage = 20; //每页显示条数

$(function(){
	//初始化 
	page_init();
});	
function queryByPage() {
	var queryCondition = {
			'currentPage':currentPage,
			'recordsPerPage':recordsPerPage,
			'gceName':''
	}
    $("#tby tr").remove();
    getLoading();
    $.ajax({
      cache:false,
      type : "get",
      url : queryUrlBuilder("/ecgce",queryCondition),
      dataType : "json", /*这句可用可不用，没有影响*/
      contentType : "application/json; charset=utf-8",
      success : function(data) {
			removeLoading();
			error(data);
			var array = data.data.data;
			var tby = $("#tby");
			var totalPages = data.data.totalPages;
			for (var i = 0, len = array.length; i < len; i++) {
			  var td0 = $("<input class=\"hidden\" type=\"text\" value=\""+array[i].id+"\"\> ");
			  var td2;
			  if(array[i].status == 6){
			    td2 = $("<td>"
			      + "<a class=\"link\" href='#'>"+array[i].gceName+"</a>"
			      + "</td>");//href=\"/detail/db/"+array[i].id+"\"
			  }else if(array[i].status == 0 ||array[i].status == 3){
			    td2 = $("<td>"
			      + "<a class=\"link\" class=\"danger\" href='#'>"+array[i].gceName+"</a>"
			      + "</td>");//href=\"/audit/db/"+array[i].id+"\"
			  }else{
			    td2 = $("<td>"
			      + "<a class=\"link\" style=\"text-decoration:none;\">"+array[i].gceName+"</a>"
			      + "</td>");
			  }
			  if(array[i].hcluster){
			    var td4 = $("<td class='hidden-480'>"
			      + array[i].hcluster.hclusterNameAlias
			      + "</td>");
			  } else {
			    var td4 = $("<td class='hidden-480'> </td>");
			  }
			  if(array[i].status == 4){
			    var td7 = $("<td>"
			      +"<a href=\"#\" name=\"dbRefuseStatus\" rel=\"popover\" data-container=\"body\" data-toggle=\"popover\" data-placement=\"top\" data-trigger='hover' data-content=\""+ array[i].auditInfo + "\" style=\"cursor:pointer; text-decoration:none;\">"
			      + translateStatus(array[i].status)
				+"</a>"
			      + "</td>");
			  }else if(array[i].status == 2){
			    var td7 = $("<td>"
			      +"<a name=\"buildStatusBoxLink\" data-toggle=\"modal\" data-target=\"#create-mcluster-status-modal\" style=\"cursor:pointer; text-decoration:none;\">"
			      +"<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\" />"
			      +"创建中...</a>"
			      + "</td>");
			  }else{
			    var td7 = $("<td> <a>"
			      + translateStatus(array[i].status)
				+ "</a></td>");
			  }
		
			  if(array[i].status == 0 ||array[i].status == 5||array[i].status == 13){
			    var tr = $("<tr class=\"tr-gce warning\"></tr>");
		
			  }else if(array[i].status == 3 ||array[i].status == 4||array[i].status == 14){
			    var tr = $("<tr class=\"tr-gce default-danger\"></tr>");
		
			  }else{
			    var tr = $("<tr class='tr-gce'></tr>");
			  }
		
			  tr.append(td0).append(td2).append(td4).append(td7);
			  tr.appendTo(tby);
		
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
			
			queryVersionDetail(array[0].id);
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
		queryByPage();
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
			queryByPage();
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
			queryByPage();
		}
	});

	// 末页
	$("#lastPage").bind("click", function() {
		currentPage = $("#totalPage_input").val();
		queryByPage();
	});
}

function addClickEventListenerToGceList(){
	$('#listGce tbody').on('click',function(e){
		var event =e;
		var trEl = $(e.target).parents('.tr-gce');
		if(!trEl) return;
		
		var gceId = trEl.find('input.hidden').val();
		queryVersionDetail(gceId);
	});
}

function queryVersionDetail(gceId){
	//type 为 new|update
	$.ajax({
		cache:false,
		type : "get",
		url : "/ecgce/package/"+gceId,
		dataType : "json", /*这句可用可不用，没有影响*/
		success : function(data) {
			
			if(error(data)) return;
			var array = data.data.data;
			var tby = $(".table-version #tby");
			tby.empty();
			for (var len = array.length, i = len - 1; i >=0 ; i--) {
				var trHtml ='<tr>'+
								'<td>'+array[i].gceImageName+'</td>'+
								'<td>'+array[i].version+'</td>'+
								'<td>'+array[i].status+'</td>'+
							'</tr>';
				
				tby.append(trHtml);
			}
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			error(XMLHttpRequest);
			return false;
		}
	});
}


function page_init(){
	addClickEventListenerToGceList();
	queryByPage();
	pageControl();
}
