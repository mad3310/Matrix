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
            GceAjaxFormHandler : function(data){    	
                $("#uploadImageForm").ajaxForm({
                	success: function (data) {
                		cn.alertoolSuccess("镜像上传成功",30000);
                    },
                    beforeSubmit:function(){
                    	cn.alertoolSuccess("镜像上传中，请勿刷新页面",3000);
                    },
                    error:function(){
                    	cn.alertoolDanger("镜像上传失败",3000);
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
                    var version = $("<td width=\"30%\">" + dataArray[i].version + "</td>");
                    var status = $("<td width=\"30%\">" + cn.TranslateStatus(dataArray[i].status) + "</td>");
                    var createTime = $("<td width=\"40%\">" + cn.RemainAvailableTime(dataArray[i].createTime) + "</td>");
                    var tr = $("<tr class='data-tr'></tr>");
                    tr.append(version).append(status).append(createTime);
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