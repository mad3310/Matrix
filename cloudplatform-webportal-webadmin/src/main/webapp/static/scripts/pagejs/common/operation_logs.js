var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var userName = '';
	
$(function(){
	//初始化 
	page_init();
	var options = {
		allow_single_deselect:true,
		search_contains:true,
		no_results_text:"未找到匹配数据",
		disable_search:true,	
		width:'272px'
	}
	
	$('.chosen-select').chosen(options)

	
	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	
	$('#btnSearch').on('click',function(e){
		userName = $('#inputUserName').val();
		queryByPage();
	});
	
	$("#btnSearchClear").on('click',function(){
		var clearList = ["inputUserName"];
		clearSearch(clearList);
		queryByPage();
	});
});	

function queryByPage() {
	var queryCondition = {
			'currentPage':currentPage,
			'recordsPerPage':recordsPerPage,
			'userNameKeyword':userName
		}
	
	$("#tby tr").remove();
	getLoading();
	$.ajax({
		cache:false,
		type : "get",
		url : queryUrlBuilder("/AdminOpLog/list",queryCondition),
		dataType : "json", /*这句可用可不用，没有影响*/
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var array = data.data.data;
			var tby = $("#tby");
			var totalPages = data.data.totalPages;
			
			for (var i = 0, len = array.length; i < len; i++) {
				var trHtml =    '<tr>'+
									'<td>'+array[i].description+'</td>'+
									'<td><span class="span-operation-request" title="'+array[i].event+'">'+array[i].event+'</span></td>'+
									'<td>'+array[i].user.userName+'</td>'+
									'<td>'+date('Y-m-d H:i:s',array[i].createTime)+'</td>'+
								'</tr>';
				
				tby.append(trHtml);
			}//循环json中的数据 
			
			/*初始化tooltip*/
			$('[data-toggle = "tooltip"]').tooltip();
			
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
			error(XMLHttpRequest);
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


function page_init(){
	$('[name = "popoverHelp"]').popover();
	queryByPage();
	pageControl();
}
