var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var queryBuildStatusrefresh;//刷新handler
	
$(function(){
	//初始化 
	page_init();
	
	/*动态加载界面下拉列表值*/
	var sltArray = [1,2,3,7,8,9,10,13,14];
	addSltOpt(sltArray,$("#containerStatus"));
	
	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	var mclusterId;
	var status;
	//modal显示创建进度
	/*$(document).on('click', "[name='buildStatusBoxLink']" , function(){
		mclusterId = $(this).closest('tr').find('td:first input').val();
		
		if($(this).html().indexOf("运行中")>=0){
			$('#buildStatusHeader').html("创建成功");
			status = "1";
		}else if($(this).html().indexOf("创建中")>=0){
			$('#buildStatusHeader').html("<i class='ace-icon fa fa-spinner fa-spin green bigger-125'></i>创建中...");
			status = "2";
		}else if($(this).html().indexOf("创建失败")>=0){
			//$('#buildStatusHeader').html("<font color='red'>创建失败</font>");
			$('#buildStatusHeader').html("创建失败");
			status = "3";
		}
		queryBuildStatus(mclusterId,"new");
	});*/
	
	$('#create-mcluster-status-modal').on('shown.bs.modal', function(){
		if(status == "2") {
			queryBuildStatusrefresh = setInterval(function() {  
				queryBuildStatus(mclusterId,"update");
			},5000);
		}
	}).on('hidden.bs.modal', function (e) {
		queryBuildStatusrefresh = window.clearInterval(queryBuildStatusrefresh);
		location.reload();
	});
	
	$("#mclusterSearch").click(function(){
		var iw=document.body.clientWidth;
		if(iw>767){//md&&lg
		}else{
			$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
			$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
			var qryStr='';
			var qryStr1=$('#containerName').val();var qryStr2=$("#Physicalcluster").find('option:selected').text();var qryStr3=$("#containeruser").find('option:selected').text();var qryStr4;
			if($('#containerStatus').val()){
				qryStr4=translateStatus($('#containerStatus').val());
			}
			if(qryStr1){
				qryStr+='<span class="label label-success arrowed">'+qryStr1+'<span class="queryBadge" data-rely-id="containerName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr2){
				qryStr+='<span class="label label-warning arrowed">'+qryStr2+'<span class="queryBadge" data-rely-id="Physicalcluster"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr3){
				qryStr+='<span class="label label-purple arrowed">'+qryStr3+'<span class="queryBadge" data-rely-id="containeruser"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr4){
				qryStr+='<span class="label label-yellow arrowed">'+qryStr4+'<span class="queryBadge" data-rely-id="containerStatus"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr){
				$('.queryOption').find('.widget-title').html(qryStr);
				$('.queryBadge').click(function(event) {
					var id=$(this).attr('data-rely-id');
					$('#'+id).val('');
					$(this).parent().remove();
					queryByPage();
					if($('.queryBadge').length<=0){
						$('.queryOption').find('.widget-title').html('RDS集群查询条件');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('RDS集群查询条件');
			}

		}
		queryByPage();
	});
	$("#mclusterClearSearch").click(function(){
		var clearList = ["containerName","Physicalcluster","containeruser","containerStatus"];
		clearSearch(clearList);
	});
	
	enterKeydown($(".page-header > .input-group input"),queryByPage);
});

function queryByPage() {
	var mclusterName = $("#containerName").val()?$("#containerName").val():'';
	var hclusterName = $("#Physicalcluster").find('option:selected').attr('data-hclsName')?$("#Physicalcluster").find('option:selected').attr('data-hclsName'):'';
	var userName = $("#containeruser").find('option:selected').text()?$("#containeruser").find('option:selected').text():'';
	var status = $("#containerStatus").val()?$("#containerStatus").val():'';
	var queryCondition = {
			'currentPage':currentPage,
			'recordsPerPage':recordsPerPage,
			'mclusterName':mclusterName,
			'hclusterName':hclusterName,
			'userName':userName,
			/*'createTime':createTime,*/
			'status':status
		}
	
	$("#tby tr").remove();
	//var mclusterName = $("#nav-search-input").val()?$("#nav-search-input").val():'null';
	getLoading();
	$.ajax({
		cache:false,
		type : "get",
		url : queryUrlBuilder("/mcluster/list",queryCondition),
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var array = data.data.data;
			var tby = $("#tby");
			var totalPages = data.data.totalPages;
			var records="";
			var recordsArray=[];
			for (var i = 0, len = array.length; i < len; i++) {
				var tempObj=array[i];
				var td1 ="<td class='center'>"
								+"<label class='position-relative'>"
								+"<input name='mcluster_id' value='"+tempObj.id+"' type='checkbox' class='ace'/>"
								+"<span class='lbl'></span>"
								+"</label>"
							+"</td>";
				var td2="<td>"
						+  "<a class='link' href='/detail/mcluster/" + tempObj.id+"'>"+tempObj.mclusterName+"</a>"
						+"</td>";
				if(tempObj.hcluster){
					var td3="<td class='hidden-480'>"
							+ "<a class='link' href='/detail/hcluster/" + tempObj.hclusterId+"'>"+tempObj.hcluster.hclusterNameAlias+"</a>"
							+ "</td>";
				} else {
					var td3="<td class='hidden-480'>-</td>";
				} 
				var userName=tempObj.createUserModel?tempObj.createUserModel.userName:"system";
				var td5 ="<td>"+userName+"</td>";
				var td6 ="<td class='hidden-480'>"+date('Y-m-d H:i:s',tempObj.createTime)+"</td>";
				var tempStatus=tempObj.status;
				var td7 ="<td>"+stateTransform(tempStatus,"rdsMcluster")+"</td>";

				var startHtml='',stopHtml='',expandHtml='',deleteHtml='';
				var startOs=containerClusterOs(tempStatus,"rds","start"),
				stopOs=containerClusterOs(tempStatus,"rds","stop"),
				expandOs=containerClusterOs(tempStatus,"rds","expand"),
				deleteOs=containerClusterOs(tempStatus,"rds","delete");

				startHtml=startOs==0?startHtml:containerOsHtml("rds","start");
				stopHtml=stopOs==0?stopHtml:containerOsHtml("rds","stop");
				expandHtml=expandOs==0?expandHtml:containerOsHtml("rds","expand");
				deleteHtml=deleteOs==0?deleteHtml:containerOsHtml("rds","delete");
				var td8 ="<td data-status='"+tempStatus+"'>"
							+"<div class='hidden-sm hidden-xs  action-buttons'>"
							+startHtml+stopHtml+expandHtml+deleteHtml
							+"</div>"
							+'<div class="hidden-md hidden-lg">'
							+'<div class="inline pos-rel">'
							+'<button class="btn btn-minier btn-yellow dropdown-toggle" data-toggle="dropdown" data-position="auto">'
								+'<i class="ace-icon fa fa-caret-down icon-only bigger-120"></i>'
							+'</button>'
							+'<ul class="dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">'
								+'<li>'+startHtml+'</li>'
								+'<li>'+stopHtml+'</li>'
								+'<li>'+expandHtml+'</li>'
								+'<li>'+deleteHtml+'</li>'
							+'</ul></div></div>'
						+ "</td>";
				var tr="";	
				if(tempObj.status == 3||tempObj.status == 4||tempObj.status == 14){
					tr ="<tr class='default-danger'>";
				}else if(tempObj.status == 5||tempObj.status == 13){
					tr ="<tr class='warning'>";
				}else{
					tr ="<tr>";
				}
				recordsArray.push(tr,td1,td2,td3,td5,td6,td7,td8,"</tr>");
			}
			tby.append(recordsArray.join(''));
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
			//执行中状态查询，成功或失败后刷新页面
			var mclusterExecutingStatus=[2,7,8,10,15,17];
			array.filter(function(mcluster){
				return mclusterExecutingStatus.indexOf(mcluster.status)>-1;
			}).forEach(function(mcluster){
				var intervalId =setInterval(function(){
					$.ajax({
						cache:false,
						type : "get",
						url : "/mcluster/list?id="+mcluster.id,
						dataType : "json", /*这句可用可不用，没有影响*/
						success : function(data) {
							if(data.result===1 && data.data &&  data.data.data && data.data.data[0]){
								if(mclusterExecutingStatus.indexOf(data.data.data[0].status)==-1){
									clearInterval(intervalId);
									queryByPage();
								}
							}
							else{
								clearInterval(intervalId);
								queryByPage();
							}
						}}
					);
				},8000);
			});
			
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

function searchAction(){
		$('#nav-search-input').bind('keypress',function(event){
	        if(event.keyCode == "13")    
	        {
	        	queryByPage();
	        }
	    });
	}
	
//创建RDS集群表单验证
function formValidate() {
	$("#create-mcluster-form").bootstrapValidator({
	  message: '无效的输入',
         feedbackIcons: {
             valid: 'glyphicon glyphicon-ok',
             invalid: 'glyphicon glyphicon-remove',
             validating: 'glyphicon glyphicon-refresh'
         },
         fields: {
       	  mclusterName: {
                 validMessage: '请按提示输入',
                 validators: {
                     notEmpty: {
                         message: 'RDS集群名称不能为空!'
                     },
			          stringLength: {
			              max: 40,
			              message: 'RDS集群名过长'
			          },regexp: {
		                  regexp: /^([a-zA-Z_0-9]*)$/,
  		                  message: "请输入字母数字或'_'"
                 	  },
                 	 remote: {
	                        message: 'RDS集群名已存在!',
	                        url: "/mcluster/validate"
	                    }
	             }
         	}	
         }
     }).on('error.field.bv', function(e, data) {
    	 $('#create-mcluster-botton').addClass("disabled");
     }).on('success.field.bv', function(e, data) {
    	 $('#create-mcluster-botton').removeClass("disabled");
     });
}
//查询RDS集群创建过程
function queryBuildStatus(mclusterId,type) {	//type(update或new)
	if(type == "new"){
		$("#build_status_tby tr").remove();
	}
	getLoading();
	$.ajax({
		cache:false,
		type : "get",
		url : "/build/mcluster/"+mclusterId,
		dataType : "json", /*这句可用可不用，没有影响*/
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var array = data.data;
			var build_status_tby = $("#build_status_tby");
			
			for (var i = 0, len = array.length; i < len; i++) {
				var td1 = $("<td>"
						+ array[i].step
						+"</td>");
				var td2 = $("<td>"
						+ array[i].stepMsg
						+"</td>");
				if(array[i].startTime != null )
				{
					var td3 = $("<td>"
							+ date('Y-m-d H:i:s',array[i].startTime)
							+ "</td>");
				}else{
					var td3 = $("<td>\-</td>");
				}
				if(array[i].endTime != null)
				{
					var td4 = $("<td>"
							+ date('Y-m-d H:i:s',array[i].endTime)
							+ "</td>");
				}else{
					var td4 = $("<td>\-</td>");
				}
				if(array[i].msg != null)
				{
					var td5 = $("<td>"
							+ array[i].msg
							+ "</td>");
				}else{
					var td5 = $("<td>\-</td>");
				}
				
				if(array[i].status == 1){
					var td6 = $("<td>"
							+"<a class=\"green\"><i class=\"ace-icon fa fa-check bigger-120\">成功</a>"
							+ "</td>");
				}else if(array[i].status == 0){
					var td6 = $("<td>"
							+"<a class=\"red\"><i class=\"ace-icon fa fa-times red bigger-120\">失败</a>"
							+ "</td>");
				}else if(array[i].status == 2){
					var td6 = $("<td>"
							+"<a style=\"text-decoration:none;\" class=\"green\"><h5><i class=\"ace-icon fa fa-spinner fa-spin green bigger-120\"/>运行中</h5></a>"
							+ "</td>");
				}else{
					var td6 = $("<td>"
							+"<a class=\"orange\"><i class=\"ace-icon fa fa-coffee orange bigger-120\">等待</a>"
							+ "</td>");
				}
					
				if(array[i].status == 0){
					var tr = $("<tr class=\"danger\"></tr>");
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6);
				if(type == "new"){
					tr.appendTo(build_status_tby);
				}else{
					build_status_tby.find("tr:eq("+i+")").html(tr.html());
				}
			}
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			error(XMLHttpRequest);
			return false;
		}
	});
 }

function createMcluster(){
	getLoading();
	$.ajax({
		cache:false,
		type : "post",
		url : "/mcluster",
		data :$('#create-mcluster-form').serialize(),
		success:function (data){
			removeLoading();
			if(error(data)) return;
			$('#create-mcluster-form').find(":input").not(":button,:submit,:reset,:hidden").val("").removeAttr("checked").removeAttr("selected");
			$('#create-mcluster-form').data('bootstrapValidator').resetForm();
			$('#create-mcluster-botton').addClass('disabled');
			$('#create-mcluster-modal').modal('hide');
			//延时一秒刷新列表
			setTimeout("queryByPage()",1000);
		}
	});
}
function startMcluster(obj){
	var _target=$(obj);
	var status=_target.parents("td").attr('data-status');
	if(!containerClusterOs(status,"rds","start")){
		warn("该集群所处状态不可被启动",3000);
		return 0;
	}

	function startCmd(){
		var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/mcluster/start',
			type:'post',
			data:{mclusterId : mclusterId},
			success:function(data){
				removeLoading();
				if(error(data)) return;
				queryByPage();
			}
		});
	}
	confirmframe("启动RDS集群","启动集群大概需要几分钟时间!","请耐心等待...",startCmd);
}
function stopMcluster(obj){
	var _target=$(obj);
	var status=_target.parents("td").attr('data-status');
	if(!containerClusterOs(status,"rds","stop")){
		warn("该集群所处状态不可被停止",3000);
		return 0;
	}
	
	function stopCmd(){
		var mclusterId =_target.parents("tr").find('[name="mcluster_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/mcluster/stop',
			type:'post',
			data:{mclusterId : mclusterId},
			success:function(data){
				removeLoading();
				if(error(data)) return;
				queryByPage();
			}
		});
	}
	confirmframe("关闭RDS集群","关闭RDS集群将不能提供服务,再次启动需要十几分钟!","您确定要关闭?",stopCmd);
}
function deleteMcluster(obj){
	var _target=$(obj);
	var status=_target.parents("td").attr('data-status');
	if(!containerClusterOs(status,"rds","delete")){
		warn("该集群所处状态不可被删除",3000);
		return 0;
	}

	function deleteCmd(){
		var value=$("[name='kaptcha']").val();
		$.ajax({
			cache:false,
			url:'/kaptcha',
			type:'post',
			data:{'kaptcha': value},	
			success:function(data){
				if(data.data == true){
					var mclusterId=_target.parents("tr").find('[name="mcluster_id"]').val();
					$.ajax({
						cache:false,
						url:'/mcluster/'+mclusterId,
						type:'delete',
						success:function(data){
							if(typeof(data) == 'string'){
								data = JSON.parse(data)
							};
							if(error(data)) return;
							queryByPage();
						}
					});		
					$('#dialog-confirm').dialog("close");
				}else if(data.data == false){
					refreshCode();
					$('.warning-info').remove();
					$('.success-info').remove();
					if($('.warning-info').length == 0){						
						if($('.warning-info').length == 0){
							var _block = $(' <i class="red warning-info fa fa-exclamation-circle"></i>');
							$("#infoBlock").html(_block);
						}	
					}					
				}
			}
		})
		
	}
	
	/*验证码DOM*/
	var form = $("<form>"
			 + "<div class='form-group'>"
			 + "<a class='kaptcha' style='cursor:pointer;margin-right:10px;'><img src='/kaptcha' width='65' height='30' id='kaptchaImage' style='margin-bottom: 2px'/></a>"
			 + "<input type='text' name='kaptcha' style='width:120px;' />"			 
             + "<p id='infoBlock' style='width:20px;height：20px;display:inline;border:none;'></p>"
			 + "</div>"
             + "</form>");
	
	confirmframe("删除RDS集群","删除RDS集群后将不能恢复!",form,deleteCmd);
	
	/*刷新验证码*/
	function refreshCode(){
		var dt = new Date();
		$("#kaptchaImage").attr('src', '/kaptcha?t='+ dt);
	}
	
	/*输入框改变时绑定事件*/
	$("input[name='kaptcha']").on('input',function(e){  
		$('.warning-info').remove();
		$('.success-info').remove();
		var value=$("[name='kaptcha']").val();
		
		$.ajax({
			cache:false,
			url:'/kaptcha',
			type:'post',
			data:{'kaptcha': value},	
			success:function(data){
				if(data.data == false){
					$('.success-info').remove();
					if($('.warning-info').length == 0){
						var _block = $(' <i class="red warning-info fa fa-exclamation-circle"></i>');
						$("#infoBlock").html(_block);
					}	
				}else if(data.data==true){
					$('.warning-info').remove();
					if($('.success-info').length == 0){
						var _block = $(' <i class="green success-info fa  fa-check"></i>')
						$("#infoBlock").html(_block);
					}					
				}		
			}	
		});
		
	});
	/*焦点移开事件*/
	/*$("input[name='kaptcha']").blur(function(){
		$('.warning-info').remove();
		$('.success-info').remove();
		var value=$("[name='kaptcha']").val('');
	});*/
	
	/*点击验证码刷新验证码*/	
	$(".kaptcha").bind('click',function(){
		refreshCode();
	});
	
	/*setInterval(refreshCode,60000);*/
}
function expandMcluster(obj){
	var tr = $(obj).parents("tr").html();
	if (tr.indexOf("运行中") < 0){
		warn("当前状态无法执行扩容操作",3000);
		return 0;
	}
	var form = $("<form>"
			 + "<div class='form-group'>"
			 +"<div class='col-md-6'><span>1个</span><input type='radio' name='count' style='margin-left:5px;vertical-align:top' value='1' checked/></div>"
			 +"<div class='col-md-6'><span>2个</span><input type='radio' name='count' value='2' style='margin-left:5px;vertical-align:top'/></div>"
			 +"<div class='clearfix'></div>"
			 + "</div>"
             + "</form>");
	var expandCmd=function(){
		var value=$("input[type=radio]:checked").val();
		var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
		$.ajax({
			cache:false,
			url:'/mcluster/dilatation',
			type:'post',
			data:{'count': value,
				'mclusterId':mclusterId },	
			success:function(data){
				if(typeof(data) == 'string'){
					data = JSON.parse(data)
				};
				if(error(data)) return;
				queryByPage();
			}
		});
	}
	confirmframe("扩容RDS集群","选择扩容数量",form,expandCmd);
}
function queryHcluster(){
//	var options1 = $('#hcluster_select');
	var options2=$('#Physicalcluster');
	getLoading();
	$.ajax({
		cache:false,
		url:'/hcluster/byType/RDS',
		type:'get',
		dataType:'json',
		success:function(data){
			removeLoading();
			var array = data.data;
			for(var i = 0, len = array.length; i < len; i++){
				
				var option = $("<option value='"+array[i].id+"' data-hclsName='"+array[i].hclusterName+"'>"
								+array[i].hclusterNameAlias
								+"</option>");
//				options1.append(option);
				options2.append(option);
			}
			initChosen();
		}
	});
}


function page_init(){
	queryByPage();
	searchAction();
	formValidate();
	pageControl();
	$('[name = "popoverHelp"]').popover();
	// 新添 2015-7-7
	queryHcluster();
	queryUser();
}
