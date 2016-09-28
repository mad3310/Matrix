/**
 * Created by yaokuo on 2014/12/14.
 */
define(function(require,exports,module){
    var $ = require('jquery');
    require("jquery.form")($);
	require('bootstrap');
	require('paginator')($);
	
    var common = require('../../../common');
    var cn = new common();

    var DataHandler = function(){
    };

    module.exports = DataHandler;
    
    DataHandler.prototype = {
            GceAjaxFormHandler : function(callBackFunc){    	
                $("#uploadImageForm").ajaxForm({
                	success: function (data) {
                		console.log(data);
                		if(data.result!=1){
                			cn.alertoolDanger(data.msgs[0],30000);
                		}else{
                    		cn.alertoolSuccess("镜像上传成功",30000);
                		}
                		callBackFunc(cn.currentPage);  
                		$('#uploadImageForm').data('bootstrapValidator').enableFieldValidators('file', true);
                        $("#cancelModal").click();
                    },
                    beforeSubmit:function(){
                    	cn.alertoolSuccess("镜像上传中，请勿刷新页面",3000);
                    },
                    error:function(data){
                    	cn.alertoolDanger("镜像上传失败",30000);
                    }
                }); 
            },
            GceImageListHandler : function(data){
            	if(this.lock)return;
            	var $tby = $('#tby').empty();            	
            	var dataArray = data.data.data;
          	
                if(dataArray.length == 0){
                	if($("#paginatorBlock").length > 0){
                		$("#paginatorBlock").hide();
                	}
                	return ;
                }
            	       
	           	if($("#noData").length > 0){
	        		$("#noData").remove();
	        	}
	        	if($("#paginatorBlock").length > 0){
	        		$("#paginatorBlock").show();
	        	}
	        	 
            	for(var i=0;i<dataArray.length;++i){
                    var version = $("<td width=\"10%\">" + dataArray[i].version + "</td>");
                    var status = $("<td width=\"10%\" status='"+dataArray[i].status+"'>" + cn.TranslateGceType(dataArray[i].status) + "</td>");
                    var createTime = $("<td width=\"20%\">" + cn.TransDate('Y-m-d H:i:s',dataArray[i].createTime) + "</td>");
                    
                    var descnStr = dataArray[i].descn?dataArray[i].descn:"";
                    if(descnStr.length>20){
                    	descnStr = descnStr.substr(0,20)+"...";
                    }
                    var descn = $("<td title='"+dataArray[i].descn+"'width=\"20%\">" + descnStr + "</td>");
                    
                    var ipListStr = "";
                    if(dataArray[i].containers){
                    	dataArray[i].containers.forEach(function(item){
                    		if(item.ipAddr){
                        		var url = item.ipAddr+":"+item.bindHostPort;
                        		ipListStr += "<a class='center-block' target=_'blank' href='http://"+url+"'>"+url+"</a>";
                    		}
                    	});
                    }
                    
                    var ipList = $("<td width=\"20%\">"+ipListStr+"</td>");
                   
                    var deploy = "";
                    if(dataArray[i].status == 0){
                    	deploy =  $("<td width=\"20%\"><a class='deploy' href='javascript:void(0);return false;'>部署</a>&nbsp<a class='delete' href='javascript:void(0);return false;'>删除</a></td>");
                    }else{
                    	deploy =  $("<td width=\"20%\"><span style='color:grey'>部署</span>&nbsp<a class='delete' href='javascript:void(0);return false;'>删除</a></td>");
                    }
                    var tr = $("<tr class='data-tr' pakageId='"+dataArray[i].id+"'></tr>");
                    tr.append(version).append(status).append(createTime).append(descn).append(ipList).append(deploy)
                    tr.appendTo($tby);
            	}
                        	
            	
	            /*
	             * 设置分页数据
	             */
	            $("#totalRecords").html(data.data.totalRecords);
	            $("#recordsPerPage").html(data.data.recordsPerPage);
	            
	            if(data.data.totalPages < 1){
	        		data.data.totalPages = 1;
	        	};
	        	
	            $('#paginator').bootstrapPaginator({
	                currentPage: data.data.currentPage,
	                totalPages:data.data.totalPages
	            });
            }
    }
    	 
});