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
                		cn.alertoolSuccess("镜像上传成功",30000);
                		callBackFunc(cn.currentPage);
                		$('#upload-image-box').modal('hide');
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
                    var version = $("<td width=\"25%\">" + dataArray[i].version + "</td>");
                    var status = $("<td width=\"25%\">" + cn.TranslateGceType(dataArray[i].status) + "</td>");
                    var createTime = $("<td width=\"25%\">" + cn.TransDate('Y-m-d H:i:s',dataArray[i].createTime) + "</td>");
                    var deploy = "";
                    if(dataArray[i].status == 0){
                    	deploy =  $("<td width=\"25%\"><a class='deploy' href='javascript:void(0);return false;'>部署</a></td>");
                    }else{
                    	deploy =  $("<td width=\"25%\"><span style='color:grey'>部署</span></td>");
                    }
                    var tr = $("<tr class='data-tr' pakageId='"+dataArray[i].id+"'></tr>");
                    tr.append(version).append(status).append(createTime).append(deploy)
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