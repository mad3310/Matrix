var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var queryBuildStatusrefresh;//刷新handler
	
$(function(){
	//初始化 
	page_init();
	/*动态加载下拉列表值*/
	var sltArray = [0,1,15];
	addSltOpt(sltArray,$("#hclusterStatus"));
	
	$('[name = "popoverHelp"]').popover();
	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	
	enterKeydown($(".page-header > .input-group input"),queryByPage);
	
});

function queryByPage() {
	var hclusterName = $("#hclusterName").find('option:selected').attr('data-hclsName')?$("#hclusterName").find('option:selected').attr('data-hclsName'):'';
	// var hclusterIndex = $("#hclusterIndex").val()?$("#hclusterIndex").val():'';
	var status = $("#hclusterStatus").val()?$("#hclusterStatus").val():'';
	var queryCondition = {
			'currentPage':currentPage,
			'recordsPerPage':recordsPerPage,
			'hclusterName':hclusterName,
			// 'hclusterNameAlias':hclusterIndex,
			/*'createTime':createTime,*/
			'status':status
		}
	
	$("#tby tr").remove();
	//var mclusterName = $("#nav-search-input").val()?$("#nav-search-input").val():'null';
	getLoading();
	$.ajax({
		cache:false,
		type : "get",
		//url : "/hcluster/" + currentPage + "/" + recordsPerPage + "/" + mclusterName,
		url : queryUrlBuilder("/hcluster/list",queryCondition),
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
								+"<input name=\"hcluster_id\" value= \""+array[i].id+"\" type=\"checkbox\" class=\"ace\"/>"
								+"<span class=\"lbl\"></span>"
								+"</label>"
							+"</td>");
				var td2 = $("<td>"
						+ array[i].hclusterNameAlias
						+ "</td>");
				var td3 = $("<td>"
						+  "<a class=\"link\" href=\"/detail/hcluster/" + array[i].id+"\">"+array[i].hclusterName+"</a>"
						+ "</td>");
				var td4 = $("<td class='hidden-480'>"
						+ array[i].type
						+ "</td>");
				var td5 = $("<td class='hidden-480'>"
						+ date('Y-m-d H:i:s',array[i].createTime)
						+ "</td>");
				var tdContainerIps='<td><span class="td-container-ips text-ellipsis" title="{containerIps}">{containerIps}</span></td>'.replace(/{containerIps}/g,array[i].containerIps);
				var td6 = $("<td><a>"
						+ translateStatus(array[i].status,'hcluster')
						+ "</a></td>");
				var td7 = $("<td>"
						+"<div class=\"action-buttons\">"
						+"<a class=\"red\" href=\"#\" onclick=\"deleteHcluster(this)\" onfocus=\"this.blur();\" title=\"删除\" data-toggle=\"tooltip\" data-placement=\"right\">"
					    +"<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>"
						+"</a>"
						+"<a class=\"red\" href=\"#\" onclick=\"modifyHcluster(this)\" onfocus=\"this.blur();\" title=\"编辑\" data-toggle=\"tooltip\" data-placement=\"right\">"
					    +"<i class=\"ace-icon fa fa-pencil-square-o bigger-120\"></i>"
						+"</a>"
						+"</div>"
						+ "</td>"
				);
				
				if(array[i].status == 3){
					var tr = $("<tr class=\"default-danger\"></tr>");
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(tdContainerIps).append(td6).append(td7);
				tr.appendTo(tby);
			}//循环json中的数据 
			
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
	
//创建Container集群表单验证
function formValidate() {
	$("#form-hcluster").bootstrapValidator({
	  message: '无效的输入',
         feedbackIcons: {
             valid: 'glyphicon glyphicon-ok',
             invalid: 'glyphicon glyphicon-remove',
             validating: 'glyphicon glyphicon-refresh'
         },
         fields: {
       	  hclusterName: {
                 validMessage: '请按提示输入',
                 validators: {
                     notEmpty: {
                         message: '物理机集群名称不能为空!'
                     },
			          stringLength: {
			              max: 40,
			              message: '物理机集群名过长'
			          },regexp: {
		                  regexp: /^([a-zA-Z_0-9]*)$/,
  		                  message: "请输入字母数字或'_'"
                 	  },
                 	 remote: {
	                        message: '物理机集群名已存在!',
	                        url: "/hcluster/validate"
	                    }
	             }
         	},
         	containerIps: {
                  validators: {
                	  regexp: {
                          regexp: /^\d[\d|,|\.\/]*\d$/,
                          message: '请按提示格式输入'
                      }
 	             }
          	}
         }
     }).on('error.field.bv', function(e, data) {
    	 $('#botton-hcluster-submit').addClass("disabled");
     }).on('success.field.bv', function(e, data) {
    	 $('#botton-hcluster-submit').removeClass("disabled");
     });
}
var hclusterModalType='';
function openModalHclusterCreate(){
	hclusterModalType='create';
	$('#botton-hcluster-submit').addClass("disabled");
	$('#modal-hcluster #inputCurrentHclusterId').val('');
	$('#form-hcluster input').val('')
	$('#form-hcluster select[name="status"]').val('1');
	$('#form-hcluster select[name="type"]').select2("val", "");
	$('#modal-hcluster').modal('show');
}

function submitHcluster(){
	if(hclusterModalType==='create'){
		$.ajax({
			cache:false,
			type : "post",
			url : "/hcluster",
			data :$('#form-hcluster').serialize(),
			success:function (data){
				if(error(data)) return;
				$('#botton-hcluster-submit').addClass('disabled');
				$('#modal-hcluster').modal('hide');
				//延时一秒刷新列表
				setTimeout("queryByPage()",1000);
			}
		});
	}
	else if(hclusterModalType==='modify'){
		var currentHclusterId= $('#modal-hcluster #inputCurrentHclusterId').val();
		$.ajax({
			cache:false,
			type : "post",
			url : "/hcluster/{hclusterId}".replace('{hclusterId}',currentHclusterId),
			data :$('#form-hcluster').serialize(),
			success:function (data){
				if(error(data)) return;
				$('#botton-hcluster-submit').addClass('disabled');
				$('#modal-hcluster').modal('hide');
				//延时一秒刷新列表
				setTimeout("queryByPage()",1000);
			}
		});
	}
	else{}
}

function deleteHcluster(obj){
	var tr = $(obj).parents("tr");
	var hclusterId =tr.find('[name="hcluster_id"]').val();
	$.ajax({
		cache:false,
		url:'/hcluster/isExistHostOnHcluster/validate',
		type:'post',
		data:{ 'hclusterId' : hclusterId },
		success:function(data){
			if(data.valid){  //data.valid为true时可删除
				function deleteCmd(){
					$.ajax({
						cache:false,
						url:'/hcluster/'+hclusterId,
						type:'delete',
						success:function(data){
							if(error(data)) return;
							queryByPage();
						}
					});
				}
				confirmframe("删除物理机集群","删除物理机集群后将不能恢复!","您确定要删除?",deleteCmd);
			}else{
				warn("该集群中含有物理机,删除完物理机后,才能执行此操作!",3000);
			}
		}
	});
}

function modifyHcluster(obj){
	var tr = $(obj).parents("tr");
	var hclusterId =tr.find('[name="hcluster_id"]').val();
	hclusterModalType='modify';
	$('#botton-hcluster-submit').removeClass("disabled");
	$.ajax({
		cache:false,
		type : "get",
		url : "/hcluster/detail/{hclusterId}".replace('{hclusterId}',hclusterId),
		dataType : "json", 
		success : function(data) {
			$('#modal-hcluster #inputCurrentHclusterId').val(hclusterId);
			$('#form-hcluster #hclusterNameAlias').val(data.data.hclusterNameAlias);
			$('#form-hcluster #hclusterName').val(data.data.hclusterName);
			$('#form-hcluster #containerIps').val(data.data.containerIps);
			$('#form-hcluster #isHclusterEnable').val(data.data.status);
			$('#form-hcluster select[name="type"]').select2("val", data.data.type.split(','));
			$('#modal-hcluster').modal('show');
		}
	});
}
function searchAction(){
	$("#hclusterSearch").click(function(){
		var iw=document.body.clientWidth;
		if(iw>767){//md&&lg
		}else{
			$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
			$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
			var qryStr='';
			var qryStr1=$('#hclusterName').val();var qryStr2=$('#hclusterIndex').val();var qryStr3;
			if($('#hclusterStatus').val()){
				qryStr3=translateStatus($('#hclusterStatus').val());
			}
			if(qryStr1){
				qryStr+='<span class="label label-success arrowed">'+qryStr1+'<span class="queryBadge" data-rely-id="hclusterName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr2){
				qryStr+='<span class="label label-warning arrowed">'+qryStr2+'<span class="queryBadge" data-rely-id="hclusterIndex"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr3){
				qryStr+='<span class="label label-purple arrowed">'+qryStr3+'<span class="queryBadge" data-rely-id="hclusterStatus"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr){
				$('.queryOption').find('.widget-title').html(qryStr);
				$('.queryBadge').click(function(event) {
					var id=$(this).attr('data-rely-id');
					$('#'+id).val('');
					$(this).parent().remove();
					queryByPage();
					if($('.queryBadge').length<=0){
						$('.queryOption').find('.widget-title').html('物理机集群查询条件');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('物理机集群查询条件');
			}

		}
		queryByPage();
	});
}

function clearAction(){
	$("#hclusterSearchClear").click(function(){
		var clearList = ["hclusterName","hclusterIndex","hclusterStatus"];
		clearSearch(clearList);
	});
}
function queryHcluster(){
	var options=$('#hclusterName');
	getLoading();
	$.ajax({
		cache:false,
		url:'/hcluster',
		type:'get',
		dataType:'json',
		success:function(data){
			removeLoading();
			var array = data.data;
			for(var i = 0, len = array.length; i < len; i++){
				
				var option = $("<option value=\""+array[i].id+"\" data-hclsName='"+array[i].hclusterName+"'>"
								+array[i].hclusterNameAlias
								+"</option>");
				options.append(option);
			}
			initChosen();
		}
	});
}
function page_init(){
	queryHcluster();
	$('#nav-search').addClass("hidden");
	queryByPage();
	formValidate();
	pageControl();
	searchAction();
	clearAction();
	initChosen("typeOption","270");
}
