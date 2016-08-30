/**
 * Created by yaokuo on 2014/12/14.
 */
define(function(require,exports,module){
    var $ = require('jquery');
    var common = require('../../common');
    var cn = new common();

    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
    		GceInfoHandler : function(data){
                var gceInfo = data.data;
    			$("#gce_info_gce_name").html(gceInfo.gceName);
    			$("#gce_descn").html(gceInfo.descn);
    			$("#gce_type").html(cn.gceTypeTranslation(gceInfo.type));
                $("#gce_server_addr").html(getAccpetAddr(gceInfo.gceContainers,gceInfo.type));
    			$("#gce_create_time").html(cn.TransDate('Y-m-d H:i:s',gceInfo.createTime));
    			$("#gce_server_num").html(gceInfo.instanceNum);
                if(gceInfo.hcluster != undefined && gceInfo.hcluster != null) {
                    $("#gce_info_available_region").html(gceInfo.hcluster.hclusterNameAlias);
                }
            },
            GceExtHandler : function(data){
                var gceExt = data.data;
                if(gceExt) {
                	if(gceExt.rdsId!=null) {
                		$("#gce_rds_name").html("<a href='#' onclick='javascript:parent.location.href=\"/detail/db/"+gceExt.rdsId+"\"'>"+gceExt.db.dbName+"</a>");
                	}
                	if(gceExt.ocsId!=null) {
                		$("#gce_ocs_name").html("<a href='#' onclick='javascript:parent.location.href=\"/detail/cache/"+gceExt.ocsId+"\"'>"+gceExt.cbase.bucketName+"</a>");
                	}
                }
            }
    }
	 function getAccpetAddr(data,type){
	        if(data == null || data.length == 0){
	            return "-";
	        }
	        var ret="";
	        for(var i= 0,len=data.length;i<len;i++){
	        	var port = "8001";
	        	if(type == "JETTY")
	        		port = "8080";
	            ret = ret
	            +"<span class=\"text-success\">"+data[i].ipAddr+"</span>"
	            +"<span class=\"text-success\">:</span>"
	            +"<span class=\"text-success\">"+port+"</span>&nbsp;"
	        }
	        return ret;
	    }
    	 
});