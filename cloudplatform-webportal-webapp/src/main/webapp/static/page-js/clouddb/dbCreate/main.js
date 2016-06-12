/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require){
    var common = require('../../common');
    var cn = new common();
    var $ = require("jquery");
    require("bootstrapValidator")($);
    cn.initNavbarMenu([{
                name : "Le云控制台",
                herf : "/dashboard"
            }]);
	/* 防止误操作退出创建页面 */
	cn.AddBeforeunloadListener();

    if(document.getElementById("monthPurchaseBotton").form == null){    //兼容IE form提交
        $("#monthPurchaseBotton").click(function(){
            $("#monthPurchaseForm").submit();
        })
    }

    /*按钮组件封装 --begin*/
    $(".bk-button-primary").click(function () {
        if(!$(this).hasClass("disabled")){
            $(this).parent().find(".bk-button-primary").removeClass("bk-button-current");
            $(this).addClass("bk-button-current");
            if($(this).parent().find(".hide").length > 0 ){
                var val = $(this).val();
                $(this).parent().find(".hide").val(val);
            }
        }
    })
    /*按钮组件封装 --end*/

    /*表单验证 --begin*/
    $("#monthPurchaseForm").bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            dbName: {
                validMessage: '请按提示输入',
                validators: {
                    notEmpty: {
                        message: '数据库名称不能为空!'
                    },
                    stringLength: {
                        max: 16,
                        message: '数据库名名过长!'
                    }, regexp: {
                        regexp: /^((?!^monitor$)([a-zA-Z_]+[a-zA-Z_0-9]*))$/,
                        message: "请输入字母数字或'_',数据库名不能以数字开头且数据库名称不能命名为monitor."
                    }/*,
                    remote: {
                        message: '数据库名不合法或数据库名已存在!',
                        url: '/db/validate'
                    }*/
                }
            }
        }
    }).on('success.form.bv', function(e) {
        e.preventDefault();
        var dbName = $("[name = 'dbName']").val();
        var hclusterId = $("[name = 'hclusterId']").val();
        var engineType = $("[name = 'engineType']").val();
        var linkType = $("[name = 'linkType']").val();
        var isCreateAdmin = $("[name = 'isCreateAdmin']").val();
        var storageSize = $("[name = 'storageSize']").val();
        var memorySize = $("[name = 'memorySize']").val();
        var formData = {"dbName":dbName,"linkType":linkType,"engineType":engineType,"hclusterId":hclusterId,"isCreateAdmin":isCreateAdmin,"storageSize":storageSize,"memorySize":memorySize};
        CreateDb(formData);
    });
    /*表单验证 --end*/

    /*加载数据*/
    var dataHandler = require('./dataHandler');
    var createDbHandler = new dataHandler();
    GetHcluster();
    function GetHcluster(){
        var url="/hcluster/rds";
        cn.GetData(url,createDbHandler.GetHclusterHandler);
    }
    /*创建数据库*/
    function CreateDb(data){
    	cn.RemoveBeforeunloadListener();
        var url="/db";
        cn.PostData(url,data, function () {
            location.href = "/list/db";
        });
    }

    // 手机端 购买页面 适配
    $('.step-next').unbind('click').click(function(event) {
        $(this).parent().addClass('hide').next().removeClass('config-buy');
        $('body').scrollTop(0)
    });
    $('.step-forward').unbind('click').click(function(event) {
        $('.step-next').parent().removeClass('hide');
        $(this).parent().addClass('config-buy');
    });
    $('.connect-help a').unbind('click').click(function(event) {
        var _target=$(this).parent().next();
        if(_target.hasClass('hide')){
            _target.removeClass('hide')
        }else{
            _target.addClass('hide')
        }
        
    });
});
