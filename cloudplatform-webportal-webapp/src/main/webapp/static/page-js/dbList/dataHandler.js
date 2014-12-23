/**
 * Created by yaokuo on 2014/12/14.
 */
define(function(require,exports,module){
    var $ = require('jquery');
    /*
	 * 引入相关js，使用分页组件
	 */
	require('bootstrap');
	require('paginator');
	
    var common = require('../common');
    var cn = new common();
    
    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
        DbListHandler : function(data){
        	$(".data-tr").remove();
        	
            var $tby = $('#tby');
            var array = data.data.data;

            for(var i= 0, len= array.length;i<len;i++){
                var td1 = $("<td width=\"10\">"
                        + "<input type=\"checkbox\">"
                        + "</td>");
                var td2 = $("<td class=\"padding-left-32\">"
                        + "<div>"
                        + "<div>"
                        + "<a href=\"/detail/baseInfo/"+array[i].id+"\">" + array[i].dbName + "</a><br>"
                        + "<span text-length=\"26\">" + array[i].dbName + "</span>"
                        + "<a class=\"hide btn btn-default btn-xs glyphicon glyphicon-pencil\" href=\"#\"></a>"
                        +"</div>"
                        +"</div>"
                        +"</td>");
                var td3 = $("<td>"
                        + cn.TranslateStatus(array[i].status)
                        +"</td>");
                var td4 = $("<td>"
                        + "<span>专享</span>"
                        + "</td>");
                var td5 = $("<td><span>MySQL5.5</span></td>");
                var td6 = $("<td><span >单可用区</span></td>");
                var td7 = $("<td><span>"+array[i].hcluster.hclusterNameAlias+"</span></td>");
                var td8 = $("<td><span><span>包年</span><span class=\"text-success\">36500</span><span> 天后到期</span></span></td>");
                var td9 = $("<td class=\"text-right\"> <div><a href=\"/detail/baseInfo/"+array[i].id+"\">管理</a><span class=\"text-explode\">|</span><a href=\"#\" target=\"_self\">续费</a><span class=\"text-explode\">|</span><a href=\"#\" target=\"_self\">升级</a> </div></td>");
                var tr = $("<tr class='data-tr'></tr>");
                tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td7).append(td8).append(td9);
                tr.appendTo($tby);
            }
            /*
             * 设置分页数据
             */
            $("#totalRecords").html(data.data.totalRecords);
            $("#recordsPerPage").html(data.data.recordsPerPage);
            
            $('#paginator').bootstrapPaginator({
                currentPage: data.data.currentPage,
                totalPages:data.data.totalPages
            });
        }
    }
});