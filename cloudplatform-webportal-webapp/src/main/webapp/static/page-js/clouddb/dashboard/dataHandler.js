/**
 * Created by yaokuo on 2014/12/14.
 */
define(function(require,exports,module){
    var $ = require('jquery');

    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
    	/*
    	 * 相关资源量赋值
    	 */
        resCountHandler : function(data){
        	console.log(data);
            if(data.data.db > 0){
                $("#rds-opened").removeClass("hide");
                $("#rds-not-opened").addClass("hide");
                $("#dbCount").html(data.data.db);
            }else{
                $("#rds-opened").addClass("hide");
                $("#rds-not-opened").removeClass("hide");
            }
            if(data.data.slb > 0){
                $("#slb-opened").removeClass("hide");
                $("#slb-not-opened").addClass("hide");
                $("#slbCount").html(data.data.slb);
            }else{
                $("#slb-opened").addClass("hide");
                $("#slb-not-opened").removeClass("hide");
            }
            if(data.data.gce > 0){
            	$("#gce-opened").removeClass("hide");
            	$("#gce-not-opened").addClass("hide");
            	$("#gceCount").html(data.data.gce);
            }else{
            	$("#gce-opened").addClass("hide");
            	$("#gce-not-opened").removeClass("hide");
            }
            if(data.data.cache > 0){
                $("#ocs-opened").removeClass("hide");
                $("#ocs-not-opened").addClass("hide");
                $("#ocsCount").html(data.data.cache);
            }else{
                $("#ocs-opened").addClass("hide");
                $("#ocs-not-opened").removeClass("hide");
            }
            if(data.data.oss > 0){
                $("#oss-opened").removeClass("hide");
                $("#oss-not-opened").addClass("hide");
                $("#ossCount").html(data.data.oss);
                $("#store-and-CDN").addClass("hide");
            }else{
                $("#oss-opened").addClass("hide");
                $("#oss-not-opened").removeClass("hide");
            }
            if(data.data.vm > 0){
                $("#vm-opened").removeClass("hide");
                $("#vm-not-opened").addClass("hide");
                $("#vmCount").html(data.data.vm);
            }else{
                $("#vm-opened").addClass("hide");
                $("#vm-not-opened").removeClass("hide");
            }
            if(data.data.es > 0){
                $("#es-opened").removeClass("hide");
                $("#es-not-opened").addClass("hide");
                $("#esCount").html(data.data.es);
            }else{
                $("#es-opened").addClass("hide");
                $("#es-not-opened").removeClass("hide");
            }
        },
        InitVmModule:function(data){
        	if(!data || !data.data){
        		$("#vm-opened").addClass("hide");
        		$("#vm-not-opened").removeClass("hide");
        		$("#vm-not-opened .product-opts").html('<span class="home-orange">敬请期待...</span>');
        	}
        }
    }
});