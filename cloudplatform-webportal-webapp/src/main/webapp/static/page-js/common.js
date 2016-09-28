/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require,exports,module){
    var  $ = require('jquery');
    var tempArry;
    var monitorFrameHeight = {
                interval:null,
                oldFrameHeight:0    
            };
    var  testIsPCVal;
    var mobileEndInited = false;
    var iframeMobileEndInited= false;
    var scrollInited = false;
    
    require('bootstrap')($);
    var Common = function (){
        this.totalAvailableTime = 365;
        this.dbListRefreshTime = 60000; //单位ms
        this.maxConcurrency = 2000; //数据库最大并发量
        /*适配移动端*/
        testIsPCVal = IsPC();
        if($(".nav-sidebar-div").length > 0 ){
            sidebarMenuInit(testIsPCVal);//初始化siderbarmenu
            $('.amz-sidebar-toggle').removeClass('hide')
            sidebarR();
        }
      	/*iframe页面添加滑出菜单事件*/
        if(top.location != location &&!iframeMobileEndInited){
        	sidebarMenuInit(testIsPCVal);
        }
        
        gotop();
        this.isPC = testIsPCVal;
        /*iframe自适应高度start*/
        if(document.getElementsByTagName("iframe").length>0 && monitorFrameHeight.interval ==null){
            var iframe = document.getElementById("frame-content");
            var iframeDiv = document.getElementById("frame-content-div");
            function adaptiveFunction(){
                try{
					var bodyHeight = iframe.contentWindow.document.getElementsByTagName("html")[0].offsetHeight;
					if(bodyHeight<520){
						bodyHeight = 520;
					}
				}catch(ex){
					var bodyHeight = 800;
				}
                if(monitorFrameHeight.oldFrameHeight != bodyHeight){
                    iframe.height = bodyHeight;
                    iframeDiv.style.height = bodyHeight+"px";
                    monitorFrameHeight.oldFrameHeight = bodyHeight;
                }
            }
            adaptiveFunction();
            monitorFrameHeight.interval = window.setInterval(adaptiveFunction,300);
        }
        /*iframe自适应高度end*/
        if(testIsPCVal){TopBtnInit()};//初始化顶部菜单按钮
    };
    module.exports = Common;
    
 /*common原型属性方法end*/
    Common.prototype = {            
        currentPage:1,
        recordsPerPage:10,
        Tooltip : function (id){
            if(!id) {
                id = "[data-toggle='tooltip']";
            }
            $(id).hover(function() {
                $(this).tooltip('show');
            }, function() {
                $(this).tooltip('hide');
            });
        },
        Popover : function (id){
            if(!id) {
                id = "[data-toggle='popover']";
            }
            $(id).hover(function() {
                $(this).popover('show');
            }, function() {
                $(this).popover('hide');
            });
        },
        Collapse : function(){
            $(".collapse").on('show.bs.collapse', function () {
                    $(this).prev().find(".glyphicon-chevron-up").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
            }).on('hide.bs.collapse', function () {
                    $(this).prev().find(".glyphicon-chevron-down").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
            })
        },
        TranslateDbUserType: function(type){
            if(type == 1){
                return "管理";
            }else if(type == 2){
                return "只读"
            }else{
                return "读写"
            }
        },
        TranslateGceType: function(type){
            if(type == 0){
                return "未部署";
            }else{
                return this.TranslateStatus(type);
            }
        },
        TranslateStatus : function (status){
            if (status == 0){
                return "未审核";
            }else if(status == 1){
                return "运行中";
            }else if(status == 2){
                return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>创建中...";
            }else if(status == 3){
                return "创建失败";
            }else if(status == 4){
                return "<font color=\"red\">审核失败</font>";
            }else if(status == 5){
                return "<span class=\"text-danger\">异常</span>";
            }else if(status == 6){
                return "<span class=\"text-success\">正常<span>";
            }else if(status == 7){
                return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>启动中...";
            }else if(status == 8){
                return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>停止中...";
            }else if(status == 9){
                return "已停止";
            }else if(status == 10){
                return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>删除中...";
            }else if(status == 11){
                return "已删除";
            }else if(status == 12){
                return "不存在";
            }else if(status == 13){
                return "<font color=\"orange\">危险</font>";
            }else if(status == 14){
                return "<font color=\"red\">严重危险</font>";
            }else if(status == 15){
                return "禁用";
            }else if(status == 19){
            	return "删除失败";
            }
            else if(status == 'FAILD'){
                return "<font color=\"red\">备份失败</font>";
            }else if(status == 'SUCCESS'){
                return "<span class=\"text-success\">备份成功<span>";
            }else if(status == 'BUILDING'){
                return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>备份中...";
            }else if(status == 'ABNORMAL'){
                return "<font color=\"red\">备份异常</font>";
            }
            //cvm status
            else if(status == 'ACTIVE'){
                return '<span class=\"text-success\">活跃<span>';
            }
            else if(status == 'BUILD'){
                return '创建'
            }
            else if(status == 'PAUSED'){
                return '已暂停';
            }
            else if(status == 'SUSPENDED'){
                return '已挂起'
            }
            else if(status == 'DELETED'){
                return '已删除'
            }
            else if(status == 'SHUTOFF'){
                return '已停止'
            }
            //disk status
            else if(status == 'creating'){
                return '创建中'
            }
            else if(status == 'available'){
                return '可用的';
            }
            else if(status == 'attaching'){
                return '挂载中'
            }
            else if(status == 'in-use'){
                return '使用中'
            }
            else if(status == 'deleting'){
                return '删除中'
            }
            else if(status == 'error'){
                return '异常'
            }
            else{
                return status;
            }   
        },
        Displayable : function (status) {
            if(status == 6){
                return true;
            }
        },
        TransDate : function (format, timestamp){
            //var a, jsdate=((timestamp) ? new Date(timestamp) : new Date());
            var a;
            if(timestamp == null){
                return "---";
            }else{
                var jsdate=new Date(timestamp);
            }
            var pad = function(n, c){
                if((n = n + "").length < c){
                    return new Array(++c - n.length).join("0") + n;
                } else {
                    return n;
                }
            };
            var txt_weekdays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
            var txt_ordin = {1:"st", 2:"nd", 3:"rd", 21:"st", 22:"nd", 23:"rd", 31:"st"};
            var txt_months = ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            var f = {
                // Day
                d: function(){return pad(f.j(), 2)},
                D: function(){return f.l().substr(0,3)},
                j: function(){return jsdate.getDate()},
                l: function(){return txt_weekdays[f.w()]},
                N: function(){return f.w() + 1},
                S: function(){return txt_ordin[f.j()] ? txt_ordin[f.j()] : 'th'},
                w: function(){return jsdate.getDay()},
                z: function(){return (jsdate - new Date(jsdate.getFullYear() + "/1/1")) / 864e5 >> 0},
               
                // Week
                W: function(){
                    var a = f.z(), b = 364 + f.L() - a;
                    var nd2, nd = (new Date(jsdate.getFullYear() + "/1/1").getDay() || 7) - 1;
                    if(b <= 2 && ((jsdate.getDay() || 7) - 1) <= 2 - b){
                        return 1;
                    } else{
                        if(a <= 2 && nd >= 4 && a >= (6 - nd)){
                            nd2 = new Date(jsdate.getFullYear() - 1 + "/12/31");
                            return date("W", Math.round(nd2.getTime()/1000));
                        } else{
                            return (1 + (nd <= 3 ? ((a + nd) / 7) : (a - (7 - nd)) / 7) >> 0);
                        }
                    }
                },
               
                // Month
                F: function(){return txt_months[f.n()]},
                m: function(){return pad(f.n(), 2)},
                M: function(){return f.F().substr(0,3)},
                n: function(){return jsdate.getMonth() + 1},
                t: function(){
                    var n;
                    if( (n = jsdate.getMonth() + 1) == 2 ){
                        return 28 + f.L();
                    } else{
                        if( n & 1 && n < 8 || !(n & 1) && n > 7 ){
                            return 31;
                        } else{
                            return 30;
                        }
                    }
                },
               
                // Year
                L: function(){var y = f.Y();return (!(y & 3) && (y % 1e2 || !(y % 4e2))) ? 1 : 0},
                //o not supported yet
                Y: function(){return jsdate.getFullYear()},
                y: function(){return (jsdate.getFullYear() + "").slice(2)},
               
                // Time
                a: function(){return jsdate.getHours() > 11 ? "pm" : "am"},
                A: function(){return f.a().toUpperCase()},
                B: function(){
                    // peter paul koch:
                    var off = (jsdate.getTimezoneOffset() + 60)*60;
                    var theSeconds = (jsdate.getHours() * 3600) + (jsdate.getMinutes() * 60) + jsdate.getSeconds() + off;
                    var beat = Math.floor(theSeconds/86.4);
                    if (beat > 1000) beat -= 1000;
                    if (beat < 0) beat += 1000;
                    if ((String(beat)).length == 1) beat = "00"+beat;
                    if ((String(beat)).length == 2) beat = "0"+beat;
                    return beat;
                },
                g: function(){return jsdate.getHours() % 12 || 12},
                G: function(){return jsdate.getHours()},
                h: function(){return pad(f.g(), 2)},
                H: function(){return pad(jsdate.getHours(), 2)},
                i: function(){return pad(jsdate.getMinutes(), 2)},
                s: function(){return pad(jsdate.getSeconds(), 2)},
                //u not supported yet
               
                // Timezone
                //e not supported yet
                //I not supported yet
                O: function(){
                    var t = pad(Math.abs(jsdate.getTimezoneOffset()/60*100), 4);
                    if (jsdate.getTimezoneOffset() > 0) t = "-" + t; else t = "+" + t;
                    return t;
                },
                P: function(){var O = f.O();return (O.substr(0, 3) + ":" + O.substr(3, 2))},
                //T not supported yet
                //Z not supported yet
               
                // Full Date/Time
                c: function(){return f.Y() + "-" + f.m() + "-" + f.d() + "T" + f.h() + ":" + f.i() + ":" + f.s() + f.P()},
                //r not supported yet
                U: function(){return Math.round(jsdate.getTime()/1000)}
            };
            return format.replace(/[\\]?([a-zA-Z])/g, function(t, s){
                if( t!=s ){
                    // escaped
                    ret = s;
                } else if( f[s] ){
                    // a date function exists
                    ret = f[s]();
                } else{
                    // nothing special
                    ret = s;
                }
                return ret;
            });
        },
        RemainAvailableTime:function(ctimestamp){
            var timestamp = Date.parse(new Date());
            var differDays = parseInt((timestamp-ctimestamp)/(1000 * 60 * 60 * 24));
            // console.log("differTime:"+differDays+"  this.totalAvailableTime:"+this.totalAvailableTime);
            var remainDays = this.totalAvailableTime - differDays;
            return remainDays;
        },
        Sidebar : function(index){
            var extended = function(obj){   //obj为有二级菜单的li
                $(obj).removeClass("active");
                $(obj).find("ul").removeClass("hide");
                $(obj).find("span").removeClass("glyphicon glyphicon-chevron-right");
                $(obj).find("span").addClass("glyphicon glyphicon-chevron-down");
            }

            var stacked = function(obj){
                if($(obj).find("li").hasClass("active")){ //如果二级目录含有active
                    $(obj).addClass("active");
                }
                $(obj).find("ul").addClass("hide");
                $(obj).find("span").removeClass("glyphicon glyphicon-chevron-down");
                $(obj).find("span").addClass("glyphicon glyphicon-chevron-right");
            }
            /*菜单点击处理事件*/
            $('.sidebar-selector').find("a").click(function(){
                var $parent = $(this).closest("li");
                var str = $(this).attr("src");
                //判断src是否有值，若有值，则为有业务的菜单，若无值，判断parent是否为父菜单，若为父菜单，执行菜单收缩展开操作
                if(str) {
                    $("#frame-content").attr("src",str);
                    $('.sidebar-selector').find(".active").removeClass("active");
                    $parent.addClass("active");
                    if($('.nav-sidebar-div').hasClass("nav-sidebar-display")){
						$('.nav-sidebar-div').removeClass("nav-sidebar-display");
						iframeMobileEndInited = false;
					}
                }else {
                    if($parent.children("ul")){
                        //判断菜单是否展开
                        if($parent.children("ul").hasClass("hide")){
                            extended($parent);
                        }else{
                            stacked($parent);
                        }
                    }
                }
            });
        },
        GetData : function(url,handler){ //异步获取数据,将数据交给handler处理
            if($('body').find('.sidebar').length==0){   //layout界面不添加loading
               $('body').append("<div class=\"spin\"></div>");
               $('body').append("<div class=\"far-spin\"></div>");
            }
            return $.ajax({
                url:url,
                cache:false,
                type:"get",
                dataType:'json',
                success:function(data){
                    $('body').find('.spin').remove();
                    $('body').find('.far-spin').remove();
                    if(data.result==0){
                        var cn=new Common();
                        cn.alertoolDanger('操作出错了！'+data.msgs);
                        return;
                    }
                    /*添加当handler为空时的异常处理*/
                    if(handler){
                        handler(data);
                    }
                }
            });
        },
        GetLocalData : function(url,handler){ //异步获取数据,将数据交给handler处理
            $.ajax({
                url:url,
                cache:false,
                type:"get",
                dataType:'json',
                success:function(data){
                    if(data.result==0){
                        var cn=new Common();
                        cn.alertoolDanger('操作出错了！'+data.msgs);
                        return;
                    }
                    /*添加当handler为空时的异常处理*/
                    if(handler){
                        handler(data);
                    }
                }
            });
        },
        PostData : function (url,data,handler,obj,content){ //异步提交数据,将返回数据交给handler处理
            return $.ajax({
                url:url,
                cache:false,
                type:"post",
                dataType:'json',
                data:data,
                success:function(data){
                    $(obj).removeClass('disabled').removeAttr('disabled').text(content)
                    if(data.result==0){
                        var cn=new Common();
                        cn.alertoolDanger('操作出错了！'+data.msgs);
                        return;
                    }
                    /*添加当handler为空时的异常处理*/
                    if(handler){
                        handler(data);
                    }  
                }
            });
        },
        DeleteData : function (url,handler){ //异步提交数据,将返回数据交给handler处理
            $.ajax({
                url:url,
                type:"delete",
                dataType:'json',
                success:function(data){
                    if(data.result==0){
                        var cn=new Common();
                        cn.alertoolDanger('操作出错了！'+data.msgs);
                        return;
                    }
                    /*添加当handler为空时的异常处理*/
                    if(handler){
                        handler(data);
                    }
                }
            });
        },
        EditBoxInit: function(obj){
            var offset = $(obj).offset();
            var left = offset.left;
            var top = offset.top+$(obj).height()+5;         
            $("#edit-spin").addClass("edit-spin");

            $(".edit-text-box").css('left',left+"px");
            $(".edit-text-box").css('top',top+"px");
            $(".edit-text-box").removeClass("hide");

            $("#editBoxCancel").unbind("click").click(function(){
                $(".edit-text-box").addClass("hide");
                $("#edit-spin").removeClass("edit-spin");
            })
        },
        DialogBoxInit:function(title,text,handler,args){
            $("#dialog-box").find("#dialog-box-title").html(title);
            $("#dialog-box").find("#dialog-box-text").html("<h5 style='text-align: center; '>"+text+"</h5>");
            $("#dialogBoxSubmit").unbind("click").click(function(){
                handler(args);
            })
            $('#dialog-box').modal({
                backdrop:false,
                show:true
            });
            $("#submitCreateUserForm").removeAttr("disabled");
            $("#submitModifyUserForm").removeAttr("disabled");
        },
        DisableBackspaceEnter:function(){
            document.getElementsByTagName("body")[0].onkeydown  = function(event) {
                var target, code,tag;
                if (!event) {
                    event = window.event; //针对ie浏览器
                    target = event.srcElement;
                    code = event.keyCode;
                    if (code == 13) {
                        tag = target.tagName;
                        if (tag == "TEXTAREA") { return true; }
                        else { return false; }
                    }else if(code == 8){
                        tag = target.tagName;
                        if(tag!='INPUT' && tag!='TEXTAREA'){
                            event.returnValue = false ;
                            return ;
                        }
                        var type_e = target.type.toUpperCase();
                        if(tag=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){
                            event.returnValue = false ;
                            return ;
                        }
                        if(tag=='INPUT' && (target.readOnly==true || target.disabled ==true)){
                            event.returnValue = false ;
                            return ;
                        }
                    }
                }
                else {
                    target = event.target; //针对遵循w3c标准的浏览器，如Firefox
                    code = event.keyCode;
                    if (code == 13) {
                        tag = target.tagName;
                        if (tag == "INPUT") { return false; }
                        else { return true; }
                    }else if(code == 8){
                        tag = target.tagName;
                        if(tag!='INPUT' && tag!='TEXTAREA'){
                            event.returnValue = false ;
                            return ;
                        }
                        var type_e = target.type.toUpperCase();
                        if(tag=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){
                            event.returnValue = false ;
                            return ;
                        }
                        if(tag=='INPUT' && (target.readOnly==true || target.disabled ==true)){
                            event.returnValue = false ;
                            return ;
                        }
                    }
                }
            };
        },
        TableFilterNull:function(data){
            if(data == null || data == undefined){
                return '-';
            }else{
                return data;
            }
        },
        FilterNull:function(data){
            if(data == null || data == undefined){
                return '';
            }else{
                return data;
            }
        },
        /*刷新ifame*/
        RefreshIfame:function(url){
            var $iframe = $("body",parent.document).find("iframe");
            if(url == undefined || url == null){
                //刷新dangqianye
                $iframe.attr("src",$("body", parent.document).find("iframe").attr("src"));
            }else{
                $iframe.attr("src",url);
            }
        },
        /*弹出窗口居中*/
        center:function(){
            //获取父窗口对象
            var winobj=window.parent.document.body;
            //获取父窗口对象属性
            var screenWidth = $(winobj).width();
            //var screenHeight = $(winobj).height();
            //var scrolltop = $(winobj).scrollTop();//获取当前窗口距离页面顶部高度
            
            //获取子窗口及对象及子窗口对象的属性
            var iframebody= document.body;
            var iframebodyWidth = $(iframebody).width();
            //var iframebodyHeight = $(iframebody).height();
            
            //获取探矿对象
            var $obj = $(".modal-dialog");
            //alert($obj.offset().top);
            var objLeft = ($obj.offset().left - (screenWidth-iframebodyWidth)/2); 
            //var objTop = ($obj.offset().top  - (screenHeight-iframebodyHeight)/2);
            $obj.css({'left':  objLeft + 'px','display': 'block'});
        },
        divselect: function () {
            $('.divselect').closest('.pull-left').unbind("click").click(function(event){
                event.stopPropagation();
                var ul = $(this).find('ul');
                var index=document.body.clientHeight-event.clientY;
                if(index<=100){//下拉框适配位置，或上或下显示
                    ul.css({
                        top: '-36px'
                    });
                }
                if(ul.css("display")=="none"){
                    $('.divselect').find('ul').hide().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-o-transform:rotate(0deg);-ms-transform: rotate(0deg);");//关闭所有select
                    ul.show().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(180deg);-moz-transform:rotate(180deg);-o-transform:rotate(180deg);-ms-transform: rotate(180deg);");
                }else{
                    ul.hide().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-o-transform:rotate(0deg);-ms-transform: rotate(0deg);");
                }
            })
            $(document).click(function () {
                $('.divselect').find('ul').hide().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-o-transform:rotate(0deg);-ms-transform: rotate(0deg);");
            })

            $('.divselect').find("ul li").click(function(){
                var txt = $(this).find('a').text();
                $(this).closest('.divselect').find('span').html(txt);
                var value = $(this).find('a').attr("selectid");
                $(this).closest('.divselect').find('input').val(value).change();
            });
            $(".divselect").each(function () {
                if($(this).find('span').html() == ''&&$(this).find('li').length > 0){
                    $(this).find('ul li').first().click();
                }
            })
        },
        divSelect: function () {
            $('.divselect').closest('.pull-left').click(function(event){
                event.stopPropagation();
                var ul = $(this).find('ul');
                var index=document.body.clientHeight-event.clientY;
                if(index<=100){//下拉框适配位置，或上或下显示
                    ul.css({
                        top: '-36px'
                    });
                }
                if(ul.css("display")=="none"){
                    $('.divselect').find('ul').hide().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-o-transform:rotate(0deg);-ms-transform: rotate(0deg);");//关闭所有select
                    ul.show().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(180deg);-moz-transform:rotate(180deg);-o-transform:rotate(180deg);-ms-transform: rotate(180deg);");
                }else{
                    ul.hide().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-o-transform:rotate(0deg);-ms-transform: rotate(0deg);");
                }
            })
            $(document).click(function () {
                $('.divselect').find('ul').hide().closest('.pull-left').find('.bk-select-arrow').attr("style","-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-o-transform:rotate(0deg);-ms-transform: rotate(0deg);");
            })

            $(".divselect").click(function(e) {
						e = e ? e : window.event;
						var target = e.target || e.srcElement;
						if ($(target).is('li')) {
							target = $(target).find('a');
						}
						if($(target).is('a')){
							$(this).find('span').html($(target).text());//设置select显示标签
							$(this).find('input').val($(target).attr("selectid")).change();//设置select input 值，并处发change事件
						}
					})
            $(".divselect").each(function () {
                if($(this).find('span').html() == ''&&$(this).find('li').length > 0){
                    $(this).find('ul li').first().click();
                }
            })
        },
        getBackupDate:function(){
            var myDate = new Date();            
            var timestamp = myDate.valueOf();           
            var hour = myDate.getHours();
            
            if(hour > 4){
                timestamp = timestamp + 86400000;
            }else{
                timestamp = timestamp;
            }
            
            var newDate = new Date(timestamp);
            var year = newDate.getFullYear();
            var month = newDate.getMonth() + 1;
            var day = newDate.getDate();
            
            return year + '年' + month + '月' + day + '日';
        },
        emptyBlock:function(obj){
            if($("#noData").length < 1){
                $(obj).closest("div").after("<div class=\"help-block\" id=\"noData\">没有数据记录</div>");
            }           
        },
        TranslateAgentType:function(data){
            if(data = 1){
                return "http";
            }else{
                return "tcp";
            }
        },
        formatJson:function(json){
                var i           = 0,
                    il          = 0,
                    tab         = "    ",
                    newJson     = "",
                    indentLevel = 0,
                    inString    = false,
                    currentChar = null;
                var cn = new Common;
    
                for (i = 0, il = json.length; i < il; i += 1) { 
                    currentChar = json.charAt(i);
        
                    switch (currentChar) {
                    case '{': 
                    case '[': 
                        if (!inString) { 
                            newJson += currentChar + "\n" + cn.repeat(tab, indentLevel + 1);
                            indentLevel += 1; 
                        } else { 
                            newJson += currentChar; 
                        }
                        break; 
                    case '}': 
                    case ']': 
                        if (!inString) { 
                            indentLevel -= 1; 
                            newJson += "\n" + cn.repeat(tab, indentLevel) + currentChar; 
                        } else { 
                            newJson += currentChar; 
                        } 
                        break; 
                    case ',': 
                        if (!inString) { 
                            newJson += ",\n" + cn.repeat(tab, indentLevel); 
                        } else { 
                            newJson += currentChar; 
                        } 
                        break; 
                    case ':': 
                        if (!inString) { 
                            newJson += ": "; 
                        } else { 
                            newJson += currentChar; 
                        } 
                        break; 
                    case ' ':
                    case "\n":
                    case "\t":
                        if (inString) {
                            newJson += currentChar;
                        }
                        break;
                    case '"': 
                        if (i > 0 && json.charAt(i - 1) !== '\\') {
                            inString = !inString; 
                        }
                        newJson += currentChar; 
                        break;
                    default: 
                        newJson += currentChar; 
                        break;                    
                    } 
                } 
            return newJson; 
        },
        repeat:function(s, count){
             return new Array(count + 1).join(s);
        },  

         /*add new common function*/
         //2015-04-13 by gm new add drag及相关
         baseData:function(){
            var _base=$('.bk-slider-l2');
            var level1=_base.width();//0-250:206
            var level2=_base.next().width();//103
            var level3=_base.next().next().width();//103
            var length={
                lev1:level1,
                lev2:level1+level2,
                lev3:level1+level2+level3
            }
            return length;
        },
        btnPrimaryClick:function(){
            var _current=$('.bk-button-primary');
            _current.click(function(event) {
                var _this=$(this);
                if(_this.hasClass('bk-button-current')){

                }else{
                    _this.removeClass('disabled').addClass('bk-button-current');
                    _this.siblings().removeClass('bk-button-current');
                }
            });
        },
        dragBarInite:function(options){
            // var len=options.stepSize;
            var len=options.min;
            var width=len*options.lev1/options.grade1;
            var html='<div class="bk-slider"><div class="bk-slider-range" id="bar0"><span class="bk-slider-block bk-slider-l2"><span class="bk-slider-block-box"><span class="bk-slider-txt">'+options.grade1+options.unit+'</span></span></span><span class="bk-slider-block bk-slider-l1"><span class="bk-slider-block-box">'
                    +'<span class="bk-slider-txt">'+options.grade2+options.unit+'</span></span></span> <span class="bk-slider-block bk-slider-l1" style="margin-left:-4px;"><span class="bk-slider-block-box bk-slider-block-box-last bk-select-action"><span class="bk-slider-txt">'+options.grade3+options.unit+'</span>'
                    +'</span></span><span class="bk-slider-container bk-slider-transition" id="layer2" style="width:'+width+'px;"><span class="bk-slider-current"><span class="bk-slider-unit bk-slider-l2"><span class="bk-slider-unit-box">'
                    +'<span class="bk-slider-txt">'+options.grade1+options.unit+'</span></span></span><span class="bk-slider-unit bk-slider-l1"><span class="bk-slider-unit-box"><span class="bk-slider-txt">'+options.grade2+options.unit+'</span></span></span>'
                    +'<span class="bk-slider-unit bk-slider-l1"><span class="bk-slider-unit-box bk-slider-unit-box-last"><span class="bk-slider-txt">'+options.grade3+options.unit+'</span></span>'
                    +'</span></span></span><span class="awCursor" style="left:'+width+'px;"><div style="position: relative" class="ng-scope"><div class="bk-tip-gray hide" style="top: -44px;left: -18px"><span class="ng-binding">'+options.min+'</span> <span class="bk-tip-arrow"></span>'
                    +'</div></div></span></div></div><span class="bk-number bk-ml2"><input type="text" class="bk-number-input memSize" id="value2" name="ramQuotaMB"><span class="bk-number-unit">'+options.unit+'</span><span class="bk-number-control"><span class="bk-number-up mem-num-up"> <i class="bk-number-arrow"></i> </span>' 
                    +'<span class="bk-number-down mem-num-down bk-number-disabled"> <i class="bk-number-arrow"></i></span> </span></span>';
            $('.self-dragBar').append(html);
            // $('.memSize').val(options.stepSize);
            $('.memSize').val(options.min);
        },
        dragBarUpdate:function(options){
            var _this=$('.bk-slider-range').children('.bk-slider-l2');
            _this.find('.bk-slider-txt').text(options.grade1);
            _this.next().find('.bk-slider-txt').text(options.grade2);
            _this.next().next().find('.bk-slider-txt').text(options.grade3);
            var _grayLayer=$('.bk-slider-transition').find('.bk-slider-l2');
            _grayLayer.find('.bk-slider-txt').text(options.grade1);
            _grayLayer.next().find('.bk-slider-txt').text(options.grade2);
            _grayLayer.next().next().find('.bk-slider-txt').text(options.grade3);
            var len=options.stepSize;
            var width=len*options.lev1/options.grade1;
            $('#layer2').css('width',width);
            $('.awCursor').css('left',width);
            $('.memSize').val(options.stepSize);
            var curPosition=$('#layer2').width();
            drag(curPosition,options);
        },
        barDrag:function(options){//滑动条拖动
            var _awCursor=$('.awCursor');
            var _slider=$('.bk-slider');
            var dgFlag=false;
            var temp;
            _awCursor.on('mousedown',function(event) {
                event.preventDefault();
                dgFlag=true;
                $(this).prev().removeClass('bk-slider-transition');
            });
            $('body').on('mousemove',function(event) {
                event.preventDefault();
                if(dgFlag){
                    //可以拖拽
                    _awCursor.find('.bk-tip-gray').removeClass('hide');
                    var left=event.pageX;
                    var left2=_slider.offset().left;
                    temp=parseInt(left)-parseInt(left2);

                    if(temp>414){
                        //拖拽过最右界
                        $('.mem-num-up').addClass('bk-number-disabled');
                        temp=412;
                    }else{
                         if(temp<=0){
                            //拖拽过最左界
                            $('.mem-num-down').addClass('bk-number-disabled');
                            // temp=options.stepSize*options.lev1/options.grade1;
                            temp=options.min*options.lev1/options.grade1;
                         }else{
                            //允许范围之内
                            $('.mem-num-up').removeClass('bk-number-disabled');
                            $('.mem-num-down').removeClass('bk-number-disabled');
                        }               
                    }
                    drag(temp,options);
                }else{
                    //不能拖拽
                }
            });
            $('body').on('mouseup',function(event) {
                event.preventDefault();
                dgFlag=false;
                _awCursor.find('.bk-tip-gray').addClass('hide');
                _awCursor.prev().addClass('bk-slider-transition');
            });
        },
        barClickDrag:function(options){
            var _slider=$('.bk-slider');
            _slider.on('click',function(event) {
                event.preventDefault();
                var left=event.pageX;
                var left2=_slider.offset().left;
                var relaLen=left-left2;
                drag(relaLen,options);
            });
        },
        inputChge:function(options){
            var _memSize=$('.memSize');
            var val=_memSize.val();
            inputChgeDrag(val,options);
        },
        chgeBuyNmu:function(){
            var _taiNum=$('.tai-num');
            var _upT=$('.tai-num-up');var _downT=$('.tai-num-down');
            var val=_taiNum.val();
            val=parseInt(val);
            if(val<=1){
                val=1;
                _downT.addClass('bk-number-disabled');
            }else if(val>=99){
                val=99;
                _upT.addClass('bk-number-disabled');
            }else{
                //合法范围
                _upT.removeClass('bk-number-disabled');
                _downT.removeClass('bk-number-disabled');
            }
            _taiNum.val(val);
        },
        inite:function(){
            var h=document.body.scrollHeight;
            var ih=window.innerHeight?window.innerHeight:document.body.clientHeight;
            var temph,tempw;
            if(h>ih){
                temph=h;
            }else{
                temph=ih;
            }
            tempw=$('body').width();
            $('.shade').css({
                width: tempw,
                height: temph
            });
            var _target=$('.shade');
            var _tiparray=$('[data-type="tip"]');
            var length=_tiparray.length;
            tempArry=new Array(length);
            var content;
            var left=0,top=0,rel=0;
            _tiparray.each(function() {
                content=$(this).attr('data-content');
                var order=$(this).attr('data-order');
                var position=$(this).attr('data-position');
                left=$(this).offset().left+($(this).width())/2;//默认bottom配置
                top=$(this).offset().top+$(this).height();
                if(order=='1'){//初始化序号最先的显示
                    // var html='<button class="btn btn-danger" style="position:absolute;top:'+(top-25)+'px;left:'+(left-25)+'px;><span class="badge">'+order+'</span></button><div class="tiptool '+position+'" data-order="'+order+'" style="top:'+top+'px;left:'+left+'px;opacity:1;filter:alpha(opacity=1);"><div class="tiptool-arrow"></div><div class="tiptool-inner">'+content+'<br><div class="tip-btngroup"><button class="btn btn-default btn-sm"> 跳出</button>&nbsp;<button class="btn btn-default btn-sm" disabled><i class="fa fa-long-arrow-left"></i> 上一步</button>&nbsp;<button class="btn btn-primary btn-sm step-nxt">下一步 <i class="fa fa-long-arrow-right"></i></button></div></div></div>';
                    var html='<div class="tiptool '+position+'" data-order="'+order+'" style="top:'+top+'px;left:'+left+'px;opacity:1;filter:alpha(opacity=1);"><div class="tiptool-arrow"></div><div class="tiptool-inner">'+content+'<br><div class="tip-btngroup"><button class="btn btn-default btn-sm"> 跳出</button>&nbsp;<button class="btn btn-default btn-sm" disabled><i class="fa fa-long-arrow-left"></i> 上一步</button>&nbsp;<button class="btn btn-primary btn-sm step-nxt">下一步 <i class="fa fa-long-arrow-right"></i></button></div></div></div>';
                }else{
                    // var html='<button class="btn btn-danger btn-lucency" style="position:absolute;top:'+(top-25)+'px;left:'+(left-25)+'px;><span class="badge">'+order+'</span></button><div class="tiptool '+position+'" data-order="'+order+'" style="top:'+top+'px;left:'+left+'px;"><div class="tiptool-arrow"></div><div class="tiptool-inner">'+content+'<br><div class="tip-btngroup"><button class="btn btn-default btn-sm"> 跳出</button>&nbsp;<button class="btn btn-default btn-sm step-pre"><i class="fa fa-long-arrow-left"></i> 上一步</button>&nbsp;<button class="btn btn-primary btn-sm step-nxt">下一步 <i class="fa fa-long-arrow-right"></i></button></div></div></div>';
                    var html='<div class="tiptool '+position+'" data-order="'+order+'" style="top:'+top+'px;left:'+left+'px;"><div class="tiptool-arrow"></div><div class="tiptool-inner">'+content+'<br><div class="tip-btngroup"><button class="btn btn-default btn-sm"> 跳出</button>&nbsp;<button class="btn btn-default btn-sm step-pre"><i class="fa fa-long-arrow-left"></i> 上一步</button>&nbsp;<button class="btn btn-primary btn-sm step-nxt">下一步 <i class="fa fa-long-arrow-right"></i></button></div></div></div>';
                }
                tempArry[order]=html;
                _target.append(tempArry[order]);
                if(position=='top'){
                    rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().left-$('.tiptool[data-order='+order+']').offset().left;
                    top=$(this).offset().top-$('.tiptool[data-order='+order+']').height();
                    left=left-rela;
                }else if(position=='bottom'){
                    rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().left-$('.tiptool[data-order='+order+']').offset().left;
                    left=left-rela;
                }else if(position=='left'){
                    rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().top-$('.tiptool[data-order='+order+']').offset().top;
                    top=$(this).offset().top+($(this).height())/2-rela; 
                    left=$(this).offset().left-250;
                }else{
                    rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().top-$('.tiptool[data-order='+order+']').offset().top;
                    top=$(this).offset().top-rela;
                    left=$(this).offset().left+$(this).width();
                }
                if(order=='1'){
                    $('[data-type="tip"][data-order='+order+']').addClass('tipbg');
                }
                $('.tiptool[data-order='+order+']').css({
                    left:left+"px",
                    top:top+"px"
                }).prev('button').css({
                    left:left-25+"px",
                    top:top-25+"px"
                });
            });
        },
        validate:function(){
            var current;
            $('.tiptool').find('button').click(function(event) {
                var order=$(this).closest('.tiptool').attr('data-order');
                var i;
                if($(this).hasClass('step-nxt')){
                    i=parseInt(order)+1;
                    if(i==(tempArry.length-1)){
                        $('.tiptool[data-order='+i+']').find('.step-nxt').attr('disabled', 'true');
                    }
                }else if($(this).hasClass('step-pre')){
                    i=parseInt(order)-1;
                }else{
                    $('.shade').css('display', 'none');
                    setCookie('tiptool','false');
                }
                $(this).closest('.tiptool').css({
                    opacity: '0',
                    filter: 'alpha(opacity=0)'
                }).prev('button').addClass('btn-lucency');
                $('[data-type="tip"][data-order='+i+']').addClass('tipbg');
                current=i;
                $('.tiptool[data-order='+i+']').css({
                    opacity: '1',
                    filter: 'alpha(opacity=1)'
                }).prev('button').removeClass('btn-lucency');
                var len=tempArry.length-1;
                if(i<=len){
                }else{
                    $('.shade').css('display', 'none');
                    setCookie('tiptool','false');
                }
                while(current>=0){
                    current--;
                    if(current!=i){
                        $('[data-type="tip"][data-order='+current+']').removeClass('tipbg');
                    }
                }
                while(current<=len){
                    current++;
                    if(current!=i){
                        $('[data-type="tip"][data-order='+current+']').removeClass('tipbg');
                    }
                }
            });
        },
        resizeUpdate:function(){
            $(window).resize(function(event) {
                setSize();
                //重新定义位置
                var oritipE=$('[data-type=tip]');
                oritipE.each(function() {
                    var order=$(this).attr('data-order');
                    var rela;
                    var left=$(this).offset().left+$(this).width()/2;//默认bottom配置
                    var i=parseInt($(this).offset().top);var j=parseInt($(this).height());
                    var top=i+j; 
                    var position=$(this).attr('data-position');
                    if(position=='top'){
                        rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().left-$('.tiptool[data-order='+order+']').offset().left;
                        top=$(this).offset().top-$('.tiptool[data-order='+order+']').height();
                        left=left-rela;
                    }else if(position=='bottom'){
                        rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().left-$('.tiptool[data-order='+order+']').offset().left;
                        left=left-rela;
                    }else if(position=='left'){
                        rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().top-$('.tiptool[data-order='+order+']').offset().top;
                        top=$(this).offset().top+($(this).height())/2-rela; 
                        left=$(this).offset().left-250;
                    }else{
                        rela=$('.tiptool[data-order='+order+']').find('.tiptool-arrow').offset().top-$('.tiptool[data-order='+order+']').offset().top;
                        top=$(this).offset().top-rela;
                        left=$(this).offset().left+$(this).width();
                    }
                    $('.tiptool[data-order='+order+']').css({
                        left:left+"px",
                        top:top+"px"
                    }).prev('button').css({
                        left:left-25+"px",
                        top:top-25+"px"
                    });
                });

            });
        },
        getCookie:function(name){//读取cookie值
            var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
            if(arr=document.cookie.match(reg)){
                return unescape(arr[2]); 
            }else{
                return null;
            }
        },
        deleteCookie:function(name){//删除cookie值
            var exp = new Date(); 
            exp.setTime(exp.getTime() - 1); 
            var cval=getCookie(name); 
            if(cval!=null){
                document.cookie= name + "="+cval+";expires="+exp.toGMTString();
            }
        },
        gceTypeTranslation:function(type){
            if(type == "JETTY"){
                return "JETTY";
            }else if(type == "NGINX"){
                return "NGINX";
            }else if(type == "TOMCAT"){
                return "TOMCAT";
            }else{
                return "-";
            }
        },
        alertoolDanger:function(msg,time){
            if(time == null){
                time=6000;
            }
            var html =$("<div class=\"alert alert-danger alert-dismissible\" role=\"alert\">"
            + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>"
            + "<strong>DANGER!   </strong>"+msg
            + "</div>");
            var _target=$('#alertool',parent.document);
            if (top.location != location) { //iframe
                if(_target.length>0){//有alertool
                    $('#alertool',parent.document).append(html);
                }else{
                    $('body',parent.document).append("<div id='alertool' class='col-xs-12 col-md-offset-10 col-md-2 alertool'></div>")
                    $('#alertool',parent.document).append(html);
                }
            } else if (top.location == location) {//layout
                if(_target.length>0){//有alertool
                    $('#alertool').append(html);
                }else{
                    $('body').append("<div id='alertool' class='col-xs-12 col-md-offset-10 col-md-2 alertool'></div>")
                    $('#alertool').append(html);
                }
            }
            html.fadeOut(time)
            window.setTimeout(function(){html.remove();},time+500);
        },
        alertoolWarnning:function(msg,time){
            if(time == null){
                time=6000;
            }
            var html =$("<div class=\"alert alert-warning alert-dismissible\" role=\"alert\">"
            + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>"
            + "<strong>WARNING!   </strong>"+msg
            + "</div>");
            var _target=$('#alertool',parent.document);
            if (top.location != location) { //iframe
                if(_target.length>0){//有alertool
                    $('#alertool',parent.document).append(html);
                }else{
                    $('body',parent.document).append("<div id='alertool' class='col-xs-12 col-md-offset-10 col-md-2 alertool'></div>")
                    $('#alertool',parent.document).append(html);
                }
            } else if (top.location == location) {//layout
                if(_target.length>0){//有alertool
                    $('#alertool').append(html);
                }else{
                    $('body').append("<div id='alertool' class='col-xs-12 col-md-offset-10 col-md-2 alertool'></div>")
                    $('#alertool').append(html);
                }
            }
            html.fadeOut(time);
            window.setTimeout(function(){html.remove();},time+500);
        },
         alertoolSuccess:function(msg,time){
            if(time == null){
                time=6000;
            }
            var html =$("<div class=\"alert alert-success alert-dismissible\" role=\"alert\">"
            + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>"
            + "<strong>SUCCESS!   </strong>"+msg
            + "</div>");
            var _target=$('#alertool',parent.document);
            if (top.location != location) { //iframe
                if(_target.length>0){//有alertool
                    $('#alertool',parent.document).append(html);
                }else{
                    $('body',parent.document).append("<div id='alertool' class='col-xs-12 col-md-offset-10 col-md-2 alertool'></div>")
                    $('#alertool',parent.document).append(html);
                }
            } else if (top.location == location) {//layout
                if(_target.length>0){//有alertool
                    $('#alertool').append(html);
                }else{
                    $('body').append("<div id='alertool' class='col-xs-12 col-md-offset-10 col-md-2 alertool'></div>")
                    $('#alertool').append(html);
                }
            }
            html.fadeOut(time);
            window.setTimeout(function(){html.remove();},time+500);
        },
        initNavbarMenu:function(data){
        	for(var i=0,len=data.length;i<len;i++){
        		$("#navbar-menu").find(".navbar-header").append("<a class=\"navbar-brand m-brand"+(data[i].isActive? " active":"")+"\" href=\""+data[i].herf+"\">"+data[i].name+"</a>");
        	}
        },
        uploadfile:function(target){
            var isIE = /msie/i.test(navigator.userAgent) && !window.opera; 
            var flag=false;
            var filePath=target.value;
            var fileType={
                '.exe':'exe',
                '.pdf':'pdf','.txt':'txt','.docx':'docx','.doc':'doc','.xlsx':'xlsx','.xls':'xls','.pptx':'pptx','.ppt':'ppt',
                '.png':'png','.jpg':'jpg','.gif':'gif','.bmp':'bmp',
                '.rar':'rar','.zip':'zip','.tar':'tar','.jar':'jar','.z':'z','.gz':'gz',
                '.avi':'avi','.mov':'mov','.wmv':'wmv','.3gp':'3gp','.flv':'flv',
                '.mp3':'mp3','.rm':'rm','.swf':'swf','.wma':'wma','.wav':'wav','.mp3pro':'mp3pro'
            }
            var filemaxsize=1024*1024*1024*2;//G
            var filesuffix=filePath.substring(filePath.indexOf('.'));
            // if(fileType[filesuffix]){
            //     //符合要求
            // }else{
            //     alert('不允许此种类型');
            //     return false;
            // }
            if (isIE && !target.files) { 
                var filePath = target.value; 
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject"); 
                if(!fileSystem.FileExists(filePath)){ 
                    alert("附件不存在，请重新输入！"); 
                    return false; 
                } 
                var file = fileSystem.GetFile (filePath); 
                fileSize = file.Size; 
            }else { 
                fileSize = target.files[0].size; 
            } 
            var size = fileSize / 1024; 
            if(size>filemaxsize){ 
                alert("附件大小不能大于"+filemaxsize/1024+"M！"); 
                target.value =""; 
                return false; 
            } 
            if(size<=0){ 
                alert("附件大小不能为0M！"); 
                target.value =""; 
                return false; 
            }
            return true;
        },
        getFile:function(target){
            var filePath=target.value;
            var file;
            var isIE = /msie/i.test(navigator.userAgent) && !window.opera; 
            if(isIE&& !target.files){
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                if(fileSystem.FileExists(filePath)){ 
                    file=fileSystem.GetFile(filePath);
                } 
            }else{
                file=target.files[0];
            }
            return file;
        },
        //删除文件夹弹窗
        DelFolderModal:function(title,content){
            var html='<div class="modal fade in" id="DelFolderModal">'
                        +'<div class="modal-dialog">'
                        +'<div class="modal-content">'
                        +'<div class="modal-header">'
                        +'<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>'
                        +'<h4 class="modal-title">'+title+'</h4>'
                        +'</div>'
                        +'<div class="modal-body clearfix">'
                        +'<div class="col-xs-2 col-sm-1 text-center">'
                        +'<i class="text-warning fa fa-warning text-size-32"></i>'
                        +'</div>'
                        +'<div class="col-xs-10 col-sm-11">'
                        +'<p class="help-tip">'+content+'</p>'
                        +'</div>'
                        +'</div>'
                        +'<div class="modal-footer">'
                        +'<button type="submit" class="btn btn-primary" id="delfolder">确定</button>'
                        +'<button class="btn btn-default" data-dismiss="modal">取消</button>'
                        +'</div>'
                        +'</div>'
                        +'</div>'
                        +'</div>';
            $('body').append(html);
            $('#DelFolderModal').modal({
                show:true,
                backdrop:false
            })
        },
        TransUnit:function(num){
            var temp=Math.abs(num);
            if(temp<1024){//B
                temp=temp+'B';
            }else if(temp<1024*1024){//KB
                temp= Math.round(temp/1024*Math.pow(10, 2))/Math.pow(10, 2)+'KB'
            }else if(temp<1024*1024*1024){//MB
                temp=Math.round(temp/1024/1024*Math.pow(10, 2))/Math.pow(10, 2)+'MB'
            }else{//GB
                temp=Math.round(temp/1024/1024/1024*Math.pow(10, 2))/Math.pow(10, 2)+'GB';
            }
            if(num<0){
                temp='-'+temp
            }
            return temp;
        },
        AddBeforeunloadListener:function(text){
            $(window).on('beforeunload', function(){
                return text || '表单还没有提交。';
            });
        },
        RemoveBeforeunloadListener:function(text){
            $(window).off('beforeunload');
        },
        validateInputNum:function(inputValue){
            var reg=/^[1-9]+[0-9]*/;
            return reg.exec(inputValue)==null? false:true;
        },
        NumberInput:function(minNum,maxNum,increment,onChange){
            var that=this;
            var numberUpEl=$('.bk-number-up');
            var numberDownEl=$('.bk-number-down');
            var numberInputEl=$('.bk-number-input');

            numberInputEl.on('change',function(e){
                onChange(e);
            });
            numberInputEl.on('blur', function(e){
                if(!that.validateInputNum(numberInputEl.val())){
                    numberInputEl.val(minNum.toString());
                    numberInputEl.trigger('change');
                }
            });
            numberDownEl.on('click',function(e){
                var currentNum=parseFloat(numberInputEl.val());
                if(currentNum==minNum){
                    return;
                }
                if((currentNum-increment)==minNum){
                    numberDownEl.addClass('bk-number-disabled');
                }
                if(currentNum==maxNum){
                    numberUpEl.removeClass('bk-number-disabled');
                }
                numberInputEl.val((currentNum-increment).toString());
                numberInputEl.trigger('change');
            });
            numberUpEl.on('click',function(e){
                var currentNum=parseFloat(numberInputEl.val());
                if(currentNum==maxNum){
                    return;
                }
                if((currentNum+increment)==maxNum){
                    numberUpEl.addClass('bk-number-disabled');
                }
                if(currentNum==minNum){
                    numberDownEl.removeClass('bk-number-disabled');
                }
                numberInputEl.val((currentNum+increment).toString());
                numberInputEl.trigger('change');
            });
        }
    }
    /*common原型属性方法end*/
    /*common 私有方法start*/
    function drag(relaLen,options){
        var _memSize=$('.memSize');
        var _awCursor=$('.awCursor');
        var _layer2=$('#layer2');
        var _up=$('.mem-num-up');
        var _down=$('.mem-num-down');
        var base=options.stepSize;
        var baseL=options.min*options.lev1/options.grade1;
        var lev1=options.lev1;//0-250:206 grade1
        var lev2=options.lev2;//250-500:206+103 grade2
        var lev3=options.lev3;//500-1000:206+103+103 grade3
        if(relaLen>lev3){
            //不合适
            _up.addClass('bk-number-disabled');
        }else{
            _up.removeClass('bk-number-disabled');
            _down.removeClass('bk-number-disabled');
            if(relaLen>lev2){
                //500-1000档
                var tempL=parseInt(relaLen)-lev2;
                // var temp=tempL*500/(lev2-lev1);
                var temp=tempL*(options.grade3-options.grade2)/(lev3-lev2);
                var i=Math.ceil(temp);
                i=base*Math.ceil(i/base);
                // temp=i+500;
                temp=i+options.grade2;
                _memSize.val(temp);
                _awCursor.find('.ng-binding').text(temp);
                _layer2.css({
                    width:relaLen
                });
                _awCursor.css({
                    left:relaLen
                });
            }else if(relaLen>lev1){
                //250-500
                var tempL=parseInt(relaLen)-lev1;
                // var temp=tempL*250/(lev2-lev1);
                // var temp=tempL*options.grade1/(lev2-lev1);
                var temp=tempL*(options.grade2-options.grade1)/(lev2-lev1);
                var i=Math.ceil(temp);
                i=base*Math.ceil(i/base);
                // temp=i+250;
                temp=i+options.grade1;
                _memSize.val(temp);
                _awCursor.find('.ng-binding').text(temp);
                _layer2.css({
                    width:relaLen
                });
                _awCursor.css({
                    left:relaLen
                });
            // }else if(relaLen>=base){
            }else if(relaLen>=baseL){
                //5-250
                var tempL=parseInt(relaLen);
                // var temp=250*tempL/lev1;
                var temp=options.grade1*tempL/lev1;
                var j=Math.ceil(temp/base);
                tempL=j*base;
                _memSize.val(tempL);
                _awCursor.find('.ng-binding').text(tempL);
                _layer2.css({
                    width:relaLen
                });
                _awCursor.css({
                    left:relaLen
                });
            }else{
                //<5 不合适
                _down.addClass('bk-number-disabled')
            }
        }
    }
    function inputChgeDrag(val,options){
        var _memSize=$('.memSize');
        var _awCursor=$('.awCursor');
        var _layer2=$('#layer2');
        var base=options.stepSize;
        var lev1=options.lev1;//0-250:206 grade1
        var lev2=options.lev2;//250-500:206+103 grade2
        var lev3=options.lev3;//500-1000:206+103+103 grade3
        var lenPerc;
        var i=Math.ceil(val/base);
        val=i*base;
        _memSize.val(val);
        if(val<options.min){
            _memSize.val(options.min);
            // lenPerc=_memSize.val()*lev1/250;
            lenPerc=_memSize.val()*lev1/options.grade1;
            _awCursor.css({
                left:lenPerc
            });
        }else{
            // if(val<=250){
            if(val<=options.grade1){
                // lenPerc=_memSize.val()*lev1/250;
                lenPerc=_memSize.val()*lev1/options.grade1;
                _awCursor.css({
                    left:lenPerc
                });
            // }else if(val<=500){
              }else if(val<=options.grade2){  
                // lenPerc=(_memSize.val()-250)*(lev2-lev1)/250;
                lenPerc=(_memSize.val()-options.grade1)*(lev2-lev1)/options.grade1;
                lenPerc+=lev1;
                _awCursor.css({
                    left:lenPerc
                });
            }else{
                // if(val<=1000){
                if(val<=options.grade3){
                    // lenPerc=(_memSize.val()-500)*(lev2-lev1)/500;
                    lenPerc=(_memSize.val()-options.grade2)*(lev2-lev1)/options.grade2;
                    lenPerc+=lev2;
                    _awCursor.css({
                        left:lenPerc
                    });
                }else{
                    _memSize.val(options.grade3);
                    lenPerc=lev3+1;
                    _awCursor.css({
                        left:lenPerc
                    });
                }
            }
        }
        _awCursor.css({
            left:lenPerc
        });
        _layer2.css({
            width:lenPerc
        });
    } 
    //end 2015-04-10 by gm new add drag及相关 
    //2015-04-20 tip提示
    function setSize(){
        var h=document.body.scrollHeight;
        var ih=window.innerHeight?window.innerHeight:document.body.clientHeight;
        var temph,tempw;
        if(h>ih){
            temph=h;
        }else{
            temph=ih;
        }
        tempw=$('body').width();
        $('.shade').css({
            width: tempw,
            height: temph
        });
    }
    function setCookie(name,value){ 
        var Days = 60; 
        var exp = new Date(); 
        exp.setTime(exp.getTime() + Days*24*60*60*1000); 
        document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString(); 
    }
    //删除cookies 
    function delCookie(name) { 
        var exp = new Date(); 
        exp.setTime(exp.getTime() - 1); 
        var cval=getCookie(name); 
        if(cval!=null){
            document.cookie= name + "="+cval+";expires="+exp.toGMTString();
        }          
    } 
    //end tip 提示
    var TopBtnInit = function(){
        $("body",parent.document).find(".top-bar-btn").mouseenter(function(){
            $(this).width(88);
            $(this).html("管理控制台");
        }).mouseleave(function () {
            $(this).width(20);
            $(this).html("<i class=\"fa fa-home text-20\"></i>");
        })
    }
    var sidebarMenuInit = function(isPC) {
		var startX = 0, startY = 0;
		function touchSatrtFunc(evt) {
			try {
				var touch = evt.touches[0]; // 获取第一个触点
				var x = Number(touch.pageX); // 页面触点X坐标
				var y = Number(touch.pageY); // 页面触点Y坐标
				startX = x;
				startY = y;
			} catch (e) {
			}
		}
		function touchMoveFunc(evt) {
			try {
				var touch = evt.touches[0]; // 获取第一个触点
				var x = Number(touch.pageX); // 页面触点X坐标
				var y = Number(touch.pageY); // 页面触点Y坐标
				if (x - startX > 100) {
					$(".nav-sidebar-div", parent.document)
							.addClass("nav-sidebar-display");
				} else if (x - startX < -100) {
					$(".nav-sidebar-div", parent.document)
							.removeClass("nav-sidebar-display");
				}
			} catch (e) {
			}
		}
		function touchEndFunc(evt) {
			try {
			} catch (e) {
			}
		}
		function bindEvent() {
			document.addEventListener('touchstart', touchSatrtFunc, false);
			document.addEventListener('touchmove', touchMoveFunc, false);
			document.addEventListener('touchend', touchEndFunc, false);
		}
		function initTouchDevice() {
			try {
				document.createEvent("TouchEvent");
				bindEvent(); // 绑定事件
			} catch (e) {
			}
		}
		if (!isPC) {
			if (top.location != location && !iframeMobileEndInited) { //layout页面滑出事件初始化
				initTouchDevice();
				iframeMobileEndInited = true;
			} else if (top.location == location && !mobileEndInited) {//iframe页面滑出事件初始化
				initTouchDevice();
				mobileEndInited = true;
			}
		}
	}
    var IsPC = function() {
        var userAgentInfo = navigator.userAgent;
        var Agents = new Array("Android", "iPhone", "SymbianOS",
                "Windows Phone", "iPad", "iPod");
        var flag = true;
        for (var v = 0; v < Agents.length; v++) {
            if (userAgentInfo.indexOf(Agents[v]) > 0) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    var sidebarR=function(min_height){
        $('.amz-sidebar-toggle').unbind('click').click(function(event) {
            var _target=$('.nav-sidebar-div');
            if(_target.hasClass('nav-sidebar-display')){
                _target.removeClass('nav-sidebar-display');
            }else{
                _target.addClass('nav-sidebar-display');
            }
        });

        //小图标拖放
        var flag=false;var _x,_y;var _width=document.body.clientWidth;var _height=window.screen.height;
        var _target=document.getElementsByClassName('amz-sidebar-toggle')[0];
        _target.addEventListener('touchstart',function(){flag=true;$('.amz-sidebar-toggle').css('color', 'red');},false);
        document.body.addEventListener('touchmove',function(event){event.preventDefault();if(flag){
            _x=event.touches[0].clientX;_y=event.touches[0].clientY;
            //var _rit=_width-_x; _rit<58?_rit=10:_rit;_rit>_width-58?_rit=_width-58:_rit;//不做随时追踪
            var _botm=_height-_y-48;
            if(_botm>_height-182){
            	_botm=_height-182;
            }else if(_botm<10){
	            _botm= 10;
            }
            if(_x>_width/2){
                $('.amz-sidebar-toggle').css({right:10,bottom: _botm});
            }else{
                $('.amz-sidebar-toggle').css({right:_width-58,bottom: _botm});
            } 
            event.stopPropagation();
        }},false);
        document.addEventListener('touchend',function(){flag=false;$('.amz-sidebar-toggle').css('color', '#555');},false);        
    }
    var gotop=function(min_height){
        $("#amz-go-top").click(function(){
            $('html,body').animate({scrollTop:0},500);
        });
        min_height ? min_height=min_height : min_height=256;
        if(top.location == location && !scrollInited){
	        $(window).scroll(function(){
	          var s=$(window).scrollTop()||$('html,body').scrollTop();
	          if(s>min_height){
	              $("#amz-go-top").fadeIn(100,function(){$('#amz-go-top').css('display', 'block');});
	          }else{
	              $("#amz-go-top").fadeOut(200,function(){$('#amz-go-top').css('display', 'none');});
	          }
	        });
	        scrollInited=true;
        }
    }
    /*common私有方法end*/
});
