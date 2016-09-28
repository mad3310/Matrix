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
	var mclusterId;
	var status;
	//modal显示创建进度
	/*$(document).on('click', "[name='buildStatusBoxLink']" , function(){
		mclusterId = $(this).closest('tr').find('td:first input').val();
		
		if($(this).html().indexOf("运行中")>=0){
			$('#buildStatusHeader').html("创建成功");
			status = "1";
		}else if($(this).html().indexOf("创建中")>=0){
			$('#buildStatusHeader').html("<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>创建中...");
			status = "2";
		}else if($(this).html().indexOf("创建失败")>=0){
			//$('#buildStatusHeader').html("<font color=\"red\">创建失败</font>");
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
						$('.queryOption').find('.widget-title').html('Container集群查询条件');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('Container集群查询条件');
			}

		}
		queryByPage();
	});
	$("#mclusterClearSearch").click(function(){
		var clearList = ["containerName","Physicalcluster","containeruser","containerStatus"];
		clearSearch(clearList);
		queryByPage();
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
			'clusterName':mclusterName,
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
		//url : "/mcluster/" + currentPage + "/" + recordsPerPage + "/" + mclusterName,
		url : queryUrlBuilder("/ecgce/cluster",queryCondition),
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
						+  "<a class=\"link\" href=\"/detail/gce/cluster/" + array[i].id+"\">"+array[i].clusterName+"</a>"
						+ "</td>");
				if(array[i].hcluster){
					var td3 = $("<td class='hidden-480'>"
							+ "<a class=\"link\" href=\"/detail/hcluster/" + array[i].hclusterId+"\">"+array[i].hcluster.hclusterNameAlias+"</a>"
							+ "</td>");
				} else {
					var td3 = $("<td class='hidden-480'> </td>");
				} 
				
				var userName='system';
				if(array[i].createUserModel) {
					userName = array[i].createUserModel.userName;
				}
				var td5 = $("<td>"
						+ userName
						+ "</td>");
				var td6 = $("<td class='hidden-480'>"
						+ date('Y-m-d H:i:s',array[i].createTime)
						+ "</td>");
				
				if(array[i].status == 3){
					var td7 = $("<td class='text-danger'>"
							+translateStatus(array[i].status)
							+"</a>"
							+ "</td>");
				}else{
					var td7 = $("<td>"
							+translateStatus(array[i].status)
							+ "</td>");
					
				}
				//var btnIcons=['start','stop','restart','extend'];
				var btnIcons=[];
				var td8=$(transStateHtml(btnIcons,array[i].status));	
				if(array[i].status == 3||array[i].status == 4||array[i].status == 14){
					var tr = $("<tr class=\"default-danger\"></tr>");
				}else if(array[i].status == 5||array[i].status == 13){
					var tr = $("<tr class=\"warning\"></tr>");
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(td1).append(td2).append(td3).append(td5).append(td6).append(td7).append(td8);
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
	
	//新加table click 函数
	$("#tby").unbind('click').click(function(event) {
		var _target=event.target;
		var type=$(_target).closest('a').attr('data-click-type')
		if(type){//操作列
			switch(type){
				case 'start':
					startMcluster((_target).closest('a'));
					break;
				case 'stop':
					stopMcluster((_target).closest('a'));
					break;
				case 'restart':
					restartCluster((_target).closest('a'));
					break;
				case 'delete':
					deleteMcluster((_target).closest('a'));
					break;
				case 'extend':
					addMemory((_target).closest('a'));
					break;
				default:
					break;
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

function searchAction(){
		$('#nav-search-input').bind('keypress',function(event){
	        if(event.keyCode == "13")    
	        {
	        	queryByPage();
	        }
	    });
}
	
//创建Container集群表单验证
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
                         message: 'Container集群名称不能为空!'
                     },
			          stringLength: {
			              max: 40,
			              message: 'Container集群名过长'
			          },regexp: {
		                  regexp: /^([a-zA-Z_0-9]*)$/,
  		                  message: "请输入字母数字或'_'"
                 	  },
                 	 remote: {
	                        message: 'Container集群名已存在!',
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
//查询Container集群创建过程
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
	var tr = $(obj).parents("tr").html();
	if (tr.indexOf("已停止") < 0){
		warn("当前状态无法执行启动操作!",3000);
		return 0;
	}
	function startCmd(){
		var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/gce/start',
			type:'post',
			data:{id : mclusterId},
			success:function(data){
				removeLoading();
				if(error(data)){
					warn(data.msgs);
					return;
				}else{
					success('服务启动成功！')
				}
				queryByPage();
			}
		});
	}
	confirmframe("启动container集群","启动集群大概需要几分钟时间!","请耐心等待...",startCmd);
}
function restartCluster(obj){
	var tr = $(obj).parents("tr").html();
	if (tr.indexOf("运行中") < 0 &&tr.indexOf("异常") < 0&&tr.indexOf("已停止") < 0&&tr.indexOf("危险") < 0&&tr.indexOf("严重危险") < 0 ){
		warn("当前状态无法执行重启操作!",3000);
		return 0;
	}
	function restartCmd(){
		var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/gce/restart ',
			type:'post',
			data:{id : mclusterId},
			success:function(data){
				removeLoading();
				if(error(data)){
					warn(data.msgs);
					return;
				}else{
					success('服务重启成功！')
				}
				queryByPage();
			}
		});
	}
	confirmframe("重启container集群","重启集群大概需要几分钟时间!","请耐心等待...",restartCmd);
}
function stopMcluster(obj){
	var tr = $(obj).parents("tr").html();
	if (tr.indexOf("运行中") < 0 &&tr.indexOf("异常") < 0&&tr.indexOf("危险") < 0&&tr.indexOf("严重危险") < 0 ){
		warn("当前状态无法执行关闭操作!",3000);
		return 0;
	}
	function stopCmd(){
		var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
		getLoading();
		$.ajax({
			cache:false,
			url:'/gce/stop',
			type:'post',
			data:{id : mclusterId},
			success:function(data){
				removeLoading();
				if(error(data)){
					warn(data.msgs);
					return;
				}else{
					success('服务停止成功！')
				}
				queryByPage();
			}
		});
	}
	confirmframe("关闭container集群","关闭container集群将不能提供服务,再次启动需要十几分钟!","您确定要关闭?",stopCmd);
}

function deleteMcluster(obj){
	/*warn("危险操作，本版本不启用...",3000);
	return;*/
	
	var tr = $(obj).parents("tr").html();
	if (tr.indexOf("删除中") >= 0){
		warn("正在删除集群,请耐心等待...",3000);
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
					var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
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
			 + "<div class=\"form-group\">"
			 + "<a class=\"kaptcha\" style=\"cursor:pointer;margin-right:10px;\"><img src=\"/kaptcha\" width=\"65\" height=\"30\" id=\"kaptchaImage\" style=\"margin-bottom: 2px\"/></a>"
			 + "<input type=\"text\" name=\"kaptcha\" style=\"width:120px;\" />"			 
             + "<p id=\"infoBlock\" style=\"width:20px;height：20px;display:inline;border:none;\"></p>"
			 + "</div>"
             + "</form>");
	
	confirmframe("删除container集群","删除container集群后将不能恢复!",form,deleteCmd);
	
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


function addMemory(obj){
	var tr = $(obj).parents("tr").html();
	if (tr.indexOf("扩容中") >= 0){
		warn("正在扩容集群,请耐心等待...",3000);
		return 0;
	}
	
	function extendCmd(){
		var value=$("[name='kaptcha']").val();
		$.ajax({
			cache:false,
			url:'/kaptcha',
			type:'post',
			data:{'kaptcha': value},	
			success:function(data){
				if(data.data == true){
					var mclusterId =$(obj).parents("tr").find('[name="mcluster_id"]').val();
					var mulvalue='';
					mulvalue=$('input[type=radio]:checked').val();
					$.ajax({
						cache:false,
						url:'/gce/memory',
						type:'post',
						data:{
							id : mclusterId,
							multiple:mulvalue},
						success:function(data){
							if(error(data)){
								warn(data.msgs);
								return;
							}else{
								success('扩容操作成功！')
							}
							queryByPage();
						}
					});		
					$('#dialog-confirm').dialog("close");
				}else if(data.data == false){
					refreshCode();
					warn('验证码输入错误')
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
			 + "<div class=\"form-group\">"
			 + "<div>2倍<input type=\"radio\" name=\"add\" value=\"2\" style=\"margin:-5px 25px 25px 10px ;\" checked/>4倍<input type=\"radio\" name=\"add\" value=\"4\" style=\"margin:-5px 25px 25px 10px ;\"/></div>"
			 + "<a class=\"kaptcha\" style=\"cursor:pointer;margin-right:10px;\"><img src=\"/kaptcha\" width=\"65\" height=\"30\" id=\"kaptchaImage\" style=\"margin-bottom: 2px\"/></a>"
			 + "<input type=\"text\" name=\"kaptcha\" style=\"width:120px;\" />"			 
             + "<p id=\"infoBlock\" style=\"width:20px;height：20px;display:inline;border:none;\"></p>"
			 + "</div>"
             + "</form>");
	
	confirmframe("扩充container集群内存","container集群内存扩充为当前的:",form,extendCmd);
	
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
// function of 扩容
function queryHcluster(){
//	var options1 = $('#hcluster_select');
	var options2=$('#Physicalcluster');
	getLoading();
	$.ajax({
		cache:false,
		url:'/hcluster/byType/gce',
		type:'get',
		dataType:'json',
		success:function(data){
			removeLoading();
			var array = data.data;
			for(var i = 0, len = array.length; i < len; i++){
				
				var option = $("<option value=\""+array[i].id+"\" data-hclsName='"+array[i].hclusterName+"'>"
								+array[i].hclusterNameAlias
								+"</option>");
//				options1.append(option);
				options2.append(option)
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
	// 2015-7-7 物理机查询 select
	queryHcluster();
	queryUser();
}
