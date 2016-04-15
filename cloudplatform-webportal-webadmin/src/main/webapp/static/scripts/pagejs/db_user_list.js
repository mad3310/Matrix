var currentPage = 1; //第几页 
var recordsPerPage = 15; //每页显示条数
var currentSelectedLineDbName = 1;
	
 $(function(){
	//初始化
	page_init();
	
	var sltArray = [6,3,0];
	addSltOpt(sltArray,$("#dbuserStatus"));
	
	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	
	/*查询功能*/
	$("#dbuserSearch").unbind('click').click(function(){
		var iw=document.body.clientWidth;
		if(iw>767){//md&&lg
		}else{
			$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
			$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
			var qryStr='';
			var qryStr1=$('#userName').val();var qryStr2=$('#userDb').val();var qryStr3=$('#userAuthority').val();var qryStr4=$('#userIp').val();var qryStr5;
			if($('#dbuserStatus').val()){
				qryStr5=translateStatus($('#dbuserStatus').val());
			}
			if(qryStr1){
				qryStr+='<span class="label label-success arrowed">'+qryStr1+'<span class="queryBadge" data-rely-id="userName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr2){
				qryStr+='<span class="label label-warning arrowed">'+qryStr2+'<span class="queryBadge" data-rely-id="userDb"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr3){
				var author={1:'管理员',2:'只读用户',3:'读写用户'}
				qryStr+='<span class="label label-purple arrowed">'+author[qryStr3]+'<span class="queryBadge" data-rely-id="userAuthority"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr4){
				qryStr+='<span class="label label-yellow arrowed">'+qryStr4+'<span class="queryBadge" data-rely-id="userIp"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr5){
				qryStr+='<span class="label label-pink arrowed">'+qryStr5+'<span class="queryBadge" data-rely-id="dbuserStatus"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr){
				$('.queryOption').find('.widget-title').html(qryStr);
				$('.queryBadge').click(function(event) {
					var id=$(this).attr('data-rely-id');
					$('#'+id).val('');
					$(this).parent().remove();
					queryByPage();
					if($('.queryBadge').length<=0){
						$('.queryOption').find('.widget-title').html('数据库查询条件');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('数据库查询条件');
			}

		}
		queryByPage();
	});
	$("#dbuseClearSearch").click(function(){
		var clearList = ["userDb","userName","userAuthority","userIp","dbuserStatus"];
		clearSearch(clearList);
	})
	enterKeydown($(".page-header > .input-group input"),queryByPage);
	/*页面按钮初始化 --start*/
    $(".toCreateAccount").unbind('click').click(function () {           //切换到创建账户
        $("#accountList").addClass("hide");
        $("#modifyAccountTab").addClass("hide");
        $("#newAccountTab").removeClass("hide");
        asyncDbUserIpData();
    })
    $(".toAccountList").unbind('click').click(function () {             //切换到账户列表
        $("#newAccountTab").addClass("hide");
        $("#modifyAccountTab").addClass("hide");
        $("#accountList").removeClass("hide");
    })
});	
function asyncDbUserIpData(){
    // cn.GetData("/dbIp/"+$("#dbId").val()+"/null",dbUser.DbUserIpHandler);   //创建用户加载IP
}
function buildUser() {
	var ids = $("[name='db_user_id']:checked");
	var str="";
	var flag = 0; //0:无数据 -1:错误
	ids.each(function(){
		if($(this).closest("tr").children().last().html() == "正常"){
			$.gritter.add({
				title: '警告',
				text: '创建用户只能选择待审核数据!',
				sticky: false,
				time: '3000',
				class_name: 'gritter-warning'
			});
			flag = -1;
			return false;
		}else{
			str +=($(this).val())+",";
 			flag += 1;
		}
	});
	
	if(flag > 0) {
		getLoading();
		$.ajax({ 
			cache:false,
			type : "post",
			url : "/dbUser",
			dataType : "json",
			data : {'dbUserId':str},
			success : function(data) {
				removeLoading();
				if(error(data)) return;
				if(data.result == 1) {
					$("#titleCheckbox").attr("checked", false);
					queryByPage(currentPage,recordsPerPage);
				}
			}
		});
	} else if (flag == 0){
		$.gritter.add({
		title: '警告',
		text: '请选择数据！',
		sticky: false,
		time: '5',
		class_name: 'gritter-warning'
	});

	return false;
	}else{
		return false;
	}
}

function queryByPage(){
	var dbName = $("#userDb").val()?$("#userDb").val():'';
	var userName = $("#userName").val()?$("#userName").val():'';
	var userAuthority = $("#userAuthority").val()?$("#userAuthority").val():'';
	// var acceptIp = $("#userIp").val()?$("#userIp").val():'';
	var status = $("#dbuserStatus").val()?$("#dbuserStatus").val():'';
	var queryCondition = {
			'currentPage':currentPage,
			'recordsPerPage':recordsPerPage,
			'dbName':dbName,
			'username':userName,
			'type':userAuthority,
			//'acceptIp':acceptIp.replace(/\%/g,"%25"),
			/*'createTime':createTime,*/
			'status':status
		}
	$("#tby tr").remove();
	var dbName = $("#nav-search-input").val()?$("#nav-search-input").val():'null';
	getLoading();
	$.ajax({ 
		cache:false,
		type : "get",
		//url : "/dbUser/" + currentPage + "/" + recordsPerPage+"/" + dbName,
		url : queryUrlBuilder("/dbUser",queryCondition),
		dataType : "json", /*这句可用可不用，没有影响*/
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			removeLoading();
			if(error(data)) return;
			var array=data.data.data;
			var tby = $("#tby");
			var totalPages = data.data.totalPages;
			for (var i = 0, len = array.length; i < len; i++) {
				if(array[i].db == undefined || array[i].db == null) continue;
				var td1 = $("<td class=\"center\">"
								+"<label class=\"position-relative\">"
								+"<input name=\"db_user_id\" value= \""+array[i].id+"\" type=\"checkbox\" class=\"ace\"/>"
								+"<span class=\"lbl\"></span>"
								+"</label>"
							+"</td>");
				var td2 = $("<td>"
						+ array[i].username
						+ "</td>");
				var td3 = $("<td class='hidden-xs'>"
						+ "<a class=\"link\" href=\"/detail/db/" + array[i].dbId+"\">"+array[i].db.dbName+"</a>"
						+ "</td>");
				// if(array[i].type == 1){
				// 	var td4 = $("<td>"
				// 			    + "管理员"
				// 			    + "</td>");
				// }else if(array[i].type == 2){
				// 	var td4 = $("<td>"
				// 		    + "只读用户"
				// 		    + "</td>");
				// }else{
				// 	var td4 = $("<td>"
				// 			    + "读写用户"
				// 			    + "</td>");
				// }
				// var td5 = $("<td>"
				// 		+ array[i].acceptIp
				// 		+ "</td>");
				var td6 = $("<td class='hidden-xs'>"
						+ array[i].maxConcurrency
						+ "</td>");
				var td7 = $("<td><a>"
						+ translateStatus(array[i].status)
						+ "</a></td>");
				var td8; 
				if(array[i].descn){
					td8=$("<td class='hidden-xs'><span>"
						+array[i].descn
						+"</span></td>");
				}else{td8=$("<td class='hidden-xs'><span>-"
						+"</span></td>")}
				
				var td9=$("<td class='hidden-xs'>" 
						+ "<a class=\"dbuser-list-ip-privilege\" href='javascript:void(0)' data-db-id="+array[i].dbId+">ip访问权限</a><span class=\"text-explode\">"
						+ "|</span><a class=\"dbuser-list-reset-password\"  href=\"javascript:void(0);\" data-db-id="+array[i].dbId+">重置密码</a><span class=\"text-explode\">"
	                    
	                    + "|</span><a class=\"dbuser-list-delete\"  href=\"javascript:void(0);\" data-db-id="+array[i].dbId+">删除</a></div>"
						+"</td>"
						+"<td class='hidden-lg hidden-md hidden-sm'>"
						+ "<a class=\"dbuser-list-ip-privilege\" href=\"javascript:void(0);\" data-db-id="+array[i].dbId+"><span class='text-primary'><i class='fa fa-cogs'></i></span></a><span class=\"text-explode\">"
						+ "|</span><a class=\"dbuser-list-reset-password\"  href=\"javascript:void(0);\" data-db-id="+array[i].dbId+"><i class='fa fa-key'></i></a><span class=\"text-explode\">"
	                    
	                    + "|</span><a class=\"dbuser-list-delete\"  href=\"javascript:void(0);\" data-db-id="+array[i].dbId+"><i class='fa fa-trash-o'></i></a> </div>"
						+"</td>");	
				if(array[i].status == 0 ||array[i].status == 5||array[i].status == 13){
					var tr = $("<tr class=\"warning\"></tr>");
				}else if(array[i].status == 3 ||array[i].status == 4||array[i].status == 14){
					var tr = $("<tr class=\"default-danger\"></tr>");
					
				}else{
					var tr = $("<tr></tr>");
				}
				// tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td7);
				tr.append(td1).append(td2).append(td6).append(td7).append(td3).append(td8).append(td9);
				tr.appendTo(tby);
			}
			
			if (totalPages <= 1) {
				$("#pageControlBar").hide();
			} else {
				$("#pageControlBar").show();
				$("#totalPage_input").val(totalPages);
				$("#currentPage").html(currentPage);
				$("#totalRows").html(data.data.totalRecords);
				$("#totalPage").html(totalPages);
			}

			// new add 2015-07-01-----------------------------------
			/*获取行信息*/
            function getLineData(obj){
                var line = {
                    "username":$(obj).closest("tr").find("td:eq(1)").html(),
                    // "readWriterRate":$(obj).closest("tr").find("td:eq(2)").html(),
                    "maxConcurrency":$(obj).closest("tr").find("td:eq(2)").html(),
                    "descn":$(obj).closest("tr").find("td:eq(5) span").html()
                }
                return line;
            }
			var dbUserFuc={
				GetDbUserPrivilege: function(data){
			        var $tby = $("#ip-privilege-tby");
			        $tby.find("tr").remove();
			        $('#showDbuserIpPrivilege').modal({
			            backdrop:false,
			            show:true
			        });
			        var array = data.data;
			        for(var i= 0,len=array.length;i<len;i++){
			            if(array[i].used == 1){
			                var td1 =  $("<td>"
			                + array[i].addr
			                + "</td>");
			                var td2 =  $("<td>"
			                + TranslateDbUserType(array[i].type)
			                + "</td>");
			                var tr =$("<tr></tr>");
			                tr.append(td1).append(td2);
			                tr.appendTo($tby);
			            }
			        }
			    },
		        DeleteDbUser:function(name,id){
		            var username = name;
		            var url= "/dbUser/"+id+"/"+username;
		            $.ajax({
		            	url:url,
		            	type: 'delete',
		            	dataType: 'json',
		            	success:function(data){
		            		if(error(data)){return;}else{success('删除操作执行成功！')}
		            		queryByPage();
		            	}
		            });
		        }
			}
			/*初始化查看用户权限按钮*/
			$(".dbuser-list-ip-privilege").unbind('click').click(function () {
			    var lineData = getLineData(this);
			    var dbId=$(this).attr('data-db-id');
			    $.ajax({
			    	url: "/dbIp/"+dbId+"/"+lineData.username,
			    	type: 'get',
			    	success:function(data){
			    		if(error(data)){warn(data.msgs);return;}
			    		dbUserFuc.GetDbUserPrivilege(data);
			    	}
			    });
			    $("#showDbuserIpPrivilegeTitle").html(lineData.username);
			});
			/*初始化重置密码*/
            $(".dbuser-list-reset-password").unbind('click').click(function () {
                var lineData = getLineData(this);
                $("#reset-password-box-title").html("重置账户("+lineData.username+")密码");
                $("#reset-password-box").modal({
                    backdrop:false,
                    show:true
                });
                $("#reset-password-username").val(lineData.username);
                var dbId=$(this).attr('data-db-id');
                $("#reset-password-username").attr('data-db-id', dbId);
            });
            /*初始化删除按钮*/
            $(".dbuser-list-delete").unbind('click').click(function(event) {
            	event.preventDefault();    
                var lineData = getLineData(this);
                var title = "确认";
                var text = "您确定要删除("+lineData.username+")账户";
                var name = lineData.username;
                var dbId=$(this).attr('data-db-id');

                DialogBoxInit(title,text,dbUserFuc.DeleteDbUser,name,dbId);
            });
            // /*初始化修改用户权限按钮*/
            // $(".dbuser-list-modify-privilege").unbind('click').click(function(){
            //     $("#newAccountTab").addClass("hide");
            //     $("#accountList").addClass("hide");
            //     $("#modifyAccountTab").removeClass("hide");
            //     var dbId=$(this).attr('data-db-id');
            //     var lineData = getLineData(this);
            //     $.get("/dbIp/"+dbId+"/"+lineData.username, function(data) {
            //     	console.log('aaa')
            //     });
            //     $("#modifyFormDbUsername").html(lineData.username);
            //     $("#modifydbUserMaxConcurrency").val(lineData.maxConcurrency);
            //     $("#modifydbUserReadWriterRate").val(lineData.readWriterRate);
            //     $("#modifyFormDbDesc").html(lineData.descn);
            // })
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
//修改密码表单验证
function formValidate() {
	$('#reset-password-form').bootstrapValidator({
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'reset-password': {
                validators: {
                    notEmpty: {
                        message:'密码不能为空'
                    },regexp: {
                        regexp: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z@#$%^&*!~_-]{6,32}$/,
                        message: "由字母、数字或特殊字符如：@#$%^&*!~ 组成,要求6-32位，必须要包含数字，大小写字母"
                    }
                }
            },
            'reset-password-repeat': {
                validators: {
                    notEmpty: {
                        message:'密码不能为空'
                    },
                    identical: {
                        field: 'reset-password',
                        message: '两次输入密码不同'
                    }
                }
            }
        }
    }).on('success.form.bv', function(e) {
        e.preventDefault();
        var postdata = {
            "username":$("#reset-password-username").val(),
            "password":$("[name = 'reset-password']").val(),
            "dbId":$("#reset-password-username").attr('data-db-id')
        }
        var url = "/dbUser/security/"+$("#reset-password-username").val();
    	$.post(url,postdata, function(data) {
    		if(error(data)){warn(data.msgs);return;
    		}else{
    			success('密码重置成功！');$('#reset-password-form')[0].reset();$('#reset-password-box').modal('hide');
    			queryByPage();
    		}
    	});
    }).on('keyup', '[name="reset-password"]', function () {
        if($("[name = 'reset-password-repeat']").val() != ''){
            $('#reset-password-form').bootstrapValidator('revalidateField', 'reset-password-repeat');
            $("#reset-password-box").modal({show:false});
        }
    })
}
function InitDoubleFrame(id,data){
    var $d = $(id);		                    //双选框div
    $d.find("li").remove();
    $sl = $d.find(".select-list-left");		//左选框ul
    $sr = $d.find(".select-list-right");	//右选框ul
    for(var i= 0,len=data.length;i<len;i++){
        if(data[i].used == 0){
            AddToLeftFrame($sl,data[i]);
        }else{
            AddToRightFrame($sr,data[i]);
        }
    };
    /*初始化选中操作*/
    SelectToggle($d);
    /*双击移动操作*/
    DoubleClickToggle($d);

    /*添加选中内容*/
    $d.find(".btn_db_add").unbind('click').click(function(){
        var selected= [];
        $sl.find(".active").each(function (i,val) {
            selected.push({addr:$(val).html()});
        });
        $sl.find(".active").remove();
        for(var i= 0,len=selected.length;i<len;i++){
            AddToRightFrame($sr,selected[i]);
        }
        SelectToggle($d);
        DoubleClickToggle($d);
    });
    $d.find(".btn_db_remove").unbind('click').click(function(){
        var selected= [];
        $sr.find(".active").each(function (i,val) {
            selected.push({addr:$(val).find("p:first").html()});
        });
        $sr.find(".active").remove();
        for(var i= 0,len=selected.length;i<len;i++){
            AddToLeftFrame($sl,selected[i]);
        }
        SelectToggle($d);
        DoubleClickToggle($d);
    });
    /*全选按钮初始化*/
    $d.find(".select-all-rw").unbind('click').click(function(){
        var $sr = $(this).closest(".mcluster-select");
        if($(this).html() == "全部设管理"){
            $(this).html("全部设读写");
            $sr.find(".mgr").prev().click();
        }else if ($(this).html() == "全部设读写"){
            $(this).html("全部设只读");
            $sr.find(".rw").prev().click();
        }else{
            $(this).html("全部设管理");
            $sr.find(".ro").prev().click();
        }
    });

}
/*function searchAction(){
	$('#nav-search-input').bind('keypress',function(event){
        if(event.keyCode == "13")    
        {
        	queryByPage();
        }
    });
}*/

function page_init(){
	queryByPage();
	pageControl();
	formValidate();
}

