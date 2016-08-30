/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require){
    var common = require('../../../common');
    var cn = new common();
    var $ = require("jquery");
    require("bootstrapValidator")($);

	/* 防止误操作退出创建页面 */
	cn.AddBeforeunloadListener();

    if(document.getElementById("monthPurchaseBotton").form == null){    //兼容IE form提交
        $("#monthPurchaseBotton").click(function(){
            $("#monthPurchaseForm").submit();
        })
    }

    $("[name = buyNum]").closest('.bk-number').click(function(e){
    	e = e? e:window.event;
		var _target = e.target || e.srcElement;
		var aTarget = $(_target).parent()[0];
		var aTargetClassList = aTarget.classList;
		var oldVal = $("[name = buyNum]").val();
		if(aTargetClassList.contains("bk-number-up")){
			$("[name = buyNum]").val(parseInt(oldVal) < 2?  parseInt(oldVal)+1:2);
		}else if(aTargetClassList.contains("bk-number-down")){
		 	$("[name = buyNum]").val(parseInt(oldVal) > 1?  parseInt(oldVal)-1:1);
		}
    })
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
    
    $('.divselect').find("ul li").unbind("click").click(function(){
        var txt = $(this).find('a').text();
        $("#bill_buyNum").text(txt);
    }); 
    
    /*表单验证 --begin*/
    $("#monthPurchaseForm").bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
        	gceName:{
				validMessage: '请按提示输入',
				validators: {
					notEmpty: {
						message: '应用名不能为空!'
					}, regexp: {
						regexp: /^[a-zA-Z][a-zA-Z_0-9]{1,15}$/,
						message: "2-16个字符,支持英文、数字和“_”格式,请以英文字母开头"
					}
				}
			},descn:{
				validMessage: '请按提示输入',
				validators: {
					stringLength: {
						max: 50,
						message: '应用描述在50字以内!'
					},             
                    callback: {
                        callback: function(value, validator, $field) {
                            if (/[<>=]/.test(value)) {
                                return {
                                    valid: false,
                                    message: '不能包含左右尖括号以及等号'
                                }
                            }
                            else{
                                return {
                                    valid: true
                                }
                            }

                        }
                    }
				}
			}
        }
    }).on('success.form.bv', function(e) {
    	e.preventDefault();
		var createGceData = {
			gceName : $("[name = gceName]").val(),
			descn : $("[name = descn]").val(),
			hclusterId : $("[name = 'hclusterId']").val(),
			ocsId : $("[name = 'ocsId']").val(),
			rdsId : $("[name = 'rdsId']").val(),
			memorySize : $("[name = 'memorySize']").val(),
            type:$("[name = type]").val(),
            instanceNum:$("[name = buyNum]").val()
		}
		
		cn.RemoveBeforeunloadListener();
		var url = "/ecgce";
		cn.PostData(url, createGceData, function () {
			location.href = "/list/ecgce";
		});
    });
    /*表单验证 --end*/

    /*加载数据*/
    var dataHandler = require('./dataHandler');
    var createDbHandler = new dataHandler();
    initSelect();
    function initSelect(){
    	var url="/hcluster/gce";
    	cn.GetData(url,createDbHandler.GetHclusterHandler);
    	var url="/db";
    	cn.GetData(url,createDbHandler.getRdsHandler);
    	var url="/cbase";
    	cn.GetData(url,createDbHandler.getOcsHandler);
    }
});
