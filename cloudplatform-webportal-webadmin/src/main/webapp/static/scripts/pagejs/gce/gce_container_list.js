var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var queryBuildStatusrefresh;//刷新handler
	
$(function(){
	//初始化 
	page_init();
	
	/*动态加载界面下拉列表值*/
	var sltArray = [1,2,3,5,7,8,9,10,11,12,13,14];
	addSltOpt(sltArray,$("#containerStatus"));
	
	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	
	$("#mclusterSearch").click(function(){
		var iw=document.body.clientWidth;
		if(iw>767){//md&&lg
		}else{
			$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
			$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
			var qryStr='';
			var qryStr1=$('#containerName').val();var qryStr2=$('#ipAddr').val();var qryStr3;
			if($('#containerStatus').val()){
				qryStr3=translateStatus($('#containerStatus').val());
			}
			if(qryStr1){
				qryStr+='<span class="label label-success arrowed">'+qryStr1+'<span class="queryBadge" data-rely-id="containerName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr2){
				qryStr+='<span class="label label-warning arrowed">'+qryStr2+'<span class="queryBadge" data-rely-id="ipAddr"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr3){
				qryStr+='<span class="label label-purple arrowed">'+qryStr3+'<span class="queryBadge" data-rely-id="containerStatus"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr){
				$('.queryOption').find('.widget-title').html(qryStr);
				$('.queryBadge').click(function(event) {
					var id=$(this).attr('data-rely-id');
					$('#'+id).val('');
					$(this).parent().remove();
					queryByPage();
					if($('.queryBadge').length<=0){
						$('.queryOption').find('.widget-title').html('Container查询条件');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('Container查询条件');
			}

		}
		queryByPage();
	});
	$("#mclusterClearSearch").click(function(){
		var clearList = ["containerName","ipAddr","containerStatus"];
		clearSearch(clearList);
	});
	
	enterKeydown($(".page-header > .input-group input"),queryByPage);
});

function queryByPage() {
	var containerName = $("#containerName").val()?$("#containerName").val():'';
	var  ipAddr= $("#ipAddr").val()?$("#ipAddr").val():'';
	var status = $("#containerStatus").val()?$("#containerStatus").val():'';
	var queryCondition = {
			'currentPage':currentPage,
			'recordsPerPage':recordsPerPage,
			'containerName':containerName,
			'ipAddr':ipAddr,
			/*'createTime':createTime,*/
			'status':status
		}
	
	$("#tby tr").remove();
	getLoading();
	$.ajax({
		cache:false,
		type : "get",
		url : queryUrlBuilder("/ecgce/container",queryCondition),
		dataType : "json", /*这句可用可不用，没有影响*/
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var array = data.data.data;
			var tby = $("#tby");
			var totalPages = data.data.totalPages;
			
			for (var i = 0, len = array.length; i < len; i++) {
				var td1 = $("<td class=\"center\">"
								+"<label class=\"position-relative\">"
								+"<input name=\"mcluster_id\" value= \""+array[i].id+"\" type=\"checkbox\" class=\"ace\"/>"
								+"<span class=\"lbl\"></span>"
								+"</label>"
							+"</td>");
				var td2 = $("<td>"
						+array[i].containerName
						+ "</td>");
				if(array[i].gceCluster){
					var td3 = $("<td class='hidden-480'>"
							+ "<a class=\"link\"  href=\"/detail/gce/cluster/" + array[i].gceClusterId+"\">"+array[i].gceCluster.clusterName+"</a>"
							+ "</td>");
				} else {
					var td3 = $("<td class='hidden-480'>-</td>");
				} 
				if(array[i].hcluster){
					var td4 =$("<td class='hidden-480'>"
									+ "<a class=\"link\" href='#'>"+array[i].hcluster.hclusterNameAlias+"</a>"
									+ "</td>");//href=\"/detail/hcluster/" + array[i].gcecluster.hclusterId+"\"
				} else {
					var td4= $("<td class='hidden-480'>-</td>");
				}
				var td5 = $("<td>"
						+ array[i].ipAddr
						+ "</td>");
				var td6 = $("<td>"
						+ array[i].hostIp
						+ "</td>");
				var td9 = $("<td class='hidden-480'>"
						+ date('Y-m-d H:i:s',array[i].createTime)
						+ "</td>");
				var td7 = $("<td>"
						+translateStatus(array[i].status)
						+ "</td>");
					
				if(array[i].status == 3||array[i].status == 4||array[i].status == 14){
					var tr = $("<tr class=\"default-danger\"></tr>");
				}else if(array[i].status == 5||array[i].status == 13){
					var tr = $("<tr class=\"warning\"></tr>");
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td9).append(td7);
				tr.appendTo(tby);
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
	queryByPage();
	pageControl();
	$('[name = "popoverHelp"]').popover();
}
