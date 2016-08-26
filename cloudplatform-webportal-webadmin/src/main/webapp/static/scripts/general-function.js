/* 翻译时间戳为时间格式 */
function date(format, timestamp) {
	// var a, jsdate=((timestamp) ? new Date(timestamp) : new Date());
	var a;
	if (timestamp == null) {
		return "---";
	} else {
		var jsdate = new Date(timestamp);
	}

	var pad = function(n, c) {
		if ((n = n + "").length < c) {
			return new Array(++c - n.length).join("0") + n;
		} else {
			return n;
		}
	};
	var txt_weekdays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday"];
	var txt_ordin = {
		1 : "st",
		2 : "nd",
		3 : "rd",
		21 : "st",
		22 : "nd",
		23 : "rd",
		31 : "st"
	};
	var txt_months = ["", "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
			"December"];
	var f = {
		// Day
		d : function() {
			return pad(f.j(), 2)
		},
		D : function() {
			return f.l().substr(0, 3)
		},
		j : function() {
			return jsdate.getDate()
		},
		l : function() {
			return txt_weekdays[f.w()]
		},
		N : function() {
			return f.w() + 1
		},
		S : function() {
			return txt_ordin[f.j()] ? txt_ordin[f.j()] : 'th'
		},
		w : function() {
			return jsdate.getDay()
		},
		z : function() {
			return (jsdate - new Date(jsdate.getFullYear() + "/1/1")) / 864e5 >> 0
		},

		// Week
		W : function() {
			var a = f.z(), b = 364 + f.L() - a;
			var nd2, nd = (new Date(jsdate.getFullYear() + "/1/1").getDay() || 7)
					- 1;
			if (b <= 2 && ((jsdate.getDay() || 7) - 1) <= 2 - b) {
				return 1;
			} else {
				if (a <= 2 && nd >= 4 && a >= (6 - nd)) {
					nd2 = new Date(jsdate.getFullYear() - 1 + "/12/31");
					return date("W", Math.round(nd2.getTime() / 1000));
				} else {
					return (1 + (nd <= 3 ? ((a + nd) / 7) : (a - (7 - nd)) / 7) >> 0);
				}
			}
		},

		// Month
		F : function() {
			return txt_months[f.n()]
		},
		m : function() {
			return pad(f.n(), 2)
		},
		M : function() {
			return f.F().substr(0, 3)
		},
		n : function() {
			return jsdate.getMonth() + 1
		},
		t : function() {
			var n;
			if ((n = jsdate.getMonth() + 1) == 2) {
				return 28 + f.L();
			} else {
				if (n & 1 && n < 8 || !(n & 1) && n > 7) {
					return 31;
				} else {
					return 30;
				}
			}
		},

		// Year
		L : function() {
			var y = f.Y();
			return (!(y & 3) && (y % 1e2 || !(y % 4e2))) ? 1 : 0
		},
		// o not supported yet
		Y : function() {
			return jsdate.getFullYear()
		},
		y : function() {
			return (jsdate.getFullYear() + "").slice(2)
		},

		// Time
		a : function() {
			return jsdate.getHours() > 11 ? "pm" : "am"
		},
		A : function() {
			return f.a().toUpperCase()
		},
		B : function() {
			// peter paul koch:
			var off = (jsdate.getTimezoneOffset() + 60) * 60;
			var theSeconds = (jsdate.getHours() * 3600)
					+ (jsdate.getMinutes() * 60) + jsdate.getSeconds() + off;
			var beat = Math.floor(theSeconds / 86.4);
			if (beat > 1000)
				beat -= 1000;
			if (beat < 0)
				beat += 1000;
			if ((String(beat)).length == 1)
				beat = "00" + beat;
			if ((String(beat)).length == 2)
				beat = "0" + beat;
			return beat;
		},
		g : function() {
			return jsdate.getHours() % 12 || 12
		},
		G : function() {
			return jsdate.getHours()
		},
		h : function() {
			return pad(f.g(), 2)
		},
		H : function() {
			return pad(jsdate.getHours(), 2)
		},
		i : function() {
			return pad(jsdate.getMinutes(), 2)
		},
		s : function() {
			return pad(jsdate.getSeconds(), 2)
		},
		// u not supported yet

		// Timezone
		// e not supported yet
		// I not supported yet
		O : function() {
			var t = pad(Math.abs(jsdate.getTimezoneOffset() / 60 * 100), 4);
			if (jsdate.getTimezoneOffset() > 0)
				t = "-" + t;
			else
				t = "+" + t;
			return t;
		},
		P : function() {
			var O = f.O();
			return (O.substr(0, 3) + ":" + O.substr(3, 2))
		},
		// T not supported yet
		// Z not supported yet

		// Full Date/Time
		c : function() {
			return f.Y() + "-" + f.m() + "-" + f.d() + "T" + f.h() + ":"
					+ f.i() + ":" + f.s() + f.P()
		},
		// r not supported yet
		U : function() {
			return Math.round(jsdate.getTime() / 1000)
		}
	};

	return format.replace(/[\\]?([a-zA-Z])/g, function(t, s) {
				if (t != s) {
					// escaped
					ret = s;
				} else if (f[s]) {
					// a date function exists
					ret = f[s]();
				} else {
					// nothing special
					ret = s;
				}
				return ret;
			});
}
/* 翻译所有状态信息 */
var Status={
			hcluster:{		
				'0':'未启用',
				'1':'启用'
			}
};

function initChart(obj,title,ytitle,unit, option){
	
	var configObj = {
        chart: {
            type:'line',
            zoomType: 'x',
            spacingRight: 20
        },
        colors:['#ff66cc','#66ff66','#66ffff','#FFBB33','#C9C','#090','#330000','#CCCC00','#66cc99','#ccff66','#996666','#66cc33'],
        title: {
            text: title
        },
        legend :{
            borderColor: '#000000',
            backgroundColor: '#f9f9f9',
            symbolRadius: '2px',
            borderRadius: '5px',
            itemHoverStyle: {
                Color: '#000000'
            }
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval:150,
            labels:{
                rotation:0,
                align:'right'
            },
            dateTimeLabelFormats:{
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%e. %b',
                week: '%e. %b',
                month: '%b \'%y',
                year: '%Y'
            }
        },
        scrollbar:{
	        enabled:true
	    },
        plotOptions: {
        	lineWidth: 0.1,  
            fillOpacity: 0.1,
            line: {
                marker: {
                    enabled: false,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            },
            series:{
            	lineWidth:2,  
                fillOpacity: 0.5,
                states:{
                    hover:{
                        lineWidthPlus:0
                    }
            	}
        	}
        },
        credits:{
            enabled: false
        },
        yAxis: {
            title: {
                text: ytitle
            }
        },
        tooltip: {
            valueSuffix: unit,
            shared: true,
            pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>'
        }
	};
	
	$.extend(true, configObj, option);
	
    $(obj).highcharts(configObj);

} 


//formatter: function() { 
//console.log(this);
//var pointArray = this.points;
//var pointStr = "";
//pointArray.forEach(function(item){
//	var color = item.series.color;
//	var name = item.series.name;
//	var y = item.y;
//	pointStr += '<span style="color:'+color+'">\u25CF</span> '+name+': <b>'+TransUnit(y)+'</b><br/>'
//});
//
//return pointStr;

function translateStatus(status, resourceType) {
	if(resourceType){
		if(Status[resourceType] && Status[resourceType][status]){
			return Status[resourceType][status];
		}
	}
	if (status == 0) {
		return "未审核";
	} else if (status == 1) {
		return "运行中";
	} else if (status == 2) {
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>创建中...";
	} else if (status == 3) {
		return "创建失败";
	} else if (status == 4) {
		return "<font color=\"red\">审核失败</font>";
	} else if (status == 5) {
		return "<font color=\"orange\">异常</font>";
	} else if (status == 6) {
		return "正常";
	} else if (status == 7) {
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>启动中...";
	} else if (status == 8) {
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>停止中...";
	} else if (status == 9) {
		return "已停止";
	} else if (status == 10) {
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>删除中...";
	} else if (status == 11) {
		return "已删除";
	} else if (status == 12) {
		return "不存在";
	} else if (status == 13) {
		return "<font color=\"orange\">危险</font>";
	} else if (status == 14) {
		return "<font color=\"red\">严重危险</font>";
	} else if (status == 15) {
		return "禁用";
	} else if (status == 'FAILD') {
		return "备份失败";
	} else if (status == 'SUCCESS') {
		return "备份成功";
	} else if (status == 'BUILDING') {
		return "备份中...";
	} else if (status == 'ABNORMAL') {
		return "备份异常";
	} else if (status == 'INCR') {
		return "增量";
	}else if (status == 'FULL') {
		return "全量";
	}else if (status == 'NONE') {
		return "异常";
	}else if (status == null) {
		return "未知";
	}
}

//ES状态翻译
function esStateTransform(status){
    if(status=='DEFAULT'){
        return '未审核';
    }else if(status=='RUNNING'){
        return '运行中';
    }else if(status=='BUILDDING'){
        return '创建中';
    }else if(status=='BUILDFAIL'){
        return '创建失败';
    }else if(status=='AUDITFAIL'){
        return '审核失败';
    }else if(status=='ABNORMAL'){
        return '异常';
    }else if(status=='NORMAL'){
        return '正常';
    }else{
        return status;
    }
}

// 状态翻译重写
function stateTransform(status,type){
	var defaultStatus={
		"0":"未审核",
		"1":"运行中",
		"2":"<i class='ace-icon fa fa-spinner fa-spin green bigger-125'></i>创建中...",
		"3":"<i class='ace-icon fa fa-warning red bigger-120'></i><font color='red'>创建失败</font>",
		"4":"<font color='red'>审核失败</font>",
		"5":"<font color='orange'>异常</font>",
		"6":"正常",
		"7":"<i class='ace-icon fa fa-spinner fa-spin green bigger-125'></i>启动中...",
		"8":"<i class='ace-icon fa fa-spinner fa-spin green bigger-125'></i>停止中...",
		"9":"已停止",
		"10":"<i class='ace-icon fa fa-spinner fa-spin green bigger-125'></i>删除中...",
		"11":"已删除",
		"12":"不存在",
		"13":"<font color='orange'>危险</font>",
		"14":"<font color='red'>严重危险</font>",
		"15":"禁用",
		"FAILD":"备份失败",
		"SUCCESS":"备份成功",
		"BUILDING":"备份中...",
		"ABNORMAL":"备份异常",
		"null":"未知"
	};
	var rdsStatus={
		"15":"<i class='ace-icon fa fa-spinner fa-spin orange bigger-125'></i>扩容中...",
		"16":"<i class='ace-icon fa fa-warning red bigger-120'></i><font color='red'>扩容失败</font>",
		"17":"<i class='ace-icon fa fa-spinner fa-spin orange bigger-125'></i>缩容中..."
	};
	var statusObj={
		"default":defaultStatus,
		'rdsMcluster':rdsStatus
	};
	
	var temp=statusObj[type][status];
	return temp?temp:statusObj["default"][status];
}
function containerClusterOs(status,type,os){
	var rds={
		"1":{"start":0,"restart":1,"stop":1,"delete":0,"create":1,"expand":1,"compress":1,"backup":1},
		"2":{"start":0,"restart":0,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"3":{"start":0,"restart":0,"stop":0,"delete":1,"create":1,"expand":0,"compress":0,"backup":0},
		"5":{"start":0,"restart":1,"stop":1,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"7":{"start":0,"restart":0,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"8":{"start":0,"restart":0,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"10":{"start":0,"restart":0,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"9":{"start":1,"restart":1,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"13":{"start":0,"restart":1,"stop":1,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"14":{"start":0,"restart":1,"stop":1,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"15":{"start":0,"restart":0,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"16":{"start":1,"restart":1,"stop":1,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"17":{"start":0,"restart":0,"stop":0,"delete":0,"create":1,"expand":0,"compress":0,"backup":0},
		"18":{"start":0,"restart":0,"stop":0,"delete":0,"create":0,"expand":0,"compress":0,"backup":0},
	};
	var rdsContainer={
		"1":{"start":0,"stop":0,"delete":0,"compress":1},
		"5":{"start":0,"stop":1,"delete":0,"compress":0},
		"7":{"start":0,"stop":0,"delete":0,"compress":0},
		"8":{"start":0,"stop":0,"delete":0,"compress":0},
		"10":{"start":0,"stop":0,"delete":0,"compress":0},
		"9":{"start":1,"stop":0,"delete":0,"compress":0},
		"17":{"start":0,"stop":0,"delete":0,"compress":0},
		"15":{"start":0,"stop":0,"delete":0,"compress":0}
	}
	var osObjs={
		"rds":rds,
		"rdsContainer":rdsContainer
	};
	var temp=osObjs[type] && osObjs[type][status] && osObjs[type][status][os];
	return typeof temp !== 'number'? 1:temp;
}
function containerOsHtml(type,os){
	var rdsHtml={
		"start":"<a class='green' href='#' onclick='startMcluster(this)' onfocus='this.blur();' title='启动' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-play-circle-o bigger-130'></i></a>",
		"stop":"<a class='blue' href='#' onclick='stopMcluster(this)' onfocus='this.blur();' title='停止' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-power-off bigger-120'></i></a>",
		"expand":"<a class='orange' href='#' onclick='expandMcluster(this);' onfocus='this.blur();'  title='扩容' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-expand bigger-120'></i></a>",
		"delete":"<a class='red' href='#' onclick='deleteMcluster(this);' onfocus='this.blur();'  title='删除' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-trash-o bigger-120'></i></a>",
		"all_backUp":"<a class='red' href='#' onclick='' onfocus='this.blur();'  title='全量备份' data-toggle='tooltip' data-placement='right'>"
								+"<i class='ace-icon fa fa-trash-o bigger-120'></i></a>",
		"add_backUp":"<a class='red' href='#' onclick='' onfocus='this.blur();'  title='增量备份' data-toggle='tooltip' data-placement='right'>"
									+"<i class='ace-icon fa fa-trash-o bigger-120'></i></a>"
	};
	var rdsContainerHtml={
		"start":"<a class='green' href='#' onclick='startContainer(this)' onfocus='this.blur();' title='启动' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-play-circle-o bigger-130'></i></a>",
		"stop":"<a class='blue' href='#' onclick='stopContainer(this)' onfocus='this.blur();' title='停止' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-power-off bigger-120'></i></a>",
		"compress":"<a class='red' href='#' onclick='compressContainer(this)' title='缩容' data-toggle='tooltip' data-placement='right'>"
							+"<i class='ace-icon fa fa-compress bigger-120'></i></a>"
	}
	var htmlObjs={
		"rds":rdsHtml,
		"rdsContainer":rdsContainerHtml
	}
	var temp=htmlObjs[type][os];
	return temp?temp:'-';
}
function rdsExecutingStatusHandler(executingStatus,entityList,queryUrl,callback){
	entityList.filter(function(entity){
		return executingStatus.indexOf(entity.status)>-1;
	}).forEach(function(entity){
		var intervalId =setInterval(function(){
			$.ajax({
				cache:false,
				type : "get",
				url : queryUrl+entity.id,
				dataType : "json", /*这句可用可不用，没有影响*/
				success : function(data) {
					callback(data,intervalId);
				}}
			);
		},8000);
	});
}
/* 错误提示框,一般在异步请求返回信息中用到 */
function error(errorThrown, time) {
	if (errorThrown.result == 0) {
		if (!time) {
			time = 1000;
		}
		$.gritter.add({
					title : '错误',
					text : errorThrown.msgs,
					sticky : false,
					time : time,
					class_name : 'gritter-error'
				});
		return true;
	}
	return false;
}
/* 警告提示框,一般在操作提示中用到 */
function warn(warnMsg, time) {
	if (!time) {
		time = 1000;
	}
	$.gritter.add({
				title : '警告',
				text : warnMsg,
				sticky : false,
				time : time,
				class_name : 'gritter-error'
			});
	return;
}
/* 成功提示框,一般在操作提示中用到 */
function success(warnMsg, time) {
	if (!time) {
		time = 1000;
	}
	$.gritter.add({
				title : '成功',
				text : warnMsg,
				sticky : false,
				time : time,
				class_name : 'gritter-success'
			});
	return;
}
/*
 * 确认框脚本
 * 
 */
function confirmframe(title, content, question, ok, cancle) {
	$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
						_title : function(title) {
							var $title = this.options.title || '&nbsp;'
							if (("title_html" in this.options)
									&& this.options.title_html == true)
								title.html($title);
							else
								title.text($title);
						}
					}));

	$('#dialog-confirm').removeClass('hide').dialog({
				resizable : false,
				modal : true,
				title : "<div class='widget-header'><h4 class='smaller'>"
						+ title,
				title_html : true,
				buttons : [{
							html : "确定",
							"class" : "btn btn-primary btn-xs",
							click : function() {
								ok();
								$(this).dialog("close");
							}
						}, {
							html : "取消",
							"class" : "btn btn-xs",
							click : function() {
								$(this).dialog("close");
							}
						}]
			});
	$('#dialog-confirm-content').html(content);
	$('#dialog-confirm-question').html(question);
}
/* 高级搜索toggle效果 */
function bt_toggle(containerId) {
	$('#' + containerId).click(function(event) {
				var temp = $(this).attr("id");
				$('#' + temp + "-div").slideToggle();
			});
}
function getLoading() {
	$('body').append("<div class=\"spin\"></div>");
	$('body').append("<div class=\"far-spin\"></div>");
}
function removeLoading() {
	$('body').find('.spin').remove();
	$('body').find('.far-spin').remove();
}
function FilterNull(data) {
	if (data == null || data == undefined) {
		return '';
	} else {
		return data;
	}
}

function queryUrlBuilder(url, data) {
	var queryUrl = "?key=";
	for (var key in data) {
		queryUrl = queryUrl + "&&" + key + "=" + data[key]
	}
	return url + queryUrl;
}
// chosen 组件配置
function initChosen(id, width) {
	var _id = ".chosen-select";
	if (id)
		_id = "#" + id;
	$(_id).trigger('chosen:updated');
	var defOpt = {
		allow_single_deselect : true,
		search_contains : true,
		no_results_text : "未找到匹配数据",
		disable_search : false
	}
	$(_id).chosen(defOpt);

	// resize the chosen on window resize
	var _width = width;
	$(window).off('resize.chosen').on('resize.chosen', function() {
				$(_id).each(function() {
							var $this = $(this);
							if (!_width)
								_width = $this.parent().width();
							$this.next().css({
										'width' : _width
									});
						})
			}).trigger('resize.chosen');
}

function initMultiple() {
	$('.chosen-select').trigger('chosen:updated');
	$('.chosen-select').chosen({
				allow_single_deselect : true
			});
	$(window).off('resize.chosen').on('resize.chosen', function() {
				$('.chosen-select').each(function() {
							var $this = $(this);
							$this.next().css({
										'width' : $this.parent().width()
									});
						})
			}).trigger('resize.chosen');
}
/* 预警管理页面查看详情通用函数 begin */
function strToJson(str) {
	var jsonArr = {};
	var strArr = str.split(",");
	var len = strArr.length;
	if (len <= 1) {
		return str;
	} else {
		for (var i = 0; i < len; i++) {
			strArrC = strArr[i].split("=");
			if (strArrC.length == 2) {
				jsonArr[strArrC[0]] = strArrC[1];
			}
		}
		return jsonArr;
	}
}

function msgFormat(data, obj, eleId) {
	if (typeof(data) == "string") {
		var span = $("<span>" + data + "</span>" + "<br/>");
		obj.append(span);
	} else {
		for (var key in data) {
			var span = $("<span>" + key + "  :  " + data[key] + "</span>"
					+ "<br/>");
			if ((key.indexOf("failed") >= 0 || key.indexOf("lost") >= 0)
					&& data[key] > 0) {
				$("#" + eleId).val(data[key]);
				span.eq(0).addClass("monitor-text-danger");
			}
			obj.append(span);
		}
	}
}

function alarmFormat(str, obj, eleId) {
	var span = $("<span>" + str + "</span>" + "<br/>");
	var failNum = $("#" + eleId).val()
	if (failNum == "0") {
		span.eq(0).addClass("monitor-text-success");
	} else if (failNum == "1") {
		span.eq(0).addClass("monitor-text-danger");
	} else if (failNum == "2") {
		span.eq(0).addClass("monitor-text-serious");
	}

	obj.append(span);
}

function errorRecordFormat(json, obj) {
	if (typeof(json) == "string") {
		var span = $("<span>" + json + "</span>" + "<br/>");
		obj.append(span)
	} else if (!$.isEmptyObject(json)) {
		for (var key in json) {
			if (typeof(json[key] == "object")) {
				var span = $("<span>" + key + "  :   "
						+ JSON.stringify(json[key]) + "</span>" + "<br/>")
			} else {
				var span = $("<span>" + key + "  :   " + json[key] + "</span>"
						+ "<br/>")
			}
			obj.append(span);
		}
	} else {
		var span = $("<span>" + JSON.stringify(json) + "</span>" + "<br/>");
		obj.append(span);
	}
}

function dataAppend(data, tableFlag, eleId) {
	for (var key in data) {
		var tr = $("<tr></tr>");
		var tdStr = $("<td>" + key + "</td>");
		var tdInfo = $("<td></td>");
		if (key == "message") {
			var msgJson = strToJson(data[key]);
			if (!$.isEmptyObject(msgJson)) {
				msgFormat(msgJson, tdInfo, eleId)
			}
		} else if (key == "alarm") {
			alarmFormat(data[key], tdInfo, eleId);
		} else if (key == "error_record") {
			errorRecordFormat(data[key], tdInfo)
		} else {
			var tdInfo = $("<td>" + JSON.stringify(data[key]) + "</td>");
		}

		tr.append(tdStr).append(tdInfo);
		tableFlag.after(tr);
	}
}
function getCount(obj) {
	var t = typeof(obj);
	if (t == "string") {
		return obj.length;
	} else if (t == "object") {
		var n = 0;
		for (var i in obj) {
			n++;
		}
		return n;
	} else {
		return false;
	}
}
function getItem(data) {
	var tdArr = [];
	for (var key in data) {
		tdArr.push(key);
	}
	return tdArr
}
/* 预警管理页面查看详情通用函数 end */

/* 搜索功能begin */
function addSltOpt(array, obj) {
	var len = array.length;
	for (var i = 0; i < len; i++) {
		var opt = $("<option></option>");
		if (array[i] == 0) {
			opt.html("未审核");
		} else if (array[i] == 1) {
			opt.html("运行中");
		} else if (array[i] == 2) {
			opt.html("创建中");
		} else if (array[i] == 3) {
			opt.html("创建失败");
		} else if (array[i] == 4) {
			opt.html("审核失败");
		} else if (array[i] == 5) {
			opt.html("异常");
		} else if (array[i] == 6) {
			opt.html("正常");
		} else if (array[i] == 7) {
			opt.html("启动中");
		} else if (array[i] == 8) {
			opt.html("停止中");
		} else if (array[i] == 9) {
			opt.html("已停止");
		} else if (array[i] == 10) {
			opt.html("删除中");
		} else if (array[i] == 11) {
			opt.html("已删除");
		} else if (array[i] == 12) {
			opt.html("不存在");
		} else if (array[i] == 13) {
			opt.html("危险");
		} else if (array[i] == 14) {
			opt.html("严重危险");
		} else if (array[i] == 15) {
			opt.html("禁用");
		}
		opt.attr({
					"value" : array[i]
				});
		obj.append(opt);
	}
}

function clearSearch(objArr) {
	for (var i = 0; i < objArr.length; i++) {
		$("#" + objArr[i]).val("");
		// 针对 chosen 新添 清除
		var _target = $("#" + objArr[i]);
		if (_target.siblings('div').length > 0) {
			if (_target.siblings('div').children('a').find('abbr').length > 0) {
				_target.siblings('div').children('a')
						.addClass('chosen-default');
				_target.siblings('div').children('a').find('span').text(_target
						.attr('data-placeholder'));
				_target.siblings('div').children('a').find('abbr').remove();
			}
		}
	}
}

/* enter绑定函数 */
function enterKeydown(obj, func) {
	obj.each(function() {
				$(this).bind('keypress', function(event) {
							if (event.keyCode == "13") {
								func();
							}
						});
			})
}

function dynamicLoadJs(src, callback, args) {
	var _doc = document.getElementsByTagName('head')[0];
	var script = document.createElement('script');
	script.setAttribute('type', 'text/javascript');
	script.setAttribute('src', src);
	_doc.appendChild(script);
	script.onload = script.onreadystatechange = function() {
		if (!this.readyState || this.readyState == 'loaded'
				|| this.readyState == 'complete') {
			if (args == null) {
				callback();
			} else {
				callback(args);
			}
		}
		script.onload = script.onreadystatechange = null;
	}
}

function IsPC() {
	var userAgentInfo = navigator.userAgent;
	var Agents = new Array("Android", "iPhone", "SymbianOS", "Windows Phone",
			"iPad", "iPod");
	var flag = true;
	for (var v = 0; v < Agents.length; v++) {
		if (userAgentInfo.indexOf(Agents[v]) > 0) {
			flag = false;
			break;
		}
	}
	return flag;
}
// new add by gm 2015-07-01---------------------------------
function TranslateDbUserType(type) {
	if (type == 1) {
		return "管理";
	} else if (type == 2) {
		return "只读"
	} else {
		return "读写"
	}
}
function transStateHtml(btnIcons, state) {
	if (!state) {
		state = 6;
	}
	var start = restart = stop = del = extend = modify = isDefault = '';
	if (btnIcons.indexOf('start') >= 0 && state == 9) {
		start = "<a class=\"green\" href=\"#\" data-click-type='start' onfocus=\"this.blur();\" title=\"启动\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-play-circle-o bigger-130\"></i>"
				+ "</a>"
	}
	if (btnIcons.indexOf('restart') >= 0 && state == 1 || state == 5
			|| state == 9 || state == 13 || state == 14) {
		restart = "<a class=\"green\" href=\"#\" data-click-type='restart' onfocus=\"this.blur();\" title=\"重启\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-retweet bigger-130\"></i>"
				+ "</a>"
	}
	if (btnIcons.indexOf('stop') >= 0 && state == 1 || state == 5
			|| state == 13 || state == 14) {
		stop = "<a class=\"blue\" href=\"#\" data-click-type='stop' onfocus=\"this.blur();\" title=\"停止\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-power-off bigger-120\"></i>"
				+ "</a>"
	}
	if (btnIcons.indexOf('del') >= 0 && state != 10) {
		del = "<a class=\"red\" href=\"#\" data-click-type='delete' onfocus=\"this.blur();\"  title=\"删除\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>"
				+ "</a>"
	}
	if (btnIcons.indexOf('modify') >= 0) {
		modify = "<a class=\"green\" href=\"#\" data-click-type='modify' onfocus=\"this.blur();\"  title=\"修改\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-pencil bigger-120\"></i>" + "</a>"
	}
	if (btnIcons.indexOf('extend') >= 0 && state == 1 || state == 9) {
		extend = "<a class=\"red\" href=\"#\" data-click-type='extend' onfocus=\"this.blur();\"  title=\"扩容\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-plus bigger-120\"></i>" + "</a>"
	}
	if (btnIcons.indexOf('default') >= 0) {
		isDefault = "<a class=\"blue\" href=\"#\" data-click-type='default' onfocus=\"this.blur();\"  title=\"默认使用\" data-toggle=\"tooltip\" data-placement=\"right\">"
				+ "<i class=\"ace-icon fa fa-plus bigger-120\"></i>" + "</a>"
	}
	var td = "<td>"
			+ "<div class=\"hidden-sm hidden-xs  action-buttons\">"
			+ start
			+ stop
			+ restart
			+ modify
			+ del
			+ extend
			+ isDefault
			+ "</div>"
			+ '<div class="hidden-md hidden-lg">'
			+ '<div class="inline pos-rel">'
			+ '<button class="btn btn-minier btn-yellow dropdown-toggle" data-toggle="dropdown" data-position="auto">'
			+ '<i class="ace-icon fa fa-caret-down icon-only bigger-120"></i>'
			+ '</button>'
			+ '<ul class="dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">'
			+ '<li>' + start + stop + restart + modify + del + extend
			+ isDefault + '</li></ul></div></div>' + "</td>";
	return td;
}
function DialogBoxInit(title, text, handler, name, id) {
	$("#dialog-box").find("#dialog-box-title").html(title);
	$("#dialog-box").find("#dialog-box-text")
			.html("<h5 style='text-align: center; '>" + text + "</h5>");
	$("#dialogBoxSubmit").unbind("click").click(function() {
				handler(name, id);
			})
	$('#dialog-box').modal({
				backdrop : false,
				show : true
			});
	$("#submitCreateUserForm").removeAttr("disabled");
	$("#submitModifyUserForm").removeAttr("disabled");
}
function queryUser() {
	var options = $('#containeruser');
	getLoading();
	$.ajax({
				cache : false,
				url : '/user',
				type : 'get',
				dataType : 'json',
				success : function(data) {
					removeLoading();
					var array = data.data;
					for (var i = 0, len = array.length; i < len; i++) {

						var option = $("<option value=\"" + array[i].id + "\">"
								+ array[i].userName + "</option>");
						options.append(option)
					}
					initChosen();
				}
			});
}
function TransUnit(num) {
	var temp = Math.abs(num);
	if (temp < 1024) {// B
		temp = temp + 'B';
	} else if (temp < 1024 * 1024) {// KB
		temp = Math.round(temp / 1024)
				+ 'KB'
	} else if (temp < 1024 * 1024 * 1024) {// MB
		temp = Math.round(temp / 1024 / 1024 ) + 'MB'
	} else {// GB
		temp = Math.round(temp / 1024 / 1024 / 1024) + 'GB';
	}
	if (num < 0) {
		temp = '-' + temp
	}
	return temp;
}
function TransTimeUnit(num) {
	var temp = Math.abs(num);
	if (temp < 60) {// 秒
		temp = temp + '秒';
	} else if (temp < 3600) {// 分
		temp = Math.round(temp / 60) + '分钟'
	} else if(temp <3600*24){// 时
		temp = Math.round(temp /3600) + '小时';
	}else{//天
		temp = Math.round(temp /86400) + '天';
	}
	if (num < 0) {
		temp = '-' + temp
	}
	return temp;
}
function dataErrorFilter(data){
	if(data != '-1' && data != null){
		return data;
	}else{
		return '<i class="fa fa-times"></i>'
	}
}