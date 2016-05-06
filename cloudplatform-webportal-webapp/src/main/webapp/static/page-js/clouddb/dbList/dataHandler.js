/**
 * Created by yaokuo on 2014/12/14.
 * dblist page 
 */
define(function(require,exports,module){
    var $ = require('jquery');
    /*
	 * 引入相关js，使用分页组件
	 */
	require('bootstrap');
	require('paginator')($);
	
    var common = require('../../common');
    var cn = new common();
   
    
    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
        DbListHandler : function(data){
        	$(".data-tr").remove();
        	
            var $tby = $('#tby');
            var array = data.data.data;
            if(array.length == 0){
            	cn.emptyBlock($tby);
            	if($("#paginatorBlock").length > 0){
            		$("#paginatorBlock").hide();
            	}
            }else{
            	 if($("#noData").length > 0){
            		 $("#noData").remove();
            	 }
            	 if($("#paginatorBlock").length > 0){
            		 $("#paginatorBlock").show();
            	 }
                for(var i= 0, len= array.length;i<len;i++){
                    var td1 = $("<td width=\"10\">"
                            + "<input type=\"checkbox\" name=\"mcluster_id\" value= \""+array[i].id+"\">"
                            + "</td>");
                    var dbName = "";
                    if(cn.Displayable(array[i].status)){
                        dbName = "<a href=\"/detail/db/"+array[i].id+"\">" + array[i].dbName + "</a>"
                    }else{
                        dbName = "<span class=\"text-explode font-disabled\">"+array[i].dbName+"</span>"
                    }
                    var td2 = $("<td class=\"padding-left-32\">"
                            + dbName
                            +"</td>");
                    
                    var td3 = '';                
                    if(array[i].status == 2){
                    	var td3 = $("<td class='hidden-xs'>"
                    			+ "<div class=\"progress\" id= \"prg"+ array[i].id + "\">"
                    			+ "<div class=\"progress-bar\" role=\"progressbar\" aria-valuenow=\"60\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: 0;\">"
                    			+ "</div>"
                    			+ "</div>"
                    			+ "<span class=\"progress-info\"></span>"
                                + "<input class=\"hide\" type=\"text\" name=\"progress_db_id\" id= \""+ array[i].id + "\" value= \""+ array[i].id + "\" >"
                                + "</td><td class='hidden-sm hidden-md hidden-lg'>创建中...</td>");
                    }else{
                    	var td3 = $("<td>"
                                + cn.TranslateStatus(array[i].status)
                                +"</td>");
                    }
                    var td4 = $("<td class='hidden-xs'>"
                            + "<span>专享</span>"
                            + "</td>");
                    var td5 = $("<td class='hidden-xs'><span>MySQL5.5</span></td>");
                    var td6 = $("<td class='hidden-xs'><span >单可用区</span></td>");
                    var td7 = $("<td class='hidden-xs'><span>"+array[i].hcluster.hclusterNameAlias+"</span></td>");
                    if(array[i].mcluster == null){
                    	var td8 = $("<td class='hidden-xs'></td>");
                    }else{
                    	var td8 = $("<td class='hidden-xs'><span>"+cn.FilterNull(array[i].mcluster.mclusterName)+"</span></td>")
                    }
                    
                    var td9 = $("<td class='hidden-xs'><span><span>包年  </span><span class=\"text-success\">"+cn.RemainAvailableTime(array[i].createTime)+"</span><span>天后到期</span></span></td>");
                    if(cn.Displayable(array[i].status)){
                    	var td10 = $("<td class=\"text-right hidden-xs\"><a href=\"/detail/db/"+array[i].id+"\">管理</a>&nbsp;<span class=\"text-explode font-disabled\">|续费|升级</span></td>"
                        +"<td class=\"hidden-sm hidden-md hidden-lg\">"
                        +"<div class='pull-right'>"                 
                        +"<a href=\"/detail/db/"+array[i].id+"\"><span class='text-success'><i class='fa fa-cogs'></i></span></a>&nbsp;&nbsp;"
                        +"<a><span class=\"text-explode font-disabled\"><i class='fa fa-shopping-cart'></i></span></a>&nbsp;&nbsp;"
                        +"<a><span class='text-explode font-disabled'><i class='fa fa-upload'></i></span></a></div></td>");
                      // +"<div class='pull-right m-tgbtn dropdown'><button type='button' class='dropdown-toggle' data-toggle='dropdown'><i class='fa fa-navicon'></i></button>"                 
                      //   +"<ul class='dropdown-menu m-dropdown-caret'><li><a href=\"/detail/db/"+array[i].id+"\"><span class='text-success'><i class='fa fa-cogs'></i></span></a></li>"
                      //   +"<li><a><span class=\"text-explode font-disabled\"><i class='fa fa-shopping-cart'></i></span></a></li>"
                      //   +"<li><a><span class='text-explode font-disabled'><i class='fa fa-upload'></i></span></a></li></ul></div></td>");
                    }else{
                    	var td10 = $("<td class=\"text-right hidden-xs\"><span class=\"text-explode font-disabled\">管理|续费|升级</span></td>"
                        +"<td class=\"hidden-sm hidden-md hidden-lg\">"
                        +"<div class='pull-right'>"                 
                        +"<a><span class='text-explode font-disabled'><i class='fa fa-cogs'></i></span></a>&nbsp;&nbsp;"
                        +"<a><span class=\"text-explode font-disabled\"><i class='fa fa-shopping-cart'></i></span></a>&nbsp;&nbsp;"
                        +"<a><span class='text-explode font-disabled'><i class='fa fa-upload'></i></span></a></div></td>");
                        // +"<div class='m-tgbtn dropdown pull-right'><button type='button' class='dropdown-toggle' data-toggle='dropdown'><i class='fa fa-navicon'></i></button>"                 
                        // +"<ul class='dropdown-menu m-dropdown-caret text-center'><li><a><span class='text-explode font-disabled'><i class='fa fa-cogs'></i></span></a></li>"
                        // +"<li><a><span class=\"text-explode font-disabled\"><i class='fa fa-shopping-cart'></i></span></a></li>"
                        // +"<li><a><span class='text-explode font-disabled'><i class='fa fa-upload'></i></span></a></li></ul></div></td>");
                    }
                    var tr = $("<tr class='data-tr'></tr>");
                    
                    tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td7).append(td8).append(td9).append(td10);
                    tr.appendTo($tby);
                 }
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
        },
        /*进度条进度控制*/
	    progress : function(dbId,data,onProgressEnd){
	    	var data = data.data;	    	
   	        var unitLen = 100 / 10;
   	        var $obj = $("#prg" + dbId);
   	        var $prg = $obj.find(".progress-bar");
   	       	var pWidth = unitLen * data;
           	if( data == 1){
           		$prg.css({"width": pWidth + '%'});
           		$prg.html( pWidth + "%");
           		$obj.next().html("正在准备安装环境...");
           	}else if (data > 1 && data <= 3){
           		$prg.css({"width": pWidth + '%'});
           		$prg.html( pWidth + "%");
           		$obj.next().html("正在检查安装环境...");
           	}else if (data > 3 && data <= 6){
           		$prg.css({"width": pWidth + '%'});
           		$prg.html( pWidth + "%");
           		$obj.next().html("正在初始化数据库服务...");
           	}else if (data > 6 && data < 10){
           		$prg.css({"width": pWidth + '%'});
           		$prg.html( pWidth + "%");
           		$obj.next().html("正在创建数据库...");
           	}else if (data == 0 || data >= 10){
           		$prg.css({"width": "100%"});
           		$prg.html("100%");
           		$obj.next().html("创建完成");  
           		onProgressEnd();
           	}else if(data == -1){
           		$prg.css({"width": "100%"});
           		$prg.html("100%");
           		$obj.next().html("创建失败");
           		onProgressEnd();
           	}else{
           		onProgressEnd();
           	}
	   	}
    }
});