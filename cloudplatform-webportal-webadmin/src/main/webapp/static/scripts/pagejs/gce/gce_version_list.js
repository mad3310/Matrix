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
			'gceName':$('#gceName').val()
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
			  var tdIdHtml = "<input class=\"hidden\" type=\"text\" value=\""+array[i].id+"\"\> ";
			  var tdNameHtml="<td>" +
							      (array[i].gceName || '')+
							  "</td>";
			  var tdHclusterNameHtml = "<td class='hidden-480'>"+
							    			(array[i].hcluster?array[i].hcluster.hclusterNameAlias:'')+
									   "</td>";
			  var tdStatusHtml = $("<td>"+
							      		(array[i].status===7?'正常':'异常')+
									"</td>");
			  var tr = $("<tr class='tr-gce'></tr>");
		
			  tr.append(tdIdHtml).append(tdNameHtml).append(tdHclusterNameHtml).append(tdStatusHtml);
			  tr.appendTo(tby);		
			}
		
			if (totalPages <= 1) {
			  $("#pageControlBar").hide();
			} else {
			  $("#pageControlBar").show();
			  $("#totalPage_input").val(totalPages);
			  $("#currentPage").val(currentPage);
			  $("#totalRows").html(data.data.totalRecords);
			  $("#totalPage").val(totalPages);
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

function addClickEventListenerToButtonSearch(){
	$('#btnSearch').on('click',function(e){
		queryByPage();
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
								'<td>'+array[i].version+'</td>'+
								'<td style="max-width:250px;">'+(array[i].gceImageName || '')+'</td>'+
								'<td style="max-width:300px;min-width:200px;">'+
									'<span title="'+(array[i].descn || '')+'">'+(array[i].descn || '')+'</span>'+
								'</td>'+
								'<td>'+translateStatus(array[i].status, 'gceVersion')+'</td>'+
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
	addClickEventListenerToButtonSearch();
	addClickEventListenerToGceList();
	queryByPage();
	pageControl();
}
