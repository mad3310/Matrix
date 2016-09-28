/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require) {
	var $ = require('jquery');
	require('bootstrap');
	require('paginator')($);
	require("bootstrapValidator")($);
	var common = require('../../../common');
	var cn = new common();
	window.lock = false;
	/*初始化工具提示*/
	cn.Tooltip('#serviceName');

	/*手风琴收放效果箭头变化*/
	cn.Collapse();
	var dataHandler = require('./dataHandler');
	var gceInfoHandler = new dataHandler();

	
	/*初始化上传镜像*/
	$("#uploadImage").click(function() {
		$("#upload-image-box").modal({
			backdrop : false,
			show : true
		});
		$('#uploadImageForm')[0].reset();
	});
	//刷新按钮
	$("#refresh").click(function() {
		asyncData(1);
	});

	//初始化分页组件
	$('#paginator').bootstrapPaginator({
		size : "small",
		alignment : 'right',
		bootstrapMajorVersion : 3,
		numberOfPages : 3,
		onPageClicked : function(e, originalEvent, type, page) {
			cn.currentPage = page;
			asyncData(page);
		}
	});
	
    formValidatorInit();
    
	/*修改描述*/
	function formValidatorInit(){
		$("#uploadImageForm").bootstrapValidator({
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				'version' : {
					validMessage : '请按提示输入',
					validators : {
						notEmpty : {
							message : '版本号不能为空!'
						},
						regexp : {
							regexp : /^\d+\.\d+\.\d+\.\d+$/,
							message : "版本号规范必须为x.x.x.x，例如1.1.1.12"
						}
					}
				},
				'file' : {
					validMessage : '请按提示输入',
					validators : {
						notEmpty : {
							message : '镜像文件不能为空!'
						},
	                    file: {
	                        extension: 'zip',
	                        type: 'application/zip',
	                        maxSize: 1048 * 1024*500,
	                        message: '文件必须为小于500M的zip文件'
	                    }
					}
				},
				'descn' : {
					validMessage : '请按提示输入',
					validators : {
						stringLength: {
							max:100,
	                        message: '备注描述不能超过100位'
	                    }
					}
				}
			}
		});	
	}
	
	//删除
	$('#tby').delegate(".delete","click",function(){
		var gceId = $("#gceId").val();
		var packageId = $(this).parents("tr").attr("pakageId");
			
		cn.DeleteData("/ecgce/packages/"+packageId+"?gceId="+gceId, function(data){
			if(data.result!=1){
				cn.alertoolDanger("GCE删除失败",50000);
			}else{
				cn.alertoolSuccess("GCE删除中",50000);
			}
			asyncData(cn.currentPage);
		});
	});
	//部署
	$('#tby').delegate(".deploy","click",function(){
		var gceId = $("#gceId").val();
		var packageId = $(this).parents("tr").attr("pakageId");
		$(this).removeClass("deploy");
		window.lock = true;
		cn.GetData("/ecgce/packages/deploy/"+packageId+"?gceId="+gceId, function(data){
			window.lock = false;
			if(data.result!=1){
				cn.alertoolDanger("GCE部署失败",50000);
			}else{
				cn.alertoolSuccess("GCE开始部署，请等待",50000);
			}
			asyncData(cn.currentPage);
		});
	});
	
	//加载列表数据
	function asyncData(page) {
		if (!page)
			page = cn.currentPage;
		url = "/ecgce/packages/" + $("#gceId").val() + "?currentPage=" + page
				+ "&&recordsPerPage=" + 10;
		cn.GetData(url, gceInfoHandler.GceImageListHandler);
	}

	gceInfoHandler.GceAjaxFormHandler(asyncData);
	
	asyncData(1);

	setInterval(function() {
		if($("#tby td[status=2]").length>0 || $("#tby td[status=10]").length>0){
			asyncData(cn.currentPage);
		}
	}, 5000);
});
