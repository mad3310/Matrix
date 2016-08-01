/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require){
	var $ = require('jquery');
	require('bootstrap');
	require('paginator')($);
	
    var common = require('../../../common');
    var cn = new common();

/*初始化工具提示*/
    cn.Tooltip('#serviceName');

/*手风琴收放效果箭头变化*/
    cn.Collapse();
    var dataHandler = require('./dataHandler');
    var gceInfoHandler = new dataHandler();   

    /*初始化上传镜像*/
    $("#uploadImage").click(function () {
        $("#reset-password-box").modal({
            backdrop:false,
            show:true
        });
    });
    //刷新按钮
    $("#refresh").click(function () {
    	asyncData(1);
    });
    
    //初始化分页组件
	$('#paginator').bootstrapPaginator({
		size:"small",
    	alignment:'right',
		bootstrapMajorVersion:3,
		numberOfPages: 1,
		onPageClicked: function(e,originalEvent,type,page){
			cn.currentPage = page;
        	asyncData(page);
        }
	});
    
    
    
	//加载列表数据
	function asyncData(page) {
		if(!page) page = cn.currentPage;
		url = "/ecgce/packages/"+$("#gceId").val()+"?currentPage=" + page +"&&recordsPerPage=" + 10;
		cn.GetData(url,gceInfoHandler.GceImageListHandler);
	}
     
    cn.GetData("/ecgce/"+$("#gceId").val(),gceInfoHandler.GceAjaxFormHandler);
    
    asyncData(1);
    
    setInterval(function(){
    	asyncData(cn.currentPage);
    },5000);
});
