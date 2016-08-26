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
	
    var common = require('../../../common');
    var cn = new common();
   
    
    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
        GceListHandler : function(data){
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
                            + "<input type=\"checkbox\" name=\"gcecluster_id\" value= \""+array[i].id+"\">"
                            + "</td>");
                    var gceName = "<a href=\"/detail/ecgce/"+array[i].id+"\">" + array[i].gceName + "</a>"
                    var td2 = $("<td class=\"padding-left-32\">"
                            + gceName
                            +"</td>");
                    var td10 = $("<td width=\"250\">"
                    		+ (array[i].descn?array[i].descn:'')
                    		+"</td>");
                    var td4 = $("<td class='hidden-xs'><span>"+array[i].type+"</span></td>");
                    var td7="<td class='hidden-xs'></td>";
                    if(array[i].hcluster != undefined && array[i].hcluster != null){
                        var td7 = $("<td class='hidden-xs'>"
                        + "<span>"+array[i].hcluster.hclusterNameAlias+"</span></td>"
                        + "</td>");
                    }
                    
                    var td8 = $("<td class='hidden-xs'><span><span>包年  </span><span class=\"text-success\">"+cn.RemainAvailableTime(array[i].createTime)+"</span><span>天后到期</span></span></td>");
                    var td9 = $("<td><a href=\"javascript:void(0)\"><span class=\"text-explode gce-delete\">删除</span></a></td>");
            
                     var tr = $("<tr class='data-tr'></tr>");
                    tr.append(td1).append(td2).append(td10).append(td4).append(td7).append(td8).append(td9);
                    tr.appendTo($tby);
	            }

	            	 $(".gce-delete").click(function () {    
		            	var id =$(this).closest("tr").find("input").val();
		            	var name = $(this).closest("tr").find("td:eq(1)").html();
		                var title = "确认";
		                var text = "您确定要删除("+name+")此应用";
		                cn.DialogBoxInit(title,text,gceDelete,id);
		            })
	       
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
    }
    function getAccpetAddr(data){
        if(data == null || data.length == 0){
            return "-";
        }
        var ret="";
        for(var i= 0,len=data.length;i<len;i++){
        	var port = "8001";
        	if(data[i].type == "jetty")
        		port = "8080";
            ret = ret
            +"<span class=\"text-success\">"+data[i].ipAddr+"</span>"
            +"<span class=\"text-success\">:</span>"
            +"<span class=\"text-success\">"+port+"</span><br>"
        }
        return ret;
    }
      function gceDelete(id){
    	var url = "/ecgce/"+id;
            cn.DeleteData(url, function () {
                location.href="/list/ecgce";
            });
    }
});